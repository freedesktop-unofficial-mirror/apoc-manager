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

package com.sun.apoc.manager.profiles;

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.entities.Leaf;
import com.sun.apoc.spi.entities.Role;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.web.ui.model.CCActionTableModelInterface;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class AssignedAboveTableModel extends ProfileModel {
    
    public static final String CHILD_ASSIGNEDTO_TEXT    = "AssignedToText";
    public static final String CHILD_ASSIGNEDTO_COLUMN  = "AssignedToColumn";
    
    public AssignedAboveTableModel() {
        super();
    }
    
    public void setRequestContext(RequestContext requestContext) {
        super.setRequestContext(requestContext);
        
        setActionValue(CHILD_NAME_COLUMN,       "APOC.navigation.name");
        setActionValue(CHILD_PRIO_COLUMN,       "APOC.pool.priority");
        setActionValue(CHILD_ASSIGNEDTO_COLUMN, "Assigned To");

        setPrimarySortName(CHILD_PRIO_TEXT); 
        setPrimarySortOrder(CCActionTableModelInterface.DESCENDING);
    }
    
    public void retrieve(Entity entity) throws ModelControlException {
        clear();
        if (entity==null) {
            return;
        }
        
        try {
            int         nPrio   = 1;
            LinkedList  parents = new LinkedList();
            
            if (entity instanceof Leaf) {
                Iterator memberships = ((Leaf) entity).getMemberships();
                while (memberships.hasNext()) {
                    Role role = (Role) memberships.next(); 
                    parents.add(0,role);
                }
            }
            
            while ((entity=entity.getParent())!=null) {
                parents.add(0,entity);
            }
            
            for (int nParentRunner=0; nParentRunner<parents.size(); nParentRunner++) {
                
                Iterator assignedProfiles = ((Entity)parents.get(nParentRunner)).getAssignedProfiles();
                
                while (assignedProfiles.hasNext()) {
                    Profile profile = (Profile) assignedProfiles.next();
                    
                    appendRow();
                    
                    setValue(CHILD_NAME_TEXT,       profile.getDisplayName());
                    setValue(CHILD_NAME_HREF,       profile.getId());
                    setValue(CHILD_PRIO_TEXT,       new Integer(nPrio++));
                    setValue(CHILD_ASSIGNEDTO_TEXT, Toolbox2.getParentagePath((Entity)parents.get(nParentRunner), false, true, "/"));
                }
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
}
