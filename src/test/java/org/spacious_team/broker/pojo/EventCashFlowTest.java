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
import static org.spacious_team.broker.pojo.CashFlowType.CASH;
import static org.spacious_team.broker.pojo.CashFlowType.FEE;

class EventCashFlowTest {

    EventCashFlow flow = EventCashFlow.builder()
            .portfolio("1")
            .timestamp(Instant.MIN)
            .eventType(CASH)
            .value(new BigDecimal("100.0")) // scale = 1
            .currency("RUB")
            .id(0)
            // different
            .description("desc1")
            .build();

    EventCashFlow equalsFlow = EventCashFlow.builder()
            .portfolio("1")
            .timestamp(Instant.MIN)
            .eventType(CASH)
            .value(new BigDecimal("100.00")) // scale = 2
            .currency("RUB")
            // different
            .id(1)
            .description("desc2")
            .build();

    @Test
    void checkEquality_equals() {
        assertTrue(EventCashFlow.checkEquality(flow, equalsFlow));
    }

    @Test
    void checkEquality_exponentValue_equals() {
        EventCashFlow exponentValueFlow = flow.toBuilder()
                .value(new BigDecimal("1e2"))
                .build();

        assertTrue(EventCashFlow.checkEquality(flow, exponentValueFlow));
    }

    @Test
    void checkEquality_anotherPortfolio_notEquals() {
        EventCashFlow notEqualsFlow = flow.toBuilder()
                .portfolio("2")
                .build();

        assertFalse(EventCashFlow.checkEquality(flow, notEqualsFlow));
    }

    @Test
    void checkEquality_anotherTimestamp_notEquals() {
        EventCashFlow notEqualsFlow = flow.toBuilder()
                .timestamp(Instant.MAX)
                .build();

        assertFalse(EventCashFlow.checkEquality(flow, notEqualsFlow));
    }

    @Test
    void checkEquality_anotherEventType_notEquals() {
        EventCashFlow notEqualsFlow = flow.toBuilder()
                .eventType(FEE)
                .build();

        assertFalse(EventCashFlow.checkEquality(flow, notEqualsFlow));
    }

    @Test
    void checkEquality_anotherValue_notEquals() {
        EventCashFlow notEqualsFlow = flow.toBuilder()
                .value(BigDecimal.ZERO)
                .build();

        assertFalse(EventCashFlow.checkEquality(flow, notEqualsFlow));
    }

    @Test
    void checkEquality_anotherCurrency_notEquals() {
        EventCashFlow notEqualsFlow = flow.toBuilder()
                .currency("USD")
                .build();

        assertFalse(EventCashFlow.checkEquality(flow, notEqualsFlow));
    }

    @Test
    void mergeDuplicates() {
        Collection<EventCashFlow> mergedFlows = EventCashFlow.mergeDuplicates(flow, equalsFlow);

        assertEquals(1, mergedFlows.size());
        assertEquals(
                flow.getValue().add(equalsFlow.getValue()),
                mergedFlows.iterator().next().getValue());
        assertEquals(
                flow.getDescription() + "; " + equalsFlow.getDescription(),
                mergedFlows.iterator().next().getDescription());
    }

    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(EventCashFlow.class)
                .suppress(STRICT_INHERITANCE) // no subclass for test
                .withLombokCachedHashCode(EventCashFlow.builder().build())
                .verify();
    }

    @Test
    void testToString() {
        assertEquals("EventCashFlow(id=0, portfolio=1, timestamp=-1000000000-01-01T00:00:00Z, eventType=CASH, " +
                        "value=100.0, currency=RUB, description=desc1)",
                flow.toString());
    }
}