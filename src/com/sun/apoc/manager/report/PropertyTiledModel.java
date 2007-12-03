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

package com.sun.apoc.manager.report;

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestParticipant;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.templates.parsing.TemplateProperty;
import com.sun.apoc.templates.parsing.TemplatePropertyConstraint;
import com.sun.apoc.templates.parsing.TemplateSection;
import com.sun.apoc.manager.resource.ResourceRepository;

import com.sun.apoc.spi.cfgtree.property.Property;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.policies.Policy;
import com.sun.apoc.spi.cfgtree.property.MergedProperty;
import com.sun.apoc.spi.SPIException;

import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.model.CCActionTableModel;
import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.templates.parsing.TemplateSet;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

public class PropertyTiledModel extends CCActionTableModel implements RequestParticipant {

    public static final String NAME = "NameValue";
    public static final String VALUE = "ValueValue";
    public static final String STATUS = "StatusValue";
    public static final String PROFILE = "ProfileValue";
    public static final String DESCRIPTION = "DescriptionValue";
    public static final String STATUSPRO = "StatusValueProtection";


    private ResultsData m_resultsData = null;
    private String m_Id = "1";
    private PolicyMgrReportHelper m_policyMgrHelper = null;
    
    CCI18N m_I18n = null;
    HttpServletRequest m_Request;
    
    public PropertyTiledModel() {
        super();
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }

    public void setRequestContext(RequestContext requestContext) { 
        m_Request = requestContext.getRequest();
    }
 
    public void setData(ResultsData resultsData, String id) {
        m_resultsData = resultsData;
        m_Id = id;
        m_policyMgrHelper  = (PolicyMgrReportHelper)RequestManager.getRequestContext().getRequest()
                                            .getSession(true).getAttribute(Constants.POLICY_MANAGER_HELPER);
        try {
            this.setSize(m_resultsData.getPropertyList().size());
        } catch (ModelControlException mce){
            CCDebug.trace2("Model contol expection caught: "+mce);
        } 
    }

    public void retrieve() throws ModelControlException {
        ResourceRepository resources = ResourceRepository.getDefaultRepository();
        ArrayList templateProperties = m_resultsData.getTemplatePropertyList();
        ArrayList properties = m_resultsData.getPropertyList();
        int j = 1;
        for (int i = 0; i < templateProperties.size(); i++) {
            String id = Integer.toString(j);
            String superId = m_Id + ".";
            String path  = m_resultsData.getPath();
            String linkPath  = m_resultsData.getLinkPath();
            boolean isASet = m_resultsData.isASet();
            TemplateSection section = m_resultsData.getSection();
            if (isASet) {
                section = (TemplateSet)section;
            }
            TemplateProperty templateProperty = (TemplateProperty)templateProperties.get(i);
            PropertyParams propParams = new PropertyParams((Property)properties.get(i));
            try {
                addToModel(NAME, getModelValue(section, templateProperty, propParams, path, linkPath, isASet, NAME)); 
                addToModel(VALUE, getModelValue(section, templateProperty, propParams, path, linkPath, isASet, VALUE)); 
                addToModel(STATUS, getModelValue(section, templateProperty, propParams, path, linkPath, isASet, STATUS)); 
                addToModel(STATUSPRO, getModelValue(section, templateProperty, propParams, path, linkPath, isASet, STATUSPRO)); 
                addToModel(PROFILE, getModelValue(section, templateProperty, propParams, path, linkPath, isASet, PROFILE)); 
                addToModel(DESCRIPTION, getModelValue(section, templateProperty, propParams, path, linkPath, isASet, DESCRIPTION));
                setValue(PropertyTiledView.CHILD_ROW_ID, superId + id);
            }
            catch (Exception se) {
                throw new ModelControlException(se);
            }
            j++;
            next();
        }
        first();
    }
    
