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

import net.sf.etl.parsers.SourceLocation;
import net.sf.etl.parsers.event.grammar.TermParserContext;

import java.util.HashMap;

/**
 * The map choice action
 */
public abstract class MapChoiceAction<T> extends Action {
    /**
     * The chosen alternatives
     */
    public final HashMap<T, Action> next = new HashMap<T, Action>();
    /**
     * The action performed if none of the alternatives matched
     */
    public Action fallback;

    /**
     * The constructor
     *
     * @param source the source location in the grammar that caused this node creation
     */
    public MapChoiceAction(SourceLocation source) {
        super(source);
    }

    @Override
    public void parseMore(TermParserContext context, ActionState state) {
        Action action = next.get(key(context, state));
        action = action == null ? fallback : action;
        state.nextAction(action);
    }

    /**
     * Get key for the choice
     *
     * @param context the context
     * @param state   the state to use
     * @return the current key
     */
    protected abstract T key(TermParserContext context, ActionState state);
}
