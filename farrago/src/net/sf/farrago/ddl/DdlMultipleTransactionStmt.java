/*
// $Id$
// Farrago is an extensible data management system.
// Copyright (C) 2008-2008 The Eigenbase Project
// Copyright (C) 2008-2008 Disruptive Tech
// Copyright (C) 2008-2008 LucidEra, Inc.
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
package net.sf.farrago.ddl;

import net.sf.farrago.catalog.FarragoReposTxnContext;
import net.sf.farrago.session.*;

/**
 * DdlMultipleTransactionStmt represents a DdlStmt that requires its work to
 * divided among multiple repository transactions to avoid holding the 
 * repository transaction lock for excessive periods of time (and thereby
 * blocking other statements).
 *
 * @author Stephan Zuercher
 * @version $Id$
 */
public interface DdlMultipleTransactionStmt extends FarragoSessionDdlStmt
{
    /**
     * Provides access to the repository in preparation for the execution of 
     * DdlStmt.  This method is invoked within the context the original 
     * repository transaction for the DDL statement.  Whether that transaction
     * is a write transaction depends on the DDL statement being executed.
     *
     * @param ddlValidator DDL validator for this statement
     * @see net.sf.farrago.catalog.FarragoReposTxnContext
     */
    void prepForExecuteUnlocked(
        FarragoSessionDdlValidator ddlValidator,
        FarragoSession session);

    /**
     * Executes long-running DDL actions. This method is invoked outside the
     * context of any repository transaction.
     *
     * @param ddlValidator DDL validator for this statement
     * @param session reentrant Farrago session which may be used to execute
     *                DML statements
     */
    void executeUnlocked(
        FarragoSessionDdlValidator ddlValidator,
        FarragoSession session);

    /**
     * Checks whether the 
     * {@link #cleanupAfterExecuteUnlocked(
     *      FarragoSessionDdlValidator, FarragoSession)}
     * method requires a repository write transaction.
     * 
     * @return true if a write txn must be started before cleanup, false if
     *         a read txn is sufficient
     */
    boolean cleanupRequiresWriteTxn();
    
    /**
     * Provides access to the repository after execution of the DDL.  
     * Typically implementations of this method modify the repository to store
     * the results of
     * {@link #executeUnlocked(FarragoSessionDdlValidator, FarragoSession)}.
     * This method is invoked in a 
     * {@link FarragoReposTxnContext#beginLockedTxn(boolean) locked} repository
     * transaction.  The method {@link #cleanupRequiresWriteTxn()} controls
     * whether the transaction read-only or not.  Ths method may access
     * and/or modify repository objects loaded in a previous transaction so
     * long as it is guaranteed (for instance by "table-in-use" semantics)
     * that they have not been modified by another statement.
     *
     * @param ddlValidator DDL validator for this statement
     * @param session reentrant Farrago session which may be used to execute
     *                DML statements
     */
    void cleanupAfterExecuteUnlocked(
        FarragoSessionDdlValidator ddlValidator,
        FarragoSession session);
}

// End DdlMultipleTransactionStmt.java