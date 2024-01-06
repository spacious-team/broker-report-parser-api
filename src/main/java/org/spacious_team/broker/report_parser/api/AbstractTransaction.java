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
import org.spacious_team.broker.pojo.Transaction;
import org.spacious_team.broker.pojo.TransactionCashFlow;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.spacious_team.broker.pojo.CashFlowType.FEE;
import static org.spacious_team.broker.pojo.CashFlowType.PRICE;

@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder(toBuilder = true)
public abstract class AbstractTransaction {
    protected final Integer id;
    protected final String tradeId;
    protected final String portfolio;
    protected final int security;
    protected final Instant timestamp;
    protected final int count;
    @EqualsAndHashCode.Exclude
    protected final @Nullable BigDecimal value; // стоимость в валюте цены, null для зачисления и списания ЦБ
    @EqualsAndHashCode.Exclude
    protected final @Nullable BigDecimal fee;
    protected final @Nullable String valueCurrency; // валюта платежа. Обязателен, если заполнен value
    protected final @Nullable String feeCurrency; // валюта комиссии. Обязателен, если заполнен fee


    @SuppressWarnings("unused")
    public Transaction getTransaction() {
        return Transaction.builder()
                .id(id)
                .tradeId(tradeId)
                .portfolio(portfolio)
                .security(security)
                .timestamp(timestamp)
                .count(count)
                .build();
    }

    @SuppressWarnings("unused")
    public List<TransactionCashFlow> getTransactionCashFlows() {
        List<TransactionCashFlow> list = new ArrayList<>(2);
        getValueCashFlow(PRICE).ifPresent(list::add);
        getFeeCashFlow().ifPresent(list::add);
        return list;
    }

    protected Optional<TransactionCashFlow> getValueCashFlow(CashFlowType type) {
        if (value != null && valueCurrency != null && Math.abs(value.floatValue()) >= 0.0001) {
        return Optional.of(TransactionCashFlow.builder()
                .transactionId(id)
                .eventType(type)
                .value(value)
                .currency(valueCurrency)
                .build());
        }
        return Optional.empty();
    }

    protected Optional<TransactionCashFlow> getFeeCashFlow() {
        if (fee != null && feeCurrency != null && Math.abs(fee.floatValue()) >= 0.0001) {
            return Optional.of(TransactionCashFlow.builder()
                    .transactionId(id)
                    .eventType(FEE)
                    .value(fee)
                    .currency(feeCurrency)
                    .build());
        }
        return Optional.empty();
    }

    @EqualsAndHashCode.Include
    @SuppressWarnings("unused")
    private  @Nullable BigDecimal getValueForEquals() {
        return (value == null) ? null : value.stripTrailingZeros();
    }

    @EqualsAndHashCode.Include
    @SuppressWarnings("unused")
    private @Nullable BigDecimal getFeeForEquals() {
        return (fee == null) ? null : fee.stripTrailingZeros();
    }

    public abstract AbstractTransactionBuilder<? extends AbstractTransaction, ? extends AbstractTransactionBuilder<?, ?>> toBuilder();
}
