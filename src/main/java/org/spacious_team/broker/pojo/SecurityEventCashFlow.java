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

@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode
@Schema(name = "Событие по бумаге", description = "Дивиденды, купоны, амортизация, вариационная маржа, налоги, комиссии")
public class SecurityEventCashFlow {
    //@Nullable // autoincrement
    @Schema(description = "Внутренний идентификатор записи", example = "222", nullable = true)
    private final Integer id;

    @NotNull
    @Schema(description = "Номер счета в системе учета брокера", example = "10200I", required = true)
    private final String portfolio;

    @NotNull
    @Schema(description = "Время события", example = "2021-01-01T19:00:00+03:00", required = true)
    private final Instant timestamp;

    @NotNull
    @Schema(description = "Инструмент", example = "NL0009805522", required = true)
    private final String security;

    @NotNull
    @Schema(description = "Количество бумаг (контрактов)", example = "10", required = true)
    private final Integer count;

    @NotNull
    @JsonProperty("event-type")
    @Schema(description = "Тип события", example = "DIVIDEND", required = true)
    private final CashFlowType eventType;

    @NotNull
    @Schema(description = "Сумма", example = "100.20", required = true)
    private final BigDecimal value;

    @Builder.Default
    @Schema(description = "Валюта", example = "RUB", defaultValue = "RUB", nullable = true)
    private final String currency = "RUR";

    /**
     * Checks DB unique index constraint
     */
    public static boolean checkEquality(SecurityEventCashFlow cash1, SecurityEventCashFlow cash2) {
        return cash1.getPortfolio().equals(cash2.getPortfolio()) &&
                cash1.getTimestamp().equals(cash2.getTimestamp()) &&
                cash1.getEventType().equals(cash2.getEventType()) &&
                cash1.getSecurity().equals(cash2.getSecurity());
    }

    /**
     * Merge information of two objects with equals by {@link #checkEquality(SecurityEventCashFlow, SecurityEventCashFlow)}
     */
    public static Collection<SecurityEventCashFlow> mergeDuplicates(SecurityEventCashFlow cash1, SecurityEventCashFlow cash2) {
        if (!String.valueOf(cash1.getCurrency())
                .equals(String.valueOf(cash2.getCurrency()))) {
            throw new RuntimeException("Не могу объединить выплаты по ЦБ, разные валюты: " + cash1 + " и " + cash2);
        } else if (!String.valueOf(cash1.getCount())
                .equals(String.valueOf(cash2.getCount()))) {
            throw new RuntimeException("Не могу объединить выплаты по ЦБ, разное количество ЦБ: " + cash1 + " и " + cash2);
        }
        return Collections.singletonList(cash1.toBuilder()
                .value(cash1.getValue().add(cash2.getValue()))
                .build());
    }
}
