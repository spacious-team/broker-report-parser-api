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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode
@Schema(name = "Официальный обменный курс")
public class ForeignExchangeRate {
    @NotNull
    @Schema(description = "Дата", example = "2021-21-23", required = true)
    private final LocalDate date;

    @NotNull
    @JsonProperty("currency-pair")
    @Schema(description = "Валютная пара, для курса доллара в рублях - USDRUB", example = "USDRUB", required = true)
    private final String currencyPair;

    @NotNull
    @Schema(description = "Значение обменного курса", example = "75.67", required = true)
    private final BigDecimal rate;
}
