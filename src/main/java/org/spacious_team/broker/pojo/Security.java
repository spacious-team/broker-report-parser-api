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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode
@Schema(name = "Инструмент", description = "Акция, облигация, валютная пара, фьючерс или опцион")
public class Security {
    @NotNull
    @Schema(description = "Идентификатор (для акций и облигаций - ISIN, валютный пары и срочных контрактов - название)",
            example = "NL0009805522, USDRUB_TOM или Si-12.21", required = true)
    private final String id;

    //@Nullable
    @Schema(description = "Тикер (опционально)", example = "YNDX", nullable = true)
    private final String ticker;

    //@Nullable
    @Schema(description = "Наименвание (опционально)", example = "Yandex clA", nullable = true)
    private final String name;

    //@Nullable
    @Schema(description = "Эмитент (опционально)", example = "7736207543", nullable = true)
    private final Long inn;
}
