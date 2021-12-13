/*
 * Broker Report Parser API
 * Copyright (C) 2020  Vitalii Ananev <spacious-team@ya.ru>
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

package org.spacious_team.broker.report_parser.api;

import java.io.InputStream;

public interface BrokerReportFactory {

    /**
     * Fast check if this factory can't parse report.
     * Method should always reset input stream mark to original position.
     * @return false when can't parse, true when parsing maybe possible
     * @throws IllegalArgumentException if InputStream is not supports mark
     */
    boolean canCreate(String fileName, InputStream is);

    /**
     * Checks input stream and returns broker report if can, otherwise reset input stream mark to original position
     * and returns null
     * @return broker report if can parse or null
     * @throws IllegalArgumentException if InputStream is not supports mark
     */
    BrokerReport create(String fileName, InputStream is);

    String getBrokerName();
}
