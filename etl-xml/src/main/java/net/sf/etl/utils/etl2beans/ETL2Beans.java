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
package net.sf.etl.utils.etl2beans;

import net.sf.etl.parsers.TextPos;
import net.sf.etl.parsers.streams.TermParserReader;
import net.sf.etl.parsers.streams.beans.BeansTermParser;
import net.sf.etl.utils.ETL2AST;

import java.beans.DefaultPersistenceDelegate;
import java.beans.XMLEncoder;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This class converts ETL source to serialized JavaBeans
 *
 * @author const
 */
public class ETL2Beans extends ETL2AST {
    /**
     * map from namespace to package
     */
    final HashMap<String, String> packageMap = new HashMap<String, String>();

    @Override
    protected void processContent(OutputStream outStream, TermParserReader p)
            throws Exception {
        final BeansTermParser bp = new BeansTermParser(p, ETL2Beans.class.getClassLoader());
        configureStandardOptions(bp);
        for (final Entry<String, String> e : packageMap.entrySet()) {
            bp.mapNamespaceToPackage(e.getKey(), e.getValue());
        }
        final XMLEncoder en = new XMLEncoder(outStream);
        en.setPersistenceDelegate(TextPos.class,
                new DefaultPersistenceDelegate(new String[]{"line", "column", "offset"}));
        while (bp.hasNext()) {
            en.writeObject(bp.next());
        }
        en.close();
    }

    @Override
    protected int handleCustomOption(String[] args, int i) throws Exception {
        if ("-map".equals(args[i])) {
            final String namespace = args[i + 1];
            final String packageName = args[i + 2];
            i += 2;
            packageMap.put(namespace, packageName);
        } else {
            return super.handleCustomOption(args, i);
        }
        return i;
    }

    /**
     * Application entry point
     *
     * @param args application arguments
     */
    public static void main(String args[]) {
        try {
            new ETL2Beans().start(args);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
