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
import static org.spacious_team.broker.pojo.PortfolioPropertyType.TOTAL_ASSETS_USD;

class PortfolioPropertyTest {

    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(PortfolioProperty.class)
                .suppress(STRICT_INHERITANCE) // no subclass for test
                .withLombokCachedHashCode(PortfolioProperty.builder().build())
                .verify();
    }

    @Test
    void testToString() {
        PortfolioProperty cash = PortfolioProperty.builder()
                .id(1)
                .portfolio("123")
                .timestamp(Instant.MIN)
                .property(TOTAL_ASSETS_USD)
                .value("123.45")
                .build();

        assertEquals(
                "PortfolioProperty(id=1, portfolio=123, timestamp=-1000000000-01-01T00:00:00Z, " +
                        "property=TOTAL_ASSETS_USD, value=123.45)",
                cash.toString());
    }
}