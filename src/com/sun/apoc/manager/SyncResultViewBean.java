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
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.html.OptionList;
import com.sun.apoc.manager.sync.ContextTiledModel;
import com.sun.apoc.manager.sync.ContextTiledView;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.model.CCPropertySheetModel;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCDropDownMenu;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCOption;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.masthead.CCSecondaryMasthead;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.propertysheet.CCPropertySheet;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SyncResultViewBean extends ViewBeanBase {
    public static final String PAGE_NAME                = "SyncResult";
    public static final String DEFAULT_DISPLAY_URL      = "/jsp/sync/Result.jsp";
    public static final String CHILD_MASTHEAD           = "Masthead";
    public static final String CHILD_FORM               = "ResultsForm";
    public static final String CHILD_ALERT              = "Alert";
    public static final String CHILD_STACKTRACE         = "StackTrace";
    public static final String CHILD_SYNC_TITLE         = "SyncResultTitle";
    public static final String CHILD_CONTEXT_VIEW       = "ContextTiledView";
    public static final String CHILD_CONTEXTS_PROPS     = "Contexts";
    public static final String CHILD_DEFAULT_HREF       = "DefaultHref";
    public static final String CHILD_SELECTION_HREF     = "SelectionHref";
    public static final String CHILD_LEFTBROWSE_HREF    = "LeftBrowseHref";
    public static final String CHILD_RIGHTBROWSE_HREF   = "RightBrowseHref";
    public static final String CHILD_COMPARE_BUTTON     = "CompareButton";
    public static final String CHILD_SYNC_BUTTON        = "SyncButton";
    public static final String CHILD_LEFTCONTEXT_TEXT   = "LeftContextJS";
    public static final String CHILD_CURRENTACTION_TEXT = "CurrentAction";
    public static final String CHILD_ELAPSEDTIME_TEXT   = "ElapsedTime";
    public static final String CHILD_RIGHTCONTEXT_TEXT  = "RightContextJS";
    public static final String CHILD_HELP0_TEXT         = "HelpText0";
    public static final String CHILD_HELP1_TEXT         = "HelpText1";
    public static final String CHILD_HELP2_TEXT         = "HelpText2";
    public static final String CHILD_HELP3_TEXT         = "HelpText3";
    public static final String CHILD_HELP4_TEXT         = "HelpText4";
    public static final String CHILD_HELP5_TEXT         = "HelpText5";
    public static final String CHILD_BUSY1_TEXT         = "BusyText1";
    public static final String CHILD_CYCLE_HIDDEN       = "CycleHidden";
    
    private CCPropertySheetModel    m_sheetModel        = null;
    private boolean                 m_bComp             = false;
    private boolean                 m_bSync             = false;
    private boolean                 m_bCycle            = false;
    private boolean                 m_bIsIdle           = true;
    private static Thread           m_thread            = null;
    
    public SyncResultViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        m_sheetModel = new CCPropertySheetModel(RequestManager.getRequestContext().getServletContext(), "/jsp/sync/Contexts.xml");
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_ALERT,              CCAlertInline.class);
        registerChild(CHILD_STACKTRACE,         CCStaticTextField.class);
        registerChild(CHILD_MASTHEAD,           CCSecondaryMasthead.class);
        registerChild(CHILD_FORM,               CCForm.class);
        registerChild(CHILD_SYNC_TITLE,         CCPageTitle.class);
        registerChild(CHILD_CONTEXT_VIEW,       ContextTiledView.class);
        registerChild(CHILD_CONTEXTS_PROPS,     CCPropertySheet.class);
        registerChild(CHILD_DEFAULT_HREF,       CCHref.class);
        registerChild(CHILD_SELECTION_HREF,     CCHref.class);
        registerChild(CHILD_LEFTBROWSE_HREF,    CCHref.class);
        registerChild(CHILD_RIGHTBROWSE_HREF,   CCHref.class);
        registerChild(CHILD_COMPARE_BUTTON,     CCButton.class);
        registerChild(CHILD_SYNC_BUTTON,        CCButton.class);
        registerChild(CHILD_CURRENTACTION_TEXT, CCStaticTextField.class);
        registerChild(CHILD_ELAPSEDTIME_TEXT,   CCStaticTextField.class);
        registerChild(CHILD_LEFTCONTEXT_TEXT,   CCStaticTextField.class);
        registerChild(CHILD_RIGHTCONTEXT_TEXT,  CCStaticTextField.class);
        registerChild(CHILD_HELP0_TEXT,         CCStaticTextField.class);
        registerChild(CHILD_HELP1_TEXT,         CCStaticTextField.class);
        registerChild(CHILD_HELP2_TEXT,         CCStaticTextField.class);
        registerChild(CHILD_HELP3_TEXT,         CCStaticTextField.class);
        registerChild(CHILD_HELP4_TEXT,         CCStaticTextField.class);
        registerChild(CHILD_HELP5_TEXT,         CCStaticTextField.class);
        registerChild(CHILD_BUSY1_TEXT,         CCStaticTextField.class);
        registerChild(CHILD_CYCLE_HIDDEN,       CCHiddenField.class);
        m_sheetModel.registerChildren(this);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
            return child;
        } else if (name.equals(CHILD_STACKTRACE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        } else if (name.equals(CHILD_SYNC_TITLE)) {
            CCPageTitleModel pageTitleModel = new CCPageTitleModel();
            Toolbox2.setPageTitleHelp(pageTitleModel, "APOC.sync.title",
                "APOC.sync.help", "gbgqm.html");
            CCPageTitle child = new CCPageTitle(this, pageTitleModel , name);
            return child;
        } else if (name.equals(CHILD_MASTHEAD)) {
            CCSecondaryMasthead child = new CCSecondaryMasthead(this, name);
            return child;
        }
        if (name.equals(CHILD_FORM)) {
            CCForm child = new CCForm(this, name);
            return child;
        } else if (name.equals(CHILD_CONTEXT_VIEW)) {
            ContextTiledView child = new ContextTiledView(this, name);
            return child;
        } else if (name.equals(CHILD_CONTEXTS_PROPS)) {
            CCPropertySheet child = new CCPropertySheet(this, m_sheetModel, name);
            return child;
        } else if (name.equals(CHILD_DEFAULT_HREF) ||
            name.equals(CHILD_SELECTION_HREF) ||
            name.equals(CHILD_LEFTBROWSE_HREF) ||
            name.equals(CHILD_RIGHTBROWSE_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        } else if (name.equals(CHILD_COMPARE_BUTTON)||name.equals(CHILD_SYNC_BUTTON)) {
            CCButton child = new CCButton(this, name, null);
            return child;
        } else if (name.equals(CHILD_CURRENTACTION_TEXT) || name.equals(CHILD_ELAPSEDTIME_TEXT) ||
            name.equals(CHILD_LEFTCONTEXT_TEXT) || name.equals(CHILD_RIGHTCONTEXT_TEXT) ||
            name.equals(CHILD_HELP0_TEXT) || name.equals(CHILD_HELP1_TEXT) ||
            name.equals(CHILD_HELP2_TEXT) || name.equals(CHILD_HELP3_TEXT) ||
            name.equals(CHILD_HELP4_TEXT) || name.equals(CHILD_HELP5_TEXT) ||
            name.equals(CHILD_BUSY1_TEXT)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        } else if (name.equals(CHILD_CYCLE_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;
        } else if ((m_sheetModel != null) && m_sheetModel.isChildSupported(name)) {
            return m_sheetModel.createChild(this, name);
        } else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleDefaultHrefRequest(RequestInvocationEvent event) {
        forwardTo(getRequestContext());
    }
    
    public void handleCompareButtonRequest(RequestInvocationEvent event) {
        if (Toolbox2.getParameter(CHILD_CYCLE_HIDDEN).length()==0) {
            m_bComp=true;
        } else {
            m_bCycle=true;
        }
        forwardTo(getRequestContext());
    }
    
    public void handleSyncButtonRequest(RequestInvocationEvent event) {
        m_bSync = true;
        forwardTo(getRequestContext());
    }
    
    public void handleLeftBrowseHrefRequest(RequestInvocationEvent event) {
        CCHref  href    = (CCHref) getChild("LeftBrowseHref");
        HashMap syncEnv = (HashMap) getSession().getAttribute(Constants.SYNC_ENVIRONMENT);
        String  sValue  = (String) href.getValue();
        String entityType = sValue.substring(0, sValue.indexOf("|"));
        String entityId = sValue.substring(sValue.indexOf("|") + 1);
        syncEnv.put(SyncLoginViewBean.ENV_LEFT_ENTITYID, entityId);
        syncEnv.put(SyncLoginViewBean.ENV_LEFT_ENTITY_TYPE, entityType);
        forwardTo(getRequestContext());
    }
    
    public void handleRightBrowseHrefRequest(RequestInvocationEvent event) {
        CCHref  href    = (CCHref) getChild("RightBrowseHref");
        HashMap syncEnv = (HashMap) getSession().getAttribute(Constants.SYNC_ENVIRONMENT);
        String  sValue  = (String) href.getValue();
        String entityType = sValue.substring(0, sValue.indexOf("|"));
        String entityId = sValue.substring(sValue.indexOf("|") + 1);
        syncEnv.put(SyncLoginViewBean.ENV_RIGHT_ENTITYID, entityId);
        syncEnv.put(SyncLoginViewBean.ENV_RIGHT_ENTITY_TYPE, entityType);
        forwardTo(getRequestContext());
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        HttpServletRequest request = getRequestContext().getRequest();
        mapRequestParameters(request);
        setParameterValues("Left", SyncLoginViewBean.ENV_LEFT_CONTEXTNAME, SyncLoginViewBean.ENV_LEFT_CONTEXT, SyncLoginViewBean.ENV_LEFT_ENTITY_TYPE, SyncLoginViewBean.ENV_LEFT_ENTITYID);
        setParameterValues("Right", SyncLoginViewBean.ENV_RIGHT_CONTEXTNAME, SyncLoginViewBean.ENV_RIGHT_CONTEXT, SyncLoginViewBean.ENV_RIGHT_ENTITY_TYPE, SyncLoginViewBean.ENV_RIGHT_ENTITYID);
        
        if (!m_bCycle) {
            getModel().resetModel();
        }
        if (m_bCycle) {
            m_bIsIdle = (m_thread==null) || !m_thread.isAlive();
        }
        if (m_bComp||m_bSync) {
            LinkedList syncList = new LinkedList();
            if (m_bSync) {
                Enumeration aParamNamesEnum = request.getParameterNames();
                while (aParamNamesEnum.hasMoreElements()) {
                    String sParamName = (String) aParamNamesEnum.nextElement();
                    if (sParamName.endsWith("LeftIdHidden")) {
                        String[] leftIds    = request.getParameterValues(sParamName);
                        String[] rightIds   = request.getParameterValues(sParamName.substring(0, sParamName.lastIndexOf(".")+1)+"RightIdHidden");
                        for (int nValueRunner=0; nValueRunner<leftIds.length; nValueRunner++) {
                            if (leftIds[nValueRunner].startsWith("+")) {
                                syncList.add(leftIds[nValueRunner].substring(1));
                                syncList.add(rightIds[nValueRunner].substring(1));
                            }
                        }
                    }
                }
            }
            m_thread = new SyncCompThread(getSession(), syncList);
            m_thread.start();
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException ie) {}
            m_bIsIdle = (m_thread==null) || !m_thread.isAlive();
        }
    }
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        
        ContextTiledModel model     = (ContextTiledModel) getSession().getAttribute("ThreadSyncModel");
        CCAlertInline     alert     = (CCAlertInline) getChild(CHILD_ALERT);
        boolean           bIsAlert  = (alert.getSummary()!=null) && (alert.getSummary().length() > 0);
        
        if (!bIsAlert) {
            if ((model!=null) && (model.getException()!=null)) {
                Toolbox2.prepareErrorDisplay(model.getException(), alert, getChild(CHILD_STACKTRACE));
                bIsAlert = true;
            } else if (beginActionCompletedDisplay(event)) {
                if (m_bComp||m_bSync||m_bCycle) {
                    if (!getModel().areTreesCompareable()) {
                        alert.setValue(CCAlert.TYPE_WARNING);
                        alert.setSummary("APOC.sync.msg.orgdomcomapre");
                        alert.setDetail("");
                        bIsAlert = true;
                    } else if (getModel().getFoundProfiles()==0) {
                        alert.setValue(CCAlert.TYPE_INFO);
                        alert.setSummary("APOC.sync.msg.noprofiles");
                        alert.setDetail("");
                        bIsAlert = true;
                    } else if (getModel().getComparedProfiles()>0 && getModel().getNumRows()<1) {
                        alert.setValue(CCAlert.TYPE_INFO);
                        alert.setSummary("APOC.sync.msg.allequal");
                        alert.setDetail("");
                        bIsAlert = true;
                    }
                    if (getModel().getFoundProfiles()!=getModel().getComparedProfiles()) {
                        if (getModel().getComparedProfiles()==0) {
                            alert.setSummary("APOC.sync.msg.nocompare");
                        } else {
                            alert.setSummary("APOC.sync.msg.partialcompare");
                        }
                        alert.setDetail("APOC.sync.msg.compare.detail");
                        alert.setValue(CCAlert.TYPE_WARNING);
                        bIsAlert = true;
                    }
                }
            }
        }
        return bIsAlert;
    }
    
    public boolean beginActionCompletedDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        return m_bIsIdle;
    }
    
    public boolean beginActionCyclingDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        return !beginActionCompletedDisplay(event);
    }
    
    public boolean beginCurrentActionDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCStaticTextField child = (CCStaticTextField) getChild(event.getChildName());
        child.setValue(Toolbox2.getI18n(getModel().getCurrentAction(), getModel().getCurrentNumbers()));
        return true;
    }
    
    public boolean beginElapsedTimeDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCStaticTextField child = (CCStaticTextField) getChild(event.getChildName());
        child.setValue(Toolbox2.getI18n("APOC.sync.waitmsg.time", getModel().getElapsedTime()));
        return true;
    }
    
    public boolean beginContextTiledViewDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        return (beginActionCompletedDisplay(event) && getModel().getNumRows()>0);
    }
    
    public boolean beginHelpDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        return (beginActionCompletedDisplay(event) && !beginContextTiledViewDisplay(event));
    }
    
    public boolean beginSyncListDisplay(ChildDisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        CCHiddenField syncList = (CCHiddenField) getChild("SyncList");
        syncList.setValue("");
        return true;
    }
    
    private ContextTiledModel getModel() {
        //changed because this model is executed in a seperate thread now and has thus no access to the request context
        ContextTiledModel model = (ContextTiledModel) RequestManager.getSession().getAttribute("ThreadSyncModel");
        if (model==null) {
            model = new ContextTiledModel();
            getSession().setAttribute("ThreadSyncModel", model);
        }
        return model;
    }
    
    private void setParameterValues(String sPrefix, String sContextNameKey, String sContextKey, String sEntityTypeKey, String sEntityIdKey)
    throws ModelControlException {
        try {
            CCStaticTextField   contextName     = (CCStaticTextField) getChild(sPrefix+"ContextJS");
            CCDropDownMenu      contextMenu     = (CCDropDownMenu) getChild(sPrefix+"Context");
            CCStaticTextField   contextPath     = (CCStaticTextField) getChild(sPrefix+"Path");
            CCButton            contextButton   = (CCButton) getChild(sPrefix+"BrowseButton");
            HashMap             syncEnv         = (HashMap) getSession().getAttribute(Constants.SYNC_ENVIRONMENT);
            PolicyManager           manager         = (PolicyManager) syncEnv.get(sContextKey);
            
            int         nOption         = 0;
            Iterator    backendNames    = Toolbox2.getBackendNames();
            OptionList  options         = new OptionList();
            
            while (backendNames.hasNext()) {
                String backendName = (String)backendNames.next();
                options.add(nOption++, new CCOption(backendName, backendName, false));
            }
            
            contextMenu.setOptions(options);
            
            contextName.setValue(Toolbox2.encode((String)syncEnv.get(sContextNameKey)));
            contextMenu.setValue(syncEnv.get(sContextNameKey));
            String sEntityType = (String) syncEnv.get(sEntityTypeKey);
            String sEntityId = (String) syncEnv.get(sEntityIdKey);
            if (sEntityId==null) {
                contextPath.setValue("/");
            } else {
                String sEntityPath = Toolbox2.getParentagePath(manager.getEntity(sEntityType, sEntityId), false, true, "/", manager);
                contextPath.setValue(sEntityPath);
            }
            
        } catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    class SyncCompThread extends Thread {
        private HttpSession  m_session   = null;
        private LinkedList   m_syncList  = null;
        SyncCompThread(HttpSession session, LinkedList syncList) {
            m_session  = session;
            m_syncList = syncList;
        }
        
        public void run() {
            ContextTiledModel model = (ContextTiledModel) m_session.getAttribute("ThreadSyncModel");
            try {
                HashMap syncEnv                 = (HashMap) m_session.getAttribute(Constants.SYNC_ENVIRONMENT);
                PolicyManager leftManager       = (PolicyManager) syncEnv.get(SyncLoginViewBean.ENV_LEFT_CONTEXT);
                String sLeftEntityId            = (String) syncEnv.get(SyncLoginViewBean.ENV_LEFT_ENTITYID);
                String sLeftEntityType          = (String) syncEnv.get(SyncLoginViewBean.ENV_LEFT_ENTITY_TYPE);
                PolicyManager rightManager      = (PolicyManager) syncEnv.get(SyncLoginViewBean.ENV_RIGHT_CONTEXT);
                String sRightEntityId           = (String) syncEnv.get(SyncLoginViewBean.ENV_RIGHT_ENTITYID);
                String sRightEntityType         = (String) syncEnv.get(SyncLoginViewBean.ENV_RIGHT_ENTITY_TYPE);
                if (m_syncList.size()>0) {
                    model.sync(leftManager, sLeftEntityId, sLeftEntityType, rightManager, sRightEntityId, sRightEntityType, m_syncList, m_session, true, true);
                }
                model.retrieve(leftManager, sLeftEntityId, sLeftEntityType, rightManager, sRightEntityId, sRightEntityType);
            } catch (Exception ex) {
                model.setException(ex);
            }
        }
    }
}
