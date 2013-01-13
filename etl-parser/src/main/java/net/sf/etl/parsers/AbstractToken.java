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

/**
 * Base class for tokens used by parsers.
 *
 * @author const
 */
public abstract class AbstractToken {
    /**
     * a end position of token
     */
    private final TextPos end;
    /**
     * a lexical error info
     */
    private final ErrorInfo errorInfo;
    /**
     * a start position of token
     */
    private final TextPos start;

    /**
     * The generic constructor for token
     *
     * @param start     start position
     * @param end       end position
     * @param errorInfo error information associated with token
     */
    protected AbstractToken(TextPos start, TextPos end, ErrorInfo errorInfo) {
        if (start == null) {
            throw new NullPointerException("Start must not be null");
        }
        if (end == null) {
            throw new NullPointerException("Start must not be null");
        }
        this.start = start;
        this.end = end;
        this.errorInfo = errorInfo;
    }

    /**
     * The constructor for non erroneous token
     *
     * @param start start position
     * @param end   end position
     */
    protected AbstractToken(TextPos start, TextPos end) {
        this(start, end, null);
    }


    /**
     * @return a position of next character right after token
     */
    public TextPos end() {
        return end;
    }

    /**
     * @return ErrorInfo associated with token if token is error token
     */
    public ErrorInfo errorInfo() {
        return errorInfo;
    }

    /**
     * @return start position of token
     */
    public TextPos start() {
        return start;
    }

    /**
     * @return true if the token has associated errors
     */
    public boolean hasErrors() {
        return errorInfo() != null;
    }
}
