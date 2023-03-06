/*
 * Broker Report Parser API
 * Copyright (C) 2023  Spacious Team <spacious-team@ya.ru>
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
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.spacious_team.table_wrapper.api.ReportPage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
class AbstractBrokerReportFactoryTest {

    BrokerReportFactory factory = new BrokerReportFactory(Pattern.compile("test.*"));

    @Test
    void canCreate() {
        assertTrue(factory.canCreate("test", null));
        assertFalse(factory.canCreate("1test", null));
        assertTrue(factory.canCreate("test2", null));
        assertFalse(factory.canCreate("t1est", null));
        assertFalse(factory.canCreate("", null));
        assertThrows(NullPointerException.class, () -> factory.canCreate(null, null));
    }

    @Test
    void create() {
        CloseCheckInputStream is = new CloseCheckInputStream(10);
        is.read();

        Optional<?> result = factory.create("file", is);

        assertTrue(result.isPresent());
        assertEquals(9, is.available());
        assertFalse(is.isClosed());
    }

    @Test
    void create_markNotSupported_exception() {
        CloseCheckInputStream is = new CloseCheckInputStream(10) {
            @Override
            public boolean markSupported() {
                return false;
            }
        };
        is.read();

        assertThrows(IllegalArgumentException.class, () -> factory.create("file", is));
        assertEquals(9, is.available());
        assertFalse(is.isClosed());
    }

    @Test
    void create_readExceptionally_optionalEmpty() {
        CloseCheckInputStream is = new CloseCheckInputStream(10) {
            @Override
            public int read(byte[] b, int off, int len) {
                read(); // available() == 8
                throw new RuntimeException();
            }
        };
        is.read();

        Optional<?> result = factory.create("file", is);

        assertFalse(result.isPresent());
        assertEquals(9, is.available());
        assertFalse(is.isClosed());
    }

    @Test
    void create_resetExceptionally_suppressedException() {
        String readError = "read error";
        String resetError = "resetError";
        CloseCheckInputStream is = new CloseCheckInputStream(10) {
            @Override
            public int read(byte[] b, int off, int len) {
                read(); // available() == 8
                throw new RuntimeException(readError);
            }

            @Override
            public synchronized void reset() {
                throw new RuntimeException(resetError);
            }
        };
        is.read();

        Exception e = assertThrows(RuntimeException.class, () -> factory.create("file", is));
        assertEquals("Can't reset input stream", e.getMessage());
        Throwable cause = e.getCause();
        assertSame(resetError, cause.getMessage());
        Throwable[] suppressed = cause.getSuppressed();
        assertEquals(1, suppressed.length);
        assertSame(readError, suppressed[0].getMessage());
        assertEquals(8, is.available()); // yes 8, can't reset
        assertFalse(is.isClosed());
    }

    @Getter
    static class CloseCheckInputStream extends ByteArrayInputStream {
        private boolean closed = false;

        public CloseCheckInputStream(int available) {
            super(new byte[available]);
        }

        @Override
        public void close() {
            closed = true;
        }
    }


    @RequiredArgsConstructor
    static class BrokerReportFactory extends AbstractBrokerReportFactory {
        private final Pattern expectedFileNamePattern;

        @Override
        public boolean canCreate(String fileName, InputStream is) {
            return super.canCreate(expectedFileNamePattern, fileName, is);
        }

        @Override

        public Optional<BrokerReport> create(String fileName, InputStream is) {
            return super.create(fileName, is, this::getBrokerReport);
        }

        @NonNull
        @SneakyThrows
        private BrokerReport getBrokerReport(String file, InputStream is) {
            is.readAllBytes();
            is.close();
            return new BrokerReport() {

                @Override
                public void close() {

                }

                @Override
                @SuppressWarnings("ReturnOfNull")
                public ReportPage getReportPage() {
                    return null;
                }
            };
        }

        @Override
        @SuppressWarnings("ReturnOfNull")
        public String getBrokerName() {
            return null;
        }
    }
}