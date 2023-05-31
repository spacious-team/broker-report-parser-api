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
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static java.util.Collections.singletonList;
import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode(cacheStrategy = LAZY)
@Schema(name = "Событие по бумаге", description = "Дивиденды, купоны, амортизация, вариационная маржа, налоги, комиссии")
public class SecurityEventCashFlow {
    // autoincrement
    @Schema(description = "Внутренний идентификатор записи", example = "222", nullable = true)
    private final @Nullable Integer id;

    @Schema(description = "Номер счета в системе учета брокера", example = "10200I", requiredMode = REQUIRED)
    private final @NotEmpty String portfolio;

    @Schema(description = "Время события", example = "2021-01-01T19:00:00+03:00", requiredMode = REQUIRED)
    private final Instant timestamp;

    @Schema(description = "Инструмент", example = "123", requiredMode = REQUIRED)
    private final int security;

    @Schema(description = "Количество бумаг (контрактов)", example = "10", requiredMode = REQUIRED)
    private final Integer count;

    @JsonProperty("event-type")
    @Schema(description = "Тип события", example = "DIVIDEND", requiredMode = REQUIRED)
    private final CashFlowType eventType;

    @EqualsAndHashCode.Exclude
    @Schema(description = "Сумма", example = "100.20", requiredMode = REQUIRED)
    private final BigDecimal value;

    @Builder.Default
    @Schema(description = "Валюта", example = "RUB", defaultValue = "RUB", nullable = true)
    private final String currency = "RUR";

    /**
     * Checks DB unique index constraint
     */
    @SuppressWarnings("unused")
    public static boolean checkEquality(SecurityEventCashFlow cash1, SecurityEventCashFlow cash2) {
        return cash1.getSecurity() == cash2.getSecurity() &&
                Objects.equals(cash1.getEventType(), cash2.getEventType()) &&
                Objects.equals(cash1.getTimestamp(), cash2.getTimestamp()) &&
                Objects.equals(cash1.getPortfolio(), cash2.getPortfolio());
    }

    /**
     * Merge information of two objects with equals by {@link #checkEquality(SecurityEventCashFlow, SecurityEventCashFlow)}
     */
    @SuppressWarnings("unused")
    public static Collection<SecurityEventCashFlow> mergeDuplicates(SecurityEventCashFlow cash1, SecurityEventCashFlow cash2) {
        if (!Objects.equals(cash1.getCurrency(), cash2.getCurrency())) {
            throw new IllegalArgumentException("Can't merge events with different currencies: " + cash1 + " and " + cash2);
        } else if (!Objects.equals(cash1.getCount(), cash2.getCount())) {
            throw new IllegalArgumentException("Can't merge events with different 'count' fields: " + cash1 + " and " + cash2);
        }
        BigDecimal summedValue = cash1.getValue().add(cash2.getValue());
        return singletonList(
                cash1.toBuilder()
                        .value(summedValue)
                        .build());
    }

    @EqualsAndHashCode.Include
    @SuppressWarnings({"nullness", "ConstantConditions", "ReturnOfNull", "unused"})
    private BigDecimal getValueForEquals() {
        return (value == null) ? null : value.stripTrailingZeros();
    }
}
