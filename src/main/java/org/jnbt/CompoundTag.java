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

//@formatter:off

//@formatter:on

import java.util.Collections;
import java.util.Map;

/**
 * The <code>TAG_Compound</code> tag.
 *
 * @author Graham Edgecombe
 */
public final class CompoundTag extends Tag {

    /**
     * The value.
     */
    private final Map<String, Tag> value;

    /**
     * Creates the tag.
     *
     * @param name  The name.
     * @param value The value.
     */
    public CompoundTag(final String name, final Map<String, Tag> value) {

        super(name);
        this.value = Collections.unmodifiableMap(value);
    }

    @Override
    public Map<String, Tag> getValue() {

        return value;
    }

    @Override
    public String toString() {

        final String name = getName();
        String append = "";
        if ((name != null) && !name.equals("")) {
            append = "(\"" + getName() + "\")";
        }
        final StringBuilder bldr = new StringBuilder();
        bldr.append("TAG_Compound" + append + ": " + value.size()
                + " entries\r\n{\r\n");
        for (final Map.Entry<String, Tag> entry : value.entrySet()) {
            bldr.append("   "
                    + entry.getValue().toString().replaceAll("\r\n", "\r\n   ")
                    + "\r\n");
        }
        bldr.append("}");
        return bldr.toString();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = (prime * result) + ((value == null) ? 0 : value.hashCode());
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
        if (!(obj instanceof CompoundTag)) {
            return false;
        }
        final CompoundTag other = (CompoundTag) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

}
