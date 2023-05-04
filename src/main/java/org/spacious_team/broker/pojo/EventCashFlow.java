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

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.StringJoiner;

import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode(cacheStrategy = LAZY)
@Schema(name = "Движение ДС по счету", description = "Ввод и вывод ДС, налоги, комиссии, а также выплаты по инструментам другого счета")
public class EventCashFlow {
    // autoincrement
    @Schema(description = "Идентификатор записи", example = "123", nullable = true)
    private final @Nullable Integer id;

    @Schema(description = "Номер счета", example = "10200I", required = true)
    private final @NotEmpty String portfolio;

    @Schema(description = "Время события", example = "2021-01-01T12:00:00+03:00", required = true)
    private final Instant timestamp;

    @JsonProperty("event-type")
    @Schema(description = "Тип события", example = "CASH", required = true)
    private final CashFlowType eventType;

    @EqualsAndHashCode.Exclude
    @Schema(description = "Значение", example = "100.50", required = true)
    private final BigDecimal value;

    @Builder.Default
    @Schema(description = "Валюта", example = "RUB", defaultValue = "RUB")
    private final String currency = "RUB";

    @Schema(description = "Описание события", example = "Внесение наличных", nullable = true)
    private final @Nullable String description;

    /**
     * Checks DB unique index constraint
     */
    @SuppressWarnings("unused")
    public static boolean checkEquality(EventCashFlow cash1, EventCashFlow cash2) {
        BigDecimal value1 = cash1.getValue();
        BigDecimal value2 = cash2.getValue();
        //noinspection NumberEquality
        return Objects.equals(cash1.getEventType(), cash2.getEventType()) &&
                Objects.equals(cash1.getTimestamp(), cash2.getTimestamp()) &&
                Objects.equals(cash1.getPortfolio(), cash2.getPortfolio()) &&
                Objects.equals(cash1.getCurrency(), cash2.getCurrency()) &&
                ((value1 == value2) || (value1.compareTo(value2) == 0));
    }

    /**
     * Merge information of two objects with equals by {@link #checkEquality(EventCashFlow, EventCashFlow)}
     */
    @SuppressWarnings("unused")
    public static Collection<EventCashFlow> mergeDuplicates(EventCashFlow cash1, EventCashFlow cash2) {
        StringJoiner joiner = new StringJoiner("; ");
        if (cash1.getDescription() != null) joiner.add(cash1.getDescription());
        if (cash2.getDescription() != null) joiner.add(cash2.getDescription());
        String description = joiner.toString();
        return Collections.singletonList(cash1.toBuilder()
                .value(cash1.getValue().add(cash2.getValue()))
                .description(description.isEmpty() ? null : description.substring(0, Math.min(500, description.length())))
                .build());
    }

    @EqualsAndHashCode.Include
    @SuppressWarnings({"nullness", "ConstantConditions", "ReturnOfNull", "unused"})
    private BigDecimal getValueForEquals() {
        return (value == null) ? null : value.stripTrailingZeros();
    }
}
