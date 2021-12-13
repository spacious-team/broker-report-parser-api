/*
 * Broker Report Parser API
 * Copyright (C) 2020  Vitalii Ananev <spacious-team@ya.ru>
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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SecurityType {

    STOCK("акция"),
    BOND("облигация"),
    STOCK_OR_BOND("акция/облигация"), // not exactly known: STOCK or BOND
    DERIVATIVE("срочный контракт"),
    CURRENCY_PAIR("валютная пара"),
    ASSET("произвольный актив");

    @Getter
    private final String description;

    /**
     * Returns currency pairs, for example USDRUB, EURRUB
     */
    public static String getCurrencyPair(String contract) {
        return (contract.length() == 6) ? contract :
                contract.substring(0, Math.min(6, contract.length()));
    }

    public boolean isStockOrBond() {
        return this == STOCK || this == BOND || this == STOCK_OR_BOND;
    }

    public boolean isStock() {
        return this == STOCK || this == STOCK_OR_BOND;
    }

    public boolean isBond() {
        return this == BOND || this == STOCK_OR_BOND;
    }
}
