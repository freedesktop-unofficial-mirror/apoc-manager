/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either
 * the GNU General Public License Version 2 only ("GPL") or
 * the Common Development and Distribution License("CDDL")
 * (collectively, the "License"). You may not use this file
 * except in compliance with the License. You can obtain a copy
 * of the License at www.sun.com/CDDL or at COPYRIGHT. See the
 * License for the specific language governing permissions and
 * limitations under the License. When distributing the software,
 * include this License Header Notice in each file and include
 * the License file at /legal/license.txt. If applicable, add the
 * following below the License Header, with the fields enclosed
 * by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by
 * only the CDDL or only the GPL Version 2, indicate your
 * decision by adding "[Contributor] elects to include this
 * software in this distribution under the [CDDL or GPL
 * Version 2] license." If you don't indicate a single choice
 * of license, a recipient has the option to distribute your
 * version of this file under either the CDDL, the GPL Version
 * 2 or to extend the choice of license to its licensees as
 * provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the
 * option applies only if the new code is made subject to such
 * option by the copyright holder.
 */

package com.sun.apoc.manager.entities;

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestParticipant;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.entities.Role;
import com.sun.apoc.spi.entities.User;

import com.sun.web.ui.model.CCActionTableModel;
import java.util.Iterator;
public class MemberTableModel extends CCActionTableModel implements RequestParticipant {
    //
    public static final String CHILD_NAME_COLUMN = "NameColumn";
    public static final String CHILD_NAME_TEXT   = "NameText";
    public static final String CHILD_NAME_HREF   = "NameHref";
    public static final String CHILD_PATH_COLUMN = "PathColumn";
    public static final String CHILD_PATH_TEXT   = "PathText";
    public static final int    RETRIEVE_ROLES    = 0;
    public static final int    RETRIEVE_USERS    = 1;
    
    public MemberTableModel() {
        super();
    }
    
    public void setRequestContext(RequestContext requestContext) {
        setActionValue(CHILD_NAME_COLUMN, "APOC.navigation.name");
        setActionValue(CHILD_PATH_COLUMN, "APOC.ca.path");
        setMaxRows(10);
    }
    
    public void retrieve() throws ModelControlException {
        clear();
        
        Iterator resultIterator = null;
        
        try {
            Entity entity = Toolbox2.getSelectedEntity();
            
            if (entity instanceof User) {
                resultIterator = ((User) entity).getMemberships();
                setTitle("APOC.roles.title");
                setSummary("APOC.roles.tableSummary");
                setEmpty("APOC.policies.set.table.empty");
            } else if (entity instanceof Role) {
                resultIterator = ((Role) entity).getMembers();
                setTitle("APOC.users.title");
                setSummary("APOC.users.tableSummary");
                setEmpty("APOC.policies.set.table.empty");
            }
            
            while (resultIterator!=null && resultIterator.hasNext()) {
                appendRow();
                
                entity = (Entity) resultIterator.next();
                
                String sDisplayName = entity.getDisplayName(Toolbox2.getLocale());
                
                if (entity instanceof User) {
                    sDisplayName = sDisplayName + " [" + ((User)entity).getUserId() + "]";
                }
                
                // Name column: Entity name and query param(s)
                setValue(CHILD_NAME_TEXT, sDisplayName);
                setValue(CHILD_NAME_HREF, entity.getId());
                
                // Path column(optional): Path
                setValue(CHILD_PATH_TEXT, Toolbox2.getParentagePath(entity, true, false, "/"));
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
}
