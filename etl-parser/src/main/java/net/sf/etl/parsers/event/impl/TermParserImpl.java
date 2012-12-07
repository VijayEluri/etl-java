/*
 * Reference ETL Parser for Java
 * Copyright (c) 2000-2012 Constantine A Plotnikov
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

package net.sf.etl.parsers.event.impl;

import net.sf.etl.parsers.DefinitionContext;
import net.sf.etl.parsers.ParserException;
import net.sf.etl.parsers.PhraseToken;
import net.sf.etl.parsers.TermToken;
import net.sf.etl.parsers.event.Cell;
import net.sf.etl.parsers.event.ParserState;
import net.sf.etl.parsers.event.TermParser;
import net.sf.etl.parsers.event.grammar.*;
import net.sf.etl.parsers.event.impl.term.SourceStateFactory;
import net.sf.etl.parsers.resource.ResolvedObject;
import net.sf.etl.parsers.resource.ResourceRequest;

import java.util.ArrayList;

/**
 * Core implementation of term parser that delegates to other term parsers.
 */
public class TermParserImpl implements TermParser {
    /**
     * The compiled grammar
     */
    private CompiledGrammar grammar;
    /**
     * The system id of the source
     */
    private String systemId;
    /**
     * The initial definition context
     */
    private DefinitionContext initialContext;
    /**
     * The term parser context
     */
    private final TermParserContext context = new TermParserContextImpl();
    /**
     * Current token cell (set by parse method)
     */
    private Cell<PhraseToken> tokenCell;
    /**
     * The token queue
     */
    private final MarkedQueue<TermToken> queue = new MarkedQueue<TermToken>();
    /**
     * If true, the grammar is in script mode
     */
    private boolean scriptMode;
    /**
     * The state stack
     */
    private TermParserState stateStack;
    /**
     * If true, advance is needed
     */
    private boolean advanceNeeded;
    /**
     * Stack for soft ends
     */
    private final ArrayList<Integer> softEndStack = new ArrayList<Integer>();
    /**
     * The count for disabled soft ends
     */
    private int disabledSoftEndCount = 0;

    @Override
    public void forceGrammar(CompiledGrammar grammar, boolean scriptMode) {
        this.scriptMode = scriptMode;
        if (this.grammar != null) {
            throw new IllegalStateException("The grammar is already provided");
        }
        this.grammar = grammar;
    }

    @Override
    public boolean isGrammarDetermined() {
        return grammar != null;
    }

    @Override
    public CompiledGrammar grammar() {
        return grammar;
    }

    @Override
    public DefinitionContext initialContext() {
        return initialContext != null ? initialContext : grammar == null ? null : grammar().getDefaultContext();
    }

    @Override
    public void start(String systemId) {
        this.systemId = systemId;
        this.stateStack = SourceStateFactory.INSTANCE.start(context, null);
    }

    @Override
    public ResourceRequest grammarRequest() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void provideGrammar(ResolvedObject<CompiledGrammar> grammar) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TermToken read() {
        if (queue.hasMark() || queue.isEmpty()) {
            throw new ParserException("Unable to get element");
        }
        return queue.get();
    }

