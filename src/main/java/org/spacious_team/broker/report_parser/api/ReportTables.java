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

import org.spacious_team.broker.pojo.EventCashFlow;
import org.spacious_team.broker.pojo.PortfolioProperty;
import org.spacious_team.broker.pojo.Security;
import org.spacious_team.broker.pojo.SecurityEventCashFlow;
import org.spacious_team.broker.pojo.SecurityQuote;

public interface ReportTables {
    BrokerReport getReport();

    ReportTable<PortfolioProperty> getPortfolioPropertyTable();

    ReportTable<PortfolioCash> getCashTable();

    ReportTable<EventCashFlow> getCashFlowTable();

    ReportTable<Security> getSecuritiesTable();

    ReportTable<SecurityTransaction> getSecurityTransactionTable();

    ReportTable<DerivativeTransaction> getDerivativeTransactionTable();

    ReportTable<ForeignExchangeTransaction> getForeignExchangeTransactionTable();

    ReportTable<SecurityEventCashFlow> getCouponAmortizationRedemptionTable();

    ReportTable<SecurityEventCashFlow> getDividendTable();

    ReportTable<SecurityEventCashFlow> getDerivativeCashFlowTable();

    ReportTable<SecurityQuote> getSecurityQuoteTable();
}
