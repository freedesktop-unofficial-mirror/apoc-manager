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
import java.io.PrintWriter;
import javax.servlet.ServletException;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.settings.SectionModel;

import com.sun.web.ui.view.masthead.CCSecondaryMasthead;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.common.CCI18N;
import com.sun.apoc.manager.settings.SpinProperty;

public class EditColorViewBean extends ViewBeanBase {

    // The "logical" name for this page.
    public static final String PAGE_NAME =
        "EditColor";

    // The URL that points to the JSP which uses this ViewBean
    public static final String DEFAULT_DISPLAY_URL =
	    "/jsp/EditColor.jsp";

    public static final String CHILD_MASTHEAD =
        "SecondaryMH";
    public static final String CHILD_PAGETITLE =
        "PageTitle";
    public static final String CHILD_FORM =
        "EditColorForm";
    public static final String CHILD_INTROTEXT =
        "IntroText";
    public static final String CHILD_PALETTE_HEADING =
        "PaletteHeading";
    public static final String CHILD_COLOR_HEADING =
        "ColorHeading";
    public static final String CHILD_ORIGINAL_HEADING =
        "OriginalHeading";
    public static final String CHILD_WHEEL_HEADING =
        "WheelHeading";
    public static final String CHILD_ALT_WHEEL =
        "AltWheel";
    public static final String CHILD_SUMMARY_SLIDER =
        "SummarySlider";
    public static final String CHILD_PALETTEROW_TILEDVIEW =
        "PaletteRowTiledView";
    public static final String CHILD_RED =
        "Red";
    public static final String CHILD_GREEN =
        "Green";
    public static final String CHILD_BLUE =
        "Blue";
    public static final String CHILD_HUE =
        "Hue";
    public static final String CHILD_SAT =
        "Sat";
    public static final String CHILD_LUM =
        "Lum";
    public static final String CHILD_NAME_LABEL =
        "NameLabel";
    public static final String CHILD_HTML_LABEL =
        "HtmlLabel";
    public static final String CHILD_NAME_VALUE =
        "NameValue";
    public static final String CHILD_HTML_VALUE =
        "HtmlValue";
    public static final String CHILD_RESTORE_BUTTON =
        "RestoreButton";
    public static final String CHILD_CONFIRM_RESTORE =
        "RestoreConfirm";
    public static final String CHILD_LOCALIZED_NONAME =
        "LocalizedNoName";
    public static final String CHILD_ADD_BUTTON =
        "AddButton";
    public static final String CHILD_REPLACE_BUTTON =
        "ReplaceButton";
    public static final String CHILD_REPLACE_ALERT =
        "ReplaceAlert";
    public static final String CHILD_ADD_ALERT =
        "AddAlert";
    public static final String CHILD_REMOVE_BUTTON =
        "RemoveButton";
    public static final String CHILD_REMOVE_ALERT =
        "RemoveAlert";
    public static final String CHILD_NAME_ALERT =
        "NameAlert";    
    public static final String CHILD_RESTOREORIGCOLOR_BUTTON =
        "RestoreOrigColorButton";
    public static final String CHILD_BG_ORG_COLOR =
        "BgOrgColor";
    public static final String CHILD_ALT_BG_ORG_COLOR =
        "AltBgOrgColor";
    public static final String CHILD_BG_COLOR =
        "BgColor";
    public static final String CHILD_ALT_BG_COLOR =
        "AltBgColor";
    public static final String CHILD_HIDDEN_VALUES =
        "HiddenValues";
    public static final String CHILD_HIDDEN_NAMES =
        "HiddenNames";
    
    private CCPageTitleModel m_pagetitleModel = null;
    private CCI18N m_I18n;
    private static String m_OriginalColor;
    private static String m_OriginalColorName;    
    private String m_OrgString;
    private String m_TemplateSelection;
    
