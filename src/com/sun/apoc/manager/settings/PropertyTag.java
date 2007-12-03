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

package com.sun.apoc.manager.settings;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.Tag;

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.util.NonSyncStringBuffer;
import com.iplanet.jato.view.ContainerView;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.html.OptionList;
import com.sun.apoc.templates.parsing.TemplateProperty;
import com.sun.apoc.templates.parsing.TemplatePropertyConstraint;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.sun.web.ui.common.CCBodyContentImpl;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.common.CCJspWriterImpl;
import com.sun.web.ui.common.CCStyle;
import com.sun.web.ui.taglib.common.CCDisplayFieldTagBase;
import com.sun.web.ui.taglib.common.CCTagBase;
import com.sun.web.ui.taglib.help.CCHelpInlineTag;
import com.sun.web.ui.taglib.help.CCHelpTag;
import com.sun.web.ui.taglib.html.CCDropDownMenuTag;
import com.sun.web.ui.taglib.html.CCPasswordTag;
import com.sun.web.ui.taglib.html.CCRadioButtonTag;
import com.sun.web.ui.taglib.html.CCTextAreaTag;
import com.sun.web.ui.taglib.html.CCTextFieldTag;
import com.sun.web.ui.taglib.html.CCStaticTextFieldTag;
import com.sun.web.ui.taglib.html.CCButtonTag;
import com.sun.web.ui.view.html.CCDropDownMenu;
import com.sun.web.ui.view.html.CCPassword;
import com.sun.web.ui.view.html.CCRadioButton;
import com.sun.web.ui.view.html.CCTextArea;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.html.CCButton;

import com.sun.apoc.manager.ChooserHelper;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.Toolbox2;


public class PropertyTag extends CCTagBase {
    
    private static final int MIN_TEXT_LENGTH = 28;
    private static final int MIN_NUM_LENGTH = 4;
    private static final int MAX_LENGTH = 60;

    private CCI18N m_i18N = null;
    private String m_notSet = "";
    		    
    public PropertyTag() {
	    super();
    }
    
    public void reset() {
	    super.reset();
    }
    
    protected String getHTMLStringInternal(Tag parent, PageContext pageContext,
	        View view) throws JspException {
    	if (parent == null) {
    	    throw new IllegalArgumentException("parent cannot be null.");
    	} else if (pageContext == null) {
    	    throw new IllegalArgumentException("pageContext cannot be null.");
    	} else if (view == null) {
    	    throw new IllegalArgumentException("view cannot be null.");
    	}
        m_i18N = new CCI18N(RequestManager.getRequestContext(),
                            Constants.RES_BASE_NAME); 
        checkChildType(view, PropertyView.class);
    	PropertyView field = (PropertyView) view;
    	SectionModel model = (SectionModel) field.getModel();
    	if (model == null) {
    	    throw new IllegalArgumentException("Model cannot be null");
    	}
    	setParent(parent);
    	setPageContext(pageContext);
        
        m_notSet = SectionModel.getUndefinedValue();        
        NonSyncStringBuffer buffer = new NonSyncStringBuffer(DEFAULT_BUFFER_SIZE);
        TemplateProperty prop = (TemplateProperty) model.getValue(SectionModel.PROPERTY);
        boolean readonly = model.getSheetModel().getEditorModel().isReadOnlyProfile();
        
        if (prop.getVisualType().equals(TemplateProperty.STRING_LIST)) {
            renderStringList(buffer, field, model, prop, readonly);
            
        } else if (prop.getVisualType().equals(TemplateProperty.PASSWORD)) {
            renderPasswordField(buffer, field, model, prop, readonly);
            
        } else if (prop.getVisualType().equals(TemplateProperty.TEXTAREA)) {
            renderTextArea(buffer, field, model, prop, readonly);                                           
            
        } else if (prop.getVisualType().equals(TemplateProperty.CHECKBOX)) {
            renderCheckBox(buffer, field, model, prop, readonly);

        } else if (prop.getVisualType().equals(TemplateProperty.RADIOBUTTONS)) {
            renderRadioButtons(buffer, field, model, prop, readonly);

        } else if (prop.getVisualType().equals(TemplateProperty.COMBOBOX)) {
            renderDropDownMenu(buffer, field, model, prop, readonly);
            
        } else if (prop.getVisualType().equals(TemplateProperty.COLOR_CHOOSER)) {
            renderColorChooser(buffer, field, model, prop, readonly);
            
        } else if (prop.getVisualType().equals(TemplateProperty.CHOOSER)) {
            renderChooser(buffer, field, model, prop);
            
        } else {
            renderTextField(buffer, field, model, prop, readonly);
        }
        renderDescription(buffer, field, prop);

        return buffer.toString();
    }

    
    protected int getSize(int minSize, int defaultSize, int valueSize) {
        int size = 0;
        if (defaultSize < valueSize) {
            size = valueSize + 1;
        } else {
            size = defaultSize + 1;
        }
        if (size < minSize) {
            size = minSize; 
        } else if (size > MAX_LENGTH) {
            size = MAX_LENGTH;
        }
        return size;
    }
    
