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
import java.util.ArrayList;
import java.util.Locale;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.report.CategoryTiledModel;
import com.sun.apoc.manager.report.CategoryTiledView;
import com.sun.apoc.manager.report.ElementsInvolvedTableModel;
import com.sun.web.ui.view.masthead.CCSecondaryMasthead;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.common.CCI18N;

import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.cfgtree.property.Property;
import com.sun.apoc.spi.entities.Role;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.templates.parsing.TemplatePage;
import com.sun.apoc.templates.parsing.TemplateSet;
import com.sun.apoc.templates.parsing.TemplateSection;
import com.sun.apoc.templates.parsing.TemplateCategory;
import com.sun.apoc.templates.parsing.TemplateProperty;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.apoc.manager.resource.ResourceRepository;

import javax.servlet.ServletException;
import java.io.IOException;
import com.sun.apoc.manager.report.PolicyMgrReportHelper;
import com.sun.apoc.manager.report.ResultsData;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.entities.Leaf;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCSelectableList;
import com.sun.web.ui.view.table.CCActionTable;
import java.util.Arrays;public class ShowResultsViewBean extends ViewBeanBase {
    
    // The "logical" name for this page.
    public static final String PAGE_NAME = "ShowResults";
    // The URL that points to the JSP which uses this ViewBean
    public static final String DEFAULT_DISPLAY_URL =            "/jsp/report/ShowResults.jsp";
    public static final String CHILD_MASTHEAD =                 "SecondaryMH";
    public static final String CHILD_PAGETITLE =                "PageTitle";
    public static final String CHILD_ALERT =                    "Alert";
    public static final String CHILD_STACKTRACE =               "StackTrace";
    public static final String CHILD_ELEMENTS_INVOLVED_LIST =   "ElementsInvolvedList";
    public static final String CHILD_ELEMENTS_INVOLVED_LABEL =  "ElementsInvolvedListLabel";   
    public static final String CHILD_ELEMENTS_INVOLVED_TABLE =  "ElementsInvolvedTable"; 
    public static final String CHILD_ADDBUTTON =                "AddButton";
    public static final String CHILD_REMOVEBUTTON =             "RemoveButton";
    public static final String CHILD_UPBUTTON =                 "UpButton";
    public static final String CHILD_DOWNBUTTON =               "DownButton";
    public static final String CHILD_HIDDEN_ORGENTITY =         "OrgEntity";
    public static final String CHILD_HIDDEN_DOMENTITY =         "DomEntity";
    public static final String CHILD_HIDDEN_MERGE_ORDER =       "MergeOrder";
    public static final String CHILD_REPORT_INFO =              "ReportInfo";
    public static final String CHILD_WINDOW_TITLE =             "WindowTitle";
    public static final String CHILD_CREATION_INFO =            "CreationInfo";
    public static final String CHILD_PROFILES_INVOLVED_TITLE =  "ProfilesInvolvedTitle";
    public static final String CHILD_ENTITY_INVOLVED_TITLE =    "EntityInvolvedTitle";
    public static final String CHILD_PROFILES_INVOLVED =        "ProfilesInvolved";    
    public static final String CHILD_SETTINGS_TITLE =           "SettingsSummaryTitle"; 
    public static final String CHILD_NAME_HEADING =             "NameHeading";     
    public static final String CHILD_VALUE_HEADING =            "ValueHeading"; 
    public static final String CHILD_STATUS_HEADING =           "StatusHeading"; 
    public static final String CHILD_CATEGORY_TILEDVIEW =       "CategoryTiledView";
    public static final String CHILD_NOSECTIONS =               "NoSections";

    public static final String SET_PLACEHOLDER = "\\$queriedId";
            
    private CCPageTitleModel        m_pagetitleModel    = null;
    
    private Entity                  m_Entity            = null;
    private Entity                  m_OrgEntity         = null;
    private Entity                  m_DomEntity         = null;
    private ArrayList               m_InvolvedSources   = null;
    private ArrayList               m_InvolvedElements  = null;
    private ArrayList               m_AllSources        = null;
    private String                  m_EntityType        = null;

    private String                  m_OrgEntityName     = null;
    private String                  m_DomEntityName     = null;

    private PolicyManager           m_PolicyManager     = null;
    private PolicyMgrReportHelper   m_PolicyMgrHelper   = null;

    private CCI18N                  m_I18n              = null;

    private boolean                 m_isFirstLoad       = false; 
    private boolean                 m_hideReport        = false; 
    
    private HashMap                 m_sectionPathsMap   = null;
    private HashMap                 m_definedPropsMap   = null;
    private ArrayList               m_resultsData       = null;
    private ArrayList               m_allResultsData    = null;
    
    private ElementsInvolvedTableModel m_elementsInvolvedModel = null;
    
    public ShowResultsViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        initMemberVariables();
        registerChildren();
    }

    public ElementsInvolvedTableModel getElementsInvolvedModel() {
        if (m_elementsInvolvedModel == null) {
            m_elementsInvolvedModel = new ElementsInvolvedTableModel();
            m_elementsInvolvedModel.setDocument(RequestManager.getRequestContext().getServletContext(), "/jsp/report/ElementsInvolvedTable.xml");
        }
        return m_elementsInvolvedModel;
    }
     
    protected void registerChildren() {
        registerChild(CHILD_MASTHEAD, CCSecondaryMasthead.class);
        registerChild(CHILD_PAGETITLE, CCPageTitle.class);
        registerChild(CHILD_ALERT, CCAlertInline.class);
        registerChild(CHILD_STACKTRACE, CCStaticTextField.class);
        registerChild(CHILD_ELEMENTS_INVOLVED_LIST, CCSelectableList.class);
        registerChild(CHILD_ELEMENTS_INVOLVED_LABEL, CCLabel.class);
        registerChild(CHILD_ELEMENTS_INVOLVED_TABLE, CCActionTable.class);
        registerChild(CHILD_ADDBUTTON, CCButton.class);
        registerChild(CHILD_REMOVEBUTTON, CCButton.class);
        registerChild(CHILD_UPBUTTON, CCButton.class);
        registerChild(CHILD_DOWNBUTTON, CCButton.class);
        registerChild(CHILD_HIDDEN_ORGENTITY, CCHiddenField.class);
        registerChild(CHILD_HIDDEN_DOMENTITY, CCHiddenField.class);
        registerChild(CHILD_HIDDEN_MERGE_ORDER, CCHiddenField.class);
        registerChild(CHILD_WINDOW_TITLE, CCStaticTextField.class);
        if (!m_hideReport) {
            registerChild(CHILD_REPORT_INFO, CCStaticTextField.class);
            registerChild(CHILD_CREATION_INFO, CCStaticTextField.class);
            registerChild(CHILD_PROFILES_INVOLVED_TITLE, CCStaticTextField.class);
            registerChild(CHILD_ENTITY_INVOLVED_TITLE, CCStaticTextField.class);
            registerChild(CHILD_PROFILES_INVOLVED, CCStaticTextField.class);
            registerChild(CHILD_NOSECTIONS, CCStaticTextField.class);
            registerChild(CHILD_SETTINGS_TITLE, CCStaticTextField.class);
            registerChild(CHILD_NAME_HEADING, CCStaticTextField.class);
            registerChild(CHILD_VALUE_HEADING, CCStaticTextField.class);
            registerChild(CHILD_STATUS_HEADING, CCStaticTextField.class);
            registerChild(CHILD_CATEGORY_TILEDVIEW, CategoryTiledView.class);
        }
        m_elementsInvolvedModel.registerChildren(this);
        m_pagetitleModel.registerChildren(this);
    }

    protected View createChild(String name) {
        if (name.equals(CHILD_MASTHEAD)) {
            CCSecondaryMasthead child   = new CCSecondaryMasthead(this, name);
            String sSubProductName = m_I18n.getMessage("APOC.masthead.altText");
        
            child.setAlt(sSubProductName);
            child.setHeight("21");
            child.setWidth("355");
            child.setSrc("/apoc/images/popuptitle.gif");
            return child;
            
        } else if (name.equals(CHILD_PAGETITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_pagetitleModel, name);
            Toolbox2.setPageTitleHelp(m_pagetitleModel, "APOC.configrep.effective.settings", 
                                "APOC.configrep.help", "gbgoz.html");
            return child;  
            
        } else if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
            return child;
               
        } else if (name.equals(CHILD_ELEMENTS_INVOLVED_LIST)) {
            CCSelectableList child = new CCSelectableList(this, name, null);
            return child;  
            
        } else if (name.equals(CHILD_ELEMENTS_INVOLVED_TABLE)) {
            CCActionTable child = new CCActionTable(this, m_elementsInvolvedModel, name);
            return child;  
            
        } else if (name.equals(CHILD_ELEMENTS_INVOLVED_LABEL)) {
            CCLabel child = new CCLabel(this,
                name, m_I18n.getMessage("APOC.report.addremove.elements"));   
            return child;
        
        } else if (name.equals(CHILD_ADDBUTTON) || name.equals(CHILD_REMOVEBUTTON)
                        || name.equals(CHILD_UPBUTTON) || name.equals(CHILD_DOWNBUTTON)) { 
            CCButton child =  new CCButton(this, name, null); 
            return child; 
        
        } else if (name.equals(CHILD_HIDDEN_ORGENTITY) || name.equals(CHILD_HIDDEN_DOMENTITY)
                    || name.equals(CHILD_HIDDEN_MERGE_ORDER) ) {
            CCHiddenField child = new CCHiddenField(this, name, "");
            return child;

        } else if (m_pagetitleModel.isChildSupported(name)) {
            return m_pagetitleModel.createChild(this, name);
         
        } else if (m_elementsInvolvedModel.isChildSupported(name)) {
            return m_elementsInvolvedModel.createChild(this, name);    
            
        } else if (name.equals(CHILD_REPORT_INFO)) {
            CCStaticTextField child = new CCStaticTextField(this, name, "");
            return child;
            
        } else if (name.equals(CHILD_WINDOW_TITLE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
            
        } else if (name.equals(CHILD_CREATION_INFO)) {
            // #b4990175 make the date locale specific
            HttpServletRequest request = RequestManager.getRequestContext().getRequest();
            Locale locale = request.getLocale();
            DateFormat dateFormatter = DateFormat.getDateTimeInstance(
                DateFormat.FULL, DateFormat.FULL, locale);
            Date rightNow = new Date();
            String sDateNow = dateFormatter.format(rightNow);
            Object[] args = {sDateNow};
            String sCreationDate = m_I18n.getMessage("APOC.report.createdate", args);
            CCStaticTextField child = new CCStaticTextField(this, name, sCreationDate);
            return child;
            
        } else if (name.equals(CHILD_PROFILES_INVOLVED_TITLE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, "");
            return child;
            
         } else if (name.equals(CHILD_ENTITY_INVOLVED_TITLE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, "");
            return child;
           
         } else if (name.equals(CHILD_PROFILES_INVOLVED)) {
            CCStaticTextField child = new CCStaticTextField (this, name, "");
            return child;
     
        } else if (name.equals(CHILD_CATEGORY_TILEDVIEW)) {
            CategoryTiledView child = new CategoryTiledView(this, name);
            return child;
        
        } else if (name.equals(CHILD_NOSECTIONS)) {
             CCStaticTextField child = new CCStaticTextField(this, name, "");
            return child;
        
        } else if (name.equals(CHILD_STACKTRACE)) {
             CCStaticTextField child = new CCStaticTextField(this, name, "");
            return child;  
            
        } else if (name.equals(CHILD_SETTINGS_TITLE)) {
             CCStaticTextField child = new CCStaticTextField(this, name, 
                                            m_I18n.getMessage("APOC.report.settings.summary.title"));
            return child;
            
        } else if (name.equals(CHILD_NAME_HEADING)) {
             CCStaticTextField child = new CCStaticTextField(this, name, 
                                            m_I18n.getMessage("APOC.report.table.nameheading"));
            return child;
            
        } else if (name.equals(CHILD_VALUE_HEADING)) {
             CCStaticTextField child = new CCStaticTextField(this, name, 
                                            m_I18n.getMessage("APOC.report.table.valueheading"));
            return child;
        
        } else if (name.equals(CHILD_STATUS_HEADING)) {
             CCStaticTextField child = new CCStaticTextField(this, name, 
                                            m_I18n.getMessage("APOC.report.table.statusheading"));
            return child;
            
        } else {
            throw new IllegalArgumentException( 
                "Invalid child name [" + name + "]");
        }
    }
       
    public void beginDisplay(DisplayEvent event) 
            throws ModelControlException {
        
        CCHiddenField hiddenOrgId    = (CCHiddenField) getChild(CHILD_HIDDEN_ORGENTITY);
        CCHiddenField hiddenDomId    = (CCHiddenField) getChild(CHILD_HIDDEN_DOMENTITY);
        CCHiddenField hiddenMergeOrder = (CCHiddenField) getChild(CHILD_HIDDEN_MERGE_ORDER);

        Entity selectedEntity = Toolbox2.getSelectedEntity();
        String selectedSource = selectedEntity.getPolicySourceName();
        if (m_AllSources.contains(EnvironmentConstants.USER_SOURCE)) {
            String hiddenOrgIdValue = Toolbox2.decode((String)hiddenOrgId.getValue());
            if (hiddenOrgIdValue.length() == 0) {
                if (selectedSource.equals(EnvironmentConstants.USER_SOURCE)) {
                    hiddenOrgId.setValue(Toolbox2.encode(selectedEntity.getId()));           
                } 
            }
        } 
        if (m_AllSources.contains(EnvironmentConstants.HOST_SOURCE)) {
            String hiddenDomIdValue = Toolbox2.decode((String)hiddenDomId.getValue());
            if (hiddenDomIdValue.length() == 0) {
                if (selectedSource.equals(EnvironmentConstants.HOST_SOURCE)) {
                    hiddenDomId.setValue(Toolbox2.encode(selectedEntity.getId()));           
                } 
            } 
        }

        String hiddenMergeOrderValue  = (String) hiddenMergeOrder.getValue();
        
        if (hiddenMergeOrderValue == null || hiddenMergeOrderValue.length() == 0 ) {
            String path = Toolbox2.getParentagePath(Toolbox2.getSelectedEntity(), false, true, ">");
            String topEntity = "";
            String baseEntity = "";
            if (path.indexOf(">") != -1) {
                String[] entities = path.split(" > ");
                topEntity = entities[0];
                baseEntity = entities[entities.length-1];
            } else {
                topEntity = path;
                baseEntity = path;
            }
            String defaultMergeOrder = baseEntity + " (" + topEntity + ")|" + Toolbox2.getSelectedEntity().getPolicySourceName();
            hiddenMergeOrder.setValue(defaultMergeOrder);
            hiddenMergeOrderValue = defaultMergeOrder;
        }

        String[] involvedSources = hiddenMergeOrderValue.split("\\|");
        for (int i = 1; i < involvedSources.length; i += 2) {
            m_InvolvedSources.add(involvedSources[i]);
        }   
        ArrayList elementTypes = new ArrayList();        
        if (m_InvolvedSources.contains(EnvironmentConstants.USER_SOURCE)
                    && m_InvolvedSources.contains(EnvironmentConstants.HOST_SOURCE)) {
            m_InvolvedElements.add(hiddenDomId.getValue());
            m_InvolvedElements.add(hiddenOrgId.getValue());
            elementTypes.add(EnvironmentConstants.HOST_SOURCE);
            elementTypes.add(EnvironmentConstants.USER_SOURCE);
        } else if (m_InvolvedSources.contains(EnvironmentConstants.USER_SOURCE)) {
            m_InvolvedElements.add(hiddenOrgId.getValue());
            elementTypes.add(EnvironmentConstants.USER_SOURCE);
        } else if (m_InvolvedSources.contains(EnvironmentConstants.HOST_SOURCE)) {
            m_InvolvedElements.add(hiddenDomId.getValue());
            elementTypes.add(EnvironmentConstants.HOST_SOURCE);
        }
        CCActionTable elementsInvolvedTable = (CCActionTable)getChild(CHILD_ELEMENTS_INVOLVED_TABLE);
        ElementsInvolvedTableModel elementsInvolvedModel = (ElementsInvolvedTableModel)elementsInvolvedTable.getModel();
        elementsInvolvedModel.retrieve(m_InvolvedElements, elementTypes);        
        setMemberVariables();
        CategoryTiledView categoryChild = ((CategoryTiledView)getChild(CHILD_CATEGORY_TILEDVIEW));
        CategoryTiledModel categoryModel = (CategoryTiledModel)categoryChild.getPrimaryModel();
        categoryModel.setData(m_resultsData);  
    }

    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        
        if ((alert.getSummary() != null) && (alert.getSummary().length() > 0)) {
            return true;
        }
        
        return false;
    }
    
    public boolean beginNoSectionsDisplay(ChildDisplayEvent event) {
        String sNoSections = m_I18n.getMessage("APOC.report.novalues");
        ((CCStaticTextField)getChild(this.CHILD_NOSECTIONS)).setValue(sNoSections);
        return !showAlert() && !sectionsAvailable();
    }
    
    public boolean beginPageTitleDisplay(ChildDisplayEvent event) {
        if (m_hideReport) {
            ((CCButton)getChild("PrintButton")).setExtraHtml("disabled='disabled'");
        }
        return true;
    }
    
    public boolean beginReportInfoDisplay(ChildDisplayEvent event) {
        ((CCStaticTextField)getChild(CHILD_REPORT_INFO)).setValue("APOC.configrep.effective.settings"); 
        ((CCStaticTextField)getChild(CHILD_WINDOW_TITLE)).setValue("APOC.configrep.effective.settings");
        ((CCStaticTextField)getChild(CHILD_WINDOW_TITLE)).setEscape(false);
        return true;
    }
    
    public boolean beginCategoryTiledViewDisplay(ChildDisplayEvent event){
        return sectionsAvailable();
    }

    public boolean beginDisplayTablesDisplay(ChildDisplayEvent event){
        return sectionsAvailable();
    }
    
    public boolean beginDisplayProfilesTableDisplay(ChildDisplayEvent event){
        return showAlert() || sectionsAvailable();
    }    
    
    public boolean beginDisplayReportDisplay(ChildDisplayEvent event){
        if (m_isFirstLoad) {
            return true;
        }
        return !m_hideReport;
    }
    
    public boolean beginProfilesInvolvedTitleDisplay(ChildDisplayEvent event){
        
        LinkedList profileList = m_PolicyMgrHelper.getPolicyGroupList();
        String tableTitle = m_I18n.getMessage("APOC.report.assigned.profiles.title");
        tableTitle += " (" + Integer.toString(profileList.size()) + ")";
        ((CCStaticTextField)getChild(CHILD_PROFILES_INVOLVED_TITLE)).setValue(tableTitle);      

        return showAlert() || sectionsAvailable();
    }
    
    public boolean beginProfilesInvolvedDisplay(ChildDisplayEvent event){
        
        LinkedList profileList = m_PolicyMgrHelper.getPolicyGroupList();
        StringBuffer buffer = new StringBuffer();
        Iterator iter = profileList.iterator();
        int counter = 0;
        int lastProfile = profileList.size();
        while (iter.hasNext()) {
            counter++;
            Profile profile = (Profile)iter.next();
            buffer.append("<a href=\"\" onclick=\"" )
                .append("javascript:var selectedProfile='")
                .append(profile.getId())
                .append("'; openWindow(window, null, '/apoc/manager/ProfileWindowFrameset?ProfileWindowFrameset.SelectedProfile='+escape(selectedProfile)")
                .append(", 'ProfileEditorWindow', 600, 1200, true); return false;\">")
                .append(profile.getDisplayName())
                .append("</a>");
            if (counter !=  lastProfile) {
                buffer.append(", ");
            }
        }
        ((CCStaticTextField)getChild(CHILD_PROFILES_INVOLVED)).setValue(buffer.toString());      
        ((CCStaticTextField)getChild(CHILD_PROFILES_INVOLVED)).setEscape(false);
        return showAlert() || sectionsAvailable();
    }
    
    
    public void handleReportButtonRequest(RequestInvocationEvent event) 
        throws ServletException, IOException {
        
        forwardTo(getRequestContext());
    }
     
    private CCPageTitleModel createPageTitleModel() {
        CCPageTitleModel model = new CCPageTitleModel(
                        RequestManager.getRequestContext().getServletContext(),
                        "/jsp/report/ShowResultsPageTitle.xml");
        model.setValue("ReportButton", "APOC.configrep.button.report");
        model.setValue("PrintButton", "APOC.report.buttons.print");
        model.setValue("CloseButton", "APOC.profilewnd.buttons.close");
        return model;
    }
   
    private void initMemberVariables() {
        try {
            m_isFirstLoad = Boolean.valueOf(Toolbox2.getParameter("isFirstLoad")).booleanValue();
            m_hideReport = Boolean.valueOf(Toolbox2.getParameter("isReportHidden")).booleanValue();  
            m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
            m_resultsData = new ArrayList();   
            m_allResultsData = new ArrayList(); 
            m_InvolvedSources = new ArrayList();
            m_InvolvedElements = new ArrayList();
            m_PolicyManager = Toolbox2.getPolicyManager();
            m_AllSources = new ArrayList(Arrays.asList(m_PolicyManager.getSources()));
            m_pagetitleModel = createPageTitleModel();
            m_elementsInvolvedModel = getElementsInvolvedModel();
        } catch (SPIException spie) {
            CCDebug.trace3(spie.toString());
        }
    }
    
    private void setMemberVariables() {
        try {
            ArrayList involvedEntities  = new ArrayList();
            for (int i = 0; i < m_InvolvedSources.size(); i++) {
                if (m_InvolvedSources.get(i).equals(EnvironmentConstants.USER_SOURCE)) {
                    String orgId    =  Toolbox2.decode(getDisplayFieldStringValue(CHILD_HIDDEN_ORGENTITY));
                    if (m_PolicyManager.getRootEntity(EnvironmentConstants.USER_SOURCE).getId().equals(orgId)) {
                        m_OrgEntity = m_PolicyManager.getRootEntity(EnvironmentConstants.USER_SOURCE);
                    } else {
                        m_OrgEntity = m_PolicyManager.getEntity(EnvironmentConstants.USER_SOURCE, orgId);
                    }
                    m_OrgEntityName = getDisplayName(m_OrgEntity);
                    involvedEntities.add(m_OrgEntity);
                } else if (m_InvolvedSources.get(i).equals(EnvironmentConstants.HOST_SOURCE)) {
                    String domId    =  Toolbox2.decode(getDisplayFieldStringValue(CHILD_HIDDEN_DOMENTITY));
                    if (m_PolicyManager.getRootEntity(EnvironmentConstants.HOST_SOURCE).getId().equals(domId)) {
                        m_DomEntity = m_PolicyManager.getRootEntity(EnvironmentConstants.HOST_SOURCE);
                    } else {
                        m_DomEntity = m_PolicyManager.getEntity(EnvironmentConstants.HOST_SOURCE, domId);
                    }
                    m_DomEntityName = getDisplayName(m_DomEntity);
                    involvedEntities.add(m_DomEntity);
                } 
            }
            m_PolicyMgrHelper = new PolicyMgrReportHelper(involvedEntities);
        } catch (SPIException e) {
            CCDebug.trace3("SPI Exception caught: " + e);
        }
        RequestManager.getRequestContext().getRequest().getSession(true).setAttribute(Constants.POLICY_MANAGER_HELPER, m_PolicyMgrHelper);
        RequestManager.getRequestContext().getRequest().getSession(true).setAttribute(Constants.REPORT_TYPE, Constants.ENTITY_REPORT_TYPE);

        if (!m_hideReport) {
            setData();
        }
    }    
    
    private void setData() {
        try {
            m_definedPropsMap = m_PolicyMgrHelper.getAllDefinedProperties();
        } catch (SPIException spie) {
            m_definedPropsMap = new HashMap();
            CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
            Toolbox2.prepareErrorDisplay(spie, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE));
            alert.setValue(CCAlertInline.TYPE_INFO);
            alert.setType(CCAlertInline.TYPE_INFO);
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            alert.setSummary(i18n.getMessage("APOC.report.xml.parsing.error"));
        }

        setSectionsList();
        Iterator it = m_allResultsData.iterator();
        while (it.hasNext()) {
            ResultsData data = (ResultsData)it.next();
            if (data.isASet()) {
                setSetData(data);
            } else {
                setSectionData(data);
            }
        }
    }
 
    private void setSetData(ResultsData data) {
        TemplateSet set = (TemplateSet)data.getSection();
        String setDataPath = set.getDataPath();
        String separator = TemplateRepository.TEMPLATE_PATH_SEPARATOR;
        if (!setDataPath.endsWith(separator)) {
            setDataPath = setDataPath + separator;
        }
        
        ArrayList setProps = new ArrayList();
        ArrayList dymanicSetNames = new ArrayList();
        Iterator keys = m_definedPropsMap.keySet().iterator();
        while (keys.hasNext()) {
            String fullDataPath = (String)keys.next();
            if (fullDataPath.startsWith(setDataPath)) {
                int start = setDataPath.length();
                int end = fullDataPath.indexOf("/", start);
                String setNode = fullDataPath.substring(0, end + 1);
                try {
                    if (m_PolicyMgrHelper.isASetNode(setNode)) {
                        String setName  = fullDataPath.substring(start, end);
                        if (!dymanicSetNames.contains(setName)) {
                            dymanicSetNames.add(setName);
                        }
                    }
                } catch (SPIException spie) {
                    CCDebug.trace3("SPI Exception caught: " + spie);                    
                }
            }
        }
        Property property = null;
        Iterator it = dymanicSetNames.iterator();
        while (it.hasNext()) {
            String setName = (String)it.next();
            ArrayList propsWithValues = new ArrayList();
            ArrayList templatePropsWithValues = new ArrayList();
            TemplatePage page = set.getPage();
            List sectionList = page.getSections();
            if (sectionList != null) {
                Iterator sectionIt = sectionList.iterator();
                while (sectionIt.hasNext()) {
                    TemplateSection section  = (TemplateSection)sectionIt.next();
                    List sectionProps = section.getProperties();
                    if (sectionProps != null) {
                        Iterator propsIt = sectionProps.iterator();
                        while (propsIt.hasNext()) {
                            TemplateProperty prop = (TemplateProperty)propsIt.next();  
                            String propDataPath = prop.getDataPath();
                            property = null;
                            String dataPath = propDataPath.replaceFirst("./", setDataPath);
                            dataPath = dataPath.replaceFirst(SET_PLACEHOLDER, setName);
                            property = (Property)m_definedPropsMap.get(dataPath); 
                            if (property != null) {
                                propsWithValues.add(property);
                                templatePropsWithValues.add(prop);
                            }
                        }
                    }
                }
            }

            String path  = data.getPath();
            String linkPath = data.getLinkPath();
            path = path + ";" + set.getDefaultName() + ";" + Toolbox2.decode(setName);
            linkPath = linkPath + ";" + TemplateRepository.SET_PREFIX + set.getDefaultName() + TemplateRepository.SET_INDEX_SEPARATOR + Toolbox2.decode(setName);           
            ResultsData resultsData = new ResultsData(set, templatePropsWithValues, propsWithValues, path, linkPath, true);
            m_resultsData.add(resultsData);
        }
    }

    private void setSectionData(ResultsData data) {
        TemplateSection section = data.getSection();
        List sectionProps = section.getProperties();
        if (sectionProps != null) {
            int     sectionPropsSize = sectionProps.size();
            boolean sectionHasValue = false;
            Property property = null;
            ArrayList propsWithValues = new ArrayList();
            ArrayList templatePropsWithValues = new ArrayList();

            Iterator listIt = sectionProps.iterator();
            while (listIt.hasNext()) {
                TemplateProperty prop   = (TemplateProperty)listIt.next();  
                String dataPath         = prop.getDataPath();
                property = (Property)m_definedPropsMap.get(dataPath);
                if (property != null) {
                    propsWithValues.add(property);
                    templatePropsWithValues.add(prop);
                    sectionHasValue = true;
                }

            }

            if (sectionHasValue == true) {
                String path  = data.getPath();
                String linkPath = data.getLinkPath();
                ResultsData resultsData = new ResultsData(section, templatePropsWithValues, propsWithValues, path, linkPath, false);
                m_resultsData.add(resultsData);
            }
        }
    }

    private void setSectionsList() {
        TemplateRepository tmpRep = TemplateRepository.getDefaultRepository();
        ResourceRepository resourceRepository   = ResourceRepository.getDefaultRepository();
        HashMap topLevelCatMap = tmpRep.getTopLevelCategories();
        m_sectionPathsMap = new HashMap();
        Iterator it = topLevelCatMap.values().iterator();
        while (it.hasNext()) {
            TemplateCategory cat  = (TemplateCategory)it.next();
            String catname        = resourceRepository.getMessage(
                                        cat.getResourceId(),
                                        cat.getResourceBundle(),
                                        RequestManager.getRequest());
            recursiveSetSectionsList(cat, catname, cat.getDefaultName());
        }
    }

    private void recursiveSetSectionsList(TemplateCategory cat, String path, String linkPath) {
        if (cat.hasSubCategories()) {
            HashMap  subCats = cat.getSubCategories();
            Iterator it = subCats.values().iterator();
            ResourceRepository resourceRepository   = ResourceRepository.getDefaultRepository();

            // There are subcategories so go recursively through them
            while (it.hasNext()) {
                TemplateCategory subCategory    = (TemplateCategory) it.next();    
                String catname                  = resourceRepository.getMessage(
                                                    subCategory.getResourceId(),
                                                    subCategory.getResourceBundle(),
                                                    RequestManager.getRequest());
 
                StringBuffer pathCopy = new StringBuffer();
                pathCopy.append(path).append(";");
                pathCopy.append(catname);

                StringBuffer linkPathCopy = new StringBuffer();
                linkPathCopy.append(linkPath).append(";");
                linkPathCopy.append(subCategory.getDefaultName());

                
                recursiveSetSectionsList(subCategory, pathCopy.toString(), linkPathCopy.toString());
            }
        } else {
            TemplatePage subPage = (TemplatePage) cat;
            if ((subPage != null) && (subPage.getSections() != null)) {
                List sections = subPage.getSections();
                for (int i = 0; i < sections.size(); i++) {
                    TemplateSection section        = subPage.getSection(i);
                    ResultsData resultsData = null;
                    if (isASet(section)) {
                        resultsData = new ResultsData(section, null, null, path, linkPath, true);    
                    } else {
                        resultsData = new ResultsData(section, null, null, path, linkPath, false);
                    }
                    m_allResultsData.add(resultsData);
                }
            }
        } 
    }
    
    private boolean isASet(TemplateSection section) {
        boolean isASet = true;
        try {
            TemplateSet set = (TemplateSet)section;
        } catch (ClassCastException cce) {
            isASet= false;
        }
        return isASet;
    }
    
    private boolean sectionsAvailable(){
        if (m_resultsData.size() > 0) 
            return true;
        else    
            return false;
    }    
    
    private boolean showAlert() {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        if ((alert.getSummary() != null) && (alert.getSummary().length() > 0)) {
            return true;
        }
        return false;
    }
    
    private String getParentsString(Entity entity) {
        return Toolbox2.getParentagePath(entity, false, false, ">");
    }    

    private String getDisplayName(Entity entity) throws SPIException {
        String sName = entity.getDisplayName(Toolbox2.getLocale());
        if (entity.equals(m_PolicyManager.getRootEntity(EnvironmentConstants.HOST_SOURCE))) {
            sName = m_I18n.getMessage("APOC.navigation.domains");
        } else if (entity.equals(m_PolicyManager.getRootEntity(EnvironmentConstants.USER_SOURCE))) {
            sName = m_I18n.getMessage("APOC.navigation.organizations");
        } 
        return sName;
    }
    
    private String getEntityTypeResource(String sSource, Entity entity) {
        String      sResource       = "";
    
         if (sSource.equals(EnvironmentConstants.USER_SOURCE)) {
            if (entity instanceof Leaf) {
                sResource  = m_I18n.getMessage("APOC.navigation.user");
            } else {
                sResource  = m_I18n.getMessage("APOC.navigation.organization");
            }
        } else if (sSource.equals(EnvironmentConstants.HOST_SOURCE)) {
            if (entity instanceof Leaf) {
                sResource  = m_I18n.getMessage("APOC.navigation.host");
            } else {
                sResource  = m_I18n.getMessage("APOC.navigation.domain");
            }
        } 
        if (entity instanceof Role) {
                sResource  = m_I18n.getMessage("APOC.navigation.role");
        }   
        
        return sResource;
    }
}    
