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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.spacious_team.broker.pojo.Transaction;
import org.spacious_team.broker.pojo.TransactionCashFlow;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.spacious_team.broker.pojo.CashFlowType.FEE;
import static org.spacious_team.broker.pojo.CashFlowType.PRICE;

class ForeignExchangeTransactionTest {

    ForeignExchangeTransaction tr = ForeignExchangeTransaction.builder()
            .id(1)
            .tradeId("t123")
            .portfolio("a123")
            .security(2)
            .timestamp(Instant.MIN)
            .count(3)
            .value(BigDecimal.TEN)
            .valueCurrency("USD")
            .fee(BigDecimal.ONE)
            .feeCurrency("RUB")
            .build();

    @Test
    void getTransaction() {
        Transaction expected = Transaction.builder()
                .id(tr.getId())
                .tradeId(tr.getTradeId())
                .portfolio(tr.getPortfolio())
                .security(tr.getSecurity())
                .timestamp(tr.getTimestamp())
                .count(tr.getCount())
                .build();
        assertEquals(expected, tr.getTransaction());
    }

    @Test
    void getTransactionCashFlows() {
        expectedCashFlows(tr,
                getValueCashFlow(tr),
                getFeeCashFlow(tr));
    }

    @Test
    void getTransactionCashFlows_valueIsZero() {
        ForeignExchangeTransaction tr = this.tr.toBuilder()
                .value(BigDecimal.ZERO)
                .build();
        expectedCashFlows(tr, getFeeCashFlow(tr));
    }

    @Test
    void getTransactionCashFlows_valueIsNull() {
        ForeignExchangeTransaction tr = this.tr.toBuilder()
                .value(null)
                .build();
        expectedCashFlows(tr, getFeeCashFlow(tr));
    }

    @Test
    void getTransactionCashFlows_feeIsZero() {
        ForeignExchangeTransaction tr = this.tr.toBuilder()
                .fee(BigDecimal.ZERO)
                .build();
        expectedCashFlows(tr, getValueCashFlow(tr));
    }

    @Test
    void getTransactionCashFlows_feeIsNull() {
        ForeignExchangeTransaction tr = this.tr.toBuilder()
                .fee(null)
                .build();
        expectedCashFlows(tr, getValueCashFlow(tr));
    }

    @NonNull
    private TransactionCashFlow getValueCashFlow(ForeignExchangeTransaction transaction) {
        return TransactionCashFlow.builder()
                .transactionId(transaction.getId())
                .eventType(PRICE)
                .value(transaction.getValue())
                .currency(transaction.getValueCurrency())
                .build();
    }

    @NonNull
    private TransactionCashFlow getFeeCashFlow(ForeignExchangeTransaction transaction) {
        return TransactionCashFlow.builder()
                .transactionId(transaction.getId())
                .eventType(FEE)
                .value(transaction.getFee())
                .currency(transaction.getFeeCurrency())
                .build();
    }

    private void expectedCashFlows(ForeignExchangeTransaction transaction, TransactionCashFlow... flows) {
        assertEquals(
                List.of(flows),
                transaction.getTransactionCashFlows());
    }

    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(ForeignExchangeTransaction.class)
                .suppress(STRICT_INHERITANCE) // no subclass for test
                .withLombokCachedHashCode(ForeignExchangeTransaction.builder().build())
                .verify();
    }

    @Test
    void testToString() {
        assertEquals("ForeignExchangeTransaction(super=AbstractTransaction(id=1, tradeId=t123, portfolio=a123, " +
                        "security=2, timestamp=-1000000000-01-01T00:00:00Z, count=3, " +
                        "value=10, fee=1, valueCurrency=USD, feeCurrency=RUB))",
                tr.toString());
    }
}