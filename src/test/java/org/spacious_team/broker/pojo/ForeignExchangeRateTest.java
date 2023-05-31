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
import java.time.LocalDate;

import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.junit.jupiter.api.Assertions.*;

class ForeignExchangeRateTest {

    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(ForeignExchangeRate.class)
                .suppress(STRICT_INHERITANCE) // no subclass for test
                .withLombokCachedHashCode(ForeignExchangeRate.builder().build())
                .verify();
    }

    @Test
    void testToString() {
        ForeignExchangeRate rate = ForeignExchangeRate.builder()
                .date(LocalDate.of(2023, 2, 1))
                .currencyPair("USDRUB")
                .rate(BigDecimal.TEN)
                .build();

        assertEquals("ForeignExchangeRate(date=2023-02-01, currencyPair=USDRUB, rate=10)", rate.toString());
    }
}