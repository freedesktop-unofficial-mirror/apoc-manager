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
import com.iplanet.jato.model.DefaultModel;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.JspDisplayEvent;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.DisplayField;
import com.iplanet.jato.view.RequestHandlingViewBase;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.html.CCCheckBox;
import com.sun.web.ui.view.html.CCDropDownMenu;
import com.iplanet.jato.view.html.OptionList;

import com.sun.web.ui.view.wizard.CCWizardPage;

import netscape.ldap.LDAPConnection;

import com.sun.web.ui.common.CCDebug;

import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Constants;

/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class Wizard5View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil {

    // The "logical" name for this page.
    public static final String PAGE_NAME = "Wizard5View";

    // Child view names (i.e. display fields).

    public static final String CHILD_LDAP_SERVER_TYPE_LABEL =
        "ServerTypeLabel";
    public static final String CHILD_SERVER_TYPE =
        "ServerType";
    private CCI18N m_I18n;
    
    // Should be in CCWizardPage ?
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public Wizard5View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }

    public Wizard5View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        registerChildren();
    }


    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_LDAP_SERVER_TYPE_LABEL, CCLabel.class);
        registerChild(CHILD_SERVER_TYPE, CCDropDownMenu.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        if (name.equals(CHILD_LDAP_SERVER_TYPE_LABEL)) {
        child = (View)new CCLabel(this,
            name, m_I18n.getMessage("APOC.wiz.5.type"));
        } else if (name.equals(CHILD_SERVER_TYPE)) {
            OptionList options = new OptionList();
            options.add(0, m_I18n.getMessage("APOC.wiz.5.sun"), "0");
            options.add(1, m_I18n.getMessage("APOC.wiz.5.openldap"), "1");
            options.add(2, m_I18n.getMessage("APOC.wiz.5.ad"), "2");
            options.add(3, m_I18n.getMessage("APOC.wiz.5.other"), "3");
            child = (View)new CCDropDownMenu(this, name, null);
            ((CCDropDownMenu)child).setOptions(options);
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
        return "/jsp/contexts/Wizard5.jsp";
    }

    public String getErrorMsg() {

        String emsg = null;
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        LDAPConnection conn = (LDAPConnection)wm.getValue(wm.CONNECTION);  
        String vendorId = (String)wm.getWizardValue(CHILD_SERVER_TYPE);        
        String backendType = (String)wm.getWizardValue(Wizard2View.CHILD_BACKEND_TYPE);
        wm.setValue(wm.VENDOR_ID, vendorId);
        if (backendType.equals("0")) {   
            if (vendorId != null) {
                if (vendorId.equals("1")) {
                    Object[] args = {m_I18n.getMessage("APOC.wiz.5.openldap")};
                    emsg = m_I18n.getMessage(Wizard10View.checkSchema(conn), args);
                } else if (vendorId.equals("2")) {
                    Object[] args = {m_I18n.getMessage("APOC.wiz.5.ad")};
                    emsg = m_I18n.getMessage(Wizard10View.checkSchema(conn), args);
                } else if (vendorId.equals("3")) {
                    Object[] args = {m_I18n.getMessage("APOC.wiz.5.other")};
                    emsg = m_I18n.getMessage(Wizard10View.checkSchema(conn), args);
                }
            }
        }
        return emsg;
    }
}


