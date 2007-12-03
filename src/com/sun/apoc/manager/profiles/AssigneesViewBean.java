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
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.AlertViewBean;
import com.sun.apoc.manager.ProfileWindowViewBean;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.web.ui.model.CCActionTableModel;
import com.sun.web.ui.model.CCActionTableModelInterface;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.table.CCActionTable;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
public class AssigneesViewBean extends RequestHandlingViewBase {
    
    public static final String CHILD_PAGETITLE          = "PageTitle";
    public static final String CHILD_ALERT              = "Alert";
    public static final String CHILD_STACKTRACE         = "StackTrace";
    public static final String CHILD_TABLE              = "ActionTable";
    public static final String CHILD_ENTITYID_HIDDEN    = "AssigneeEntityId";
    public static final String CHILD_ENTITYTYPE_HIDDEN  = "AssigneeEntityType";
    public static final String CHILD_ENTITYID_TEXT      = "JSEntityId";
    public static final String CHILD_ENTITYTYPE_TEXT    = "JSEntityType";
    public static final String CHILD_READONLY           = "JSReadOnly";
    public static final String CHILD_ASSIGN_HREF        = "AssignHref";
    public static final String ASSIGNEES_MODEL          = "AssigneesModel"; 
    
    private CCPageTitleModel m_titleModel   = null;
    private CCActionTableModel m_tableModel = null;
    private ProfileWindowModel mEditorModel = null;
    
    public AssigneesViewBean(View parent, String name) {
        super(parent, name);
        registerChildren();
    }
    
