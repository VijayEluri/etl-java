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
package net.sf.etl.parsers.term;

import net.sf.etl.parsers.DefaultTermParserConfiguration;
import net.sf.etl.parsers.StandardGrammars;
import net.sf.etl.parsers.Terms;
import net.sf.etl.parsers.streams.DefaultTermReaderConfiguration;
import net.sf.etl.parsers.streams.TermParserReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Base class for structural term test case
 *
 * @author const
 */
public abstract class TermStructureTestCase {
    /**
     * a logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(TermStructureTestCase.class);

    /**
     * a parser
     */
    protected TermParserReader parser;

    /**
     * Start parsing resource
     *
     * @param resourceName a resource to parse
     */
    protected void startWithResource(String resourceName) {
        final java.net.URL in = this.getClass().getResource(resourceName);
        assertNotNull(in);
        startWithURL(in);
    }

    /**
     * Start parsing with URL
     *
     * @param in an URL to parse
     */
    protected void startWithURL(java.net.URL in) {
        parser = new TermParserReader(in);
        parser.advance();
    }

    /**
     * Start parsing resource with specified reader
     *
     * @param resourceName a resource to parse
     */
    protected void startWithResourceAsReader(String resourceName) {
        final java.net.URL in = this.getClass().getResource(resourceName);
        assertNotNull(in);
        parser = new TermParserReader(in);
        parser.advance();
    }

    /**
     * Start parsing resource with specified reader
     *
     * @param text            a text to parse
     * @param grammarSystemId system id of default grammar
     * @param grammarPublicId public id of default grammar
     * @param defaultContext  a default context with which to start
     */
    protected void startWithStringAndDefaultGrammar(String text,
                                                    String grammarSystemId, String grammarPublicId,
                                                    String defaultContext) {
        parser = new TermParserReader(DefaultTermReaderConfiguration.INSTANCE, new StringReader(text), "none:test");
        parser.setDefaultGrammar(grammarPublicId, grammarSystemId, defaultContext, false);
        assertEquals(parser.getConfiguration().getParserConfiguration().getTabSize(parser.getSystemId()),
                Integer.getInteger(DefaultTermParserConfiguration.ETL_TAB_SIZE_PROPERTY, 8).intValue());
        parser.advance();
    }

    /**
     * End parsing resource
     *
     * @param errorExit true if error exit
     */
    protected void endParsing(boolean errorExit) {
        if (!errorExit) {
            skipIgnorable();
            assertEquals("EOF is expected: " + parser.current(), Terms.EOF,
                    parser.current().kind());
        }
        // if (errorExit) {
        // try {
        // while (parser.current().kind() != Terms.EOF) {
        // LOG.fine("POST ERROR: " + parser);
        // parser.advance();
        // }
        // } catch (Throwable ex) {
        // LOG.LOG(java.util.logging.Level.SEVERE, "error during parsing",
        // ex);
        // }
        // }
        parser.close();
    }

    /**
     * read start object
     *
     * @param ns   an object name
     * @param name an object namespace
     */
    protected void objectStart(String ns, String name) {
        this.skipIgnorable();
        assertEquals("term kind " + parser.current(), Terms.OBJECT_START, parser.current().kind());
        assertEquals("namespace " + parser.current(), ns, parser.current().objectName().namespace());
        assertEquals("name " + parser.current(), name, parser.current().objectName().name());
        parser.advance();
    }

    /**
     * read the end of the object
     *
     * @param ns   an object name
     * @param name an object namespace
     */
    protected void objectEnd(String ns, String name) {
        this.skipIgnorable();
        assertEquals("term kind " + parser.current(), Terms.OBJECT_END, parser.current().kind());
        assertEquals("namespace " + parser.current(), ns, parser.current().objectName().namespace());
        assertEquals("name " + parser.current(), name, parser.current().objectName().name());
        parser.advance();
    }

    /**
     * read start of list property
     *
     * @param name a name of property
     */
    protected void listStart(String name) {
        this.skipIgnorable();
        assertEquals("term kind " + parser.current(), Terms.LIST_PROPERTY_START, parser.current().kind());
        assertEquals("name " + parser.current(), name, parser.current().propertyName().name());
        parser.advance();
    }

    /**
     * read end of list property
     *
     * @param name a name of property
     */
    protected void listEnd(String name) {
        this.skipIgnorable();
        assertEquals("term kind " + parser.current(), Terms.LIST_PROPERTY_END, parser.current().kind());
        assertEquals("name " + parser.current(), name, parser.current().propertyName().name());
        parser.advance();
    }

    /**
     * read start of property
     *
     * @param name a name of property
     */
    protected void propStart(String name) {
        this.skipIgnorable();
        assertEquals("term kind " + parser.current(), Terms.PROPERTY_START, parser.current().kind());
        assertEquals("name " + parser.current(), name, parser.current().propertyName().name());
        parser.advance();
    }

