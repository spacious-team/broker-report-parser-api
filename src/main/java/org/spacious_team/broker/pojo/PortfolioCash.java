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

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.math.BigDecimal;
import java.time.Instant;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;


@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode(cacheStrategy = LAZY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Остаток денежных средств")
public class PortfolioCash {
    // autoincrement
    @Schema(description = "Внутренний идентификатор", example = "123", nullable = true)
    private final @Nullable Integer id;

    @Schema(description = "Номер счета в системе учета брокера", example = "10200I", requiredMode = REQUIRED)
    private final @NotEmpty String portfolio;

    @Schema(description = "Значение актуально на дату", example = "2021-01-23T12:00:00+03:00", requiredMode = REQUIRED)
    private final Instant timestamp;

    @Schema(description = "Рынок", example = "Фондовый", requiredMode = REQUIRED)
    private final String market;

    @EqualsAndHashCode.Exclude
    @Schema(description = "Остаток денежных средств", example = "102.30", requiredMode = REQUIRED)
    private final BigDecimal value;

    @Schema(description = "Валюта", example = "RUB", requiredMode = REQUIRED)
    private final @NotEmpty String currency;

    @EqualsAndHashCode.Include
    @SuppressWarnings({"nullness", "ConstantConditions", "ReturnOfNull", "unused"})
    private BigDecimal getValueForEquals() {
        return (value == null) ? null : value.stripTrailingZeros();
    }
}
