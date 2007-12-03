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
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.model.CCPropertySheetModel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.masthead.CCSecondaryMasthead;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.propertysheet.CCPropertySheet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import javax.servlet.ServletException;

public class SyncLoginViewBean extends ViewBeanBase {
    
    public static final String PAGE_NAME                    = "SyncLogin";
    public static final String DEFAULT_DISPLAY_URL          = "/jsp/sync/Login.jsp";
    public static final String CHILD_ALERT                  = "Alert";
    public static final String CHILD_STACKTRACE             = "StackTrace";
    public static final String CHILD_MASTHEAD               = "Masthead";
    public static final String CHILD_FORM                   = "ContextLoginForm";
    public static final String CHILD_CONTEXTLOGIN_TITLE     = "ContextLoginTitle";
    public static final String CHILD_CONTEXTLOGIN_SHEET     = "ContextLoginSheet";
    public static final String CHILD_LEFTCONTEXT_HREF       = "LeftContext";
    public static final String CHILD_RIGHTCONTEXT_HREF      = "RightContext";
    
    public static final String ENV_LEFT_CONTEXT      = "LeftContext";
    public static final String ENV_LEFT_CONTEXTNAME  = "LeftContextName";
    public static final String ENV_LEFT_ENTITYID     = "LeftEntityId";
    public static final String ENV_LEFT_ENTITY_TYPE  = "LeftEntityType";
    public static final String ENV_RIGHT_CONTEXT     = "RightContext";
    public static final String ENV_RIGHT_CONTEXTNAME = "RightContextName";
    public static final String ENV_RIGHT_ENTITYID    = "RightEntityId";
    public static final String ENV_RIGHT_ENTITY_TYPE = "RightEntityType";
    
    //
    private CCPageTitleModel     m_titleModel           = null;
    private CCPropertySheetModel m_sheetModel           = null;
    
    public SyncLoginViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        
        m_titleModel = new CCPageTitleModel();
        m_sheetModel = new CCPropertySheetModel(
        RequestManager.getRequestContext().getServletContext(), "/jsp/sync/LoginSheet.xml");
        
