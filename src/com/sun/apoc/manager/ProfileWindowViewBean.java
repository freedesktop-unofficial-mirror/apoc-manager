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
import com.sun.apoc.manager.profiles.ProfileWindowModel;
import com.sun.apoc.manager.profiles.ProfileWindowPageletView;
import com.sun.apoc.spi.profiles.Profile;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBean;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.model.CCNavNode;
import com.sun.web.ui.model.CCTabsModel;
import com.sun.web.ui.model.CCTabsModelInterface;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.masthead.CCSecondaryMasthead;
import com.sun.web.ui.view.tabs.CCNodeEventHandlerInterface;
import com.sun.web.ui.view.tabs.CCTabs;
import com.sun.apoc.manager.profiles.GeneralSettingsViewBean;
import com.sun.apoc.spi.SPIException;
import com.sun.web.ui.common.CCI18N;

import java.io.IOException;
import javax.servlet.ServletException;


public class ProfileWindowViewBean extends ViewBeanBase 
        implements CCNodeEventHandlerInterface {

    public static final String PAGE_NAME           = "ProfileWindow";
    public static final String CHILD_MASTHEAD      = "SecondaryMH";
    public static final String CHILD_TABS          = "ProfileWindowTabs";
    public static final String CHILD_FORM          = "ProfileForm";
    public static final String CHILD_ALERT         = "Alert";
    public static final String CHILD_PAGELET_VIEW  = "TabContent";
    public static final String CHILD_DEFAULT_CMD   = "DefaultCommand";
    public static final String CHILD_TITLE         = "Title";
    public static final String CHILD_TAB_CHANGE_SCRIPT = "TabChangeScript";
    public static final String DEFAULT_DISPLAY_URL = "/jsp/profiles/ProfileWindow.jsp";
    
    private CCTabsModelInterface m_tabsModel = null;
    private ProfileWindowModel mModel = null;
    private CCI18N m_I18n = null;
        
    public ProfileWindowViewBean() {
    	super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
    }

    protected void registerChildren() {
        registerChild(CHILD_MASTHEAD, CCSecondaryMasthead.class);
        registerChild(CHILD_TABS, CCTabs.class);
        registerChild(CHILD_FORM, CCForm.class);
        registerChild(CHILD_ALERT, CCAlertInline.class);
        registerChild(CHILD_TITLE, CCStaticTextField.class);
        registerChild(CHILD_TAB_CHANGE_SCRIPT, CCStaticTextField.class);
        registerChild(CHILD_PAGELET_VIEW, ProfileWindowPageletView.class);
        registerChild(CHILD_DEFAULT_CMD, CCHref.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_MASTHEAD)) {
            CCSecondaryMasthead child = new CCSecondaryMasthead(this, CHILD_MASTHEAD);
            child.setHeight("21");
            child.setWidth("355");
            child.setSrc("/apoc/images/popuptitle.gif");
	        return child;
            
        } else if (name.equals(CHILD_TABS)) {
            CCTabs child = new CCTabs(this, getTabsModel(), name);
            return child;
        
        } else if (name.equals(CHILD_TITLE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, "");
            return child;    
        
        } else if (name.equals(CHILD_TAB_CHANGE_SCRIPT)) {
            CCStaticTextField child = new CCStaticTextField(this, name, "");
            return child;  
            
        } else if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
            return child;        
        
        } else if (name.equals(CHILD_DEFAULT_CMD)) {
            CCHref child = new CCHref(this, name, "");
            return child;
        
        } else if (name.equals(CHILD_FORM)) {
            CCForm child = new CCForm(this, name);
            return child;
            
        } else if (name.equals(CHILD_PAGELET_VIEW)) {
            ProfileWindowPageletView child = new ProfileWindowPageletView(this, name);
            return child;

        } else {
            throw new IllegalArgumentException( 
                "Invalid child name [" + name + "]");
        }
    }
    
    protected CCTabsModelInterface getTabsModel() {
        if (m_tabsModel == null) {
          m_tabsModel = new CCTabsModel();
          CCNavNode settingsTab  = new CCNavNode(1, "APOC.profilewnd.settings.tab", "", "");
          CCNavNode assigneesTab = new CCNavNode(2, "APOC.profilewnd.assignees.tab", "", "");
          CCNavNode generalTab   = new CCNavNode(3, "APOC.profilewnd.general.tab", "", "");
          CCNavNode summaryTab   = new CCNavNode(4, "APOC.report.settings.summary.title", "", "");
          CCNavNode advancedTab  = new CCNavNode(5, "APOC.profilewnd.advanced.tab", "", "");
          m_tabsModel.addNode(generalTab);
          m_tabsModel.addNode(settingsTab);
          m_tabsModel.addNode(assigneesTab);
          m_tabsModel.addNode(summaryTab);
          m_tabsModel.addNode(advancedTab);
        }
        return m_tabsModel;
    }
    
    public void nodeClicked(RequestInvocationEvent event, int id) {
        getModel().setSelectedTab(new Integer(id).toString());
        CCStaticTextField child = (CCStaticTextField) getChild(CHILD_TAB_CHANGE_SCRIPT);
        child.setValue("parent.location.href='../manager/ProfileWindowFrameset?SelectedEditorTab="+id+"&com_sun_web_ui_popup=true&';");
        forwardTo(getRequestContext());
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        // set the currently selected tab
        try {
            int id = Integer.parseInt(getModel().getSelectedTab());
            getTabsModel().setSelectedNode(id);
        } catch (Exception e) {
            CCDebug.trace1("Could not set selected tab: " + e.getMessage());
        }
        
        // set the onClick() event handler
        for (int i = 1; i <= getTabsModel().getNodeCount(); i++) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("return switchToTab(");
            buffer.append(i);
            buffer.append(");");
            getTabsModel().getNodeById(i).setOnClick(buffer.toString());
        }
    }
    
    
    public void endDisplay(DisplayEvent event) {
        // reset any error/info messages
        getModel().setErrorMessage(null, "");
    }
    
    public void handleDefaultCommandRequest(RequestInvocationEvent event) 
            throws ServletException, IOException {
        ProfileWindowPageletView pagelet = (ProfileWindowPageletView) getChild(CHILD_PAGELET_VIEW);
        GeneralSettingsViewBean general = (GeneralSettingsViewBean) pagelet.getChild(ProfileWindowPageletView.CHILD_GENERAL);
        general.handleSaveButtonRequest(event);
        forwardTo();
    }
    
    public boolean beginTitleDisplay(ChildDisplayEvent event) 
            throws ModelControlException {
        // set window title
        CCStaticTextField child = (CCStaticTextField) getChild(CHILD_TITLE);
        Profile pg = getModel().getProfile();
        if (pg != null) {
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            child.setValue(i18n.getMessage("APOC.profiles.profile") + " " + pg.getDisplayName());
        }
        return true;
    }
    
    public boolean beginAlertAreaDisplay(ChildDisplayEvent event) {
        if (getModel().hasAlert()) {
            CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
            alert.setType(getModel().getAlertType());
            alert.setSummary(getModel().getAlertMessage());
            alert.setDetail(getModel().getAlertDetails());
            return true;
        }
        
        boolean bIsReadOnly = false;
        
        try {
            bIsReadOnly = Toolbox2.getSelectedEntity().getProfileRepository().isReadOnly();
        } catch (SPIException se) {
            CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
            alert.setType(CCAlertInline.TYPE_ERROR);
            alert.setSummary(se.getLocalizedMessage(Toolbox2.getLocale()));
            return true;
        }
        
        if (bIsReadOnly) {
            CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
            alert.setType(CCAlertInline.TYPE_INFO);
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            alert.setSummary(i18n.getMessage("APOC.policies.info.readonly.access"));
        }
        
        return bIsReadOnly;
    }
    
    public boolean beginProfileWindowTabsDisplay(ChildDisplayEvent event) {
        return getModel().getProfile() != null;
    }
    
    public boolean beginTabContentDisplay(ChildDisplayEvent event) {
        return getModel().getProfile() != null;
    }
    
    private ProfileWindowModel getModel() {
        if (mModel == null) {
            mModel = (ProfileWindowModel) getModel(ProfileWindowModel.class);
        }
        return mModel;
    }
}
