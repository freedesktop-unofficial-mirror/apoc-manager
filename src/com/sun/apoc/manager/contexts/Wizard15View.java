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

import com.iplanet.jato.model.Model;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingViewBase;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.html.CCPassword;

import com.sun.web.ui.view.wizard.CCWizardPage;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;

import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import java.util.Properties;
/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class Wizard15View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil{

    // The "logical" name for this page.
    public static final String PAGE_NAME = "Wizard15View";

    // Child view names (i.e. display fields).

    public static final String CHILD_USERDN_LABEL =
        "UserDNLabel";
    public static final String CHILD_PASSWORD_LABEL =
        "PasswordLabel";
    public static final String CHILD_USERDN_FIELD =
        "UserDN";
    public static final String CHILD_PASSWORD_FIELD =
        "Password";
    private CCI18N m_I18n;
    
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public Wizard15View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
         m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }

    public Wizard15View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);        
        registerChildren();
    }

    protected void registerChildren() {
        registerChild(CHILD_USERDN_LABEL, CCLabel.class);
        registerChild(CHILD_PASSWORD_LABEL, CCLabel.class);
        registerChild(CHILD_USERDN_FIELD, CCTextField.class);
        registerChild(CHILD_PASSWORD_FIELD, CCPassword.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        if (name.equals(CHILD_USERDN_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.4.admin.dn"));
        } else if (name.equals(CHILD_PASSWORD_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.4.admin.pw"));
        } else if (name.equals(CHILD_USERDN_FIELD)) {
            child = (View)new CCTextField(this, name, null);            
        } else if (name.equals(CHILD_PASSWORD_FIELD)) {
             child = (View)new CCPassword(this, name, null);
        } else {
         throw new IllegalArgumentException(
            "WizardPage15View : Invalid child name [" + name + "]");
        }
        return child;
    }

    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/contexts/Wizard15.jsp";
    }

    public String getErrorMsg() {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        String emsg = null;
        String hostname = (String)wm.getWizardValue(Wizard3View.CHILD_HOST_FIELD);
        String portString = (String)wm.getWizardValue(Wizard3View.CHILD_PORT_FIELD);        
        
        String userDn = (String)wm.getWizardValue(CHILD_USERDN_FIELD);
        String password = (String)wm.getWizardValue(CHILD_PASSWORD_FIELD);
        LDAPConnection conn = (LDAPConnection)wm.getValue(wm.CONNECTION);

        try {
            conn.bind(3, userDn, password);
        } catch (LDAPException e) {
            Object[] args = {userDn, hostname + ":" + portString};
            emsg = m_I18n.getMessage("APOC.wiz.4.alert1", args);
            return emsg;
            // Eat exception.            
        }

        createPolicyManager();
        
        return emsg;
    }

    private String createPolicyManager() {
        PolicyManager policyManager = null;
        StringBuffer ldapBuff = new StringBuffer();
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel(); 
        try {
            Properties selectedBackendProperties = new Properties();
            String host = (String)wm.getValue(Wizard3View.CHILD_HOST_FIELD) ;
            String port = (String)wm.getValue(Wizard3View.CHILD_PORT_FIELD);
            String baseDn = (String)wm.getValue(wm.BASEDN);
            String datastore = "ldap";
            String useAnon = (String)wm.getValue(Wizard4View.CHILD_ANON_ACCESS);
            String useSSL = (String)wm.getValue(Wizard3View.CHILD_USE_SSL);
            if (useSSL.equals("true")) {
                datastore = "ldaps";
            }
            ldapBuff.append(datastore)
                .append("://")
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(baseDn);

            selectedBackendProperties.put(EnvironmentConstants.URL_KEY, ldapBuff.toString());
            
            String userAuthDn = (String)wm.getValue(Wizard15View.CHILD_USERDN_FIELD);
            String userPassword = (String)wm.getValue(Wizard15View.CHILD_PASSWORD_FIELD);
            selectedBackendProperties.setProperty(EnvironmentConstants.USER_KEY, userAuthDn);
            selectedBackendProperties.setProperty(EnvironmentConstants.CREDENTIALS_KEY, userPassword);
            selectedBackendProperties.setProperty(EnvironmentConstants.CREDENTIALS_ENCODING_KEY, EnvironmentConstants.SCRAMBLE_ENCODING);
            
            policyManager = new PolicyManager(selectedBackendProperties);  
            wm.setValue(wm.POLICYMGR, policyManager);

        } catch (SPIException e) {
            Object[] args  = {(String)wm.getValue(Wizard15View.CHILD_USERDN_FIELD), ldapBuff.toString()};
            String errorMsg = e.getLocalizedMessage();
            return errorMsg; 
        }
        return null;
    }      
    
}


