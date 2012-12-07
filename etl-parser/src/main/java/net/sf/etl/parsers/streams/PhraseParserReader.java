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

package net.sf.etl.parsers.streams;

import net.sf.etl.parsers.ParserException;
import net.sf.etl.parsers.PhraseToken;
import net.sf.etl.parsers.Token;
import net.sf.etl.parsers.event.Cell;
import net.sf.etl.parsers.event.ParserState;
import net.sf.etl.parsers.event.PhraseParser;
import net.sf.etl.parsers.event.impl.PhraseParserImpl;

/**
 * The reader for the phrase parser
 */
public class PhraseParserReader extends AbstractReaderImpl<PhraseToken> {
    /**
     * The underlying lexer
     */
    final Cell<Token> tokenCell = new Cell<Token>();
    /**
     * The lexer
     */
    final LexerReader lexer;
    /**
     * The phrase parser
     */
    final PhraseParser phraseParser = new PhraseParserImpl();

    /**
     * The constructor from lexer
     *
     * @param lexer the base lexer
     */
    public PhraseParserReader(LexerReader lexer) {
        this.lexer = lexer;
        this.phraseParser.start(lexer.getSystemId());
    }

    @Override
    protected boolean doAdvance() {
        while (true) {
            ParserState state = phraseParser.parse(tokenCell);
            switch (state) {
                case INPUT_NEEDED:
                    if (lexer.advance()) {
                        tokenCell.put(lexer.current());
                    } else {
                        throw new ParserException("Advancing should be possible before EOF: " + lexer);
                    }
                    break;
                case EOF:
                    return false;
                case OUTPUT_AVAILABLE:
                    current = phraseParser.read();
                    return true;
                default:
                    throw new ParserException("Invalid state from the phrase parser: " + state);
            }
        }
    }

    @Override
    protected void doClose() throws Exception {
        lexer.close();
    }

    @Override
    public String getSystemId() {
        return lexer.getSystemId();
    }
}