    /**
     * read end of property
     *
     * @param name a name of property
     */
    protected void propEnd(String name) {
        this.skipIgnorable();
        assertEquals("term kind " + parser.current(), Terms.PROPERTY_END, parser.current().kind());
        assertEquals("name " + parser.current(), name, parser.current().propertyName().name());
        parser.advance();
    }

    /**
     * read value
     *
     * @param value an expected value
     */
    protected void value(String value) {
        this.skipIgnorable();
        assertEquals("term kind " + parser.current(), Terms.VALUE, parser.current().kind());
        assertEquals("name " + parser.current(), value, parser.current().token().token().text());
        parser.advance();
    }

    /**
     * Skip ignorable tokens in the stream
     */
    protected void skipIgnorable() {
        while (true) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("processing " + parser.current());
            }
            switch (parser.current().kind()) {
                case OBJECT_START:
                case OBJECT_END:
                case PROPERTY_START:
                case PROPERTY_END:
                case LIST_PROPERTY_START:
                case LIST_PROPERTY_END:
                case VALUE:
                case SYNTAX_ERROR:
                case EOF:
                    return;
                case ATTRIBUTES_START:
                case ATTRIBUTES_END:
                case CONTROL:
                case IGNORABLE:
                case STRUCTURAL:
                case EXPRESSION_START:
                case EXPRESSION_END:
                case MODIFIERS_START:
                case MODIFIERS_END:
                case DOC_COMMENT_START:
                case DOC_COMMENT_END:
                case STATEMENT_START:
                case STATEMENT_END:
                case BLOCK_START:
                case BLOCK_END:
                case GRAMMAR_IS_LOADED:
                    assertFalse("" + parser.current(), parser.current().hasAnyErrors());
                    parser.advance();
                    break;
                default:
                    throw new IllegalStateException("Unknown term kind: " + parser);
            }

        }
    }

    /**
     * Read error of the specified kind
     *
     * @param kind a kind of error
     */
    protected void readError(Terms kind) {
        switch (kind) {
            case SYNTAX_ERROR:
                break;
            default:
                throw new IllegalArgumentException("Unknown error term kind: " + kind);
        }
        this.skipIgnorable();
        assertEquals("term kind " + parser.current(), kind, parser.current().kind());
        parser.advance();
    }

    /**
     * Read doctype
     *
     * @param systemId a system id of the grammar
     * @param context  context name
     */
    protected void readDocType(String systemId, String context) {
        this.objectStart(StandardGrammars.DOCTYPE_NS, "DoctypeDeclaration");
        {
            if (systemId != null) {
                propStart("SystemId");
                value(systemId);
                propEnd("SystemId");
            }
            if (context != null) {
                propStart("Context");
                value(context);
                propEnd("Context");
            }
        }
        this.objectEnd(StandardGrammars.DOCTYPE_NS, "DoctypeDeclaration");
    }

    /**
     * Read doctype
     *
     * @param type     the source type
     * @param systemId the system id of the grammar
     * @param context  the context name
     */
    protected void readDocType(String type, String systemId, String context) {
        this.objectStart(StandardGrammars.DOCTYPE_NS, "DoctypeDeclaration");
        {
            if (type != null) {
                propStart("Type");
                value(type);
                propEnd("Type");
            }
            if (systemId != null) {
                propStart("SystemId");
                value(systemId);
                propEnd("SystemId");
            }
            if (context != null) {
                propStart("Context");
                value(context);
                propEnd("Context");
            }
        }
        this.objectEnd(StandardGrammars.DOCTYPE_NS, "DoctypeDeclaration");
    }


    /**
     * Read entire source and fail if errors are detected
     *
     * @param resource a resource
     */
    protected void readAllWithoutErrors(String resource) {
        startWithResource(resource);
        boolean errorExit = true;
        try {
            final long start = System.currentTimeMillis();
            long grammar = -1;
            int count = 0;
            int countAfterGrammar = 0;
            while (parser.current().kind() != Terms.EOF) {
                count++;
                countAfterGrammar++;
                switch (parser.current().kind()) {
                    case SYNTAX_ERROR:
                        fail("There should be no errors: " + parser.current());
                        break;
                    case GRAMMAR_IS_LOADED:
                        grammar = System.currentTimeMillis();
                        countAfterGrammar = 0;
                    default:
                        break;
                }
                parser.advance();
            }
            final long end = System.currentTimeMillis();
            if (LOG.isDebugEnabled()) {
                LOG.debug("STATISTICS: " + resource + "," + (grammar - start)
                        + "," + (end - start) + "," + count + ","
                        + countAfterGrammar);
            }
            errorExit = false;
        } finally {
            endParsing(errorExit);
        }
    }
}
