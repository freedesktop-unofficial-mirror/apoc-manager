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

import com.iplanet.jato.model.Model;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingViewBase;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCStaticTextField;

import com.sun.web.ui.view.wizard.CCWizardPage;

import com.sun.web.ui.common.CCDebug;

import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.profiles.ProfileRepository;
import com.sun.apoc.spi.entities.Entity;

import com.sun.apoc.manager.Toolbox2;
import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.apoc.manager.sync.ContextTiledModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class CopyMoveWizard4View extends RequestHandlingViewBase
    implements CCWizardPage, WizardImplDataUtil{

    // The "logical" name for this page.
    public static final String PAGE_NAME = "CopyMoveWizard4View";

    // Child view names (i.e. display fields).

    public static final String CHILD_SOURCE_CONTEXT_LABEL =
        "SourceContextLabel";
    public static final String CHILD_SOURCE_CONTEXT =
        "SourceContext";
    public static final String CHILD_SOURCE_PROFILE_LABEL =
        "SourceProfileLabel";
    public static final String CHILD_SOURCE_PROFILE =
        "SourceProfile";
    public static final String CHILD_ACTION_LABEL =
        "SelectActionLabel";
    public static final String CHILD_ACTION =
        "ActionSelected";
    public static final String CHILD_ASSIGNMENTS_LABEL =
        "AssignmentsLabel";
    public static final String CHILD_ASSIGNMENTS =
        "AssignmentsYesNo";
    public static final String CHILD_TARGET_CONTEXT_LABEL =
        "TargetContextLabel";
    public static final String CHILD_TARGET_CONTEXT =
        "TargetContext";
    public static final String CHILD_TARGET_ENTITY_LABEL =
        "TargetEntityLabel";
    public static final String CHILD_TARGET_ENTITY =
        "TargetEntity";
    private CCI18N m_I18n;    
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public CopyMoveWizard4View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }

    public CopyMoveWizard4View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        registerChildren();
    }

    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_SOURCE_CONTEXT_LABEL, CCLabel.class);
        registerChild(CHILD_SOURCE_CONTEXT, CCStaticTextField.class);
        registerChild(CHILD_SOURCE_PROFILE_LABEL, CCLabel.class);
        registerChild(CHILD_SOURCE_PROFILE, CCStaticTextField.class);
        registerChild(CHILD_ACTION_LABEL, CCLabel.class);
        registerChild(CHILD_ACTION, CCStaticTextField.class);
        registerChild(CHILD_ASSIGNMENTS_LABEL, CCLabel.class);
        registerChild(CHILD_ASSIGNMENTS, CCStaticTextField.class);
        registerChild(CHILD_TARGET_CONTEXT_LABEL, CCLabel.class);
        registerChild(CHILD_TARGET_CONTEXT, CCStaticTextField.class);
        registerChild(CHILD_TARGET_ENTITY_LABEL, CCLabel.class);
        registerChild(CHILD_TARGET_ENTITY, CCStaticTextField.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        if (name.equals(CHILD_SOURCE_CONTEXT_LABEL)) {
            child = (View)new CCLabel(this, name, null);
        } else if (name.equals(CHILD_SOURCE_CONTEXT)) {
            child = (View)new CCStaticTextField(this, name, null);
        } else if (name.equals(CHILD_SOURCE_PROFILE_LABEL)) {
            child = (View)new CCLabel(this, name, null);
        } else if (name.equals(CHILD_SOURCE_PROFILE)) {
            child = (View)new CCStaticTextField(this, name, null);
        } else if (name.equals(CHILD_ACTION_LABEL)) {
            child = (View)new CCLabel(this, name, null);
        } else if (name.equals(CHILD_ACTION)) {
            String action = (String)wm.getValue(CopyMoveWizard2View.CHILD_ACTION);
            String value = null;
            if (action.equals("0")) {
                value = "APOC.wiz.copy.copy";
            } else {
                value = "APOC.wiz.copy.move";
            }
            child = (View)new CCStaticTextField(this, name, value);
            ((CCStaticTextField)child).setValue(value, true);
        } else if (name.equals(CHILD_ASSIGNMENTS_LABEL)) {
            child = (View)new CCLabel(this, name, null);
        } else if (name.equals(CHILD_ASSIGNMENTS)) {
            String doAssignments = (String)wm.getValue(CopyMoveWizard2View.CHILD_ASSIGNMENTS);
            String value = null;
            if (doAssignments.equals("true")) {
                value = "APOC.wiz.6.yes";
            } else {
                value = "APOC.wiz.6.no";
            }
            child = (View)new CCStaticTextField(this, name, value);
            ((CCStaticTextField)child).setValue(value, true);
        } else if (name.equals(CHILD_TARGET_CONTEXT_LABEL)) {
            child = (View)new CCLabel(this, name, null);
        } else if (name.equals(CHILD_TARGET_CONTEXT)) {
            child = (View)new CCStaticTextField(this, name, null);
        } else if (name.equals(CHILD_TARGET_ENTITY_LABEL)) {
            child = (View)new CCLabel(this, name, null);
        } else if (name.equals(CHILD_TARGET_ENTITY)) {
            child = (View)new CCStaticTextField(this, name, null);
        } else {
        throw new IllegalArgumentException(
            "CopyMoveWizard4 : Invalid child name [" + name + "]");
        }
        return child;
    }


    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/profiles/CopyMoveWizard4.jsp";
    }

    public String getErrorMsg() {
        String emsg = null;
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        wm.setValue(wm.SUCCESS, "true");
        PolicyManager sourcePmgr = (PolicyManager) RequestManager.getRequest().getSession(false).getAttribute(wm.SOURCE_POLICYMGR);
        PolicyManager targetPmgr = (PolicyManager) RequestManager.getRequest().getSession(false).getAttribute(wm.TARGET_POLICYMGR);
        Profile profile =  (Profile)wm.getWizardValue(wm.SOURCE_PROFILE);
        Entity targetEntity = (Entity)wm.getWizardValue(wm.TARGET_ENTITY);
        Entity hostEntity = (Entity)wm.getWizardValue(wm.PROFILE_HOST_ENTITY);
        String sourceEntityId = hostEntity.getId();
        String targetEntityId = targetEntity.getId();
        String sourceEntityType = hostEntity.getPolicySourceName();
        String targetEntityType = targetEntity.getPolicySourceName();
        LinkedList syncList = new LinkedList();
        syncList.add(profile.getId());
        syncList.add(targetEntityId + "|"  + targetEntityType);
        String action = (String)wm.getWizardValue(CopyMoveWizard2View.CHILD_ACTION);
        String doAssignments = (String)wm.getValue(CopyMoveWizard2View.CHILD_ASSIGNMENTS);
        boolean considerAssignments = false;
        String sTargetProfile = "";
        if (doAssignments.equals("true")) {
            considerAssignments = true;
        }  
        ArrayList involvedEntities = new ArrayList();
        involvedEntities.add(targetEntity);
        involvedEntities.add(hostEntity);
        try {
            ContextTiledModel model = new ContextTiledModel();
            String sourceContextName = (String)sourcePmgr.getEnvironment().get(Constants.POLICY_MANAGER_NAME);
            String targetContextName = (String)targetPmgr.getEnvironment().get(Constants.POLICY_MANAGER_NAME);
            Iterator entities = null;
            if (considerAssignments && sourceContextName.equals(targetContextName)) {
                // Test if new location of the profile does conflict with entities
                // already assigned to that profile.
                // Only entities at and below the storage location (applicable from)
                // of the profile are allowed to be assigned to the profile
                boolean  bRuleBreach= false;
                try {
                    entities   = profile.getAssignedEntities();
                } catch (SPIException ex) {
                    CCDebug.trace3(ex.toString());
                }
                while (entities.hasNext()&&!bRuleBreach) {
                    Entity entity = (Entity) entities.next();
                    involvedEntities.add(entity);
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
                
                
                if (bRuleBreach) {
                    String displayName = profile.getDisplayName();
                    String targetEntityName = Toolbox2.getParentagePath(
                                                targetEntity,
                                                false,
                                                true,
                                                ">",
                                                targetPmgr,
                                                true);

                    String actionString = "";
                    if (action.equals("0")) {
                        actionString = "APOC.wiz.copy.results.copied.lc";                          
                    } else {
                        actionString = "APOC.wiz.copy.results.moved.lc";                             
                    }
                    Object[] args = {displayName, actionString, targetEntityName};
                    String alert = m_I18n.getMessage("APOC.wiz.copy.4.alert", args);

                    return alert;
                }
            }
            sTargetProfile = model.sync(sourcePmgr, sourceEntityId, sourceEntityType, targetPmgr, targetEntityId, targetEntityType, syncList, getSession(), false, considerAssignments);
        } catch (ModelControlException mce) {
            CCDebug.trace3(mce.toString());
            setFailureMessage(m_I18n.getMessage("APOC.wiz.copy.4.fail1"), mce.toString());
        } catch (SPIException spie) {
            CCDebug.trace3(spie.toString());
            setFailureMessage(m_I18n.getMessage("APOC.wiz.copy.4.fail1"), spie.toString());
        } catch (Exception e) {
            CCDebug.trace3(e.toString());
            setFailureMessage(m_I18n.getMessage("APOC.wiz.copy.4.fail1"), e.toString());            
        }
        
        
        if (action.equals("1")) {
            try {
                ProfileModel.destroy(profile.getId(), sourcePmgr);
            } catch (ModelControlException mce) {
                CCDebug.trace3(mce.toString());
                setFailureMessage(m_I18n.getMessage("APOC.wiz.copy.4.fail2"), mce.toString());
            }
        }

        if (action.equals("0") && (targetEntity.getId().equals(hostEntity.getId()))) {
            try {
                Profile newProfile = targetPmgr.getProfile(sTargetProfile);
                ProfileRepository profileRep = targetEntity.getProfileRepository();
                for (int i=1; i < 100; i++) {
                    Object[] args = {Integer.toString(i), profile.getDisplayName()};    
                    String newDisplayName = m_I18n.getMessage("APOC.wiz.copy.copyof", args);
                    if (profileRep.findProfile(newDisplayName) == null) {
                        newProfile.setDisplayName(newDisplayName);
                        break;
                    }
                }
            } catch (SPIException spie) {
                CCDebug.trace3(spie.toString());
                setFailureMessage(m_I18n.getMessage("APOC.wiz.copy.4.fail3"), spie.toString());
            }
        }
        try {
            Profile targetProfile = targetPmgr.getProfile(sTargetProfile);
            targetEntity.assignProfile(targetProfile);
        } catch (SPIException spie) {
            CCDebug.trace3(spie.toString());
            setFailureMessage(m_I18n.getMessage("APOC.wiz.copy.4.fail4"), spie.toString());
        }
        
        try {
            Iterator updateEntities = involvedEntities.iterator();
            while (updateEntities.hasNext()) {
                Entity entity = (Entity)updateEntities.next();
                NavigationModel navModel = (NavigationModel) RequestManager.getRequestContext().getModelManager().
                                            getModel(NavigationModel.class, "NavTree", true, true);
                navModel.setAssignedState(entity, entity.getAssignedProfiles().hasNext());            
            } 
        } catch (SPIException spie) {
            CCDebug.trace3(spie.toString());
            setFailureMessage(m_I18n.getMessage("APOC.wiz.copy.4.fail4"), spie.toString());
        }        
        return emsg;
    }
    
    private void setFailureMessage(String error, String cause) {
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        if (((String)wm.getValue(wm.SUCCESS)).equals("true")) {
            wm.setValue(wm.SUCCESS, "false");
            wm.setValue(wm.FAIL_MESSAGE, error + "<br>- " + cause);
        }
    }    
}


