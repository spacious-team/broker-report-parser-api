/*
 * Broker Report Parser API
 * Copyright (C) 2021  Vitalii Ananev <an-vitek@ya.ru>
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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.spacious_team.broker.pojo.CashFlowType;
import org.spacious_team.broker.pojo.TransactionCashFlow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SecurityTransaction extends AbstractTransaction {
    private final BigDecimal accruedInterest; // НКД, в валюте бумаги

    public List<TransactionCashFlow> getTransactionCashFlows() {
        List<TransactionCashFlow> list = new ArrayList<>(3);
        getValueCashFlow(CashFlowType.PRICE).ifPresent(list::add);
        getAccruedInterestCashFlow().ifPresent(list::add);
        getCommissionCashFlow().ifPresent(list::add);
        return list;
    }

    private Optional<TransactionCashFlow> getAccruedInterestCashFlow() {
        // for securities accrued interest = 0
        if (accruedInterest != null && accruedInterest.abs().compareTo(minValue) >= 0) {
            return Optional.of(TransactionCashFlow.builder()
                    .transactionId(transactionId)
                    .portfolio(portfolio)
                    .eventType(CashFlowType.ACCRUED_INTEREST)
                    .value(accruedInterest)
                    .currency(valueCurrency)
                    .build());
        }
        return Optional.empty();
    }
}
