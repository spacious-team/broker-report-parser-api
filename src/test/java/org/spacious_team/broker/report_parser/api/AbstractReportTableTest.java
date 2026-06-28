/*
 * Broker Report Parser API
 * Copyright (C) 2023  Spacious Team <spacious-team@ya.ru>
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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spacious_team.table_wrapper.api.ReportPage;
import org.spacious_team.table_wrapper.api.Table;
import org.spacious_team.table_wrapper.api.TableColumn;
import org.spacious_team.table_wrapper.api.TableHeaderColumn;
import org.spacious_team.table_wrapper.api.TableRow;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class AbstractReportTableTest {

    @Mock
    BrokerReport brokerReport;
    @Mock
    ReportPage reportPage;
    @Mock
    Table table;
    @Mock
    Table tableWithoutTotal;
    @Captor
    ArgumentCaptor<Predicate<Object>> tableNamePredicateCaptor;
    @Captor
    ArgumentCaptor<Predicate<Object>> tableFooterPredicateCaptor;

    @BeforeEach
    void before() {
        lenient().when(brokerReport.getReportPage()).thenReturn(reportPage);
    }

    @Test
    void createTable_exception() {
        when(reportPage.createTable(any(Predicate.class), eq(1), eq(null), eq(TableHeader.class), eq(1)))
                .thenThrow(IllegalArgumentException.class);
        ReportTable reportTable = new ReportTable(brokerReport, "table1", null);

        assertThrows(BrokerReportParseException.class, reportTable::parseTable);
    }

    @Test
    void createTable_byPrefixAndWithoutFooter() {
        when(reportPage.createTable(any(Predicate.class), eq(1), eq(null), eq(TableHeader.class), eq(1))).thenReturn(table);
        ReportTable reportTable = new ReportTable(brokerReport, "table1", null);

        reportTable.parseTable();

        verify(table, never()).excludeLastRow();
        verify(table).getDataCollection(eq(brokerReport), any(), any(), any());
        verify(reportPage).createTable(tableNamePredicateCaptor.capture(), eq(1), eq(null), eq(TableHeader.class), eq(1));
        Predicate<Object> tableNameFinder = tableNamePredicateCaptor.getValue();
        matches(tableNameFinder, "table1", "TABLE1", " table1 ", "table12");
        notMatches(tableNameFinder, "table2", "table", "", new Object(), null);
    }

    @Test
    void createTable_byPrefixAndFooter() {
        when(reportPage.createTable(any(Predicate.class), eq(1), any(Predicate.class), eq(TableHeader.class), eq(1)))
                .thenReturn(table);
        when(table.excludeLastRow()).thenReturn(tableWithoutTotal);
        ReportTable reportTable = new ReportTable(brokerReport, "table1", "total1");

        reportTable.parseTable();

        verify(table).excludeLastRow();
        verify(table, never()).getDataCollection(any(), any(), any(), any());
        verify(tableWithoutTotal).getDataCollection(eq(brokerReport), any(), any(), any());
        verify(reportPage).createTable(
                tableNamePredicateCaptor.capture(),
                eq(1),
                tableFooterPredicateCaptor.capture(),
                eq(TableHeader.class),
                eq(1));
        Predicate<Object> tableNameFinder = tableNamePredicateCaptor.getValue();
        matches(tableNameFinder, "table1", "TABLE1", " table1 ", "table12");
        notMatches(tableNameFinder, "table2", "table");
        Predicate<Object> tableFooterFinder = tableFooterPredicateCaptor.getValue();
        matches(tableFooterFinder, "total1", "TOTAL1", " total1 ", "total12");
        notMatches(tableFooterFinder, "total2", "total", "", new Object(), null);
    }

    @Test
    void createTable_byPredicateAndWithoutFooter() {
        Predicate<String> tableNameFinder = v -> Objects.equals(v, "table1");
        Predicate<String> tableFooterFinder = v -> Objects.equals(v, "total1");

        assertDoesNotThrow(() -> new ReportTable(brokerReport, tableNameFinder, null));
        assertDoesNotThrow(() -> new ReportTable(brokerReport, tableNameFinder, tableFooterFinder));
        // other tests already done
    }

    @Test
    void createTable_byHeaderRowCount() {
        int headerRowCnt = 2;
        when(reportPage.createTable(any(Predicate.class), anyInt(), any(), any(), anyInt())).thenReturn(table);
        ReportTable reportTable = new ReportTable(brokerReport, "table1", "", headerRowCnt);

        reportTable.parseTable();

        verify(reportPage).createTable(any(Predicate.class), eq(1), eq(null), eq(TableHeader.class), eq(headerRowCnt));
    }

    @Test
    void createTable_byTableFirstRow() {
        when(reportPage.createTable(any(Predicate.class), anyInt(), any(), any(), any())).thenReturn(table);
        ReportTable reportTable = new ReportTable(brokerReport, "table1", 2, "firstData", null);

        reportTable.parseTable();

        verify(reportPage).createTable(any(Predicate.class), eq(2), any(Predicate.class), eq(null), eq(TableHeader.class));
    }

    @Test
    void createTableNameless_byTableFirstRow() {
        when(reportPage.createNamelessTable(any(String.class), any(Predicate.class), any(), any(), any())).thenReturn(table);
        ReportTable reportTable = new ReportTable(brokerReport, "provided-name", "header", "firstData", null);

        reportTable.parseTable();

        verify(reportPage).createNamelessTable(eq("provided-name"), any(Predicate.class), any(Predicate.class), eq(null), eq(TableHeader.class));
    }

    @Test
    void createNamelessTable_byPrefixAndWithoutFooter() {
        when(reportPage.createNamelessTable(eq("providedName"), any(Predicate.class), eq(null), eq(TableHeader.class), eq(1)))
                .thenReturn(table);
        ReportTable reportTable =
                new ReportTable(brokerReport, "providedName", "row1", null);

        reportTable.parseTable();

        verify(table, never()).excludeLastRow();
        verify(table).getDataCollection(eq(brokerReport), any(), any(), any());
        verify(reportPage).createNamelessTable(
                eq("providedName"), tableNamePredicateCaptor.capture(), eq(null), eq(TableHeader.class), eq(1));
        Predicate<Object> tableFirstLineFinder = tableNamePredicateCaptor.getValue();
        matches(tableFirstLineFinder, "row1", "ROW1", " row1 ", "row12");
        notMatches(tableFirstLineFinder, "row2", "row", "", new Object(), null);
    }

    @Test
    void createNamelessTable_byPrefixAndFooter() {
        when(reportPage.createNamelessTable(
                eq("providedName"), any(Predicate.class), any(Predicate.class), eq(TableHeader.class), eq(1)))
                .thenReturn(table);
        when(table.excludeLastRow()).thenReturn(tableWithoutTotal);
        ReportTable reportTable =
                new ReportTable(brokerReport, "providedName", "row1", "total1");

        reportTable.parseTable();

        verify(table).excludeLastRow();
        verify(table, never()).getDataCollection(any(), any(), any(), any());
        verify(tableWithoutTotal).getDataCollection(eq(brokerReport), any(), any(), any());
        verify(reportPage).createNamelessTable(
                eq("providedName"),
                tableNamePredicateCaptor.capture(),
                tableFooterPredicateCaptor.capture(),
                eq(TableHeader.class),
                eq(1));
        Predicate<Object> tableFirstLineFinder = tableNamePredicateCaptor.getValue();
        matches(tableFirstLineFinder, "row1", "ROW1", " row1 ", "row12");
        notMatches(tableFirstLineFinder, "row2", "row", "", new Object(), null);
        Predicate<Object> tableFooterFinder = tableFooterPredicateCaptor.getValue();
        matches(tableFooterFinder, "total1", "TOTAL1", " total1 ", "total12");
        notMatches(tableFooterFinder, "total2", "total", "", new Object(), null);
    }

    @Test
    void createNamelessTable_byPredicateAndWithoutFooter() {
        Predicate<String> tableFirstLineFinder = v -> Objects.equals(v, "table1");
        Predicate<String> tableFooterFinder = v -> Objects.equals(v, "total1");

        assertDoesNotThrow(() -> new ReportTable(brokerReport, "providedName", tableFirstLineFinder, null));
        assertDoesNotThrow(() -> new ReportTable(brokerReport, "providedName", tableFirstLineFinder, tableFooterFinder));
        // other tests already done
    }

    private static void matches(Predicate<Object> tableNameFinder, Object... values) {
        for (Object value : values) {
            assertTrue(tableNameFinder.test(value));
        }
    }

    private static void notMatches(Predicate<Object> tableNameFinder, @Nullable Object... values) {
        for (Object value : values) {
            assertFalse(tableNameFinder.test(value));
        }
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void parseRowToCollection_noRows() {
        ReportTable reportTable = mock(ReportTable.class);
        when(reportTable.parseRowToCollection(any())).thenCallRealMethod();

        Collection<?> rows = reportTable.parseRowToCollection(null);

        assertTrue(rows.isEmpty());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void parseRowToCollection_singleRow() {
        Object row = new Object();
        ReportTable reportTable = mock(ReportTable.class);
        when(reportTable.parseRowToCollection(any())).thenCallRealMethod();
        when(reportTable.parseRow(any())).thenReturn(row);

        Collection<?> rows = reportTable.parseRowToCollection(null);

        assertEquals(1, rows.size());
        assertEquals(row, rows.iterator().next());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void parseRow() {
        ReportTable reportTable = mock(ReportTable.class);
        when(reportTable.parseRow(any())).thenCallRealMethod();

        assertNull(reportTable.parseRow(null));
        assertNull(reportTable.parseRow(mock(TableRow.class)));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void checkEquality() {
        Object row1 = new Object();
        Object row2 = new Object();
        ReportTable reportTable = mock(ReportTable.class);
        when(reportTable.checkEquality(any(), any())).thenCallRealMethod();

        assertTrue(reportTable.checkEquality(row1, row1));
        assertFalse(reportTable.checkEquality(row1, row2));
        assertFalse(reportTable.checkEquality(row1, null));
        assertFalse(reportTable.checkEquality(null, row2));
        assertTrue(reportTable.checkEquality(null, null));
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void mergeDuplicates() {
        Object row1 = new Object();
        Object row2 = new Object();
        ReportTable reportTable = mock(ReportTable.class);
        when(reportTable.mergeDuplicates(any(), any())).thenCallRealMethod();

        assertEquals(List.of(row1, row1), reportTable.mergeDuplicates(row1, row1));
        assertEquals(List.of(row1, row2), reportTable.mergeDuplicates(row1, row2));
        assertEquals(List.of(row1), reportTable.mergeDuplicates(row1, null));
        assertEquals(List.of(row2), reportTable.mergeDuplicates(null, row2));
        assertEquals(List.of(), reportTable.mergeDuplicates(null, null));
    }


    static class ReportTable extends AbstractReportTable<Object> {

        ReportTable(BrokerReport report, String tableName, @Nullable String tableFooter) {
            super(report, tableName, tableFooter, TableHeader.class);
        }

        ReportTable(BrokerReport report, String tableName, @Nullable String tableFooter, int headerRowsCount) {
            super(report, tableName, tableFooter, TableHeader.class, headerRowsCount);
        }

        ReportTable(BrokerReport report,
                    String tableName,
                    int tableNameRowCount,
                    String firstDataRow,
                    @Nullable String tableFooter) {
            super(report, tableName, tableNameRowCount, firstDataRow, tableFooter, TableHeader.class);
        }

        ReportTable(BrokerReport report, Predicate<String> tableNameFinder, @Nullable Predicate<String> tableFooterFinder) {
            super(report, tableNameFinder, tableFooterFinder, TableHeader.class);
        }

        ReportTable(BrokerReport report, String providedTableName, String namelessTableFirstLine,
                    @Nullable String tableFooter) {
            super(report, providedTableName, namelessTableFirstLine, tableFooter, TableHeader.class);
        }

        ReportTable(BrokerReport report,
                    String providedTableName,
                    String headerRowPrefix,
                    String firstDataRowPrefix,
                    @Nullable String tableFooter) {
            super(report, providedTableName, headerRowPrefix, firstDataRowPrefix, tableFooter, TableHeader.class);
        }

        ReportTable(BrokerReport report, String providedTableName, Predicate<String> namelessTableFirstLineFinder,
                    @Nullable Predicate<String> tableFooterFinder) {
            super(report, providedTableName, namelessTableFirstLineFinder, tableFooterFinder, TableHeader.class);
        }

        @Override
        public Collection<Object> parseRowToCollection(TableRow row) {
            return super.parseRowToCollection(row);
        }

        @Override
        public @Nullable Object parseRow(TableRow row) {
            return super.parseRow(row);
        }

        @Override
        public boolean checkEquality(Object object1, Object object2) {
            return super.checkEquality(object1, object2);
        }

        @Override
        public Collection<Object> mergeDuplicates(Object oldObject, Object newObject) {
            return super.mergeDuplicates(oldObject, newObject);
        }
    }

    enum TableHeader implements TableHeaderColumn {
        ;

        @Override
        @SuppressWarnings({"ReturnOfNull", "ConstantConditions", "NullableProblems"})
        public TableColumn getColumn() {
            return null;
        }
    }
}