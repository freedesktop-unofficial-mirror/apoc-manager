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

package com.sun.apoc.manager.sync;

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.DefaultModel;
import com.iplanet.jato.model.DefaultModel.SingleFieldValueComparator;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.SyncLoginViewBean;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Domain;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.entities.Host;
import com.sun.apoc.spi.entities.Node;
import com.sun.apoc.spi.entities.Role;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.ldap.entities.LdapEntityType;
import com.sun.apoc.spi.policies.Policy;
import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.spi.profiles.InvalidProfileException;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.profiles.ProfileRepository;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.TreeSet;
import javax.servlet.http.HttpSession;
public class ContextTiledModel extends DefaultModel {
    
    public static final String LEFT_ENTITY      = "LeftEntity";
    public static final String RIGHT_ENTITY     = "RightEntity";
    public static final String PROFILES         = "Profiles";
    public static final String TITLE            = "Title";
    public static final String ORG_ROOT_IDENT   = "_!ORGfzwn!_";
    public static final String DOM_ROOT_IDENT   = "_!DOMwsui!_";
    
    private PolicyManager   m_leftPolicyManager = null;
    private PolicyManager   m_rightPolicyManager= null;
    private Entity      m_leftRootEntity    = null;
    private Entity      m_rightRootEntity   = null;
    private boolean     m_bComparableTrees  = true;
    private int         m_nFoundProfiles    = 0;
    private int         m_nComparedProfiles = 0;
    private int         m_nEntities         = 0;
    private HashMap     m_pathCache         = new HashMap();
    private String      m_sCurrentAction    = "APOC.sync.waitmsg.prepare";
    private Integer[]   m_currentNumbers    = new Integer[]{new Integer(0)};
    private long        m_nStartTime        = System.currentTimeMillis();
    private Exception   m_exception         = null;
    
    public ContextTiledModel() {
        super();
    }
    
    public boolean areTreesCompareable() {
        return m_bComparableTrees;
    }
    
    public int getFoundProfiles() {
        return m_nFoundProfiles;
    }
    
    public int getComparedProfiles() {
        return m_nComparedProfiles;
    }
    
    public int getProcessedEntities() {
        return m_nEntities;
    }
    
    synchronized public String getCurrentAction() {
        return m_sCurrentAction;
    }
    
    synchronized public Integer[] getCurrentNumbers() {
        return m_currentNumbers;
    }
    
    public Integer[] getElapsedTime() {
        float   nDiff    = (System.currentTimeMillis()-m_nStartTime)/1000;
        double  nHours   = Math.floor(nDiff/(60*60));
        double  nMinutes = Math.floor((nDiff-(nHours*60*60))/60);
        double  nSeconds = nDiff%60;
        return (new Integer[]{
            new Integer((int)nHours), 
            new Integer((int)nMinutes), 
            new Integer((int)nSeconds)});
    }
    
    public void setException(Exception ex) {
        m_exception = ex;
    }
    
    public Exception getException() {
        return m_exception;
    }
    
    public void resetModel () {
        m_leftPolicyManager = null;
        m_rightPolicyManager= null;
        m_leftRootEntity    = null;
        m_rightRootEntity   = null;
        m_bComparableTrees  = true;
        m_nFoundProfiles    = 0;
        m_nComparedProfiles = 0;
        m_nEntities         = 0;
        m_pathCache         = new HashMap();
        m_sCurrentAction    = "APOC.sync.waitmsg.prepare";
        m_currentNumbers    = new Integer[]{new Integer(0)};
        m_nStartTime        = System.currentTimeMillis();
        m_exception         = null;
        clear();
        m_pathCache.clear();
    }
    
