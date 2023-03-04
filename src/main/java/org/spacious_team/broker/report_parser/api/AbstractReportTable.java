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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spacious_team.table_wrapper.api.ReportPage;
import org.spacious_team.table_wrapper.api.Table;
import org.spacious_team.table_wrapper.api.TableHeaderColumn;
import org.spacious_team.table_wrapper.api.TableRow;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;

/**
 * To implement override one of {@link #parseTable()}, {@link #parseRow(TableRow)} or
 * {@link #parseRowToCollection(TableRow)} methods.
 */
@SuppressWarnings("unused")
public abstract class AbstractReportTable<R, T extends Enum<T> & TableHeaderColumn>
        extends InitializableReportTable<R> {

    private @Nullable String tableName;
    private final Predicate<Object> tableNameFinder;
    private final @Nullable Predicate<Object> tableFooterFinder;
    private final Class<T> headerDescription;
    private final int headersRowCount;
    private final CreateMode createMode;

    protected AbstractReportTable(BrokerReport report,
                                  String tableName,
                                  @Nullable String tableFooter,
                                  Class<T> headerDescription) {
        this(report, tableName, tableFooter, headerDescription, 1);
        this.tableName = tableName;
    }

    protected AbstractReportTable(BrokerReport report,
                                  String tableName,
                                  @Nullable String tableFooter,
                                  Class<T> headerDescription,
                                  int headersRowCount) {
        this(report, getPrefixPredicate(tableName), getPrefixPredicateOrNull(tableFooter),
                headerDescription, headersRowCount);
        this.tableName = tableName;
    }

    protected AbstractReportTable(BrokerReport report,
                                  Predicate<String> tableNameFinder,
                                  @Nullable Predicate<String> tableFooterFinder,
                                  Class<T> headerDescription) {
        this(report, tableNameFinder, tableFooterFinder, headerDescription, 1);
    }

    protected AbstractReportTable(BrokerReport report,
                                  Predicate<String> tableNameFinder,
                                  @Nullable Predicate<String> tableFooterFinder,
                                  Class<T> headerDescription,
                                  int headersRowCount) {
        super(report);
        this.createMode = CreateMode.TABLE_BY_PREDICATE;
        this.tableName = null;
        this.tableNameFinder = cast(tableNameFinder);
        this.tableFooterFinder = castOrNull(tableFooterFinder);
        this.headerDescription = headerDescription;
        this.headersRowCount = headersRowCount;
    }

    protected AbstractReportTable(BrokerReport report,
                                  String providedTableName,
                                  String namelessTableFirstLine,
                                  @Nullable String tableFooter,
                                  Class<T> headerDescription) {
        this(report, providedTableName, namelessTableFirstLine, tableFooter, headerDescription, 1);
    }

    protected AbstractReportTable(BrokerReport report,
                                  String providedTableName,
                                  String namelessTableFirstLine,
                                  @Nullable String tableFooter,
                                  Class<T> headerDescription,
                                  int headersRowCount) {
        this(report, providedTableName, getPrefixPredicate(namelessTableFirstLine),
                getPrefixPredicateOrNull(tableFooter), headerDescription, headersRowCount);
    }

    protected AbstractReportTable(BrokerReport report,
                                  String providedTableName,
                                  Predicate<String> namelessTableFirstLineFinder,
                                  @Nullable Predicate<String> tableFooterFinder,
                                  Class<T> headerDescription) {
        this(report, providedTableName, namelessTableFirstLineFinder, tableFooterFinder, headerDescription, 1);
    }

    protected AbstractReportTable(BrokerReport report,
                                  String providedTableName,
                                  Predicate<String> namelessTableFirstLineFinder,
                                  @Nullable Predicate<String> tableFooterFinder,
                                  Class<T> headerDescription,
                                  int headersRowCount) {
        super(report);
        this.createMode = CreateMode.NAMELESS_TABLE_BY_PREDICATE;
        this.tableName = providedTableName;
        this.tableNameFinder = cast(namelessTableFirstLineFinder);
        this.tableFooterFinder = castOrNull(tableFooterFinder);
        this.headerDescription = headerDescription;
        this.headersRowCount = headersRowCount;
    }

    private static @Nullable Predicate<String> getPrefixPredicateOrNull(@Nullable String prefix) {
        return (prefix == null || prefix.isEmpty()) ? null : getPrefixPredicate(prefix);
    }

    private static Predicate<String> getPrefixPredicate(String prefix) {
        String lowercasePrefix = prefix.trim().toLowerCase();
        @SuppressWarnings("ConstantConditions")
        Predicate<String> stringPredicate = (cell) ->
                (cell != null) && cell.trim().toLowerCase().startsWith(lowercasePrefix);
        return requireNonNull(stringPredicate);
    }

    private static @Nullable Predicate<Object> castOrNull(@Nullable Predicate<String> predicate) {
        return (predicate == null) ? null : cast(predicate);
    }

    private static Predicate<Object> cast(Predicate<String> predicate) {
        return (cell) -> (cell instanceof String) && predicate.test(cell.toString());
    }

    @Override
    protected Collection<R> parseTable() {
        try {
            ReportPage reportPage = getReport().getReportPage();
            Table table = createTable(reportPage);
            return parseTable(table);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге таблицы" + (tableName == null ? "" : " '" + tableName + "'")
                    + " в отчете " + getReport(), e);
        }
    }

    private Table createTable(ReportPage reportPage) {
        switch (createMode) {
            case TABLE_BY_PREDICATE:
                return (tableFooterFinder != null) ?
                        reportPage.create(tableNameFinder, tableFooterFinder, headerDescription, headersRowCount).excludeTotalRow() :
                        reportPage.create(tableNameFinder, headerDescription, headersRowCount);
            case NAMELESS_TABLE_BY_PREDICATE:
                @SuppressWarnings({"nullness", "ConstantConditions"})
                String tableName = requireNonNull(this.tableName);
                return (tableFooterFinder != null) ?
                        reportPage.createNameless(tableName, tableNameFinder, tableFooterFinder, headerDescription, headersRowCount).excludeTotalRow() :
                        reportPage.createNameless(tableName, tableNameFinder, headerDescription, headersRowCount);
        }
        throw new IllegalArgumentException("Unexpected create mode = " + createMode);
    }

    @DefaultQualifier(NonNull.class)  // checkerframework bug fix
    protected Collection<R> parseTable(Table table) {
        return table.getDataCollection(getReport(), this::parseRowToCollection, this::checkEquality, this::mergeDuplicates);
    }

    protected Collection<R> parseRowToCollection(TableRow row) {
        @Nullable R data = parseRow(row);
        return (data == null) ? emptyList() : singleton(data);
    }

    protected @Nullable R parseRow(TableRow row) {
        return null;
    }

    protected boolean checkEquality(R object1, R object2) {
        return Objects.equals(object1, object2);
    }

    protected Collection<R> mergeDuplicates(R oldObject, R newObject) {
        return Arrays.asList(oldObject, newObject);
    }

    private enum CreateMode {
        TABLE_BY_PREDICATE,
        NAMELESS_TABLE_BY_PREDICATE
    }
}