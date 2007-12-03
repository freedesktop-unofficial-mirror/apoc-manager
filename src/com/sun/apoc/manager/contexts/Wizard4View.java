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
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingViewBase;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.html.CCCheckBox;
import com.sun.web.ui.view.html.CCPassword;

import com.sun.web.ui.view.wizard.CCWizardPage;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPEntry;

import java.util.Enumeration;

import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import java.util.Properties;

/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class Wizard4View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil{

    // The "logical" name for this page.
    public static final String PAGE_NAME = "Wizard4View";

    // Child view names (i.e. display fields).

    public static final String CHILD_ANON_ACCESS =
        "AnonAccess";    
    public static final String CHILD_ANON_ACCESS_LABEL =
        "AnonAccessLabel";    
    public static final String CHILD_NONANON_USERDN_LABEL =
        "NonAnonUserDNLabel";
    public static final String CHILD_NONANON_PASSWORD_LABEL =
        "NonAnonPasswordLabel";
    public static final String CHILD_NONANON_USERDN_FIELD =
        "NonAnonUserDN";
    public static final String CHILD_NONANON_PASSWORD_FIELD =
        "NonAnonPassword";
    private CCI18N m_I18n;
    
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public Wizard4View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
         m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }

    public Wizard4View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);        
        registerChildren();
    }

    protected void registerChildren() {
        registerChild(CHILD_ANON_ACCESS_LABEL, CCLabel.class);        
        registerChild(CHILD_ANON_ACCESS, CCCheckBox.class);        
        registerChild(CHILD_NONANON_USERDN_LABEL, CCLabel.class);
        registerChild(CHILD_NONANON_PASSWORD_LABEL, CCLabel.class);
        registerChild(CHILD_NONANON_USERDN_FIELD, CCTextField.class);
        registerChild(CHILD_NONANON_PASSWORD_FIELD, CCPassword.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        if (name.equals(CHILD_ANON_ACCESS_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.4.anon"));
        } else if (name.equals(CHILD_ANON_ACCESS)) {
            child = (View)new CCCheckBox(this, name,
            "true", "false", true);
        } else if (name.equals(CHILD_NONANON_USERDN_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.4.user.dn"));
        } else if (name.equals(CHILD_NONANON_PASSWORD_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.4.user.pw"));
        } else if (name.equals(CHILD_NONANON_USERDN_FIELD)) {
            child = (View)new CCTextField(this, name, null);            
        } else if (name.equals(CHILD_NONANON_PASSWORD_FIELD)) {
             child = (View)new CCPassword(this, name, null);
        } else {
         throw new IllegalArgumentException(
            "WizardPage4View : Invalid child name [" + name + "]");
        }
        return child;
    }

    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/contexts/Wizard4.jsp";
    }

    public void beginDisplay(DisplayEvent event)
    throws ModelControlException {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        Properties properties = (Properties)wm.getValue(wm.PROPERTIES);
        CCCheckBox useAnon = (CCCheckBox)getChild(CHILD_ANON_ACCESS);
        CCTextField username = (CCTextField)getChild(CHILD_NONANON_USERDN_FIELD);
        CCPassword password = (CCPassword)getChild(CHILD_NONANON_PASSWORD_FIELD);
        String usernameValue = (String)username.getValue();
        if (usernameValue == null) {
            if (properties != null) {
                String oldUsernameValue = properties.getProperty(EnvironmentConstants.ORGANIZATION_PREFIX
                                                            + EnvironmentConstants.LDAP_AUTH_USER_KEY);
                String oldPassword = properties.getProperty(EnvironmentConstants.ORGANIZATION_PREFIX
                                                            + EnvironmentConstants.LDAP_AUTH_PASSWORD_KEY);
                if (oldUsernameValue != null) {
                    properties.remove(EnvironmentConstants.ORGANIZATION_PREFIX
                                                            + EnvironmentConstants.LDAP_AUTH_USER_KEY);                        
                    useAnon.setChecked(false);
                    username.setValue(oldUsernameValue);
                    password.setValue(oldPassword);
                } 
            }
        }
    }   
    
    public String getErrorMsg() {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        String hostname = (String)wm.getWizardValue(Wizard3View.CHILD_HOST_FIELD);
        String portString = (String)wm.getWizardValue(Wizard3View.CHILD_PORT_FIELD);        
        String baseDN = (String)wm.getWizardValue(wm.BASEDN);       
        String backendType = (String)wm.getValue(Wizard2View.CHILD_BACKEND_TYPE);   

        String nonAnonUserDn = (String)wm.getWizardValue(CHILD_NONANON_USERDN_FIELD);
        String nonAnonPassword = (String)wm.getWizardValue(CHILD_NONANON_PASSWORD_FIELD);
        String useAnon = (String)wm.getWizardValue(CHILD_ANON_ACCESS);
        
        LDAPConnection conn = (LDAPConnection)wm.getValue(wm.CONNECTION);

        String emsg = null;
        if (useAnon.equals("true")) {
            nonAnonUserDn = "";
            nonAnonPassword = "";            
        }
  
        try {
            conn.bind(3, nonAnonUserDn, nonAnonPassword);
        } catch (LDAPException e) {
            Object[] args = {nonAnonUserDn, hostname + ":" + portString};
            emsg = m_I18n.getMessage("APOC.wiz.4.alert1", args);
            return emsg;
            // Eat exception.            
        }
        // anon or non anon, both must be able to read server
        LDAPEntry foundEntry = null;
        try {
            foundEntry = conn.read( nonAnonUserDn );
        } catch ( LDAPException e ) {
            Object[] args = {nonAnonUserDn, hostname + ":" + portString};
            emsg = m_I18n.getMessage("APOC.wiz.4.alert3", args);
            return emsg;
            // Eat exception.      
        }
        try {
            LDAPEntry entry = conn.read(baseDN);
        } catch (LDAPException e) {
             if ( e.getLDAPResultCode() == LDAPException.NO_SUCH_OBJECT) {
                Object[] args = {baseDN, hostname + ":" + portString};
                emsg = m_I18n.getMessage("APOC.wiz.8.alert", args);
                return emsg;
             } else {
                if (useAnon.equals("true")) {
                    nonAnonUserDn = m_I18n.getMessage("APOC.wiz.10.anonymous");
                }
                Object[] args = {nonAnonUserDn, hostname + ":" + portString};
                emsg = m_I18n.getMessage("APOC.wiz.4.alert3", args);
                return emsg;             
             }
        }
        //Check to see if an older version of APOC has been installed
        String services = "ou=ApocRegistry,ou=default,ou=OrganizationConfig,ou=1.0,ou=ApocService,ou=services," + baseDN;
        wm.setValue(wm.EXISTING_INSTALL, "false");
        wm.setValue(wm.APOC1_INSTALL, "false");
        try {
            LDAPEntry tryEntry = conn.read(services);
            //  Active Dir sometimes returns null if it cant read the services branch
            if (tryEntry == null) {
                if (useAnon.equals("true")) {
                    nonAnonUserDn = m_I18n.getMessage("APOC.wiz.10.anonymous");
                }
                Object[] args = {nonAnonUserDn, hostname + ":" + portString};
                emsg = m_I18n.getMessage("APOC.wiz.4.alert3", args);
                return emsg;
            }
            LDAPAttribute attr = tryEntry.getAttribute("sunkeyvalue");
            if (attr != null) {
                Enumeration en = attr.getStringValues();
                while (en.hasMoreElements()) {
                    String value = (String)en.nextElement();
                    // Versions of APOC older than 2.0 do not contain the ApocVersion attribute in the metaconfig
                    if (value.startsWith("organizationalmapping=")) {
                        wm.setValue(wm.EXISTING_INSTALL, "true"); 
                        if (value.indexOf("ApocVersion=") == -1) {
                             wm.setValue(wm.APOC1_INSTALL, "true"); 
                        }
                    }
                }
            }

        } catch (LDAPException e) {
             if ( e.getLDAPResultCode() == LDAPException.INSUFFICIENT_ACCESS_RIGHTS ) {
                if (useAnon.equals("true")) {
                    nonAnonUserDn = m_I18n.getMessage("APOC.wiz.10.anonymous");
                }
                Object[] args = {nonAnonUserDn, hostname + ":" + portString};
                emsg = m_I18n.getMessage("APOC.wiz.4.alert3", args);
             }

        }        
        
        
        return emsg;
    }

}


