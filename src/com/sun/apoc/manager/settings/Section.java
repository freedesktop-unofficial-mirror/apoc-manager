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
import javax.servlet.ServletException;

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.ContainerView;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.PolicySettingsContentViewBean;
import com.sun.apoc.spi.SPIException;
import com.sun.web.ui.model.CCActionTableModelInterface;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.table.CCActionTable;
import com.sun.web.ui.common.CCDebug;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.manager.profiles.ProfileWindowModel;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import java.util.LinkedList;
import com.sun.web.ui.common.CCI18N;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.ProfileWindowSettingsTreeViewBean;
import com.sun.apoc.templates.parsing.TemplateSet;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.sun.web.ui.view.html.CCStaticTextField;


public class Section extends RequestHandlingViewBase  {
    
    public static final String CHILD_SECTION_TABLE      = "PoliciesSectionTable";
    public static final String CHILD_SECTION_TILED_VIEW = "PoliciesSectionTiledView";
    public static final String CHILD_HIDDEN_INPUT       = "UserInput";
    public static final String CHILD_HIDDEN_SAVE_ALL    = "SaveAllPage";
    public static final String CHILD_NEW_BUTTON         = "NewButton";
    public static final String CHILD_DELETE_BUTTON      = "DeleteButton";
    public static final String CHILD_RENAME_BUTTON      = "RenameButton";
   
    
    protected CCActionTableModelInterface m_model = null;
    private ProfileWindowModel mEditorModel = null;
    
    
    public Section(ContainerView parent, 
                         CCActionTableModelInterface model, 
                         String name) {
        super(parent, name);
        if (model == null) {
            throw new IllegalArgumentException("Model must not be null.");
        }
        m_model = model;
        registerChildren();
    }

    
    protected void registerChildren() {
        registerChild(CHILD_SECTION_TABLE, CCActionTable.class);
        registerChild(CHILD_SECTION_TILED_VIEW, SectionTiledView.class);
        m_model.registerChildren(this);
    }
    
    
    protected View createChild(String name) {
        if (name.equals(CHILD_SECTION_TILED_VIEW)) { 
            SectionTiledView child = new SectionTiledView(this, 
                                                m_model, name);
            return child;
            
        } else if (name.equals(CHILD_SECTION_TABLE)) { 
            CCActionTable child = new CCActionTable(this, m_model, name);
            child.setTiledView((ContainerView) 
                        getChild(CHILD_SECTION_TILED_VIEW));
            return child;  

        } else if (m_model.isChildSupported(name)) {
            return m_model.createChild(this, name);
        
        } else {
            throw new IllegalArgumentException( 
                "Invalid child name [" + name + "]");
        }
    }
    
    public void handleNewButtonRequest(RequestInvocationEvent event)
            throws ServletException, IOException {
        CCDebug.trace3("NewButton clicked");
        try {
            String newPropertyName = Toolbox2.getParameter(CHILD_HIDDEN_INPUT);
            ((SetModel) m_model).addEntry(newPropertyName);
            ((SetModel) m_model).update();
        } catch (SPIException ex) {
            getEditorModel().setErrorMessage("Could not add entry", ex);
        }

        CCStaticTextField text = (CCStaticTextField) getViewBean(PolicySettingsContentViewBean.class).getChild(PolicySettingsContentViewBean.CHILD_REFRESH_SCRIPT);
        text.setEscape(false);
        text.setValue("setCurrentCategory('" + getEditorModel().getSelectedCategory() + "');refreshNavigationTree();");        
        ProfileWindowSettingsTreeViewBean.resetTreeModel();
        String doFullPageSave = Toolbox2.getParameter(CHILD_HIDDEN_SAVE_ALL);
        if (doFullPageSave != null && doFullPageSave.equals("true")) {
            try {
                getViewBean(PolicySettingsContentViewBean.class).mapRequestParameters(getRequestContext().getRequest());
                ((PolicySettingsContentViewBean)getViewBean(PolicySettingsContentViewBean.class)).handleSaveButtonRequest(event);
            } catch (ModelControlException ex) {
                CCDebug.trace3(ex.toString());
            }
        } else {
            getViewBean(PolicySettingsContentViewBean.class).forwardTo(RequestManager.getRequestContext());
        }
    }

    public void handleRenameButtonRequest(RequestInvocationEvent event)
            throws ServletException, IOException {
        CCDebug.trace3("RenameButton clicked");
            try {
            String newPropertyName = Toolbox2.getParameter(CHILD_HIDDEN_INPUT);
            CCActionTable child = (CCActionTable) getChild(CHILD_SECTION_TABLE);
            child.restoreStateData();
            LinkedList changedNodes = new LinkedList();
            for(int i = 0; i < m_model.getNumRows(); i++) {
                if (m_model.isRowSelected(i)) {
                    m_model.setRowIndex(i);
                    String originalPropertyName = (String)m_model.getValue(SetModel.ENTRY_NAME);
                    m_model.setRowSelected(i, false);
                    ((SetModel) m_model).renameEntry(i, originalPropertyName, newPropertyName);
                    break;
                }
            }
            ((SetModel) m_model).update();
        } catch (SPIException ex) {
            getEditorModel().setErrorMessage("Could not add entry", ex);
        } catch (ModelControlException ex) {
            CCDebug.trace1("Error removing entry", ex);
        }

        CCStaticTextField text = (CCStaticTextField) getViewBean(PolicySettingsContentViewBean.class).getChild(PolicySettingsContentViewBean.CHILD_REFRESH_SCRIPT);
        text.setEscape(false);
        text.setValue("setCurrentCategory('" + getEditorModel().getSelectedCategory() + "');refreshNavigationTree();");        
        ProfileWindowSettingsTreeViewBean.resetTreeModel();
        String doFullPageSave = Toolbox2.getParameter(CHILD_HIDDEN_SAVE_ALL);
        if (doFullPageSave != null && doFullPageSave.equals("true")) {
            try {
                getViewBean(PolicySettingsContentViewBean.class).mapRequestParameters(getRequestContext().getRequest());
                ((PolicySettingsContentViewBean)getViewBean(PolicySettingsContentViewBean.class)).handleSaveButtonRequest(event);
            } catch (ModelControlException ex) {
                CCDebug.trace3(ex.toString());
            }
        } else {
            getViewBean(PolicySettingsContentViewBean.class).forwardTo(RequestManager.getRequestContext());
        }
    }
        
    
    
