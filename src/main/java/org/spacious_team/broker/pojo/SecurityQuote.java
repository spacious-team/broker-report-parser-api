/*
 * Broker Report Parser API
 * Copyright (C) 2021  Vitalii Ananev <an-vitek@ya.ru>
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

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

import static org.spacious_team.broker.pojo.SecurityType.DERIVATIVE;

@Getter
@ToString
@Builder(toBuilder = true)
@EqualsAndHashCode
public class SecurityQuote {

    @NotNull
    private final Integer id;

    @NotNull
    private final String security;

    @NotNull
    private final Instant timestamp;

    @NotNull
    private final BigDecimal quote; // for stock and currency pair in currency, for bond - in percent, for derivative - in quote

    //@Nullable
    private final BigDecimal price; // for bond and derivative - in currency, for others is null

    //@Nullable
    private final BigDecimal accruedInterest; // for bond in currency, for others is null

    /**
     * Returns price in currency (not a quote), bond price accounted without accrued interest. May be null if unknown.
     */
    public BigDecimal getCleanPriceInCurrency() {
        SecurityType type = SecurityType.getSecurityType(security);
        if (type == DERIVATIVE) {
            return price;
        } else {
            if (price == null && accruedInterest == null) {
                return quote; // for stocks and currency pairs
            } else {
                return price; // for bonds
            }
        }
    }

    /**
     * Returns price in currency (not a quote), bond price accounted with accrued interest. May be null if unknown.
     */
    public BigDecimal getDirtyPriceInCurrency() {
        BigDecimal cleanPrice = getCleanPriceInCurrency();
        return (cleanPrice == null || accruedInterest == null) ? cleanPrice : cleanPrice.add(accruedInterest);
    }
}
