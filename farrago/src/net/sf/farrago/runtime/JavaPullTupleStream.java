/*
// $Id$
// Farrago is an extensible data management system.
// Copyright (C) 2005-2005 The Eigenbase Project
// Copyright (C) 2005-2005 Disruptive Tech
// Copyright (C) 2005-2005 LucidEra, Inc.
// Portions Copyright (C) 2003-2005 John V. Sichi
//
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the Free
// Software Foundation; either version 2 of the License, or (at your option)
// any later version approved by The Eigenbase Project.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package net.sf.farrago.runtime;

import java.nio.*;
import java.util.*;

import org.eigenbase.util.*;

/**
 * JavaPullTupleStream is the counterpart of a Fennel C++ JavaPullSource XO
 * On request from its C++ peer, it gets rows from a Java Iterator, converts them to fennel
 * tuples, and marshals them into a buffer provided by the peer.
 * It runs as a subroutine to the XO, which waits for it to complete.
 *
 * @author John V. Sichi
 * @version $Id$
 */
public class JavaPullTupleStream implements JavaTupleStream
{
    //~ Instance fields -------------------------------------------------------

    private FennelTupleWriter tupleWriter;
    private Iterator iter;
    private Object next;

    //~ Constructors ----------------------------------------------------------

    /**
     * Constructs a new JavaPullTupleStream.
     *
     * @param tupleWriter the FennelTupleWriter to use for marshalling tuples
     * @param iter Iterator producing objects
     */
    public JavaPullTupleStream(
        FennelTupleWriter tupleWriter,
        Iterator iter)
    {
        this.tupleWriter = tupleWriter;
        this.iter = iter;
        next = null;
    }

    //~ Methods ---------------------------------------------------------------

    /**
     * Called from native code.
     *
     * @param byteBuffer target buffer for marshalled tuples; note that this
     *        is allocated from within native code so that cache pages can be
     *        used, and so that data never needs to be copied
     *
     * @return number of bytes written to buffer
     */
    private int fillBuffer(ByteBuffer byteBuffer)
    {
        if (next == null) {
            if (!iter.hasNext()) {
                return 0;
            }
            next = iter.next();
        }
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.clear();
        for (;;) {
            if (!tupleWriter.marshalTuple(byteBuffer, next)) {
                break;
            }
            if (!iter.hasNext()) {
                next = null;
                break;
            }
            next = iter.next();
        }
        byteBuffer.flip();
        return byteBuffer.limit();
    }

    // implement JavaTupleStream
    public void restart()
    {
        Util.restartIterator(iter);
        next = null;
    }
}


// End JavaPullTupleStream.java
