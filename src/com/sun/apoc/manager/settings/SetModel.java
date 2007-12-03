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

import java.util.Iterator;
import com.iplanet.jato.RequestManager;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.model.CCActionTableModel;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.apoc.templates.parsing.TemplateSection;
import com.sun.apoc.templates.parsing.TemplateSet;
import com.sun.apoc.templates.parsing.TemplateProperty;
import com.sun.apoc.manager.profiles.ProfileWindowModel;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.cfgtree.policynode.PolicyNode;
import com.sun.apoc.spi.cfgtree.policynode.PolicyNodeImpl;
import com.sun.apoc.spi.cfgtree.property.Property;
import com.sun.apoc.spi.cfgtree.XMLStreamable;
import com.sun.web.ui.common.CCI18N;

public class SetModel extends CCActionTableModel {
    
    public static final String ENTRY_NAME = "PropertyName";
    public static final String ENTRY_NAME_NOT_LINK = "PropertyNameNotLink";
    public static final String ENTRY_HREF = "SubPageHref";
    public static final String ENTRY_ACTION = "ActionValue";
    
    private ProfileWindowModel mEditorModel = null;
    private TemplateRepository  m_templateRepository = null;
    private TemplateSet m_templateSet = null;
    private ResourceRepository m_resources = null;

    
    public SetModel(TemplateSection section) {
        super();
        m_templateRepository = TemplateRepository.getDefaultRepository();
        m_templateSet = (TemplateSet) section;
        byte scope = getEditorModel().getSettingsScope();
        if (m_templateSet.getPage().hasVisibleContent(scope)) {
            setDocument(RequestManager.getRequestContext().getServletContext(), 
                        "/jsp/ContentAreaPoliciesSet.xml");
        } else {
            setDocument(RequestManager.getRequestContext().getServletContext(),
                        "/jsp/ContentAreaPoliciesSimpleSet.xml");
        }
        
        m_resources = ResourceRepository.getDefaultRepository();
        String sectionName = m_resources.getMessage(
                                section.getResourceId(), 
                                section.getResourceBundle(), 
                                RequestManager.getRequest());
        setTitle(sectionName);
        setActionValue("NewButton", "APOC.policies.new.button");
        setActionValue("DeleteButton", "APOC.policies.delete.button");
        setActionValue("RenameButton", "APOC.pool.rename");
   //     setActionValue("ToggleButton", "APOC.policies.toggle.button");
        setActionValue("NameColumn", "APOC.policies.nameCol");
   //     setActionValue("ActionColumn", "APOC.policies.actionCol");
        setSelectionType(CCActionTableModel.MULTIPLE);
        update();
    }
    
    
    public TemplateSet getTemplateSet() {
        return m_templateSet;
    }
    
    
    public void update() {
        try {
            getRowList().clear();
            PolicyMgrHelper mgr = getEditorModel().getProfileHelper();
                            
            StringBuffer sPath = new StringBuffer(getEditorModel().getSelectedCategory());
            sPath.append(TemplateRepository.TEMPLATE_PATH_SEPARATOR);               
            sPath.append(TemplateRepository.SET_PREFIX);             
            sPath.append(m_templateSet.getDefaultName());
            sPath.append(TemplateRepository.SET_INDEX_SEPARATOR);                              
           
            CCI18N i18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
            
            PolicyNode node = mgr.getNode(m_templateSet.getDataPath()); 
            if ((node != null)) {
                String[] childrenNames = node.getChildrenNames();
                for(int i = 0; i < childrenNames.length; i++) {
                    PolicyNode subNode = mgr.getNode(m_templateSet.getDataPath() + TemplateRepository.TEMPLATE_PATH_SEPARATOR 
                        + mgr.encodePath(childrenNames[i]));
                    String attributes = ((PolicyNodeImpl)subNode).getAttributes(XMLStreamable.POLICY_SCHEMA);               
                    if(attributes.indexOf("oor:op=\"replace\"") != -1) {
                            appendRow();
                            setValue(ENTRY_NAME, subNode.getName());
             //               setValue(ENTRY_NAME_NOT_LINK, "");
                            setValue(ENTRY_HREF, sPath.toString() + subNode.getName());
            //                setValue(ENTRY_ACTION, i18n.getMessage("APOC.policies.include_in") + " " + getTitle());
                    } /*else if (attributes.indexOf("oor:op=\"remove\"") != -1) {
                            appendRow();                       
                            setValue(ENTRY_NAME, "");
                            setValue(ENTRY_NAME_NOT_LINK, subNode.getName());
                            setValue(ENTRY_HREF, "");
                            setValue(ENTRY_ACTION, i18n.getMessage("APOC.policies.exclude_from") +  " " + getTitle());
                    }*/
                }
            }
            beforeFirst();
            setMaxRows(getRowIndex() + 1);
        } catch (SPIException ex) {
            getEditorModel().setErrorMessage("Could not read values", ex);

        } catch (ModelControlException ex) {
            CCDebug.trace1("Error initializing the model!", ex); 
        }
    }
    
    
    public void addEntry(String name) throws SPIException {
        PolicyMgrHelper mgr = getEditorModel().getProfileHelper();
        PolicyNode node = mgr.createNode(m_templateSet.getDataPath());
        PolicyNode entry = mgr.createReplaceNode(m_templateSet.getDataPath() 
                        + TemplateRepository.TEMPLATE_PATH_SEPARATOR 
                        + mgr.encodePath(name));
        addRequiredProperties(m_templateSet.getDataPath(), name);
        mgr.flushAllChanges();
    }

        
  /*  public void addRemoveEntry(int index) throws SPIException, ModelControlException {
        setLocation(index);                            
        String name = (String) getValue(ENTRY_NAME);
        PolicyMgrHelper mgr = getEditorModel().getProfileHelper();
        PolicyNode node = mgr.getNode(m_templateSet.getDataPath());
        node.addRemoveNode(node.getAbsolutePath());
        mgr.flushAllChanges();
    }*/

