/*
 * Broker Report Parser API
 * Copyright (C) 2020  Vitalii Ananev <an-vitek@ya.ru>
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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

public abstract class AbstractBrokerReportFactory implements BrokerReportFactory {

    /**
     * Checks input stream and returns broker report if can, otherwise reset input stream mark to original position
     * and returns null
     *
     * @param expectedFileNamePattern used for fast report check without input stream reading
     * @return broker report if can parse or null
     * @throws IllegalArgumentException if InputStream is not supports mark
     */
    public BrokerReport create(Pattern expectedFileNamePattern, String excelFileName, InputStream is,
                                                    BiFunction<String, InputStream, BrokerReport> brokerReportProvider) {
        if (!expectedFileNamePattern.matcher(excelFileName).matches()) {
            return null;
        }
        if (!is.markSupported()) {
            throw new IllegalArgumentException("Provided input stream doesn't supports mark");
        }
        is = new CloseIgnoringInputStream(is); // do not close stream
        is.mark(Integer.MAX_VALUE);
        Exception exception = null;
        try {
            return brokerReportProvider.apply(excelFileName, is);
        } catch (Exception e) {
            exception = e;
            return null;
        } finally {
            resetInputStream(is, exception);
        }
    }

    private static void resetInputStream(InputStream is, Throwable t) {
        try {
            is.reset();
        } catch (IOException ioe) {
            if (t != null) {
                ioe.addSuppressed(t);
            }
            throw new RuntimeException("Can't reset input stream", ioe);
        }
    }

    private static class CloseIgnoringInputStream extends FilterInputStream {
        public CloseIgnoringInputStream(InputStream in) {
            super(in);
        }

        public void close() {
            // Does nothing and ignores closing the wrapped stream
        }
    }
}
