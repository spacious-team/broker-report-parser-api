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


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.math.BigDecimal;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode(cacheStrategy = LAZY)
@Schema(name = "Движение ДС по сделке")
public class TransactionCashFlow {
    // autoincrement
    @Schema(description = "Внутренний идентификатор записи", example = "1", nullable = true)
    private final @Nullable Integer id;

    @JsonProperty("transaction-id")
    @Schema(description = "Внутренний идентификатор сделки", example = "123", requiredMode = REQUIRED)
    private final int transactionId;

    @JsonProperty("event-type")
    @Schema(description = "Тип события (стоимость бумаг без НКД, НКД, комиссия)", example = "PRICE", requiredMode = REQUIRED)
    private final CashFlowType eventType;

    @EqualsAndHashCode.Exclude
    @Schema(description = "Сумма по событию", example = "1000.20", requiredMode = REQUIRED)
    private final BigDecimal value;

    @Builder.Default
    @Schema(description = "Валюта", example = "RUB", defaultValue = "RUB", nullable = true)
    private final String currency = "RUB";

    @EqualsAndHashCode.Include
    @SuppressWarnings({"nullness", "ConstantConditions", "ReturnOfNull", "unused"})
    private BigDecimal getValueForEquals() {
        return (value == null) ? null : value.stripTrailingZeros();
    }
}
