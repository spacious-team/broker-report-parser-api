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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public abstract class AbstractBrokerReportFactory implements BrokerReportFactory {

    /**
     * @param expectedFileNamePattern used for fast report check without input stream reading
     */
    protected boolean canCreate(Pattern expectedFileNamePattern, String fileName, InputStream is) {
        return expectedFileNamePattern.matcher(fileName).matches();
    }

    /**
     * Checks input stream and returns broker report if it's possible.
     * Resets input stream to marked position.
     *
     * @return broker report if parse is possible
     * @throws IllegalArgumentException if InputStream does not support mark
     */
    protected Optional<BrokerReport> create(String fileName,
                                            InputStream is,
                                            BiFunction<String, InputStream, BrokerReport> brokerReportProvider) {
        if (!is.markSupported()) {
            throw new IllegalArgumentException("Provided input stream doesn't supports mark");
        }
        is = new CloseIgnoringInputStream(is); // do not close stream
        is.mark(Integer.MAX_VALUE);
        @Nullable Exception exception = null;
        try {
            return Optional.of(brokerReportProvider.apply(fileName, is));
        } catch (Exception e) {
            exception = e;
            return Optional.empty();
        } finally {
            resetInputStream(is, exception);
        }
    }

    private static void resetInputStream(InputStream is, @Nullable Exception exception) {
        try {
            is.reset();
        } catch (Exception e) {
            if (exception != null) {
                e.addSuppressed(exception);
            }
            throw new BrokerReportParseException("Can't reset input stream", e);
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
