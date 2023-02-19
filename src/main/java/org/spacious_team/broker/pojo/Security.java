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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.validation.constraints.Pattern;

import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode(cacheStrategy = LAZY)
@Schema(name = "Инструмент", description = "Акция, облигация, валютная пара, фьючерс или опцион")
public class Security {
    @Nullable // autoincrement
    @Schema(description = "Внутренний идентификатор инструмента", example = "123", nullable = true)
    private final Integer id;

    @Schema(description = "Тип ценной бумаги", example = "STOCK", required = true)
    private final SecurityType type;

    @Nullable
    @Pattern(regexp = "^[A-Z]{2}[A-Z0-9]{9}[0-9]$")
    @Schema(description = "ISIN акций и облигаций (опционально)", example = "NL0009805522", nullable = true)
    private final String isin;

    @Nullable
    @Schema(description = "Тикер (опционально)", example = "YNDX, USDRUB_TOM или Si-12.21", nullable = true)
    private final String ticker;

    @Nullable
    @Schema(description = "Наименование (опционально)", example = "Yandex clA", nullable = true)
    private final String name;
}
