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

import com.sun.apoc.manager.report.CategoryTiledModel;
import com.sun.apoc.manager.report.CategoryTiledView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.report.PolicyMgrReportHelper;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.model.CCPageTitleModel;

import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.cfgtree.property.Property;

import com.sun.apoc.templates.parsing.TemplatePage;
import com.sun.apoc.templates.parsing.TemplateSet;
import com.sun.apoc.templates.parsing.TemplateSection;
import com.sun.apoc.templates.parsing.TemplateCategory;
import com.sun.apoc.templates.parsing.TemplateProperty;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.sun.apoc.manager.report.ResultsData;
import com.sun.web.ui.view.alert.CCAlertInline;
import java.io.IOException;
import javax.servlet.ServletException;


public class SettingsSummaryViewBean extends ViewBeanBase {
    
    // The "logical" name for this page.
    public static final String PAGE_NAME =
        "SettingsSummary";

    // The URL that points to the JSP which uses this ViewBean
    public static final String DEFAULT_DISPLAY_URL =
	"/jsp/SettingsSummary.jsp";
    public static final String CHILD_PAGETITLE =
        "PageTitle";
    public static final String CHILD_ALERT =
        "Alert";
    public static final String CHILD_STACKTRACE =
        "StackTrace";
    public static final String CHILD_CONTENTS_TITLE =
        "ContentsTitle";
    public static final String CHILD_SETTINGS_TITLE =
        "SettingsSummaryTitle"; 
    public static final String CHILD_NAME_HEADING =
        "NameHeading";     
    public static final String CHILD_VALUE_HEADING =
        "ValueHeading"; 
    public static final String CHILD_STATUS_HEADING =
        "StatusHeading"; 
    public static final String CHILD_CATEGORY_TILEDVIEW =
        "CategoryTiledView";
    public static final String CHILD_NOSECTIONS =
        "NoSections";

    private CCI18N              m_I18n = null;
    private Profile             m_PolicyGroup = null;
    private PolicyMgrReportHelper m_PolicyMgrHelper = null;
    private CCPageTitleModel    m_pagetitleModel = null;
    private HashMap             m_sectionPathsMap   = null;
    private HashMap             m_definedPropsMap   = null;
    private ArrayList           m_resultsData       = null;
    private ArrayList           m_allResultsData    = null;    