    @Override
    public ParserState parse(Cell<PhraseToken> token) {
        tokenCell = token;
        try {
            while (true) {
                if (queue.hasElement()) {
                    return ParserState.OUTPUT_AVAILABLE;
                }
                if (stateStack == null) {
                    return ParserState.EOF;
                }
                if (token.isEmpty()) {
                    return ParserState.INPUT_NEEDED;
                }
                stateStack.parseMore();
            }
        } finally {
            tokenCell = null;
        }
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

    private class TermParserContextImpl implements TermParserContext {

        @Override
        public boolean isScriptMode() {
            return scriptMode;
        }

        @Override
        public PhraseToken current() {
            ensureTokenCellNonEmpty();
            return tokenCell.peek();
        }

        private void ensureTokenCellNonEmpty() {
            if (tokenCell == null || tokenCell.isEmpty()) {
                throw new IllegalStateException("The token cell is empty");
            }
        }

        @Override
        public void consumePhraseToken() {
            ensureTokenCellNonEmpty();
            tokenCell.take();
            advanceNeeded = true;
        }

        @Override
        public boolean produce(TermToken token) {
            queue.append(token);
            return queue.hasMark();
        }

        @Override
        public boolean produceAfterMark(TermToken token) {
            queue.insertAtMark(token);
            return queue.hasMark();
        }

        @Override
        public void pushMark() {
            queue.pushMark();
        }

        @Override
        public void popMark() {
            queue.popMark();
        }

        @Override
        public void pushKeywordContext(KeywordContext context) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public Integer classify() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void popKeywordContext(KeywordContext context) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void call(TermParserStateFactory stateFactory) {
            stateStack = stateFactory.start(this, stateStack);
        }

        @Override
        public void exit(TermParserState state) {
            if (state != stateStack) {
                throw new IllegalArgumentException("Exiting wrong state");
            }
            stateStack = state.getPreviousState();
        }

        @Override
        public void advanced() {
            advanceNeeded = false;
        }

        @Override
        public boolean isAdvanceNeeded() {
            return advanceNeeded;
        }

        @Override
        public boolean isMoreTokensNeeded() {
            return queue.hasMark();
        }

        @Override
        public boolean canSoftEndStatement() {
            return disabledSoftEndCount == 0;
        }

        @Override
        public void startSoftEndContext() {
            softEndStack.add(disabledSoftEndCount);
            disabledSoftEndCount = 0;
        }

        @Override
        public void disableSoftEnd() {
            disabledSoftEndCount++;
        }

        @Override
        public boolean enableSoftEnd() {
            return (--disabledSoftEndCount) == 0;
        }

        @Override
        public void endSoftEndContext() {
            assert disabledSoftEndCount == 0 : "Disabled soft end count should be zero at the end of the context" +
                    disabledSoftEndCount;
            disabledSoftEndCount = softEndStack.remove(softEndStack.size() - 1);
        }

        @Override
        public TermParser parser() {
            return TermParserImpl.this;
        }
    }

    /**
     * This class represents a queue of tokens that have a possibility of
     * position and inserting new tokens just after mark. The functionality is
     * separated into own class just for convenience.
     */
    private final static class MarkedQueue<T> {
        // NOTE POST 0.2: introduce single item optimization.
        /**
         * A stack of marks
         */
        private final ArrayList<Link<T>> markStack = new ArrayList<Link<T>>();
        /**
         * amount of committed marks
         */
        private int committedMarks;

        /**
         * the first link or null if queue is empty
         */
        private Link<T> first;

        /**
         * the last link or null if queue is empty.
         */
        private Link<T> last;

        /**
         * Create new mark at the end of queue
         */
        void pushMark() {
            markStack.add(last);
        }

        /**
         * commit mark.
         *
         * @return true if some tokens become available and control should be
         *         returned to the parser
         */
        public boolean commitMark() {
            if (committedMarks == markStack.size() - 1) {
                committedMarks++;
                return first != null;
            }
            return false;
        }

        /**
         * Pop the mark
         *
         * @return true if there are no more marks and queue is not empty
         */
        boolean popMark() {
            assert markStack.size() > 0 : "[BUG] Mark stack is empty";
            final int size = markStack.size();
            markStack.remove(size - 1);
            if (size == committedMarks) {
                committedMarks--;
            }
            return !hasMark() && first != null;
        }

        /**
         * @return true if there is at least one mark on the stack
         */
        boolean hasMark() {
            return markStack.size() > committedMarks;
        }

        /**
         * Insert object after mark
         *
         * @param value a value to insert
         */
        void insertAtMark(T value) {
            assert hasMark() : "[BUG] Mark stack is empty";
            final Link<T> mark = peekMark();
            final Link<T> l = new Link<T>(value);
            l.previous = mark;
            if (mark == null) {
                l.next = first;
                first = l;
            } else {
                l.next = mark.next;
                mark.next = l;
            }
            if (l.next == null) {
                last = l;
            } else {
                l.next.previous = l;
            }
        }

        /**
         * @return a current mark
         */
        private Link<T> peekMark() {
            assert hasMark() : "[BUG] Mark stack is empty";
            return markStack.get(markStack.size() - 1);
        }

        /**
         * Append value at end of the queue
         *
         * @param value a value
         */
        void append(T value) {
            final Link<T> l = new Link<T>(value);
            if (last == null) {
                first = last = l;
            } else {
                last.next = l;
                l.previous = last;
                last = l;
            }
        }

        /**
         * @return peek object after mark or null if there are no objects after
         *         mark.
         */
        T peekObjectAfterMark() {
            final Link<T> mark = peekMark();
            final Link<T> afterMark = (mark == null ? first : mark.next);
            return afterMark == null ? null : afterMark.value;
        }


        boolean hasElement() {
            return !hasMark() && !isEmpty();
        }

        /**
         * Get and remove item from queue.
         *
         * @return first item of queue or null.
         */
        T get() {
            assert !hasMark() : "[BUG]Clients are not supposed to poll "
                    + "the queue while marks are active.";
            if (first == null) {
                return null;
            } else {
                final T rc = first.value;
                if (first.next == null) {
                    last = first = null;
                } else {
                    first = first.next;
                    first.previous = null;
                }
                return rc;
            }
        }

        /**
         * @return true if the queue is empty
         */
        public boolean isEmpty() {
            return first == null;
        }

        /**
         * Queue link
         */
        private final static class Link<T> {
            /**
             * next link
             */
            private Link<T> next;

            /**
             * previous link
             */
            private Link<T> previous;

            /**
             * value
             */
            private final T value;

            /**
             * A constructor
             *
             * @param value a value that is held by link
             */
            public Link(T value) {
                if (value == null) {
                    // This is an artificial limitation. However get() interface
                    // should be changed to lift it.
                    throw new IllegalArgumentException("Value cannot be null");
                }
                this.value = value;
            }
        }

        /**
         * Insert value before mark. This is a dirty hack that is required in
         * the precisely one situation: when first segment of the source is not
         * a doctype instruction. So there is exactly one mark on the stack and
         * it is null.
         *
         * @param value a value to insert.
         */
        void insertBeforeMark(T value) {
            if (markStack.size() != 1 || peekMark() != null) {
                throw new RuntimeException("[BUG]The method is used in "
                        + "unintended way: " + markStack);
            }
            final Link<T> link = new Link<T>(value);
            markStack.set(0, link);
            link.next = first;
            if (first != null) {
                first.previous = link;
            } else {
                last = link;
            }
            first = link;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            final StringBuilder rc = new StringBuilder();
            rc.append('[');
            Link c = first;
            while (c != null) {
                if (c.previous != null) {
                    rc.append(", ");
                }
                rc.append(c.value);
                c = c.next;
            }
            rc.append(']');
            return rc.toString();
        }
    }
}