    public void handleAssignHrefRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        try {
            String sEntityId    = (String) ((CCHiddenField)getChild("AssigneeEntityId")).getValue();
            String sEntityType  = (String) ((CCHiddenField)getChild("AssigneeEntityType")).getValue();
            //TPF_TODO: don't like downcasts
            ((AssigneesTableModel)getTableModel()).assign(Toolbox2.decode(sEntityId), sEntityType);
            ((AssigneesTableModel)getTableModel()).update();
        }
        catch (ModelControlException mce) {
            Toolbox2.prepareErrorDisplay(mce,
            getChild(CHILD_ALERT),
            getChild(CHILD_STACKTRACE));
        }
        getRootView().forwardTo(getRequestContext());
    }
    
    public void handleUnassignButtonRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        try {
            ((CCActionTable) getChild(CHILD_TABLE)).restoreStateData();
            updateSelections();
            //TPF_TODO: don't like downcasts
            ((AssigneesTableModel)getTableModel()).unassign();
            ((AssigneesTableModel)getTableModel()).update();
        }
        catch (ModelControlException mce) {
            Toolbox2.prepareErrorDisplay(mce,
            getChild(AlertViewBean.CHILD_ALERT),
            getChild(AlertViewBean.CHILD_STACKTRACE));
        }
        getRootView().forwardTo(getRequestContext());
    }
    
    public void handleCloseButtonRequest(RequestInvocationEvent event)
                throws ServletException, IOException {
        CCStaticTextField child = (CCStaticTextField)((ProfileWindowViewBean)getViewBean(ProfileWindowViewBean.class)).getChild(ProfileWindowViewBean.CHILD_TAB_CHANGE_SCRIPT);
        child.setValue("closeEditorAndRefresh();");
        getRootView().forwardTo(getRequestContext());
    }  
        
    protected void registerChildren() {
        registerChild(CHILD_PAGETITLE,          CCPageTitle.class);
        registerChild(CHILD_ALERT,              CCAlertInline.class);
        registerChild(CHILD_STACKTRACE,         CCStaticTextField.class);
        registerChild(CHILD_TABLE,              CCActionTable.class);
        registerChild(CHILD_ENTITYID_HIDDEN,    CCHiddenField.class);
        registerChild(CHILD_ENTITYTYPE_HIDDEN,  CCHiddenField.class);
        registerChild(CHILD_ENTITYID_TEXT,      CCStaticTextField.class);
        registerChild(CHILD_ENTITYTYPE_TEXT,    CCStaticTextField.class);
        registerChild(CHILD_READONLY,           CCStaticTextField.class);
        registerChild(CHILD_ASSIGN_HREF,        CCHref.class);
        getPageTitleModel().registerChildren(this);
        getTableModel().registerChildren(this);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_PAGETITLE)) {
            CCPageTitle child = new CCPageTitle(this, getPageTitleModel(), name);
            Toolbox2.setPageTitleHelp(getPageTitleModel(), "APOC.profilewnd.assignees.title", 
                                    "APOC.profilewnd.assignees.help", "gbgkr.html");           
            return child;
       
        } else if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
            return child;
        } else if (name.equals(CHILD_STACKTRACE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
                   
        } else if (name.equals(CHILD_TABLE)) {
            CCActionTable child = new CCActionTable(this, getTableModel(), name);
            return child;
            
        } else if (name.equals(CHILD_ENTITYID_TEXT)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            try {
                //TPF_TODO: move into model
                Profile profile = getEditorModel().getProfile();
                if (profile != null) {
                    Entity  entity  = profile.getProfileRepository().getEntity();
                    child.setValue(Toolbox2.encode(entity.getId()));
                }
            } catch (SPIException se) {
                throw new RuntimeException(se);
            }
            return child;
            
        } else if (name.equals(CHILD_READONLY)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
            
        } else if (name.equals(CHILD_ENTITYTYPE_TEXT)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            //TPF_TODO: move into model
            Profile         profile = getEditorModel().getProfile();
            if (profile != null) {
                Applicability use = profile.getApplicability();
            
                if (use.equals(Applicability.HOST)) {
                    child.setValue(EnvironmentConstants.HOST_SOURCE);
                } else if (use.equals(Applicability.USER)){
                    child.setValue(EnvironmentConstants.USER_SOURCE);
                }
            }
            
            return child;
            
        } else if (name.equals(CHILD_ASSIGN_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
            
        } else if (getPageTitleModel().isChildSupported(name)) {
            return getPageTitleModel().createChild(this, name);
            
        } else if (getTableModel().isChildSupported(name)) {
            return getTableModel().createChild(this, name);
            
        } else if (name.equals(CHILD_ENTITYID_HIDDEN) || name.equals(CHILD_ENTITYTYPE_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;
            
        } else {
            throw new IllegalArgumentException(
            "Invalid child name [" + name + "]");
        }
    }
    
    public boolean beginActionTableDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCActionTable   table       = (CCActionTable) getChild(event.getChildName());
        boolean         bIsReadOnly = false;
        
        try {
            bIsReadOnly = Toolbox2.getSelectedEntity().getProfileRepository().isReadOnly();
        } catch (SPIException se) {
            throw new ServletException(se);
        }
        if (bIsReadOnly) {
            table.getModel().setSelectionType(CCActionTableModelInterface.NONE);
        }
        return true;
    }
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        return ((alert.getSummary() != null) && (alert.getSummary().length() > 0));
    }
    
    public boolean beginJSReadOnlyDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCStaticTextField   text        = (CCStaticTextField) getChild(CHILD_READONLY);
        boolean             bIsReadOnly = false;
        
        try {
            bIsReadOnly = Toolbox2.getSelectedEntity().getProfileRepository().isReadOnly();
        } catch (SPIException se) {
            throw new ServletException(se);
        }
        
        text.setValue(Boolean.toString(bIsReadOnly));
        return true;
    }
    
    private void updateSelections() {
        HttpServletRequest request         = getRequestContext().getRequest();
        Enumeration        aParamNamesEnum = request.getParameterNames();
        while (aParamNamesEnum.hasMoreElements()) {
            String sParamName = (String) aParamNamesEnum.nextElement();
            int    nOptionPos = sParamName.indexOf(CCActionTable.CHILD_SELECTION_CHECKBOX);
            
            if (nOptionPos != -1) {
                String sParamValue = request.getParameter(sParamName);
                String sRowNumber  = sParamName.substring(nOptionPos+CCActionTable.CHILD_SELECTION_CHECKBOX.length());
                if (sRowNumber.indexOf(".")==-1) {
                    getTableModel().setRowSelected(Integer.parseInt(sRowNumber), Boolean.valueOf(sParamValue).booleanValue());
                }
                
            }
        }
    }
    
    protected CCPageTitleModel getPageTitleModel() {
        if (m_titleModel == null) {
            m_titleModel = new CCPageTitleModel(
            RequestManager.getRequestContext().getServletContext(),
            "/jsp/profiles/AssigneesPageTitle.xml");
        }
        return m_titleModel;
    }
    
    protected CCActionTableModel getTableModel() {
        if (m_tableModel == null) {
            m_tableModel = new AssigneesTableModel();
        }
        return m_tableModel;
    }
    
    protected ProfileWindowModel getEditorModel() {
        if (mEditorModel == null) {
            mEditorModel = (ProfileWindowModel) getModel(ProfileWindowModel.class);
        }
        return mEditorModel;
    }
}