    /** Creates a new instance of ReportViewBean */
    public SettingsSummaryViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);        
        initMemberVariables();
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_PAGETITLE, CCPageTitle.class);
        registerChild(CHILD_ALERT, CCAlertInline.class);
        registerChild(CHILD_STACKTRACE, CCStaticTextField.class);
        registerChild(CHILD_CONTENTS_TITLE, CCStaticTextField.class);
        registerChild(CHILD_SETTINGS_TITLE, CCStaticTextField.class);
        registerChild(CHILD_NAME_HEADING, CCStaticTextField.class);
        registerChild(CHILD_VALUE_HEADING, CCStaticTextField.class);
        registerChild(CHILD_STATUS_HEADING, CCStaticTextField.class);
        registerChild(CHILD_CATEGORY_TILEDVIEW, CategoryTiledView.class);
        registerChild(CHILD_NOSECTIONS, CCStaticTextField.class);
    }

    protected View createChild(String name) {
        if (name.equals(CHILD_PAGETITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_pagetitleModel, name);
            return child;         
        
        } else if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
            return child;
            
        } else if (name.equals(CHILD_STACKTRACE)) {
             CCStaticTextField child = new CCStaticTextField(this, name, "");
            return child;  
            
        } else if (name.equals(CHILD_CONTENTS_TITLE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, m_I18n.getMessage("APOC.report.contitle"));
            return child;
        
        } else if (name.equals(CHILD_CATEGORY_TILEDVIEW)) {
            CategoryTiledView child = new CategoryTiledView(this, name);
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
            
        } else if (name.equals(CHILD_NOSECTIONS)) {
            String sArgs = m_PolicyGroup.getDisplayName();
            Object[] args = {sArgs};
            String sNoSections = m_I18n.getMessage("APOC.report.table.empty", args);
            CCStaticTextField child = new CCStaticTextField(this, name, sNoSections);
            return child;
            
        } else if (m_pagetitleModel.isChildSupported(name)) {
            return m_pagetitleModel.createChild(this, name);
                    
        } else {
            throw new IllegalArgumentException( 
                "Invalid child name [" + name + "]");
        }
    }
    
    public void beginDisplay(DisplayEvent event) {
        StringBuffer buffer     = new StringBuffer(250);
        buffer.append(Toolbox2.buildProfileTitle("APOC.report.settings.summary.title"));
        m_pagetitleModel.setPageTitleText(buffer.toString());  
        Toolbox2.setPageTitleHelp(m_pagetitleModel, "APOC.report.settings.summary.title", 
                                "APOC.profilewnd.summary.help", "gbgda.html");
        setData();
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
    
  /*  public boolean beginNoSectionsDisplay(ChildDisplayEvent event) {
        return !sectionsAvailable();
    }*/

    public boolean beginContentsTitleDisplay(ChildDisplayEvent event){
        return sectionsAvailable();
    }
    
    public boolean beginCategoryTiledViewDisplay(ChildDisplayEvent event){
        return sectionsAvailable();
    }

    public boolean beginDisplayTablesDisplay(ChildDisplayEvent event){
        return sectionsAvailable();
    }
    
    public boolean beginNoSectionsDisplay(ChildDisplayEvent event) {
     //   String sNoSections = m_I18n.getMessage("APOC.report.novalues");
    //    ((CCStaticTextField)getChild(this.CHILD_NOSECTIONS)).setValue(sNoSections);
        return !showAlert() && !sectionsAvailable();
    }
    
    public void handleCloseButtonRequest(RequestInvocationEvent event)
                throws ServletException, IOException {
        
        CCStaticTextField child = (CCStaticTextField)((ProfileWindowViewBean)getViewBean(ProfileWindowViewBean.class)).getChild(ProfileWindowViewBean.CHILD_TAB_CHANGE_SCRIPT);
        child.setValue("closeEditorAndRefresh();");
        getRootView().forwardTo(getRequestContext());
    }  
     
    private CCPageTitleModel createPageTitleModel() {
        CCPageTitleModel model = new CCPageTitleModel(
                        RequestManager.getRequestContext().getServletContext(),
                        "/jsp/profiles/SettingsSummaryPageTitle.xml");
        model.setValue("CloseButton", "APOC.profilewnd.buttons.close");
        return model;
    }
     
    private void initMemberVariables() {
        m_pagetitleModel = createPageTitleModel();
        // The resources
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        // The results data arrays
        m_resultsData = new ArrayList();   
        m_allResultsData = new ArrayList(); 
        
        m_PolicyGroup = ProfileWindowFramesetViewBean.getSelectedProfile();
        m_PolicyMgrHelper  =  new PolicyMgrReportHelper(m_PolicyGroup);  
        RequestManager.getRequestContext().getRequest().getSession(true).setAttribute(Constants.REPORT_TYPE, Constants.POLICY_GROUP_REPORT_TYPE);
        RequestManager.getRequestContext().getRequest().getSession(true).setAttribute(Constants.POLICY_MANAGER_HELPER, m_PolicyMgrHelper);
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
                            dataPath = dataPath.replaceFirst(ShowResultsViewBean.SET_PLACEHOLDER, setName);
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
            path = path + ";" + set.getDefaultName() + ";" + Toolbox2.decode(setName);
            String linkPath  = data.getLinkPath();
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
                String linkPath  = data.getLinkPath();
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
    
    private boolean showAlert() {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        if ((alert.getSummary() != null) && (alert.getSummary().length() > 0)) {
            return true;
        }
        return false;
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
    
    private String getParentsString(Entity entity) {
        return Toolbox2.getParentagePath(entity, false, false, ">");
    }
    
}