    public void addToModel(String name, String value)
        throws ModelControlException {
        int  currentLocation = getLocation();
        if (name.equals(NAME)) {
            setValue(PropertyTiledView.CHILD_NAME_VALUE, value);
        } else if (name.equals(VALUE)) {
            setValue(PropertyTiledView.CHILD_VALUE_VALUE, value);            
        } else if (name.equals(STATUS)) {
            setValue(PropertyTiledView.CHILD_STATUS_VALUE, value);            
        } else if (name.equals(STATUSPRO)) {
            setValue(PropertyTiledView.CHILD_STATUSPRO_VALUE, value);   
        } else if (name.equals(PROFILE)) {
            String profileName = "";
            if (value != null && value.length() != 0) {
                String profileId = value.substring(0, value.indexOf("|"));
                String sectionPath = value.substring(value.indexOf("|") + 1);
                if (profileId != null && profileId.length() != 0) {
                    profileName = m_policyMgrHelper.getReportProfile(profileId).getDisplayName();
                }
                setValue(PropertyTiledView.CHILD_PROFILE_VALUE, profileName);
                setValue(PropertyTiledView.CHILD_PROFILE_VALUE_HREF, profileId + "\\|" + sectionPath);
            } else {
                setValue(PropertyTiledView.CHILD_PROFILE_VALUE, profileName);
                setValue(PropertyTiledView.CHILD_PROFILE_VALUE_HREF, profileName);               
            }
        }
    }