    public void handleDeleteButtonRequest(RequestInvocationEvent event)
            throws ServletException, IOException {
        CCDebug.trace3("DeleteButton clicked");
        try {
            CCActionTable child = (CCActionTable) getChild(CHILD_SECTION_TABLE);
            child.restoreStateData();
            LinkedList changedNodes = new LinkedList();
            for(int i = 0; i < m_model.getNumRows(); i++) {
                if (m_model.isRowSelected(i)) {
                    CCDebug.trace1("Deleting row " + i);
                    m_model.setRowSelected(i, false);
                    ((SetModel) m_model).removeEntry(i);   
                }
            }
            m_model.setRowSelected(false);
            ((SetModel) m_model).update();
        } catch (SPIException ex) {
            getEditorModel().setErrorMessage("Could not remove entry", ex);
        } catch (ModelControlException ex) {
            CCDebug.trace1("Error removing entry", ex);
        }

        CCStaticTextField text = (CCStaticTextField) getViewBean(PolicySettingsContentViewBean.class).getChild(PolicySettingsContentViewBean.CHILD_REFRESH_SCRIPT);
        text.setEscape(false);
        text.setValue("setCurrentCategory('" + getEditorModel().getSelectedCategory() + "');refreshNavigationTree();");  
        ProfileWindowSettingsTreeViewBean.resetTreeModel();
        try {
            getViewBean(PolicySettingsContentViewBean.class).mapRequestParameters(getRequestContext().getRequest());
            ((PolicySettingsContentViewBean)getViewBean(PolicySettingsContentViewBean.class)).handleSaveButtonRequest(event);
        } catch (ModelControlException ex) {
            CCDebug.trace3(ex.toString());
        
        }
    }
    
 /*   public void handleToggleButtonRequest(RequestInvocationEvent event)
            throws ServletException, IOException {
        CCDebug.trace3("ToggleButton clicked");
        try {
            CCActionTable child = (CCActionTable) getChild(CHILD_SECTION_TABLE);
            child.restoreStateData();
            LinkedList changedNodes = new LinkedList();
            for(int i = 0; i < m_model.getNumRows(); i++) {
                if (m_model.isRowSelected(i)) {
                    m_model.setRowSelected(i, false);
                    ((SetModel) m_model).addRemoveEntry(i);   
                }
            }
            ((SetModel) m_model).update();
        } catch (SPIException ex) {
            getEditorModel().setErrorMessage("Could not add a remove set entry", ex);
        } catch (ModelControlException ex) {
            CCDebug.trace1("Error addding remove set entry", ex);
        }
        getViewBean(PolicySettingsContentViewBean.class).forwardTo(getRequestContext());
    }*/
    
    public boolean beginNewButtonDisplay(ChildDisplayEvent event) 
            throws ModelControlException {
        CCButton button = (CCButton) getChild(CHILD_NEW_BUTTON);
        if (m_model instanceof SetModel) {
	    TemplateSet set = ((SetModel) m_model).getTemplateSet();
            String labelPopup = set.getLabelPopup();
            labelPopup = getLocalizedSetLabel(labelPopup, set.getResourceBundle());
            button.setExtraHtml("onClick=\"javascript: return requestSetPropertyName(this,'" +  labelPopup + "', 'false', document);\"");        
        }
        return true;
    }

    public boolean beginDeleteButtonDisplay(ChildDisplayEvent event) 
            throws ModelControlException {
        CCButton button = (CCButton) getChild(CHILD_DELETE_BUTTON);
        if (m_model instanceof SetModel) {
            button.setDisabled(true);
        }
        return true;
    }
 
    public boolean beginRenameButtonDisplay(ChildDisplayEvent event) 
            throws ModelControlException {
        CCButton button = (CCButton) getChild(CHILD_RENAME_BUTTON);
        if (m_model instanceof SetModel) {
            button.setDisabled(true);
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);        
            String prompt = i18n.getMessage("APOC.policies.rename.set") ;  
            button.setExtraHtml("onClick=\"javascript: return requestSetPropertyName(this,'" +  prompt + "', 'true', document);\"");        
        }
        return true;
    }    
    
    protected String getLocalizedSetLabel(String label, String resourceBundle) {
        if (label == "" || label == null) {
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);        
            label = i18n.getMessage("APOC.policies.name.question") ;
        } else {
            ResourceRepository resources = ResourceRepository.getDefaultRepository();
            String localizedLabel = resources.getValidMessage(
                                        label, 
                                        resourceBundle, 
                                        RequestManager.getRequest());
            if (localizedLabel != null) {
                label = localizedLabel;
            }
        }
        return label;
    }    

    protected ProfileWindowModel getEditorModel() {
        if (mEditorModel == null) {
            mEditorModel = (ProfileWindowModel) getModel(ProfileWindowModel.class);
        }
        return mEditorModel;
    }
}
