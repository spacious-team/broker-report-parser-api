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

import static nl.jqno.equalsverifier.Warning.STRICT_INHERITANCE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityQuoteTest {

    SecurityQuote quote = SecurityQuote.builder()
            .id(1)
            .security(2)
            .timestamp(Instant.MIN)
            .quote(BigDecimal.ONE)
            .currency("USD")
            .build();

    SecurityQuote stockQuote = quote;

    SecurityQuote bondQuote = quote.toBuilder()
            .price(BigDecimal.TEN)
            .accruedInterest(BigDecimal.valueOf(2))
            .build();

    SecurityQuote derivativeQuote = quote.toBuilder()
            .price(BigDecimal.TEN)
            .build();

    @Test
    void getCleanPriceInCurrency_forStockOrCurrencyPairOrAsset() {
        assertEquals(stockQuote.getQuote(), stockQuote.getCleanPriceInCurrency(false));
    }

    @Test
    void getCleanPriceInCurrency_forBond() {
        assertEquals(bondQuote.getPrice(), bondQuote.getCleanPriceInCurrency(false));
    }

    @Test
    void getCleanPriceInCurrency_forDerivative() {
        assertEquals(derivativeQuote.getPrice(), derivativeQuote.getCleanPriceInCurrency(true));
    }

    @Test
    void getDirtyPriceInCurrency_forStockOrCurrencyPairOrAsset() {
        assertEquals(stockQuote.getQuote(), stockQuote.getDirtyPriceInCurrency(false));
    }

    @Test
    void getDirtyPriceInCurrency_forBond() {
        //noinspection ConstantConditions
        BigDecimal dirtyPrice = bondQuote.getPrice()
                .add(bondQuote.getAccruedInterest());

        assertEquals(dirtyPrice, bondQuote.getDirtyPriceInCurrency(false));
    }

    @Test
    void getDirtyPriceInCurrency_forDerivative() {
        assertEquals(derivativeQuote.getPrice(), derivativeQuote.getDirtyPriceInCurrency(true));
    }

    @Test
    void testEqualsAndHashCode() {
        EqualsVerifier
                .forClass(SecurityQuote.class)
                .suppress(STRICT_INHERITANCE) // no subclass for test
                .withLombokCachedHashCode(SecurityQuote.builder().build())
                .verify();
    }

    @Test
    void testToString() {
        assertEquals(
                "SecurityQuote(id=1, security=2, timestamp=-1000000000-01-01T00:00:00Z, quote=1, " +
                        "price=10, accruedInterest=2, currency=USD)",
                bondQuote.toString());
    }
}