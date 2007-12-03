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
import com.iplanet.jato.view.DisplayField;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.html.OptionList;
import com.sun.apoc.manager.ProfileWindowFramesetViewBean;
import com.sun.apoc.manager.ProfileWindowViewBean;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.model.CCOrderableListModel;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.model.CCPropertySheetModel;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.orderablelist.CCOrderableList;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.propertysheet.CCPropertySheet;
import java.io.IOException;
import java.util.LinkedList;
import javax.servlet.ServletException;

public class AdvancedOptionsViewBean extends RequestHandlingViewBase {
    
    public static final String CHILD_ALERT              = "Alert";
    public static final String CHILD_STACKTRACE         = "StackTrace";
    public static final String CHILD_PAGETITLE          = "PageTitle";
    public static final String CHILD_PROPERTIES         = "Properties";
    public static final String CHILD_PRIO_LIST          = "PrioritizationList";
    public static final String CHILD_RELOCATE_BUTTON    = "RelocateButton";
    public static final String CHILD_DEFAULT_COMMAND    = "DefaultCommand";
    public static final String CHILD_ENTITYID_HIDDEN    = "GeneralEntityId";
    public static final String CHILD_ENTITY_TYPE_HIDDEN = "GeneralEntityType";
    public static final String CHILD_ENTITYID_TEXT      = "JSEntityId";
    public static final String CHILD_ENTITYTYPE_TEXT    = "JSEntityType";
    public static final String CHILD_READONLY           = "JSReadOnly";
    public static final String APPLICABLE               = "Applicable";
    
