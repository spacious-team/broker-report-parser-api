/*
 * Broker Report Parser API
 * Copyright (C) 2021  Spacious Team <spacious-team@ya.ru>
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.math.BigDecimal;
import java.time.Instant;

import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode(cacheStrategy = LAZY)
@Schema(name = "Котировка")
public class SecurityQuote {

    // autoincrement
    @Schema(description = "Внутренний идентификатор записи", example = "333", nullable = true)
    private final @Nullable Integer id;

    @Schema(description = "Инструмент", example = "123", required = true)
    private final int security;

    @Schema(description = "Время", example = "2021-01-01T19:00:00+03:00", required = true)
    private final Instant timestamp;

    @Schema(description = "Котировка (для облигаций - в процентах, деривативы - в пунктах)", example = "4800.20", required = true)
    private final BigDecimal quote; // for stock, currency pair and asset in currency, for bond - in percent, for derivative - in quote

    @Schema(description = "Котировка (в валюте, только для облигаций и деривативов)", example = "1020.30", nullable = true)
    private final @Nullable BigDecimal price; // for bond and derivative - in currency, for others is null

    @JsonProperty("accrued-interest")
    @Schema(description = "НКД (в валюте, только для облигаций)", example = "10.20", nullable = true)
    private final @Nullable BigDecimal accruedInterest; // for bond in currency, for others is null

    @JsonProperty("currency")
    @Schema(description = "Валюта котировки для акций, облигаций, произвольных активов и опционально для деривативов",
            example = "RUB", nullable = true)
    private final @Nullable String currency;

    /**
     * Returns price in currency (not a quote), bond price accounted without accrued interest. May be null if unknown.
     */
    @JsonIgnore
    @Schema(hidden = true)
    public @Nullable BigDecimal getCleanPriceInCurrency(boolean isDerivative) {
        if (isDerivative) {
            return price; // for future and option always use price, also in case of price == null
        } else if (price == null && accruedInterest == null) {
            return quote; // for stocks, currency pairs, asset
        } else {
            return price; // for bonds
        }
    }

    /**
     * Returns price in currency (not a quote), bond price accounted with accrued interest. May be null if unknown.
     */
    @JsonIgnore
    @Schema(hidden = true)
    @SuppressWarnings("unused")
    public @Nullable BigDecimal getDirtyPriceInCurrency(boolean isDerivative) {
        @Nullable BigDecimal cleanPrice = getCleanPriceInCurrency(isDerivative);
        return (cleanPrice == null || accruedInterest == null) ? cleanPrice : cleanPrice.add(accruedInterest);
    }
}
