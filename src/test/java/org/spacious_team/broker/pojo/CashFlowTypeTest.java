/*
 * Broker Report Parser API
 * Copyright (C) 2022  Spacious Team <spacious-team@ya.ru>
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CashFlowTypeTest {

    @Test
    void valueOf() {
        int i =0;
        for (CashFlowType expected : CashFlowType.values()) {
            CashFlowType actual = CashFlowType.valueOf(i);
            assertSame(expected, actual);
            assertEquals(expected.getId(), i);
            i++;
        }
    }

    @Test
    void valueOfThrowable() {
        assertThrows(IllegalArgumentException.class, () -> CashFlowType.valueOf(-1));
        assertThrows(IllegalArgumentException.class, () -> CashFlowType.valueOf(14));
    }
}