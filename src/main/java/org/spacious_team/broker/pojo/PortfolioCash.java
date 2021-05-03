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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

import static lombok.EqualsAndHashCode.CacheStrategy.LAZY;


@Getter
@ToString
@Jacksonized
@Builder(toBuilder = true)
@EqualsAndHashCode(cacheStrategy = LAZY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PortfolioCash {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    //@Nullable // autoincrement
    private final Integer id;
    private final String portfolio;
    private final Instant timestamp;
    private final String section;
    private final BigDecimal value;
    private final String currency;

    /**
     * Serializes in format:
     * <pre>
     * [
     *     {
     *         "section": "forts",
     *         "value": "1000",
     *         "currency": "RUB"
     *     }
     * ]
     * </pre>
     * {@code Portfolio} and {@code timestamp} fields are not serialized.
     * Used to serialize portfolio cash for {@link PortfolioProperty.PortfolioPropertyBuilder#value(String)}.
     */
    @Deprecated
    public static String serialize(Collection<PortfolioCash> cash) {
        try {
            cash = cash.stream()
                    .map(PortfolioCash::toBuilder)
                    .map(builder -> builder.portfolio(null))
                    .map(builder -> builder.timestamp(null))
                    .map(PortfolioCashBuilder::build)
                    .collect(Collectors.toList());
            return objectMapper.writeValueAsString(cash);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Can't serialize portfolio cash", e);
        }
    }

    /**
     * Used to deserialize portfolio cash from {@link PortfolioProperty#getValue()}.
     */
    @Deprecated
    public static Collection<PortfolioCash> deserialize(String value) {
        try {
            return objectMapper.readValue(value, new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't deserialize portfolio cash", e);
        }
    }
}
