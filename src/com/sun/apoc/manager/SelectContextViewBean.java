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
import com.sun.apoc.spi.PolicyManager;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.model.CCPropertySheetModel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCDropDownMenu;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCOption;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.masthead.CCPrimaryMasthead;
import com.sun.web.ui.view.masthead.CCSecondaryMasthead;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.propertysheet.CCPropertySheet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.ServletException;


public class SelectContextViewBean extends ViewBeanBase {
    
    public static final String PAGE_NAME                = "SelectContext";
    public static final String DEFAULT_DISPLAY_URL      = "/jsp/profiles/Login.jsp";
    public static final String CHILD_ALERT              = "Alert";
    public static final String CHILD_STACKTRACE         = "StackTrace";
    public static final String CHILD_MASTHEAD           = "Masthead";
    public static final String CHILD_CONTEXTLOGIN_TITLE = "ContextLoginTitle";
    public static final String CHILD_CONTEXTLOGIN_SHEET = "ContextLoginSheet";
    public static final String CHILD_CONTEXT_HIDDEN     = "SelectedContext";
    
    private String SESSION_CONSTANT = "SelectContextSessionConstant";
    
    private CCPageTitleModel     m_titleModel           = null;
    private CCPropertySheetModel m_sheetModel           = null;
    
    public SelectContextViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        
        m_titleModel = new CCPageTitleModel();
        
        m_sheetModel = new CCPropertySheetModel(
        RequestManager.getRequestContext().getServletContext(), "/jsp/profiles/LoginSheet.xml");
        String sessionConstant = Toolbox2.getParameter("SessionConstant");
        if (sessionConstant != null && sessionConstant.length() != 0) {
            RequestManager.getRequestContext().getRequest().getSession(true).setAttribute(SESSION_CONSTANT, sessionConstant);    
        }
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
            CCSecondaryMasthead child = new CCSecondaryMasthead(this, name);    
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
    
    public void handleLoginButtonRequest(RequestInvocationEvent event) {
        String contextMenuChange = Toolbox2.getParameter("ContextChange");
        CCDropDownMenu  context = (CCDropDownMenu) getChild("ContextMenu");
        CCTextField     username    = (CCTextField) getChild("UsernameText");
        CCTextField     password    = (CCTextField) getChild("PasswordText");
        String          sContext    = (String) context.getValue();
        String          sUsername   = (String) username.getValue();
        String          sPassword   = (String) password.getValue();

        try {
            if (!(contextMenuChange.equals("true"))) {
                PolicyManager mgr = Toolbox2.createPolicyManager(sContext, sUsername, sPassword, true);
                String sessionConstant = (String)RequestManager.getRequest().getSession(false).getAttribute(SESSION_CONSTANT);
                RequestManager.getRequestContext().getRequest().getSession(true).setAttribute(sessionConstant, mgr);
                PrintWriter pw = getRequestContext().getResponse().getWriter() ;
                StringBuffer buffer = new StringBuffer();
                buffer.append("<html><header>") ;
                buffer.append("<script type=\"text/javascript\">") ;
                buffer.append("window.opener.selectContextCallback('");
                buffer.append(sContext);
                buffer.append("');top.window.close();</script>") ;
                buffer.append("</header><body></body></html>") ;
                pw.write(buffer.toString()) ;
                pw.flush() ;
            } else {
                forwardTo();
            }
        } catch (Exception e) {
            Toolbox2.prepareErrorDisplay(e, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE));
            forwardTo();
        }
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);

        CCDropDownMenu menu = (CCDropDownMenu) getChild("ContextMenu");
        int nOption = 0;
        Iterator backendNames = Toolbox2.getBackendNames();
        OptionList options = new OptionList();

        while (backendNames.hasNext()) {
            String backendName = (String)backendNames.next();
            options.add(nOption++, new CCOption(backendName, backendName, false));
        }

        ((CCDropDownMenu)menu).setOptions(options);
        String sContext = (String)menu.getValue();
        if ( (sContext==null) || (sContext.length()==0) ) {
            String sHrefContext = Toolbox2.getParameter("ContextName");
            menu.setValue(sHrefContext); 
        }
        
        sContext = (String) menu.getValue();
        CCTextField username    = (CCTextField) getChild("UsernameText");
        CCTextField password    = (CCTextField) getChild("PasswordText");
        String backendType = Toolbox2.getBackendType(sContext);
        if (backendType != null && backendType.equals("file")) {
            username.setDisabled(true);
            password.setDisabled(true);
        }

        try {
            HashMap authorizedContexts = (HashMap) RequestManager.getRequestContext().getRequest().getSession(true).getAttribute(Constants.AUTH_CONTEXTS);
            String sUsername = "";
            String sPassword = ""; 
            username.setValue(sUsername);
            password.setValue(sPassword);
            if (authorizedContexts!=null) {
                if (authorizedContexts.containsKey(sContext)) {
                    sUsername = ((String[]) authorizedContexts.get(sContext))[0];
                    sPassword = ((String[]) authorizedContexts.get(sContext))[1];
                }
            }
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