    public void retrieve(PolicyManager leftManager, String sLeftEntityId, String sLeftEntityType, 
                         PolicyManager rightManager, String sRightEntityId, String sRightEntityType)
    throws ModelControlException {
        clear();
        m_pathCache.clear();
        int nAllProfiles=0;
        
        try {
            m_leftPolicyManager = leftManager;
            m_rightPolicyManager= rightManager;
            m_leftRootEntity    = (sLeftEntityId==null)?null:m_leftPolicyManager.getEntity(sLeftEntityType, sLeftEntityId);
            m_rightRootEntity   = (sRightEntityId==null)?null:m_rightPolicyManager.getEntity(sRightEntityType, sRightEntityId);
            
            if (((m_leftRootEntity!=null)&&(m_rightRootEntity!=null)) &&
                (Toolbox2.isDomainSubtype(m_leftRootEntity)^Toolbox2.isDomainSubtype(m_rightRootEntity))) {
                m_bComparableTrees = false;
                return;
            }
            
            HashMap 	leftEntityProfiles  = new HashMap();
            HashMap 	rightEntityProfiles = new HashMap();
            HashMap 	leftEntityPeer      = new HashMap();
            HashMap 	rightEntityPeer     = new HashMap();
            TreeSet     entityPaths         = new TreeSet();
            Iterator    leftProfiles;
            Iterator    rightProfiles;
            
            if ((m_leftRootEntity==null)^(m_rightRootEntity==null)) {
                if (m_leftRootEntity==null) {
                    m_leftRootEntity = m_leftPolicyManager.getRootEntity(m_rightRootEntity.getPolicySourceName());
                }
                if (m_rightRootEntity==null) {
                    m_rightRootEntity = m_rightPolicyManager.getRootEntity(m_leftRootEntity.getPolicySourceName());
                }
            }        
            m_sCurrentAction = "APOC.sync.waitmsg.retsource";
            if (m_leftRootEntity==null) {
                leftProfiles = m_leftPolicyManager.getAllProfiles();
            } else {
                String source = m_leftRootEntity.getPolicySourceName();
                leftProfiles = m_leftPolicyManager.getProfileProvider(source).getAllProfiles(m_leftRootEntity);
            }
            
            m_sCurrentAction = "APOC.sync.waitmsg.rettarget";
            if (m_rightRootEntity==null) {
                rightProfiles = m_rightPolicyManager.getAllProfiles();
            } else {
                String source = m_rightRootEntity.getPolicySourceName();
                rightProfiles = m_rightPolicyManager.getProfileProvider(source).getAllProfiles(m_rightRootEntity);
            }
            
            // partition profiles to entity paths
            synchronized (this) {
                m_sCurrentAction = "APOC.sync.waitmsg.prepx";
                m_currentNumbers = new Integer[]{new Integer(0)};
            }
            while (leftProfiles.hasNext()) {
                nAllProfiles++;
                Profile profile     = (Profile) leftProfiles.next();
                Entity  entity      = profile.getProfileRepository().getEntity();
                //CCDebug.trace1(profile.getDisplayName()+" : "+profile.getApplicability().getStringValue()+" : "+entity.getDisplayName()+" : "+entity.getClass().toString());
                Entity  peerEntity  = getPeer(entity, false);
                if (peerEntity!=null) {
                    leftEntityPeer.put(entity, peerEntity);
                    HashMap profiles;
                    String  sEntityPath = getRelativePath(entity, m_leftRootEntity, m_leftPolicyManager);
                    if ((m_leftRootEntity==null) && (sEntityPath.length()==0)) {
                        if (profile.getApplicability().equals(Applicability.HOST)) {
                            sEntityPath = DOM_ROOT_IDENT;
                        } else if (profile.getApplicability().equals(Applicability.USER)) {
                            sEntityPath = ORG_ROOT_IDENT;
                        } 
                    }
                    entityPaths.add(sEntityPath);
                    if (leftEntityProfiles.containsKey(sEntityPath)) {
                        profiles = (HashMap) leftEntityProfiles.get(sEntityPath);
                    } else {
                        profiles = new HashMap();
                        leftEntityProfiles.put(sEntityPath, profiles);
                    }
                    profiles.put(profile.getDisplayName(), profile);
                }
                synchronized (this) {
                    m_sCurrentAction = "APOC.sync.waitmsg.prepx";
                    m_currentNumbers = new Integer[]{new Integer(++m_nFoundProfiles)};
                }
            }
            
            while (rightProfiles.hasNext()) {
                nAllProfiles++;
                Profile profile     = (Profile) rightProfiles.next();
                Entity  entity      = profile.getProfileRepository().getEntity();
                Entity  peerEntity  = getPeer(entity, true);
                //CCDebug.trace1(profile.getDisplayName()+" : "+profile.getApplicability().getStringValue()+" : "+entity.getDisplayName()+" : "+entity.getClass().toString());
                if (peerEntity!=null) {
                    rightEntityPeer.put(entity, peerEntity);
                    HashMap profiles;
                    String  sEntityPath = getRelativePath(entity, m_rightRootEntity, m_rightPolicyManager);
                    if ((m_rightRootEntity==null) && (sEntityPath.length()==0)) {
                        if (profile.getApplicability().equals(Applicability.HOST)) {
                            sEntityPath = DOM_ROOT_IDENT;
                        } else if (profile.getApplicability().equals(Applicability.USER)) {
                            sEntityPath = ORG_ROOT_IDENT;
                        } 
                    }
                    entityPaths.add(sEntityPath);
                    if (rightEntityProfiles.containsKey(sEntityPath)) {
                        profiles = (HashMap) rightEntityProfiles.get(sEntityPath);
                    } else {
                        profiles = new HashMap();
                        rightEntityProfiles.put(sEntityPath, profiles);
                    }
                    profiles.put(profile.getDisplayName(), profile);
                }
                synchronized (this) {
                    m_sCurrentAction = "APOC.sync.waitmsg.prepx";
                    m_currentNumbers = new Integer[]{new Integer(++m_nFoundProfiles)};
                }
            }
            
            synchronized (this) {
                m_sCurrentAction = "APOC.sync.waitmsg.compx";
                m_currentNumbers = new Integer[]{new Integer(0), new Integer(nAllProfiles)};
            }
            // iterate over entity paths
            Iterator entityPathsIter = entityPaths.iterator();
            while (entityPathsIter.hasNext()) {
                String      sEntityPath     = (String) entityPathsIter.next();
                HashMap     leftProfileMap  = (HashMap) leftEntityProfiles.get(sEntityPath);
                HashMap     rightProfileMap = (HashMap) rightEntityProfiles.get(sEntityPath);
                LinkedList  leftCommonProfilesList = new LinkedList();
                LinkedList  rightCommonProfilesList= new LinkedList();
                
                if (leftProfileMap==null) {
                    leftProfileMap=new HashMap();
                }
                
                if (rightProfileMap==null) {
                    rightProfileMap=new HashMap();
                }
                
                // Build CommonProfilesList; these lists contain only profiles
                // which are available on the left and the right side.
                // They are needed to discover differing priorities.
                leftProfiles = leftProfileMap.values().iterator();
                while (leftProfiles.hasNext()) {
                    Profile profile = (Profile) leftProfiles.next();
                    // sort list (required by compareProfiles())
                    int nCommonRunner = 0;
                    while ( (nCommonRunner<leftCommonProfilesList.size()) &&
                        (((Profile)leftCommonProfilesList.get(nCommonRunner)).getPriority()<profile.getPriority())) {
                        nCommonRunner++;
                    }
                    leftCommonProfilesList.add(nCommonRunner,profile);
                }
                
                rightProfiles = rightProfileMap.values().iterator();
                while (rightProfiles.hasNext()) {
                    Profile profile = (Profile) rightProfiles.next();
                    if (leftProfileMap.containsKey(profile.getDisplayName())) {
                        // sort list (required by compareProfiles())
                        int nCommonRunner = 0;
                        while ( (nCommonRunner<rightCommonProfilesList.size()) &&
                            (((Profile)rightCommonProfilesList.get(nCommonRunner)).getPriority()<profile.getPriority())) {
                            nCommonRunner++;
                        }
                        rightCommonProfilesList.add(nCommonRunner,profile);
                    }
                }
                
                Iterator leftPrios = leftCommonProfilesList.iterator();
                while (leftPrios.hasNext()) {
                    String sProfileName = ((Profile) leftPrios.next()).getDisplayName();
                    if (!rightProfileMap.containsKey(sProfileName)) {
                        leftPrios.remove();
                    }
                }
                
                ProfileTableModel profileModel = null;
                
                if ((leftProfileMap.size()>0)||(rightProfileMap.size()>0)) {
                    profileModel = new ProfileTableModel(m_leftPolicyManager, m_rightPolicyManager);
                    //changed because this model is executed in a seperate thread now and has thus no access to the session
                    //profileModel.setDocument(session.getServletContext(), "/jsp/sync/Table.xml");
                    profileModel.setDocument(getTableStream());
                    
                    Profile leftProfile  = null;
                    Profile rightProfile = null;
                    Entity  leftEntity   = null;
                    Entity  rightEntity  = null;
                    
                    // iterate over the left profiles for one entity
                    leftProfiles = leftProfileMap.values().iterator();
                    while (leftProfiles.hasNext()) {
//Thread.currentThread().sleep(10000);
                        leftProfile = (Profile) leftProfiles.next();
                        rightProfile= (Profile) rightProfileMap.get(leftProfile.getDisplayName());
                        leftEntity  = leftProfile.getProfileRepository().getEntity();
                        rightEntity = (Entity) leftEntityPeer.get(leftEntity);
                        if (leftEntity!=null && rightEntity!=null) {
                            if (rightProfile!=null) {
                                m_nComparedProfiles++;
                            }
                            profileModel.compareProfiles(leftProfile, rightProfile, leftEntity, rightEntity, leftCommonProfilesList, rightCommonProfilesList);
                            synchronized (this) {
                                m_sCurrentAction = "APOC.sync.waitmsg.compx";
                                m_currentNumbers = new Integer[]{new Integer(++m_nComparedProfiles), new Integer(nAllProfiles)};
                            }
                        }
                    }
                    
                    // iterate over the right profiles for one entity which are
                    // not already covered by the previous iteration
                    rightProfiles = rightProfileMap.values().iterator();
                    while (rightProfiles.hasNext()) {
//Thread.currentThread().sleep(10000);
                        rightProfile = (Profile) rightProfiles.next();
                        if (!leftProfileMap.containsKey(rightProfile.getDisplayName())) {
                            leftProfile = null;
                            rightEntity = rightProfile.getProfileRepository().getEntity();
                            leftEntity  = (Entity) rightEntityPeer.get(rightEntity);
                            if (leftEntity!=null && rightEntity!=null) {
                                profileModel.compareProfiles(leftProfile, rightProfile, leftEntity, rightEntity, leftCommonProfilesList, rightCommonProfilesList);
                                synchronized (this) {
                                    m_sCurrentAction = "APOC.sync.waitmsg.compx";
                                    m_currentNumbers = new Integer[]{new Integer(++m_nComparedProfiles), new Integer(nAllProfiles)};
                                }
                            }
                        }
                    }
                    
                    if (profileModel.getNumRows()>0) {
                        appendRow();
                        setValue(LEFT_ENTITY,   leftEntity);
                        setValue(RIGHT_ENTITY,  rightEntity);
                        setValue(PROFILES,      profileModel);
                    }
                }
            }
        } catch (Exception se) {
            throw new ModelControlException(se);
        }
    }
    
