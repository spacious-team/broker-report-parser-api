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

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;


@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode(cacheStrategy = LAZY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Остаток денежных средств")
public class PortfolioCash {
    //@Nullable // autoincrement
    @Schema(description = "Внутренний идентификатор", example = "123", nullable = true)
    private final Integer id;

    @NotEmpty
    @Schema(description = "Номер счета в системе учета брокера", example = "10200I", required = true)
    private final String portfolio;

    @NotNull
    @Schema(description = "Значение актуально на дату", example = "2021-01-23T12:00:00+03:00", required = true)
    private final Instant timestamp;

    @NotNull
    @Schema(description = "Рынок", example = "Фондовый", required = true)
    private final String market;

    @NotNull
    @Schema(description = "Остаток денежных средств", example = "102.30", required = true)
    private final BigDecimal value;

    @NotEmpty
    @Schema(description = "Валюта", example = "RUB", required = true)
    private final String currency;
}
