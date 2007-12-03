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
import com.sun.web.ui.view.html.CCTextArea;

import com.sun.web.ui.view.wizard.CCWizardPage;

import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.environment.EnvironmentMgr;
import com.sun.apoc.spi.ldap.environment.LdapEnvironmentMgr;
import com.sun.web.ui.view.html.CCHiddenField;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.factory.JSSESocketFactory;

/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class Wizard7View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil {

    // The "logical" name for this page.
    public static final String PAGE_NAME = "Wizard7View";

    // Child view names (i.e. display fields).

    public static final String CHILD_METACONFIG_LABEL =
        "MetaConfigLabel";
    public static final String CHILD_METACONFIG =
        "MetaConfig";
    public static final String CHILD_IS_FIRST_VIEW =
        "isFirstView7";
    private CCI18N m_I18n;

    
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public Wizard7View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);

    }

    public Wizard7View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        registerChildren();
    }


    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_IS_FIRST_VIEW, CCHiddenField.class);
        registerChild(CHILD_METACONFIG_LABEL, CCLabel.class);
        registerChild(CHILD_METACONFIG, CCTextArea.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        if (name.equals(CHILD_METACONFIG_LABEL)) {
            child = (View)new CCLabel(this,
                name, m_I18n.getMessage("APOC.wiz.7.adapt"));
        } else if (name.equals(CHILD_METACONFIG)) {
            child = (View)new CCTextArea(this, name, null);
            AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
            String metaConfig = (String)wm.getValue(wm.METACONFIG);
            ((CCTextArea)child).setValue(metaConfig);
        } else if (name.equals(CHILD_IS_FIRST_VIEW)) {
            child = (View)new CCHiddenField(this, name, "true");
        } else {
        throw new IllegalArgumentException(
            "WizardPage7View : Invalid child name [" + name + "]");
        }
        return child;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // CCWizardPage methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/contexts/Wizard7.jsp";
    }

    public void beginDisplay(DisplayEvent event)
    throws ModelControlException {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        Properties properties = (Properties)wm.getValue(wm.PROPERTIES);
        CCTextArea metaConfig = (CCTextArea)getChild(CHILD_METACONFIG);
        CCHiddenField isFirstView = (CCHiddenField)getChild(CHILD_IS_FIRST_VIEW);
        if (isFirstView.getValue().equals("true") && properties != null) {
            String metaConfigLocation = properties.getProperty(EnvironmentConstants.LDAP_META_CONF_PREFIX
                                                                + EnvironmentConstants.URL_KEY);
            String entityUrl = properties.getProperty(EnvironmentConstants.ORGANIZATION_PREFIX
                                                                + EnvironmentConstants.URL_KEY);
            if (metaConfigLocation == null) {
                if (entityUrl.startsWith("ldap")) {
                    String baseDN = LdapEnvironmentMgr.getBaseEntryFromURL(entityUrl);
                    String services = "ou=ApocRegistry,ou=default,ou=OrganizationConfig,ou=1.0,ou=ApocService,ou=services," + baseDN;
                    try {
                        LDAPConnection conn  = null; 
                        String hostname = EnvironmentMgr.getHostFromURL(entityUrl);
                        int port = EnvironmentMgr.getPortFromURL(entityUrl);
                        if(entityUrl.startsWith("ldaps")) {
                            conn = new LDAPConnection(new JSSESocketFactory(null));
                        } else {
                            conn = new LDAPConnection(); 
                        }
                        conn.connect(hostname, port);
                        // try an anon connection, if not available use the default metaconfig   
                        conn.bind("", "");
        
                        LDAPEntry tryEntry = conn.read(services);
                        if (tryEntry != null) {
                            LDAPAttribute attr = tryEntry.getAttribute("sunkeyvalue");
                            if (attr != null) {
                                Enumeration en = attr.getStringValues();
                                while (en.hasMoreElements()) {
                                    String value = (String)en.nextElement();
                                    String key = "organizationalmapping=";
                                    if (value.startsWith(key)) {
                                        value = value.substring(key.length());
                                        metaConfig.setValue(value); 
                                    }
                                }
                            }
                        }

                    } catch (LDAPException e) {} // if an exception occurs use the default metaconfig values                    
                } 
            } else {
                 if (metaConfigLocation.startsWith("file")) {
                    String filepath = EnvironmentMgr.getPathFromURL(metaConfigLocation);
                    StringBuffer metaConfigBuffer = new StringBuffer();
                    try {
                        File metaConfigFile = new File(filepath + System.getProperty("file.separator") + "OrganizationMapping.properties");
                        if (metaConfigFile.exists()) {
                            FileInputStream fi = new FileInputStream(metaConfigFile);

                            int c;
                            while ((c = fi.read()) != -1) {
                                String aChar = new Character((char)c).toString();
                                metaConfigBuffer.append(aChar);
                            }
                            fi.close() ;
                            metaConfig.setValue(metaConfigBuffer.toString());
                        }
                    } catch(IOException ioe) {}  // if an exception occurs use the default metaconfig values
                 }
            }
        }
        isFirstView.setValue("false");
    }
    
    
    
    public String getErrorMsg() {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        String metaConfig = (String)wm.getWizardValue(CHILD_METACONFIG);
        wm.setValue(wm.METACONFIG, metaConfig);

        return null;
    }
}


