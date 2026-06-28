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

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.spacious_team.table_wrapper.api.ReportPage;
import org.spacious_team.table_wrapper.api.Table;
import org.spacious_team.table_wrapper.api.TableHeaderColumn;
import org.spacious_team.table_wrapper.api.TableRow;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;
import static org.spacious_team.table_wrapper.api.StringPrefixPredicate.ignoreCaseStringPrefixPredicate;

/**
 * To implement override one of {@link #parseTable()}, {@link #parseRow(TableRow)} or
 * {@link #parseRowToCollection(TableRow)} methods.
 */
@SuppressWarnings("unused")
public abstract class AbstractReportTable<R> extends InitializableReportTable<R> {

    private @MonotonicNonNull String tableName;
    private final Predicate<@Nullable Object> tableNameFinder;
    private final int tableNameRowCount;  // 0 for CreateMode.NAMELESS_TABLE_BY_PREDICATE
    private final int headerRowsCount;    // -1 if header row count is determined by firstDataRowFinder
    private final @Nullable Predicate<@Nullable Object> firstDataRowFinder;
    private final @Nullable Predicate<@Nullable Object> tableFooterFinder;
    private final Class<?> headerDescription;  // <? extends Enum<T> & TableHeaderColumn>
    private final CreateMode createMode;

