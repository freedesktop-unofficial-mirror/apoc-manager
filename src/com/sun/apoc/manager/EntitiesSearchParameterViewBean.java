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
import com.iplanet.jato.view.DisplayField;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.html.OptionList;
import com.sun.apoc.manager.entities.SearchTableModel;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.model.CCPropertySheetModel;
import com.sun.web.ui.view.html.CCCheckBox;
import com.sun.web.ui.view.html.CCDropDownMenu;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.masthead.CCSecondaryMasthead;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.propertysheet.CCPropertySheet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class EntitiesSearchParameterViewBean extends ViewBeanBase {
    public static final String  PAGE_NAME                = "EntitiesSearchParameter";
    public static final String  DEFAULT_DISPLAY_URL      = "/jsp/entities/SearchParameter.jsp";
    public static final String  CHILD_FORM               = "SearchForm";
    public static final String  CHILD_MASTHEAD           = "Masthead";
    public static final String  CHILD_SEARCHPARAMS_TITLE = "SearchParamsTitle";
    public static final String  CHILD_SEARCHPARAMS_SHEET = "SearchParamsSheet";
    public static final String  CHILD_TOGGLE_HREF        = "ToggleOptions";
    public static final String  CHILD_ROWLAYOUT_TEXT     = "RowLayout";
    public static final String  CHILD_CONTEXT_TEXT       = "JSContext";
    public static final String  CHILD_CONTEXT_HIDDEN     = "ContextHidden";
    public static final String  CHILD_ENTITY_HIDDEN      = Constants.CURRENT_ENTITY_ID;
    public static final String  CHILD_ENTITY_TYPE_HIDDEN = "CurrentEntityType";
    public static final String  CHILD_RESTRICT_HIDDEN    = "RestrictHidden";
    public static final String  CHILD_RESULTS_HIDDEN     = "ResultsHidden";
    
    //
    private CCPropertySheetModel m_sheetModel             = null;
    private CCPageTitleModel     m_titleModel             = null;
    String m_restrictedEntity                             = "";
    String m_restrictedEntityType                         = "";
    
    public EntitiesSearchParameterViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        
        ServletContext  context     = RequestManager.getRequestContext().getServletContext();
        String          sQuery      = RequestManager.getRequest().getQueryString();
        boolean         bToggle     = (sQuery==null)?false:sQuery.indexOf("ToggleOptions")>0;
        boolean         bWasAdvanced= Toolbox2.getParameter("ResultsMenu").length()>0;
        String          sXmlPath;
        
        if ((bToggle && !bWasAdvanced) || (!bToggle && bWasAdvanced)) {
            sXmlPath = "/jsp/entities/SearchParameterAdvanced.xml";
        } else {
            sXmlPath = "/jsp/entities/SearchParameter.xml";
        }
        
        m_restrictedEntity = Toolbox2.getParameter("RestrictedEntity");
        if (m_restrictedEntity != null && m_restrictedEntity.length() != 0) {
            sXmlPath = "/jsp/entities/SearchParameterAdvanced.xml";
        }
        m_restrictedEntityType = Toolbox2.getParameter("RestrictedEntityType");

        m_sheetModel = new CCPropertySheetModel(context, sXmlPath);
        m_titleModel = new CCPageTitleModel(context ,"/jsp/entities/SearchTitle.xml");
        
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_FORM, CCForm.class);
        registerChild(CHILD_MASTHEAD, CCSecondaryMasthead.class);
        registerChild(CHILD_SEARCHPARAMS_TITLE, CCPageTitle.class);
        registerChild(CHILD_SEARCHPARAMS_SHEET, CCPropertySheet.class);
        registerChild(CHILD_ROWLAYOUT_TEXT, CCStaticTextField.class);
        registerChild(CHILD_ENTITY_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_ENTITY_TYPE_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_RESTRICT_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_RESULTS_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_CONTEXT_HIDDEN, CCHiddenField.class);
        registerChild(SearchTableModel.CHILD_NAME_HREF, CCHref.class);
        registerChild(CHILD_TOGGLE_HREF, CCHref.class);
        registerChild(CHILD_CONTEXT_TEXT, CCStaticTextField.class);
        m_titleModel.registerChildren(this);
        m_sheetModel.registerChildren(this);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_FORM)) {
            View child = new CCForm(this, name);
            return child;
        }
        else if (name.equals(CHILD_MASTHEAD)) {
            CCSecondaryMasthead child = new CCSecondaryMasthead(this, name);
            return child;
        }
        if (name.equals(CHILD_SEARCHPARAMS_TITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_titleModel, name);
            Toolbox2.setPageTitleHelp(m_titleModel, "APOC.search.search", 
                                "APOC.search.param.title.help", "gbgpa.html");
            return child;
        }
        else if (name.equals(CHILD_SEARCHPARAMS_SHEET)) {
            CCPropertySheet child = new CCPropertySheet(this, m_sheetModel, name);
            return child;
        }
        else if (name.equals(CHILD_ROWLAYOUT_TEXT)||name.equals(CHILD_CONTEXT_TEXT)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        } 
        else if (name.equals(CHILD_ENTITY_HIDDEN) ||
        name.equals(CHILD_ENTITY_TYPE_HIDDEN) ||
        name.equals(CHILD_RESTRICT_HIDDEN) ||
        name.equals(CHILD_RESULTS_HIDDEN) ||
        name.equals(CHILD_CONTEXT_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;
        }
        else if (name.equals(SearchTableModel.CHILD_NAME_HREF) || name.equals(CHILD_TOGGLE_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }
        else if ((m_titleModel != null) && m_titleModel.isChildSupported(name)) {
            View child = m_titleModel.createChild(this, name);
            return child;
        }
        else if ((m_sheetModel != null) && m_sheetModel.isChildSupported(name)) {
            View child = m_sheetModel.createChild(this, name);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleNameHrefRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        forwardTo(event.getRequestContext());
    }
    
    public void handleToggleOptionsRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        forwardTo(event.getRequestContext());
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        
        // simple / advanced
        boolean         bToggle         = RequestManager.getRequest().getQueryString().indexOf("ToggleOptions")>0;
        boolean         bWasAdvanced    = Toolbox2.getParameter("ResultsMenu").length()>0;
        CCHiddenField   restrictHidden  = (CCHiddenField)  getChild(CHILD_RESTRICT_HIDDEN);
        CCHiddenField   resultsHidden   = (CCHiddenField)  getChild(CHILD_RESULTS_HIDDEN);
        
        if (bWasAdvanced) {
            String sRestrictCheckbox= Toolbox2.getParameter("RestrictCheckbox");
            String sResultsMenu     = Toolbox2.getParameter("ResultsMenu");
            restrictHidden.setValue((sRestrictCheckbox.length()==0)?"false":sRestrictCheckbox);
            resultsHidden.setValue(sResultsMenu);
        }
        
        if (!bWasAdvanced && bToggle) {
            CCCheckBox      restrictCheckbox= (CCCheckBox)     getChild("RestrictCheckbox");
            CCDropDownMenu  resultsMenu     = (CCDropDownMenu) getChild("ResultsMenu");
            restrictCheckbox.setValue(restrictHidden.getValue());
            resultsMenu.setValue(resultsHidden.getValue());
        }
        
        // context
        CCHiddenField   contextHidden   = (CCHiddenField) getChild(CHILD_CONTEXT_HIDDEN);
        String          sContext        = (String) contextHidden.getValue();
        
        if (sContext==null || sContext.length()==0) {
            sContext = Toolbox2.getQuery("ContextId");
            contextHidden.setValue(sContext);
        }
        
        DisplayField contextText = (DisplayField) getChild(CHILD_CONTEXT_TEXT);
        contextText.setValue(Toolbox2.encode(sContext));
        
        // starting entity
        CCHiddenField   entity  = (CCHiddenField) getChild(CHILD_ENTITY_HIDDEN);
        CCHiddenField   entityType  = (CCHiddenField) getChild(CHILD_ENTITY_TYPE_HIDDEN);
        String          sEntity = (String) entity.getValue();
        String          sEntityType = (String) entityType.getValue();
        
        if ((sEntity==null) || (sEntity.length()==0)) {
            try {
                Entity selectedEntity = Toolbox2.getSelectedEntity();
                if (selectedEntity != null) {
                    PolicyManager policymgr = Toolbox2.createPolicyManager(sContext, null, null, false);
                    sEntity = policymgr.getRootEntity(selectedEntity.getPolicySourceName()).getId();
                    sEntityType = selectedEntity.getPolicySourceName();
                }
      /*          PolicyManager policymgr = Toolbox2.createPolicyManager(sContext, null, null, false);
                String[] sources = policymgr.getSources();
                if (sources.length > 0) {
                    sEntity = policymgr.getRootEntity(sources[0]).getId();
                    sEntityType = sources[0];
                    
                }*/
            } catch (Exception ex) {
                throw new ModelControlException(ex);
            }
        }
        
        entity.setValue(sEntity);
        entityType.setValue(sEntityType);
        if (m_restrictedEntity != null && m_restrictedEntity.length() != 0) {
            entity.setValue(m_restrictedEntity); 
            entityType.setValue(m_restrictedEntityType);
            restrictHidden.setValue("false");
            CCCheckBox restrictCheckbox= (CCCheckBox) getChild("RestrictCheckbox");
            restrictCheckbox.setChecked(true);
        }
        
    }
    
    public boolean beginEntityTypeMenuDisplay(ChildDisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        
        try {
            CCDropDownMenu dropDown = (CCDropDownMenu) getChild(event.getChildName());
            PolicyManager policymgr = Toolbox2.getPolicyManager();
            ArrayList sources = new ArrayList(Arrays.asList(policymgr.getSources()));
            OptionList optionList = new OptionList();
            optionList.add("APOC.search.all", "ALL");
            if (sources.contains(EnvironmentConstants.USER_SOURCE)) {
                optionList.add("APOC.search.organizations", "ORG");
                optionList.add("APOC.search.users", "USERID");
                optionList.add("APOC.search.roles", "ROLE");
            }
            if (sources.contains(EnvironmentConstants.HOST_SOURCE)) {
                optionList.add("APOC.search.domains", "DOMAIN");
                optionList.add("APOC.search.hosts", "HOST");
            }
            dropDown.setOptions(optionList);

        } catch (Exception ex) {
            throw new ModelControlException(ex);
        }
        
        return true;
    }
    
    public boolean beginStartPointTextDisplay(ChildDisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        
        try {
            String              sEntityId   = (String) ((CCHiddenField) getChild(CHILD_ENTITY_HIDDEN)).getValue();
            String              sEntityType = (String) ((CCHiddenField) getChild(CHILD_ENTITY_TYPE_HIDDEN)).getValue();
            CCStaticTextField   child       = (CCStaticTextField) getChild(event.getChildName());
            String              sStartPoint = "";
            
            if (sEntityId.length()==0) {
                sStartPoint = (String) getSession().getAttribute(Constants.POLICY_MANAGER_NAME);
            } else {
                CCHiddenField   contextHidden   = (CCHiddenField) getChild(CHILD_CONTEXT_HIDDEN);
                String          sContext        = (String) contextHidden.getValue();
                PolicyManager       manager         = Toolbox2.createPolicyManager(sContext, null, null, false);
                sStartPoint = Toolbox2.getParentagePath(manager.getEntity(sEntityType, sEntityId), true, true, "/", manager);
            }
            
            child.setValue(sStartPoint);
        } catch (Exception ex) {
            throw new ModelControlException(ex);
        }
        
        return true;
    }
    
    public boolean beginRowLayoutDisplay(ChildDisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        
        CCStaticTextField   child       = (CCStaticTextField) getChild(event.getChildName());
        boolean             bAdvanced   = false;
        String              sRowLayout  = "220, *, 45";
        
        try {
            bAdvanced = (getChild("ResultsMenu")!=null);
        } catch (Exception e) {}
        
        if (bAdvanced) {
            sRowLayout="300, *, 45";
        }
        
        child.setValue(sRowLayout);
        
        return true;
    }
}
