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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.spacious_team.broker.pojo.SecurityType.*;

class SecurityTypeTest {

    @Test
    void isStockOrBond() {
        assertTrue(STOCK.isStockOrBond());
        assertTrue(BOND.isStockOrBond());
        assertTrue(STOCK_OR_BOND.isStockOrBond());
        assertFalse(DERIVATIVE.isStockOrBond());
        assertFalse(CURRENCY_PAIR.isStockOrBond());
        assertFalse(ASSET.isStockOrBond());
    }

    @Test
    void isStock() {
        assertTrue(STOCK.isStock());
        assertFalse(BOND.isStock());
        assertTrue(STOCK_OR_BOND.isStock());
        assertFalse(DERIVATIVE.isStock());
        assertFalse(CURRENCY_PAIR.isStock());
        assertFalse(ASSET.isStock());
    }

    @Test
    void isBond() {
        assertFalse(STOCK.isBond());
        assertTrue(BOND.isBond());
        assertTrue(STOCK_OR_BOND.isBond());
        assertFalse(DERIVATIVE.isBond());
        assertFalse(CURRENCY_PAIR.isBond());
        assertFalse(ASSET.isBond());
    }

    @ParameterizedTest
    @MethodSource("getFxContractAndCurrencyPair")
    void getCurrencyPair(String contract, String currencyPair) {
        assertEquals(currencyPair, SecurityType.getCurrencyPair(contract));
    }

    static Object[][] getFxContractAndCurrencyPair() {
        return new Object[][] {
                {"USDRUB_TOM", "USDRUB"},
                {"USDRUB", "USDRUB"},
                {"USDRU", "USDRU"},
                {"", ""}
        };
    }

    @Test
    void getCurrencyPair_nullValue_exception() {
        //noinspection ConstantConditions
        assertThrows(NullPointerException.class, () -> SecurityType.getCurrencyPair(null));
    }
}