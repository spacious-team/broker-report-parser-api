/*
 * Broker Report Parser API
 * Copyright (C) 2020  Vitalii Ananev <spacious-team@ya.ru>
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

import org.spacious_team.table_wrapper.api.ReportPage;
import org.spacious_team.table_wrapper.api.Table;
import org.spacious_team.table_wrapper.api.TableColumnDescription;
import org.spacious_team.table_wrapper.api.TableRow;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

/**
 * To implement override one of {@link #parseTable()}, {@link #parseRow(TableRow)} or
 * {@link #parseRowToCollection(TableRow)} methods.
 */
public abstract class AbstractReportTable<RowType> extends InitializableReportTable<RowType> {

    private final String tableName;
    private final String namelessTableFirstLine;
    private final String tableFooter;
    private final Predicate<Object> tableNameFinder;
    private final Predicate<Object> tableFooterFinder;
    private final Class<? extends TableColumnDescription> headerDescription;
    private final int headersRowCount;
    private final CreateMode createMode;

    protected AbstractReportTable(BrokerReport report,
                                  String tableName,
                                  String tableFooter,
                                  Class<? extends TableColumnDescription> headerDescription) {
        this(report, tableName, tableFooter, headerDescription, 1);
    }

    protected AbstractReportTable(BrokerReport report,
                                  String tableName,
                                  String tableFooter,
                                  Class<? extends TableColumnDescription> headerDescription,
                                  int headersRowCount) {
        super(report);
        this.createMode = CreateMode.TABLE_BY_PREFIX;
        this.tableName = tableName;
        this.namelessTableFirstLine = null;
        this.tableFooter = tableFooter;
        this.tableNameFinder = null;
        this.tableFooterFinder = null;
        this.headerDescription = headerDescription;
        this.headersRowCount = headersRowCount;
    }

    protected AbstractReportTable(BrokerReport report,
                                  Predicate<String> tableNameFinder,
                                  Predicate<String> tableFooterFinder,
                                  Class<? extends TableColumnDescription> headerDescription) {
        this(report, tableNameFinder, tableFooterFinder, headerDescription, 1);
    }

    protected AbstractReportTable(BrokerReport report,
                                  Predicate<String> tableNameFinder,
                                  Predicate<String> tableFooterFinder,
                                  Class<? extends TableColumnDescription> headerDescription,
                                  int headersRowCount) {
        super(report);
        this.createMode = CreateMode.TABLE_BY_PREDICATE;
        this.tableName = null;
        this.namelessTableFirstLine = null;
        this.tableFooter = null;
        this.tableNameFinder = (cell) -> (cell instanceof String) && tableNameFinder.test(cell.toString());
        this.tableFooterFinder = (cell) -> (cell instanceof String) && tableFooterFinder.test(cell.toString());
        this.headerDescription = headerDescription;
        this.headersRowCount = headersRowCount;
    }

    protected AbstractReportTable(BrokerReport report,
                                  String providedTableName,
                                  String namelessTableFirstLine,
                                  String tableFooter,
                                  Class<? extends TableColumnDescription> headerDescription) {
        this(report, providedTableName, namelessTableFirstLine, tableFooter, headerDescription, 1);
    }

    protected AbstractReportTable(BrokerReport report,
                                  String providedTableName,
                                  String namelessTableFirstLine,
                                  String tableFooter,
                                  Class<? extends TableColumnDescription> headerDescription,
                                  int headersRowCount) {
        super(report);
        this.createMode = CreateMode.NAMELESS_TABLE_BY_PREFIX;
        this.tableName = providedTableName;
        this.namelessTableFirstLine = namelessTableFirstLine;
        this.tableFooter = tableFooter;
        this.tableNameFinder = null;
        this.tableFooterFinder = null;
        this.headerDescription = headerDescription;
        this.headersRowCount = headersRowCount;
    }