    public String sync(PolicyManager leftManager, String sLeftEntityId, String sLeftEntityType,
                     PolicyManager rightManager, String sRightEntityId, String sRightEntityType,
                     LinkedList syncList, HttpSession session,
                     boolean bSyncPrios, boolean bDoFullSync) throws ModelControlException {
        
        m_pathCache.clear();
        String targetProfileId = "";
        synchronized (this) {
            m_sCurrentAction = "APOC.sync.waitmsg.sync";
            m_currentNumbers = new Integer[]{new Integer(0), new Integer(syncList.size())};
        }
        try {
            m_leftPolicyManager = leftManager;
            m_rightPolicyManager= rightManager;
            m_leftRootEntity    = (sLeftEntityId==null)?null:m_leftPolicyManager.getEntity(sLeftEntityType, sLeftEntityId);
            m_rightRootEntity   = (sRightEntityId==null)?null:m_rightPolicyManager.getEntity(sRightEntityType, sRightEntityId);
            
            String leftContextName = (String)m_leftPolicyManager.getEnvironment().get(Constants.POLICY_MANAGER_NAME);
            String rightContextName = (String)m_rightPolicyManager.getEnvironment().get(Constants.POLICY_MANAGER_NAME);
            NavigationModel navModel        = null;
            HashMap         syncEnv         = (HashMap) session.getAttribute(Constants.SYNC_ENVIRONMENT);

            // This method is used by both sync and copymove, bSyncPrios is true for sync
            // but not for copymove so use as flag rather than having separate method/flag
            String sRightContext = "";
            if (bSyncPrios) {
                sRightContext = (String) syncEnv.get(SyncLoginViewBean.ENV_RIGHT_CONTEXTNAME);
            } else {
                sRightContext = (String)m_rightPolicyManager.getEnvironment().get(Constants.POLICY_MANAGER_NAME);
            }
            String  sMainContext    = (String) session.getAttribute(Constants.POLICY_MANAGER_NAME);
            boolean bSyncMainContext= (sMainContext==null)?false:sMainContext.equals(sRightContext);
            if (bSyncMainContext) {
                // using RequestManager here does only work if sync is called from the main request thread
                navModel = (NavigationModel) RequestManager.
                getRequestContext().
                getModelManager().
                getModel(NavigationModel.class, "NavTree", true, true);
            }
            
            int         nCurrentPos  = 0;
            Iterator    syncListIter = syncList.iterator();
            while (syncListIter.hasNext()) {
//Thread.currentThread().sleep(10000);
                String sLeft  = (String) syncListIter.next();
                String sLeftType = "";
                if (sLeft.indexOf("|") != -1) {
                    sLeftType = sLeft.substring(sLeft.indexOf("|") + 1);
                    sLeft = sLeft.substring(0, sLeft.indexOf("|"));
                }
                String sRight = (String) syncListIter.next();
                String sRightType = "";
                if (sRight.indexOf("|") != -1) {
                    sRightType = sRight.substring(sRight.indexOf("|") + 1);
                    sRight = sRight.substring(0, sRight.indexOf("|"));
                }
                nCurrentPos+=2;
                Profile sourceProfile   = null;
                Profile targetProfile   = null;
                Entity  sourceEntity    = null;
                Entity  targetEntity    = null;
                try {
                    sourceProfile = m_leftPolicyManager.getProfile(sLeft);
                } catch (InvalidProfileException ipe) {}
                try {
                    targetProfile = m_rightPolicyManager.getProfile(sRight);
                } catch (InvalidProfileException ipe) {}
                // if there is no profile the client sent the entity hosting the repository
                if (sourceProfile==null) {
                    sourceEntity = m_leftPolicyManager.getEntity(sLeftType, sLeft);
                } else if (targetProfile==null) {
                    targetEntity = m_rightPolicyManager.getEntity(sRightType, sRight);
                }
                
                // source profile not available -> delete drain
                if (sourceProfile==null) {
                    if (bDoFullSync) {
                        // delete target profile
                        ProfileRepository   repository          = targetProfile.getProfileRepository();
                        Iterator            assignedEntities    = targetProfile.getAssignedEntities();
                        
                        while (assignedEntities.hasNext()) {
                            Entity entity = (Entity) assignedEntities.next();
                            entity.unassignProfile(targetProfile);
                            if (bSyncMainContext) {
                                navModel.setAssignedState(entity, entity.getAssignedProfiles().hasNext());
                            }
                        }
                        
                        repository.destroyProfile(targetProfile);
                        continue;
                    } else {
                        // delete only policies and comment in target profile
                        Iterator targetPolicies = targetProfile.getPolicies();
                        
                        while (targetPolicies.hasNext()) {
                            Policy policy = (Policy) targetPolicies.next();
                            targetProfile.destroyPolicy(policy);
                        }
                        
                        targetProfile.setComment("");
                    }
                }
                
                // create target profile if it doesn't exist
                if (targetProfile==null) {
                    ProfileRepository repository = targetEntity.getProfileRepository();
                    targetProfile = repository.createProfile(sourceProfile.getDisplayName(), sourceProfile.getApplicability());
                    targetProfileId = targetProfile.getId();
                    // align prio
                    LinkedList targetProfileList= new LinkedList();
                    LinkedList sourceNameList   = new LinkedList();
                    LinkedList targetNameList   = new LinkedList();
                    Iterator   sourceProfiles   = sourceProfile.getProfileRepository().getProfiles(sourceProfile.getApplicability());
                    Iterator   targetProfiles   = targetProfile.getProfileRepository().getProfiles(targetProfile.getApplicability());
                    // populate structures
                    while (sourceProfiles.hasNext()) {
                        Profile profile = (Profile) sourceProfiles.next();
                        sourceNameList.add(profile.getDisplayName());
                    }
                    while (targetProfiles.hasNext()) {
                        Profile profile = (Profile) targetProfiles.next();
                        targetProfileList.add(profile);
                        targetNameList.add(profile.getDisplayName());
                    }
                    
                    // find a profile with the same name in source and target
                    // list which is directly above the source profile (ignoring
                    // profiles which are not on both sides)
                    String  sProfileName        = sourceProfile.getDisplayName();
                    int     nSourceUpperBoundPos= sourceNameList.indexOf(sProfileName)-1;
                    int     nTargetUpperBoundPos= -1;
                    while ( (nSourceUpperBoundPos>-1) && (nTargetUpperBoundPos==-1) ) {
                        nTargetUpperBoundPos = targetNameList.indexOf(sourceNameList.get(nSourceUpperBoundPos));
                        nSourceUpperBoundPos--;
                    }
                    
                    // increase prio of profiles below found profile by one
                    int nProfileRunner = targetProfileList.size()-1;
                    while (nProfileRunner>nTargetUpperBoundPos) {
                        Profile profile = (Profile)targetProfileList.get(nProfileRunner);
                        profile.setPriority(profile.getPriority()+1);
                        nProfileRunner--;
                    }
                    
                    // set the prio of the targetProfile
                    targetProfile.setPriority(((Profile)targetProfileList.get(nProfileRunner+1)).getPriority()-1);
                }
                
                // sync comment
                String sComment = sourceProfile.getComment();
                targetProfile.setComment((sComment==null)?"":sComment);
                
                // sync policies
                Iterator sourcePolicies  = sourceProfile.getPolicies();
                Iterator targetPolicies  = targetProfile.getPolicies();
                HashMap  sourcePolicyMap = new HashMap();
                HashMap  targetPolicyMap = new HashMap();
                
                while (sourcePolicies.hasNext()) {
                    Policy policy = (Policy) sourcePolicies.next();
                    sourcePolicyMap.put(policy.getId(), policy);
                }
                
                while (targetPolicies.hasNext()) {
                    Policy policy = (Policy) targetPolicies.next();
                    targetPolicyMap.put(policy.getId(), policy);
                }
                
                // copy diffs from source to target
                sourcePolicies = sourcePolicyMap.keySet().iterator();
                while (sourcePolicies.hasNext()) {
                    String sPolicyId    = (String) sourcePolicies.next();
                    Policy sourcePolicy = (Policy) sourcePolicyMap.get(sPolicyId);
                    Policy targetPolicy = (Policy) targetPolicyMap.get(sPolicyId);
                    
                    if ( (targetPolicy==null) || (!targetPolicy.getData().equals(sourcePolicy.getData())) ) {
                        Policy newPolicy = new Policy(sPolicyId, targetProfile.getId(), ((Policy) sourcePolicyMap.get(sPolicyId)).getData());
                        targetProfile.storePolicy(newPolicy);
                    }
                }
                // remove diffs from target
                targetPolicies = targetPolicyMap.keySet().iterator();
                while (targetPolicies.hasNext()) {
                    String sPolicyId  = (String) targetPolicies.next();
                    if ( !sourcePolicyMap.containsKey(sPolicyId) ) {
                        targetProfile.destroyPolicy((Policy)targetPolicyMap.get(sPolicyId));
                    }
                }
                
                if (bDoFullSync) {
                    // sync assignments
                    Iterator assignedEntities = targetProfile.getAssignedEntities();
                    while (assignedEntities.hasNext()) {
                        Entity entity = (Entity) assignedEntities.next();
                        entity.unassignProfile(targetProfile);
                        if (bSyncMainContext) {
                            navModel.setAssignedState(entity, entity.getAssignedProfiles().hasNext());
                        }
                    }
                    
                    assignedEntities = sourceProfile.getAssignedEntities();
                    while (assignedEntities.hasNext()) {
                        Entity entity               = (Entity) assignedEntities.next();
                        Entity targetAssignEntity   = null;
                        if (leftContextName.equals(rightContextName)) {
                            targetAssignEntity = entity;
                        } else {
                            targetAssignEntity = getPeer(entity, false);
                        }
                        if (targetAssignEntity!=null) {
                            targetAssignEntity.assignProfile(targetProfile);
                        }
                        if (bSyncMainContext) {
                            navModel.setAssignedState(targetAssignEntity, true);
                        }
                    }
                    
                    //sync prios
                    if (bSyncPrios) {
                        //keep profiles which are in the source and the target repsitories only
                        LinkedList sourceProfileList= new LinkedList();
                        LinkedList targetProfileList= new LinkedList();
                        LinkedList sourceNameList   = new LinkedList();
                        LinkedList targetNameList   = new LinkedList();
                        Iterator sourceProfiles = sourceProfile.getProfileRepository().getProfiles(sourceProfile.getApplicability());
                        Iterator targetProfiles = targetProfile.getProfileRepository().getProfiles(targetProfile.getApplicability());
                        while (targetProfiles.hasNext()) {
                            Profile profile = (Profile) targetProfiles.next();
                            targetProfileList.add(profile);
                            targetNameList.add(profile.getDisplayName());
                        }

                        int nMaxPrio = ((Profile) targetProfileList.getLast()).getPriority();

                        while (sourceProfiles.hasNext()) {
                            Profile profile = (Profile) sourceProfiles.next();
                            if (targetNameList.contains(profile.getDisplayName())) {
                                sourceProfileList.add(profile);
                                sourceNameList.add(profile.getDisplayName());
                            }
                        }

                        Iterator targetNameIter = targetNameList.iterator();
                        while (targetNameIter.hasNext()) {
                            String sName = (String) targetNameIter.next();
                            if (!sourceNameList.contains(sName)) {
                                targetProfileList.remove(targetNameList.indexOf(sName));
                                targetNameIter.remove();
                            }
                        }

                        int nSourceProfilePos = sourceNameList.indexOf(sourceProfile.getDisplayName());
                        int nTargetProfilePos = targetNameList.indexOf(sourceProfile.getDisplayName());

                        if (nSourceProfilePos!=nTargetProfilePos) {
                            // sync is necessary, it's done by swapping the prios of the
                            // target profile and the 2nd profile in the target repository
                            int nTargetProfilePrio  = targetProfile.getPriority();
                            String sProfile2        = (String) sourceNameList.get(nTargetProfilePos);
                            Profile targetProfile2  = (Profile) targetProfileList.get(targetNameList.indexOf(sProfile2));
                            int nTargetProfile2Prio = targetProfile2.getPriority();

                            targetProfile.setPriority(nMaxPrio+1);
                            targetProfile2.setPriority(nTargetProfilePrio);
                            targetProfile.setPriority(nTargetProfile2Prio);
                        }  
                    }
                }
                synchronized (this) {
                    m_sCurrentAction = "APOC.sync.waitmsg.sync";
                    m_currentNumbers = new Integer[]{new Integer(nCurrentPos), new Integer(syncList.size())};
                }
            }
        } catch (Exception se) {
            throw new ModelControlException(se);
        }
        return targetProfileId;
    }

