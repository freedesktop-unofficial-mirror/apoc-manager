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

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.manager.ChooserHelper;
import com.sun.apoc.manager.ProfileWindowFramesetViewBean;
import com.sun.apoc.templates.parsing.TemplateProperty;
import com.sun.apoc.templates.parsing.TemplateSection;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.sun.apoc.spi.cfgtree.DataType;
import com.sun.apoc.spi.cfgtree.property.Property;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.templates.handler.ActionHandler;
import com.sun.apoc.templates.handler.ActionHandlerProperty;
import com.sun.apoc.templates.handler.HandlerContext;
import com.sun.apoc.templates.parsing.TemplateActionHandler;
import com.sun.apoc.templates.parsing.TemplatePage;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.apoc.templates.parsing.TemplateXMLHandler;
import com.sun.apoc.templates.parsing.TemplateXMLHandlerModel;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.model.CCActionTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SectionModel extends CCActionTableModel {

    public static final String PROPERTY                 = "Property";
    public static final String PROPERTY_NAME            = "PropertyName";
    public static final String PROPERTY_VALUE           = "PropertyValue";
    public static final String PROPERTY_VALUE_TMP       = "PropertyValueTmp";
    public static final String ENFORCED_STATE           = "Enforced";
    public static final String ENFORCED_STATE_TMP       = "EnforcedTmp";
    
    public static final String DISABLED_FIELDS_VALUES   = "DisabledFieldsValues";
    
    public static final String RELATIVE_PATH = "./";
    public static final String QUERIED_ID    = "$queriedId";
    public static final String UNDEFINED_VALUE          = "-- Not Set --";    
    
    private TemplateSection m_templateSection = null;
    private SheetModel m_templateModel = null;

    public SectionModel(TemplateSection section, 
                              SheetModel templateModel) {
        super(RequestManager.getRequestContext().getServletContext(), 
              "/jsp/settings/ContentAreaPoliciesSection.xml");
        m_templateSection = section;
        m_templateModel = templateModel;
        setActionValue("NameColumn", "APOC.profilewnd.settings.nameColumn");    
        setActionValue("ValueColumn", "APOC.profilewnd.settings.valueColumn");
        setSelectionType(CCActionTableModel.MULTIPLE);
        update();
    }
    
    public void update() {
        ResourceRepository resources = ResourceRepository.getDefaultRepository();
        String sectionName = resources.getMessage(
                                m_templateSection.getResourceId(), 
                                m_templateSection.getResourceBundle(), 
                                RequestManager.getRequest());
        setTitle(sectionName);
        try {
            getRowList().clear();

            PolicyMgrHelper mgr = ProfileWindowFramesetViewBean.getProfileHelper();
            if (m_templateSection.getProperties() != null) {
                HashMap handlerMap = new HashMap();
                for(int i=0; i < m_templateSection.getProperties().size(); i++) {
                    TemplateProperty property = m_templateSection.getProperty(i);
                    String value = null;
                    String overwriteAllowed = "true";
                    Property prop = mgr.getProperty(getAbsolutePath(property.getDataPath()));

                    //if (property.isInScope(PolicyNavigationModel.getScope())) {

                    // If a property has an associated TemplateXMLHandler we need to 
                    // add the required javascript function to a stringBuffer and in turn 
                    // to a vector which can then be rendered by the view bean
                    if (property.getXmlHandler() != null) {
                        String handlerName              =   property.getXmlHandler() ;
                        TemplateXMLHandlerModel handlerModel    =   new TemplateXMLHandlerModel(m_templateModel) ;
                        TemplatePage page               =   m_templateModel.getPage() ;
                        TemplateXMLHandler handler      = page.getXMLHandler(handlerName) ;
                        StringBuffer b                  = new StringBuffer();
                        ArrayList whenConditions        = new ArrayList();
                        // Convert the handlers elements and attributes to javascript.
                        if (handler != null) {
                            if (handler.getCommands() != null) {
                                for(int j=0 ; j < handler.getCommands().size(); j++) {
                                    String command = handlerModel.getJavaScript(handler.getCommand(j), TemplateXMLHandlerModel.COMMAND_TYPE) ;
                                    b.append(command)
                                        .append(";");
                                }
                            } 
                            if (handler.getWhenCommandLists() != null) {
                                for(int k=0; k < handler.getWhenCommandLists().size(); k++) {
                                    String condition = handlerModel.getJavaScript(handler.getTest(k), TemplateXMLHandlerModel.CONDITION_TYPE) ;
                                    whenConditions.add(condition);
                                    b.append("\nif(")
                                        .append(condition)
                                        .append(") {") ;
                                    List commandList = handler.getWhenCommandList(k) ;
                                    for(int j=0 ; j < commandList.size(); j++) {
                                        String command = handlerModel.getJavaScript((String)commandList.get(j), TemplateXMLHandlerModel.COMMAND_TYPE) ;
                                        b.append(command)
                                            .append(";");
                                    }
                                    b.append("}") ;
                                }
                            } 
                            if (handler.getOtherwiseCommands() != null) {
                                b.append("\nif (!(");
                                for (int j = 0; j < whenConditions.size(); j++) {
                                    b.append("(")
                                        .append(whenConditions.get(j))
                                        .append(")");
                                    if (j != whenConditions.size() - 1) {
                                        b.append("||");
                                    }
                                }
                                b.append(")) {") ;
                                for(int j=0 ; j < handler.getOtherwiseCommands().size(); j++) {
                                    String command = handlerModel.getJavaScript(handler.getOtherwiseCommand(j), TemplateXMLHandlerModel.COMMAND_TYPE) ;
                                    b.append(command)
                                        .append(";");
                                }
                                b.append("}\n") ;
                            }
                            // Add the javascript to the vector  - both inline and then within a function
                            b.insert(0, "\nfunction "+ handlerName + "() {") ;
                            b.append("}\n\n") ;
                            m_templateModel.addToXmlHandlerList(handlerName + "()\n") ;
                            m_templateModel.addToXmlHandlerList(b.toString()) ;
                        }
                    }
                   
                    
                    appendRow();
                    // get localized property name
                    String name = resources.getMessage(
                                        property.getResourceId(), 
                                        property.getResourceBundle(), 
                                        RequestManager.getRequest());


                    // evaluate property value
                    if (prop != null) {
                        value = prop.getValue();
                        if (prop.isProtected()) {
                            overwriteAllowed = "false";
                        }
                    }

                    if (value == null) {
                        //value = getDefaultValue(property);
                        value = getUndefinedValue();
                    }

                    // special handling for choosers
                    // need to check if any choices used in content area 
                    // (i.e. values stored in backend or defaults)  are
                    // not stored in the current chooser list so 
                    // they can  be added where needed.
                    if (property.getVisualType().equals(TemplateProperty.CHOOSER)) {
                        ChooserHelper chooserHelper = new ChooserHelper(Toolbox2.getPropertyPath(property));
                        TemplateProperty definedChooser = chooserHelper.getDefiningChooser();
                         // Add the default value for a chooser to the backend list
                        if ((property.getDefaultValue() != null) 
                                    && (value != property.getDefaultValue())) {
                            m_templateModel.addToChooserLists(chooserHelper.getDefiningChooserPath(), property.getDefaultValue());
                        }                             
                        if (value != getUndefinedValue()) {
                            if (property.getVisualType().equals(TemplateProperty.CHOOSER)) {
                                m_templateModel.addToChooserLists(chooserHelper.getDefiningChooserPath(), value);
                            }
                        }
                    }

                    // special handling for color values in StarOffice
                    // (see #b4946354#)
                    if (property.getVisualType().equals(TemplateProperty.COLOR_CHOOSER) &&
                        property.getDataPath().startsWith("org.openoffice.Office")) {
                        if (value != getUndefinedValue()) {
                            StringBuffer buffer = new StringBuffer();
                            try {
                                String val = Integer.toHexString(new Integer(value).intValue()); 
                                buffer.append("#");
                                if (val.length() < 6) {
                                    for (int j = 0; j < 6-val.length(); j++){
                                        buffer.append("0");
                                    }
                                }
                                buffer.append(val);
                                value = buffer.toString();
                            } catch (NumberFormatException ex) {
                                CCDebug.trace1("Error decoding value" + value, ex);
                            }
                        }
                    }
                    if (property.getActionHandler() != null) {
                        String handlerId = property.getActionHandler();
                        LinkedList propList = (LinkedList)handlerMap.get(handlerId);
                        if (propList == null) {
                            propList = new LinkedList();
                        }
                        ActionHandlerProperty ahp = new ActionHandlerProperty(property, value, !Boolean.getBoolean(overwriteAllowed));
                        propList.add(ahp);
                        handlerMap.put(handlerId, propList);
                    } 
                    if (false) {
                        CCI18N i18n = new CCI18N(RequestManager.getRequestContext(),
                                                    Constants.RES_BASE_NAME);
                        // checkbox handling
                        if (property.getVisualType().equals(TemplateProperty.CHECKBOX)) {
                            if (value.equals("true")) {
                                value = resources.getValidMessage(
                                            property.getResourceId() + ".checked", 
                                            property.getResourceBundle(), 
                                            RequestManager.getRequest());
                                if (value == null) {
                                    value = i18n.getMessage("APOC.policies.enabled");            
                                }
                            } else {
                                value = resources.getValidMessage(
                                            property.getResourceId() + ".unchecked", 
                                            property.getResourceBundle(), 
                                            RequestManager.getRequest());
                                if (value == null) {
                                    value = i18n.getMessage("APOC.policies.disabled");
                                }
                            }

                        // handling for combobox and radio button    
                        } else if ((property.getVisualType().equals(TemplateProperty.RADIOBUTTONS)) || 
                                  (property.getVisualType().equals(TemplateProperty.COMBOBOX))) {
                            StringBuffer buffer = new StringBuffer();
                            buffer.append(property.getResourceId());
                            buffer.append(".");
                            buffer.append(value);
                            String descr = resources.getMessage(
                                            buffer.toString(), 
                                            property.getResourceBundle(), 
                                            RequestManager.getRequest());
                            if (descr != null) {
                                value = descr;
                            }

                        // handling for password fields (do not display passwords
                        // as clear text!)     
                        } else if (property.getVisualType().equals(TemplateProperty.PASSWORD)) {
                             StringBuffer buffer = new StringBuffer();
                             for (int w = 0; w < value.length(); w++) {
                                 buffer.append("*");
                             }
                             value = buffer.toString();
                        }
                    }

                    // fill the model
                    super.setValue(PROPERTY_NAME, name);
                    super.setValue(PROPERTY, property);
                    super.setValue(PROPERTY_VALUE, value);
                    super.setValue(PROPERTY_VALUE_TMP, null);
                    super.setValue(ENFORCED_STATE, overwriteAllowed);
                    super.setValue(ENFORCED_STATE_TMP, null);
                    //}
                }
                ArrayList propNames = new ArrayList();
                for (int i=0;i < super.getSize(); i++){
                    super.setRowIndex(i);
                    TemplateProperty tp = (TemplateProperty)super.getValue(PROPERTY);
                    propNames.add(tp.getDefaultName());
                }
                Iterator keys = handlerMap.keySet().iterator();
                while (keys.hasNext()) {
                    String handlerId = (String)keys.next();
                    LinkedList propList = (LinkedList)handlerMap.get(handlerId);
                    HandlerContext context = new HandlerContext(propList);
                    TemplateActionHandler handler = m_templateModel.getPage().getActionHandler(handlerId);
                    ((ActionHandler)(handler.getClassInstance())).handleLoad(context);
                    Iterator it = context.getProperties();
                    while (it.hasNext()) {
                        ActionHandlerProperty ahp = (ActionHandlerProperty)it.next();
                        String name = ahp.getName();
                        int location = propNames.indexOf(name);
                        super.setRowIndex(location);
                        super.setValue(PROPERTY_VALUE, ahp.getValue());                        
                    }
                }
                beforeFirst();
            }
        } catch (SPIException ex) {
            CCDebug.trace1("Error using policy manager!", ex); 
        } catch (ModelControlException ex) {
            CCDebug.trace1("Error initializing the model!", ex); 
        } 
    }
    
    public void storeChanges() throws SPIException {
        HashMap handlerMap = new HashMap();        
        //The disabledValueString is a '|' delimited string consisting of
        //a triplet of property values - section name;index;value;.
        String disabledValueString = (String)m_templateModel.getValue(SectionModel.DISABLED_FIELDS_VALUES) ;
        String[] disabledValues = disabledValueString.split("\\|", -1) ;
        for(int i = 0; i < disabledValues.length-2; i=i+3) {
            if(this.getTemplateSection().getDefaultName().equals(disabledValues[i])) {
                setRowIndex(Integer.parseInt(disabledValues[i+1]));
                super.setValue(PROPERTY_VALUE_TMP, disabledValues[i+2]);
            }
        }
        for(int index = 0; index < getNumRows(); index++) {
            setRowIndex(index);
            TemplateProperty property = (TemplateProperty) getValue(PROPERTY);
            String newValue = (String) getValue(PROPERTY_VALUE_TMP);
            String oldValue = (String) getValue(PROPERTY_VALUE);
            String oldOverwriteState = (String) getValue(ENFORCED_STATE);
            String newOverwriteState = (String) getValue(ENFORCED_STATE_TMP);
            if (newOverwriteState == null) {
                newOverwriteState = "false";
            }
            PolicyMgrHelper mgr = ProfileWindowFramesetViewBean.getProfileHelper();
            Property prop = null;
                                   
            // evaluate value column
            if (newValue == null) {
                newValue = "false";
            }
            // If a prop has an ActionHandler don't create/remove any 
            // Property objects let the handler deal with it
            if (property.getActionHandler() != null) {
                String handlerId = property.getActionHandler();
                LinkedList propList = (LinkedList)handlerMap.get(handlerId);
                if (propList == null) {
                    propList = new LinkedList();
                }
                ActionHandlerProperty ahp = new ActionHandlerProperty(property, newValue, !Boolean.getBoolean(newOverwriteState));
                propList.add(ahp);
                handlerMap.put(handlerId, propList);
            }
            
            if ((!newValue.equals(oldValue))) {
                CCDebug.trace3("Value changed: " + property.getDefaultName());
                CCDebug.trace3("New Value: " + newValue);
                CCDebug.trace3("Old Value: " + oldValue);
                prop = mgr.getProperty(getAbsolutePath(property.getDataPath()));
                //  special handling for color values in StarOffice
                // (see #b4946354#)
                String soColorChooserValue = newValue ;
                if (!newValue.equals(getUndefinedValue())) {
                    if (property.getVisualType().equals(TemplateProperty.COLOR_CHOOSER) &&
                        property.getDataPath().startsWith("org.openoffice.Office")) {
                        try {
                            if (newValue.length() != 0) {
                                newValue = newValue.substring(1);
                                int hexInt = Integer.parseInt(newValue, 16);
                                long hexLong = Long.parseLong(newValue, 16);
                                newValue = Integer.toString(hexInt);
                            }               
                        } catch (NumberFormatException ex) {
                            CCDebug.trace1("Error wrong value format" + newValue, ex);
                            newValue = ""; 
                        }
                    }
                }

                // clear
                if (newValue.equals(getUndefinedValue())) {
                    mgr.removeProperty(getAbsolutePath(property.getDataPath()));
                    // we do no longer support the scenario, where the property
                    // value is NULL and the property itself is protected!
                    super.setValue(ENFORCED_STATE, "true");
                    
                // new property value
                } else { 
                    if (prop == null) {
                        prop = mgr.createProperty(getAbsolutePath(property.getDataPath()));
                    }    
                    if (property.getVisualType().equals(TemplateProperty.STRING_LIST)) {
                        prop.setSeparator(property.getSeparator());
                    }
                    prop.put(newValue, getDataType(property));

                }

                if (property.getVisualType().equals(TemplateProperty.COLOR_CHOOSER) &&
                    property.getDataPath().startsWith("org.openoffice.Office")) {
                        super.setValue(PROPERTY_VALUE, soColorChooserValue);
                } else {
                        super.setValue(PROPERTY_VALUE, newValue);                
                }
            }
            
            // evaluate enforce column
            if ((!newOverwriteState.equals(oldOverwriteState))) {
                prop = mgr.getProperty(getAbsolutePath(property.getDataPath()));
                if (prop != null) {
                    if (newOverwriteState.equals("false")) {
                        prop.setProtected(true);
                    } else {
                        prop.setProtected(false);
                    }
                } else {
                    // we do no longer support the scenario, where the property
                    // value is NULL and the property itself is protected!
                    newOverwriteState = "true";
                }
                super.setValue(ENFORCED_STATE, newOverwriteState);
                oldOverwriteState = newOverwriteState;
            }
        }
        Iterator keys = handlerMap.keySet().iterator();
        while (keys.hasNext()) {
            String handlerId = (String)keys.next();
            LinkedList propList = (LinkedList)handlerMap.get(handlerId);
            HandlerContext context = new HandlerContext(propList);
            TemplateActionHandler handler = m_templateModel.getPage().getActionHandler(handlerId);
            ((ActionHandler)(handler.getClassInstance())).handleSave(context);
        }
    }
    
    
    public void setValue(String name, Object value) {
        if (name.equals(PROPERTY_VALUE)) {
            super.setValue(PROPERTY_VALUE_TMP, value);
        
        } else if (name.equals(ENFORCED_STATE)) {
            super.setValue(ENFORCED_STATE_TMP, value);
            
        } else {
            super.setValue(name, value);
        }
    }
    
    public TemplateSection getTemplateSection() {
        return m_templateSection;
    }
    
    public SheetModel getSheetModel() {
        return m_templateModel;
    }
    
    public static DataType getDataType(TemplateProperty property) {
        if (property.getDataType() == null) {
            // No datatype associated with property! Using fallback datatype.
            if (property.getVisualType().equals(TemplateProperty.CHECKBOX)) {
                return DataType.BOOLEAN;
            } else if (property.getVisualType().equals(TemplateProperty.COMBOBOX)) {
                return DataType.INT;
            } else if (property.getVisualType().equals(TemplateProperty.TEXTFIELD)) {
                return DataType.STRING;
            } else if (property.getVisualType().equals(TemplateProperty.TEXTAREA)) {
                return DataType.STRING;
            } else if (property.getVisualType().equals(TemplateProperty.RADIOBUTTONS)) {
                return DataType.INT;
            } else {
                return DataType.STRING;
            }
        } else {
          if (property.getDataType().equals("xs:boolean")) {
              return DataType.BOOLEAN;
          } else if (property.getDataType().equals("xs:short")) {
              return DataType.SHORT;
          } else if (property.getDataType().equals("xs:int")) {
              return DataType.INT;
          } else if (property.getDataType().equals("xs:long")) {
              return DataType.LONG;
          } else if (property.getDataType().equals("xs:double")) {
              return DataType.DOUBLE;
          } else if (property.getDataType().equals("xs:string")) {
              return DataType.STRING;
          } else if (property.getDataType().equals("xs:hexBinary")) {
              return DataType.HEXBIN;                                                                        
          } else if (property.getDataType().equals("oor:any")) {
              return DataType.ANY;                                                                        
          } else if (property.getDataType().equals("oor:boolean-list")) {
              return DataType.BOOLEAN_LIST;
          } else if (property.getDataType().equals("oor:short-list")) {
              return DataType.SHORT_LIST;
          } else if (property.getDataType().equals("oor:int-list")) {
              return DataType.INT_LIST;
          } else if (property.getDataType().equals("oor:long-list")) {
              return DataType.LONG_LIST;
          } else if (property.getDataType().equals("oor:double-list")) {
              return DataType.DOUBLE_LIST;
          } else if (property.getDataType().equals("oor:string-list")) {
              return DataType.STRING_LIST;                                                                        
          } else if (property.getDataType().equals("oor:hexBinary-list")) {
              return DataType.HEXBIN_LIST;                                                                        
          } else {
              return DataType.STRING;
          } 
      }
   }
    
   protected String getAbsolutePath(String datapath) throws SPIException {
        //special handling for properties in sets
        String absolutePath = datapath;
        if (datapath.startsWith(RELATIVE_PATH)) {
            absolutePath = m_templateModel.getPage().getDataPath() 
                + datapath.substring(1);
            int dynamicPos = absolutePath.indexOf(QUERIED_ID);
            absolutePath = absolutePath.substring(0, dynamicPos)
                + PolicyMgrHelper.encodePath(m_templateModel.getSetIndex())
                + absolutePath.substring(dynamicPos + QUERIED_ID.length());
         }
         return absolutePath;
    } 
    
    public static String getUndefinedValue() {
        CCI18N i18n = new CCI18N(RequestManager.getRequestContext(),
                            Constants.RES_BASE_NAME);  
        String localizedNotSet = i18n.getMessage("APOC.settings.notset");
        if (localizedNotSet.equals("APOC.settings.notset")) {
            localizedNotSet = "-- Not Set --";
        }
        return localizedNotSet;
    }
}
