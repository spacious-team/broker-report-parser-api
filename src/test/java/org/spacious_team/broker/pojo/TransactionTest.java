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

import java.time.Instant;

import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionTest {

    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(Transaction.class)
                .suppress(STRICT_INHERITANCE) // no subclass for test
                .withLombokCachedHashCode(Transaction.builder().build())
                .verify();
    }

    @Test
    void testToString() {
        Transaction transaction = Transaction.builder()
                .id(1)
                .tradeId("AB123")
                .portfolio("2")
                .security(3)
                .timestamp(Instant.MIN)
                .count(4)
                .build();

        assertEquals(
                "Transaction(id=1, tradeId=AB123, portfolio=2, security=3, " +
                        "timestamp=-1000000000-01-01T00:00:00Z, count=4)",
                transaction.toString());
    }
}