    // this method must be executed after the retrieve thread is done and
    // from within a valid request context because I need localized parentage paths
    // as table titles
    public void runSessionActions() throws ModelControlException {
        
        beforeFirst();
        
        while (next()) {
            
            Entity leftEntity = (Entity) getValue(LEFT_ENTITY);
            Entity rightEntity = (Entity) getValue(RIGHT_ENTITY);
            
            String sTitle = Toolbox2.getParentagePath(
                (leftEntity!=null)?leftEntity:rightEntity, false, true, "/",
                (leftEntity!=null)?m_leftPolicyManager:m_rightPolicyManager);
            
            if (((leftEntity!=null)?m_leftRootEntity:m_rightRootEntity)!=null) {
                
                String sRoot = Toolbox2.getParentagePath(
                    (leftEntity!=null)?m_leftRootEntity:m_rightRootEntity, false, false, "/",
                    (leftEntity!=null)?m_leftPolicyManager:m_rightPolicyManager);
                
                if (sRoot.length()>0) {
                    sTitle.substring(sRoot.length()+2);
                    sTitle = sTitle.trim();
                }
            }
            
            setValue(TITLE, sTitle);
            ((ProfileTableModel)getValue(PROFILES)).setTitle(sTitle);
            ((ProfileTableModel)getValue(PROFILES)).setTitleLabel("APOC.sync.result.title2");
        }
        
        sort(new SingleFieldValueComparator(new String[]{TITLE}, new EntityPathComparator()));
    }
    