    protected void addCommonRenderParts(CCDisplayFieldTagBase tag, 
            View propView, TemplateProperty prop) {
        if (prop.getExtraHtml() != null) {
            StringBuffer extraHtml = new StringBuffer();
            if (tag.getExtraHtml() != null) {
                extraHtml.append(tag.getExtraHtml());
            }
            extraHtml.append(" ");
            extraHtml.append(prop.getExtraHtml());
            tag.setExtraHtml(extraHtml.toString());
        }
        StringBuffer buffer2 = new StringBuffer();
        buffer2.append("javascript:setTimeout(\"updateSaveStatus('");
        buffer2.append(propView.getQualifiedName());
        buffer2.append("');\",0);");
        buffer2.append("setTimeout(toggleOverwriteCheckbox(this), 0);");
        tag.setOnChange(buffer2.toString());
        tag.setOnKeyPress(buffer2.toString());
        if(prop.getXmlHandler() != null) {
            String handlerName = prop.getXmlHandler() ;
            tag.setOnChange(handlerName + "();" + buffer2.toString());
            tag.setOnKeyPress(handlerName + "();" + buffer2.toString());
        } 
    }
    

    protected void renderDescription(NonSyncStringBuffer buffer, PropertyView view, 
            TemplateProperty prop) throws JspException {
        StringBuffer descr = new StringBuffer();
        if (prop.getDescriptionId() != null) {
            ResourceRepository resources = ResourceRepository.getDefaultRepository();
            String descriptionString = resources.getMessage(
                                prop.getDescriptionId(), 
                                prop.getResourceBundle(), 
                                RequestManager.getRequest());
            descr.append(descriptionString);
            descr.append("<br>");
        }
        if ((prop.getVisualType().equals(TemplateProperty.TEXTAREA)) ||
            (prop.getVisualType().equals(TemplateProperty.TEXTFIELD))) {
            String defaultValue = prop.getDefaultValue();
            if ((defaultValue != null) && (defaultValue.length() > 0)) {
                defaultValue = defaultValue.replaceAll("<", "&#60;");
                defaultValue = defaultValue.replaceAll(">", "&#62;");
                if (descr.length() > 0) {
                    descr.append(" ");
                }
                descr.append(m_i18N.getMessage("APOC.profilewnd.settings.defaultLabel"));
                descr.append(" <a ");
                descr.append("class=\"HlpFldTxt\" href=\"javascript:;\" ");
                descr.append("onClick=\"setDefaultFieldValue('");
                descr.append(view.getQualifiedName());
                descr.append("','");
                descr.append(defaultValue);
                descr.append("');\">");
                descr.append(defaultValue);
                descr.append("</a>");
            }
        }
        if ((prop.getVisualType().equals(TemplateProperty.STRING_LIST))) {
            String defaultValue = prop.getDefaultValue();
                if (descr.length() > 0) {
                    descr.append(" ");
                }
                String separator = prop.getSeparator();
                descr.append("<a class=\"HlpFldTxt\" href='' onClick=\"javascript: var defaultValue ='")
                    .append(defaultValue)
                    .append("'; var separator = '")
                    .append(separator)
                    .append("'; setListToDefault('")
                    .append(view.getQualifiedName())
                    .append("', defaultValue, separator); return false;\">")
                    .append(m_i18N.getMessage("APOC.profilewnd.settings.setToDefaultLabel"))
                    .append("</a>");
        }
        if (descr.length() > 0) {    
            BodyContent bodyContent = new CCBodyContentImpl(
                                        new CCJspWriterImpl(null, 100, false));
            try {
                bodyContent.print(descr.toString());
            } catch (IOException e) {
                CCDebug.trace3(e.getMessage());
            }
            CCHelpInlineTag helpTag = new CCHelpInlineTag();
            helpTag.setBodyContent(bodyContent);
            helpTag.setType(CCHelpTag.TYPE_FIELD);
            buffer.append("<br>");
            buffer.append(helpTag.getHTMLString(getParent(), pageContext, view));
        }
    }

    
    protected void renderTextField(NonSyncStringBuffer buffer, PropertyView view, 
            SectionModel model, TemplateProperty prop, boolean readonly) 
            throws JspException {        
        if (readonly) {
            renderStaticText(buffer, view, model, prop);
        } else {
            CCTextFieldTag tag = new CCTextFieldTag();
            CCTextField textfield = new CCTextField((ContainerView) view.getParent(), model, view.getName(), null);
            int valueSize = 0;
            if (textfield.getValue() != null) {
                valueSize = ((String) textfield.getValue()).length();
            }
            int defaultSize = 0;
            if (prop.getDefaultValue() != null) {
                defaultSize = prop.getDefaultValue().length();
            }
            if ((prop.getDataType() != null) &&
                 (prop.getDataType().equals("xs:boolean") 
                 || prop.getDataType().equals("xs:short")
                 || prop.getDataType().equals("xs:int")
                 || prop.getDataType().equals("xs:long")
                 || prop.getDataType().equals("xs:double"))) {
                tag.setExtraHtml("size='" + getSize(MIN_NUM_LENGTH, defaultSize, valueSize) + "'");
            } else {
                tag.setExtraHtml("size='" + getSize(MIN_TEXT_LENGTH, defaultSize, valueSize) + "'");
            }
            tag.setOnFocus("select();"); 
            addCommonRenderParts(tag, view, prop);
            buffer.append(tag.getHTMLString(getParent(), pageContext, textfield));

            CCButtonTag tag2 = new CCButtonTag();
            tag2.setOnClick("resetField(this); return false;");
            String buttonLabel = this.m_i18N.getMessage("APOC.policies.actions.clear");
            CCButton button = new CCButton((ContainerView) view.getParent(), view.getName() + "clear", buttonLabel);
            buffer.append("&nbsp;");
            buffer.append(tag2.getHTMLString(getParent(), pageContext, button));
        }     
    }
    
