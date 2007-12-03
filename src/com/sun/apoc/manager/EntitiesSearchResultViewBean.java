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
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.entities.SearchTableView;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.table.CCActionTable;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;

public class EntitiesSearchResultViewBean extends ViewBeanBase {
    public static final String PAGE_NAME                = "EntitiesSearchResult";
    public static final String DEFAULT_DISPLAY_URL      = "/jsp/entities/SearchResult.jsp";
    public static final String CHILD_FORM               = "ResultForm";
    public static final String CHILD_TABLE_VIEW         = "EntitiesSearchTableView";
    public static final String CHILD_SEARCHRESULT_TITLE = "SearchResultTitle";
    public static final String CHILD_HELP1_TEXT         = "HelpText1";
    public static final String CHILD_HELP2_TEXT         = "HelpText2";
    public static final String CHILD_HELP3_TEXT         = "HelpText3";
    public static final String CHILD_ENTITYID_HIDDEN    = "EntityId";
    public static final String CHILD_ENTITY_TYPE_HIDDEN = "EntityType";
    public static final String CHILD_TYPE_HIDDEN        = "EntityTypeMenu";
    public static final String CHILD_SEARCHTEXT_HIDDEN  = "SearchText";
    public static final String CHILD_RESULTS_HIDDEN     = "ResultsMenu";
    public static final String CHILD_RESTRICT_HIDDEN    = "RestrictCheckbox";
    public static final String CHILD_CONTEXT_HIDDEN     = "ContextId";
    public static final String CHILD_IS_NEW_SEARCH      = "IsNewSearch";
    public static final String CHILD_SEARCH_BUTTON      = "SearchButton";
    public static final String CHILD_DEFAULT_HREF       = "DefaultHref";
    public static final String DEFAULT_VALUE            = "defval";
    public static final String CHILD_JS_ALERT           = "SearchTargetAlert";
    private CCPageTitleModel   m_titleModel             = null;
    private boolean            m_bIsDefaultView         = false;
    private CCI18N m_I18n = null;
    
    public EntitiesSearchResultViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        m_titleModel = new CCPageTitleModel();
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_SEARCHRESULT_TITLE, CCPageTitle.class);
        registerChild(CHILD_TABLE_VIEW, SearchTableView.class);
        registerChild(CHILD_FORM, CCForm.class);
        registerChild(CHILD_HELP1_TEXT, CCStaticTextField.class);
        registerChild(CHILD_HELP2_TEXT, CCStaticTextField.class);
        registerChild(CHILD_HELP3_TEXT, CCStaticTextField.class);
        registerChild(CHILD_ENTITYID_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_ENTITY_TYPE_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_TYPE_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_SEARCHTEXT_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_RESULTS_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_RESTRICT_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_CONTEXT_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_IS_NEW_SEARCH, CCHiddenField.class);
        registerChild(CHILD_SEARCH_BUTTON, CCButton.class);
        registerChild(CHILD_DEFAULT_HREF, CCHref.class);
        registerChild(CHILD_JS_ALERT, CCStaticTextField.class);
        m_titleModel.registerChildren(this);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_SEARCHRESULT_TITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_titleModel, name);
            return child;
        }
        else if (name.equals(CHILD_TABLE_VIEW)) {
            SearchTableView child = new SearchTableView(this, name);
            return child;
        }
        else if (name.equals(CHILD_ENTITYID_HIDDEN) || 
        name.equals(CHILD_ENTITY_TYPE_HIDDEN) ||
        name.equals(CHILD_TYPE_HIDDEN) ||
        name.equals(CHILD_SEARCHTEXT_HIDDEN) ||
        name.equals(CHILD_RESULTS_HIDDEN) || 
        name.equals(CHILD_RESTRICT_HIDDEN) || 
        name.equals(CHILD_CONTEXT_HIDDEN)|| 
        name.equals(CHILD_IS_NEW_SEARCH)) {
            CCHiddenField child = new CCHiddenField(this, name, DEFAULT_VALUE);
            return child;
        }
        else if (name.equals(CHILD_FORM)) {
            View child = new CCForm(this, name);
            return child;
        }
        else if (name.equals(CHILD_SEARCH_BUTTON)) {
            View child = new CCButton(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_DEFAULT_HREF)) {
            View child = new CCHref(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_JS_ALERT)) {
            String alert1 = m_I18n.getMessage("APOC.navigation.search.target.alert1");
            CCStaticTextField child = new CCStaticTextField(this, name, alert1);
            child.setEscape(false);
            return child;  
        }
        else if (name.equals(CHILD_HELP1_TEXT) || name.equals(CHILD_HELP2_TEXT) ||
            name.equals(CHILD_HELP3_TEXT)) {
            View child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if ((m_titleModel != null) && m_titleModel.isChildSupported(name)) {
            View child = m_titleModel.createChild(this, name);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleDefaultHrefRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        m_bIsDefaultView = true;
        forwardTo(getRequestContext());
    }
    
    public void handleSearchButtonRequest(RequestInvocationEvent event)
    throws ModelControlException, ServletException, IOException {
        forwardTo(getRequestContext());
    }
    
    public boolean beginSearchResultTitleDisplay(ChildDisplayEvent event)
    throws ModelControlException {
        if (m_bIsDefaultView) {
            m_titleModel.setPageTitleText("APOC.search.help.title");
            m_titleModel.setPageTitleHelpMessage("");
        }
        
        return true;
    }
    
    public boolean beginHelpDisplay(ChildDisplayEvent event) throws ModelControlException {
        return m_bIsDefaultView;
    }
    
    public boolean beginEntitiesSearchTableViewDisplay(ChildDisplayEvent event)
    throws ModelControlException {
        CCHiddenField isNewSearch = (CCHiddenField)getChild(CHILD_IS_NEW_SEARCH);
        String isNewSearchValue = (String)isNewSearch.getValue();
        if (isNewSearchValue != null && isNewSearchValue.equals("true")) {
            SearchTableView view = (SearchTableView)getChild(CHILD_TABLE_VIEW);
            CCActionTable table = (CCActionTable)(view.getChild(view.CHILD_ENTITY_TABLE));
            table.resetStateData();
            isNewSearch.setValue("false");
        }
        return !m_bIsDefaultView;
    }
}
