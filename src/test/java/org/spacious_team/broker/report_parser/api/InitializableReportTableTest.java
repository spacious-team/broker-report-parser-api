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

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitializableReportTableTest {

    @Mock
    BrokerReport brokerReport;

    @Test
    void getReport() {
        InitializableReportTableImpl reportTable = new InitializableReportTableImpl(brokerReport);
        assertSame(brokerReport, reportTable.getReport());
    }

    @Test
    void getData() {
        InitializableReportTableImpl reportTable = new InitializableReportTableImpl(brokerReport);
        Collection<Object> expected = reportTable.getData();
        assertSame(expected, reportTable.getData());
    }

    @Test
    void initializeIfNeed() {
        InitializableReportTableImpl reportTable = spy(new InitializableReportTableImpl(brokerReport));

        reportTable.getData();
        reportTable.getData();

        verify(reportTable, times(2)).initializeIfNeed();
        verify(reportTable).parseTable();
    }


    static class InitializableReportTableImpl extends InitializableReportTable<Object> {

        public InitializableReportTableImpl(BrokerReport report) {
            super(report);
        }

        @Override
        protected Collection<Object> parseTable() {
            return new ArrayList<>();  // new object every time
        }

        @Override
        public void initializeIfNeed() {
            super.initializeIfNeed();
        }
    }
}