    protected void renderPasswordField(NonSyncStringBuffer buffer, PropertyView view, 
            SectionModel model, TemplateProperty prop, boolean readonly) throws JspException {
        if (readonly) {
            renderStaticText(buffer, view, model, prop);
        } else {
            CCPasswordTag tag = new CCPasswordTag();
            CCPassword password = new CCPassword((ContainerView) view.getParent(), model, view.getName(), null);
            int valueSize = 0;
            if (password.getValue() != null) {
                valueSize = ((String) password.getValue()).length();
            }
            int defaultSize = 0;
            if (prop.getDefaultValue() != null) {
                defaultSize = prop.getDefaultValue().length();
            }
            tag.setOnFocus("select();");
            tag.setExtraHtml("size='" + getSize(MIN_TEXT_LENGTH, defaultSize, valueSize) + "'");
            addCommonRenderParts(tag, view, prop);
            buffer.append(tag.getHTMLString(getParent(), pageContext, password));
        }
    }

    
    protected void renderTextArea(NonSyncStringBuffer buffer, PropertyView view, 
            SectionModel model, TemplateProperty prop, boolean readonly) 
            throws JspException {
        CCTextAreaTag tag = new CCTextAreaTag();
        CCTextArea textarea = new CCTextArea((ContainerView) view.getParent(), model, 
                                   view.getName(), null);  
        int valueSize = MIN_TEXT_LENGTH;
        if (textarea.getValue() != null) {
            valueSize = ((String) textarea.getValue()).length();
        }
        int defaultSize = MIN_TEXT_LENGTH;
        if (prop.getDefaultValue() != null) {
            defaultSize = prop.getDefaultValue().length();
        }
        int cols = valueSize;
        if (defaultSize > valueSize) {
            cols = defaultSize;
        }
        int rows = cols / MAX_LENGTH;
        if (rows <= 1) {
            rows = 2;
        } else if (rows > 12) {
            rows = 12;
            cols = MAX_LENGTH;
        } else {
            cols = MAX_LENGTH;
        }
        tag.setOnFocus("select();");
        StringBuffer extraHtml = new StringBuffer("cols='" + cols + "' rows='" + rows + "' wrap='virtual'");
        if (readonly) {
            extraHtml.append(" readonly='readonly'");    
        }
        tag.setExtraHtml(extraHtml.toString());
        addCommonRenderParts(tag, view, prop);
        buffer.append(tag.getHTMLString(getParent(), pageContext, textarea)); 
    }