    private String getRelativePath(Entity entity, Entity rootEntity, PolicyManager policyManager) {
        
        // this kind of quick&dirty caching works correctly only if the cache
        // is cleared between retrieve() and sync() calls
        if (m_pathCache.containsKey(entity)) {
            return (String) m_pathCache.get(entity);
        }
        
        String sEntityPath = Toolbox2.getParentagePath(entity, false, true, "/", policyManager, false);
        
        if (rootEntity!=null) {
            String sRootEntityPath = Toolbox2.getParentagePath(rootEntity, false, true, "/", policyManager, false);
            sEntityPath = sEntityPath.substring(sRootEntityPath.length()).trim();
            if (sEntityPath.startsWith("/")) {
                sEntityPath = sEntityPath.substring(1);
            }
        } else {
            int nSlashPos = sEntityPath.indexOf("/");
            if (nSlashPos>-1) {
                sEntityPath = sEntityPath.substring(nSlashPos+1);
            } else {
                sEntityPath="";
            }
        }
        
        sEntityPath = sEntityPath.trim();
        
        m_pathCache.put(entity, sEntityPath);
        
        return sEntityPath;
    }
    
    private Entity getPeer(Entity entity, boolean bGetLeftPeer) throws SPIException {
        
        PolicyManager sourcePolicyManager;
        PolicyManager targetPolicyManager;
        Entity    sourceRootEntity;
        Entity    targetRootEntity;
        
        if (bGetLeftPeer) {
            sourcePolicyManager = m_rightPolicyManager;
            targetPolicyManager = m_leftPolicyManager;
            sourceRootEntity    = m_rightRootEntity;
            targetRootEntity    = m_leftRootEntity;
        } else {
            sourcePolicyManager = m_leftPolicyManager;
            targetPolicyManager = m_rightPolicyManager;
            sourceRootEntity    = m_leftRootEntity;
            targetRootEntity    = m_rightRootEntity;
        }
        
        if (((entity!=null)&&(sourceRootEntity!=null)) &&
            Toolbox2.isDomainSubtype(entity)^Toolbox2.isDomainSubtype(sourceRootEntity)) {
            return null;
        }
        
        String sEntityPath = getRelativePath(entity, sourceRootEntity, sourcePolicyManager);
        
        if (targetRootEntity==null) {
            targetRootEntity = targetPolicyManager.getRootEntity(entity.getPolicySourceName());
        }

        Entity targetAssignEntity = getEntity(targetRootEntity, sEntityPath);
        return targetAssignEntity;
    }
    
