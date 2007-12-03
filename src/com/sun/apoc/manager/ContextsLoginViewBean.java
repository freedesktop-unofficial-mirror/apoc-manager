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
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.management.services.authentication.UserPrincipal;
import com.sun.management.services.authentication.PasswordCredential;
import com.sun.web.ui.model.CCMastheadModel;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.model.CCPropertySheetModel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.masthead.CCPrimaryMasthead;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.propertysheet.CCPropertySheet;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.Subject;
import javax.servlet.ServletException;


public class ContextsLoginViewBean extends ViewBeanBase {
    
    public static final String PAGE_NAME                = "ContextsLogin";
    public static final String DEFAULT_DISPLAY_URL      = "/jsp/contexts/Login.jsp";
    public static final String CHILD_ALERT              = "Alert";
    public static final String CHILD_STACKTRACE         = "StackTrace";
    public static final String CHILD_MASTHEAD           = "Masthead";
    public static final String CHILD_CONTEXTLOGIN_TITLE = "ContextLoginTitle";
    public static final String CHILD_CONTEXTLOGIN_SHEET = "ContextLoginSheet";
    public static final String CHILD_CONTEXT_HIDDEN     = "SelectedContext";
    //
    private CCPageTitleModel     m_titleModel           = null;
    private CCPropertySheetModel m_sheetModel           = null;
    
    public ContextsLoginViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        
        m_titleModel = new CCPageTitleModel();
        
        m_sheetModel = new CCPropertySheetModel(
        RequestManager.getRequestContext().getServletContext(), "/jsp/contexts/LoginSheet.xml");
        
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_ALERT,              CCAlertInline.class);
        registerChild(CHILD_STACKTRACE,         CCStaticTextField.class);
        registerChild(CHILD_MASTHEAD,           CCPrimaryMasthead.class);
        registerChild(CHILD_CONTEXTLOGIN_TITLE, CCPageTitle.class);
        registerChild(CHILD_CONTEXTLOGIN_SHEET, CCPropertySheet.class);
        registerChild(CHILD_CONTEXT_HIDDEN,     CCHiddenField.class);
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
            CCMastheadModel model = new CCMastheadModel();
            CCPrimaryMasthead child = new CCPrimaryMasthead(this, model, name);
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
        else if (name.equals(CHILD_CONTEXT_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, name, null);
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
    
    public void handleCancelButtonRequest(RequestInvocationEvent event) {
        getViewBean(WelcomeViewBean.class).forwardTo(getRequestContext());
    }
    
    public void handleLoginButtonRequest(RequestInvocationEvent event) {
        CCHiddenField   context     = (CCHiddenField) getChild(CHILD_CONTEXT_HIDDEN);
        CCTextField     username    = (CCTextField) getChild("UsernameText");
        CCTextField     password    = (CCTextField) getChild("PasswordText");
        String          sContext    = (String) context.getValue();
        String          sUsername   = (String) username.getValue();
        String          sPassword   = (String) password.getValue();
        
        try {
            Toolbox2.setPolicyManager(sContext, sUsername, sPassword);
            getViewBean(ThreepaneViewBean.class).forwardTo(getRequestContext());
        } catch (Exception e) {
            Toolbox2.prepareErrorDisplay(e, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE));
            forwardTo();
        }
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);

        CCHiddenField   context = (CCHiddenField) getChild(CHILD_CONTEXT_HIDDEN);
        String          sContext= (String) context.getValue();
        String          sUsername = null;
        String          sPassword = null;
        if ( (sContext==null) || (sContext.length()==0) ) {
            String sHrefContext = Toolbox2.getParameter("NameHref");
            context.setValue(sHrefContext); 
        }
        
        sContext = (String) context.getValue();
        CCStaticTextField contextText = (CCStaticTextField) getChild("ContextText");
        contextText.setValue(sContext);

        try {
            Toolbox2.unsetPolicyManager();
            SSOTokenManager tokenmanger     = SSOTokenManager.getInstance();
            SSOToken        token           = tokenmanger.createSSOToken(RequestManager.getRequest());
            Subject         subject         = token.getSubject();

            if (subject != null) {
                Set      principals     = subject.getPrincipals(UserPrincipal.class);
                Set      credentials    = subject.getPrivateCredentials(PasswordCredential.class);
                Iterator principalIter  = principals.iterator();
                Iterator credentialIter = credentials.iterator();

                while ((principalIter.hasNext()) && (sUsername == null)) {
                    sUsername = ((UserPrincipal) principalIter.next()).getUserName();
                }

                while ((credentialIter.hasNext()) && (sPassword == null)) {
                    sPassword = new String(((PasswordCredential) credentialIter.next()).getUserPassword());
                }
            }
            try {
                // test the console login credentials and default them if theyre good
                Toolbox2.createPolicyManager(sContext, sUsername, sPassword, false);
                CCTextField username    = (CCTextField) getChild("UsernameText");
                CCTextField password    = (CCTextField) getChild("PasswordText");
                username.setValue(sUsername);
                password.setValue(sPassword);
            } catch (Exception e) {
                // if the login credentials dont work then dont default the textfields
            }

        } catch (Exception e) {
            Toolbox2.prepareErrorDisplay(e, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE));
        }
    }
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        return ((alert.getSummary() != null) && (alert.getSummary().length() > 0));
    }
}
