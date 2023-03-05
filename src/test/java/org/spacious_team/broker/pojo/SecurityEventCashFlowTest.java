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

package org.spacious_team.broker.pojo;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;

import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.junit.jupiter.api.Assertions.*;
import static org.spacious_team.broker.pojo.CashFlowType.COUPON;
import static org.spacious_team.broker.pojo.CashFlowType.DIVIDEND;

class SecurityEventCashFlowTest {

    SecurityEventCashFlow flow = SecurityEventCashFlow.builder()
            .id(1)
            .portfolio("123")
            .timestamp(Instant.MIN)
            .security(2)
            .eventType(DIVIDEND)
            // differ
            .count(3)
            .value(BigDecimal.ONE)
            .currency("USD")
            .build();

    SecurityEventCashFlow equalsFlow = SecurityEventCashFlow.builder()
            .id(1)
            .portfolio("123")
            .timestamp(Instant.MIN)
            .security(2)
            .eventType(DIVIDEND)
            // differ
            .count(4)
            .value(BigDecimal.TEN)
            .currency("RUB")
            .build();

    @Test
    void checkEquality_equals() {
        assertTrue(SecurityEventCashFlow.checkEquality(flow, equalsFlow));
    }

    @Test
    void checkEquality_anotherPortfolio_notEquals() {
        SecurityEventCashFlow notEqualsFlow = flow.toBuilder()
                .portfolio("2")
                .build();

        assertFalse(SecurityEventCashFlow.checkEquality(flow, notEqualsFlow));
    }

    @Test
    void checkEquality_anotherTimestamp_notEquals() {
        SecurityEventCashFlow notEqualsFlow = flow.toBuilder()
                .timestamp(Instant.MAX)
                .build();

        assertFalse(SecurityEventCashFlow.checkEquality(flow, notEqualsFlow));
    }

    @Test
    void checkEquality_anotherSecurity_notEquals() {
        SecurityEventCashFlow notEqualsFlow = flow.toBuilder()
                .security(3)
                .build();

        assertFalse(SecurityEventCashFlow.checkEquality(flow, notEqualsFlow));
    }

    @Test
    void checkEquality_anotherEventType_notEquals() {
        SecurityEventCashFlow notEqualsFlow = flow.toBuilder()
                .eventType(COUPON)
                .build();

        assertFalse(SecurityEventCashFlow.checkEquality(flow, notEqualsFlow));
    }

    @Test
    void mergeDuplicates_success() {
        SecurityEventCashFlow mergingFlow = flow.toBuilder()
                .value(BigDecimal.TEN)
                .build();
        Collection<SecurityEventCashFlow> mergedFlows = SecurityEventCashFlow.mergeDuplicates(flow, mergingFlow);

        assertEquals(1, mergedFlows.size());
        assertEquals(
                flow.getValue().add(mergingFlow.getValue()),
                mergedFlows.iterator().next().getValue());
    }

    @Test
    void mergeDuplicates_anotherCount_exception() {
        SecurityEventCashFlow mergingFlow = flow.toBuilder()
                .count(4)
                .build();

        assertThrows(RuntimeException.class, () -> SecurityEventCashFlow.mergeDuplicates(flow, mergingFlow));
    }

    @Test
    void mergeDuplicates_anotherCurrency_exception() {
        SecurityEventCashFlow mergingFlow = flow.toBuilder()
                .currency("RUB")
                .build();

        assertThrows(RuntimeException.class, () -> SecurityEventCashFlow.mergeDuplicates(flow, mergingFlow));
    }

    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(SecurityEventCashFlow.class)
                .suppress(STRICT_INHERITANCE) // no subclass for test
                .withLombokCachedHashCode(SecurityEventCashFlow.builder().build())
                .verify();
    }

    @Test
    void testToString() {
        assertEquals(
                "SecurityEventCashFlow(id=1, portfolio=123, timestamp=-1000000000-01-01T00:00:00Z, " +
                        "security=2, count=3, eventType=DIVIDEND, value=1, currency=USD)",
                flow.toString());
    }
}