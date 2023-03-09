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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WrappingReportTableTest {

    @Mock
    BrokerReport brokerReport;

    @Test
    void ofEmptyData() {
        ReportTable<?> reportTable = WrappingReportTable.of(brokerReport);

        assertSame(brokerReport, reportTable.getReport());
        assertTrue(reportTable.getData().isEmpty());
    }

    @Test
    void ofData() {
        Collection<Object> col1 = List.of(new Object(), "123");
        Collection<Object> col2 = List.of(1, BigDecimal.ZERO);
        ReportTable<Object> reportTable = WrappingReportTable.of(brokerReport, col1, col2);

        List<Object> data = reportTable.getData();

        assertSame(brokerReport, reportTable.getReport());
        assertEquals(4, data.size());
        assertEquals(col1, data.subList(0, 2));
        assertEquals(col2, data.subList(2, 4));
    }

    @Test
    void ofReportTables() {
        Collection<Object> col1 = List.of(new Object(), "123");
        Collection<Object> col2 = List.of(1, BigDecimal.ZERO);
        ReportTable<Object> reportTable1 = spy(WrappingReportTable.of(brokerReport, col1));
        ReportTable<Object> reportTable2 = spy(WrappingReportTable.of(brokerReport, col2));
        ReportTable<Object> reportTable = WrappingReportTable.of(reportTable1, reportTable2);

        reportTable.getData();
        List<Object> data = reportTable.getData();

        assertSame(brokerReport, reportTable.getReport());
        assertEquals(4, data.size());
        assertEquals(col1, data.subList(0, 2));
        assertEquals(col2, data.subList(2, 4));
        verify(reportTable1).getData();
        verify(reportTable2).getData();
        verify(reportTable1, times(2)).getReport();
        verify(reportTable2).getReport();
    }

    @Test
    void ofReportTables_noTables_exception() {
        assertThrows(IllegalArgumentException.class, WrappingReportTable::of);
    }

    @Test
    void ofReportTables_differentBrokerReport_exception() {
        BrokerReport brokerReport1 = mock(BrokerReport.class);
        ReportTable<Object> reportTable1 = WrappingReportTable.of(brokerReport1);
        ReportTable<Object> reportTable2 = WrappingReportTable.of(brokerReport);

        assertThrows(IllegalArgumentException.class, () -> WrappingReportTable.of(reportTable1, reportTable2));
    }
}