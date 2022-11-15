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
import org.spacious_team.broker.pojo.Transaction;
import org.spacious_team.broker.pojo.TransactionCashFlow;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
@EqualsAndHashCode(cacheStrategy = LAZY)
public abstract class AbstractTransaction {
    protected static final BigDecimal minValue = BigDecimal.valueOf(0.01);
    protected final Integer id;
    protected final String tradeId;
    protected final String portfolio;
    protected final int security;
    protected final Instant timestamp;
    protected final int count;
    protected final BigDecimal value; // стоиомсть в валюце цены
    protected final BigDecimal commission;
    protected final String valueCurrency; // валюта платежа
    protected final String commissionCurrency; // валюта коммиссии

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

    public List<TransactionCashFlow> getTransactionCashFlows() {
        List<TransactionCashFlow> list = new ArrayList<>(2);
        getValueCashFlow(CashFlowType.PRICE).ifPresent(list::add);
        getCommissionCashFlow().ifPresent(list::add);
        return list;
    }

    protected Optional<TransactionCashFlow> getValueCashFlow(CashFlowType type) {
        if (value != null && value.abs().compareTo(minValue) >= 0) {
        return Optional.of(TransactionCashFlow.builder()
                .transactionId(id)
                .eventType(type)
                .value(value)
                .currency(valueCurrency)
                .build());
        }
        return Optional.empty();
    }

    protected Optional<TransactionCashFlow> getCommissionCashFlow() {
        if (commission != null && commission.abs().compareTo(minValue) >= 0) {
            return Optional.of(TransactionCashFlow.builder()
                    .transactionId(id)
                    .eventType(CashFlowType.COMMISSION)
                    .value(commission)
                    .currency(commissionCurrency)
                    .build());
        }
        return Optional.empty();
    }

    public abstract AbstractTransactionBuilder<? extends AbstractTransaction, ? extends AbstractTransactionBuilder<?, ?>> toBuilder();
}
