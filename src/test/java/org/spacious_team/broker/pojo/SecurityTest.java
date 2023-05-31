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

import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.spacious_team.broker.pojo.SecurityType.STOCK;

class SecurityTest {

    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(Security.class)
                .suppress(STRICT_INHERITANCE) // no subclass for test
                .withLombokCachedHashCode(Security.builder().build())
                .verify();
    }

    @Test
    void testToString() {
        Security security = Security.builder()
                .id(1)
                .type(STOCK)
                .isin("NL0009805522")
                .ticker("YNDX")
                .name("Yandex clA")
                .build();

        assertEquals(
                "Security(id=1, type=STOCK, isin=NL0009805522, ticker=YNDX, name=Yandex clA)",
                security.toString());
    }
}