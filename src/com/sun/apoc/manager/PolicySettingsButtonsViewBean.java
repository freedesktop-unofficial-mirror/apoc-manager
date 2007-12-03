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


package com.sun.apoc.manager;

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.profiles.AdvancedOptionsViewBean;
import com.sun.apoc.manager.profiles.GeneralSettingsViewBean;
import com.sun.apoc.manager.profiles.ProfileWindowModel;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCStaticTextField;
import java.io.IOException;
import javax.servlet.ServletException;
public class PolicySettingsButtonsViewBean extends ViewBeanBase {

    public static final String PAGE_NAME           = "PolicySettingsButtons";
    public static final String DEFAULT_DISPLAY_URL = "/jsp/settings/Buttons.jsp";
    public static final String CHILD_SAVE_BUTTON   = "SaveButton";
    public static final String CHILD_CLOSE_BUTTON  = "CloseButton";
    public static final String CHILD_FORM          = "Form";
    public static final String CHILD_JS_ALERT1         = "Alert1";
    public static final String CHILD_JS_ALERT2         = "Alert2";
    public static final String CHILD_JS_ALERT3         = "Alert3";
    public static final String CHILD_JS_ALERT4         = "Alert4";
    public static final String CHILD_JS_ALERT5         = "Alert5";
    
    private ProfileWindowModel mEditorModel = null;
    private CCI18N m_I18n = null;
    public PolicySettingsButtonsViewBean() {
    	super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        registerChildren();
    }

    protected void registerChildren() {
        registerChild(CHILD_SAVE_BUTTON, CCButton.class);
        registerChild(CHILD_CLOSE_BUTTON, CCButton.class);
        registerChild(CHILD_FORM, CCForm.class);
        registerChild(CHILD_JS_ALERT1, CCStaticTextField.class);
        registerChild(CHILD_JS_ALERT2, CCStaticTextField.class);
        registerChild(CHILD_JS_ALERT3, CCStaticTextField.class);
        registerChild(CHILD_JS_ALERT4, CCStaticTextField.class);
        registerChild(CHILD_JS_ALERT5, CCStaticTextField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_SAVE_BUTTON)) {
            CCButton child = new CCButton(this, name, null);
            return child;
            
        } else if (name.equals(CHILD_CLOSE_BUTTON)) {
            CCButton child = new CCButton(this, name, null);
            return child;
        
        } else if (name.equals(CHILD_FORM)) {
            CCForm child = new CCForm(this, name);
            return child;
        
        } else if (name.equals(CHILD_JS_ALERT1)) {
            CCStaticTextField child = new CCStaticTextField(this, name, m_I18n.getMessage("APOC.profilewnd.js.alert1"));
            return child;          
        
        } else if (name.equals(CHILD_JS_ALERT2)) {
            CCStaticTextField child = new CCStaticTextField(this, name, m_I18n.getMessage("APOC.profilewnd.js.alert2"));
            return child;          
        
        } else if (name.equals(CHILD_JS_ALERT3)) {
            CCStaticTextField child = new CCStaticTextField(this, name, m_I18n.getMessage("APOC.profilewnd.js.alert3"));
            return child;          
        
        } else if (name.equals(CHILD_JS_ALERT4)) {
            CCStaticTextField child = new CCStaticTextField(this, name, m_I18n.getMessage("APOC.profilewnd.js.alert4"));
            return child; 
        
        } else if (name.equals(CHILD_JS_ALERT5)) {
            CCStaticTextField child = new CCStaticTextField(this, name, m_I18n.getMessage("APOC.profilewnd.js.alert5"));
            return child; 
            
        } else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public boolean beginSaveButtonDisplay(ChildDisplayEvent event) 
            throws ModelControlException {        
        if (getEditorModel().getProfile() == null) {
            return false;
        } else { 
            // do not show the save button for read-only profiles
            if (getEditorModel().isReadOnlyProfile()) {
                return false;
            } else {
                String selectedTab = getEditorModel().getSelectedTab();
                return ( (selectedTab.equals(ProfileWindowModel.GENERAL_TAB)) || 
                        (selectedTab.equals(ProfileWindowModel.SETTINGS_TAB)) || 
                        (selectedTab.equals(ProfileWindowModel.ADVANCED_TAB)));
            }
        }
    }
        
/*    public void handleCloseButtonRequest(RequestInvocationEvent event)
                throws ServletException, IOException {

        CCStaticTextField child = (CCStaticTextField)((ProfileWindowViewBean)getViewBean(ProfileWindowViewBean.class)).getChild(ProfileWindowViewBean.CHILD_TAB_CHANGE_SCRIPT);
        child.setValue("closeEditorAndRefresh()");
        
        String selectedTab = getEditorModel().getSelectedTab();
        try {
            mapRequestParameters(getRequestContext().getRequest());
            if (selectedTab.equals(ProfileWindowModel.GENERAL_TAB)) {
                ((GeneralSettingsViewBean)getViewBean(GeneralSettingsViewBean.class)).handleSaveButtonRequest(event);
            } else if (selectedTab.equals(ProfileWindowModel.SETTINGS_TAB)) {
                ((PolicySettingsContentViewBean)getViewBean(PolicySettingsContentViewBean.class)).handleSaveButtonRequest(event);
            } else if (selectedTab.equals(ProfileWindowModel.ADVANCED_TAB)) {
                ((AdvancedOptionsViewBean)getViewBean(AdvancedOptionsViewBean.class)).handleDefaultCommandRequest(event);
            }
        } catch (ModelControlException ex) {
            CCDebug.trace3(ex.toString());
        }  
    }  */

    private ProfileWindowModel getEditorModel() {
        if (mEditorModel == null) {
            mEditorModel = (ProfileWindowModel) getModel(ProfileWindowModel.class);
        }
        return mEditorModel;
    }
}    