    private Entity getEntity(Entity startingPoint, String sPath) throws SPIException {
        
        String sPrefix;
        
        if (sPath.length()==0) {
            return startingPoint;
        } else if (sPath.indexOf("/")!=-1) {
            sPrefix = sPath.substring(0, sPath.indexOf("/")).trim();
            sPath   = sPath.substring(sPath.indexOf("/")+1).trim();
        } else {
            sPrefix = sPath.trim();
            sPath   = "";
        }
        
        if (startingPoint instanceof Node) {
            startingPoint = getMatch(((Node)startingPoint).getChildren(), sPrefix);
        } else if (startingPoint instanceof Role) {
            startingPoint = getMatch(((Role)startingPoint).getRoles(), sPrefix);
        }
        
        return getEntity(startingPoint, sPath);
    }
    
    private Entity getMatch(Iterator entityIter, String sRequestedName) throws SPIException {
        while (entityIter.hasNext()) {
            Entity entity = (Entity) entityIter.next();
            if (entity.getDisplayName(Toolbox2.getLocale()).equals(sRequestedName)) {
                return entity;
            }
        }
        return null;
    }
    
    private boolean haveMatch(Entity leftEntity, Entity rightEntity) throws SPIException {
        
        String sLeftClass  = leftEntity.getClass().getName();
        String sRightClass = rightEntity.getClass().getName();
        
        if (sLeftClass.equals(sRightClass)) {
            
            String[] sources = m_leftPolicyManager.getSources();
            for (int i = 0; i < sources.length; i++) {
                if (leftEntity.equals(m_leftPolicyManager.getRootEntity(sources[i]))) {
                    return rightEntity.equals(m_rightPolicyManager.getRootEntity(sources[i]));
                }                
            }
            
            String sLeftName  = leftEntity.getDisplayName(Toolbox2.getLocale());
            String sRightName = rightEntity.getDisplayName(Toolbox2.getLocale());
            
            return sLeftName.equals(sRightName);
        }
        return false;
    }
    
