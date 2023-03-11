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
import org.spacious_team.broker.pojo.EventCashFlow;
import org.spacious_team.broker.pojo.ForeignExchangeRate;
import org.spacious_team.broker.pojo.PortfolioCash;
import org.spacious_team.broker.pojo.PortfolioProperty;
import org.spacious_team.broker.pojo.Security;
import org.spacious_team.broker.pojo.SecurityEventCashFlow;
import org.spacious_team.broker.pojo.SecurityQuote;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AbstractReportTablesTest {

    @Mock
    BrokerReport brokerReport;

    @Test
    void emptyTable() {
        ReportTables tables = new ReportTables(brokerReport);

        EmptyReportTable<Object> table = tables.emptyTable();

        assertTrue(table.getData().isEmpty());
        assertSame(brokerReport, table.getReport());
    }


    @SuppressWarnings({"ReturnOfNull", "ConstantConditions"})
    static class ReportTables extends AbstractReportTables<BrokerReport> {


        protected ReportTables(BrokerReport report) {
            super(report);
        }

        @Override
        public <E> EmptyReportTable<E> emptyTable() {
            return super.emptyTable();
        }

        @Override
        public ReportTable<PortfolioProperty> getPortfolioPropertyTable() {
            return null;
        }

        @Override
        public ReportTable<PortfolioCash> getPortfolioCashTable() {
            return null;
        }

        @Override
        public ReportTable<EventCashFlow> getCashFlowTable() {
            return null;
        }

        @Override
        public ReportTable<Security> getSecuritiesTable() {
            return null;
        }

        @Override
        public ReportTable<AbstractTransaction> getTransactionTable() {
            return null;
        }

        @Override
        public ReportTable<SecurityEventCashFlow> getSecurityEventCashFlowTable() {
            return null;
        }

        @Override
        public ReportTable<SecurityQuote> getSecurityQuoteTable() {
            return null;
        }

        @Override
        public ReportTable<ForeignExchangeRate> getForeignExchangeRateTable() {
            return null;
        }
    }
}