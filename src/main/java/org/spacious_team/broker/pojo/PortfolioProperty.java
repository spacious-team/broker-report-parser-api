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
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode(cacheStrategy = LAZY)
@Schema(name = "Свойства счета")
public class PortfolioProperty {
    // autoincrement
    @Schema(description = "Внутренний идентификатор записи", example = "111", nullable = true)
    private final @Nullable Integer id;

    @Schema(description = "Номер счета", example = "10200I", requiredMode = REQUIRED)
    private final @NotEmpty String portfolio;

    @Schema(description = "Информация актуальна на время", example = "2021-01-01T12:00:00+03:00", nullable = true)
    private final @Nullable Instant timestamp;

    @Schema(description = "Свойство портфеля", example = "TOTAL_ASSETS_RUB", requiredMode = REQUIRED)
    private final PortfolioPropertyType property;

    @Schema(description = "Значение свойства", example = "100.20", requiredMode = REQUIRED)
    private final String value;
}
