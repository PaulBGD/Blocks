/*
 * COPYRIGHT AND PERMISSION NOTICE
 *
 * Copyright (c) 2014, PaulBGD, <paul@paulbgd.me>.
 *
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software for any purpose
 * with or without fee is hereby granted, provided that the above copyright
 * notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of a copyright holder shall not
 * be used in advertising or otherwise to promote the sale, use or other dealings
 * in this Software without prior written authorization of the copyright holder.
 */

package org.jnbt;

import java.util.Arrays;

//@formatter:off

//@formatter:on

/**
 * The <code>TAG_Byte_Array</code> tag.
 *
 * @author Jocopa3
 */
public final class IntArrayTag extends Tag {

    /**
     * The value.
     */
    private final int[] value;

    /**
     * Creates the tag.
     *
     * @param name  The name.
     * @param value The value.
     */
    public IntArrayTag(final String name, final int[] value) {

        super(name);
        this.value = value;
    }

    @Override
    public int[] getValue() {

        return value;
    }

    @Override
    public String toString() {

        final StringBuilder integers = new StringBuilder();
        for (final int b : value) {
            integers.append(b).append(" ");
        }
        final String name = getName();
        String append = "";
        if ((name != null) && !name.equals("")) {
            append = "(\"" + getName() + "\")";
        }
        return "TAG_Int_Array" + append + ": " + integers.toString();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + Arrays.hashCode(value);
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof IntArrayTag)) {
            return false;
        }
        final IntArrayTag other = (IntArrayTag) obj;
        if (!Arrays.equals(value, other.value)) {
            return false;
        }
        return true;
    }

}
