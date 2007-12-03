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

import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Domain;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.entities.Host;
import com.sun.apoc.spi.policies.Policy;
import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.profiles.ProfileRepository;
import com.sun.web.ui.model.CCActionTableModel;
import com.sun.web.ui.model.CCActionTableModelInterface;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;


public class ProfileTableModel extends CCActionTableModel {
    
    // Child view names (i.e. display fields).
    public static final String CHILD_LEFTNAME_COLUMN    = "LeftNameColumn";
    public static final String CHILD_LEFTNAME_TEXT      = "LeftNameText";
    public static final String CHILD_SYNC_COLUMN        = "SyncColumn";
    public static final String CHILD_SYNC_BUTTON        = "SyncButton";
    public static final String CHILD_RIGHTNAME_COLUMN   = "RightNameColumn";
    public static final String CHILD_RIGHTNAME_TEXT     = "RightNameText";
    public static final String CHILD_DIFFS_COLUMN       = "DiffsColumn";
    public static final String CHILD_DIFFS_HREF         = "DiffsHref";
    public static final String CHILD_DIFFS_HREF_CLICK   = "DiffsHrefClick";
    public static final String CHILD_DIFFS_TEXT         = "DiffsText";
    public static final String CHILD_DIFFS_TEXT2        = "DiffsText2";
    public static final String CHILD_LEFTRIGHT_SORT     = "LeftRightSort";
    public static final String CHILD_LEFTID_HIDDEN      = "LeftIdHidden";
    public static final String CHILD_RIGHTID_HIDDEN     = "RightIdHidden";
    
    //
    private PolicyManager       m_leftPolicyManager;
    private PolicyManager       m_rightPolicyManager;
//    private int             m_nProfiles = 0;
    
    public ProfileTableModel() {
        super();
        
        setActionValue(CHILD_LEFTNAME_COLUMN,   "APOC.sync.result.column1");
        setActionValue(CHILD_DIFFS_COLUMN,      "APOC.sync.result.column2");
        
        setMaxRows(10);

        setPrimarySortName(CHILD_LEFTRIGHT_SORT);
        setPrimarySortOrder(CCActionTableModelInterface.ASCENDING);
    }
    
    public ProfileTableModel(PolicyManager leftPolicyManager, PolicyManager rightPolicyManager) {
        this();
        m_leftPolicyManager  = leftPolicyManager;
        m_rightPolicyManager = rightPolicyManager;
    }
    
