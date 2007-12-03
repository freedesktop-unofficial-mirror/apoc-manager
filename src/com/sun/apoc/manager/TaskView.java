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

import com.iplanet.jato.ModelManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.util.HtmlUtil;
import com.iplanet.jato.view.BasicCommandField;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.contexts.AddContextWizardPageModel;
import com.sun.apoc.spi.SPIException;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.model.CCWizardWindowModel;
import com.sun.web.ui.model.CCWizardWindowModelInterface;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.wizard.CCWizardWindow;
import java.io.IOException;
import javax.servlet.ServletException;
import com.sun.web.ui.common.CCI18N;
import com.sun.apoc.manager.profiles.CopyMoveWizardPageModel;
import com.sun.apoc.manager.profiles.CopyMoveWizardImpl;


public class TaskView extends RequestHandlingViewBase {
    public static final String CHILD_ALERT          = "Alert";
    public static final String CHILD_STACKTRACE     = "StackTrace";
    public static final String CHILD_PROFILE_TITLE  = "TaskTitle";
    public static final String CHILD_ADD_BUTTON     = "AddContextButton";
    public static final String CHILD_ADD_HREF       = "AddContext";
    public static final String CHILD_ADD_TEXT       = "AddContextText";
    public static final String CHILD_CREATE_HREF    = "CreateProfile";
    public static final String CHILD_CREATE_TEXT    = "CreateProfileText";
    public static final String CHILD_SYNC_HREF      = "SyncContext";
    public static final String CHILD_SYNC_TEXT      = "SyncContextText";
    public static final String CHILD_EDIT_HREF      = "EditProfile";
    public static final String CHILD_EDIT_TEXT      = "EditProfileText";
    public static final String CHILD_ASSIGN_HREF    = "AssignProfile";
    public static final String CHILD_ASSIGN_TEXT    = "AssignProfileText";
    public static final String CHILD_COPY_HREF      = "CopyProfile";
    public static final String CHILD_COPY_TEXT      = "CopyProfileText";
    public static final String CHILD_REPORT_HREF    = "ReportProfile";
    public static final String CHILD_REPORT_TEXT    = "ReportProfileText";
    public static final String CHILD_REPORT2_HREF   = "ReportEntity";
    public static final String CHILD_REPORT2_TEXT   = "ReportEntityText";
    public static final String CHILD_REMOTE_HREF    = "RemoteDesktop";
    public static final String CHILD_REMOTE_TEXT    = "RemoteDesktopText";
    public static final String CHILD_CONTEXT_TEXT   = "CurrentContext";
    public static final String WIZARDPAGEMODELNAME          = "pageModelName";
    public static final String WIZARDPAGEMODELNAME_PREFIX   = "WizardPageModel";
    public static final String WIZARDIMPLNAME               = "wizardImplName";
    public static final String WIZARDIMPLNAME_PREFIX        = "WizardImpl";
    public static final String CHILD_FRWD_TO_CMDCHILD       = "forwardToVb";
    public static final String COPYMOVEWIZARDPAGEMODELNAME          = "copyMovePageModelName";
    public static final String COPYMOVEWIZARDPAGEMODELNAME_PREFIX   = "CopyMoveWizardPageModel";
    public static final String COPYMOVEWIZARDIMPLNAME               = "copyMoveWizardImplName";
    public static final String COPYMOVEWIZARDIMPLNAME_PREFIX        = "CopyMoveWizardImpl";
    public static final String CHILD_COPYMOVE_WIZ           = "copyMoveWizardWindow";
    public static final String CHILD_FRWD_TO_CMDCHILD_COPYMOVE       = "copyMoveForwardToVb";
    
    public static AddContextWizardPageModel m_wizardPageModel;
    public static CopyMoveWizardPageModel  m_cmWizardPageModel;
    
    private String  m_pageModelName;
    private String  m_wizardImplName;
    private boolean m_wizardLaunched = false;

    private String  m_cmPageModelName;
    private String  m_cmWizardImplName;
    private boolean m_cmWizardLaunched = false;
    
