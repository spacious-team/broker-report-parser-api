/*
 * Broker Report Parser API
 * Copyright (C) 2021  Vitalii Ananev <spacious-team@ya.ru>
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

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;
import static org.spacious_team.broker.pojo.SecurityType.DERIVATIVE;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode(cacheStrategy = LAZY)
@Schema(name = "Котировка")
public class SecurityQuote {

    //@Nullable // autoincrement
    @Schema(description = "Внутренний идентификатор записи", example = "333", nullable = true)
    private final Integer id;

    @NotNull
    @Schema(description = "Инструмент", example = "NL0009805522", required = true)
    private final String security;

    @NotNull
    @Schema(description = "Тип", example = "STOCK", required = true)
    private final SecurityType securityType;

    @NotNull
    @Schema(description = "Время", example = "2021-01-01T19:00:00+03:00", required = true)
    private final Instant timestamp;

    @NotNull
    @Schema(description = "Котировка (для облигаций - в процентах, деривативы - в пунктах)", example = "4800.20", required = true)
    private final BigDecimal quote; // for stock, currency pair and asset in currency, for bond - in percent, for derivative - in quote

    //@Nullable
    @Schema(description = "Котировка (в валюте, только для облигаций и деривативов)", example = "1020.30", nullable = true)
    private final BigDecimal price; // for bond and derivative - in currency, for others is null

    //@Nullable
    @JsonProperty("accrued-interest")
    @Schema(description = "НКД (в валюте, только для облигаций)", example = "10.20", nullable = true)
    private final BigDecimal accruedInterest; // for bond in currency, for others is null

    //@Nullable
    @JsonProperty("currency")
    @Schema(description = "Валюта котировки для акций, облигаций, произвольных активов и опционально для деривативов",
            example = "RUB", nullable = true)
    private final String currency;

    /**
     * Returns price in currency (not a quote), bond price accounted without accrued interest. May be null if unknown.
     */
    @JsonIgnore
    @Schema(hidden = true)
    public BigDecimal getCleanPriceInCurrency() {
        if (securityType == DERIVATIVE) {
            return price;
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
    public BigDecimal getDirtyPriceInCurrency() {
        BigDecimal cleanPrice = getCleanPriceInCurrency();
        return (cleanPrice == null || accruedInterest == null) ? cleanPrice : cleanPrice.add(accruedInterest);
    }
}
