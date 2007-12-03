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
import com.iplanet.jato.model.Model;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingViewBase;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCHiddenField;

import com.sun.web.ui.view.wizard.CCWizardPage;

import com.sun.web.ui.common.CCDebug;

import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.Toolbox2;

import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.profiles.ProfileRepository;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.web.ui.common.CCI18N;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class CopyMoveWizard3View extends RequestHandlingViewBase
    implements CCWizardPage, WizardImplDataUtil{

    // The "logical" name for this page.
    public static final String PAGE_NAME = "CopyMoveWizard3View";

    // Child view names (i.e. display fields).

    public static final String CHILD_CONTEXT_LABEL =
        "TargetContextLabel";
    public static final String CHILD_TARGET_CONTEXT =
        "TargetContext";
    public static final String CHILD_CONTEXT_BUTTON =
        "ContextBrowseButton";
    public static final String CHILD_ENTITY_LABEL =
        "TargetEntityLabel";
    public static final String CHILD_ENTITY =
        "TargetEntity";
    public static final String CHILD_ENTITY_BUTTON =
        "EntityBrowseButton";
    public static final String CHILD_HIDDEN_ENTITY =
        "HiddenSelectedTargetEntity";
    public static final String CHILD_HIDDEN_ENTITY_TYPE =
        "HiddenSelectedTargetEntityType";
    public static final String CHILD_HIDDEN_CONTEXT =
        "SelectedContext";
    private CCI18N m_I18n;
     
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public CopyMoveWizard3View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME); 
    }

    public CopyMoveWizard3View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);        
        registerChildren();
    }

    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_CONTEXT_LABEL, CCLabel.class);
        registerChild(CHILD_TARGET_CONTEXT, CCStaticTextField.class);
        registerChild(CHILD_CONTEXT_BUTTON, CCButton.class);
        registerChild(CHILD_ENTITY_LABEL, CCLabel.class);
        registerChild(CHILD_ENTITY, CCStaticTextField.class);
        registerChild(CHILD_ENTITY_BUTTON, CCButton.class);
        registerChild(CHILD_HIDDEN_ENTITY, CCHiddenField.class);
        registerChild(CHILD_HIDDEN_ENTITY_TYPE, CCHiddenField.class);
        registerChild(CHILD_HIDDEN_CONTEXT, CCHiddenField.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        if (name.equals(CHILD_CONTEXT_LABEL)) {
            child = (View)new CCLabel(this, name, null);
        } else if (name.equals(CHILD_TARGET_CONTEXT)) {
            String value = (String)wm.getWizardValue(CopyMoveWizard1View.CHILD_CONTEXT);
            child = (View)new CCStaticTextField(this, name, value);
        } else if (name.equals(CHILD_CONTEXT_BUTTON)) {
            child = (View)new CCButton(this, name, null);
        } else if (name.equals(CHILD_ENTITY_LABEL)) {
            child = (View)new CCLabel(this, name, null);            
        } else if (name.equals(CHILD_ENTITY)) {
            child = (View)new CCStaticTextField(this, name, null);
        } else if (name.equals(CHILD_ENTITY_BUTTON)) {
            child = (View)new CCButton(this, name, null);
        } else if (name.equals(CHILD_HIDDEN_ENTITY)) {
            child = (View)new CCHiddenField(this, name, "");                
        } else if (name.equals(CHILD_HIDDEN_ENTITY_TYPE)) {
            child = (View)new CCHiddenField(this, name, "");
        } else if (name.equals(CHILD_HIDDEN_CONTEXT)) {
            child = (View)new CCHiddenField(this, name, null);
        } else {
        throw new IllegalArgumentException(
            "CopyMoveWizard3View : Invalid child name [" + name + "]");
        }
        return child;
    }

    public void beginDisplay(DisplayEvent event) 
            throws ModelControlException {
            
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        PolicyManager sourcePmgr = (PolicyManager) RequestManager.getRequest().getSession(false).getAttribute(wm.SOURCE_POLICYMGR);
        if (sourcePmgr == null) {
            sourcePmgr = Toolbox2.getPolicyManager();
            RequestManager.getRequestContext().getRequest().getSession(true).setAttribute(wm.SOURCE_POLICYMGR, sourcePmgr);
        }
        PolicyManager targetPmgr = (PolicyManager) RequestManager.getRequest().getSession(false).getAttribute(wm.TARGET_POLICYMGR);
        if (targetPmgr == null) {
            targetPmgr = (PolicyManager) RequestManager.getRequest().getSession(false).getAttribute(wm.SOURCE_POLICYMGR);
            RequestManager.getRequestContext().getRequest().getSession(true).setAttribute(wm.TARGET_POLICYMGR, targetPmgr);
        }
        
        try {
            String context = (String)targetPmgr.getEnvironment().get(Constants.POLICY_MANAGER_NAME);
            CCStaticTextField contextNameField = (CCStaticTextField)getChild(CHILD_TARGET_CONTEXT);
            contextNameField.setValue(context);
            CCButton selectContextBtn = (CCButton)getChild(CHILD_CONTEXT_BUTTON);
            selectContextBtn.setExtraHtml("onClick=\"javascript:openSelectContextWindow('" + context + "','" + wm.TARGET_POLICYMGR + "');return false;\"");
            CCHiddenField hiddenContext    = (CCHiddenField) getChild(CHILD_HIDDEN_CONTEXT);
            hiddenContext.setValue(context);
        } catch (SPIException e) {
            CCDebug.trace3(e.toString());
        }
        
        String sourceContextName = "";
        String targetContextName = "";
        try {
            sourceContextName = (String)sourcePmgr.getEnvironment().get(Constants.POLICY_MANAGER_NAME);
            targetContextName = (String)targetPmgr.getEnvironment().get(Constants.POLICY_MANAGER_NAME);
        } catch (SPIException e) {
            CCDebug.trace3(e.toString());
        }
        CCStaticTextField entityName = (CCStaticTextField)getChild(CHILD_ENTITY);

        CCStaticTextField selectedEntity = (CCStaticTextField) getChild(CHILD_ENTITY);
        CCHiddenField hiddenEntity    = (CCHiddenField) getChild(CHILD_HIDDEN_ENTITY);
        CCHiddenField hiddenEntityType= (CCHiddenField) getChild(CHILD_HIDDEN_ENTITY_TYPE);
        String entityId = Toolbox2.decode((String)hiddenEntity.getValue());
        String entityType = Toolbox2.decode((String)hiddenEntityType.getValue());
        if ((entityId != null) && (entityId.length() != 0)) {
            try {
                Entity entity = targetPmgr.getEntity(entityType, entityId);
                String entityPath = Toolbox2.getParentagePath(
                                        entity,
                                        false,
                                        true,
                                        ">",
                                        targetPmgr,
                                        true);
                selectedEntity.setValue(entityPath);
                wm.setValue(wm.TARGET_ENTITY, entity);
                hiddenEntity.setValue(Toolbox2.encode(entity.getId()));
                hiddenEntityType.setValue(Toolbox2.encode(entity.getPolicySourceName()));
            } catch (SPIException spie) {
                CCDebug.trace3(spie.toString());
            }
        } else {
            String action = (String)wm.getWizardValue(CopyMoveWizard2View.CHILD_ACTION);
            String entityPath = "";
            Entity hostEntity = (Entity)wm.getWizardValue(wm.PROFILE_HOST_ENTITY);
            if (action.equals("0") && (sourceContextName.equals(targetContextName))) {
                entityPath = Toolbox2.getParentagePath(
                                        hostEntity,
                                        false,
                                        true,
                                        ">",
                                        sourcePmgr,
                                        true);
                wm.setValue(wm.TARGET_ENTITY, hostEntity);
                hiddenEntity.setValue(Toolbox2.encode(hostEntity.getId()));
                hiddenEntityType.setValue(Toolbox2.encode(hostEntity.getPolicySourceName()));
                selectedEntity.setValue(entityPath);
            } else {           
                hiddenEntity.setValue("");
                hiddenEntityType.setValue("");
                selectedEntity.setValue("");
            }
        }
    }
    
    public boolean beginEntityBrowseButtonDisplay(ChildDisplayEvent event) 
            throws ModelControlException, SPIException {
        CCButton child = (CCButton) getChild("EntityBrowseButton");
        StringBuffer buffer = new StringBuffer();
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        PolicyManager targetPmgr = (PolicyManager) RequestManager.getRequest().getSession(false).getAttribute(wm.TARGET_POLICYMGR);
        String contextId = Toolbox2.encode((String)wm.getWizardValue(CHILD_TARGET_CONTEXT));
        Profile profile = (Profile)wm.getValue(wm.SOURCE_PROFILE); 
        String sourceName = profile.getApplicability().getStringValue();
        ArrayList sources = new ArrayList(Arrays.asList(targetPmgr.getSources()));
        String entityId = null;
        if (sources.contains(sourceName)) {
            entityId = Toolbox2.encode(targetPmgr.getRootEntity(sourceName).getId());
        } else {
            if (!sources.isEmpty()) {
                sourceName = "";//(String)sources.get(0);
                entityId = "";//Toolbox2.encode(targetPmgr.getRootEntity(sourceName).getId());
            }
        }
        String entityType = sourceName;
        buffer.append(" onClick=\"")
                .append("openWindow(window, null, '/apoc/manager/BrowseTreeIndex?EntityId=")
                .append(entityId)
                .append("&EntityType=")
                .append(entityType)
                .append("&ContextId=")
                .append(contextId)
                .append("', 'TreeWindow', 500, 600, true);") 
                .append("return false;\"");
        child.setExtraHtml(buffer.toString());
        return true;
    }

    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/profiles/CopyMoveWizard3.jsp";
    }

    public String getErrorMsg() {
        String emsg = null;
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        Entity targetEntity = (Entity)wm.getWizardValue(wm.TARGET_ENTITY);
        Entity hostEntity = (Entity)wm.getWizardValue(wm.PROFILE_HOST_ENTITY);
        PolicyManager targetPmgr = (PolicyManager) RequestManager.getRequest().getSession(false).getAttribute(wm.TARGET_POLICYMGR);
        String action = (String)wm.getWizardValue(CopyMoveWizard2View.CHILD_ACTION);
        String actionString = "";
        if (action.equals("0")) {
            actionString = "APOC.wiz.copy.results.copied.lc";                          
        } else {
            actionString = "APOC.wiz.copy.results.moved.lc";                             
        }
        String targetEntityName = Toolbox2.getParentagePath(
                                targetEntity,
                                false,
                                true,
                                ">",
                                targetPmgr,
                                true);        

        try {
            if (targetEntity == null) {
                emsg = "APOC.wiz.copy.3.alert";
            } else if (targetEntity.getProfileRepository().isReadOnly()) {
                String username = (String)targetPmgr.getEnvironment().get(EnvironmentConstants.USER_KEY);
                String profile = ((Profile)wm.getWizardValue(wm.SOURCE_PROFILE)).getDisplayName();
                Object[] args = {username, profile, actionString, targetEntityName};
                emsg = m_I18n.getMessage("APOC.wiz.copy.2.alert", args);
            } else {
                if (action.equals("1") && (targetEntity.getId().equals(hostEntity.getId()))) {
                    emsg = "APOC.wiz.copy.3.alert1";
                }
                Profile profile =  (Profile)wm.getWizardValue(wm.SOURCE_PROFILE);
                ProfileRepository profileRep = targetEntity.getProfileRepository();
                String displayName = profile.getDisplayName();
                if (!(targetEntity.getId().equals(hostEntity.getId()))) {
                    if (profileRep.findProfile(displayName) != null) {
                        Object[] args = {displayName, actionString, targetEntityName};
                        emsg = m_I18n.getMessage("APOC.wiz.copy.3.alert2", args);
                    }
                }  
            }
        } catch (SPIException spie) {
            CCDebug.trace3(spie.toString());
        }
        return emsg;
    }
}


