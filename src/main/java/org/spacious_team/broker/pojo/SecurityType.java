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
    STOCK_OR_BOND("акция/облигация"), // нельзя сказать точно акция или облигация
    DERIVATIVE("срочный контракт"),
    CURRENCY_PAIR("валюта"),
    ASSET("произвольный актив");

    public static final String ASSET_PREFIX = "ASSET:";
    @Getter
    private final String description;

    public static SecurityType getSecurityType(Security security) {
        return getSecurityType(security.getId());
    }

    public static SecurityType getSecurityType(String security) {
        int length = security.length();
        if (length == 12 && security.indexOf('-') == -1) {
            return STOCK_OR_BOND;
        } else if (length == 6 || (length > 7 && security.charAt(6) == '_')) { // USDRUB_TOM or USDRUB_TOD or USDRUB
            return CURRENCY_PAIR;
        } else if (security.startsWith(ASSET_PREFIX)) {
            return ASSET;
        }
        // фьючерс всегда с дефисом, например Si-12.21, опцион может быть MXI-6.21M170621CA3000 или MM3000BF1
        return DERIVATIVE;
    }

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
}