    protected AbstractReportTable(BrokerReport report,
                                  String providedTableName,
                                  Predicate<String> namelessTableFirstLineFinder,
                                  Predicate<String> tableFooterFinder,
                                  Class<? extends TableColumnDescription> headerDescription) {
        this(report, providedTableName, namelessTableFirstLineFinder, tableFooterFinder, headerDescription, 1);
    }

    protected AbstractReportTable(BrokerReport report,
                                  String providedTableName,
                                  Predicate<String> namelessTableFirstLineFinder,
                                  Predicate<String> tableFooterFinder,
                                  Class<? extends TableColumnDescription> headerDescription,
                                  int headersRowCount) {
        super(report);
        this.createMode = CreateMode.NAMELESS_TABLE_BY_PREDICATE;
        this.tableName = providedTableName;
        this.namelessTableFirstLine = null;
        this.tableFooter = null;
        this.tableNameFinder = (cell) -> (cell instanceof String) && namelessTableFirstLineFinder.test(cell.toString());
        this.tableFooterFinder = (cell) -> (cell instanceof String) && tableFooterFinder.test(cell.toString());
        this.headerDescription = headerDescription;
        this.headersRowCount = headersRowCount;
    }

    @Override
    protected Collection<RowType> parseTable() {
        try {
            ReportPage reportPage = getReport().getReportPage();
            Table table = createTable(reportPage);
            return parseTable(table);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге таблицы '" + tableName + "' в отчете " + getReport(), e);
        }
    }

    private Table createTable(ReportPage reportPage) {
        switch (createMode) {
            case TABLE_BY_PREFIX:
                return (tableFooter != null && !tableFooter.isEmpty()) ?
                        reportPage.create(tableName, tableFooter, headerDescription, headersRowCount).excludeTotalRow() :
                        reportPage.create(tableName, headerDescription, headersRowCount);
            case TABLE_BY_PREDICATE:
                return (tableFooterFinder != null) ?
                    reportPage.create(tableNameFinder, tableFooterFinder, headerDescription, headersRowCount).excludeTotalRow() :
                    reportPage.create(tableNameFinder, headerDescription, headersRowCount);
            case NAMELESS_TABLE_BY_PREFIX:
                return (tableFooter != null && !tableFooter.isEmpty()) ?
                        reportPage.createNameless(tableName, namelessTableFirstLine, tableFooter, headerDescription, headersRowCount).excludeTotalRow() :
                        reportPage.createNameless(tableName, namelessTableFirstLine, headerDescription, headersRowCount);
            case NAMELESS_TABLE_BY_PREDICATE:
                return (tableFooter != null && !tableFooter.isEmpty()) ?
                        reportPage.createNameless(tableName, tableNameFinder, tableFooterFinder, headerDescription, headersRowCount).excludeTotalRow() :
                        reportPage.createNameless(tableName, tableNameFinder, headerDescription, headersRowCount);
        }
        throw new IllegalArgumentException("Unexpected create mode = " + createMode);
    }

    protected Collection<RowType> parseTable(Table table) {
        return table.getDataCollection(getReport(), this::parseRowToCollection, this::checkEquality, this::mergeDuplicates);
    }

    protected Collection<RowType> parseRowToCollection(TableRow row) {
        RowType data = parseRow(row);
        return (data == null) ? emptyList() : singleton(data);
    }

    protected RowType parseRow(TableRow row) {
        return null;
    }

    protected boolean checkEquality(RowType object1, RowType object2) {
        return object1.equals(object2);
    }

    protected Collection<RowType> mergeDuplicates(RowType oldObject, RowType newObject) {
        return Arrays.asList(oldObject, newObject);
    }

    private enum CreateMode {
        TABLE_BY_PREFIX,
        TABLE_BY_PREDICATE,
        NAMELESS_TABLE_BY_PREFIX,
        NAMELESS_TABLE_BY_PREDICATE
    }
}