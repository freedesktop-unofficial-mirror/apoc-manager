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

package com.sun.apoc.manager.contexts;

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.util.HtmlUtil;
import com.iplanet.jato.view.BasicCommandField;
import com.iplanet.jato.view.ContainerView;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.AlertViewBean;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.ContextsLoginViewBean;
import com.sun.apoc.manager.ThreepaneViewBean;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.manager.WelcomeViewBean;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.model.CCWizardWindowModel;
import com.sun.web.ui.model.CCWizardWindowModelInterface;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.table.CCActionTable;
import com.sun.web.ui.view.wizard.CCWizardWindow;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;public class ManagerTableView extends RequestHandlingViewBase {
    
    public static AddContextWizardPageModel m_wizardPageModel;
    public static final String             WIZARDPAGEMODELNAME          = "pageModelName";
    public static final String             WIZARDPAGEMODELNAME_PREFIX   = "WizardPageModel";
    public static final String             WIZARDIMPLNAME               = "wizardImplName";
    public static final String             WIZARDIMPLNAME_PREFIX        = "WizardImpl";
    public static final String             CHILD_CONTEXT_TABLE          = "ContextTable";
    public static final String             CHILD_WIZARDWINDOW           = "wizardWindow";
    public static final String             CHILD_FRWD_TO_CMDCHILD       = "forwardToVb";
    public static final String             CHILD_WIZARDLAUNCHED_HIDDEN  = "WizardLaunched";
    
    protected ManagerTableModel m_Model             = null;
    
    private String              m_pageModelName;
    private String              m_wizardImplName;
    private boolean             m_wizardLaunched    = false;
    
    public ManagerTableView(View parent, String name) {
        super(parent, name);
        m_Model = getModel();
        setRequestContext(RequestManager.getRequestContext());
        registerChildren();
    }
    
    public ManagerTableModel getModel() {
        if (m_Model == null) {
            m_Model = (ManagerTableModel) getModel(ManagerTableModel.class);
            m_Model.setDocument(getRequestContext().getServletContext(), "/jsp/contexts/ManagerTable.xml");
        }
        
        return m_Model;
    }
    
    protected void registerChildren() {
        registerChild(CHILD_CONTEXT_TABLE, CCActionTable.class);
        registerChild(CHILD_WIZARDWINDOW, CCWizardWindow.class);
        registerChild(CHILD_FRWD_TO_CMDCHILD, BasicCommandField.class);
        registerChild(CHILD_WIZARDLAUNCHED_HIDDEN, CCHiddenField.class);
        m_Model.registerChildren(this);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_CONTEXT_TABLE)) {
            CCActionTable child = new CCActionTable(this, m_Model, name);
            return child;
        }
        else if (name.equals(CHILD_WIZARDWINDOW)) {
            Boolean launched = (Boolean) getRootView().getPageSessionAttribute("WIZARDLAUNCHED");
            
            boolean init = launched == null ||
            launched.booleanValue() == false;
            
            CCWizardWindowModel wizWinModel = createModel(init);
            
            // CCWizardWindow extends CCButton and therefor has
            // all the behavior of CCButton.
            //
            CCWizardWindow child = new CCWizardWindow(this, wizWinModel,
            name, (Object)"APOC.pool.new");
            child.setDisabled(!init);
            return child;
        } else if (name.equals(CHILD_FRWD_TO_CMDCHILD)) {
            
            // The command child that receives the request in the
            // form submit to this ViewBean when the wizard session
            // ends.
            //
            BasicCommandField bcf = new BasicCommandField(this, name);
            return bcf;
        } else if (name.equals(CHILD_WIZARDLAUNCHED_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, name, Boolean.valueOf(m_wizardLaunched));
            return child;
        } else if (m_Model.isChildSupported(name)) {
            return m_Model.createChild(this, name);
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
   
    public void handleNameHrefRequest(RequestInvocationEvent event) {
        try {
            getSession().setAttribute(Constants.FIRST_VISIT, "true");
            HashMap authorizedContexts = (HashMap) getSession().getAttribute(Constants.AUTH_CONTEXTS);
            String sHrefContext = Toolbox2.getParameter("NameHref");     
            if (authorizedContexts==null) {
                getViewBean(ContextsLoginViewBean.class).forwardTo(getRequestContext());
            } else {
                if (authorizedContexts.containsKey(sHrefContext)) {
                    String sUsername = ((String[]) authorizedContexts.get(sHrefContext))[0];
                    String sPassword = ((String[]) authorizedContexts.get(sHrefContext))[1];
                    Toolbox2.setPolicyManager(sHrefContext, sUsername, sPassword);
                    getViewBean(ThreepaneViewBean.class).forwardTo(getRequestContext());
                } else {
                    getViewBean(ContextsLoginViewBean.class).forwardTo(getRequestContext());
                }
            }
        }
        catch (Exception e) {
            Toolbox2.prepareErrorDisplay(e,
            ((ContainerView)getParent()).getChild(AlertViewBean.CHILD_ALERT),
            ((ContainerView)getParent()).getChild(AlertViewBean.CHILD_STACKTRACE));
            getRootView().forwardTo(getRequestContext());
        }
    }
    
    public void handleRemoveButtonRequest(RequestInvocationEvent event) {
        try {
            m_Model.retrieve();
            ((CCActionTable) getChild(CHILD_CONTEXT_TABLE)).restoreStateData();
            updateSelections();
            m_Model.delete();
        }
        catch (ModelControlException mce) {
            Toolbox2.prepareErrorDisplay(mce,
            ((ContainerView)getParent()).getChild(AlertViewBean.CHILD_ALERT),
            ((ContainerView)getParent()).getChild(AlertViewBean.CHILD_STACKTRACE));
        }
        getRootView().forwardTo(getRequestContext());
    }
    
    public void handleRenameButtonRequest(RequestInvocationEvent event) {
        try {
            CCHiddenField newName = (CCHiddenField) ((WelcomeViewBean)getParent()).getChild(WelcomeViewBean.CHILD_CONTEXT_HIDDEN);
            String        sNewName= (String) newName.getValue();
            
            m_Model.retrieve();
            ((CCActionTable) getChild(CHILD_CONTEXT_TABLE)).restoreStateData();
            updateSelections();
            m_Model.rename(sNewName);
        }
        catch (ModelControlException mce) {
            Toolbox2.prepareErrorDisplay(mce,
            ((ContainerView)getParent()).getChild(AlertViewBean.CHILD_ALERT),
            ((ContainerView)getParent()).getChild(AlertViewBean.CHILD_STACKTRACE));
        }
        getRootView().forwardTo(getRequestContext());
    }
    
    /**
     * The request handler for the CCWizardWindow button.
     * Realize that the wizard will be displaying concurrently
     * as this ViewBean redisplays.
     */
    public void handleWizardWindowRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        m_wizardLaunched = true;
        getRootView().forwardTo(getRequestContext());
    }
    
    /**
     * Request handler for the form submit when the wizard session ends.
     */
    public void handleForwardToVbRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        
        m_wizardLaunched = false;
        getRootView().forwardTo(getRequestContext());
    }
    
    public void beginDisplay(DisplayEvent event)
    throws ModelControlException {
        super.beginDisplay(event);
        
        // Ensure that the page session attributes contain the
        // unique model name and WizardImpl name
        getRootView().setPageSessionAttribute(WIZARDPAGEMODELNAME, getWizardPageModelName(WIZARDPAGEMODELNAME_PREFIX));
        getRootView().setPageSessionAttribute(WIZARDIMPLNAME, getWizardImplName(WIZARDIMPLNAME_PREFIX));
        getRootView().setPageSessionAttribute("WIZARDLAUNCHED", new Boolean(m_wizardLaunched));
        ((CCWizardWindow)getChild(CHILD_WIZARDWINDOW)).setDisabled(m_wizardLaunched);
        ((CCHiddenField)getChild(CHILD_WIZARDLAUNCHED_HIDDEN)).setValue(Boolean.valueOf(m_wizardLaunched));
        m_Model.retrieve();
    }
    
    public boolean beginConditionalContextTableDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        Iterator backends = Toolbox2.getBackendNames();
        return backends.hasNext();
    }
    
    /** When the application receives its first request
     * the page session attributes will not be available.
     *
     * Unique names are created to distinguish this instance
     * of the application from others thereby ensuring that
     * this wizard session is not corrupted by other wizard
     * sessions based on this application page.
     *
     * On subsequent requests the model name and wizard name
     * will be available. This is most important when the
     * wizard session ends. When the wizard session ends, it
     * forwards a request to this ViewBean thereby effecting
     * a redisply. During that redisplay the model that was
     * originally created by this ViewBean can be recovered
     * and made available to the views that represent this
     * application page. The result is that the data collected
     * during the wizard session is displayed in the
     * application page.
     */
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
    
    /**
     * It is important to have a unique Wizard name since the
     * WizardImpl example wizard is not a stateless wizard.
     */
    private String getWizardImplName(String prefix) {
        
        if (m_wizardImplName == null) {
            m_wizardImplName =
            (String)getRootView().getPageSessionAttribute(WIZARDIMPLNAME);
            if (m_wizardImplName == null) {
                m_wizardImplName = prefix + "_" + HtmlUtil.getUniqueValue();
                getRootView().setPageSessionAttribute(WIZARDIMPLNAME, m_wizardImplName);
            }
        }
        
        return m_wizardImplName;
    }
    
    
    private CCWizardWindowModel createModel(boolean init) {
        CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
        
        CCWizardWindowModel wizWinModel = new CCWizardWindowModel();
        if (init) {
            wizWinModel.setValue(CCWizardWindowModelInterface.MASTHEAD_ALT,
            "APOC.masthead.altText");
            wizWinModel.setValue(CCWizardWindowModelInterface.BASENAME,
            Constants.RES_BASE_NAME);
            wizWinModel.setValue(CCWizardWindowModelInterface.BUNDLEID,
            "apocBundle");
            wizWinModel.setValue(CCWizardWindowModelInterface.TITLE,
            "APOC.wiz.title");
            wizWinModel.setValue(CCWizardWindowModelInterface.WINDOW_HEIGHT,
            new Integer(594));
            wizWinModel.setValue(CCWizardWindowModelInterface.WINDOW_WIDTH,
            new Integer(900));
            
            wizWinModel.setValue(
            CCWizardWindowModelInterface.WIZARD_REFRESH_CMDCHILD,
            getQualifiedName() + "." + CHILD_FRWD_TO_CMDCHILD);
            
            // Static stateful simple wizard
            //
            wizWinModel.setValue(
            CCWizardWindowModelInterface.WIZARD_CLASS_NAME,
            "com.sun.apoc.manager.contexts.AddContextWizardImpl");
            
            // Create a unique name for the wizard since
            // WizardImpl.java isn't stateless
            //
            wizWinModel.setValue(CCWizardWindowModelInterface.WIZARD_NAME, 
           getWizardImplName(WIZARDIMPLNAME_PREFIX));
            
            // Application dependent value, which is obtained
            // by the WizardImpl class from the request parameters
            // during its creation
            //
            wizWinModel.setValue(WIZARDPAGEMODELNAME,
            getWizardPageModelName(WIZARDPAGEMODELNAME_PREFIX));
            
        }
        
        /**
         * Since the page session attribute is not needed by the
         * the individual wizard pages, don't pass it to
         * popup wizard window.
         *
         * This property was introduced
         * in 2.1 to prevent the problems when an extremely long
         * page session attribute is passed as a request parameter.
         * Some browsers limit the length of URL's.
         *
         * If you do not need to access information in a
         * wizard page from this page session, then set
         * this property to false.
         *
         *	wizWinModel.setValue(
         *	CCWizardWindowModelInterface.WIZARD_PASS_PAGESESSION, "false");
         */
        return wizWinModel;
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
                    m_Model.setRowSelected(Integer.parseInt(sRowNumber), Boolean.valueOf(sParamValue).booleanValue());
                }
            }
        }
    }
}
