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

package net.sf.etl.parsers.resource;

import java.io.Serializable;

/**
 * Resource usage.
 */
public final class ResourceUsage implements Serializable {
    /**
     * UID.
     */
    private static final long serialVersionUID = 2414028213409213686L;
    /**
     * The reference for this usage.
     */
    private final ResourceReference reference;
    /**
     * The used resource.
     */
    private final ResourceDescriptor descriptor;
    /**
     * The resource role.
     */
    private final String role;

    /**
     * The constructor.
     *
     * @param reference  the reference
     * @param descriptor the descriptor
     * @param role       the role
     */
    public ResourceUsage(final ResourceReference reference, final ResourceDescriptor descriptor, final String role) {
        this.reference = reference;
        this.descriptor = descriptor;
        this.role = role;
    }

    /**
     * @return the original reference
     */
    public ResourceReference getReference() {
        return reference;
    }

    /**
     * @return the descriptor
     */
    public ResourceDescriptor getDescriptor() {
        return descriptor;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    @Override
    public boolean equals(final Object o) {
        //CHECKSTYLE:OFF
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ResourceUsage that = (ResourceUsage) o;

        if (descriptor != null ? !descriptor.equals(that.descriptor) : that.descriptor != null) {
            return false;
        }
        if (reference != null ? !reference.equals(that.reference) : that.reference != null) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (role != null ? !role.equals(that.role) : that.role != null) {
            return false;
        }

        return true;
        //CHECKSTYLE:ON
    }

    @Override
    public int hashCode() {
        //CHECKSTYLE:OFF
        int result = reference != null ? reference.hashCode() : 0;
        result = 31 * result + (descriptor != null ? descriptor.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
        //CHECKSTYLE:ON
    }
}
