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

import com.sun.web.ui.view.wizard.CCWizardPage;

import com.sun.web.ui.common.CCDebug;
import netscape.ldap.factory.JSSESocketFactory;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPv2;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPAttributeSet;

import java.util.Enumeration;
import java.util.ArrayList;

import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.environment.EnvironmentMgr;
import java.util.Properties;

/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class Wizard3View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil {

    // The "logical" name for this page.
    public static final String PAGE_NAME = "Wizard3View";

    // Child view names (i.e. display fields).

    public static final String CHILD_HOST_LABEL =
        "HostLabel";
    public static final String CHILD_PORT_LABEL =
        "PortLabel";
    public static final String CHILD_HOST_FIELD =
        "HostField";
    public static final String CHILD_PORT_FIELD =
        "PortField";
    public static final String CHILD_SSL_LABEL =
        "SSLLabel";
    public static final String CHILD_USE_SSL =
        "UseSSL";
    /** minimum LDAP port number allowed */
    private final static int LDAP_MINPORTNUMBER = 0;
    /** maximum LDAP port number allowed */
    private final static int LDAP_MAXPORTNUMBER = 65535; 
    private CCI18N m_I18n;

    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public Wizard3View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }

    public Wizard3View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        registerChildren();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Child manipulation methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_HOST_LABEL, CCLabel.class);
        registerChild(CHILD_PORT_LABEL, CCLabel.class);
        registerChild(CHILD_HOST_FIELD, CCTextField.class);
        registerChild(CHILD_PORT_FIELD, CCTextField.class);
        registerChild(CHILD_SSL_LABEL, CCLabel.class);        
        registerChild(CHILD_USE_SSL, CCCheckBox.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        if (name.equals(CHILD_HOST_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.3.host"));
        } else if (name.equals(CHILD_PORT_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.3.port"));
        } else if (name.equals(CHILD_HOST_FIELD)) {
            child = (View)new CCTextField(this, name, null);            
        } else if (name.equals(CHILD_PORT_FIELD)) {
             child = (View)new CCTextField(this, name, "389");
        } else if (name.equals(CHILD_SSL_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.3.ssl"));
        } else if (name.equals(CHILD_USE_SSL)) {
            child = (View)new CCCheckBox(this, name,
            "true", "false", false);
       } else {
        throw new IllegalArgumentException(
            "WizardPage1View : Invalid child name [" + name + "]");
        }
        return child;
    }


     /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/contexts/Wizard3.jsp";
    }

    public void beginDisplay(DisplayEvent event)
    throws ModelControlException {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        Properties properties = (Properties)wm.getValue(wm.PROPERTIES);
        CCTextField hostField = (CCTextField)getChild(CHILD_HOST_FIELD);
        CCTextField portField = (CCTextField)getChild(CHILD_PORT_FIELD);
        String hostname = (String)hostField.getValue();
        if (hostname == null) {
            if (properties != null) {
                String entityUrl = properties.getProperty(EnvironmentConstants.ORGANIZATION_PREFIX
                                                            + EnvironmentConstants.URL_KEY);
                if (entityUrl != null && entityUrl.startsWith("ldap")) {
                    hostField.setValue(EnvironmentMgr.getHostFromURL(entityUrl));
                    portField.setValue(Integer.toString(EnvironmentMgr.getPortFromURL(entityUrl)));
                } 
            }
        }
    }    
    
   
    public String getErrorMsg() {
  
        int SCOPE           = LDAPv2.SCOPE_BASE;
        String FILTER       = "(objectclass=*)";
        String SEARCHBASE   = "";
        String OPEN_LDAP_ID = "structuralObjectClass";
        String SUN_ONE_DS_ID= "vendorName";
        String BASE_DN_ID   = "namingContexts";
        String[] attrs      = {OPEN_LDAP_ID, SUN_ONE_DS_ID, BASE_DN_ID};
        String SUN_ONE_DS   = "Sun Microsystems, Inc.";
        String OPEN_LDAP    = "OpenLDAProotDSE";
 
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        String hostname = (String)wm.getWizardValue(CHILD_HOST_FIELD);
        String portString = (String)wm.getWizardValue(CHILD_PORT_FIELD);
        String useSSL = (String)wm.getWizardValue(CHILD_USE_SSL);
        int port = 389;
        String emsg = null;
        if((portString != null) && (portString.length() != 0)) {
            try {
                port = Integer.parseInt(portString);
                if ((port < LDAP_MINPORTNUMBER) || (port > LDAP_MAXPORTNUMBER)) {
                    Object[] args = {portString};
                    return m_I18n.getMessage("APOC.wiz.3.alert1", args);                     
                }
            } catch (NumberFormatException nfe) {
                Object[] args = {portString};
                return m_I18n.getMessage("APOC.wiz.3.alert1", args);               
            }
        }    

        LDAPConnection conn  = null; 

        if(useSSL.equals("true")) {
            conn = new LDAPConnection(new JSSESocketFactory(null));
        } else {
            conn = new LDAPConnection(); 
        }


        try {
            conn.connect(hostname, port);
            wm.setValue(wm.CONNECTION, conn);
        } catch (LDAPException e) {
            CCDebug.trace3(e.getMessage());
            Object[] args = {hostname + ":" + portString};
            return m_I18n.getMessage("APOC.wiz.3.alert2", args);
        }

        wm.setValue(wm.VENDOR_ID, null);
        wm.setValue(wm.BASEDN_LIST, null);        
        
        String backendType = (String)wm.getValue(Wizard2View.CHILD_BACKEND_TYPE);   

       
        /* Get the root DSE by doing a search where:
         - The scope is SCOPE_BASE
         - The base is ""
         - The search filter is "(objectclass=*)"
        */
        try {
            LDAPSearchResults res = conn.search( SEARCHBASE,
                SCOPE, FILTER, attrs, false );

            /* There should be only one entry in the results (the root DSE). */
            while ( res.hasMoreElements() ) {
                LDAPEntry findEntry = (LDAPEntry)res.nextElement();
                /* Get the attributes of the root DSE. */
                LDAPAttributeSet findAttrs = findEntry.getAttributeSet();
                Enumeration enumAttrs = findAttrs.getAttributes();

                /* Iterate through each attribute. */
                while ( enumAttrs.hasMoreElements() ) {
                    LDAPAttribute anAttr = (LDAPAttribute)enumAttrs.nextElement();
                    /* Get and print the attribute name. */
                    String attrName = anAttr.getName();
                    /* Get the values of the attribute. */
                    if (attrName.equals(OPEN_LDAP_ID)) {
                        Enumeration enumVals = anAttr.getStringValues();
                        /* Get and set each value. */
                        if ( enumVals != null ) {
                            while ( enumVals.hasMoreElements() ) {
                                String aVal = ( String )enumVals.nextElement();
                                if (aVal.equals(OPEN_LDAP)) {
                                    wm.setValue(wm.VENDOR_ID, "1");
                                }
                            } 
                        }
                    } else if (attrName.equals(SUN_ONE_DS_ID)) {
                        Enumeration enumVals = anAttr.getStringValues();
                        /* Get and print each value. */
                        if ( enumVals != null ) {
                            while ( enumVals.hasMoreElements() ) {
                                String aVal = ( String )enumVals.nextElement();
                                if (aVal.equals(SUN_ONE_DS)) {
                                    wm.setValue(wm.VENDOR_ID, "0");
                                }
                            } 
                        }                        
                    } else if (attrName.equals(BASE_DN_ID)) {
                        Enumeration enumVals = anAttr.getStringValues();
                        /* Get and print each value. */
                        if ( enumVals != null ) {
                            ArrayList baseDNs = new ArrayList(); 
                            while ( enumVals.hasMoreElements() ) {
                                String aVal = ( String )enumVals.nextElement();
                                baseDNs.add(aVal);
                            } 
                            wm.setValue(wm.BASEDN_LIST, baseDNs);
                        }                        
                    }

                }
            }
        } catch( LDAPException e ) {
            CCDebug.trace3( "Error: " + e.toString() );
        }

        String vendorId = (String)wm.getValue(wm.VENDOR_ID);
        if (backendType.equals("0")) {    
            if (vendorId != null) {
                if (vendorId.equals("1")) {
                    Object[] args = {m_I18n.getMessage("APOC.wiz.5.openldap")};
                    return m_I18n.getMessage(Wizard10View.checkSchema(conn), args);
                } 
            }
        }
        
        return emsg;
    }

}



