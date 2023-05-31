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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CashFlowType {
    CASH(0),   // пополнение и снятие
    PRICE(1),  // купля/продажа, чистая стоимость (без НКД)
    ACCRUED_INTEREST(2), // НКД
    FEE(3), // комиссия
    AMORTIZATION(4),    // амортизация
    REDEMPTION(5), // погашение номинала облигации
    COUPON(6), // выплата купона (сумма до удержания налога)
    DIVIDEND(7), // выплата дивиденда (сумма до удержания налога)
    DERIVATIVE_PROFIT(8), // вариационная маржа
    MARGIN(9), // гарантийное обеспечение
    TAX(10), // налог уплаченный
    FORECAST_TAX(11), // прогнозируемый налог
    DERIVATIVE_PRICE(12), // Стоимость сделки с деривативом, валюта (RUB, USD или др.)
    DERIVATIVE_QUOTE(13); // Стоимость сделки с деривативом, пункты

    @Getter
    private final int id;

    public static CashFlowType valueOf(int type) {
        for (CashFlowType e : values()) {
            if (e.getId() == type) {
                return e;
            }
        }
        throw new IllegalArgumentException("Invalid cash flow event type: " + type);
    }
}
