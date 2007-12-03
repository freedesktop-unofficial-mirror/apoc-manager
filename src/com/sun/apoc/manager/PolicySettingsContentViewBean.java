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

import java.io.IOException;

import javax.servlet.ServletException;

import java.util.ArrayList;

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.profiles.ProfileWindowModel;
import com.sun.apoc.manager.profiles.ProfileWindowSettingsCategoriesTableModel;
import com.sun.apoc.templates.parsing.TemplatePage;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.sun.apoc.manager.settings.JumpLinks;
import com.sun.apoc.manager.settings.SectionModel;
import com.sun.apoc.manager.settings.Sheet;
import com.sun.apoc.manager.settings.SheetModel;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.table.CCActionTable;
import com.sun.web.ui.common.CCI18N;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.taglib.header.CCHtmlHeaderTag;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.alert.CCAlert;
import java.io.File;
import java.util.Collections;
import java.util.Locale;


public class PolicySettingsContentViewBean extends ViewBeanBase {

    public static final String PAGE_NAME           = "PolicySettingsContent";
    public static final String DEFAULT_DISPLAY_URL = "/jsp/settings/Content.jsp";
    
    public static final String CHILD_PAGETITLE          = "PageTitle";
    public static final String CHILD_HEADER             = "Header";
    public static final String CHILD_FORM               = "Form";
    public static final String CHILD_ALERT              = "Alert";
    public static final String CHILD_ALERT_JS           = "AlertJavascript";
    public static final String CHILD_NAVIGATION_TABLE   = "PoliciesNavigationTable";
    public static final String CHILD_TEMPLATE_CONTENT   = "PolicyTemplateContent";
    public static final String CHILD_JUMP_LINKS         = "JumpLinksSection";
    public static final String CHILD_SECTIONS_TILED     = "SectionsTiledView";
    public static final String CHILD_SAVE_BUTTON        = "SaveButton";
    public static final String CHILD_FOOTNOTE           = "Footnote";
    public static final String CHILD_PROTECTED_CHOOSERS = "ProtectedChoosers";
    public static final String CHILD_PROTECTED_CHOOSERS_TYPE = "ProtectedChoosersType";  
    public static final String CHILD_HANDLER_TEXT       = "XmlHandlerText";
    public static final String CHILD_DISABLED_FIELDS_VALUES = "DisabledFieldsValues";
    public static final String CHILD_INVALID_HEX        = "InvalidHex";
    public static final String CHILD_LOCALIZED_NOTSET   = "LocalizedNotSet";
    public static final String CHILD_REFRESH_SCRIPT     = "RefreshNavigationTreeScript";
    public static final String CHILD_ORIGINAL_VALUES    = "OriginalValues";
    public static final String CHILD_JUMPTO_SCRIPT      = "JumpToScript";
    public static final String CHILD_CLOSE_WINDOW_SCRIPT = "CloseWindowScript";
    public static final String CHILD_HELP_PAGE          = "HelpPage";

    private String mFirstDisplay = null; 
    public static final String FIRST_DISPLAY = "firstDisplay";
    
    private CCPageTitleModel mTitleModel = null;
    private ProfileWindowSettingsCategoriesTableModel mCategoriesModel = null;
    private ProfileWindowModel mEditorModel = null;
    private SheetModel mTemplateModel = null;
    private CCI18N m_i18n = null;
    
    public PolicySettingsContentViewBean() {
    	super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        m_i18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        registerChildren();
    }