        registerChildren();
    }
        
    protected void registerChildren() {
        registerChild(CHILD_ALERT,              CCAlertInline.class);
        registerChild(CHILD_STACKTRACE,         CCStaticTextField.class);
        registerChild(CHILD_MASTHEAD,           CCSecondaryMasthead.class);
        registerChild(CHILD_FORM,               CCForm.class); 
        registerChild(CHILD_CONTEXTLOGIN_TITLE, CCPageTitle.class);
        registerChild(CHILD_CONTEXTLOGIN_SHEET, CCPropertySheet.class);
        registerChild(CHILD_LEFTCONTEXT_HREF,   CCHref.class);
        registerChild(CHILD_RIGHTCONTEXT_HREF,  CCHref.class);
        m_titleModel.registerChildren(this);
        m_sheetModel.registerChildren(this);
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
        else if (name.equals(CHILD_MASTHEAD)) {
            CCSecondaryMasthead child = new CCSecondaryMasthead(this,name);
            return child;
        }
        else if (name.equals(CHILD_FORM)) {
            View child = new CCForm(this, name);
            return child;
        } 
        else if (name.equals(CHILD_CONTEXTLOGIN_TITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_titleModel, name);
            return child;
        }
        else if (name.equals(CHILD_CONTEXTLOGIN_SHEET)) {
            CCPropertySheet child = new CCPropertySheet(this, m_sheetModel, name);
            return child;
        }
        else if (name.equals(CHILD_LEFTCONTEXT_HREF) || name.equals(CHILD_RIGHTCONTEXT_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }
        else if ((m_titleModel != null) && m_titleModel.isChildSupported(name)) {
            View child = m_titleModel.createChild(this, name);
            return child;
        }
        else if ((m_sheetModel != null) && m_sheetModel.isChildSupported(name)) {
            View child = m_sheetModel.createChild(this, name);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleLeftContextRequest(RequestInvocationEvent event) {
        handleLeftRightContextRequest();
    }

    public void handleRightContextRequest(RequestInvocationEvent event) {
        handleLeftRightContextRequest();
    }

    public void handleLeftRightContextRequest() {
        String      sLeftContext        = Toolbox2.getParameter("LeftContext");
        String      sRightContext       = Toolbox2.getParameter("RightContext");
        HashMap     authorizedContexts  = (HashMap) getSession().getAttribute(Constants.AUTH_CONTEXTS);
        HashMap     syncEnv             = (HashMap) getSession().getAttribute(Constants.SYNC_ENVIRONMENT);
        
        //TPF_TODO: redesign synvEnv as model instead of being a HashMap
        if (syncEnv==null) {
            syncEnv = new HashMap();
            getSession().setAttribute(Constants.SYNC_ENVIRONMENT, syncEnv);
        }
        
        if (sLeftContext.length()>0) {
            syncEnv.put(ENV_LEFT_CONTEXTNAME, sLeftContext);
            syncEnv.remove(ENV_LEFT_CONTEXT);
            syncEnv.remove(ENV_LEFT_ENTITYID);
            syncEnv.remove(ENV_LEFT_ENTITY_TYPE);
            if (authorizedContexts!=null && authorizedContexts.containsKey(sLeftContext)) {
                String      sUsername   = ((String[]) authorizedContexts.get(sLeftContext))[0];
                String      sPassword   = ((String[]) authorizedContexts.get(sLeftContext))[1];
                PolicyManager   manager     = null;
                try {
                    manager = Toolbox2.createPolicyManager(sLeftContext, sUsername, sPassword, true);
                } catch (Exception e) {}
                if (manager!=null) {
                    syncEnv.put(ENV_LEFT_CONTEXT, manager);
                }
            }
        }
        if (sRightContext.length()>0) {
            syncEnv.put(ENV_RIGHT_CONTEXTNAME, sRightContext);
            syncEnv.remove(ENV_RIGHT_CONTEXT);
            syncEnv.remove(ENV_RIGHT_ENTITYID);
            syncEnv.remove(ENV_RIGHT_ENTITY_TYPE);
            if (authorizedContexts!=null && authorizedContexts.containsKey(sRightContext)) {
                String      sUsername   = ((String[]) authorizedContexts.get(sRightContext))[0];
                String      sPassword   = ((String[]) authorizedContexts.get(sRightContext))[1];
                PolicyManager   manager     = null;
                try {
                    manager = Toolbox2.createPolicyManager(sRightContext, sUsername, sPassword, true);
                } catch (Exception e) {}
                if (manager!=null) {
                    syncEnv.put(ENV_RIGHT_CONTEXT, manager);
                }
            }
        }
        
        if ((syncEnv.get(ENV_LEFT_CONTEXT)==null) || (syncEnv.get(ENV_RIGHT_CONTEXT)==null)) {
            forwardTo();
        } else {
            getViewBean(SyncIndexViewBean.class).forwardTo(getRequestContext());
        }
    }
    
    public void handleLoginButtonRequest(RequestInvocationEvent event) {
        HashMap     syncEnv             = (HashMap) getSession().getAttribute(Constants.SYNC_ENVIRONMENT);
        String      sLeftContext        = (String) syncEnv.get(ENV_LEFT_CONTEXTNAME);
        String      sRightContext       = (String) syncEnv.get(ENV_RIGHT_CONTEXTNAME);
        String      sLoginContext       = (syncEnv.get(ENV_LEFT_CONTEXT)==null)?sLeftContext:sRightContext;
        CCTextField username            = (CCTextField) getChild("UsernameText");
        CCTextField password            = (CCTextField) getChild("PasswordText");
        String      sUsername           = (String) username.getValue();
        String      sPassword           = (String) password.getValue();
        
        try {
            PolicyManager manager = Toolbox2.createPolicyManager(sLoginContext, sUsername, sPassword, true);
            if (syncEnv.get(ENV_LEFT_CONTEXT)==null) {
                syncEnv.put(ENV_LEFT_CONTEXT, manager);
            } else {
                syncEnv.put(ENV_RIGHT_CONTEXT, manager);
            }
            if ( sLeftContext!=null && sLeftContext.equals(sRightContext) ) {
                if (syncEnv.get(ENV_LEFT_CONTEXT)==null) {
                    manager = Toolbox2.createPolicyManager(sLoginContext, sUsername, sPassword, true);
                    syncEnv.put(ENV_LEFT_CONTEXT, manager);
                }
                else if (syncEnv.get(ENV_RIGHT_CONTEXT)==null) {
                    manager = Toolbox2.createPolicyManager(sLoginContext, sUsername, sPassword, true);
                    syncEnv.put(ENV_RIGHT_CONTEXT, manager);
                }
            }
            if ((syncEnv.get(ENV_LEFT_CONTEXT)==null) || (syncEnv.get(ENV_RIGHT_CONTEXT)==null)) {
                forwardTo();
            } else {
                getViewBean(SyncIndexViewBean.class).forwardTo(getRequestContext());
            }
        } catch (Exception e) {
            Toolbox2.prepareErrorDisplay(e, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE));
            forwardTo();
        }
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        HashMap             syncEnv         = (HashMap) getSession().getAttribute(Constants.SYNC_ENVIRONMENT);
        String              sLeftContext    = (String) syncEnv.get(ENV_LEFT_CONTEXTNAME);
        String              sRightContext   = (String) syncEnv.get(ENV_RIGHT_CONTEXTNAME);
        String              sLoginContext   = (syncEnv.get(ENV_LEFT_CONTEXT)==null)?sLeftContext:sRightContext;
        CCStaticTextField   contextText     = (CCStaticTextField) getChild("ContextText");
        contextText.setValue(sLoginContext);
        
        if (syncEnv.get(ENV_LEFT_CONTEXT)!=null) {
            CCStaticTextField context = (CCStaticTextField) getChild("ContextLabel");
            context.setValue("APOC.sync.targetrep.label");
            CCPageTitle title = (CCPageTitle) getChild(CHILD_CONTEXTLOGIN_TITLE);
            title.getModel().setPageTitleText("APOC.sync.login.pagetitle2");
        }

        try {
            PolicyManager   manager     = Toolbox2.createPolicyManager(sLoginContext, null, null, false);
            Hashtable   mgrEnv      = manager.getEnvironment();
            String      sUsername   = (String) mgrEnv.get(EnvironmentConstants.USER_KEY);
            String      sPassword   = (String) mgrEnv.get(EnvironmentConstants.CREDENTIALS_KEY);
            CCTextField username    = (CCTextField) getChild("UsernameText");
            CCTextField password    = (CCTextField) getChild("PasswordText");
            username.setValue(sUsername);
            password.setValue(sPassword);
        } catch (Exception e) {}
    }
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        return ((alert.getSummary() != null) && (alert.getSummary().length() > 0));
    }
}
