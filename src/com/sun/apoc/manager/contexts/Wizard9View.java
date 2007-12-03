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

import com.sun.web.ui.view.wizard.CCWizardPage;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPEntry;

import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.html.OptionList;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.ldap.environment.LdapEnvironmentMgr;
import com.sun.web.ui.view.html.CCDropDownMenu;
import com.sun.web.ui.view.html.CCHiddenField;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import javax.servlet.ServletException;

/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class Wizard9View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil {

    // The "logical" name for this page.
    public static final String PAGE_NAME = "Wizard9View";

    // Child view names (i.e. display fields).
    public static final String CHILD_SELECT_BASEDN_LABEL =
        "SelectBaseDNLabel";
    public static final String CHILD_SELECT_BASEDN =
        "SelectBaseDN";
    public static final String CHILD_IS_FIRST_VIEW =
        "isFirstView";
    
    public static final String CHILD_ENTER_BASEDN_LABEL =
        "EnterBaseDNLabel";
    public static final String CHILD_ENTER_BASEDN_FIELD =
        "EnterBaseDN";
    
    private CCI18N m_I18n;
    
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public Wizard9View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }

    public Wizard9View(View parent, Model model, String name) {

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
        registerChild(CHILD_SELECT_BASEDN_LABEL, CCLabel.class);
        registerChild(CHILD_SELECT_BASEDN, CCDropDownMenu.class);
        registerChild(CHILD_ENTER_BASEDN_LABEL, CCLabel.class);
        registerChild(CHILD_ENTER_BASEDN_FIELD, CCTextField.class);
   }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        if (name.equals(CHILD_SELECT_BASEDN_LABEL)) {
            child = (View)new CCLabel(this,
            name, m_I18n.getMessage("APOC.wiz.8.basedn"));
        } else if (name.equals(CHILD_SELECT_BASEDN)) {
            OptionList options = new OptionList();
            AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
            ArrayList baseDNs = (ArrayList)wm.getWizardValue(wm.BASEDN_LIST);
            if (baseDNs != null) {
                for (int i = 0; i < baseDNs.size(); i++) {
                    String baseDN = (String)baseDNs.get(i);
                    options.add(i, baseDN, baseDN);
                }
            }
            child = (View)new CCDropDownMenu(this, name, null);
            ((CCDropDownMenu)child).setOptions(options);    
        } else if (name.equals(CHILD_IS_FIRST_VIEW)) {
            child = (View)new CCHiddenField(this, name, "true");            
        } else if (name.equals(CHILD_ENTER_BASEDN_LABEL)) {
            child = (View)new CCLabel(this,
                name, m_I18n.getMessage("APOC.wiz.9.basedn"));
        } else if (name.equals(CHILD_ENTER_BASEDN_FIELD)) {
            child = (View)new CCTextField(this, name, null);
        } else if (name.equals(CHILD_IS_FIRST_VIEW)) {
            child = (View)new CCHiddenField(this, name, "true");  
        } else {
        throw new IllegalArgumentException(
            "Wizard9View : Invalid child name [" + name + "]");
        }
        return child;
    }

    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/contexts/Wizard9.jsp";
    }

    public void beginDisplay(DisplayEvent event)
    throws ModelControlException {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        Properties properties = (Properties)wm.getValue(wm.PROPERTIES);
        CCHiddenField isFirstView = (CCHiddenField)getChild(CHILD_IS_FIRST_VIEW);
        if (isFirstView.getValue().equals("true") && properties != null) {
            String oldProviderUrl = properties.getProperty(EnvironmentConstants.ORGANIZATION_PREFIX
                                                        + EnvironmentConstants.URL_KEY);
            if (oldProviderUrl != null && oldProviderUrl.startsWith("ldap")) {
                String baseDn  = LdapEnvironmentMgr.getBaseEntryFromURL(oldProviderUrl);
                if (isBaseDNKnown()) {
                    CCDropDownMenu baseDnMenu = (CCDropDownMenu)getChild(CHILD_SELECT_BASEDN);
                    if (baseDnMenu.getOptions().hasValue(baseDn)) {
                        baseDnMenu.setValue(baseDn);
                    }
                } else {
                    CCTextField baseDnField = (CCTextField)getChild(CHILD_ENTER_BASEDN_FIELD);
                    baseDnField.setValue(baseDn);
                }
            }
        }
        isFirstView.setValue("false");
    }   
    
    public boolean beginDisplayTextfieldInputDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        return !isBaseDNKnown();
    }
    
    public boolean beginDisplayDropdownInputDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        return isBaseDNKnown();
    }
    
    public String getErrorMsg() {
        
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        String baseDN = null;
        if (isBaseDNKnown()) {
            baseDN = (String)wm.getWizardValue(CHILD_SELECT_BASEDN);
        } else {
            baseDN = (String)wm.getWizardValue(CHILD_ENTER_BASEDN_FIELD);
        }
        LDAPConnection conn = (LDAPConnection)wm.getValue(wm.CONNECTION);        
        String hostname = (String)wm.getWizardValue(Wizard3View.CHILD_HOST_FIELD);
        String portString = (String)wm.getWizardValue(Wizard3View.CHILD_PORT_FIELD);          
        String emsg = null;
        try {
            LDAPEntry entry = conn.read(baseDN);
        } catch (LDAPException e) {
            Object[] args = {baseDN, hostname + ":" + portString};
            emsg = m_I18n.getMessage("APOC.wiz.9.alert", args);
        }
        if (emsg == null) {
            wm.setValue(wm.BASEDN, baseDN);
        }
        
        return emsg;
    }
    
    private boolean isBaseDNKnown() {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        ArrayList baseDNs = (ArrayList)wm.getValue(wm.BASEDN_LIST);
        return baseDNs != null ? true : false;
    }
}


