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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.time.Instant;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode
@Schema(name = "Свойства счета")
public class PortfolioProperty {
    //@Nullable // autoincrement
    @Schema(description = "Внутренний идентификатор записи", example = "111", nullable = true)
    private final Integer id;

    @NotNull
    @Schema(description = "Номер счета", example = "10200I", required = true)
    private final String portfolio;

    //@Nullable
    @Schema(description = "Информация актуальна на время", example = "2021-01-01T12:00:00+03:00", nullable = true)
    private final Instant timestamp;

    @NotNull
    @Schema(description = "Свойство портфеля", example = "TOTAL_ASSETS_RUB", required = true)
    private final PortfolioPropertyType property;

    @NotNull
    @Schema(description = "Значение свойства", example = "100.20", required = true)
    private final String value;
}