    public void compareProfiles(
    Profile leftProfile,
    Profile rightProfile,
    Entity leftEntity,
    Entity rightEntity,
    LinkedList leftProfileList,
    LinkedList rightProfileList) throws ModelControlException {
        try {
            if (leftProfile!=null) {
                boolean bProfileMissing     = false;
                boolean bCommentsDiffer     = false;
                boolean bAssignmentsDiffer  = false;
                boolean bPriosDiffer        = false;
                boolean bContentsDiffer     = false;
                int     nDiffCount          = 0;
                int     nSyncDirection      = 0;
                
                // profile missing ?
                if (rightProfile!=null) {
                    
                    // comments differ ?
                    //TPF_TODO: ask Katell to not let getComment() return null
                    String leftComment = leftProfile.getComment();
                    String rightComment = rightProfile.getComment();
                    leftComment = (leftComment==null)?"":leftComment;
                    rightComment = (rightComment==null)?"":rightComment;
                    if (!leftComment.equals(rightComment)) {
                        bCommentsDiffer = true;
                        nDiffCount++;
                    }
                    
                    // assignments differ ?
                    Iterator leftAssignees      = leftProfile.getAssignedEntities();
                    Iterator rightAssignees     = rightProfile.getAssignedEntities();
                    HashMap  leftAssigneeMap   = new HashMap();
                    HashMap  rightAssigneeMap  = new HashMap();
                    
                    while (leftAssignees.hasNext()) {
                        Entity entity = (Entity) leftAssignees.next();
                        leftAssigneeMap.put(Toolbox2.getParentagePath(entity, false, true, "/", m_leftPolicyManager, false), entity);
                    }
                    
                    while (rightAssignees.hasNext()) {
                        Entity entity = (Entity) rightAssignees.next();
                        rightAssigneeMap.put(Toolbox2.getParentagePath(entity, false, true, "/", m_rightPolicyManager, false), entity);
                    }
                    
                    //TPF_TODO: handle assignments to entity that does not exist in other tree
                    //          compare for absolute or relative path names
                    if (leftAssigneeMap.size()!=leftAssigneeMap.size()) {
                        bAssignmentsDiffer = true;
                        nDiffCount++;
                    } else {
                        leftAssignees = leftProfile.getAssignedEntities();
                        while (leftAssignees.hasNext()) {
                            Entity leftAssignee = (Entity) leftAssignees.next();
                            if (!rightAssigneeMap.containsKey(Toolbox2.getParentagePath(leftAssignee, false, true, "/", m_leftPolicyManager, false))) {
                                bAssignmentsDiffer = true;
                                nDiffCount++;
                                break;
                            }
                        }
                    }
                    
                    //priorities differ ?
                    int nLeftProfilePos = leftProfileList.indexOf(leftProfile);
                    int nRightProfilePos = rightProfileList.indexOf(rightProfile);
                    
                    if (nLeftProfilePos!=nRightProfilePos) {
                        bPriosDiffer = true;
                        nDiffCount++;
                    }
                    
                    // contents differ ?
                    Iterator leftPolicies   = leftProfile.getPolicies();
                    Iterator rightPolicies  = rightProfile.getPolicies();
                    HashMap  leftPolicyMap  = new HashMap();
                    HashMap  rightPolicyMap = new HashMap();
                    
                    // contents differ (size) ?
                    while (leftPolicies.hasNext()) {
                        Policy policy = (Policy) leftPolicies.next();
                        leftPolicyMap.put(policy.getId(), policy);
                    }
                    
                    while (rightPolicies.hasNext()) {
                        Policy policy = (Policy) rightPolicies.next();
                        rightPolicyMap.put(policy.getId(), policy);
                    }
                    
                    if (leftPolicyMap.size()!=rightPolicyMap.size()) {
                        bContentsDiffer = true;
                        nDiffCount++;
                    }
                    
                    if (!bContentsDiffer) {
                        leftPolicies = leftProfile.getPolicies();
                        while (leftPolicies.hasNext()) {
                            // contents differ (key) ?
                            Policy leftPolicy = (Policy) leftPolicies.next();
                            if (rightPolicyMap.containsKey(leftPolicy.getId())) {
                                // contents differ (data) ?
                                Policy rightPolicy = (Policy) rightPolicyMap.get(leftPolicy.getId());
                                if (!leftPolicy.getData().equals(rightPolicy.getData())) {
                                    bContentsDiffer = true;
                                    nDiffCount++;
                                    break;
                                }
                            } else {
                                bContentsDiffer = true;
                                nDiffCount++;
                                break;
                            }
                        }
                    }
                } else {
                    bProfileMissing = true;
                    nDiffCount++;
                }
                
                if (nDiffCount>0) {
                    appendRow();
                    
                    String sSyncText = "";
                    
                    if (nDiffCount>1) {
                        if (bContentsDiffer) {
                            sSyncText += Toolbox2.getI18n("APOC.sync.action.copycontent")+" ";
                        }
                        if (bCommentsDiffer) {
                            sSyncText += Toolbox2.getI18n("APOC.sync.action.copycomment")+" ";
                        }
                        if (bAssignmentsDiffer) {
                            sSyncText += Toolbox2.getI18n("APOC.sync.action.alignassign")+" ";
                        }
                        if (bPriosDiffer) {
                            sSyncText += Toolbox2.getI18n("APOC.sync.action.changeprios");
                        }
                    } else {
                        if (bProfileMissing) {
                            sSyncText = Toolbox2.getI18n("APOC.sync.action.create");
                        } else if (bContentsDiffer) {
                            sSyncText = Toolbox2.getI18n("APOC.sync.action.copycontent");
                        } else if (bCommentsDiffer) {
                            sSyncText = Toolbox2.getI18n("APOC.sync.action.copycomment");
                        } else if (bAssignmentsDiffer) {
                            sSyncText = Toolbox2.getI18n("APOC.sync.action.alignassign");
                        } else if (bPriosDiffer) {
                            sSyncText = Toolbox2.getI18n("APOC.sync.action.changeprios");
                        }
                    }
                    
                    setValue(CHILD_LEFTRIGHT_SORT, leftProfile.getDisplayName());
                    setValue(CHILD_LEFTID_HIDDEN,  leftProfile.getId());
                    setValue(CHILD_RIGHTID_HIDDEN, ((bProfileMissing)?rightEntity.getId() + "|" + rightEntity.getPolicySourceName():rightProfile.getId()));
                    setValue(CHILD_DIFFS_HREF,      "");
                    setValue(CHILD_DIFFS_TEXT,      "");
                    setValue(CHILD_DIFFS_TEXT2,     sSyncText);
                }
            } else if (rightProfile!=null) {
                appendRow();
                
                setValue(CHILD_LEFTRIGHT_SORT,rightProfile.getDisplayName());
                setValue(CHILD_LEFTID_HIDDEN, leftEntity.getId() + "|" + leftEntity.getPolicySourceName());
                setValue(CHILD_RIGHTID_HIDDEN,rightProfile.getId());
                setValue(CHILD_DIFFS_HREF,    "");
                setValue(CHILD_DIFFS_TEXT,    "");
                setValue(CHILD_DIFFS_TEXT2,   Toolbox2.getI18n("APOC.sync.action.delete"));
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
}

//    public int getProcessedProfiles() {
//        return m_nProfiles;
//    }
    
//    public void retrieve(Entity leftEntity, Entity rightEntity) throws ModelControlException {
//        clear();
//        
//        try {
//            Applicability       aplicty             = ((leftEntity instanceof Domain)||(leftEntity instanceof Host))?Applicability.HOST:Applicability.USER;
//            ProfileRepository   leftRepository      = leftEntity.getProfileRepository();
//            ProfileRepository   rightRepository     = rightEntity.getProfileRepository();
//            Iterator            leftProfiles        = leftRepository.getProfiles(aplicty);
//            Iterator            rightProfiles       = rightRepository.getProfiles(aplicty);
//            HashMap             leftProfileMap      = new HashMap();
//            HashMap             rightProfileMap     = new HashMap();
//            LinkedList          leftProfileList     = new LinkedList();
//            LinkedList          rightProfileList    = new LinkedList();
//            
//            // Fill profile map and list.
//            // The profile list contains only profiles which exist
//            // on the left and the right side.
//            // The list is automatically sorted by prio as the iterator returned
//            // by rep.getProfiles() delivers the profiles sorted by prio.
//            // Prio can only differ if the left and right profile exist.
//            // The prio values are not compared but the position in an
//            // ordered list.
//            // So it's not really "prio" but "merge order" which the profiles
//            // are compared for
//            while (leftProfiles.hasNext()) {
//                m_nProfiles++;
//                Profile profile = (Profile) leftProfiles.next();
//                leftProfileMap.put(profile.getDisplayName(), profile);
//                leftProfileList.add(profile);
//            }
//            
//            while (rightProfiles.hasNext()) {
//                m_nProfiles++;
//                Profile profile = (Profile) rightProfiles.next();
//                rightProfileMap.put(profile.getDisplayName(), profile);
//                if (leftProfileMap.containsKey(profile.getDisplayName())) {
//                    rightProfileList.add(profile);
//                }
//            }
//            
//            Iterator leftPrios = leftProfileList.iterator();
//            while (leftPrios.hasNext()) {
//                String sProfileName = ((Profile) leftPrios.next()).getDisplayName();
//                if (!rightProfileMap.containsKey(sProfileName)) {
//                    leftPrios.remove();
//                }
//            }
//            
//            leftProfiles    = leftProfileMap.keySet().iterator();
//            rightProfiles   = rightProfileMap.keySet().iterator();
//            
//            // left profiles
//            while (leftProfiles.hasNext()) {
//                Profile leftProfile         = (Profile) leftProfileMap.get(leftProfiles.next());
//                Profile rightProfile        = (Profile) rightProfileMap.get(leftProfile.getDisplayName());
//                
//                compareProfiles(leftProfile, rightProfile, leftEntity, rightEntity, leftProfileList, rightProfileList);
//            }
//            
//            // right profiles without left peer
//            while (rightProfiles.hasNext()) {
//                Profile rightProfile = (Profile) rightProfileMap.get(rightProfiles.next());
//                
//                if (!leftProfileMap.containsKey(rightProfile.getDisplayName())) {
//                    compareProfiles(rightProfile, null, leftEntity, rightEntity, leftProfileList, rightProfileList);
//                }
//            }
//        }
//        catch (SPIException se) {
//            throw new ModelControlException(se);
//        }
//    }

