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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.StringJoiner;

import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode(cacheStrategy = LAZY)
@Schema(name = "Движение ДС по счету", description = "Ввод и вывод ДС, налоги, комиссии, а также выплаты по инструментам другого счета")
public class EventCashFlow {
    //@Nullable // autoincrement
    @Schema(description = "Идентификатор записи", example = "123", nullable = true)
    private final Integer id;

    @NotNull
    @Schema(description = "Номер счета", example = "10200I", required = true)
    private final String portfolio;

    @NotNull
    @Schema(description = "Время события", example = "2021-01-01T12:00:00+03:00", required = true)
    private final Instant timestamp;

    @NotNull
    @JsonProperty("event-type")
    @Schema(description = "Тип события", example = "CASH", required = true)
    private final CashFlowType eventType;

    @NotNull
    @Schema(description = "Значение", example = "100.50", required = true)
    private final BigDecimal value;

    @Builder.Default
    @Schema(description = "Валюта", example = "RUB", defaultValue = "RUB", nullable = true)
    private final String currency = "RUB";

    //@Nullable
    @Schema(description = "Описание события", example = "Внесение наличных", nullable = true)
    private final String description;

    /**
     * Checks DB unique index constraint
     */
    public static boolean checkEquality(EventCashFlow cash1, EventCashFlow cash2) {
        return cash1.getPortfolio().equals(cash2.getPortfolio()) &&
                cash1.getTimestamp().equals(cash2.getTimestamp()) &&
                cash1.getEventType().equals(cash2.getEventType()) &&
                cash1.getValue().equals(cash2.getValue()) &&
                cash1.getCurrency().equals(cash2.getCurrency());
    }

    /**
     * Merge information of two objects with equals by {@link #checkEquality(EventCashFlow, EventCashFlow)}
     */
    public static Collection<EventCashFlow> mergeDuplicates(EventCashFlow cash1, EventCashFlow cash2) {
        StringJoiner joiner = new StringJoiner("; ");
        if (cash1.getDescription() != null) joiner.add(cash1.getDescription());
        if (cash2.getDescription() != null) joiner.add(cash2.getDescription());
        String description = (joiner.length() == 0) ? null : joiner.toString();
        return Collections.singletonList(cash1.toBuilder()
                .value(cash1.getValue().add(cash2.getValue()))
                .description((description == null) ? null : description.substring(0, Math.min(500, description.length())))
                .build());
    }
}