    protected void renderRadioButtons(NonSyncStringBuffer buffer, PropertyView view, 
            SectionModel model, TemplateProperty prop, boolean readonly) 
            throws JspException {
        if (readonly) {
            renderStaticText(buffer, view, model, prop);
        } else {
            OptionList options = new OptionList();
            options.add(m_notSet, m_notSet);
            if (prop.getConstraints() != null) {
                for(int i=0; i < prop.getConstraints().size(); i++) {
                    TemplatePropertyConstraint constraint = (TemplatePropertyConstraint) prop.getConstraints().elementAt(i);
                    String value = constraint.getValue();
                    String defaultValue = prop.getDefaultValue();
                    StringBuffer buffer2 = new StringBuffer();
                    if (constraint.getResourceId() != null) {
                        buffer2.append(constraint.getResourceId());
                    } else {
                        buffer2.append(prop.getResourceId());
                        buffer2.append(".");
                        buffer2.append(value);
                    }
                    ResourceRepository resources = ResourceRepository.getDefaultRepository();
                    StringBuffer descr = new StringBuffer(resources.getMessage(
                                    buffer2.toString(), 
                                    prop.getResourceBundle(), 
                                    RequestManager.getRequest()));
                    if (value.equals(defaultValue)) {
                        descr.append(" &#8225");
                    }
                    options.add(descr.toString(), value);
                }
            }
            CCRadioButtonTag radioTag = new CCRadioButtonTag();
            radioTag.setEscape("false");
            radioTag.setStyleLevel(CCStyle.LEVEL_THREE);
            radioTag.setElementId(view.getQualifiedName());
            CCRadioButton radioButton = 
                new CCRadioButton((ContainerView) view.getParent(), 
                                  model, view.getName(), "");
            radioButton.setOptions(options);
            addCommonRenderParts(radioTag, view, prop);
            buffer.append(radioTag.getHTMLString(getParent(), pageContext, radioButton));
        }
    }

    
    protected void renderDropDownMenu(NonSyncStringBuffer buffer, PropertyView view, 
            SectionModel model, TemplateProperty prop, boolean readonly) 
            throws JspException {
        if (readonly) {
            renderStaticText(buffer, view, model, prop);
        } else {
            OptionList options = new OptionList();
            options.add(m_notSet, m_notSet);
            if (prop.getConstraints() != null) {
                for(int i=0; i < prop.getConstraints().size(); i++) {
                    TemplatePropertyConstraint constraint = (TemplatePropertyConstraint) prop.getConstraints().elementAt(i);
                    String value = constraint.getValue();
                    String defaultValue = prop.getDefaultValue();
                    StringBuffer buffer2 = new StringBuffer();
                    if (constraint.getResourceId() != null) {
                        buffer2.append(constraint.getResourceId());
                    } else {
                        buffer2.append(prop.getResourceId());
                        buffer2.append(".");
                        buffer2.append(value);
                    }
                    ResourceRepository resources = ResourceRepository.getDefaultRepository();
                    StringBuffer descr = new StringBuffer(resources.getMessage(
                                    buffer2.toString(), 
                                    prop.getResourceBundle(), 
                                    RequestManager.getRequest()));
                    if (value.equals(defaultValue)) {
                        descr.append(" &#8225");
                    }
                    options.add(descr.toString(), value);
                }
            }
            CCDropDownMenuTag tag = new CCDropDownMenuTag();
            tag.setEscape("false");
            CCDropDownMenu dropDown = new CCDropDownMenu((View) view.getParent(), model, view.getName(), (Object) "0", options);
            addCommonRenderParts(tag, view, prop);
            buffer.append(tag.getHTMLString(getParent(), pageContext, dropDown));
        }
    }

    
    protected void renderStringList(NonSyncStringBuffer buffer, PropertyView view, 
            SectionModel model, TemplateProperty prop, boolean readonly) throws JspException {
        ListTag tag = new ListTag();
        ListView propView = new ListView((ContainerView) view.getParent(), model, view.getName());
        tag.setOnFocus("select();");
        if (readonly) {
            propView.setEnabled(false);
        }
        addCommonRenderParts(tag, view, prop);
        buffer.append(tag.getHTMLString(getParent(), pageContext, propView));
    }
    
    
    protected void renderStaticText(NonSyncStringBuffer buffer, PropertyView view, 
            SectionModel model, TemplateProperty prop) 
            throws JspException {
        CCStaticTextFieldTag tag = new CCStaticTextFieldTag();
        CCStaticTextField propView = new CCStaticTextField((ContainerView) view.getParent(), model, view.getName(), null);
        buffer.append(tag.getHTMLString(getParent(), pageContext, propView));
    }
    
    
    protected void renderColorChooser(NonSyncStringBuffer buffer, PropertyView view, 
            SectionModel model, TemplateProperty prop, boolean readonly) throws JspException {
        ColorChooserTag tag = new ColorChooserTag();
        if (((String)model.getValue(SectionModel.PROPERTY_VALUE)).equals(m_notSet)) {
            tag.setValueSet(false);
        }
        ColorChooserView propView = new ColorChooserView((ContainerView) view.getParent(), model, view.getName());
        if (readonly) {
            propView.setEnabled(false);
        }
        addCommonRenderParts(tag, view, prop);
        buffer.append(tag.getHTMLString(getParent(), pageContext, propView));
    }
    
