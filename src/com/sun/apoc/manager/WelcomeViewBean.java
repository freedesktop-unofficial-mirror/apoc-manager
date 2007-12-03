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

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.sun.apoc.manager.contexts.ManagerTableModel;
import com.sun.apoc.manager.contexts.ManagerTableView;
import com.sun.apoc.spi.SPIException;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.model.CCMastheadModel;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.masthead.CCPrimaryMasthead;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.table.CCActionTable;
import java.io.IOException;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
public class WelcomeViewBean extends ViewBeanBase {
    
    public static final String PAGE_NAME                = "Welcome";
    public static final String DEFAULT_DISPLAY_URL      = "/jsp/Welcome.jsp";
    public static final String CHILD_FORM               = "WelcomeForm";
    public static final String CHILD_DEFAULT_HREF       = "DefaultHref";
    public static final String CHILD_MASTHEAD           = "Masthead";
    public static final String CHILD_ALERT              = "Alert";
    public static final String CHILD_TITLE              = "ManagerTitle";
    public static final String CHILD_TABLE_VIEW         = "ManagerTableView";
    public static final String CHILD_STACKTRACE         = "StackTrace";
    public static final String CHILD_REMOVE_TEXT        = "RemoveMessage";     
    public static final String CHILD_RENAME_TEXT        = "RenameMessage"; 
    public static final String CHILD_ANCHOR_HIDDEN      = "Anchor";
    public static final String CHILD_HELPLOCALE_HIDDEN  = "HelpLocale";
    public static final String CHILD_CONTEXT_HIDDEN     = "SelectedContext";
    
    //
    private CCPageTitleModel m_titleModel = null;
//    private int m_nSelectedTab = MainWindowTabsModel.TASKS_TAB_ID;
    private int m_nSelectedTab = MainWindowTabsModel.ASSIGNED_TAB_ID;
    
    public WelcomeViewBean(RequestContext rc) {
        super(PAGE_NAME);
        setRequestContext(rc);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        m_titleModel = new CCPageTitleModel();
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_FORM,               CCForm.class);
        registerChild(CHILD_DEFAULT_HREF,       CCHref.class);
        registerChild(CHILD_MASTHEAD,           CCPrimaryMasthead.class);
        registerChild(CHILD_ALERT,              CCAlertInline.class);
        registerChild(CHILD_TITLE,              CCPageTitle.class);
        registerChild(CHILD_TABLE_VIEW,         ManagerTableView.class);
        registerChild(CHILD_STACKTRACE,         CCStaticTextField.class);
        registerChild(CHILD_REMOVE_TEXT,        CCStaticTextField.class);
        registerChild(CHILD_RENAME_TEXT,        CCStaticTextField.class);
        registerChild(CHILD_ANCHOR_HIDDEN,      CCHiddenField.class);
        registerChild(CHILD_HELPLOCALE_HIDDEN,  CCHiddenField.class);
        registerChild(CHILD_CONTEXT_HIDDEN,     CCHiddenField.class);
    }
    
    public int getSelectedTab() {
        return m_nSelectedTab;
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_FORM)) {
            View child = new CCForm(this, name);
            return child;
        }
        else if (name.equals(CHILD_DEFAULT_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_MASTHEAD)) {
            CCMastheadModel model = new CCMastheadModel();
            CCPrimaryMasthead child = new CCPrimaryMasthead(this, model, name);
            return child;
        }
        else if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_TITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_titleModel, name);
            Toolbox2.setPageTitleHelp(m_titleModel, "APOC.contexts.title", 
                                "APOC.contexts.help", "gbgqa.html");
            return child;
        }
        else if (name.equals(CHILD_TABLE_VIEW)) {
            ManagerTableView child = new ManagerTableView(this, name);
            return child;
        }
        else if (name.equals(CHILD_STACKTRACE) || name.equals(CHILD_REMOVE_TEXT) || name.equals(CHILD_RENAME_TEXT)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_ANCHOR_HIDDEN) || name.equals(CHILD_HELPLOCALE_HIDDEN) || name.equals(CHILD_CONTEXT_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        try {
            Toolbox2.unsetPolicyManager();
        } catch (SPIException se) {
            Toolbox2.prepareErrorDisplay(se, getChild(AlertViewBean.CHILD_ALERT), getChild(AlertViewBean.CHILD_STACKTRACE));
        }
    }
    
    public boolean beginMastheadDisplay(ChildDisplayEvent event) throws ModelControlException {
        CCPrimaryMasthead child = (CCPrimaryMasthead) getChild(CHILD_MASTHEAD);
        CCMastheadModel model = (CCMastheadModel) child.getCCMastheadModel();
        
        CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
        ResourceBundle bundle = i18n.getResourceBundle();
        String productNameAlt = i18n.getMessage("APOC.masthead.altTextVer");
        model.setAlt(productNameAlt);
        
        return true;
    }
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        return ((alert.getSummary() != null) && (alert.getSummary().length() > 0));
    }

}