    private CCPageTitleModel        m_titleModel        = null;
    private CCPropertySheetModel    m_propertySheetModel= null;
    private ProfileWindowModel      m_editorModel       = null;
    private PrioritizationListModel m_listModel         = null;
    
    
    public AdvancedOptionsViewBean(View parent, String name) {
        super(parent, name);
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_ALERT,              CCAlertInline.class);
        registerChild(CHILD_STACKTRACE,         CCStaticTextField.class);
        registerChild(CHILD_PAGETITLE,          CCPageTitle.class);
        registerChild(CHILD_PROPERTIES,         CCPropertySheet.class);
        registerChild(CHILD_PRIO_LIST,          CCOrderableList.class);
        registerChild(CHILD_RELOCATE_BUTTON,    CCButton.class);
        registerChild(CHILD_DEFAULT_COMMAND,    CCButton.class);
        registerChild(CHILD_ENTITYID_HIDDEN,    CCHiddenField.class);
        registerChild(CHILD_ENTITY_TYPE_HIDDEN,    CCHiddenField.class);
        registerChild(CHILD_ENTITYID_TEXT,      CCStaticTextField.class);
        registerChild(CHILD_ENTITYTYPE_TEXT,    CCStaticTextField.class);
        registerChild(CHILD_READONLY,           CCStaticTextField.class);
        getPageTitleModel().registerChildren(this);
        getPropertiesModel().registerChildren(this);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_STACKTRACE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_PAGETITLE)) {
            CCPageTitle child = new CCPageTitle(this, getPageTitleModel(), name);
            Toolbox2.setPageTitleHelp(getPageTitleModel(), "APOC.profilewnd.advanced.tab", 
                                    "APOC.profilewnd.advanced.pagetitle_help", "gbgdh.html");
            return child;
            
        } else if (name.equals(CHILD_PROPERTIES)) {
            CCPropertySheet child = new CCPropertySheet(this, getPropertiesModel(), name);
            return child;
            
        } else if (name.equals(CHILD_PRIO_LIST)) {
            CCOrderableList child = new CCOrderableList(this, new CCOrderableListModel(), name);
            return child;
            
        } else if (name.equals(CHILD_RELOCATE_BUTTON) || name.equals(CHILD_DEFAULT_COMMAND)) {
            CCButton child = new CCButton(this, name, null);
            return child;
            
        } else if (name.equals(CHILD_ENTITYID_TEXT) || 
        name.equals(CHILD_ENTITYTYPE_TEXT) || 
        name.equals(CHILD_READONLY)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
            
        } else if (name.equals(CHILD_ENTITYID_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;
        
        } else if (name.equals(CHILD_ENTITY_TYPE_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;
            
        } else if (getPageTitleModel().isChildSupported(name)) {
            return getPageTitleModel().createChild(this, name);
            
        } else if (getPropertiesModel().isChildSupported(name)) {
            return getPropertiesModel().createChild(this, name);
            
        } else {
            throw new IllegalArgumentException(
            "Invalid child name [" + name + "]");
        }
    }
    
    protected CCPageTitleModel getPageTitleModel() {
        if (m_titleModel == null) {
            m_titleModel = new CCPageTitleModel(
            RequestManager.getRequestContext().getServletContext(),
            "/jsp/profiles/AdvancedOptionsPageTitle.xml");
        }
        return m_titleModel;
    }
    
    protected CCPropertySheetModel getPropertiesModel() {
        if (m_propertySheetModel == null) {
            m_propertySheetModel = new CCPropertySheetModel(
            RequestManager.getRequestContext().getServletContext(),
            "/jsp/profiles/AdvancedOptionsPropertySheet.xml");
        }
        return m_propertySheetModel;
    }
    
    private ProfileWindowModel getEditorModel() {
        if (m_editorModel == null) {
            m_editorModel = (ProfileWindowModel) getModel(ProfileWindowModel.class);
        }
        return m_editorModel;
    }

    private PrioritizationListModel getListModel() {
        if (m_listModel == null) {
            m_listModel = (PrioritizationListModel) getModel(PrioritizationListModel.class);
        }
        return m_listModel;
    }

    public void handleRelocateButtonRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        try {
            String sSourceProfile = getEditorModel().getProfile().getId();
            String sTargetEntity  = Toolbox2.decode((String) ((CCHiddenField)getChild(CHILD_ENTITYID_HIDDEN)).getValue());
            String sTargetEntityType  = Toolbox2.decode((String) ((CCHiddenField)getChild(CHILD_ENTITY_TYPE_HIDDEN)).getValue());
            String sTargetProfile = ProfileModel.copy(sSourceProfile, sTargetEntityType, sTargetEntity, true);
            
            if (sTargetProfile.length()==0) {
                CCAlertInline     alert     = (CCAlertInline) getChild(CHILD_ALERT);
                CCStaticTextField trace     = (CCStaticTextField) getChild(CHILD_STACKTRACE);
                alert.setValue(CCAlert.TYPE_ERROR);
                alert.setSummary("APOC.relocate.error.summary");
                alert.setDetail("APOC.relocate.error.detail");
                trace.setValue("APOC.relocate.trace.text");
            } else {
                ProfileModel.destroy(sSourceProfile, Toolbox2.getPolicyManager());
                getEditorModel().setProfile(Toolbox2.getPolicyManager().getProfile(sTargetProfile));
            }
        } catch (Exception e) {
            Toolbox2.prepareErrorDisplay(e, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE));
        }
        
        getRootView().forwardTo(getRequestContext());
    }
    
    public void handleSaveButtonRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        try {
            CCOrderableList list            = (CCOrderableList) getChild(CHILD_PRIO_LIST);
            OptionList      options         = getListModel().getSelectedOptionList(list);
            int             nOptionRunner   = 0;
            LinkedList      valueList       = new LinkedList();

            while (nOptionRunner<options.size()) {
                valueList.add(options.getValue(nOptionRunner));
                nOptionRunner++;
            }

            ProfileModel.changePriority(valueList);
            
        } catch (Exception e) {
            Toolbox2.prepareErrorDisplay(e, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE));
        }
        
        getRootView().forwardTo(getRequestContext());
    }
        
    public void handleCloseButtonRequest(RequestInvocationEvent event)
                throws ServletException, IOException {
        CCStaticTextField child = (CCStaticTextField)((ProfileWindowViewBean)getViewBean(ProfileWindowViewBean.class)).getChild(ProfileWindowViewBean.CHILD_TAB_CHANGE_SCRIPT);
        child.setValue("closeEditorAndRefresh();");
        try {
            mapRequestParameters(getRequestContext().getRequest());
            handleSaveButtonRequest(event);
        } catch (ModelControlException ex) {
            CCDebug.trace3(ex.toString());
        } 

    }  
        
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        try {
            Profile profile = ProfileWindowFramesetViewBean.getSelectedProfile();
            if (profile != null) {
                Entity entity = profile.getProfileRepository().getEntity();
                
                StringBuffer sPath = new StringBuffer(Toolbox2.getParentagePath(entity, true, true, "/"));
                m_propertySheetModel.setValue(APPLICABLE, sPath);
                
                //TPF_TODO: move into model
                DisplayField child = (DisplayField) getChild(CHILD_ENTITYID_TEXT);
                if (profile.getApplicability().equals(Applicability.USER)) {
                    child.setValue(Toolbox2.encode(Toolbox2.getPolicyManager().getRootEntity(EnvironmentConstants.USER_SOURCE).getId()));
                } else if (profile.getApplicability().equals(Applicability.HOST)) {
                    child.setValue(Toolbox2.encode(Toolbox2.getPolicyManager().getRootEntity(EnvironmentConstants.HOST_SOURCE).getId()));
                }                 
                //TPF_TODO: move into model
                child = (DisplayField) getChild(CHILD_ENTITYTYPE_TEXT);
                if (profile.getApplicability().equals(Applicability.USER)) {
                    child.setValue(EnvironmentConstants.USER_SOURCE);
                } else if (profile.getApplicability().equals(Applicability.HOST)) {
                    child.setValue(EnvironmentConstants.HOST_SOURCE);
                } 
                
                CCOrderableList list = (CCOrderableList) getChild(CHILD_PRIO_LIST);
                list.resetStateData();
                getListModel().retrieve(entity);
                list.setModel(getListModel());
            }
        } catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        return ((alert.getSummary() != null) && (alert.getSummary().length() > 0));
    }

    public boolean beginChangeButtonDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCButton    button      = (CCButton) getChild(event.getChildName());
        boolean     bIsReadOnly = false;
        
        try {
            bIsReadOnly = Toolbox2.getSelectedEntity().getProfileRepository().isReadOnly();
        } catch (SPIException se) {
            throw new ServletException(se);
        }
        
        button.setDisabled(bIsReadOnly);
        return true;
    }
    
    public boolean beginJSReadOnlyDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCStaticTextField   text        = (CCStaticTextField) getChild(CHILD_READONLY);
        boolean             bIsReadOnly = getEditorModel().isReadOnlyProfile();
        text.setValue(Boolean.toString(bIsReadOnly));
        return true;
    }
    
    public boolean beginSaveButtonDisplay(ChildDisplayEvent event) 
            throws ModelControlException {        
        // do not show the save button for read-only profiles
        return !getEditorModel().isReadOnlyProfile();
    }
    
    public boolean beginMoveUpButtonDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        return false;
    }
}

