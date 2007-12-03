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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.templates.parsing.TemplateCategory;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.apoc.manager.profiles.ProfileWindowModel;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.sun.apoc.manager.settings.SectionModel;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.cfgtree.XMLStreamable;
import com.sun.apoc.spi.cfgtree.policynode.PolicyNode;
import com.sun.apoc.spi.cfgtree.policynode.PolicyNodeImpl;
import com.sun.apoc.templates.parsing.TemplatePage;
import com.sun.apoc.templates.parsing.TemplateSection;
import com.sun.apoc.templates.parsing.TemplateSet;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.model.CCNavNode;
import com.sun.web.ui.model.CCNavNodeInterface;
import com.sun.web.ui.model.CCTreeModel;
import com.sun.web.ui.model.CCTreeModelInterface;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.tree.CCDynamicTree;
import com.sun.web.ui.view.tree.CCTreeEventHandlerInterface;
public class ProfileWindowSettingsTreeViewBean extends ViewBeanBase
                    implements CCTreeEventHandlerInterface{

    public static final String PAGE_NAME            = "ProfileWindowSettingsTree";
    public static final String DEFAULT_DISPLAY_URL  = "/jsp/categories/PolicyCategories.jsp";
    public static final String TREE_MODEL           = "TreeModel";
    public static final String CHILD_FORM           = "TreeForm";
    public static final String CHILD_TREE           = "Tree";
    public static final String CHILD_REFRESH_SCRIPT = "RefreshContentScript";
    public static final String CHILD_REFRESH_BUTTON = "RefreshButton";
    public static final String CHILD_JUMPTO_SCRIPT  = "JumpToScript";
    public static final String CHILD_HIDDEN_CURRENT_CATEGORY = "CurrentCategory";
    public static final String CHILD_HIDDEN_TURNER_CLICKED = "TurnerClicked";
    public static final String AFTER_SAVE_CATEGORY = "AfterSaveSelectedCategory"; 
    private CCTreeModel m_treeModel = null;
    private ProfileWindowModel mEditorModel = null;
    private boolean m_isFirstLoad = false;
    
    public ProfileWindowSettingsTreeViewBean() {
    	super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
    }

    protected void registerChildren() {
        registerChild(CHILD_TREE, CCDynamicTree.class);
        registerChild(CHILD_REFRESH_SCRIPT, CCStaticTextField.class);
        registerChild(CHILD_REFRESH_BUTTON, CCButton.class);
        registerChild(CHILD_JUMPTO_SCRIPT, CCStaticTextField.class);
        registerChild(CHILD_FORM, CCForm.class);
        registerChild(CHILD_HIDDEN_CURRENT_CATEGORY, CCHiddenField.class);
        registerChild(CHILD_HIDDEN_TURNER_CLICKED, CCHiddenField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_TREE)) {
            CCDynamicTree child = new CCDynamicTree(this, name, getTreeModel());
            return child;
             
        } else if (name.equals(CHILD_FORM)) {
            CCForm child = new CCForm(this, name);
            return child;
            
        } else if (name.equals(CHILD_REFRESH_BUTTON)) {
            CCButton child = new CCButton(this, name, null);
            return child;
            
         } else if (name.equals(CHILD_JUMPTO_SCRIPT)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, null);
            return child;            

         } else if (name.equals(CHILD_REFRESH_SCRIPT)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, null);
            return child;
            
         } else if (name.equals(CHILD_HIDDEN_CURRENT_CATEGORY)) {
            CCHiddenField child =  new CCHiddenField(this, name, "");
            return child; 
 
         } else if (name.equals(CHILD_HIDDEN_TURNER_CLICKED)) {
            CCHiddenField child =  new CCHiddenField(this, name, "false");
            return child; 
            
        } else {
            throw new IllegalArgumentException( 
                "Invalid child name [" + name + "]");
        }
    }
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        CCHiddenField turnerClickedField = (CCHiddenField) getChild(CHILD_HIDDEN_TURNER_CLICKED);
        String turnerClicked = (String)turnerClickedField.getValue();
        turnerClickedField.setValue("false");
        if (turnerClicked != null && turnerClicked.equals("false")) {
            CCHiddenField currentCategory = (CCHiddenField) getChild(CHILD_HIDDEN_CURRENT_CATEGORY);
            String templatePath = (String)currentCategory.getValue();
            if (m_isFirstLoad == true) {
                templatePath  = getEditorModel().determineSelectedCategory();
               // if (templatePath != null && templatePath.indexOf(TemplateRepository.SET_PREFIX) != -1) {
              //      templatePath = templatePath.substring(0, templatePath.indexOf(TemplateRepository.SET_PREFIX));
              //  }
            }
            if (templatePath != null && templatePath.length() != 0) {
                if (templatePath.indexOf("#") != -1) {
                    templatePath = templatePath.replaceAll("#", "/#");
                }
                // Need to add the anchor node value (i.e. #$SET_NAME )back in at this point for getNode to work
                if (templatePath.indexOf(TemplateRepository.SET_PREFIX) != -1) {
                    int start = templatePath.indexOf(TemplateRepository.SET_PREFIX) + TemplateRepository.SET_PREFIX.length();
                    int end = templatePath.indexOf(TemplateRepository.SET_INDEX_SEPARATOR);
                    String setName = "#" + templatePath.substring(start, end);
                    templatePath = templatePath.replaceAll(TemplateRepository.SET_PREFIX, setName + "/" + TemplateRepository.SET_PREFIX );
                }
                String[] categories = templatePath.split("/");
                CCNavNodeInterface selectedNode = null;
                for(int i = 0; i < categories.length; i++) {
                    selectedNode = getNode(selectedNode, categories[i]);
                }
                if (selectedNode != null) {
                    CCDynamicTree child = (CCDynamicTree) getChild(CHILD_TREE);
                    child.yokeTo(selectedNode);
                    selectedNode.setExpanded(true);
                }
            }     
        }
    }
    
    public void nodeClicked(RequestInvocationEvent event, int nodeID,
           String treeName) {
        
        CCNavNodeInterface node = getTreeModel().getNodeById(nodeID);
        node.setExpanded(true);
        StringBuffer buffer = new StringBuffer();
        String anchor = "";
        getEditorModel().setAnchor(anchor);
        while (node != null) {
            if (node.getValue().indexOf("#") == -1) {
                buffer.insert(0, node.getValue());
                buffer.insert(0, '/');
            } else {
                anchor = node.getValue();
                getEditorModel().setAnchor(anchor);
            }
            node = node.getParent();
        }
        String isSaveRequired = Toolbox2.getParameter("isSaveRequired");
        if (getEditorModel().getSelectedCategory().equals(buffer.toString())) {
            CCStaticTextField anchorText = (CCStaticTextField) getChild(CHILD_JUMPTO_SCRIPT);
            anchorText.setEscape(false);
            anchorText.setValue("jumpTo('"+ anchor + "');");            
        } else {
            CCStaticTextField text = (CCStaticTextField) getChild(CHILD_REFRESH_SCRIPT);
            text.setEscape(false);
            if (isSaveRequired != null && !isSaveRequired.equals("true")) {
                text.setValue("refreshSettingsArea();");   
            }
        }
        if (isSaveRequired != null && !isSaveRequired.equals("true")) {
            getEditorModel().setSelectedCategory(buffer.toString());
            getRequestContext().getRequest().getSession().removeAttribute(ProfileWindowSettingsTreeViewBean.AFTER_SAVE_CATEGORY);
        } else {
            getRequestContext().getRequest().getSession().setAttribute(ProfileWindowSettingsTreeViewBean.AFTER_SAVE_CATEGORY, buffer.toString());
        }
        CCHiddenField currentCategory = (CCHiddenField) getChild(CHILD_HIDDEN_CURRENT_CATEGORY);
        String selectedCat = buffer.toString();
        if (selectedCat.indexOf(TemplateRepository.SET_PREFIX) != -1) {
            currentCategory.setValue(selectedCat);
        } else {
            currentCategory.setValue(selectedCat + anchor);
        }
        getRequestContext().getRequest().getSession().setAttribute(PolicySettingsContentViewBean.FIRST_DISPLAY, "true");
        forwardTo(getRequestContext());        
    }
 
    public void handleRefreshButtonRequest(RequestInvocationEvent event)
            throws ServletException, IOException {
        
/*        CCHiddenField currentCategory = (CCHiddenField) getChild(CHILD_HIDDEN_CURRENT_CATEGORY);
        String templatePath = (String)currentCategory.getValue();
  //      String templatePath = getEditorModel().getSelectedCategory(); 
        if (templatePath != null) {
            // Need to add the anchor node value (i.e. #$SET_NAME )back in at this point for getNode to work
            if (templatePath.indexOf(TemplateRepository.SET_PREFIX) != -1) {
                int start = templatePath.indexOf(TemplateRepository.SET_PREFIX) + TemplateRepository.SET_PREFIX.length();
                int end = templatePath.indexOf(TemplateRepository.SET_INDEX_SEPARATOR);
                String setName = "#" + templatePath.substring(start, end);
                templatePath = templatePath.replaceAll(TemplateRepository.SET_PREFIX, setName + "/" + TemplateRepository.SET_PREFIX );
            }
            String[] categories = templatePath.split("/");
            CCNavNodeInterface selectedNode = null;
            for(int i = 0; i < categories.length; i++) {
                selectedNode = getNode(selectedNode, categories[i]);
            }
            if (selectedNode != null) {
                CCDynamicTree child = (CCDynamicTree) getChild(CHILD_TREE);
                child.yokeTo(selectedNode);
                selectedNode.setExpanded(true);
            }
        }       */ 
        
        forwardTo();
    }

    public CCNavNodeInterface getNode(CCNavNodeInterface parent, String value) {
        List children = null;
        if (parent != null) {
            children = parent.getChildren();
        } else {
            children = getTreeModel().getNodes();
        }
        Iterator it = children.iterator();
        while(it.hasNext()) {
            CCNavNodeInterface node = (CCNavNodeInterface) it.next();
            if (value.equals(node.getValue())) {
                com.sun.web.ui.common.CCDebug.trace1(node.getValue());
                return node;
            }
         }
        return null;
    }
    
    
    public void turnerClicked(RequestInvocationEvent event, int nodeID,
            String treeName) {
        CCHiddenField turnerClicked = (CCHiddenField) getChild(CHILD_HIDDEN_TURNER_CLICKED);
        turnerClicked.setValue("true");
        forwardTo(getRequestContext());
    }
    
    public static void resetTreeModel() {
        RequestManager.getRequest().getSession().setAttribute(TREE_MODEL, null);
    }
    
    protected CCTreeModel getTreeModel() {
        if (m_treeModel == null) {
            m_treeModel = (CCTreeModel) RequestManager.getRequest().getSession().getAttribute(TREE_MODEL);
            if (m_treeModel == null) {
                m_isFirstLoad = true;
                m_treeModel = new CCTreeModel();
                m_treeModel.setType(CCTreeModelInterface.TITLE_TYPE);
                RequestManager.getRequest().getSession().setAttribute(TREE_MODEL, m_treeModel);
                TemplateRepository rep = TemplateRepository.getDefaultRepository();
                ResourceRepository resources = ResourceRepository.getDefaultRepository();
                int id = 0;
                
                HashMap subCategories = rep.getTopLevelCategories();
                Iterator it = subCategories.values().iterator();
                SortedMap children = new TreeMap();
                while(it.hasNext()) {
                    TemplateCategory tiledCategory = (TemplateCategory) it.next();
                    if (tiledCategory.isInScope(getEditorModel().getSettingsScope())) {
                        String catName = resources.getMessage(
                                            tiledCategory.getResourceId(), 
                                            tiledCategory.getResourceBundle(), 
                                            RequestManager.getRequest());
                        children.put(catName, tiledCategory);
                    }
                }
                it = children.keySet().iterator();
                while(it.hasNext()) {
                    String catName = (String) it.next();
                    TemplateCategory tiledCategory = (TemplateCategory) children.get(catName);
                    CCNavNode node = new CCNavNode(id, catName, null, null);
                    node.setOnClick("return displayCategory(this,'" + getCategoryPath(tiledCategory) + "');");
                    node.setValue(tiledCategory.getDefaultName());
                    m_treeModel.addNode(node);
                    id = addChildren(id, node, tiledCategory, resources) + 1;
                }
            }
        }
        return m_treeModel;
    }
    
    public int addChildren(int id, CCNavNode node, TemplateCategory parent, 
            ResourceRepository resources) {
        if (parent.hasSubCategories()) {
            HashMap subCategories = parent.getSubCategories();
            Iterator it = subCategories.values().iterator();
            SortedMap children = new TreeMap();
            while(it.hasNext()) {
                TemplateCategory tiledCategory = (TemplateCategory) it.next();
                if (tiledCategory.isInScope(getEditorModel().getSettingsScope())) {
                    id ++;
                    String catName = resources.getMessage(
                                        tiledCategory.getResourceId(), 
                                        tiledCategory.getResourceBundle(), 
                                        RequestManager.getRequest());
                    children.put(catName, tiledCategory);
                }
            }
            it = children.keySet().iterator();
            while(it.hasNext()) {
                String catName = (String) it.next();
                TemplateCategory tiledCategory = (TemplateCategory) children.get(catName);
                CCNavNode subnode = new CCNavNode(id, catName, null, null);
                subnode.setOnClick("return displayCategory(this,'" + getCategoryPath(tiledCategory) + "');");
                subnode.setValue(tiledCategory.getDefaultName());
                if (!tiledCategory.hasSubCategories()) {
                    subnode.setAcceptsChildren(false);
                    List sections = ((TemplatePage)tiledCategory).getSections();
                    for (int i = 0; i < sections.size(); i++) {
                        TemplateSection section = (TemplateSection)sections.get(i);
                        byte scope = getEditorModel().getSettingsScope();
                        if (section instanceof TemplateSet) {
                            if (((TemplateSet)section).getPage().hasVisibleContent(scope)) {
                                String datapath = ((TemplateSet)section).getDataPath();
                                String setname = ((TemplateSet)section).getDefaultName();
                                String setLabel = resources.getMessage(
                                                        ((TemplateSet)section).getResourceId(), 
                                                        ((TemplateSet)section).getResourceBundle(), 
                                                        RequestManager.getRequest());
                                subnode.setAcceptsChildren(true);
                                id++;
                                CCNavNode setNameNode = new CCNavNode(id, setLabel, null, null);
                                subnode.addChild(setNameNode);
                                setNameNode.setAcceptsChildren(false);
                                setNameNode.setOnClick("return displayCategory(this,'" + getCategoryPath(tiledCategory)+  "#" + setname + "');");
                                setNameNode.setValue("#"  + setname);                                
                                try {
                                    PolicyNode policynode = mEditorModel.getProfileHelper().getNode(datapath);
                                    if ((policynode != null)) {
                                        String[] childrenNames = policynode.getChildrenNames();
                                        for(int j = 0; j < childrenNames.length; j++) {
                                            PolicyNode childNode = mEditorModel.getProfileHelper().getNode(datapath + TemplateRepository.TEMPLATE_PATH_SEPARATOR 
                                                + mEditorModel.getProfileHelper().encodePath(childrenNames[j]));
                                            String attributes = ((PolicyNodeImpl)childNode).getAttributes(XMLStreamable.POLICY_SCHEMA);               
                                            if(attributes.indexOf("oor:op=\"replace\"") != -1) {
                                                setNameNode.setAcceptsChildren(true);
                                                id++;
                                                CCNavNode setEntryNode = new CCNavNode(id, childrenNames[j], null, null);
                                                setNameNode.addChild(setEntryNode);
                                                setEntryNode.setAcceptsChildren(false);
                                                setEntryNode.setOnClick("return displayCategory(this, '" + TemplateRepository.SET_PREFIX  + setname + TemplateRepository.SET_INDEX_SEPARATOR  + childrenNames[j] + "');");
                                                setEntryNode.setValue(TemplateRepository.SET_PREFIX  + setname + TemplateRepository.SET_INDEX_SEPARATOR  + childrenNames[j]);
                                            } 
                                        }
                                    }
                                } catch (SPIException spie) {
                                    CCDebug.trace3(spie.toString());
                                }
                            }
                        }
                    }
                }
                node.addChild(subnode);
                id = addChildren(id, subnode, tiledCategory, resources) + 1;
            }
        }
        return id;
    }
    
    private String getCategoryPath(TemplateCategory category) {
        String path = TemplateRepository.TEMPLATE_PATH_SEPARATOR + category.getDefaultName();
        while (category.getParent() != null && category.getParent().getDefaultName() != TemplateRepository.ROOT_CATEGORY_NAME ) {
            path = TemplateRepository.TEMPLATE_PATH_SEPARATOR + category.getParent().getDefaultName() + path; 
            category = category.getParent();
        } 
        return path;
    }
    
    private ProfileWindowModel getEditorModel() {
        if (mEditorModel == null) {
            mEditorModel = (ProfileWindowModel) getModel(ProfileWindowModel.class);
        }
        return mEditorModel;
    }
}    