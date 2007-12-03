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

import com.iplanet.jato.view.html.StaticTextField;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.ContainerView;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.html.OptionList;
import com.iplanet.jato.view.html.Option;

import com.sun.web.ui.model.CCPropertySheetModel;
import com.sun.web.ui.view.masthead.CCSecondaryMasthead;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.html.CCDropDownMenu;
import com.sun.web.ui.view.html.CCSelectableList;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.view.propertysheet.CCPropertySheet;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.taglib.header.CCHtmlHeaderTag;

import com.sun.apoc.manager.Toolbox2;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Vector;
import java.util.ArrayList;

import com.sun.apoc.templates.parsing.TemplateProperty ;
import com.sun.apoc.templates.parsing.TemplatePropertyConstraint;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.sun.apoc.manager.settings.SectionModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;


public class EditListViewBean extends ViewBeanBase {
    public static final String PAGE_NAME = "EditList";
    public static final String DEFAULT_DISPLAY_URL = "/jsp/EditList.jsp";
    public static final String CHILD_FORM = "ChooserForm";
    public static final String CHILD_MASTHEAD = "SecondaryMH";
    public static final String CHILD_EDITLIST_TITLE = "EditListTitle";
    public static final String CHILD_WINDOW_TITLE = "WindowTitle";
    public static final String CHILD_LIST = "List";
    public static final String CHILD_ADDREMOVE_FIELD = "AddTextField";
    public static final String CHILD_ADDBUTTON = "AddButton";
    public static final String CHILD_REMOVEBUTTON = "RemoveButton";
    public static final String CHILD_STATICTEXT = "StaticText"; 
    public static final String CHILD_HIDDEN_ELEMENTS = "HiddenElements";    
    public static final String CHILD_HIDDEN_STATES = "HiddenStates";    
    public static final String CHILD_HIDDEN_CHOOSER_DATAPATH = "HiddenChooserDataPath";   
    public static final String CHILD_PROTECTED_ALERT = "ProtectedAlert";    
    public static final String CHILD_SELECTED_ALERT = "SelectedAlert";    
    public static final String CHILD_LIST_WIDTH_MAINTAINER = "ListWidthMaintainer";    
    public static final String CHILD_SYSTEM_ADDED_FLAG = "SystemAddedFlag";    
    public static final String CHILD_UNDEFINED_VALUE = "UndefinedValue";    

    
    private CCPageTitleModel m_titleModel = null;
    private ChooserHelper m_chooserHelper = null ;
    private String m_chooserPath = null ;
    private String m_chooserLabel = null ;   
    private String m_localizedChooserLabel = null;
    private CCI18N m_I18n;

    
    public EditListViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        setUpVariables() ;
        m_titleModel = new CCPageTitleModel(RequestManager.getRequestContext().getServletContext(),
                "/jsp/EditListTitle.xml");
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        registerChildren();
    }
    
 
    protected void registerChildren() {
        registerChild(CHILD_MASTHEAD, CCSecondaryMasthead.class);
        registerChild(CHILD_WINDOW_TITLE, CCStaticTextField.class);
        registerChild(CHILD_EDITLIST_TITLE, CCPageTitle.class);
        registerChild(CHILD_FORM, CCForm.class);
        registerChild(CHILD_LIST, CCSelectableList.class);
        registerChild(CHILD_ADDREMOVE_FIELD, CCTextField.class);
        registerChild(CHILD_ADDBUTTON, CCButton.class);
        registerChild(CHILD_REMOVEBUTTON, CCButton.class);
        registerChild(CHILD_HIDDEN_ELEMENTS, CCHiddenField.class) ;
        registerChild(CHILD_HIDDEN_STATES, CCHiddenField.class) ;
        registerChild(CHILD_HIDDEN_CHOOSER_DATAPATH, CCHiddenField.class) ;
        registerChild(CHILD_STATICTEXT, CCStaticTextField.class);
        registerChild(CHILD_PROTECTED_ALERT, CCStaticTextField.class);
        registerChild(CHILD_SELECTED_ALERT, CCStaticTextField.class);
        registerChild(CHILD_LIST_WIDTH_MAINTAINER, CCStaticTextField.class);
        registerChild(CHILD_SYSTEM_ADDED_FLAG, CCStaticTextField.class);
        registerChild(CHILD_UNDEFINED_VALUE, CCStaticTextField.class);
    }

    protected View createChild(String name) {
        if (name.equals(CHILD_MASTHEAD)) {
            CCSecondaryMasthead child   = new CCSecondaryMasthead(this,
                                                CHILD_MASTHEAD);
            String sSubProductName = m_I18n.getMessage("APOC.masthead.altText");
        
            child.setAlt(sSubProductName);
            child.setHeight("21");
            child.setWidth("355");
            child.setSrc("/apoc/images/popuptitle.gif");
            return child;
        }
        else if (name.equals(CHILD_WINDOW_TITLE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, getChooserLabel());
            return child;
        }        
        else if (name.equals(CHILD_EDITLIST_TITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_titleModel, name);
            m_titleModel.setPageTitleText(getChooserLabel()) ;            
            return child;
        }
        else if (name.equals(CHILD_FORM)) {
            View child = new CCForm(this, name);
            return child;
        }
        else if (name.equals(CHILD_ADDREMOVE_FIELD)) { 
            CCTextField child =  new CCTextField(this, name, null); 
            return child; 
        }         
         else if (name.equals(CHILD_ADDBUTTON)) { 
            CCButton child =  new CCButton(this, name, null); 
            return child; 
        }         
        else if (name.equals(CHILD_REMOVEBUTTON)) { 
            CCButton child =  new CCButton(this, name, null); 
            return child; 
        }
        else if (name.equals(CHILD_STATICTEXT)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_HIDDEN_ELEMENTS)) {
            CCHiddenField child = new CCHiddenField(this, name, "");
            return child;
        }
        else if (name.equals(CHILD_HIDDEN_STATES)) {
            CCHiddenField child = new CCHiddenField(this, name, "");
            return child;
        }
        else if (name.equals(CHILD_HIDDEN_CHOOSER_DATAPATH)) {
            CCHiddenField child = new CCHiddenField(this, name, "");
            return child;
        }        
        else if (name.equals(CHILD_PROTECTED_ALERT)) {
            String protectedAlert = m_I18n.getMessage("APOC.chooser.protected.alert");
            CCStaticTextField child =  new CCStaticTextField(this, name, protectedAlert);
            return child;
        }
        else if (name.equals(CHILD_SELECTED_ALERT)) {
            String selectedAlert = m_I18n.getMessage("APOC.chooser.selected.alert");
            CCStaticTextField child =  new CCStaticTextField(this, name, selectedAlert);
            return child;
        }
        else if (name.equals(CHILD_LIST_WIDTH_MAINTAINER)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, ChooserHelper.LIST_WIDTH_STRING);
            return child;
        }
        else if (name.equals(CHILD_SYSTEM_ADDED_FLAG)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, ChooserHelper.SYSTEM_ADDED_FLAG);
            return child;
        }
        else if (name.equals(CHILD_UNDEFINED_VALUE)) {
            String localizedNotSet = SectionModel.getUndefinedValue();
            CCStaticTextField child =  new CCStaticTextField(this, name, localizedNotSet);
            return child;
        }
        else if (name.equals(CHILD_LIST)) {
            CCSelectableList child = new CCSelectableList(this, name, (Object)null);
            child.setOptions(createOptionList()) ;
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

    
    public OptionList createOptionList() {
        OptionList options = new OptionList() ;
        ArrayList elements = m_chooserHelper.getElementsList();
        ArrayList states = m_chooserHelper.getStatesList();
        for(int i = 0; i < elements.size(); i++) {
            String element = (String)elements.get(i);                
            String state = (String)states.get(i);
            if (state.equals("true")) {
                options.add(element + ChooserHelper.SYSTEM_ADDED_FLAG, element + ChooserHelper.SYSTEM_ADDED_FLAG) ;
            } else {
                options.add(element, element) ;
            }
        }	    
        //Always add the constant list width maintainer string as the last element in the list
        if(!(options.getValue(options.size()-1).equals(ChooserHelper.LIST_WIDTH_STRING))){
            options.add(ChooserHelper.LIST_WIDTH_STRING, ChooserHelper.LIST_WIDTH_STRING) ;
        }         

        return options ;
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        // Set the hidden field values from the values in the request
        String chooserDataPath = Toolbox2.getProperty(m_chooserPath).getChooserPath() ;
        CCHiddenField child = (CCHiddenField) getChild(CHILD_HIDDEN_CHOOSER_DATAPATH);
        child.setValue(chooserDataPath) ;
    }    

    
    public void handleOKButtonRequest(RequestInvocationEvent event)
                throws ServletException, IOException {
        
        String elements = getDisplayFieldStringValue(CHILD_HIDDEN_ELEMENTS);
        String states = getDisplayFieldStringValue(CHILD_HIDDEN_STATES);
        String chooserDataPath = getDisplayFieldStringValue(CHILD_HIDDEN_CHOOSER_DATAPATH) ;
        if(elements != null) {
                m_chooserHelper.setElementsList(elements, ChooserHelper.LIST_SEPARATOR);
                m_chooserHelper.setStatesList(states, ChooserHelper.LIST_SEPARATOR);
                m_chooserHelper.saveChanges();
        }
        
        PrintWriter pw = getRequestContext().getResponse().getWriter() ;
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html><header>") ;
        buffer.append("<script type=\"text/javascript\">") ;
        buffer.append("top.window.close();</script>") ;
        buffer.append("</header><body></body></html>") ;
        pw.write(buffer.toString()) ;
        pw.flush() ;
    }    

    
   
    public boolean beginRestoreDefaultsButtonDisplay(ChildDisplayEvent event) 
            throws ModelControlException {
        String chooserDefaults = m_chooserHelper.getDefaultElementsStringList() ;
       
        CCButton child = (CCButton) getChild("RestoreDefaultsButton");
        StringBuffer buffer = new StringBuffer();
        buffer.append("onClick=\"javascript: ")
              .append("var chooserDefaults = '" + chooserDefaults + "';")
              .append("updateAfterRestore(chooserDefaults);")
              .append("return false;\"");
        child.setExtraHtml(buffer.toString());

        return true ;
    }
    

    protected String getChooserLabel() {
        if (m_localizedChooserLabel == null) {
            if (m_chooserLabel == "" || m_chooserLabel == "null") {
                m_localizedChooserLabel = "APOC.page.chooser.list.editor" ;
            } else {
                ResourceRepository resources = ResourceRepository.getDefaultRepository();
                String localizedLabel = resources.getValidMessage(
                                            m_chooserLabel, 
                                            Toolbox2.getProperty(m_chooserPath).getResourceBundle(), 
                                            RequestManager.getRequest());
                // If the string isn't stored in the resources for this category then try the APOC manager resources
                if (localizedLabel == null) {
                    localizedLabel = m_I18n.getMessage(m_chooserLabel) ;
                }
                // If the string isn't stored in either resources then use the original string
                if (localizedLabel == null) {
                    localizedLabel = m_chooserLabel ;
                }           
                m_localizedChooserLabel = localizedLabel ;
            }
        }
        return m_localizedChooserLabel;
    }   

    
    private void setUpVariables() {
        m_chooserPath = Toolbox2.getParameter("ChooserPath");
        m_chooserLabel = Toolbox2.getParameter("ChooserLabel");
        m_chooserHelper = (ChooserHelper)RequestManager.getRequest().getSession(false).getAttribute("ChooserHelper");
        if(m_chooserPath != null && m_chooserPath.length() != 0) {
            m_chooserHelper = new ChooserHelper(Toolbox2.decode(m_chooserPath));       
        }     
        RequestManager.getRequest().getSession(false).setAttribute("ChooserHelper", m_chooserHelper);       
    }

}
