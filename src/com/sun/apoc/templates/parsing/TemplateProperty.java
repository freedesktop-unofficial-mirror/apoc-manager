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


package com.sun.apoc.templates.parsing;

import java.util.Vector;

public class TemplateProperty extends TemplateElement 
                              implements NarratedElement {
    
    public static final String CHECKBOX     = "checkBox";
    public static final String RADIOBUTTONS = "radioButtons";
    public static final String COMBOBOX     = "comboBox";
    public static final String LISTBOX      = "listBox";
    public static final String PASSWORD     = "password";
    public static final String TEXTAREA     = "textArea";
    public static final String TEXTFIELD    = "textField";
    public static final String CUSTOM       = "customRenderer";
    public static final String HIDDEN       = "hidden";
    public static final String STRING_LIST  = "stringList";
    public static final String COLOR_CHOOSER = "colorSelector";
    public static final String CHOOSER	    = "chooser";    
    
    
    
    private String m_dataPath      = null;
    private String m_visualType    = null;
    private String m_type          = null;
    private String m_defaultValue  = null;
    private String m_descriptionId = null;
    private String m_extraHtml     = null;
    private String m_extraData     = null;
    private String m_xmlHandler	   = null;
    private String m_actionHandler	   = null;
    private String m_chooserPath   = null;
    private String m_extendsChooser = null;
    private String m_labelPopup     = null;
    private Vector m_constraints    = null;
    private String m_labelPost      = null;
    private String m_separator      = null;

    private boolean m_autostore       = false;
    private boolean m_defaultNilValue = false;
    
    
    public TemplateProperty(String defaultName, String scope, String resourceId, 
            String resourceBundle, String dataPath, String visualType, 
            String type, String autostore, String xmlHandler, String actionHandler, String extraHtml) {
        super(defaultName, scope, resourceId, resourceBundle);

        m_dataPath = dataPath;
        m_extraHtml = extraHtml;
        m_xmlHandler = xmlHandler ;	        
        m_actionHandler = actionHandler ;	
        m_type = type;
        if (visualType == null) {
            m_visualType = TEXTFIELD;
        } else {
            m_visualType = visualType;
        }
        if ((autostore != null) && (autostore.indexOf("true") != -1)) {
            m_autostore = true;
        }
    }
    
    
    public boolean isAutoStored() {
        return m_autostore;
    }
    
    
    public String getDataPath() {
        return m_dataPath;
    }
    
    
    public String getExtraHtml() {
        return m_extraHtml;
    }

    public String getChooserPath() {
        return m_chooserPath;
    }
    
    public String getExtendsChooser() {
        return m_extendsChooser;
    }        
    
    public String getLabelPopup() {
        return m_labelPopup;
    }
    
    public String getLabelPost() {
        return m_labelPost;
    }
    
    public String getXmlHandler() {
        return m_xmlHandler;
    }     
    
    public String getActionHandler() {
        return m_actionHandler;
    } 
    
    public String getDataType() {
        return m_type;   
    }

    public String getSeparator() {
        String separator = m_separator;
        if(m_separator == null) {
            separator = " ";
        }
        return separator;
    }

    public String getVisualType() {
        return m_visualType;
    }
    
    
    public void setVisualType(String visualType) {
        m_visualType = visualType;
    }

    
    public String getDescriptionId() {
        return m_descriptionId;
    }
    
    
    public void setDescriptionId(String resourceId) {
        m_descriptionId = resourceId;
    }
    
    public void setSeparator(String separator) {
        m_separator = separator;
    }    
    
    public void addConstraint(String value) {
        if (m_constraints == null) {
            m_constraints = new Vector();
        }
        m_constraints.add(new TemplatePropertyConstraint(value, null));
    }
    
    public void addConstraint(String value, String resId) {
        if (m_constraints == null) {
            m_constraints = new Vector();
        }
        m_constraints.add(new TemplatePropertyConstraint(value, resId));
    }
    
    
    public Vector getConstraints() {
        return m_constraints;
    }
    
    
    public void setDefaultValue(String value) {
        m_defaultValue = value;
        m_defaultNilValue = false;         
    }
    
    
    public void setDefaultNilValue(String isNilValue) {
        if ((isNilValue != null) && (isNilValue.indexOf("true") != -1)) {   
            m_defaultNilValue = true;
            m_defaultValue = null;
        } 
    }
    
    
    public String getDefaultValue() {
        return m_defaultValue;
    }
    
    
    public boolean hasDefaultNilValue() {
        return m_defaultNilValue;
    }
    
    
    public String getExtraData() {
        return m_extraData;
    }
    
    
    public void setExtraData(String extraData) {
        m_extraData = extraData;
    }
       
    public void setChooserPath(String chooserPath) {
        m_chooserPath = chooserPath;
    }
    
    public void setExtendsChooser(String extendsChooser) {
        m_extendsChooser = extendsChooser;
    }
    
    public void setLabelPopup(String labelPopup) {
        m_labelPopup = labelPopup;
    }    
    
    public void setLabelPost(String labelPost) {
        m_labelPost = labelPost;
    }    
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(", ");
        buffer.append(getVisualType());
        buffer.append(", ");
        buffer.append(getDataPath());
        buffer.append(") ");
        return buffer.toString();
    }
}