    public TaskView(View parent, String name) {
        super(parent, name);
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_ALERT,              CCAlertInline.class);
        registerChild(CHILD_STACKTRACE,         CCStaticTextField.class);
        registerChild(CHILD_PROFILE_TITLE,      CCPageTitle.class);
        registerChild(CHILD_ADD_BUTTON,         CCWizardWindow.class);
        registerChild(CHILD_COPYMOVE_WIZ,       CCWizardWindow.class);
        registerChild(CHILD_ADD_HREF,           CCHref.class);
        registerChild(CHILD_ADD_TEXT,           CCStaticTextField.class);
        registerChild(CHILD_SYNC_HREF,          CCHref.class);
        registerChild(CHILD_SYNC_TEXT,          CCStaticTextField.class);
        registerChild(CHILD_CREATE_HREF,        CCHref.class);
        registerChild(CHILD_CREATE_TEXT,        CCStaticTextField.class);
        registerChild(CHILD_EDIT_HREF,          CCHref.class);
        registerChild(CHILD_EDIT_TEXT,          CCStaticTextField.class);
        registerChild(CHILD_ASSIGN_HREF,        CCHref.class);
        registerChild(CHILD_ASSIGN_TEXT,        CCStaticTextField.class);
        registerChild(CHILD_COPY_HREF,          CCHref.class);
        registerChild(CHILD_COPY_TEXT,          CCStaticTextField.class);
        registerChild(CHILD_REPORT_HREF,        CCHref.class);
        registerChild(CHILD_REPORT_TEXT,        CCStaticTextField.class);
        registerChild(CHILD_REPORT2_HREF,       CCHref.class);
        registerChild(CHILD_REPORT2_TEXT,       CCStaticTextField.class);
        registerChild(CHILD_REMOTE_HREF,        CCHref.class);
        registerChild(CHILD_REMOTE_TEXT,        CCStaticTextField.class);
        registerChild(CHILD_CONTEXT_TEXT,       CCStaticTextField.class);
        registerChild(CHILD_FRWD_TO_CMDCHILD,   BasicCommandField.class);
        registerChild(CHILD_FRWD_TO_CMDCHILD_COPYMOVE,   BasicCommandField.class);
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
        else if (name.equals(CHILD_ADD_HREF) ||
        name.equals(CHILD_CREATE_HREF) ||
        name.equals(CHILD_SYNC_HREF) ||
        name.equals(CHILD_EDIT_HREF) ||
        name.equals(CHILD_ASSIGN_HREF) ||
        name.equals(CHILD_COPY_HREF) ||
        name.equals(CHILD_REPORT_HREF) ||
        name.equals(CHILD_REPORT2_HREF) ||
        name.equals(CHILD_REMOTE_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_ADD_TEXT) ||
        name.equals(CHILD_CREATE_TEXT) ||
        name.equals(CHILD_SYNC_TEXT) ||
        name.equals(CHILD_EDIT_TEXT) ||
        name.equals(CHILD_ASSIGN_TEXT) ||
        name.equals(CHILD_COPY_TEXT) ||
        name.equals(CHILD_REPORT_TEXT) ||
        name.equals(CHILD_REPORT2_TEXT) ||
        name.equals(CHILD_REMOTE_TEXT) ||
        name.equals(CHILD_CONTEXT_TEXT)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_PROFILE_TITLE)) {
            CCPageTitleModel pageTitleModel = new CCPageTitleModel();
            CCPageTitle child = new CCPageTitle(this, pageTitleModel, name);
            return child;
        }
        else if (name.equals(CHILD_ADD_BUTTON)) {
            Boolean             launched    = (Boolean) getRootView().getPageSessionAttribute("WIZARDLAUNCHED");
            boolean             init        = (launched == null) || (launched.booleanValue() == false);
            CCWizardWindowModel wizWinModel = createModel(init);
            CCWizardWindow      child       = new CCWizardWindow(this, wizWinModel, name, (Object)"APOC.contexts.create.button");
            child.setDisabled(!init);
            return child;
        } else if (name.equals(CHILD_FRWD_TO_CMDCHILD)) {
            BasicCommandField bcf = new BasicCommandField(this, name);
            return bcf;
        } else if (name.equals(CHILD_COPYMOVE_WIZ)) {
            Boolean launched = (Boolean) getRootView().getPageSessionAttribute("COPYMOVEWIZARDLAUNCHED");
            boolean init = launched == null || launched.booleanValue() == false;
            CCWizardWindowModel wizWinModel = createCopyMoveModel(init);
            CCWizardWindow child = new CCWizardWindow(this, wizWinModel, name, null);
            child.setDisabled(!init);
            return child;
        } else if (name.equals(CHILD_FRWD_TO_CMDCHILD_COPYMOVE)) {
            BasicCommandField bcf = new BasicCommandField(this, name);
            return bcf;
        } else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleAddContextButtonRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        m_wizardLaunched = true;
        getRootView().forwardTo(getRequestContext());
    }
    
    public void handleForwardToVbRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        m_wizardLaunched = false;
        getRootView().forwardTo(getRequestContext());
    }
    
    public void handleCopyMoveWizardWindowRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        m_cmWizardLaunched = true;
        getRootView().forwardTo(getRequestContext());
    }
    

    public void handleCopyMoveForwardToVbRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        m_cmWizardLaunched = false;
        getRootView().forwardTo(getRequestContext());
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        getRootView().setPageSessionAttribute(WIZARDPAGEMODELNAME, getWizardPageModelName(WIZARDPAGEMODELNAME_PREFIX));
        getRootView().setPageSessionAttribute(WIZARDIMPLNAME, getWizardImplName(WIZARDIMPLNAME_PREFIX));
        getRootView().setPageSessionAttribute("WIZARDLAUNCHED", new Boolean(m_wizardLaunched));
        ((CCWizardWindow)getChild(CHILD_ADD_BUTTON)).setDisabled(m_wizardLaunched);

        getRootView().setPageSessionAttribute(COPYMOVEWIZARDPAGEMODELNAME, getCopyMoveWizardPageModelName(COPYMOVEWIZARDPAGEMODELNAME_PREFIX));
        getRootView().setPageSessionAttribute(COPYMOVEWIZARDIMPLNAME, getCopyMoveWizardImplName(COPYMOVEWIZARDIMPLNAME_PREFIX));
        getRootView().setPageSessionAttribute("COPYMOVEWIZARDLAUNCHED", new Boolean(m_cmWizardLaunched));
        ((CCWizardWindow)getChild(CHILD_COPYMOVE_WIZ)).setDisabled(m_cmWizardLaunched);
    }
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        return ((alert.getSummary() != null) && (alert.getSummary().length() > 0));
    }
    
    public boolean beginCurrentContextDisplay(ChildDisplayEvent event)
    throws ServletException, IOException, ModelControlException {
        try {
            CCStaticTextField   context  = (CCStaticTextField) getChild(event.getChildName());
            String              sContext = (String) Toolbox2.getPolicyManager().getEnvironment().get(Constants.POLICY_MANAGER_NAME);
            context.setValue(Toolbox2.encode(sContext));
        } catch (SPIException se) {
            throw new ModelControlException(se);
        }
        return true;
    }
    
    private String getWizardPageModelName(String prefix) {
        
        if (m_pageModelName == null) {
            m_pageModelName = (String)
            getRootView().getPageSessionAttribute(WIZARDPAGEMODELNAME);
            if (m_pageModelName == null) {
                m_pageModelName = prefix + "_" + HtmlUtil.getUniqueValue();
                getRootView().setPageSessionAttribute(WIZARDPAGEMODELNAME, m_pageModelName);
            }
        }
        
        return m_pageModelName;
    }
    
    private String getWizardImplName(String prefix) {
        
        if (m_wizardImplName == null) {
            m_wizardImplName = (String)getRootView().getPageSessionAttribute(WIZARDIMPLNAME);
            if (m_wizardImplName == null) {
                m_wizardImplName = prefix + "_" + HtmlUtil.getUniqueValue();
                getRootView().setPageSessionAttribute(WIZARDIMPLNAME, m_wizardImplName);
            }
        }
        
        return m_wizardImplName;
    }
    
    
    private CCWizardWindowModel createModel(boolean init) {
        CCWizardWindowModel wizWinModel = new CCWizardWindowModel();
        if (init) {
            wizWinModel.setValue(CCWizardWindowModelInterface.MASTHEAD_ALT, "APOC.masthead.altText");
            wizWinModel.setValue(CCWizardWindowModelInterface.BASENAME, Constants.RES_BASE_NAME);
            wizWinModel.setValue(CCWizardWindowModelInterface.BUNDLEID, "apocBundle");
            wizWinModel.setValue(CCWizardWindowModelInterface.TITLE, "APOC.wiz.title");
            wizWinModel.setValue(CCWizardWindowModelInterface.WINDOW_HEIGHT, new Integer(594));
            wizWinModel.setValue(CCWizardWindowModelInterface.WINDOW_WIDTH, new Integer(825));
            wizWinModel.setValue(CCWizardWindowModelInterface.WIZARD_REFRESH_CMDCHILD, getQualifiedName() + "." + CHILD_FRWD_TO_CMDCHILD);
            wizWinModel.setValue(CCWizardWindowModelInterface.WIZARD_CLASS_NAME, "com.sun.apoc.manager.contexts.AddContextWizardImpl");
            wizWinModel.setValue(CCWizardWindowModelInterface.WIZARD_NAME, getWizardImplName(WIZARDIMPLNAME_PREFIX));
            wizWinModel.setValue(WIZARDPAGEMODELNAME, getWizardPageModelName(WIZARDPAGEMODELNAME_PREFIX));
        }
        
        return wizWinModel;
    }

    private String getCopyMoveWizardPageModelName(String prefix) {
        
        if (m_cmPageModelName == null) {
            m_cmPageModelName = (String)
            getRootView().getPageSessionAttribute(COPYMOVEWIZARDPAGEMODELNAME);
            if (m_cmPageModelName == null) {
                m_cmPageModelName = prefix + "_" + HtmlUtil.getUniqueValue();
                getRootView().setPageSessionAttribute(COPYMOVEWIZARDPAGEMODELNAME, m_cmPageModelName);
            }
        }
        
        return m_cmPageModelName;
    }
    
    private String getCopyMoveWizardImplName(String prefix) {
        
        if (m_cmWizardImplName == null) {
            m_cmWizardImplName =
            (String)getRootView().getPageSessionAttribute(COPYMOVEWIZARDIMPLNAME);
            if (m_cmWizardImplName == null) {
                m_cmWizardImplName = prefix + "_" + HtmlUtil.getUniqueValue();
                getRootView().setPageSessionAttribute(COPYMOVEWIZARDIMPLNAME, m_cmWizardImplName);
            }
        }
        
        return m_cmWizardImplName;
    }
    
    
    private CCWizardWindowModel createCopyMoveModel(boolean init) {
        CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
        
        CCWizardWindowModel wizWinModel = new CCWizardWindowModel();
        wizWinModel.clear();
        if (init) {
            wizWinModel.setValue(CCWizardWindowModelInterface.MASTHEAD_ALT,
            "APOC.masthead.altText");
            wizWinModel.setValue(CCWizardWindowModelInterface.BASENAME,
            Constants.RES_BASE_NAME);
            wizWinModel.setValue(CCWizardWindowModelInterface.BUNDLEID,
            "apocBundle");
            wizWinModel.setValue(CCWizardWindowModelInterface.TITLE,
            "APOC.wiz.copy.title");
            wizWinModel.setValue(CCWizardWindowModelInterface.WINDOW_HEIGHT,
            new Integer(594));
            wizWinModel.setValue(CCWizardWindowModelInterface.WINDOW_WIDTH,
            new Integer(825));
            
            wizWinModel.setValue(
            CCWizardWindowModelInterface.WIZARD_REFRESH_CMDCHILD,
            getQualifiedName() + "." + CHILD_FRWD_TO_CMDCHILD_COPYMOVE);
 
            wizWinModel.setValue(
            CCWizardWindowModelInterface.WIZARD_CLASS_NAME,
            "com.sun.apoc.manager.profiles.CopyMoveWizardImpl");
 
            wizWinModel.setValue(CCWizardWindowModelInterface.WIZARD_NAME,
            getCopyMoveWizardImplName(COPYMOVEWIZARDIMPLNAME_PREFIX));
            
            wizWinModel.setValue(COPYMOVEWIZARDPAGEMODELNAME,
            getCopyMoveWizardPageModelName(COPYMOVEWIZARDPAGEMODELNAME_PREFIX));
            
        }
        return wizWinModel;
    }    
}
