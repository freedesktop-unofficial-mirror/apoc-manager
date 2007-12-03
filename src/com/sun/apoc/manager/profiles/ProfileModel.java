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
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.RequestParticipant;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.policies.Policy;
import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.profiles.ProfileRepository;
import com.sun.web.ui.model.CCActionTableModel;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

public abstract class ProfileModel extends CCActionTableModel implements RequestParticipant {
    // Child view names (i.e. display fields).
    public static final String CHILD_COLUMN             = "Col";
    public static final String CHILD_STATIC_TEXT        = "Text";
    public static final String CHILD_NAME_COLUMN        = "NameColumn";
    public static final String CHILD_NAME_HREF          = "NameHref";
    public static final String CHILD_NAME_TEXT          = "NameText";
    public static final String CHILD_PRIO_COLUMN        = "PrioColumn";
    public static final String CHILD_PRIO_TEXT          = "PrioText";
    public static final String CHILD_AUTHOR_COLUMN      = "AuthorColumn";
    public static final String CHILD_AUTHOR_TEXT        = "AuthorText";
    public static final String CHILD_LASTMOD_COLUMN     = "LastModifiedColumn";
    public static final String CHILD_LASTMOD_TEXT       = "LastModifiedText";
    public static final String CHILD_SCOPE_COLUMN       = "ScopeColumn";
    public static final String CHILD_SCOPE_TEXT         = "ScopeText";
    
    //
    protected RequestContext m_requestContext;
    
    public abstract void retrieve(Entity entity) throws ModelControlException;
    
    public ProfileModel() {
        super();
    }
    
    public void setRequestContext(RequestContext requestContext) {
        m_requestContext = requestContext;
        setMaxRows(10);
    }
    
