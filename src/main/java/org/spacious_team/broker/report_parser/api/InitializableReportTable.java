/*
 * Broker Report Parser API
 * Copyright (C) 2020  Spacious Team <spacious-team@ya.ru>
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

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public abstract class InitializableReportTable<RowType> implements ReportTable<RowType> {
    @Getter
    private final BrokerReport report;
    private final List<RowType> data = new ArrayList<>();
    private volatile boolean initialized = false;

    @Override
    public List<RowType> getData() {
        initializeIfNeed();
        return data;
    }

    protected void initializeIfNeed() {
        try {
            if (!initialized) {
                synchronized (this) {
                    if (!initialized) {
                        data.addAll(parseTable());
                    }
                    initialized = true;
                }
            }
        } catch (Exception e) {
            throw new BrokerReportParseException("Can't parse report " + report, e);
        }
    }

    protected abstract Collection<RowType> parseTable();
}
