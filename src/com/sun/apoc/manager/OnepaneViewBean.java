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
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.html.OptionList;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.model.CCBreadCrumbsModel;
import com.sun.web.ui.model.CCBreadCrumbsModelInterface;
import com.sun.web.ui.model.CCMastheadModel;
import com.sun.web.ui.view.breadcrumb.CCBreadCrumbs;
import com.sun.web.ui.view.html.CCDropDownMenu;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCOption;
import com.sun.web.ui.view.html.CCOptionSeparator;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.masthead.CCPrimaryMasthead;
import com.sun.web.ui.view.tabs.CCNodeEventHandlerInterface;
import java.util.Iterator;
import java.util.ResourceBundle;

public class OnepaneViewBean extends ViewBeanBase implements CCNodeEventHandlerInterface {
    
    public static final String PAGE_NAME                = "Onepane";
    public static final String DEFAULT_DISPLAY_URL      = "/jsp/Onepane.jsp";
    public static final String CHILD_FORM               = "OnepaneForm";
    public static final String CHILD_DEFAULT_HREF       = "DefaultHref";
    public static final String CHILD_SELECTION_HREF     = "SelectionHref";
    public static final String CHILD_MASTHEAD           = "Masthead";
    public static final String CHILD_CONTEXT_PATH       = "ContextPath";
    public static final String CHILD_PAGELET_VIEW       = "OnepanePageletView";
    public static final String CHILD_ANCHOR_HIDDEN      = "Anchor";
    public static final String CHILD_HELPLOCALE_HIDDEN  = "HelpLocale";
    
//    private String m_sSelectedView = Integer.toString(MainWindowTabsModel.TASKS_TAB_ID);
    private String m_sSelectedView = Integer.toString(MainWindowTabsModel.ASSIGNED_TAB_ID);
    
    public OnepaneViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
        
        // Determine the selected tab. Doing this that early will allow us to
        // selectively register children in the according *PageletView class by
        // calling the getSelectedTab() method defined below.
        // Selectively registering the pagelet children can result in a
        // significant speedup, if e.g. the init routines of the children
        // involve heavy traffic with the LDAP backend
        
        String  sViewMenu = RequestManager.getRequest().getQueryString();
        int     nPosition = -1;
        
        if (sViewMenu!=null) {
            nPosition = sViewMenu.indexOf("TabHref=");
            if (nPosition!=-1) {
                nPosition += "TabHref=".length();
                sViewMenu       = sViewMenu.substring(nPosition, nPosition+1);
                m_sSelectedView = sViewMenu;
                RequestManager.getRequest().getSession(true).setAttribute(Constants.SELECTED_MAINWINDOW_TAB, m_sSelectedView);
            }
        }
        
        if (nPosition==-1) {
            m_sSelectedView = (String) RequestManager.getRequest().getSession(true).getAttribute(Constants.SELECTED_MAINWINDOW_TAB);
        }
        
        if ( (m_sSelectedView==null) || (m_sSelectedView.length()==0)) {
//            m_sSelectedView = Integer.toString(MainWindowTabsModel.TASKS_TAB_ID);
            m_sSelectedView = Integer.toString(MainWindowTabsModel.ASSIGNED_TAB_ID);
        }
    }
    
    public String getSelectedTab() {
        return m_sSelectedView;
    }
    
    public void nodeClicked(RequestInvocationEvent event, int id) {
        forwardTo(getRequestContext());
    }
    
    protected void registerChildren() {
        registerChild(CHILD_FORM,               CCForm.class);
        registerChild(CHILD_DEFAULT_HREF,       CCHref.class);
        registerChild(CHILD_SELECTION_HREF,     CCHref.class);
        registerChild(CHILD_MASTHEAD,           CCPrimaryMasthead.class);
        registerChild(CHILD_PAGELET_VIEW,       OnepanePageletView.class);
        registerChild(CHILD_CONTEXT_PATH,       CCBreadCrumbs.class);
        registerChild(CHILD_ANCHOR_HIDDEN,      CCHiddenField.class);
        registerChild(CHILD_HELPLOCALE_HIDDEN,  CCHiddenField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_FORM)) {
            View child = new CCForm(this, name);
            return child;
        }
        else if (name.equals(CHILD_DEFAULT_HREF) || name.equals(CHILD_SELECTION_HREF)) {
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
        if (name.equals(CHILD_PAGELET_VIEW)) {
            OnepanePageletView child = new OnepanePageletView(this, name);
            return child;
        }
        else if (name.equals(CHILD_ANCHOR_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, CHILD_ANCHOR_HIDDEN, null);
            return child;
        }
        else if (name.equals(CHILD_HELPLOCALE_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, CHILD_HELPLOCALE_HIDDEN, null);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleDefaultHrefRequest(RequestInvocationEvent event) {
        forwardTo(getRequestContext());
    }
}