    private InputStream getTableStream() {
        StringBuffer sXml=new StringBuffer();
        sXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sXml.append("<!DOCTYPE table SYSTEM \"tags/dtd/table.dtd\">");
        sXml.append("<table>");
        sXml.append("    <column name=\"LeftNameColumn\" extrahtml=\"nowrap='nowrap'\" rowheader=\"true\">");
        sXml.append("        <cc name=\"LeftRightSort\" tagclass=\"com.sun.web.ui.taglib.html.CCStaticTextFieldTag\" />");
        sXml.append("        <cc name=\"LeftIdHidden\" tagclass=\"com.sun.web.ui.taglib.html.CCHiddenTag\" />");
        sXml.append("        <cc name=\"RightIdHidden\" tagclass=\"com.sun.web.ui.taglib.html.CCHiddenTag\" />");
        sXml.append("    </column>");
        sXml.append("    <column name=\"DiffsColumn\" extrahtml=\"nowrap='nowrap'\">");
        sXml.append("        <cc name=\"DiffsHref\" tagclass=\"com.sun.web.ui.taglib.html.CCHrefTag\">");
        sXml.append("            <cc name=\"DiffsText\" tagclass=\"com.sun.web.ui.taglib.html.CCStaticTextFieldTag\" />");
        sXml.append("        </cc>");
        sXml.append("        <cc name=\"DiffsText2\" tagclass=\"com.sun.web.ui.taglib.html.CCStaticTextFieldTag\" />");
        sXml.append("    </column>");
        sXml.append("</table>");
        return new StringBufferInputStream(sXml.toString());
    }
    