    public EditColorViewBean() {
    	super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        setUpVariables();
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_MASTHEAD, CCSecondaryMasthead.class);
        registerChild(CHILD_PAGETITLE, CCPageTitle.class);
        registerChild(CHILD_FORM, CCForm.class);
        registerChild(CHILD_INTROTEXT, CCStaticTextField.class);
        registerChild(CHILD_PALETTE_HEADING, CCLabel.class);
        registerChild(CHILD_COLOR_HEADING, CCLabel.class);
        registerChild(CHILD_ORIGINAL_HEADING, CCLabel.class);
        registerChild(CHILD_WHEEL_HEADING, CCLabel.class);
        registerChild(CHILD_ALT_WHEEL, CCStaticTextField.class);
        registerChild(CHILD_SUMMARY_SLIDER, CCStaticTextField.class);
        registerChild(CHILD_PALETTEROW_TILEDVIEW, PaletteRowTiledView.class);
        registerChild(CHILD_RED, SpinProperty.class);
        registerChild(CHILD_GREEN, SpinProperty.class);
        registerChild(CHILD_BLUE, SpinProperty.class);
        registerChild(CHILD_HUE, SpinProperty.class);
        registerChild(CHILD_SAT, SpinProperty.class);
        registerChild(CHILD_LUM, SpinProperty.class);
        registerChild(CHILD_NAME_LABEL, CCLabel.class);
        registerChild(CHILD_HTML_LABEL, CCLabel.class);
        registerChild(CHILD_NAME_VALUE, CCTextField.class);
        registerChild(CHILD_HTML_VALUE, CCTextField.class);        
        registerChild(CHILD_RESTORE_BUTTON, CCButton.class);
        registerChild(CHILD_RESTOREORIGCOLOR_BUTTON, CCButton.class);
        registerChild(CHILD_CONFIRM_RESTORE, CCStaticTextField.class);
        registerChild(CHILD_LOCALIZED_NONAME, CCStaticTextField.class);
        registerChild(CHILD_ADD_BUTTON, CCButton.class);
        registerChild(CHILD_REPLACE_BUTTON, CCButton.class);
        registerChild(CHILD_REPLACE_ALERT, CCStaticTextField.class);
        registerChild(CHILD_ADD_ALERT, CCStaticTextField.class);
        registerChild(CHILD_REMOVE_BUTTON, CCButton.class);
        registerChild(CHILD_REMOVE_ALERT, CCStaticTextField.class);
        registerChild(CHILD_NAME_ALERT, CCStaticTextField.class);        
        registerChild(CHILD_BG_ORG_COLOR, CCStaticTextField.class);
        registerChild(CHILD_ALT_BG_ORG_COLOR, CCStaticTextField.class);
        registerChild(CHILD_BG_COLOR, CCStaticTextField.class);
        registerChild(CHILD_ALT_BG_COLOR, CCStaticTextField.class);
        registerChild(CHILD_HIDDEN_VALUES, CCHiddenField.class);
        registerChild(CHILD_HIDDEN_NAMES, CCHiddenField.class);
        m_pagetitleModel.registerChildren(this);
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
            
        } else if (name.equals(CHILD_PAGETITLE)) {
	    CCPageTitle child = new CCPageTitle(this, m_pagetitleModel, CHILD_PAGETITLE);
            return child;         
            
        } else if (name.equals(CHILD_FORM)) {
	    CCForm child = new CCForm(this, CHILD_PAGETITLE);
            return child;         
            
        } else if (name.equals(CHILD_INTROTEXT)) {
            String introText = m_I18n.getMessage("APOC.setcolor.introtext");
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_INTROTEXT, introText);
            return child;
            
        } else if (name.equals(CHILD_PALETTE_HEADING)) {
            String paletteHead = m_I18n.getMessage("APOC.setcolor.palette.heading");
            CCLabel child = new CCLabel(this,
                CHILD_PALETTE_HEADING, paletteHead);
            child.setEscape(false);
            return child;
            
        } else if (name.equals(CHILD_COLOR_HEADING)) {
            String colorHead = m_I18n.getMessage("APOC.setcolor.color.heading");
            CCLabel child = new CCLabel(this, CHILD_COLOR_HEADING, colorHead);
            child.setEscape(false);
            return child;
              
        } else if (name.equals(CHILD_ORIGINAL_HEADING)) {
            String orgHead = m_I18n.getMessage("APOC.setcolor.original.heading");
            CCLabel child = new CCLabel(this, CHILD_ORIGINAL_HEADING, orgHead);
            child.setEscape(false);
            return child;
              
        } else if (name.equals(CHILD_WHEEL_HEADING)) {
            String wheelHead = m_I18n.getMessage("APOC.setcolor.wheel.heading");
            CCLabel child = new CCLabel(this, CHILD_WHEEL_HEADING, wheelHead);
            child.setEscape(false);
            return child;
            
        } else if (name.equals(CHILD_ALT_WHEEL)) {
            String alt = m_I18n.getMessage("APOC.setcolor.wheel.alt");
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_ALT_WHEEL, alt);
            return child;
            
        } else if (name.equals(CHILD_SUMMARY_SLIDER)) {
            String summary = m_I18n.getMessage("APOC.setcolor.slider.summary");
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_SUMMARY_SLIDER, summary);
            return child;
                    
        } else if (name.equals(CHILD_PALETTEROW_TILEDVIEW)) {
            PaletteRowTiledView child = new PaletteRowTiledView(this,
                                                CHILD_PALETTEROW_TILEDVIEW);
            return child;
              
        } else if (name.equals(CHILD_RED)) {
            SpinProperty child = new SpinProperty(this, CHILD_RED, "APOC.setcolor.red.heading");

            return child;
            
        } else if (name.equals(CHILD_GREEN)) {
            SpinProperty child = new SpinProperty(this, CHILD_GREEN, "APOC.setcolor.green.heading");
            return child;
            
        } else if (name.equals(CHILD_BLUE)) {
            SpinProperty child = new SpinProperty(this, CHILD_BLUE, "APOC.setcolor.blue.heading");
            return child;
              
        } else if (name.equals(CHILD_HUE)) {
            SpinProperty child = new SpinProperty(this, CHILD_HUE, "APOC.setcolor.hue.heading");
            return child;
              
        } else if (name.equals(CHILD_SAT)) {
            SpinProperty child = new SpinProperty(this, CHILD_SAT, "APOC.setcolor.sat.heading");
            return child;
              
        } else if (name.equals(CHILD_LUM)) {
            SpinProperty child = new SpinProperty(this, CHILD_LUM, "APOC.setcolor.lum.heading");
            return child;
              
        } else if (name.equals(CHILD_NAME_LABEL)) {
            String nameLabel = m_I18n.getMessage("APOC.navigation.name.label");
            CCLabel child = new CCLabel(this, CHILD_NAME_LABEL, nameLabel);
            return child;
              
        } else if (name.equals(CHILD_HTML_LABEL)) {
            String htmlLabel = m_I18n.getMessage("APOC.setcolor.html.heading");
            CCLabel child = new CCLabel(this, CHILD_HTML_LABEL, htmlLabel);
            child.setEscape(false);
            return child;
              
        } else if (name.equals(CHILD_NAME_VALUE)) {
            CCTextField child = new CCTextField(this, CHILD_NAME_VALUE, m_OriginalColorName);
            return child;
            
        } else if (name.equals(CHILD_HTML_VALUE)) {
            CCTextField child = new CCTextField(this, CHILD_HTML_VALUE, m_OriginalColor);
            return child;
                      
        } else if (name.equals(CHILD_RESTORE_BUTTON)) {
            String butName = m_I18n.getMessage("APOC.setcolor.restore.button");
            CCButton child = new CCButton(this, CHILD_RESTORE_BUTTON, butName);
            return child;
              
        } else if (name.equals(CHILD_CONFIRM_RESTORE)) {
            String confirmText = m_I18n.getMessage("APOC.setcolor.restore.confirm");
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_CONFIRM_RESTORE, confirmText);
            return child;
            
        } else if (name.equals(CHILD_LOCALIZED_NONAME)) {
            String localizedNoName = m_I18n.getMessage("APOC.setcolor.noname");
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_LOCALIZED_NONAME, localizedNoName);
            return child;
            
        } else if (name.equals(CHILD_ADD_BUTTON)) {
            String butName = m_I18n.getMessage("APOC.setcolor.add.button");
            CCButton child = new CCButton(this, CHILD_ADD_BUTTON, butName);
            return child;
              
        } else if (name.equals(CHILD_REPLACE_BUTTON)) {
            String butName = m_I18n.getMessage("APOC.setcolor.replace.button");
            CCButton child = new CCButton(this, CHILD_REPLACE_BUTTON, butName);
            return child;
              
        } else if (name.equals(CHILD_REPLACE_ALERT)) {
            String alertName = m_I18n.getMessage("APOC.setcolor.replace.alert");
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_REPLACE_ALERT, alertName);
            return child;
            
        } else if (name.equals(CHILD_ADD_ALERT)) {
            String alertName = m_I18n.getMessage("APOC.setcolor.add.alert");
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_ADD_ALERT, alertName);
            return child;
                          
        } else if (name.equals(CHILD_REMOVE_BUTTON)) {
            String butName = m_I18n.getMessage("APOC.setcolor.remove.button");
            CCButton child = new CCButton(this, CHILD_REMOVE_BUTTON, butName);
            return child;
            
        } else if (name.equals(CHILD_REMOVE_ALERT)) {
            String alertName = m_I18n.getMessage("APOC.setcolor.remove.alert");
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_REMOVE_ALERT, alertName);
            return child;
            
        } else if (name.equals(CHILD_NAME_ALERT)) {
            String alertName = m_I18n.getMessage("APOC.setcolor.name.alert");
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_NAME_ALERT, alertName);
            return child;                          
        
        } else if (name.equals(CHILD_RESTOREORIGCOLOR_BUTTON)) {
            String butName = m_I18n.getMessage("APOC.setcolor.restoreorigcolor.button");
            CCButton child = new CCButton(this, CHILD_RESTOREORIGCOLOR_BUTTON, butName);
            return child;
            
        } else if (name.equals(CHILD_BG_ORG_COLOR)) {
            StringBuffer col = new StringBuffer("#");
            col.append(m_OriginalColor);
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_BG_ORG_COLOR, col);
            return child;
            
        } else if (name.equals(CHILD_ALT_BG_ORG_COLOR)) {
            StringBuffer col = new StringBuffer("#");
            col.append(m_OriginalColor);
            Object[] args = {col.toString()};
            String alt = m_I18n.getMessage("APOC.setcolor.original.alt", args);
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_ALT_BG_ORG_COLOR, alt);
            return child;
            
        } else if (name.equals(CHILD_BG_COLOR)) {
            StringBuffer col = new StringBuffer("#");
            col.append(m_OriginalColor);
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_BG_COLOR, col);
            return child;
            
        } else if (name.equals(CHILD_ALT_BG_COLOR)) {
            StringBuffer col = new StringBuffer("#");
            col.append(m_OriginalColor);
            Object[] args = {col.toString()};
            String alt = m_I18n.getMessage("APOC.setcolor.color.alt", args);
            CCStaticTextField child = new CCStaticTextField(this,
                CHILD_ALT_BG_COLOR, alt);
            return child;
            
        } else if (name.equals(CHILD_HIDDEN_VALUES)) {
            CCHiddenField child = new CCHiddenField(this,
                CHILD_HIDDEN_VALUES, "");
            return child;
            
        } else if (name.equals(CHILD_HIDDEN_NAMES)) {
            CCHiddenField child = new CCHiddenField(this,
                CHILD_HIDDEN_NAMES, "");
            return child;
                                            
        } else if (m_pagetitleModel.isChildSupported(name)) {
            return m_pagetitleModel.createChild(this, name);
                    
        } else {
            throw new IllegalArgumentException( 
                "Invalid child name [" + name + "]");
        }
    }

    public boolean beginCloseButtonDisplay(ChildDisplayEvent event) 
            throws ModelControlException {
        CCButton child = (CCButton) getChild("CloseButton");
        StringBuffer buffer = new StringBuffer();
        buffer.append(" onClick=\"")
                .append("top.close();\"");
        child.setExtraHtml(buffer.toString());
        return true;
    }

    
    public void handleSetColorButtonRequest(RequestInvocationEvent event) 
            throws ServletException, IOException{
        String paletteNames = getDisplayFieldStringValue(CHILD_HIDDEN_NAMES);
        String paletteValues = getDisplayFieldStringValue(CHILD_HIDDEN_VALUES);
        PaletteRowTiledView rowView = (PaletteRowTiledView) 
            getChild(CHILD_PALETTEROW_TILEDVIEW);
        PaletteTDTiledView tdView = (PaletteTDTiledView)
            rowView.getChild(PaletteRowTiledView.CHILD_TD_TILEDVIEW);
        PaletteModel model = (PaletteModel) tdView.getPrimaryModel();
        model.updateColors(paletteNames, paletteValues);
        model.storeColorsInBackend();
        
        PrintWriter pw = getRequestContext().getResponse().getWriter() ;
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html><header>") ;
        buffer.append("<script type=\"text/javascript\">") ;
        buffer.append("top.window.close();</script>") ;
        buffer.append("</header><body></body></html>") ;
        pw.write(buffer.toString()) ;
        pw.flush() ;
    }
    
    private void setUpVariables() {
        // The resources
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        String localizedNoName = m_I18n.getMessage("APOC.setcolor.noname");
        String localizedNotSet = SectionModel.getUndefinedValue();
        // The original Color 
        String orgColorParam = Toolbox2.getParameter("OrgColor");
        if ((orgColorParam.equals("")) || (orgColorParam.equals(localizedNotSet))) {
            m_OriginalColor = "ffffff";
        } else {
            m_OriginalColor = orgColorParam;
        } 
        String orgColorName = Toolbox2.getParameter("Name");

        if ((orgColorName.equals("")) 
                    || (orgColorName.equals(localizedNotSet)) 
                            ||(orgColorName.equals(localizedNoName))) {
            m_OriginalColorName = "";
        } else {
            m_OriginalColorName = orgColorName;
        }
        
        // The pagetitle model
        m_pagetitleModel = createPageTitleModel();
    }
    
    private CCPageTitleModel createPageTitleModel() {
        CCPageTitleModel model = new CCPageTitleModel(
                        RequestManager.getRequestContext().getServletContext(),
                        "/jsp/EditColorPageTitle.xml");
        model.setValue("SetColorButton", "APOC.button.OK");
        model.setValue("CloseButton", "APOC.button.cancel");
        return model;
    }
    
}