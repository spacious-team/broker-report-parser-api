/*
 * Broker Report Parser API
 * Copyright (C) 2020  Spacious Team <spacious-team@ya.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.spacious_team.broker.report_parser.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class WrappingReportTable<RowType> implements ReportTable<RowType> {
    private final ReportTable<RowType> reportTable;

    @SafeVarargs
    public static <T> WrappingReportTable<T> of(BrokerReport report, Collection<? extends T>... dataset) {
        return new WrappingReportTable<>(new EagerWrappingReportTable<>(report, dataset));
    }

    @SafeVarargs
    public static <T> WrappingReportTable<T> of(ReportTable<? extends T>... tables) {
        assertIsTrue(tables.length > 0, "Can't wrap, report tables not provided");
        BrokerReport report = tables[0].getReport();
        boolean isAllReportsIsSame = Arrays.stream(tables)
                .allMatch(t -> t.getReport() == report);
        assertIsTrue(isAllReportsIsSame, "Wrapping report tables should be built for same broker report");
        return new WrappingReportTable<>(new LazyWrappingReportTable<>(report, tables));
    }

    private static void assertIsTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public BrokerReport getReport() {
        return reportTable.getReport();
    }

    @Override
    public List<RowType> getData() {
        return reportTable.getData();
    }


    @Getter
    private static class EagerWrappingReportTable<RowType> implements ReportTable<RowType> {
        private final BrokerReport report;
        private final List<RowType> data;

        @SafeVarargs
        public EagerWrappingReportTable(BrokerReport report, Collection<? extends RowType>... dataset) {
            List<RowType> rows = new ArrayList<>();
            for (Collection<? extends RowType> d : dataset) {
                rows.addAll(d);
            }
            this.report = report;
            this.data = Collections.unmodifiableList(rows);
        }
    }


    private static class LazyWrappingReportTable<RowType> implements ReportTable<RowType> {
        @Getter
        private final BrokerReport report;
        private volatile ReportTable<? extends RowType> @Nullable [] tables;  // possibly null array of non-null elements
        private volatile @MonotonicNonNull List<RowType> data;

        @SafeVarargs
        private LazyWrappingReportTable(BrokerReport report, ReportTable<? extends RowType>... tables) {
            this.report = report;
            this.tables = tables;
        }

        @Override
        public List<RowType> getData() {
            if (data == null) {
                synchronized (this) {
                    if (data == null) {
                        @SuppressWarnings("nullness")
                        ReportTable<? extends RowType>[] existingTables = requireNonNull(tables);
                        data = Stream.of(existingTables)
                                .map(ReportTable::getData)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toUnmodifiableList());
                        tables = null; // erase for GC
                    }
                }
            }
            //noinspection ConstantConditions
            return data;
        }
    }
}
