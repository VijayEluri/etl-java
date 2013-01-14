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

package net.sf.etl.parsers.event.impl.term.action;

import net.sf.etl.parsers.PhraseToken;
import net.sf.etl.parsers.SourceLocation;
import net.sf.etl.parsers.TokenKey;
import net.sf.etl.parsers.event.grammar.TermParserContext;

/**
 * The choice based on token kind
 */
public class TokenKeyChoiceAction extends MapChoiceAction<TokenKey> {
    /**
     * The constructor
     *
     * @param source the source location in the grammar that caused this node creation
     */
    public TokenKeyChoiceAction(SourceLocation source) {
        super(source);
    }

    @Override
    protected TokenKey key(TermParserContext context, ActionState state) {
        PhraseToken t = context.current();
        return t.hasToken() ? t.token().key() : null;
    }
}
