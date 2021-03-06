/*
 * Reference ETL Parser for Java
 * Copyright (c) 2000-2013 Constantine A Plotnikov
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.sf.etl.parsers;

import java.util.Objects;

/**
 * The information about grammar.
 */
public final class GrammarInfo {
    /**
     * The URI where grammar was loaded.
     */
    private final String uri;
    /**
     * The qualified name of the grammar.
     */
    private final String name;
    /**
     * The version of the grammar.
     */
    private final String version;

    /**
     * The constructor.
     *
     * @param uri     the URI where grammar were located
     * @param name    the name of the grammar
     * @param version the version of the grammar
     */
    public GrammarInfo(final String uri, final String name, final String version) {
        this.uri = uri;
        this.name = name;
        this.version = version;
    }

    /**
     * @return the grammar URI
     */
    public String uri() {
        return uri;
    }

    /**
     * @return the grammar name
     */
    public String name() {
        return name;
    }

    /**
     * @return the grammar version
     */
    public String version() {
        return version;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GrammarInfo that = (GrammarInfo) o;
        return Objects.equals(uri, that.uri) && Objects.equals(name, that.name)
                && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, name, version);
    }

    @Override
    public String toString() {
        return "GrammarInfo{uri=" + uri + ", name=" + name + ", version=" + version + '}';
    }
}
