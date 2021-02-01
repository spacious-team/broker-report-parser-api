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
import java.time.Instant;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode
@Schema(name = "Сделка")
public class Transaction {
    @NotNull
    @Schema(description = "Номер сделки в системе учета брокера", example = "123SP", required = true)
    private final String id;

    @NotNull
    @Schema(description = "Номер счета в системе учета брокера", example = "10200I", required = true)
    private final String portfolio;

    @NotNull
    @Schema(description = "Инструмент", example = "NL0009805522", required = true)
    private final String security;

    @NotNull
    @Schema(description = "Время сделки", example = "2021-01-23T12:00:00+03:00", required = true)
    private final Instant timestamp;

    @NotNull
    @Schema(description = "Количество бумаг (контрактов), шт", example = "10", required = true)
    private final int count;
}
