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

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.model.CCBreadCrumbsModel;
import com.sun.web.ui.model.CCBreadCrumbsModelInterface;
import com.sun.web.ui.model.CCMastheadModel;
import com.sun.web.ui.model.CCTabsModelInterface;
import com.sun.web.ui.view.breadcrumb.CCBreadCrumbs;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.masthead.CCPrimaryMasthead;
import com.sun.web.ui.view.tabs.CCTabs;
import java.util.ResourceBundle;
public class MastheadViewBean extends ViewBeanBase {
    
    public static final String PAGE_NAME                = "Masthead";
    public static final String DEFAULT_DISPLAY_URL      = "/jsp/Masthead.jsp";
    public static final String CHILD_DEFAULT_HREF       = "DefaultHref";
    public static final String CHILD_SELECTION_HREF     = "SelectionHref";
    public static final String CHILD_MASTHEAD           = "Masthead";
    public static final String CHILD_CONTEXT_PATH       = "ContextPath";
    public static final String CHILD_TABS               = "ThreepaneTabs";
    
    //
    private MainWindowTabsModel m_tabsModel             = null;
    
    public MastheadViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_DEFAULT_HREF,       CCHref.class);
        registerChild(CHILD_SELECTION_HREF,     CCHref.class);
        registerChild(CHILD_MASTHEAD,           CCPrimaryMasthead.class);
        registerChild(CHILD_CONTEXT_PATH,       CCBreadCrumbs.class);
        registerChild(CHILD_TABS,               CCTabs.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_DEFAULT_HREF) || name.equals(CHILD_SELECTION_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_MASTHEAD)) {
            CCMastheadModel model = new CCMastheadModel();
            CCPrimaryMasthead child = new CCPrimaryMasthead(this, model, name);
            return child;
        }
        else if (name.equals(CHILD_CONTEXT_PATH)) {
            CCBreadCrumbsModel model = (CCBreadCrumbsModel) getModel(CCBreadCrumbsModel.class);
            model.appendRow();
            model.setValue(CCBreadCrumbsModel.LABEL, Toolbox2.getI18n("APOC.masthead.path.link"));
            model.setValue(CCBreadCrumbsModel.MOUSEOVER, Toolbox2.getI18n("APOC.masthead.path.help"));
            model.setValue(CCBreadCrumbsModel.COMMANDFIELD, CHILD_SELECTION_HREF);
            model.setValue(CCBreadCrumbsModel.HREF_VALUE, "a");
            model.setValue(CCBreadCrumbsModelInterface.ONCLICK, "javascript:top.location.href='/apoc/manager/Welcome?ClosePopups=true'; return false;");
            model.setCurrentPageLabel((String) getSession().getAttribute(Constants.POLICY_MANAGER_NAME));
 
            CCBreadCrumbs child = new CCBreadCrumbs(this, model, name);
            return child;
        }
        else if (name.equals(CHILD_TABS)) {
            CCTabs child = new CCTabs(this, getTabsModel(), name);
            return child;
        }
        throw new IllegalArgumentException("Invalid child name [" + name + "]");
    }
    
    public void handleDefaultHrefRequest(RequestInvocationEvent event) {
        forwardTo(getRequestContext());
    }
    
    public boolean beginMastheadDisplay(ChildDisplayEvent event)
    throws ModelControlException {
        CCPrimaryMasthead child = (CCPrimaryMasthead) getChild(CHILD_MASTHEAD);
        CCMastheadModel model = (CCMastheadModel) child.getCCMastheadModel();
        
        CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
        ResourceBundle bundle = i18n.getResourceBundle();
        String productNameAlt = i18n.getMessage("APOC.masthead.altTextVer");
        model.setAlt(productNameAlt);
        
        return true;
    }
    
    protected CCTabsModelInterface getTabsModel() {
        if (m_tabsModel == null) {
            m_tabsModel = (MainWindowTabsModel) getModel(MainWindowTabsModel.class);
            m_tabsModel.setSelectedNode(MainWindowTabsModel.ASSIGNED_TAB_ID);
        }
        return m_tabsModel;
    }
}
