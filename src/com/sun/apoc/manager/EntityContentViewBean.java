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
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.entities.MemberTableModel;
import com.sun.apoc.manager.entities.MemberTableView;
import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.apoc.manager.profiles.AssignedAboveTableModel;
import com.sun.apoc.manager.profiles.AssignedAboveTableView;
import com.sun.apoc.manager.profiles.AssignedTableModel;
import com.sun.apoc.manager.profiles.AssignedTableView;
import com.sun.apoc.spi.SPIException;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import java.io.IOException;
import java.util.Iterator;
import javax.servlet.ServletException;
public class EntityContentViewBean extends ViewBeanBase {
    public static final String PAGE_NAME                = "EntityContent";
    public static final String DEFAULT_DISPLAY_URL      = "/jsp/EntityContent.jsp";
    public static final String CHILD_DEFAULT_HREF       = "DefaultHref";
    public static final String CHILD_UPDATE_HREF        = "UpdateHref";
    public static final String CHILD_RESULT_BUTTON      = "ResultButton";
    public static final String CHILD_FORM               = "EntityContentForm";
    public static final String CHILD_ALERT              = "Alert";
    public static final String CHILD_STACKTRACE         = "StackTrace";
    public static final String CHILD_READONLY           = "JSReadOnly";
    public static final String CHILD_PROFILE_TITLE      = "AssignedTitle";
    public static final String CHILD_TABLE_VIEW         = "AssignedTableView";
    public static final String CHILD_ABOVETABLE_VIEW    = "AssignedAboveTableView";
    public static final String CHILD_MEMBER_TABLE_VIEW  = "MemberTableView";
    public static final String CHILD_RENAME_TEXT        = "RenameMessage";
    public static final String CHILD_SWAP_TEXT          = "NavigationConditionalImageSwap";
    public static final String CHILD_NAME_HIDDEN        = "NewName";
    public static final String CHILD_COMMENT_HIDDEN     = "NewComment";
    public static final String CHILD_LOCATION_HIDDEN    = "NewLocation";
    public static final String CHILD_COMMAND_HIDDEN     = "CommandHidden";
    public static final String CHILD_PARAMETERS_HIDDEN  = "CommandParametersHidden";
    public static final String CHILD_ANCHOR_HIDDEN      = "Anchor";
    public static final String CHILD_HELPLOCALE_HIDDEN  = "HelpLocale";
    protected AssignedTableModel    m_Model             = null;
    
