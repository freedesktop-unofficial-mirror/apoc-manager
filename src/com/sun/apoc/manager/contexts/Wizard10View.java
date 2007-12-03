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

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.model.Model;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingViewBase;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCTextArea;

import com.sun.web.ui.view.wizard.CCWizardPage;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import javax.servlet.ServletException;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPSchema;
import netscape.ldap.LDAPObjectClassSchema;
import netscape.ldap.LDAPAttributeSchema;
import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPDN;
import netscape.ldap.LDAPModification;

import com.sun.web.ui.common.CCDebug;

import java.util.Vector;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.TreeSet;

import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.AssignmentProvider;

import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.ldap.profiles.LdapProfile;
import com.sun.apoc.spi.policies.Policy;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.ldap.entities.LdapEntityType;
/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class Wizard10View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil {

    // The "logical" name for this page.
    public static final String PAGE_NAME = "Wizard10View";
    
    // Child view names (i.e. display fields).

    public static final String CHILD_NAME_LABEL = "NameLabel";
    public static final String CHILD_ENTITY_TYPE_LABEL = "EntityTypeLabel";
    public static final String CHILD_ORGDOM_SECTION_LABEL = "OrgDomSectionLabel";
    public static final String CHILD_GENERAL_SECTION_LABEL = "GeneralSectionLabel";
    public static final String CHILD_TYPE_LABEL = "TypeLabel";
    public static final String CHILD_HOST_LABEL = "HostLabel";
    public static final String CHILD_PORT_LABEL = "PortLabel";
    public static final String CHILD_USERDN_LABEL = "UserDnLabel";
    public static final String CHILD_VENDOR_LABEL = "VendorLabel";
    public static final String CHILD_METACONFIG_LABEL = "MetaConfigurationLabel";
    public static final String CHILD_BASEDN_LABEL = "BaseDnLabel";
    public static final String CHILD_FILEPATH_LABEL = "FilepathLabel";
    public static final String CHILD_NAME_VALUE = "NameValue";
    public static final String CHILD_ENTITY_TYPE_VALUE = "EntityTypeValue";
    public static final String CHILD_TYPE_VALUE = "TypeValue";
    public static final String CHILD_HOST_VALUE = "HostValue";
    public static final String CHILD_PORT_VALUE = "PortValue";
    public static final String CHILD_USERDN_VALUE = "UserDnValue";
    public static final String CHILD_VENDOR_VALUE = "VendorValue";
    public static final String CHILD_MIGRATE_PROFS_LABEL = "MigrateProfsLabel";
    public static final String CHILD_MIGRATE_PROFS_VALUE = "MigrateProfsValue";
    public static final String CHILD_NO_ACTION_LABEL = "NoActionLabel";
    public static final String CHILD_METACONFIG_VALUE = "MetaConfigurationValue";
    public static final String CHILD_BASEDN_VALUE = "BaseDnValue";
    public static final String CHILD_FILEPATH_VALUE = "FilepathValue";

    
    public static final String[] ATTRIBUTE_TYPES = 
                              {" ( 1.3.6.1.4.1.42.2.27.9.1.83 NAME 'sunkeyvalue' DESC 'Attribute to store the encoded key values of the services' SYNTAX 1.3.6.1.4.1.1466.115.121.1.26 X-ORIGIN ( 'Sun ONE Identity Management' 'user defined' ) )",
                               " ( 1.3.6.1.4.1.42.2.27.9.1.84 NAME 'sunxmlkeyvalue' DESC 'Attribute to store the key values in xml format' SYNTAX 1.3.6.1.4.1.1466.115.121.1.26 X-ORIGIN ( 'Sun ONE Identity Management' 'user defined' ) )", 
                               " ( 1.3.6.1.4.1.42.2.27.9.1.81 NAME 'sunsmspriority' DESC 'To store the priority of the service with respect to its siblings' SYNTAX 1.3.6.1.4.1.1466.115.121.1.27 SINGLE-VALUE X-ORIGIN ( 'Sun ONE Identity Management' 'user defined' ) )",
                               " ( 1.3.6.1.4.1.42.2.27.9.1.78 NAME ( 'sunserviceschema' ) DESC 'SMS Attribute to Store xml schema of a particular service' SYNTAX 1.3.6.1.4.1.1466.115.121.1.15  SINGLE-VALUE X-ORIGIN 'Sun ONE Identity Management' )",
                               " ( 1.3.6.1.4.1.42.2.27.9.1.82 NAME ( 'sunpluginschema' ) DESC 'To store the plugin schema information' SYNTAX 1.3.6.1.4.1.1466.115.121.1.15 X-ORIGIN 'Sun ONE Identity Management' )",
                               " ( 1.3.6.1.4.1.42.2.27.9.1.79 NAME ( 'sunserviceid' ) DESC 'Attribute to store the reference to the inherited object' SYNTAX 1.3.6.1.4.1.1466.115.121.1.15  SINGLE-VALUE X-ORIGIN 'Sun ONE Identity Management' )"};
    public static final String[] OBJECT_CLASSES = 
                             {" ( 1.3.6.1.4.1.42.2.27.9.2.25 NAME 'sunservice' DESC 'object containing service information' SUP top STRUCTURAL MUST ou MAY ( labeledUri $ sunserviceschema $ sunkeyvalue $ sunxmlkeyvalue $ sunpluginschema $ description ) X-ORIGIN ( 'Sun ONE Identity Management' 'user defined' ) )",
                              " ( 1.3.6.1.4.1.42.2.27.9.2.27 NAME 'sunservicecomponent' DESC 'Sub-components of the service' SUP top STRUCTURAL MUST ou MAY ( sunserviceid $ sunsmspriority $ sunkeyvalue $ sunxmlkeyvalue $ description ) X-ORIGIN ( 'Sun ONE Identity Management' 'user defined' ) )"};


    public static final String[] ATTRIBUTE_TYPE_NAMES = 
                            {"sunkeyvalue", "sunxmlkeyvalue", "sunsmspriority", 
                             "sunserviceschema", "sunpluginschema", "sunserviceid"};
    public static final String[] OBJECT_CLASS_NAMES = {"sunservice", "sunservicecomponent"};          
    
    public static final String BACKEND = "Backend";
    public static final String ORGDOM_ENTITIES_FILE = "entities.txt";
    private String m_backendType = null;
    private String m_entityType = null;
    private boolean m_noAction = false;
    AddContextWizardPageModel m_wm = null;
    private CCI18N m_I18n;
    
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public Wizard10View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        m_wm = (AddContextWizardPageModel)getDefaultModel();
    }

    public Wizard10View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        m_wm = (AddContextWizardPageModel)getDefaultModel();
        registerChildren();
    }


    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_NAME_LABEL, CCLabel.class);
        registerChild(CHILD_TYPE_LABEL, CCLabel.class);
        registerChild(CHILD_HOST_LABEL, CCLabel.class);
        registerChild(CHILD_PORT_LABEL, CCLabel.class);
        registerChild(CHILD_USERDN_LABEL, CCLabel.class);
        registerChild(CHILD_VENDOR_LABEL, CCLabel.class);
        registerChild(CHILD_METACONFIG_LABEL, CCLabel.class);
        registerChild(CHILD_BASEDN_LABEL, CCLabel.class);
        registerChild(CHILD_FILEPATH_LABEL, CCLabel.class);        
        registerChild(CHILD_NAME_VALUE, CCLabel.class);
        registerChild(CHILD_TYPE_VALUE, CCLabel.class);
        registerChild(CHILD_HOST_VALUE, CCLabel.class);
        registerChild(CHILD_PORT_VALUE, CCLabel.class);
        registerChild(CHILD_USERDN_VALUE, CCLabel.class);
        registerChild(CHILD_VENDOR_VALUE, CCLabel.class);
        registerChild(CHILD_MIGRATE_PROFS_LABEL, CCLabel.class);
        registerChild(CHILD_MIGRATE_PROFS_VALUE, CCLabel.class);
        registerChild(CHILD_NO_ACTION_LABEL, CCLabel.class);
        registerChild(CHILD_BASEDN_VALUE, CCLabel.class);
        registerChild(CHILD_FILEPATH_VALUE, CCLabel.class);   
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        if (m_backendType == null) {
            m_backendType = (String)m_wm.getValue(Wizard2View.CHILD_BACKEND_TYPE);   
        }

        if (name.equals(CHILD_NAME_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.2.name"));
            return child;
        } else if (name.equals(CHILD_TYPE_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.2.type"));
            return child;
        } else if (name.equals(CHILD_NAME_VALUE)) {
            String value = (String)m_wm.getValue(Wizard2View.CHILD_NAME_FIELD);              
            child = (View)new CCLabel(this, name, value);
            return child;
        } else if (name.equals(CHILD_TYPE_VALUE)) {
            String value = "";
            if (m_backendType.equals("0")) {
                value = m_I18n.getMessage("APOC.wiz.2.ldap");
            } else if (m_backendType.equals("1")) {
                value = m_I18n.getMessage("APOC.wiz.2.file");
            } else if (m_backendType.equals("2")) {
                value = m_I18n.getMessage("APOC.wiz.2.hybrid");
            }
            child = (View)new CCLabel(this, name, value);
            return child;
        } else if (name.equals(CHILD_HOST_LABEL)) {
                child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.3.host"));
        } else if (name.equals(CHILD_HOST_VALUE)) {
            String value = (String)m_wm.getValue(Wizard3View.CHILD_HOST_FIELD);    
            child = (View)new CCLabel(this, name, value);
        } else if (name.equals(CHILD_PORT_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.3.port"));
        } else if (name.equals(CHILD_PORT_VALUE)) {
            String value = (String)m_wm.getValue(Wizard3View.CHILD_PORT_FIELD);    
            child = (View)new CCLabel(this, name, value);
        } else if (name.equals(CHILD_USERDN_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.4.user.dn"));
        } else if (name.equals(CHILD_USERDN_VALUE)) {
            String useAnon = (String)m_wm.getValue(Wizard4View.CHILD_ANON_ACCESS);
            String value = (String)m_wm.getValue(Wizard4View.CHILD_NONANON_USERDN_FIELD); 
            if (useAnon.equals("true")) {
                value = m_I18n.getMessage("APOC.wiz.10.anonymous");
            }
            child = (View)new CCLabel(this, name, value);
        } else if (name.equals(CHILD_VENDOR_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.10.vendor"));
         } else if (name.equals(CHILD_NO_ACTION_LABEL)) {
            String noActionString = "APOC.wiz.10.noaction";
            if (m_wm.getValue(m_wm.WIZARD_CONFIG_FILE) != null) {
                noActionString = "APOC.wiz.10.edit.noaction";
            }
            if (m_backendType.equals("0")) {
                String baseDN = (String)m_wm.getWizardValue(m_wm.BASEDN);
                LDAPConnection conn = (LDAPConnection)m_wm.getValue(m_wm.CONNECTION);        
                String hostname = (String)m_wm.getWizardValue(Wizard3View.CHILD_HOST_FIELD);
                String portString = (String)m_wm.getWizardValue(Wizard3View.CHILD_PORT_FIELD);          
                Object[] args = {hostname + ":" + portString + "/" + baseDN};
                child = (View)new CCLabel(this, name, m_I18n.getMessage(noActionString, args));
            } else if (m_backendType.equals("1")) {
                Object[] args = {(String)m_wm.getValue(m_wm.FILEPATH)};
                child = (View)new CCLabel(this, name, m_I18n.getMessage(noActionString, args));
            } else if (m_backendType.equals("2")) {
                String baseDN = (String)m_wm.getWizardValue(m_wm.BASEDN);
                LDAPConnection conn = (LDAPConnection)m_wm.getValue(m_wm.CONNECTION);        
                String hostname = (String)m_wm.getWizardValue(Wizard3View.CHILD_HOST_FIELD);
                String portString = (String)m_wm.getWizardValue(Wizard3View.CHILD_PORT_FIELD);
                String and = m_I18n.getMessage("APOC.report.bothand"); 
                String filepath = (String)m_wm.getValue(m_wm.FILEPATH);
                Object[] args = {hostname + ":" + portString + "/" + baseDN + " " + and + " " + filepath};                
                child = (View)new CCLabel(this, name, m_I18n.getMessage(noActionString, args));
            }
         } else if (name.equals(CHILD_VENDOR_VALUE)) {
            String value = (String)m_wm.getValue(m_wm.VENDOR_ID);             
            if (value.equals("0")) {
                value = m_I18n.getMessage("APOC.wiz.5.sun");
            } else if (value.equals("1")) {
                value = m_I18n.getMessage("APOC.wiz.5.openldap");
            } else if (value.equals("2")) {
                value = m_I18n.getMessage("APOC.wiz.5.ad");
            } else if (value.equals("3")) {
                value = m_I18n.getMessage("APOC.wiz.5.other");
            } 
            child = (View)new CCLabel(this, name, value);
        } else if (name.equals(CHILD_MIGRATE_PROFS_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.10.migrate"));
        } else if (name.equals(CHILD_MIGRATE_PROFS_VALUE)) {
            child = (View)new CCLabel(this, name, null);
         } else if (name.equals(CHILD_METACONFIG_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.10.metaconfig"));
        } else if (name.equals(CHILD_METACONFIG_VALUE)) {
            String value = (String)m_wm.getValue(Wizard7View.CHILD_METACONFIG);    
            child = (View)new CCTextArea(this, name, null);
            ((CCTextArea)child).setValue(value);
            ((CCTextArea)child).setReadOnly(true);           
        } else if (name.equals(CHILD_BASEDN_LABEL)) {
            child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.10.basedn"));
        } else if (name.equals(CHILD_BASEDN_VALUE)) {
            String value = (String)m_wm.getValue(m_wm.BASEDN);   
            child = (View)new CCLabel(this, name, value);
        } else if (name.equals(CHILD_FILEPATH_LABEL)) {
                child = (View)new CCLabel(this, name, m_I18n.getMessage("APOC.wiz.10.path"));
        } else if (name.equals(CHILD_FILEPATH_VALUE)) {
                String value = (String)m_wm.getValue(m_wm.FILEPATH); 
                child = (View)new CCLabel(this, name, value);
        } else {
                throw new IllegalArgumentException(
                    "WizardPage10View : Invalid child name [" + name + "]");
        }
        return child;
    }
    
    public boolean beginDisplayLDAPInfoDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        boolean displayLDAPInfo = false;
        if ((m_backendType.equals("0")) || (m_backendType.equals("2"))) {
            displayLDAPInfo = true;
        }
        return displayLDAPInfo;
    }
    
    public boolean beginDisplayFileBasedInfoDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        boolean displayFileBasedInfo = false;
        if ((m_backendType.equals("1")) || (m_backendType.equals("2"))) {
            displayFileBasedInfo = true;
        }
        return displayFileBasedInfo;
    }
    
     public boolean beginDisplayMigrateProfsDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        CCLabel child = (CCLabel)getChild(CHILD_MIGRATE_PROFS_VALUE);
        String migrateProfiles = (String)m_wm.getValue(Wizard14View.CHILD_MIGRATE);
        String value = "";
        if (migrateProfiles != null && migrateProfiles.equals("0")) {
            value = m_I18n.getMessage("APOC.wiz.6.yes");
        } else {
             value = m_I18n.getMessage("APOC.wiz.6.no");               
        }     
        child.setValue(value);
        String isApoc1 = (String)m_wm.getWizardValue(m_wm.APOC1_INSTALL);
        if (isApoc1.equals("true")) {
            return true;
        } else {
            return false;
        }
    }    
        
    public boolean beginDisplayMetaConfigDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        String value = (String)m_wm.getValue(Wizard6View.CHILD_ADAPT_METACONFIG);   

        boolean displayMetaConfig = false;
        if (value != null && value.equals("1")) {
            displayMetaConfig = true;
        }
        return displayMetaConfig;
    }

    public boolean beginNoActionLabelDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        String showNoAction = "false";
        if ((m_backendType.equals("1")) || (m_backendType.equals("2"))) {
            String overwrite = (String)m_wm.getWizardValue(Wizard13View.CHILD_UPDATE_OVERWRITE);
            if (overwrite != null && overwrite.equals("1")) {
                showNoAction = "true";                
            }
        } else {
            String isExistingInstall = (String)m_wm.getValue(m_wm.EXISTING_INSTALL);
            String isApoc1 = (String)m_wm.getValue(m_wm.APOC1_INSTALL);
            String migrateProfiles = (String)m_wm.getValue(Wizard14View.CHILD_MIGRATE);
            if (isExistingInstall.equals("true")) {
                if (isApoc1.equals("true")) {
                    if (migrateProfiles != null && migrateProfiles.equals("0")) {
                        showNoAction = "false";
                    } else {
                        showNoAction = "true";
                    }
                } else {
                    showNoAction = "true";
                }
            }
            m_wm.setValue(m_wm.NO_ACTION, showNoAction);
        }
        return Boolean.valueOf(showNoAction).booleanValue();
    }
    
    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/contexts/Wizard10.jsp";
    }
    
    public String getErrorMsg() {
        String err = null;
        m_wm.setValue(m_wm.SUCCESS, "true");
        m_backendType = (String)m_wm.getValue(Wizard2View.CHILD_BACKEND_TYPE);   
        boolean noAction = Boolean.valueOf((String)m_wm.getValue(m_wm.NO_ACTION)).booleanValue();
        
        if (noAction) {
            updateBackendsFile();
            disconnect();
        } else {
            if (m_backendType != null) {
                if (m_backendType.equals("0")) {
                    createLDAPBackend();
                } else if (m_backendType.equals("1")) {
                    String root = (String)m_wm.getValue(m_wm.FILEPATH); 
                    String overwrite = (String)m_wm.getWizardValue(Wizard13View.CHILD_UPDATE_OVERWRITE);
                    createFileBasedBackend(root, overwrite, ORGDOM_ENTITIES_FILE);
                } else if (m_backendType.equals("2")) {
                    createHybridBackend();
                }
            }
            if ( ((String)m_wm.getValue(m_wm.SUCCESS)).equals("true")) {
                updateBackendsFile();
            }
        }
        
        return err;
    }
    
    private String createLDAPBackend() {
        LDAPSchema dirSchema = new LDAPSchema();
                                 
        LDAPConnection conn = (LDAPConnection)m_wm.getValue(m_wm.CONNECTION); 
        
        String backendVendor = (String)m_wm.getValue(m_wm.VENDOR_ID);
        if (backendVendor.equals("0")) {
            try {
                dirSchema.fetchSchema(conn);
                for (int i = 0; i < ATTRIBUTE_TYPE_NAMES.length; i++) {
                    LDAPAttributeSchema attrType = dirSchema.getAttribute(ATTRIBUTE_TYPE_NAMES[i]);
                    if (attrType == null) {
                        attrType = new LDAPAttributeSchema(ATTRIBUTE_TYPES[i]);
                        attrType.add(conn);  
                    }              
                }
                for (int i = 0; i < OBJECT_CLASS_NAMES.length; i++) {
                    LDAPObjectClassSchema objClass = dirSchema.getObjectClass(OBJECT_CLASS_NAMES[i]);
                    if (objClass == null) {
                        objClass = new LDAPObjectClassSchema(OBJECT_CLASSES[i]);
                        objClass.add(conn);               
                    }        
                }
            } catch ( LDAPException e ) {
                CCDebug.trace3( e.toString() );
                setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail1"), e.toString());
            }
        }

        
       
        String baseDn = (String)m_wm.getValue(m_wm.BASEDN);

        String services = "ou=services," + baseDn;
        String service  = "ou=ApocService," + services; 
        String version  = "ou=1.0," + service;
        String orgConfig= "ou=OrganizationConfig," + version;
        String def      = "ou=default," + orgConfig;
        String container= "ou=ApocRegistry," + def;

        String orgUnit              = "organizationalUnit";
        String sunService           = "sunService";
        String sunServiceComponent  = "sunServiceComponent";
        
        try {
            createEntry(services, orgUnit);
            createEntry(service, sunService);
            createEntry(version, sunService);
            createEntry(orgConfig, orgUnit);
            createEntry(def, sunServiceComponent);
            createEntry(container, sunServiceComponent);
        } catch ( LDAPException e ) {
            CCDebug.trace3( e.toString() );
            setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail2"), getLDAPError(e));
        }
        
        String storageAttribute = "sunKeyValue";
        try {
            fillAttribute(container, storageAttribute, "organizationalmapping", getOrgMapping());
            fillAttribute(container, storageAttribute, "ldapattributemapping", getUserMapping());
        } catch ( LDAPException e ) {
            CCDebug.trace3( e.toString() );
            setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail3"), getLDAPError(e));
        }
        
        String sunServiceIdAttr = "sunserviceid";
        String sunServiceIdValue = "ApocRegistry";

        try {    
            addAttribute(container, sunServiceIdAttr, sunServiceIdValue);
        } catch ( LDAPException e ) {
            CCDebug.trace3( e.toString() );
            Object[] args = {sunServiceIdAttr, container};        
            setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail4", args), getLDAPError(e));
        }

        //Migrate profiles if requested
        String migrateProfiles = (String)m_wm.getValue(Wizard14View.CHILD_MIGRATE);
        if (migrateProfiles != null && migrateProfiles.equals("0")) {
            migrateProfiles();
        }
        
        disconnect();      
        
        return null;
    }

    private void disconnect() {
        LDAPConnection conn = (LDAPConnection)m_wm.getValue(m_wm.CONNECTION); 
        try {
            conn.disconnect();
        } catch ( LDAPException e ) {
            CCDebug.trace3( e.toString() );
        }           
    }
    
    private String createFileBasedBackend(String root, String overwrite, String filename) {
        // if you want to overwrite an existing one then 
        // delete the file and folders and recreate the default entities file
        if (overwrite == null || overwrite.equals("0")) {
            // Delete the profiles directory; 
            try {
                File profiles = new File(root + "/profiles");
                if (profiles.exists()) {
                    recursiveDelete(profiles);
                    File[] subfiles = profiles.listFiles();
                    profiles.delete();
                }
            } catch (Exception e) {
                CCDebug.trace3("Deleting directory failed!");        
                setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail5"), e.toString());
            }
            // Delete the assignments directory; 
            try{
                File assignments = new File(root + "/assignments");
                if (assignments.exists()) {
                    recursiveDelete(assignments);
                    assignments.delete();
                }
            } catch (Exception e) {
                CCDebug.trace3("Deleting directory failed!");
                setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail5"), e.toString());
            }
            // Delete the entities file; 
            try{
                File entities = new File(root + "/" + filename);
                if (entities.exists() && entities.isFile()) {
                    entities.delete();
                }
            } catch (Exception e) {
                CCDebug.trace3("Deleting entities file failed!");
                setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail5"), e.toString());
            }

            // recreate the profiles and assignments dirs
            File newProfiles = new File(root + "/profiles");
            File newAssignments = new File(root + "/assignments");
            try{
                newProfiles.mkdir();
                newAssignments.mkdir();
            } catch (Exception e) {
                CCDebug.trace3("Creating directory failed!");
                setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail8"), e.toString());
            }            
            // recreate the default demo structure file
            File entities = new File(root + "/" +  filename);
            try {
                if (entities.createNewFile()) {
                    InputStream istream = null;
                    FileOutputStream ostream = new FileOutputStream(entities);
                    istream=getClass().getResource("sample/" + filename).openStream();
                    int c;
                    while ((c = istream.read()) != -1) {
                        String aChar = new Character((char)c).toString();
                        ostream.write(c);
                    }
                    istream.close() ;
                    ostream.close();
                }
            } catch(Exception e)	{
                CCDebug.trace3("Creating default entities file failed!");
                setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail8"), e.toString());
            }   
        }

        return null;
    }

    private String createHybridBackend() {
        String root = (String)m_wm.getValue(m_wm.FILEPATH); 
        String overwrite = (String)m_wm.getWizardValue(Wizard13View.CHILD_UPDATE_OVERWRITE);
        createFileBasedBackend(root, overwrite, ORGDOM_ENTITIES_FILE);
        // if its a new install or you want to overwrite an existing one then 
        // delete the metaconfig files and recreate
        if (overwrite == null || overwrite.equals("0")) {
             // Delete the existing metaconfig files; 
            try{
                File orgMapping = new File(root + "/OrganizationMapping.properties");
                File userMapping = new File(root + "/UserProfileMapping.properties");
                if (orgMapping.exists() && orgMapping.isFile()) {
                    orgMapping.delete();
                }
                if (userMapping.exists() && userMapping.isFile()) {
                    userMapping.delete();
                }
            } catch (Exception e) {
                CCDebug.trace3("Deleting metaconfig file failed!");
                setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail5"), e.toString());
            }
            // Create the metaconfig files
            try {
                File orgMapping = new File(root + "/OrganizationMapping.properties");
                File userMapping = new File(root + "/UserProfileMapping.properties");
                FileWriter orgWriter = new FileWriter(orgMapping);
                FileWriter userWriter = new FileWriter(userMapping);
                orgWriter.write(this.getOrgMapping());
                userWriter.write(this.getUserMapping());
                orgWriter.close();
                userWriter.close();
            } catch(Exception e)	{
                CCDebug.trace3("Creating metaconfig file failed!");
                setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail9"), e.toString());
            }  
        }


        return null;
    }
    
    private void updateBackendsFile() {
        File backendTest = new File(ManagerTableModel.CONFIG_FILE_LOCATION + "backend1.properties");
        try {
            File editWizFile = (File)m_wm.getValue(m_wm.WIZARD_CONFIG_FILE);
            if (editWizFile != null) {
                backendTest = editWizFile;
            } else {
                TreeSet backendNames = new TreeSet();               
                TreeSet backendNumbers = new TreeSet(); 
                RequestContext requestContext = RequestManager.getRequestContext();

                String filename = "backend";
                String filetype = ".properties";
                int i = 1;
                while (backendTest.exists()) {
                    i++;
                    backendTest = new File(ManagerTableModel.CONFIG_FILE_LOCATION + filename + Integer.toString(i) + filetype);
                }

                backendTest.createNewFile();
            }
            StringBuffer buffer = new StringBuffer();
            buffer.append(getBackendProperties());

            FileWriter writer = new FileWriter(backendTest);
            writer.write(buffer.toString());
            writer.close();
        }

        catch (Exception e) {
            CCDebug.trace3( e.toString());
            Object[] args = {backendTest.getAbsolutePath()};
            setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail6", args), e.toString());
        }            
    }
    
    private void createEntry(String baseDn, String objectClassList) throws LDAPException {
        String [] dn = LDAPDN.explodeDN(baseDn, false) ;
        int equalChar = dn [0].indexOf('=') ;
        String [] values = new String [1] ;
        Vector attributes = new Vector() ;

        attributes.add("objectClass") ;
        attributes.add(splitParameter(objectClassList)) ;
        attributes.add(dn [0].substring(0, equalChar)) ;
        values [0] = dn [0].substring(equalChar + 1) ;
        attributes.add(values) ;

        LDAPConnection conn = (LDAPConnection)m_wm.getValue(m_wm.CONNECTION); 
        try {
            LDAPEntry tryEntry = conn.read(baseDn);
        } catch (LDAPException e) {
            createEntry(baseDn, attributes) ;
        }

    }
    
     private void createEntry(String aDn, Vector aAttributes) throws LDAPException {
        LDAPAttribute [] attributes = new LDAPAttribute [aAttributes.size() / 2] ;
        Enumeration elements = aAttributes.elements() ;
        
        for (int i = 0 ; i < attributes.length ; ++ i) {
            attributes [i] = new LDAPAttribute(
                                        (String) elements.nextElement(),
                                        (String []) elements.nextElement()) ;
        }
        LDAPEntry entry = new LDAPEntry(aDn, new LDAPAttributeSet(attributes)) ;
        boolean entryCreated = false ;
        
        LDAPConnection conn = (LDAPConnection)m_wm.getValue(m_wm.CONNECTION); 
        conn.add(entry) ;
        entryCreated = true ;

    }

   
    private void fillAttribute(String baseDn, String attr, String name, String metaConfig) throws LDAPException {

        boolean attrExists = false;
        LDAPConnection conn = (LDAPConnection)m_wm.getValue(m_wm.CONNECTION); 
        LDAPEntry tryEntry = conn.read(baseDn);
        LDAPAttribute tryAttr = tryEntry.getAttribute(attr);
        if (tryAttr != null) {
            Enumeration valuesEnum = tryAttr.getStringValues();
            while (valuesEnum.hasMoreElements()) {
                String value = (String)valuesEnum.nextElement();
                String attrName = value.split("=")[0];
                if (attrName.equals(name)) {
                    attrExists = true;
                    metaConfig = value.substring(value.indexOf("=")+1);
                    metaConfig = "#current product version\nApocVersion=2.0\n\n" + metaConfig;
                    break;
                }
            }
        }
        Vector attributes = new Vector() ;
        String [] values = new String [1] ;
        attributes.add(attr) ;
        values [0] = name + "=" + metaConfig ;
        attributes.add(values) ;
        setAttributes(baseDn, attributes, attrExists) ;

    }     
     
    private void modifyEntry(String aDn, LDAPModification aModification) throws LDAPException {
        
        LDAPConnection conn = (LDAPConnection)m_wm.getValue(m_wm.CONNECTION); 
        conn.modify(aDn, aModification) ;
    }
    
    private void addAttribute(String baseDn, String attr, String valueList) throws LDAPException {
        
        boolean attrExists = false;
        LDAPConnection conn = (LDAPConnection)m_wm.getValue(m_wm.CONNECTION); 
        LDAPEntry tryEntry = conn.read(baseDn);
        LDAPAttribute tryAttr = tryEntry.getAttribute(attr);
        if (tryAttr != null) {
            Enumeration valuesEnum = tryAttr.getStringValues();
            while (valuesEnum.hasMoreElements()) {
                String value = (String)valuesEnum.nextElement();
                if (value.equals(valueList)) {
                    attrExists = true;
                    break;
                }
            }
        }
        
        if (!attrExists) {
            Vector attributes = new Vector() ;

            attributes.add(attr) ;
            attributes.add(splitParameter(valueList)) ;
            setAttributes(baseDn, attributes, false) ;
        }
        
    }

    private void setAttributes(String aDn, Vector aAttributes, boolean aReplace) throws LDAPException {
        LDAPConnection conn = (LDAPConnection)m_wm.getValue(m_wm.CONNECTION); 
        int nbAttributes = aAttributes.size() / 2 ;
        Enumeration elements = aAttributes.elements() ;

        for (int i = 0 ; i < nbAttributes ; ++ i) {
            String attribute = (String) elements.nextElement() ;
            String [] values = (String []) elements.nextElement() ;

            if (aReplace) {
                modifyEntry(aDn, 
                            new LDAPModification(LDAPModification.REPLACE,
                                                 new LDAPAttribute(attribute,
                                                                   values))) ;
            }
            else {
                for (int j = 0 ; j < values.length ; ++ j) {
                    modifyEntry(aDn, 
                                new LDAPModification(LDAPModification.ADD,
                                    new LDAPAttribute(attribute,
                                                      values [j]))) ;
                }
            }
        }
    }

   
    private String getOrgMapping() {
        String metaConfig = (String)m_wm.getValue(m_wm.METACONFIG);
        return metaConfig;
    }
    
    private String getUserMapping() {
        StringBuffer metaConfig = new StringBuffer();
        InputStream istream = null;
        try {
            istream=getClass().getResource("metaconfig/UserProfileMapping").openStream();
            int c;
            while ((c = istream.read()) != -1) {
                String aChar = new Character((char)c).toString();
                metaConfig.append(aChar);
            }
            istream.close() ;
        }catch(IOException ioe)	{
            CCDebug.trace3("Wizard10View: Unable to find metaconfiguration file." + ioe.getLocalizedMessage()) ;
            setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail3"), ioe.toString());
        }
        return metaConfig.toString();
    }

    private void migrateProfiles() {
        PolicyManager policyMgr = (PolicyManager) m_wm.getValue(m_wm.POLICYMGR);
        String entityName = "";
        if (policyMgr != null) {
            try {
                Iterator it = policyMgr.getProfileProvider(LdapEntityType.STR_ALL).getAllProfiles();
                while (it.hasNext()) {
                    Profile profile = (Profile)it.next();
                    String profileId = profile.getId();
                    entityName = profile.getProfileRepository().getEntity().getDisplayName(Toolbox2.getLocale());
                    if ((profileId.startsWith("ou=_defaultuserpolicygroup_"))
                            || (profileId.startsWith("ou=_defaulthostpolicygroup_"))) {
                            migrateProfile((LdapProfile)profile);
                    }
                }
            } catch (SPIException spie) {
                CCDebug.trace3("Wizard10View: Unable to migrate profiles from 1.1 to 2.0 format" + spie.getLocalizedMessage()) ;
                Object[] args = {entityName};
                setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail10", args), spie.toString());
            }
        }
    }

    public void migrateProfile(LdapProfile profile) throws SPIException {
        PolicyManager policyManager = (PolicyManager) m_wm.getValue(m_wm.POLICYMGR);
        // create new profile
        LdapProfile newProfile = (LdapProfile)profile.getProfileRepository().createProfile(
                profile.getDisplayName(), profile.getApplicability());
        // add the profile policies to new profile
        Iterator policies = profile.getPolicies();
        while(policies.hasNext()) {
            Policy policy = (Policy)policies.next();
            newProfile.storePolicy(policy);
        }
        // assign the containing entity to the profile
        String entityType = Toolbox2.getEntityTypeString(profile.getProfileRepository().getEntity());
        AssignmentProvider assignmentProvider =
            policyManager.getAssignmentProvider(entityType);
        assignmentProvider.assignProfile(profile.getProfileRepository().getEntity(), newProfile);
        
        // unassign the local profile from this entity or else the destroyProfile method will fail
        Iterator it = profile.getAssignedEntities();
        while (it.hasNext()) {
            Entity entity = (Entity)it.next();
            entity.unassignProfile(profile);
        }
            
        // low-level destruction so that we don't check 
        // if Entities are assigned which would return true all the time
        profile.getProfileRepository().destroyProfile(profile);
        // copy the attributes of new profile into profile
        // to midify the object referenced by the argument profile
        profile.copy(newProfile);
    }

    private String getBackendProperties() {
        StringBuffer buffer = new StringBuffer();
        String repositoryName = (String)m_wm.getValue(Wizard2View.CHILD_NAME_FIELD);
        String utf8repositoryName = repositoryName;
        String buf = "";
        for(int i=0;i<repositoryName.length();i++) {
            buf = buf + "\\u" + charToHex(repositoryName.charAt(i));
        }
        //NOTE: I could probably use Properties object here and avoid the explicit UTF8 conversion
        utf8repositoryName = buf;
        buffer.append("# RepositoryName=" + repositoryName + "\n");
        buffer.append(BACKEND)
            .append("=")
            .append(utf8repositoryName)
            .append("\n");
        
        String[] sourceNames = new String[2];
        sourceNames[0] = EnvironmentConstants.HOST_SOURCE;
        sourceNames[1] = EnvironmentConstants.USER_SOURCE;
        
        if (!(m_backendType.equals("1"))) {
            String[] sources = {EnvironmentConstants.DOMAIN_PREFIX, 
                                EnvironmentConstants.ORGANIZATION_PREFIX}; 
            String host = (String)m_wm.getValue(Wizard3View.CHILD_HOST_FIELD) ;
            String port = (String)m_wm.getValue(Wizard3View.CHILD_PORT_FIELD);
            String baseDn = (String)m_wm.getValue(m_wm.BASEDN);
            String datastore = "ldap";
            String useAnon = (String)m_wm.getValue(Wizard4View.CHILD_ANON_ACCESS);
            String useSSL = (String)m_wm.getValue(Wizard3View.CHILD_USE_SSL);
            if (useSSL.equals("true")) {
                datastore = "ldaps";
            }
            StringBuffer ldapUrlBuff = new StringBuffer();
            ldapUrlBuff.append(datastore)
                    .append("://")
                    .append(host)
                    .append(":")
                    .append(port)
                    .append("/")
                    .append(baseDn);
            String ldapUrl = ldapUrlBuff.toString();
            buffer.append(EnvironmentConstants.URL_KEY)
                .append("=")
                .append(ldapUrl)
                .append("\n");                                 
            for (int i=0; i<sources.length; i++) {
                buffer.append(sources[i])    
                    .append(EnvironmentConstants.URL_KEY)
                    .append("=")
                    .append(ldapUrl)
                    .append("\n");            
                if (useAnon.equals("false")) {    
                    String authDn = (String)m_wm.getValue(Wizard4View.CHILD_NONANON_USERDN_FIELD);
                    String password = (String)m_wm.getValue(Wizard4View.CHILD_NONANON_PASSWORD_FIELD);
                    buffer.append(sources[i]) 
                        .append(EnvironmentConstants.LDAP_AUTH_USER_KEY)
                        .append("=")
                        .append(authDn)
                        .append("\n"); 
                    buffer.append(sources[i]) 
                        .append(EnvironmentConstants.LDAP_AUTH_PASSWORD_KEY)
                        .append("=")
                        .append(password) 
                        .append("\n");
                    buffer.append(sources[i])
                        .append(EnvironmentConstants.LDAP_AUTH_PWD_ENCODING_KEY)
                        .append("=")
                        .append(EnvironmentConstants.SCRAMBLE_ENCODING)
                        .append("\n");
                }
                if (m_backendType.equals("2")) {
                    String filepath = (String)m_wm.getValue(m_wm.FILEPATH);
                    datastore = "file";

                    buffer.append(sources[i]) 
                        .append(EnvironmentConstants.PROFILE_PREFIX)
                        .append(EnvironmentConstants.URL_KEY)
                        .append("=")
                        .append(datastore)
                        .append("://")
                        .append(filepath)
                        .append("/profiles")
                        .append("\n");
                    buffer.append(sources[i]) 
                        .append(EnvironmentConstants.ASSIGNMENT_PREFIX)
                        .append(EnvironmentConstants.URL_KEY)
                        .append("=")
                        .append(datastore)
                        .append("://")
                        .append(filepath)
                        .append("/assignments")
                        .append("\n");  

                } else {
                    buffer.append(sources[i]) 
                        .append(EnvironmentConstants.PROFILE_PREFIX)
                        .append(EnvironmentConstants.URL_KEY)
                        .append("=")
                        .append(ldapUrl)
                        .append("\n");
                    buffer.append(sources[i]) 
                        .append(EnvironmentConstants.ASSIGNMENT_PREFIX)
                        .append(EnvironmentConstants.URL_KEY)
                        .append("=")
                        .append(ldapUrl)
                        .append("\n");  
                }
            }
            if (m_backendType.equals("2")) {
                String filepath = (String)m_wm.getValue(m_wm.FILEPATH);
                datastore = "file";
                buffer.append(EnvironmentConstants.LDAP_META_CONF_PREFIX)
                        .append(EnvironmentConstants.URL_KEY)
                        .append("=")
                        .append(datastore)
                        .append("://")
                        .append(filepath)
                        .append("\n");
            }
        } else {
            String[] sources = {EnvironmentConstants.DOMAIN_PREFIX,
                                EnvironmentConstants.ORGANIZATION_PREFIX}; 
            String filepath = (String)m_wm.getValue(m_wm.FILEPATH);
            String datastore = "file";
            buffer.append(EnvironmentConstants.URL_KEY)
                    .append("=")
                    .append(datastore)
                    .append("://")
                    .append(filepath)
                    .append("\n"); 
            for (int i=0; i<sources.length; i++) {
                buffer.append(sources[i])
                    .append(EnvironmentConstants.URL_KEY)
                    .append("=")
                    .append(datastore)
                    .append("://")
                    .append(filepath)
                    .append("\n");
                buffer.append(sources[i]) 
                    .append(EnvironmentConstants.PROFILE_PREFIX)
                    .append(EnvironmentConstants.URL_KEY)
                    .append("=")
                    .append(datastore)
                    .append("://")
                    .append(filepath)
                    .append("/profiles")
                    .append("\n");
                buffer.append(sources[i]) 
                    .append(EnvironmentConstants.ASSIGNMENT_PREFIX)
                    .append(EnvironmentConstants.URL_KEY)
                    .append("=")
                    .append(datastore)
                    .append("://")
                    .append(filepath)
                    .append("/assignments")
                    .append("\n");  
            }

        }
        buffer.append("Sources=");
        for (int i = 0; i < sourceNames.length; i++) {
            if (i == sourceNames.length -1) {
                buffer.append(sourceNames[i]);
            } else {
                 buffer.append(sourceNames[i])
                        .append(",");               
            }
        }
        buffer.append("\n");  
        
        return buffer.toString();
    }
    
    private void setFailureMessage(String error, String cause) {
        if (((String)m_wm.getValue(m_wm.SUCCESS)).equals("true")) {
            m_wm.setValue(m_wm.SUCCESS, "false");
            m_wm.setValue(m_wm.FAIL_MESSAGE, error + "<br>- " + cause);
        }
    }

    private String getLDAPError (LDAPException e) {
        String err = "";
        switch( e.getLDAPResultCode() ) {

            case LDAPException.NO_SUCH_ATTRIBUTE:
                err = m_I18n.getMessage("APOC.wiz.ldap.err.nosuchattr");
                break;
            case LDAPException.ATTRIBUTE_OR_VALUE_EXISTS:
                err = m_I18n.getMessage("APOC.wiz.ldap.err.valueexists");
                break;
            case LDAPException.INVALID_ATTRIBUTE_SYNTAX:
                err = m_I18n.getMessage("APOC.wiz.ldap.err.attrsyntax");
                break;
            case LDAPException.NO_SUCH_OBJECT:
                err = m_I18n.getMessage("APOC.wiz.ldap.err.nosuchobj");
                break;
            case LDAPException.INVALID_DN_SYNTAX:
                err = m_I18n.getMessage("APOC.wiz.ldap.err.invaliddn");
                break;
            case LDAPException.INVALID_CREDENTIALS:
                err = m_I18n.getMessage("APOC.wiz.ldap.err.invalidcred");
                break;
            case LDAPException.INSUFFICIENT_ACCESS_RIGHTS:
                err = m_I18n.getMessage("APOC.wiz.ldap.err.noaccessrights");
                break;
            case LDAPException.ENTRY_ALREADY_EXISTS:
                err = m_I18n.getMessage("APOC.wiz.ldap.err.entryexists");
                break;
            case LDAPException.SERVER_DOWN:
                err = m_I18n.getMessage("APOC.wiz.ldap.err.serverdown");
                break;
            case LDAPException.LDAP_TIMEOUT:
                err = m_I18n.getMessage("APOC.wiz.ldap.err.timeout");
                break;
            case LDAPException.CONNECT_ERROR:
                err = m_I18n.getMessage("APOC.wiz.ldap.err.connecterror");
                break;
            default:
                err = e.toString();
                break;

        }
        return err;
    }
    
    private static String [] splitParameter(String aList) {
        StringTokenizer tokens = new StringTokenizer(aList, ",") ;
        String [] retCode = new String [tokens.countTokens()] ;
        int i = 0 ;

        while (tokens.hasMoreTokens()) { retCode [i ++] = tokens.nextToken() ; }
        return retCode ;
    }
    
    private void recursiveDelete(File file) {
        try {
            File[] subfiles = file.listFiles();
            if (subfiles != null) {
                for (int i = 0; i < subfiles.length; i++) {
                    if (subfiles[i].isDirectory()) {
                        recursiveDelete(subfiles[i]);
                    }
                    subfiles[i].delete();
                }
            }
        } catch (Exception e) {
            CCDebug.trace3("Deleting directory failed!");
            setFailureMessage(m_I18n.getMessage("APOC.wiz.10.fail5"), e.toString());
        }        
    }
    
    public static String checkSchema(LDAPConnection conn) {
        LDAPSchema dirSchema = new LDAPSchema();
        String[] attributeTypeNames = ATTRIBUTE_TYPE_NAMES;
        String[] objectClassNames = OBJECT_CLASS_NAMES;
        try {
            dirSchema.fetchSchema(conn);
            for (int i = 0; i < attributeTypeNames.length; i++) {
                LDAPAttributeSchema attrType = dirSchema.getAttribute(attributeTypeNames[i]);
                if (attrType == null) {
                    return "APOC.wiz.10.alert";
                }
            }
            for (int i = 0; i < objectClassNames.length; i++) {
                LDAPObjectClassSchema objClass = dirSchema.getObjectClass(objectClassNames[i]);
                if (objClass == null) {
                    return "APOC.wiz.10.alert";
                }         
            }
        } catch ( Exception e ) {
            CCDebug.trace3( e.toString() );
        }
        return null;
    }

    private String byteToHex(byte b) {
        // Returns hex String representation of byte b
        char hexDigit[] = {
         '0', '1', '2', '3', '4', '5', '6', '7',
         '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
        return new String(array);
    }

    private String charToHex(char c) {
        // Returns hex String representation of char c
        byte hi = (byte) (c >>> 8);
        byte lo = (byte) (c & 0xff);
        return byteToHex(hi) + byteToHex(lo);
    } 
}