    protected void renderChooser(NonSyncStringBuffer buffer, PropertyView view,
        SectionModel model, TemplateProperty prop) throws JspException {

        String extendsChooser           = prop.getExtendsChooser() ;
        OptionList options              = new OptionList() ;
        ChooserHelper chooserHelper     = new ChooserHelper(Toolbox2.getPropertyPath(prop)) ;
        TemplateProperty definingChooser= chooserHelper.getDefiningChooser();
      
        String chooserDataPath          = definingChooser.getChooserPath() ;
        String chooserNamePath          = chooserHelper.getDefiningChooserPath();
        String chooserLabel             = prop.getLabelPopup() ; 
        if (chooserLabel == null) {
            chooserLabel                = definingChooser.getLabelPopup() ;   
        }
        if (prop.getDefaultValue() == null) {
            prop.setDefaultValue(definingChooser.getDefaultValue()) ;       
        }    
        ArrayList chooserOptions        = chooserHelper.getElementsList();
        options.add(m_notSet, m_notSet);
        for(int i=0; i < chooserOptions.size(); i++) {
            options.add((String)chooserOptions.get(i), (String)chooserOptions.get(i));
        }

        // Hashcode the chooserNamePath attribute & use this as an id
        // when trying to update the profile pane with javascript
        String chooserID = new Integer(chooserNamePath.hashCode()).toString() ;
        chooserNamePath = Toolbox2.encode(chooserNamePath) ;

        // Add the 'Edit...' button for the chooser
        CCButtonTag buttonTag = new CCButtonTag();
        buttonTag.setName(chooserID + "ChooserBtn") ; 
        buttonTag.setBundleID("apocBundle") ;
        buttonTag.setDefaultValue("APOC.policies.edit.button") ;
        buttonTag.setType(CCButton.TYPE_SECONDARY) ;
        buttonTag.setOnClick( "parent.parent.parent.opener.EditListWindow = " 
                    + "window.open('/apoc/manager/EditList?ChooserPath=" + chooserNamePath 
                    + "&ChooserLabel=" + chooserLabel + "',"
                    + "'editListWindow', 'height=450,width=550,"
                    + "top='+((screen.height-650)/2)+',"
                    + "left='+((screen.width-900)/2)+',"
                    + "scrollbars,resizable');"
                    + "parent.parent.parent.opener.EditListWindow.focus();"
                    + "setChooserName(this.name); "
                    + "setChooserID('" + chooserID + "') ;"
                    + "return false;");
        buttonTag.setTitle("APOC.chooser.edit.alt");
        buttonTag.setOnMouseOver("window.status=this.title;return true");
        buttonTag.setOnMouseOut("window.status='';return true");

        CCButton button = null ;
        button = new CCButton((ContainerView) view.getParent(), 
                                        model, 
                                        chooserID + "ChooserBtn",
                                        null) ;

        button.setElementId(view.getQualifiedName().substring(0, view.getQualifiedName().lastIndexOf(".")).concat(".EditButton")) ;
 
        CCDropDownMenuTag tag = new CCDropDownMenuTag();
        CCDropDownMenu dropDown = new CCDropDownMenu((View) view.getParent(), model, view.getName(), (Object) "0", options);
        addCommonRenderParts(tag, view, prop);
        buffer.append(tag.getHTMLString(getParent(), pageContext, dropDown));
        // Add space between dropdown and button
        buffer.append("&nbsp&nbsp&nbsp");
        buffer.append(buttonTag.getHTMLString(getParent(), pageContext, button));        
    }

    
    protected void renderCheckBox(NonSyncStringBuffer buffer, PropertyView view, 
            SectionModel model, TemplateProperty prop, boolean readonly) throws JspException {
        if (readonly) {
            renderStaticText(buffer, view, model, prop);
        } else {    
            OptionList options = new OptionList();
            //options.add("&#8212;&nbsp;Not Set&nbsp;&#8212;", "-notset-");
            options.add(m_notSet, m_notSet);
            String defaultValue = prop.getDefaultValue();

            ResourceRepository resources = ResourceRepository.getDefaultRepository();
            String value = resources.getValidMessage(
                            prop.getResourceId() + ".checked", 
                            prop.getResourceBundle(), 
                    RequestManager.getRequest());
            if (value == null) {
                value = m_i18N.getMessage("APOC.policies.enabled");            
            }
            StringBuffer descr = new StringBuffer(value);
            if ("true".equals(defaultValue)) {
                descr.append(" &#8225");
            }
            options.add(descr.toString(), "true");

            value = resources.getValidMessage(
                            prop.getResourceId() + ".unchecked", 
                            prop.getResourceBundle(), 
                    RequestManager.getRequest());
            if (value == null) {
                value = m_i18N.getMessage("APOC.policies.disabled");            
            }
            descr = new StringBuffer(value);
            if ("false".equals(defaultValue)) {
                descr.append(" &#8225");
            }
            options.add(descr.toString(), "false");

            CCDropDownMenuTag tag = new CCDropDownMenuTag();
            tag.setEscape("false");
            CCDropDownMenu dropDown = new CCDropDownMenu((View) view.getParent(), model, view.getName(), (Object) "0", options);
            addCommonRenderParts(tag, view, prop);
            buffer.append(tag.getHTMLString(getParent(), pageContext, dropDown));
        }
    }
}