    public String getModelValue(TemplateSection section, TemplateProperty prop, 
                                        PropertyParams propParams, String path, 
                                        String linkPath, boolean isASet, String str) {
        ResourceRepository resources = ResourceRepository.getDefaultRepository();
        String reportType = (String)RequestManager.getRequestContext().getRequest().getSession(true).getAttribute(Constants.REPORT_TYPE);
        String result = null;
        if (str.equals(NAME)) {    
            // get localized property name
            String name = null;
            name = resources.getMessage(
                                    prop.getResourceId(), 
                                    prop.getResourceBundle(), 
                                    RequestManager.getRequest());    

            result = name;
            
        } else if (str.equals(VALUE)) {
            String value = propParams.getValue();

            if (value == null) {
                if (prop.getDefaultValue() != null ){
                    value = prop.getDefaultValue();
                } else {
                    value = "";
                }
            }

            // If its a checkbox set the value to the display value
            if (prop.getVisualType().equals(TemplateProperty.CHECKBOX)) {
                if (value.equals("true")) {
                    value = resources.getValidMessage(
                                        prop.getResourceId() + ".checked", 
                                        prop.getResourceBundle(), 
                                        RequestManager.getRequest());
                    if (value == null) {
                        value = m_I18n.getMessage("APOC.policies.enabled");
                    }
                } else {
                    value = resources.getValidMessage(
                                            prop.getResourceId() + ".unchecked", 
                                            prop.getResourceBundle(), 
                                            RequestManager.getRequest());
                    if (value == null) {
                        value = m_I18n.getMessage("APOC.policies.disabled");;
                    }
                }
            // If ifs a radio or combo selection get what the ints mean    
            } else if ((prop.getVisualType().equals(TemplateProperty.RADIOBUTTONS)) || 
                        (prop.getVisualType().equals(TemplateProperty.COMBOBOX))) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(prop.getResourceId());
                buffer.append(".");
                buffer.append(value);
                String descr = resources.getValidMessage(
                                buffer.toString(), 
                                prop.getResourceBundle(), 
                                RequestManager.getRequest());

                if (descr == null) {
                    for(int z=0; z < prop.getConstraints().size(); z++) {
                        TemplatePropertyConstraint constraint = (TemplatePropertyConstraint) prop.getConstraints().elementAt(z);
                        if (constraint.getValue().equals(value)) {
                            if (constraint.getResourceId() != null) {
                                descr = resources.getMessage(
                                        constraint.getResourceId(), 
                                        prop.getResourceBundle(), 
                                        RequestManager.getRequest());
                                break;            
                            }

                        }
                    }
                }
                if (descr != null) {
                    value = descr;
                } else {
                    value = buffer.toString();
                }                

            // Display string lists correctly bug fix #b4952223
            } else if (prop.getVisualType().equals(TemplateProperty.STRING_LIST)) {
                StringBuffer buffer = new StringBuffer();
                String separator = prop.getSeparator();
                /*while (value.indexOf(";") != -1) {
                    int end = value.indexOf(";") + 1;
                    buffer.append(value.substring(0, end));
                    buffer.append(" ");
                    value = value.substring(end, value.length());
                }
                buffer.append(value);
                value = buffer.toString();
                value = value.replaceAll("; ", "<br>");*/
                value = value.replaceAll(separator, "<br>");
            } else if (prop.getVisualType().equals(TemplateProperty.COLOR_CHOOSER) &&
                        prop.getDataPath().startsWith("org.openoffice.Office")) {
                if (value != "") {
                    Integer valueInt = new Integer(value);
                    String hex;
                    try {
                        hex = Integer.toHexString(valueInt.intValue());
                        value = "#"+hex;
                    } catch (NumberFormatException ex) {
                        CCDebug.trace1("Error wrong value format" + valueInt, ex);
                        value = ""; 
                    }
                }
            }
            result = value;
            
        } else if (str.equals(STATUS)) {
            StringBuffer buffer = new StringBuffer();
            String value = propParams.getValue();
            if (value != null) {
                if (reportType.equals(Constants.ENTITY_REPORT_TYPE)) {
                    buffer.append(m_I18n.getMessage("APOC.report.table.defined.in"));
                } else {
                    buffer.append(m_I18n.getMessage("APOC.report.table.defined"));               
                }
            }

            result = buffer.toString();
            
        } else if (str.equals(STATUSPRO)) {
            // Check if property is protected
            StringBuffer buffer = new StringBuffer();
            boolean finalized = propParams.isProtected();
            if (propParams.getOriginOfProtection() != null) {
                finalized = true;
            }
            if (finalized == true) {
                buffer.append(m_I18n.getMessage("APOC.report.table.protected.in"));
            } else {
                buffer.append("");
            }
            result = buffer.toString();
            
        } else if (str.equals(PROFILE)) {
            StringBuffer buffer = new StringBuffer();
            Profile policyGroupOrigin = null;

            String nodeValue = null;
            nodeValue = propParams.getValue();
            if (nodeValue == null) {
                result = "";
            } else {
                Policy policyOrigin = propParams.getOrigin();
                policyGroupOrigin = m_policyMgrHelper.getReportProfile(policyOrigin.getProfileId());
                if (policyGroupOrigin == null) {
                     result = "";
                } else {
                    buffer.append(policyGroupOrigin.getId());
                    String sectionPath = linkPath.replaceAll(";", "/");
                    result = buffer.toString() + "|" + sectionPath;
                }
            }

        } else if (str.equals(DESCRIPTION)) {
            String description;

            if (prop.getDescriptionId() != null) {
               description = resources.getMessage(
                                            prop.getDescriptionId(), 
                                            prop.getResourceBundle(), 
                                            RequestManager.getRequest());
            } else {
                description = "";
            }

            result = description;
            
        } else {
            result = (String)super.getValue(str);
        }
        return result;
    }     
    
    class PropertyParams {
        private String m_value = null;
        private Policy m_origin = null;
        private Policy m_originOfProtection = null;
        private boolean m_isReadOnly = false;
        private boolean m_isProtected = false;
        private Property m_property = null;
        
        public PropertyParams(Property property) {
            m_property = property;
            initialize();
        }        
        
        public String getValue() {
            return m_value;
        }

        public Policy getOrigin() {
            return m_origin;
        }

        public Policy getOriginOfProtection() {
            return m_originOfProtection;
        }
        
        public boolean isReadOnly() {
            return m_isReadOnly;
        }

        public boolean isProtected() {
            return m_isProtected;
        }
        
        private void initialize() {
            // evaluate property value
            try {
                if ((m_property != null)) {
                    m_value = m_property.getValue();
                }
            } catch (SPIException e){
                CCDebug.trace3("SPI Exception caught: "+e);
            }  
            
            // evaluate the origin
            m_origin = ((MergedProperty)m_property).getOriginOfValue();
            // evaluate the origin of protection
            m_originOfProtection = ((MergedProperty)m_property).getOriginOfProtection();
            // evalute readonly value
            m_isReadOnly = m_property.isReadOnly();
            // evalute protected value
            m_isProtected = m_property.isProtected();
        }

    }
}