    public EntityContentViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_FORM,               CCForm.class);
        registerChild(CHILD_DEFAULT_HREF,       CCHref.class);
        registerChild(CHILD_UPDATE_HREF,        CCHref.class);
        registerChild(CHILD_RESULT_BUTTON,      CCButton.class);
        registerChild(CHILD_ALERT,              CCAlertInline.class);
        registerChild(CHILD_STACKTRACE,         CCStaticTextField.class);
        registerChild(CHILD_READONLY,           CCStaticTextField.class);
        registerChild(CHILD_RENAME_TEXT,        CCStaticTextField.class);
        registerChild(CHILD_SWAP_TEXT,          CCStaticTextField.class);
        registerChild(CHILD_PROFILE_TITLE,      CCPageTitle.class);
        registerChild(CHILD_TABLE_VIEW,         AssignedTableView.class);
        registerChild(CHILD_ABOVETABLE_VIEW,    AssignedAboveTableView.class);
        registerChild(CHILD_MEMBER_TABLE_VIEW,  MemberTableView.class);
        registerChild(CHILD_NAME_HIDDEN,        CCHiddenField.class);
        registerChild(CHILD_COMMENT_HIDDEN,     CCHiddenField.class);
        registerChild(CHILD_LOCATION_HIDDEN,    CCHiddenField.class);
        registerChild(CHILD_COMMAND_HIDDEN,     CCHiddenField.class);
        registerChild(CHILD_PARAMETERS_HIDDEN,  CCHiddenField.class);
        registerChild(CHILD_ANCHOR_HIDDEN,      CCHiddenField.class);
        registerChild(CHILD_HELPLOCALE_HIDDEN,  CCHiddenField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_FORM)) {
            View child = new CCForm(this, name);
            return child;
        } 
        else if (name.equals(CHILD_DEFAULT_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_UPDATE_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_RESULT_BUTTON)) {
            CCButton child = new CCButton(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_STACKTRACE) ||
        name.equals(CHILD_READONLY) ||
        name.equals(CHILD_RENAME_TEXT) ||
        name.equals(CHILD_SWAP_TEXT)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_PROFILE_TITLE)) {
            CCPageTitleModel pageTitleModel = new CCPageTitleModel();
            Toolbox2.setPageTitleHelp(pageTitleModel, "APOC.sync.result.title", 
                                "APOC.profile.page.title", "gbgdc.html");
            CCPageTitle child = new CCPageTitle(this, pageTitleModel, name);
            return child;
        }
        else if (name.equals(CHILD_TABLE_VIEW)) {
            AssignedTableView child = new AssignedTableView(this, name);
            return child;
        }
        else if (name.equals(CHILD_ABOVETABLE_VIEW)) {
            AssignedAboveTableView child = null;
            try {
                child = new AssignedAboveTableView(this, name);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return child;
        }
        else if (name.equals(CHILD_MEMBER_TABLE_VIEW)) {
            MemberTableView child = null;
            try {
                child = new MemberTableView(this, name);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return child;
        }
        else if (name.equals(CHILD_NAME_HIDDEN) || 
        name.equals(CHILD_COMMENT_HIDDEN) || 
        name.equals(CHILD_LOCATION_HIDDEN) || 
        name.equals(CHILD_COMMAND_HIDDEN) || 
        name.equals(CHILD_PARAMETERS_HIDDEN) || 
        name.equals(CHILD_ANCHOR_HIDDEN) || 
        name.equals(CHILD_HELPLOCALE_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleDefaultHrefRequest(RequestInvocationEvent event) {
        forwardTo(getRequestContext());
    }
    
    public void handleUpdateHrefRequest(RequestInvocationEvent event) {
        TemplateRepository.getDefaultRepository().update();
        forwardTo(getRequestContext());
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
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        
        if ((alert.getSummary() != null) && (alert.getSummary().length() > 0)) {
            return true;
        }
        
        boolean bIsReadOnly = false;
        
        try {
            bIsReadOnly = Toolbox2.getSelectedEntity().getProfileRepository().isReadOnly();
        } catch (SPIException se) {
            Toolbox2.prepareErrorDisplay(se, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE));
            return true;
        }
        
        if (bIsReadOnly) {
            alert.setValue(CCAlertInline.TYPE_INFO);
            alert.setType(CCAlertInline.TYPE_INFO);
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            alert.setSummary(i18n.getMessage("APOC.policies.info.readonly.access"));
        }
        
        return bIsReadOnly;
    }
    
    public boolean beginAssignedAboveViewDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        AssignedAboveTableModel model = (AssignedAboveTableModel) getModel(AssignedAboveTableModel.class);
        return (model.getNumRows()>0);
    }
    
    public boolean beginConditionalMemberTableDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        MemberTableModel model = (MemberTableModel) getModel(MemberTableModel.class);
        return (model.getNumRows()>0);
    }
    
    public boolean beginAssignedTitleDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCPageTitle title = (CCPageTitle) getChild(event.getChildName());
        title.getModel().setPageTitleText(Toolbox2.buildEntityTitle(null));
        return true;
    }
    
    public boolean beginCommandHiddenDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCHiddenField hidden = (CCHiddenField) getChild(event.getChildName());
        hidden.setValue("");
        return true;
    }
    
    public boolean beginCommandParametersHiddenDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCHiddenField hidden = (CCHiddenField) getChild(event.getChildName());
        hidden.setValue("");
        return true;
    }
    
    public boolean beginNavigationConditionalImageSwapDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        StringBuffer        sJavascript     = new StringBuffer();
        CCStaticTextField   text            = (CCStaticTextField) getChild(event.getChildName());
        NavigationModel     navModel        = (NavigationModel) getRequestContext().getModelManager().getModel(
        NavigationModel.class, "NavTree", true, true);
        int                 nSelectedNodeId = navModel.getSelectedNode().getId();
        Iterator            dirtyNodes      = navModel.getDirtyNodeIds();
        while (dirtyNodes.hasNext()) {
            int     nNodeId     = ((Integer)dirtyNodes.next()).intValue();
            boolean bHasAssigned= navModel.getAssignedState(nNodeId);
            if (nNodeId == nSelectedNodeId) {
                nNodeId=-1;
            }
            sJavascript.append("top.navigation.switchImage("+nNodeId+","+bHasAssigned+"); ");
        }
        text.setValue(sJavascript.toString());
        navModel.clearDirtyNodeIds();
        return true;
    }
    
    public AssignedTableModel getModel() {
        if (m_Model == null) {
            m_Model = (AssignedTableModel) getModel(AssignedTableModel.class);
            m_Model.setDocument(getRequestContext().getServletContext(), "/jsp/profiles/AssignedTable.xml");
        }
        
        return m_Model;
    }
}