    class EntityPathComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            
            StringTokenizer sParticles1 = new StringTokenizer((String)o1, "/");
            StringTokenizer sParticles2 = new StringTokenizer((String)o2, "/");
            int nParticles1 = sParticles1.countTokens();
            int nParticles2 = sParticles2.countTokens();
            
            if (nParticles1<nParticles2) {
                return -1;
            } else if (nParticles1>nParticles2) {
                return 1;
            } else {
                while (sParticles1.hasMoreTokens()) {
                    String sParticle1 = sParticles1.nextToken();
                    String sParticle2 = sParticles2.nextToken();
                    int nCompare = sParticle1.compareTo(sParticle2);
                    
                    if (nCompare!=0) {
                        return nCompare;
                    }
                }
            }
            return 0;
        }
    }
}

//    protected void retrieveRecursive(Entity leftEntity, Entity rightEntity) throws SPIException, ModelControlException {
//
//        ProfileTableModel profileModel = new ProfileTableModel(m_leftPolicyManager, m_rightPolicyManager);
//        //changed because this model is executed in a seperate thread now and has thus no access to the session
//        //profileModel.setDocument(RequestManager.getSession().getServletContext(), "/jsp/sync/Table.xml");
//        profileModel.setDocument(getTableStream());
//        profileModel.retrieve(leftEntity, rightEntity);
//
//        m_nEntities+=2;
//        m_nProfiles+=profileModel.getProcessedProfiles();
//        m_sCurrentAction = "Analysing subtree ("+m_nEntities+" entities visited, "+m_nProfiles+" profiles compared)";
//
//        if (profileModel.getNumRows()>0) {
//            appendRow();
//            setValue(LEFT_ENTITY,   leftEntity);
//            setValue(RIGHT_ENTITY,  rightEntity);
//            setValue(PROFILES,      profileModel);
//        }
//
//        if ((leftEntity instanceof Domain) || (leftEntity instanceof Organization)) {
//            retrieveRecursive(((Node)leftEntity).getChildren(), ((Node)rightEntity).getChildren());
//        }
//        else if (leftEntity instanceof Role) {
//            retrieveRecursive(((Role)leftEntity).getRoles(), ((Role)rightEntity).getRoles());
//        }
//    }
//
//    private void retrieveRecursive(Iterator leftChildren, Iterator rightChildren) throws SPIException, ModelControlException {
//        HashMap     rightChildMap = new HashMap();
//        LinkedList  leftChildList = new LinkedList();
//        while (rightChildren.hasNext()) {
//            Entity entity = (Entity) rightChildren.next();
//            rightChildMap.put(entity.getDisplayName(), entity);
//        }
//
//        while (leftChildren.hasNext()) {
//            Entity leftChild    = (Entity) leftChildren.next();
//            Entity rightChild   = (Entity) rightChildMap.get(leftChild.getDisplayName());
//            leftChildList.add(leftChild.getDisplayName());
//
//            if (rightChild!=null) {
//                retrieveRecursive(leftChild, rightChild);
//            } else {
//                m_bEqualTrees = false;
//            }
//        }
//
//        Iterator rightIter = rightChildMap.keySet().iterator();
//        while (rightIter.hasNext()) {
//            String sDisplayName = (String) rightIter.next();
//            if (!leftChildList.contains(sDisplayName)) {
//                m_bEqualTrees = false;
//                break;
//            }
//        }
//    }