    public void removeEntry(int index) throws SPIException, ModelControlException {
        setLocation(index);                            
        String name = (String) getValue(ENTRY_NAME);
        PolicyMgrHelper mgr = getEditorModel().getProfileHelper();
        PolicyNode node = mgr.getNode(m_templateSet.getDataPath());
        node.removeNode(name);
        mgr.flushAllChanges();
    }
    
     
    public void renameEntry(int index, String originalName, String newName) throws SPIException, ModelControlException {
        setLocation(index);                            
        PolicyMgrHelper mgr = getEditorModel().getProfileHelper();
        PolicyNode node = mgr.getNode(m_templateSet.getDataPath());
        if ((node != null)) {
            String[] childrenNames = node.getChildrenNames();
            for(int i = 0; i < childrenNames.length; i++) {
                PolicyNode subNode = mgr.getNode(m_templateSet.getDataPath() + TemplateRepository.TEMPLATE_PATH_SEPARATOR 
                    + mgr.encodePath(childrenNames[i]));
                String attributes = ((PolicyNodeImpl)subNode).getAttributes(XMLStreamable.POLICY_SCHEMA);               
                if(subNode.getName().equals(originalName) && attributes.indexOf("oor:op=\"replace\"") != -1) {
                    subNode.setName(newName);
                    break;
                }

            }
        }
        mgr.flushAllChanges();
    }   
    
    protected void addRequiredProperties(String basepath, String index) 
            throws SPIException {
        PolicyMgrHelper mgr = getEditorModel().getProfileHelper();
        Iterator it =  m_templateSet.getPage().getSections().iterator();
        while (it.hasNext()) {
            TemplateSection section = (TemplateSection) it.next();
            Iterator propIt = section.getProperties().iterator(); 
            while(propIt.hasNext()) {
                TemplateProperty property = (TemplateProperty) propIt.next();
                if (property.isAutoStored()) {
                    String nodePath = getAbsolutePath(basepath, 
                                            mgr.encodePath(index), 
                                            property.getDataPath());
                    String value = property.getDefaultValue();
                    int pos = value.indexOf(SectionModel.QUERIED_ID);
                    value = value.substring(0, pos) + index + 
                            value.substring(pos 
                                + SectionModel.QUERIED_ID.length()); 
                    Property prop = mgr.createProperty(nodePath);
                    prop.put(value, SectionModel.getDataType(property));
                }
            }
        }
    }
        
    
    protected String getAbsolutePath(String basepath, String index, 
            String datapath) {
        String absolutePath = datapath;
        if (datapath.startsWith(SectionModel.RELATIVE_PATH)) {
            absolutePath = basepath + datapath.substring(1);
            int dynamicPos = absolutePath.indexOf(SectionModel.QUERIED_ID);
            absolutePath = absolutePath.substring(0, dynamicPos) + index
                + absolutePath.substring(dynamicPos 
                    + SectionModel.QUERIED_ID.length());
         }
         return absolutePath;
    }
    
    
    protected ProfileWindowModel getEditorModel() {
        if (mEditorModel == null) {
            mEditorModel = (ProfileWindowModel) RequestManager.getRequestContext().getModelManager().getModel(ProfileWindowModel.class);
        }
        return mEditorModel;
    }
}