    /**
     * Finds and creates a table in a report, whose title case-insensitive matches to {@code tableName>} prefix.
     * The last row of the table precedes the row, which case-insensitive matches to {@code tableFooter} prefix.
     * If {@code tableFooter} is null, the last row of the table precedes the empty row.
     * If {@code tableFooter} is null and empty row not found, when table ends with report last row.
     */
    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        String tableName,
                        @Nullable String tableFooter,
                        Class<T> headerDescription) {
        this(report, tableName, 1, tableFooter, headerDescription, 1);
    }

    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        String tableName,
                        @Nullable String tableFooter,
                        Class<T> headerDescription,
                        int headerRowsCount) {
        this(report, tableName, 1, tableFooter, headerDescription, headerRowsCount);
    }

    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        String tableName,
                        int tableNameRowCount,
                        @Nullable String tableFooter,
                        Class<T> headerDescription,
                        int headerRowsCount) {
        this(report, getPrefixPredicate(tableName), tableNameRowCount, getPrefixPredicateOrNull(tableFooter),
                headerDescription, headerRowsCount);
        this.tableName = tableName;
    }

    /**
     * Finds and creates a table in a report, whose title matches to {@code tableName} prefix.
     * The first data row of the table matches to {@code firstDataRow} prefix.
     * The last row of the table precedes the row, which matches to {@code tableFooter} prefix.
     * If {@code tableFooter} is null, the last row of the table precedes the empty row.
     * If {@code tableFooter} is null and empty row not found, when table ends with report last row.
     */
    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        String tableName,
                        int tableNameRowCount,
                        String firstDataRow,
                        @Nullable String tableFooter,
                        Class<T> headerDescription) {
        this(report, getPrefixPredicate(tableName), tableNameRowCount, getPrefixPredicate(firstDataRow),
                getPrefixPredicateOrNull(tableFooter), headerDescription);
        this.tableName = tableName;
    }

    /**
     * Finds and creates a table in a report, whose title matches to {@code tableNameFinder} predicate.
     * The last row of the table precedes the row, which matches to {@code tableFooterFinder}.
     * If {@code tableFooterFinder} is null, the last row of the table precedes the empty row.
     * If {@code tableFooterFinder} is null and empty row not found, when table ends with report last row.
     */
    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        Predicate<String> tableNameFinder,
                        @Nullable Predicate<String> tableFooterFinder,
                        Class<T> headerDescription) {
        this(report, tableNameFinder, 1, tableFooterFinder, headerDescription, 1);
    }

    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        Predicate<String> tableNameFinder,
                        @Nullable Predicate<String> tableFooterFinder,
                        Class<T> headerDescription,
                        int headerRowsCount) {
        this(report, tableNameFinder, 1, tableFooterFinder, headerDescription, headerRowsCount);
    }

    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        Predicate<String> tableNameFinder,
                        int tableNameRowCount,
                        @Nullable Predicate<String> tableFooterFinder,
                        Class<T> headerDescription,
                        int headerRowsCount) {
        super(report);
        this.createMode = CreateMode.TABLE_BY_PREDICATE;
        //noinspection DataFlowIssue
        this.tableName = null;
        this.tableNameFinder = cast(tableNameFinder);
        this.tableNameRowCount = tableNameRowCount;
        this.firstDataRowFinder = null;
        this.tableFooterFinder = castOrNull(tableFooterFinder);
        this.headerDescription = headerDescription;
        this.headerRowsCount = headerRowsCount;
    }

    /**
     * Finds and creates a table in a report, whose title matches to {@code tableNameFinder} predicate.
     * The first data row of the table matches to {@code firstDataRowFinder} predicate.
     * The last row of the table precedes the row, which matches to {@code tableFooterFinder}.
     * If {@code tableFooterFinder} is null, the last row of the table precedes the empty row.
     * If {@code tableFooterFinder} is null and empty row not found, when table ends with report last row.
     */
    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        Predicate<String> tableNameFinder,
                        int tableNameRowCount,
                        Predicate<String> firstDataRowFinder,
                        @Nullable Predicate<String> tableFooterFinder,
                        Class<T> headerDescription) {
        super(report);
        this.createMode = CreateMode.TABLE_BY_PREDICATE;
        //noinspection DataFlowIssue
        this.tableName = null;
        this.tableNameFinder = cast(tableNameFinder);
        this.tableNameRowCount = tableNameRowCount;
        this.firstDataRowFinder = castOrNull(firstDataRowFinder);
        this.tableFooterFinder = castOrNull(tableFooterFinder);
        this.headerDescription = headerDescription;
        this.headerRowsCount = -1;
    }

    /**
     * Finds and creates a table without title, whose first header row case-insensitive matches to {@code headerRowPrefix} prefix.
     * The last row of the table precedes the row, which case-insensitive matches to {@code tableFooter} prefix.
     * If {@code tableFooter} is null, the last row of the table precedes the empty row.
     * If {@code tableFooter} is null and empty row not found, when table ends with report last row.
     *
     * @param providedTableName assigned table name (doesn't exist in the broker report)
     */
    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        String providedTableName,
                        String headerRowPrefix,
                        @Nullable String tableFooter,
                        Class<T> headerDescription) {
        this(report, providedTableName, headerRowPrefix, tableFooter, headerDescription, 1);
    }

    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        String providedTableName,
                        String headerRowPrefix,
                        @Nullable String tableFooter,
                        Class<T> headerDescription,
                        int headerRowsCount) {
        this(report, providedTableName, getPrefixPredicate(headerRowPrefix),
                getPrefixPredicateOrNull(tableFooter), headerDescription, headerRowsCount);
    }

    /**
     * Finds and creates a table without title, whose first header row case-insensitive matches to {@code headerRowPrefix} prefix.
     * The first data row of the table matches to {@code firstDataRowPrefix} prefix.
     * The last row of the table precedes the row, which matches to {@code tableFooter}.
     * If {@code tableFooter} is null, the last row of the table precedes the empty row.
     * If {@code tableFooter} is null and empty row not found, when table ends with report last row.
     */
    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        String providedTableName,
                        String headerRowPrefix,
                        String firstDataRowPrefix,
                        @Nullable String tableFooter,
                        Class<T> headerDescription) {
        this(report, providedTableName, getPrefixPredicate(headerRowPrefix), getPrefixPredicate(firstDataRowPrefix),
                getPrefixPredicateOrNull(tableFooter), headerDescription);
    }

    /**
     * Finds and creates a table without title, whose first header row matches to {@code headerRowFinder} predicate.
     * TThe last row of the table precedes the row, which matches to {@code tableFooterFinder} predicate.
     * If {@code tableFooterFinder} is null, the last row of the table precedes the empty row.
     * If {@code tableFooterFinder} is null and empty row not found, when table ends with report last row.
     *
     * @param providedTableName assigned table name (doesn't exist in the broker report)
     */
    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        String providedTableName,
                        Predicate<String> headerRowFinder,
                        @Nullable Predicate<String> tableFooterFinder,
                        Class<T> headerDescription) {
        this(report, providedTableName, headerRowFinder, tableFooterFinder, headerDescription, 1);
    }

    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        String providedTableName,
                        Predicate<String> headerRowFinder,
                        @Nullable Predicate<String> tableFooterFinder,
                        Class<T> headerDescription,
                        int headerRowsCount) {
        super(report);
        this.createMode = CreateMode.NAMELESS_TABLE_BY_PREDICATE;
        this.tableName = providedTableName;
        this.tableNameRowCount = 0;
        this.tableNameFinder = cast(headerRowFinder);
        this.firstDataRowFinder = null;
        this.tableFooterFinder = castOrNull(tableFooterFinder);
        this.headerDescription = headerDescription;
        this.headerRowsCount = headerRowsCount;
    }

    protected <T extends Enum<T> & TableHeaderColumn>
    AbstractReportTable(BrokerReport report,
                        String providedTableName,
                        Predicate<String> headerRowFinder,
                        Predicate<String> firstDataRowFinder,
                        @Nullable Predicate<String> tableFooterFinder,
                        Class<T> headerDescription) {
        super(report);
        this.createMode = CreateMode.NAMELESS_TABLE_BY_PREDICATE;
        this.tableName = providedTableName;
        this.tableNameRowCount = 0;
        this.tableNameFinder = cast(headerRowFinder);
        this.firstDataRowFinder = cast(firstDataRowFinder);
        this.tableFooterFinder = castOrNull(tableFooterFinder);
        this.headerDescription = headerDescription;
        this.headerRowsCount = -1;
    }

    private static @Nullable Predicate<String> getPrefixPredicateOrNull(@Nullable String prefix) {
        return (prefix == null || prefix.isEmpty()) ? null : getPrefixPredicate(prefix);
    }

    private static Predicate<String> getPrefixPredicate(String prefix) {
        return ignoreCaseStringPrefixPredicate(prefix);
    }

    private static @Nullable Predicate<@Nullable Object> castOrNull(@Nullable Predicate<String> predicate) {
        return (predicate == null) ? null : cast(predicate);
    }

    private static Predicate<@Nullable Object> cast(Predicate<String> predicate) {
        return cell -> (cell instanceof CharSequence) && predicate.test(cell.toString());
    }

    @Override
    protected Collection<R> parseTable() {
        try {
            ReportPage reportPage = getReport().getReportPage();
            Table table = createTable(reportPage);
            return parseTable(table);
        } catch (Exception e) {
            String displayTableName = (tableName == null) ? " " : " '" + tableName + "' ";
            throw new BrokerReportParseException("Can't parse table" + displayTableName + "in report " + getReport(), e);
        }
    }

    private <T extends Enum<T> & TableHeaderColumn>
    Table createTable(ReportPage reportPage) {
        @SuppressWarnings("unchecked")
        Class<T> headerDesc = (Class<T>) headerDescription;
        Table table;
        if (createMode == CreateMode.TABLE_BY_PREDICATE) {
            table = (firstDataRowFinder == null) ?
                    reportPage.createTable(tableNameFinder, tableNameRowCount, tableFooterFinder, headerDesc, headerRowsCount) :
                    reportPage.createTable(tableNameFinder, tableNameRowCount, firstDataRowFinder, tableFooterFinder, headerDesc);
        } else if (createMode == CreateMode.NAMELESS_TABLE_BY_PREDICATE) {
            @SuppressWarnings({"nullness", "ConstantConditions"})
            String providedTableName = requireNonNull(tableName);
            table = (firstDataRowFinder == null) ?
                    reportPage.createNamelessTable(providedTableName, tableNameFinder, tableFooterFinder, headerDesc, headerRowsCount) :
                    reportPage.createNamelessTable(providedTableName, tableNameFinder, firstDataRowFinder, tableFooterFinder, headerDesc);
        } else {
            throw new IllegalArgumentException("Unexpected create mode = " + createMode);
        }
        if (tableFooterFinder != null) {
            table = table.excludeLastRow();
        }
        return table;
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

    @SuppressWarnings("ConstantConditions")
    protected Collection<R> mergeDuplicates(R oldObject, R newObject) {
        try {
            return List.of(oldObject, newObject);
        } catch (NullPointerException ignore) {
            if (oldObject == null && newObject == null) {
                return List.of();
            } else if (oldObject == null) {
                return List.of(newObject);
            }
            return List.of(oldObject);
        }
    }

    private enum CreateMode {
        TABLE_BY_PREDICATE,
        NAMELESS_TABLE_BY_PREDICATE
    }
}