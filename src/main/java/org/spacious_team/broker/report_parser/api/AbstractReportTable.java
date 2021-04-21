/*
 * Broker Report Parser API
 * Copyright (C) 2020  Vitalii Ananev <an-vitek@ya.ru>
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

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

public abstract class AbstractReportTable<RowType> extends InitializableReportTable<RowType> {

    private final String tableName;
    private final String namelessTableFirstLine;
    private final String tableFooter;
    private final Class<? extends TableColumnDescription> headerDescription;
    private final int headersRowCount;

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
        this.tableName = tableName;
        this.namelessTableFirstLine = null;
        this.tableFooter = tableFooter;
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
        this.tableName = providedTableName;
        this.namelessTableFirstLine = namelessTableFirstLine;
        this.tableFooter = tableFooter;
        this.headerDescription = headerDescription;
        this.headersRowCount = headersRowCount;
    }

    @Override
    protected Collection<RowType> parseTable() {
        try {
            ReportPage reportPage = getReport().getReportPage();
            Table table;
            if (namelessTableFirstLine == null) {
                table = (tableFooter != null && !tableFooter.isEmpty()) ?
                        reportPage.create(tableName, tableFooter, headerDescription, headersRowCount).excludeTotalRow() :
                        reportPage.create(tableName, headerDescription, headersRowCount);
            } else {
                table = (tableFooter != null && !tableFooter.isEmpty()) ?
                        reportPage.createNameless(
                                tableName, namelessTableFirstLine, tableFooter, headerDescription, headersRowCount)
                                .excludeTotalRow() :
                        reportPage.createNameless(
                                tableName, namelessTableFirstLine, headerDescription, headersRowCount);
            }
            return parseTable(table);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге таблицы '" + this.tableName + "' в отчете " + getReport(), e);
        }
    }

    protected Collection<RowType> parseTable(Table table) {
        return table.getDataCollection(getReport(), this::getRow, this::checkEquality, this::mergeDuplicates);
    }

    protected Instant convertToInstant(String dateTime) {
        return getReport().convertToInstant(dateTime);
    }

    protected abstract Collection<RowType> getRow(TableRow row);

    protected boolean checkEquality(RowType object1, RowType object2) {
        return object1.equals(object2);
    }

    protected Collection<RowType> mergeDuplicates(RowType oldObject, RowType newObject) {
        return Arrays.asList(oldObject, newObject);
    }
}