/*
// $Id$
// Fennel is a relational database kernel.
// Copyright (C) 2004-2004 John V. Sichi.
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation; either version 2.1
// of the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

#ifndef Fennel_SingleInputExecStream_Included
#define Fennel_SingleInputExecStream_Included

#include "fennel/exec/ExecStream.h"

FENNEL_BEGIN_NAMESPACE

/**
 * SingleInputExecStreamParams defines parameters for SingleInputExecStream.
 */
struct SingleInputExecStreamParams : virtual public ExecStreamParams
{
};
    
/**
 * SingleInputExecStream is an abstract base for all implementations
 * of ExecStream which have exactly one input.  By default
 * no outputs are produced, but derived classes may override.
 *
 * @author John V. Sichi
 * @version $Id$
 */
class SingleInputExecStream : virtual public ExecStream
{
protected:
    SharedExecStreamBufAccessor pInAccessor;
    
public:
    // implement ExecStream
    virtual void setOutputBufAccessors(
        std::vector<SharedExecStreamBufAccessor> const &inAccessors);
    virtual void setInputBufAccessors(
        std::vector<SharedExecStreamBufAccessor> const &outAccessors);
    virtual void prepare(SingleInputExecStreamParams const &params);
    virtual void open(bool restart);
    virtual ExecStreamBufProvision getInputBufProvision() const;
};

FENNEL_END_NAMESPACE

#endif

// End SingleInputExecStream.h