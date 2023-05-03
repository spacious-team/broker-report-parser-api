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
import org.spacious_team.broker.pojo.CashFlowType;
import org.spacious_team.broker.pojo.TransactionCashFlow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;
import static org.spacious_team.broker.pojo.CashFlowType.DERIVATIVE_PRICE;
import static org.spacious_team.broker.pojo.CashFlowType.DERIVATIVE_QUOTE;

@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true, cacheStrategy = LAZY)
public class DerivativeTransaction extends AbstractTransaction {
    public static final String QUOTE_CURRENCY = "PNT";  // point
    @EqualsAndHashCode.Exclude
    private final BigDecimal valueInPoints;

    @Override
    public List<TransactionCashFlow> getTransactionCashFlows() {
        List<TransactionCashFlow> list = new ArrayList<>(2);
        getValueInPointsCashFlow().ifPresent(list::add);
        getValueCashFlow(DERIVATIVE_PRICE).ifPresent(list::add);
        getFeeCashFlow().ifPresent(list::add);
        return list;
    }

    protected Optional<TransactionCashFlow> getValueInPointsCashFlow() {
        //noinspection ConstantConditions
        if (valueInPoints != null) {
            return Optional.of(TransactionCashFlow.builder()
                    .transactionId(id)
                    .eventType(DERIVATIVE_QUOTE)
                    .value(valueInPoints)
                    .currency(QUOTE_CURRENCY)
                    .build());
        }
        return Optional.empty();
    }

    @Override
    protected Optional<TransactionCashFlow> getValueCashFlow(CashFlowType type) {
        //noinspection ConstantConditions
        if (value != null) {
            return Optional.of(TransactionCashFlow.builder()
                    .transactionId(id)
                    .eventType(type)
                    .value(value)
                    .currency(valueCurrency)
                    .build());
        }
        return Optional.empty();
    }

    @EqualsAndHashCode.Include
    @SuppressWarnings({"nullness", "ConstantConditions", "ReturnOfNull", "unused"})
    private BigDecimal getValueInPointsForEquals() {
        return (valueInPoints == null) ? null : valueInPoints.stripTrailingZeros();
    }
}
