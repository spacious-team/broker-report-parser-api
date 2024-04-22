/*
 * Broker Report Parser API
 * Copyright (C) 2021  Spacious Team <spacious-team@ya.ru>
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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spacious_team.broker.pojo.CashFlowType;
import org.spacious_team.broker.pojo.TransactionCashFlow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

@Getter
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, cacheStrategy = LAZY)
public class SecurityTransaction extends AbstractTransaction {
    @EqualsAndHashCode.Exclude
    private final @Nullable BigDecimal accruedInterest; // НКД, в валюте бумаги. Если задано, то поле valueCurrency обязательно

    @Override
    public List<TransactionCashFlow> getTransactionCashFlows() {
        List<TransactionCashFlow> list = new ArrayList<>(3);
        getValueCashFlow(CashFlowType.PRICE).ifPresent(list::add);
        getAccruedInterestCashFlow().ifPresent(list::add);
        getFeeCashFlow().ifPresent(list::add);
        return list;
    }

    private Optional<TransactionCashFlow> getAccruedInterestCashFlow() {
        // for securities accrued interest = 0
        if (accruedInterest != null && valueCurrency != null && Math.abs(accruedInterest.floatValue()) >= 0.0001) {
            return Optional.of(TransactionCashFlow.builder()
                    .transactionId(id)
                    .eventType(CashFlowType.ACCRUED_INTEREST)
                    .value(accruedInterest)
                    .currency(valueCurrency)
                    .build());
        }
        return Optional.empty();
    }

    @EqualsAndHashCode.Include
    @SuppressWarnings("unused")
    private @Nullable BigDecimal getAccruedInterestForEquals() {
        return (accruedInterest == null) ? null : accruedInterest.stripTrailingZeros();
    }
}