    static public String create(String sName, String sComment, String sLocation, boolean bAssign) throws ModelControlException {
        try {
            PolicyManager       policyManager   = Toolbox2.getPolicyManager();
            Entity          entity          = Toolbox2.getSelectedEntity();
            Entity          writableEntity  = entity;
            Applicability   use             = Applicability.USER;
            
            while (entity!=null) {
                ProfileRepository repository = entity.getProfileRepository();
                if (!repository.isReadOnly()) {
                    writableEntity = entity;
                }
                entity = entity.getParent();
            }
            
            if (writableEntity.getPolicySourceName().equals(EnvironmentConstants.HOST_SOURCE)) {
                use = Applicability.HOST;
            } 
            ProfileRepository   repository  = writableEntity.getProfileRepository();
            Profile             newProfile  = repository.createProfile(sName, use);
            
            newProfile.setComment(sComment);
            
            if (bAssign) {
                Toolbox2.getSelectedEntity().assignProfile(newProfile);
                NavigationModel navigationModel = (NavigationModel) RequestManager.getRequestContext().getModelManager().getModel(NavigationModel.class, "NavTree", true, true);
                navigationModel.setAssignedState(Toolbox2.getSelectedEntity(), true);
            }
            
            return newProfile.getId();
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    public void destroy() throws ModelControlException {
        int currentLocation = getLocation();
        for (int nModelRunner = 0; nModelRunner < getNumRows(); nModelRunner++) {
            if (isRowSelected(nModelRunner)) {
                setLocation(nModelRunner);
                destroy((String) getValue(CHILD_NAME_HREF), Toolbox2.getPolicyManager());
                setRowSelected(false);
            }
        }
        setLocation(currentLocation);
    }
    
    static public void destroy(String sProfile, PolicyManager pmgr) throws ModelControlException {
        try {
            NavigationModel navigationModel = (NavigationModel) RequestManager.getRequestContext().getModelManager().getModel(NavigationModel.class, "NavTree", true, true);
            
            Profile             profile             = pmgr.getProfile(sProfile);
            ProfileRepository   repository          = profile.getProfileRepository();
            Iterator            assignedEntities    = profile.getAssignedEntities();
            
            while (assignedEntities.hasNext()) {
                Entity          entity          = (Entity) assignedEntities.next();
                Iterator        assignedProfiles= entity.getAssignedProfiles();
                assignedProfiles.next();
                navigationModel.setAssignedState(entity, assignedProfiles.hasNext());
                entity.unassignProfile(profile);
            }
            
            repository.destroyProfile(profile);
            
            // close gaps in prios originating from deletions
            // don't do this once at the end as the retrieve() method
            // may have populated this model with profiles stored in
            // different repositories
            Iterator profileIter = repository.getProfiles(profile.getApplicability());
            int nMaxPrio = 1;
            while (profileIter.hasNext()) {
                Profile prioProfile = (Profile) profileIter.next();
                // don't set all prios with brute force to minimize write access
                if (prioProfile.getPriority()!=nMaxPrio) {
                    prioProfile.setPriority(nMaxPrio);
                }
                nMaxPrio++;
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }

    
    static public String copy(String sSourceProfile, String sTargetEntityType, String sTargetEntity, boolean bConsiderAssignments) throws ModelControlException {
        Profile targetProfile = null;
        try {
            PolicyManager           manager             = Toolbox2.getPolicyManager();
            Profile             sourceProfile       = (Profile) manager.getProfile(sSourceProfile);
            ProfileRepository   sourceRepository    = sourceProfile.getProfileRepository();
            Entity              targetEntity        = manager.getEntity(sTargetEntityType, sTargetEntity);
            Iterator            entities            = null;
            
            if (bConsiderAssignments) {
                // Test if new location of the profile does conflict with entities
                // already assigned to that profile.
                // Only entities at and below the storage location (applicable from)
                // of the profile are allowed to be assigned to the profile
                boolean  bRuleBreach= false;
                entities   = sourceProfile.getAssignedEntities();
                while (entities.hasNext()&&!bRuleBreach) {
                    Entity entity = (Entity) entities.next();
                    boolean assignedEntityBelowNewProfileLocation = false;
                    while (entity!=null) {
                        if (entity.equals(targetEntity)) {
                            assignedEntityBelowNewProfileLocation = true;
                            break;
                        }
                        entity = entity.getParent();
                    }
                    bRuleBreach = !assignedEntityBelowNewProfileLocation;
                }
                
                //TPF_TODO: use an exception rather than this empty string
                if (bRuleBreach) {
                    return "";
                }
            }
            
            ProfileRepository targetRepository = targetEntity.getProfileRepository();
            targetProfile = targetRepository.createProfile(sourceProfile.getDisplayName(), sourceProfile.getApplicability());
            targetProfile.setComment(sourceProfile.getComment());
            
            Iterator policies = sourceProfile.getPolicies();
            while (policies.hasNext()) {
                Policy policy = (Policy) policies.next();
                targetProfile.storePolicy(policy);
            }
            
            if (bConsiderAssignments) {
                entities = sourceProfile.getAssignedEntities();
                while (entities.hasNext()) {
                    Entity entity = (Entity) entities.next();
                    entity.assignProfile(targetProfile);
                }
            }
        } catch (Exception e) {
            throw new ModelControlException(e);
        }
        
        return targetProfile.getId();
    }
    
    public void assign() throws ModelControlException {
        try {
            int    currentLocation = getLocation();
            
            for (int nModelRunner = 0; nModelRunner < getNumRows(); nModelRunner++) {
                if (isRowSelected(nModelRunner)) {
                    setLocation(nModelRunner);
                    
                    String  sProfile = (String) getValue(CHILD_NAME_HREF);
                    Profile profile  = Toolbox2.getPolicyManager().getProfile(sProfile);
                    Toolbox2.getSelectedEntity().assignProfile(profile);
                    setRowSelected(false);
                }
            }
            
            setLocation(currentLocation);
            
            NavigationModel navigationModel = (NavigationModel) m_requestContext.getModelManager().getModel(
            NavigationModel.class, "NavTree", true, true);
            
            navigationModel.setAssignedState(Toolbox2.getSelectedEntity(), true);
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    public void unassign() throws ModelControlException {
        try {
            int    currentLocation  = getLocation();
            Entity entity           = Toolbox2.getSelectedEntity();
            
            for (int nModelRunner = 0; nModelRunner < getNumRows(); nModelRunner++) {
                if (isRowSelected(nModelRunner)) {
                    setLocation(nModelRunner);
                    
                    String  sProfile = (String) getValue(CHILD_NAME_HREF);
                    Profile profile  = Toolbox2.getPolicyManager().getProfile(sProfile);
                    entity.unassignProfile(profile);
                    setRowSelected(false);
                }
            }
            
            setLocation(currentLocation);
            
            NavigationModel navigationModel = (NavigationModel) m_requestContext.getModelManager().getModel(
            NavigationModel.class, "NavTree", true, true);
            
            navigationModel.setAssignedState(entity, entity.getAssignedProfiles().hasNext());
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    public void rename(String sNewName) throws ModelControlException {
        try {
            int currentLocation = getLocation();
            
            for (int nModelRunner = 0; nModelRunner < getNumRows(); nModelRunner++) {
                if (isRowSelected(nModelRunner)) {
                    setLocation(nModelRunner);
                    
                    String  sProfile= (String) getValue(CHILD_NAME_HREF);
                    Profile profile = Toolbox2.getPolicyManager().getProfile(sProfile);
                    
                    profile.setDisplayName(sNewName);
                    setRowSelected(false);
                }
            }
            
            setLocation(currentLocation);
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    public void changePriority(int nChange) throws ModelControlException {
        
        if (nChange==0) {
            return;
        }
        
        try {
            String              sProfile        = (String) getValue(CHILD_NAME_HREF);
            Profile             profile         = Toolbox2.getPolicyManager().getProfile(sProfile);
            ProfileRepository   repository      = profile.getProfileRepository();
            int                 nMaxPrio        = 0;
            int                 nCurrentLocation= getLocation();
            Vector              profiles        = new Vector();
            Vector              profilePrios    = new Vector();
            
            for (int nModelRunner = 0; nModelRunner < getNumRows(); nModelRunner++) {
                if (isRowSelected(nModelRunner)) {
                    setLocation(nModelRunner);
                    
                    if (profiles.size()==0) {
                        Iterator profileIter = repository.getProfiles(profile.getApplicability());
                        while (profileIter.hasNext()) {
                            Profile tmpProfile = (Profile) profileIter.next();
                            profiles.add(tmpProfile);
                            profilePrios.add(new Integer(tmpProfile.getPriority()));
                            if (tmpProfile.getPriority()>nMaxPrio) {
                                nMaxPrio=tmpProfile.getPriority();
                            }
                        }
                        
                        if (nChange>0) {
                            if (profiles.indexOf(profile)-nChange<0) {
                                return;
                            }
                        } else {
                            if (profiles.indexOf(profile)-nChange>=getNumRows()) {
                                return;
                            }
                        }
                    }
                    
                    profiles.add(nModelRunner-nChange, profiles.remove(nModelRunner));
                }
            }
            
            // TPF_TODO: can be solved more efficiently with a swap
            for (int nProfileRunner=0; nProfileRunner<profiles.size(); nProfileRunner++) {
                if ( ((Profile)profiles.get(nProfileRunner)).getPriority() != ((Integer)profilePrios.get(nProfileRunner)).intValue() ) {
                    ((Profile)profiles.get(nProfileRunner)).setPriority(++nMaxPrio);
                }
            }
            
            for (int nProfileRunner=0; nProfileRunner<profiles.size(); nProfileRunner++) {
                if ( ((Profile)profiles.get(nProfileRunner)).getPriority() != ((Integer)profilePrios.get(nProfileRunner)).intValue() ) {
                    ((Profile)profiles.get(nProfileRunner)).setPriority(((Integer)profilePrios.get(nProfileRunner)).intValue());
                }
            }
            
            //move selections also
            if (nChange>0) {
                for (int nModelRunner = 0; nModelRunner < getNumRows(); nModelRunner++) {
                    if (isRowSelected(nModelRunner)) {
                        setRowSelected(nModelRunner-nChange, true);
                        setRowSelected(nModelRunner, false);
                    }
                }
            } else {
                for (int nModelRunner = getNumRows(); nModelRunner > 0; nModelRunner--) {
                    if (isRowSelected(nModelRunner)) {
                        setRowSelected(nModelRunner-nChange, true);
                        setRowSelected(nModelRunner, false);
                    }
                }
            }
            
            setLocation(nCurrentLocation);
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    static public void changePriority(LinkedList newProfileSequence) throws ModelControlException {
        // TPF_TODO: can be solved more efficiently with a selective swap
        //           handle prio 0 "local settings" profile
        
        PolicyManager manager = Toolbox2.getPolicyManager();
        
        try {
            int nMaxPrio = 0;
            for (int nProfileRunner=0; nProfileRunner<newProfileSequence.size(); nProfileRunner++) {
                Profile profile = manager.getProfile((String)newProfileSequence.get(nProfileRunner));
                if (profile.getPriority()>nMaxPrio) {
                    nMaxPrio=profile.getPriority();
                }
            }
            for (int nProfileRunner=0; nProfileRunner<newProfileSequence.size(); nProfileRunner++) {
                Profile profile = manager.getProfile((String)newProfileSequence.get(nProfileRunner));
                profile.setPriority(nMaxPrio+nProfileRunner+1);
            }
            for (int nProfileRunner=0; nProfileRunner<newProfileSequence.size(); nProfileRunner++) {
                Profile profile = manager.getProfile((String)newProfileSequence.get(nProfileRunner));
                profile.setPriority(nProfileRunner+1);
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    public void exportGroup(String sProfile, OutputStream stream) throws ModelControlException {
        try {
            Profile             profile     = Toolbox2.getPolicyManager().getProfile(sProfile);
            ProfileRepository   repository  = profile.getProfileRepository();
            
            repository.exportProfile(profile, stream);
            
            for (int nModelRunner = 0; nModelRunner < getNumRows(); nModelRunner++) {
                setRowSelected(nModelRunner, false);
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    static public void importGroup(String sName, Entity entity, Applicability use, boolean bAssign, InputStream stream)
    throws ModelControlException {
        try {
            Entity          writableEntity  = entity;
            
            while (entity!=null) {
                ProfileRepository repository = entity.getProfileRepository();
                if (!repository.isReadOnly()) {
                    writableEntity = entity;
                }
                entity = entity.getParent();
            }
            
           
            ProfileRepository repository = writableEntity.getProfileRepository();
            
            repository.importProfile(sName, use, stream);
            
            if (bAssign) {
                Iterator    profiles    = repository.getProfiles(use);
                Profile     profile     = null;
                
                while (profiles.hasNext()) {
                    profile = (Profile) profiles.next();
                    if (profile.getDisplayName().equals(sName)) {
                        break;
                    }
                }
                
                if (profile!=null) {
                    Toolbox2.getSelectedEntity().assignProfile(profile);
                    NavigationModel navigationModel = (NavigationModel) RequestManager.getRequestContext().getModelManager().getModel(NavigationModel.class, "NavTree", true, true);
                    navigationModel.setAssignedState(Toolbox2.getSelectedEntity(), true);
                }
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
}
