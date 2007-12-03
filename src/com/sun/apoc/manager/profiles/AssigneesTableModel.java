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

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.web.ui.model.CCActionTableModel;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.http.HttpSession;

public class AssigneesTableModel extends CCActionTableModel {
    
    public static final String NAME      = "OrganizationName";
    public static final String LOCATION  = "LocationValue";
    public static final String ENTITY    = "Entity";
    
    private ProfileWindowModel mEditorModel = null;
    
    public AssigneesTableModel() {
        super(RequestManager.getRequestContext().getServletContext(),
        "/jsp/profiles/AssigneesTable.xml");
        setActionValue("LocationColumn", "APOC.search.path");
        setActionValue("NameColumn", "APOC.profilewnd.assignees.nameColumn");
        try {
            update();
        } catch (ModelControlException mce) {
            throw new RuntimeException(mce);
        }
    }
    
    public void update() throws ModelControlException {
        Profile profile = getEditorModel().getProfile();
        if (profile != null) {
            try {
                getRowList().clear();
                // Need to ensure that the entity list is always in the same
                // order, therefore sort them by their IDs before adding to model
                Iterator it = sortEntities(profile.getAssignedEntities());
                while(it.hasNext()) {
                    appendRow();
                    Entity entity = (Entity) it.next();
                    setValue(NAME, Toolbox2.getDisplayName(entity));
                    setValue(LOCATION, Toolbox2.getParentagePath(entity, false, false, "/"));
                    setValue(ENTITY, entity);
                }
            } catch (SPIException se) {
                throw new ModelControlException(se);
            }
        }
    }
    
    public void assign(String sEntityId, String sEntityType) throws ModelControlException {
        try {
            PolicyManager   policyManager   = Toolbox2.getPolicyManager();
            Entity      entity          = policyManager.getEntity(sEntityType, sEntityId);
            Profile     profile         = getEditorModel().getProfile();
            entity.assignProfile(profile);
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    public void unassign() throws ModelControlException {
        try {
            int    currentLocation = getLocation();
            
            for (int nModelRunner = 0; nModelRunner < getNumRows(); nModelRunner++) {
                if (isRowSelected(nModelRunner)) {
                    setLocation(nModelRunner);
                    HttpSession         session = RequestManager.getSession();
                    ProfileWindowModel  model   = (ProfileWindowModel) RequestManager.getRequestContext().getModelManager().getModel(ProfileWindowModel.class);
                    Profile             profile = model.getProfile();
                    Entity              entity  = (Entity) getValue(ENTITY);
                    entity.unassignProfile(profile);
                    setRowSelected(false);
                }
            }
            
            setLocation(currentLocation);
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    protected ProfileWindowModel getEditorModel() {
        if (mEditorModel == null) {
            // TODO: Retrieving the request context via the request manager is
            // not optimal. It would be better to store the request context as
            // part of the AssigneesTableModel initialization
            mEditorModel = (ProfileWindowModel) RequestManager.getRequestContext().getModelManager().getModel(ProfileWindowModel.class);
        }
        return mEditorModel;
    }
    
    private Iterator sortEntities(Iterator it) {
        ArrayList sortedIds = new ArrayList();
        ArrayList sortEntities = new ArrayList();
        while(it.hasNext()) {
            Entity entity = (Entity)it.next();
            String id = entity.getId();
            if (sortedIds.size() == 0) {
                sortedIds.add(id);
                sortEntities.add(entity);
                continue;
            }
            for(int i = 0; i < sortedIds.size(); i++) {
                String sortedId = (String)sortedIds.get(i);
                if(id.compareToIgnoreCase(sortedId) <= 0) {
                    sortedIds.add(i, id);
                    sortEntities.add(i, entity);
                    break;
                } else if(i == (sortedIds.size() - 1)) {
                    sortedIds.add(id);
                    sortEntities.add(entity);
                    break;
                }
            }
        }
        return sortEntities.iterator();
    }
}