    protected void registerChildren() {
        registerChild(CHILD_PAGETITLE, CCPageTitle.class);
        registerChild(CHILD_FORM, CCForm.class);
        registerChild(CHILD_ALERT, CCAlertInline.class);
        registerChild(CHILD_ALERT_JS, CCStaticTextField.class);
        registerChild(CHILD_NAVIGATION_TABLE, CCActionTable.class);
        registerChild(CHILD_TEMPLATE_CONTENT, Sheet.class);
        registerChild(CHILD_JUMP_LINKS, JumpLinks.class);
        registerChild(CHILD_INVALID_HEX, CCStaticTextField.class);        
        registerChild(CHILD_LOCALIZED_NOTSET, CCStaticTextField.class);
        registerChild(CHILD_SAVE_BUTTON, CCButton.class);
        registerChild(CHILD_FOOTNOTE, CCStaticTextField.class);
        registerChild(CHILD_PROTECTED_CHOOSERS, CCHiddenField.class);
        registerChild(CHILD_PROTECTED_CHOOSERS_TYPE, CCHiddenField.class);        
        registerChild(CHILD_HANDLER_TEXT, CCStaticTextField.class);
        registerChild(CHILD_DISABLED_FIELDS_VALUES, CCHiddenField.class);
        registerChild(CHILD_REFRESH_SCRIPT, CCStaticTextField.class); 
        registerChild(CHILD_ORIGINAL_VALUES, CCHiddenField.class);
        registerChild(CHILD_JUMPTO_SCRIPT, CCStaticTextField.class);
        registerChild(CHILD_CLOSE_WINDOW_SCRIPT, CCStaticTextField.class);
        registerChild(CHILD_HELP_PAGE, CCHiddenField.class);
        getPageTitleModel().registerChildren(this);
        getCategoriesModel().registerChildren(this);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_PAGETITLE)) {
            CCPageTitle child = new CCPageTitle(this, getPageTitleModel(), name);
            Toolbox2.setPageTitleHelp(getPageTitleModel(), "APOC.profilewnd.settings.title", 
                                    "APOC.profilewnd.settings.title.help", "gbgeb.html");
            return child;
        
        } else if (name.equals(CHILD_FORM)) {
            CCForm child = new CCForm(this, name);
            return child;
        
        } else if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
            return child;         
            
        } else if (name.equals(CHILD_ALERT_JS)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, null);
            return child;    
            
        } else if (name.equals(CHILD_SAVE_BUTTON)) {
            CCButton child = new CCButton(this, name, null);
            return child;
            
        } else if (name.equals(CHILD_REFRESH_SCRIPT)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, null);
            return child;        
            
        } else if (name.equals(CHILD_FOOTNOTE)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, null);
            child.setValue(m_i18n.getMessage("APOC.policies.footnote"));
            return child;    
         
        } else if (name.equals(CHILD_JUMPTO_SCRIPT)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, null);
            return child;
            
        } else if (name.equals(CHILD_CLOSE_WINDOW_SCRIPT)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, null);
            return child;
            
         } else if (name.equals(CHILD_INVALID_HEX)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, null);
            child.setValue(m_i18n.getMessage("APOC.setcolor.invalid.hex"));
            return child;
            
         } else if (name.equals(CHILD_LOCALIZED_NOTSET)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, null);
            child.setValue(SectionModel.getUndefinedValue());
            return child;
            
        } else if (name.equals(CHILD_TEMPLATE_CONTENT)) { 
            Sheet child = new Sheet(this, getTemplateModel(), name, false);
            return child;
            
        } else if (name.equals(CHILD_JUMP_LINKS)) {
            JumpLinks child = new JumpLinks(this, getTemplateModel(), name, false);
            return child;
        
        } else if (name.equals(CHILD_PROTECTED_CHOOSERS)) { 
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;     
	    
        } else if (name.equals(CHILD_PROTECTED_CHOOSERS_TYPE)) { 
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;   
            
        } else if (name.equals(CHILD_HANDLER_TEXT)) {
	    CCStaticTextField child =  new CCStaticTextField(this, name, null);
            return child;
         
        } else if (name.equals(CHILD_ORIGINAL_VALUES)) { 
            CCHiddenField child = 
                new CCHiddenField(this, name, null);
            return child; 
            
         } else if (name.equals(CHILD_DISABLED_FIELDS_VALUES)) { 
            CCHiddenField child = 
                new CCHiddenField(this, name, null);
            return child;   

        } else if (name.equals(CHILD_HELP_PAGE)) { 
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;   
            
        } else if (getPageTitleModel().isChildSupported(name)) {
            return getPageTitleModel().createChild(this, name);

        } else if (name.equals(CHILD_NAVIGATION_TABLE)) { 
            CCActionTable child = new CCActionTable(this,  
                getCategoriesModel(), name);
            return child;
            
        } else if (getCategoriesModel().isChildSupported(name)) {
            return getCategoriesModel().createChild(this, name);

        } else {
            throw new IllegalArgumentException( 
                "Invalid child name [" + name + "]");
        }
    }
    
    
    public boolean beginFootnoteDisplay(ChildDisplayEvent event) 
            throws ModelControlException {
        return !getCategoriesModel().hasSubCategories();
    }
    
    
    public boolean beginPoliciesNavigationAreaDisplay(ChildDisplayEvent event) 
            throws ModelControlException {
        CCPageTitle child = (CCPageTitle) getChild(CHILD_PAGETITLE);
        Profile profile = getEditorModel().getProfile();
        TemplatePage page = getCategoriesModel().getSelectedTemplatePage();
        ResourceRepository resources = ResourceRepository.getDefaultRepository();
        String categoryName = resources.getMessage(
                               page.getResourceId(), 
                               page.getResourceBundle(), 
                               RequestManager.getRequest());
        Object[] args = {profile.getDisplayName()};
        String settingsTitle = m_i18n.getMessage("APOC.policies.settings_title", args);
        if (categoryName.equals("APOC.policies.root")) {
            child.getModel().setPageTitleText(settingsTitle);
        } else {
            child.getModel().setPageTitleText(categoryName + " - " + settingsTitle);
        }
        if (page.getDescriptionId() != null) {
            String help = resources.getMessage(
                            page.getDescriptionId(), 
                            page.getResourceBundle(), 
                            RequestManager.getRequest());
            

            String helpFile = page.getHelpFile();
            // If there is no helpFile specified we are either on a navigation page
            // or there simply wasn't one specified and we want the normal online
            // help to appear.
            if (helpFile==null)
                helpFile="policies";

            // set the current package name
            String pkgName = page.getTemplatePackageName();

            // set the country and language
            boolean fileExists = false;
            StringBuffer name = new StringBuffer();
            StringBuffer checkFileName = new StringBuffer();
            StringBuffer helpLocale = new StringBuffer();
            File associatedHelpFile;
            String fileName;
            if (!helpFile.equals("policies")){

                checkFileName.append("packages").append(File.separator)
                    .append(pkgName).append(File.separator)
                    .append("web")
                    .append(helpFile);
                ArrayList locales = (ArrayList) Collections.list(
                    getRequestContext().getRequest().getLocales());
                for (int i=0; i<locales.size(); i++){
                    name = new StringBuffer();
                    Locale locale = (Locale) locales.get(i);
                    String lang = locale.getLanguage();
                    if (lang != null && lang.length() > 0) {
                        helpLocale.append("_").append(lang);
                        String country = locale.getCountry();
                        if (country != null && country.length() > 0) {
                            helpLocale.append("_").append(country);
                            String variant = locale.getVariant();
                            if (variant != null && variant.length() > 0) {
                                helpLocale.append("_").append(variant);
                            }
                        }
                    }
                    // The english help files are all en_US suffixed so need to 
                    // handle them specially
                    if (helpLocale.toString().equals("_en")) {
                        helpLocale.append("_US");
                    }
                    name.append(checkFileName.toString())
                        .append(helpLocale.toString())
                        .append(".html");

                    fileName = getRequestContext().
                        getServletContext().getRealPath(name.toString());
                    associatedHelpFile = new File(fileName);
                    if (associatedHelpFile.exists()) {
                        fileExists = true;
                    } else {
                        // If the fully specified locale file doesn't exist try
                        // step back from the variant, to the country, to the language,
                        // to the plain file name
                        int loc = helpLocale.toString().lastIndexOf("_");
                        while (loc != -1) {
                            name.delete(0, name.length());
                            helpLocale.delete(loc, helpLocale.length());
                            // The english help files are all en_US suffixed so need to 
                            // handle them specially
                            if (helpLocale.toString().equals("_en")) {
                                helpLocale.append("_US");
                            }
                            name.append(checkFileName.toString())
                                .append(helpLocale.toString())
                                .append(".html");
                            fileName = getRequestContext().
                                getServletContext().getRealPath(name.toString());
                            associatedHelpFile = new File(fileName);
                            if (associatedHelpFile.exists()) {
                                fileExists = true;
                                break;
                            }
                            if (helpLocale.toString().equals("_en_US")) {
                                loc = -1;
                            } else {
                                loc = helpLocale.toString().lastIndexOf("_");
                            }
                        }
                    }
                    if (fileExists == true) {
                        break;    
                    }

                    helpLocale.delete(0, helpLocale.length());
                }
                
                // A localized file was found so set hidden field
                if (fileExists == false) {
                    // Try for the default of en_US
                    name = name.delete(0, name.length());
                    name.append(checkFileName.toString())
                        .append("_en_US.html");
                    fileName = getRequestContext().
                        getServletContext().getRealPath(name.toString());                
                    associatedHelpFile = new File(fileName);

                    if (associatedHelpFile.exists()) {
                        fileExists=true;
                    }
                }
            }            
            
            
            
            CCHiddenField pagePath = (CCHiddenField)getChild(CHILD_HELP_PAGE);
            pagePath.setValue(File.separator + "apoc" + File.separator + name);
 
            if (fileExists == true) {
                StringBuffer linkBuffer = new StringBuffer();
                String tooltip = m_i18n.getMessage("APOC.help.helpTooltip");
                linkBuffer.append("<a href=\"\" title=\"")
                            .append(tooltip)
                            .append("\" onClick=\"javascript: openTemplateHelp(); return false;\">")
                            .append(m_i18n.getMessage("APOC.help.more"))
                            .append("</a>");
                help = help + " " + linkBuffer.toString(); 
            }
            child.getModel().setPageTitleHelpMessage(help);
            
        }
        
        return getCategoriesModel().hasSubCategories();
    }

    public void beginDisplay(DisplayEvent event) throws ModelControlException {

        getCategoriesModel().setRequestContext(getRequestContext());
        
        // show an info message if the user has read-only access
        if (getEditorModel().isReadOnlyProfile()) {
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            getEditorModel().setInfoMessage(i18n.getMessage("APOC.policies.info.readonly.access"), "");
        }
        if (getEditorModel().getAnchor() != null && getEditorModel().getAnchor().length() != 0) {
            CCStaticTextField anchorText = (CCStaticTextField) getChild(CHILD_JUMPTO_SCRIPT);
            anchorText.setEscape(false);
            anchorText.setValue("jumpTo('"+ getEditorModel().getAnchor() + "');");
        }
        String query = getRequestContext().getRequest().getQueryString();
        if (query != null) {
            String selectedSet = Toolbox2.decode(query.substring(query.indexOf("=") + 1));
            if (query.indexOf(TemplateRepository.SET_PREFIX) != -1) {
                getEditorModel().setSelectedCategory(selectedSet);
                CCStaticTextField text = (CCStaticTextField) getChild(CHILD_REFRESH_SCRIPT);
                text.setEscape(false);
                text.setValue("setCurrentCategory('" + getEditorModel().getSelectedCategory() + "');refreshNavigationTree();");            
            } 
        }
    }
    
    public boolean beginHeaderDisplay(ChildDisplayEvent event) { 
        super.endDisplay(event); 

        // Reset state when displaying this page for the first time; 
        // otherwise, state will persist until cookies expire. 
        if (mFirstDisplay == null) {
            mFirstDisplay = (String) getRequestContext().getRequest().getSession().getAttribute(FIRST_DISPLAY) ;
        }
        if (mFirstDisplay == null) {
            mFirstDisplay = "true";
        }
        if (mFirstDisplay.equals("true")) {
            CCHtmlHeaderTag tag = (CCHtmlHeaderTag) event.getSource();
            tag.resetFocus(true); 
            tag.resetScroll(true); 
        }
        mFirstDisplay = "false";
        getRequestContext().getRequest().getSession().setAttribute(FIRST_DISPLAY, "false");
        return true; 
    }
    
    public boolean beginXmlHandlerTextDisplay(ChildDisplayEvent event) {
        updateChooserList();
        updateXmlHandlers();
        return true;
    }   
        
    public void handleSaveButtonRequest(RequestInvocationEvent event)
            throws ServletException, IOException {
        
        try {
            String disabledValues = getDisplayFieldStringValue(CHILD_DISABLED_FIELDS_VALUES);
	    getTemplateModel().setValue(SectionModel.DISABLED_FIELDS_VALUES, disabledValues) ;
            getTemplateModel().storeChanges();
            String selectedCat = (String)getRequestContext().getRequest().getSession().getAttribute(ProfileWindowSettingsTreeViewBean.AFTER_SAVE_CATEGORY);
            if (selectedCat != null) {
                getEditorModel().setSelectedCategory(selectedCat);
                getTemplateModel().setRequestContext(getRequestContext());
                getRequestContext().getRequest().getSession().removeAttribute(ProfileWindowSettingsTreeViewBean.AFTER_SAVE_CATEGORY);
         //       mFirstDisplay = "true"; 
         //       setPageSessionAttribute(FIRST_DISPLAY, "true"); 
            }
        } catch (SPIException ex) {
            getEditorModel().setErrorMessage(Toolbox2.getI18n("APOC.profile.error.storing"), ex);
        } 
        forwardTo();
    }
    
    public void handleCloseButtonRequest(RequestInvocationEvent event)
                throws ServletException, IOException {
        CCStaticTextField child = (CCStaticTextField)getChild(CHILD_CLOSE_WINDOW_SCRIPT);
        child.setValue("closeEditorAndRefresh();");

        try {
            mapRequestParameters(getRequestContext().getRequest());
            handleSaveButtonRequest(event);
        } catch (ModelControlException ex) {
            CCDebug.trace3(ex.toString());
        } 
    }  
    
     public boolean beginSaveButtonDisplay(ChildDisplayEvent event) 
            throws ModelControlException {        
        // do not show the save button for read-only profiles
        return !getEditorModel().isReadOnlyProfile();
    }
    
    public boolean beginAlertAreaDisplay(ChildDisplayEvent event) {
        if (getEditorModel().hasAlert()) {
            if (!getEditorModel().getAlertType().equals(CCAlert.TYPE_ERROR)) {
                CCAlertInline child = (CCAlertInline) getChild(CHILD_ALERT);
                child.setType(getEditorModel().getAlertType());
                child.setSummary(getEditorModel().getAlertMessage());
                child.setDetail(getEditorModel().getAlertDetails());
                getEditorModel().setErrorMessage(null, "");
                return true;
            }
        } 
        return false;
    }
    
    public boolean beginAlertJavascriptDisplay(ChildDisplayEvent event) {
        if (getEditorModel().hasAlert()) {
            if (getEditorModel().getAlertType().equals(CCAlert.TYPE_ERROR)) {
                CCStaticTextField child = (CCStaticTextField) getChild(CHILD_ALERT_JS);
                StringBuffer buffer = new StringBuffer("alert('");
                buffer.append(getEditorModel().getAlertMessage());
                buffer.append("\\n");
                buffer.append(getEditorModel().getAlertDetails());
                buffer.append("');");
                child.setValue(buffer.toString());
                getEditorModel().setErrorMessage(null, "");
                return true;
            }
        }
        return false;
    }
    
    public void handleCategoryHrefRequest(RequestInvocationEvent event)
            throws ServletException, IOException {
        // the user selected a different category within the table - thus 
        // trigger a refresh of the navigation tree and the navigation table
        CCStaticTextField text = (CCStaticTextField) getChild(CHILD_REFRESH_SCRIPT);
        text.setEscape(false);
        text.setValue("setCurrentCategory('" + getEditorModel().getSelectedCategory() + "');refreshNavigationTree();");
        getEditorModel().setAnchor("");
        getRequestContext().getRequest().getSession().setAttribute(FIRST_DISPLAY, "true");
        forwardTo();
    }
    
    protected CCPageTitleModel getPageTitleModel() {
        if (mTitleModel == null) {
            mTitleModel = new CCPageTitleModel(
                RequestManager.getRequestContext().getServletContext(), 
                "/jsp/settings/ContentPageTitle.xml");
        }
        return mTitleModel;
    }
    
    protected ProfileWindowSettingsCategoriesTableModel getCategoriesModel() {
        if (mCategoriesModel == null) {
            mCategoriesModel = new ProfileWindowSettingsCategoriesTableModel();
        }
        return mCategoriesModel;
    }
    
    protected ProfileWindowModel getEditorModel() {
        if (mEditorModel == null) {
            mEditorModel = (ProfileWindowModel) getModel(ProfileWindowModel.class);
        }
        return mEditorModel;
    }
    
    protected SheetModel getTemplateModel() {
        return (SheetModel) getModel(SheetModel.class);
    }

    private void updateChooserList() {
        ArrayList ids = getTemplateModel().getChooserIds() ;
        
        if(ids == null || ids.size() == 0) {
            return;
        }
        
        String protectedChooserValues = "";
        String protectedChooserTypes = "";
        for(int i = 0; i < ids.size(); i++) {
            String id = (String)ids.get(i);
            ArrayList values = getTemplateModel().getChooserValues(id);
            String codedId = new Integer(id.hashCode()).toString();
            for(int j = 0; j < values.size(); j++) {
                String value = (String)values.get(j);
                protectedChooserValues = protectedChooserValues + value + ";";               
                protectedChooserTypes = protectedChooserTypes + codedId + ";";
            }
            ChooserHelper chooserHelper = new ChooserHelper(id);
            chooserHelper.setSystemElements(values);
        }
        CCHiddenField hiddenTypes = (CCHiddenField) getChild(CHILD_PROTECTED_CHOOSERS_TYPE) ;
        hiddenTypes.setValue(protectedChooserTypes);

        CCHiddenField hiddenValues = (CCHiddenField) getChild(CHILD_PROTECTED_CHOOSERS) ;
        hiddenValues.setValue(protectedChooserValues);
        
        getTemplateModel().resetChoosers() ;

    }    

    private void updateXmlHandlers() {
        ArrayList handlers = getTemplateModel().getXmlHandlers() ;

	if(handlers == null || handlers.size() == 0) {
            return;
        }
	
        
	StringBuffer buffer = new StringBuffer() ;
	for(int i = 0; i < handlers.size(); i++) {
	    buffer.append((String)handlers.get(i)) ;
 	}
	CCStaticTextField handlerText = (CCStaticTextField) getChild(CHILD_HANDLER_TEXT) ;
	handlerText.setEscape(false) ;
	handlerText.setValue((String)buffer.toString()) ;
	getTemplateModel().resetXmlHandlers() ;
    }
}    