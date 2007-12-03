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

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.taglib.header.CCHtmlHeaderTag;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCDropDownMenu;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.tree.CCDynamicTree;
import com.sun.web.ui.view.tree.CCTreeEventHandlerInterface;
import java.io.IOException;
import javax.servlet.ServletException;

public class SearchWindowBrowseTreeViewBean extends ViewBeanBase implements CCTreeEventHandlerInterface {

    public static final String CHILD_HEADER                 = "HtmlHeader";  
    public static final String PAGE_NAME                    = "SearchWindowBrowseTree";
    public static final String DEFAULT_DISPLAY_URL          = "/jsp/entities/SearchWindowBrowseTree.jsp";
    public static final String CHILD_STACKTRACE             = "StackTrace";
    public static final String CHILD_FORM                   = "SearchWindowBrowseTreeForm"; 
    public static final String CHILD_TREE                   = "Tree";
    public static final String CHILD_ROOT_ENTITY            = "RootEntity";
    public static final String CHILD_SELECTED_ENTITY        = "SelectedEntity";  
    public static final String CHILD_SELECTED_ENTITY_TYPE   = "SelectedEntityType";        
    public static final String CHILD_SELECTED_ENTITY_PATH   = "SelectedEntityPath"; 
    public static final String CHILD_DEFAULT_HREF           = "DefaultHref";
    public static final String CHILD_ENTITY_HREF            = "EntityHref";
    public static final String CHILD_ROOT_ENTITY_HIDDEN     = "HiddenRootEntity";
    public static final String CHILD_ROOT_ENTITY_TYPE_HIDDEN= "HiddenRootEntityType";
    
    private String m_root                           = null;
    private String m_sContext                       = "";
    private String m_sEntityId                      = "";
    private boolean m_bReload                       = false;    
    private boolean m_isFirstLoad                   = false;
    
    public SearchWindowBrowseTreeViewBean(RequestContext rc) throws ModelControlException {
        super(PAGE_NAME);
        setRequestContext(rc);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
        String sEntityId = (String)RequestManager.getRequest().getSession(false).getAttribute(Constants.SEARCH_WINDOW_PREFIX + Constants.BROWSE_TREE_ENTITY);
        String sEntityType = (String)RequestManager.getRequest().getSession(false).getAttribute(Constants.SEARCH_WINDOW_PREFIX + Constants.BROWSE_TREE_ENTITY_TYPE);
        String sContext = (String)RequestManager.getRequest().getSession(false).getAttribute(Constants.SEARCH_WINDOW_PREFIX + Constants.BROWSE_TREE_CONTEXT);
        if( (sEntityId.length() != 0) ){
            getModel().initTree(sContext, sEntityType, sEntityId);
            m_sContext = sContext;
            m_sEntityId = sEntityId;
            m_isFirstLoad = true;
        }
        RequestManager.getRequest().getSession(false).setAttribute(Constants.SEARCH_WINDOW_PREFIX + Constants.BROWSE_TREE_ENTITY, "");
        RequestManager.getRequest().getSession(false).setAttribute(Constants.SEARCH_WINDOW_PREFIX + Constants.BROWSE_TREE_CONTEXT, "");
    }
    
    protected void registerChildren() {
        registerChild(CHILD_FORM,           CCForm.class);
        registerChild(CHILD_STACKTRACE,     CCStaticTextField.class);
        registerChild(CHILD_SELECTED_ENTITY,CCStaticTextField.class);
        registerChild(CHILD_SELECTED_ENTITY_TYPE,CCStaticTextField.class);          
        registerChild(CHILD_SELECTED_ENTITY_PATH,CCStaticTextField.class);
        registerChild(CHILD_ROOT_ENTITY,CCStaticTextField.class);
        registerChild(CHILD_TREE,           CCDynamicTree.class);        
        registerChild(CHILD_DEFAULT_HREF,   CCHref.class);
        registerChild(CHILD_ENTITY_HREF,    CCHref.class);        
        registerChild(CHILD_ROOT_ENTITY_HIDDEN,CCHiddenField.class); 
        registerChild(CHILD_ROOT_ENTITY_TYPE_HIDDEN,CCHiddenField.class); 
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_FORM)) {
            View child = new CCForm(this, name);
            return child;
        } 
        else if (name.equals(CHILD_STACKTRACE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_SELECTED_ENTITY)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_ROOT_ENTITY)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_SELECTED_ENTITY_TYPE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }    
        else if (name.equals(CHILD_SELECTED_ENTITY_PATH)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }          
        else if (name.equals(CHILD_TREE)) {
            CCDynamicTree child = new CCDynamicTree(this, name, getModel());
            return child;
        }
        else if (name.equals(CHILD_DEFAULT_HREF) || name.equals(CHILD_ENTITY_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }       
        else if (name.equals(CHILD_ROOT_ENTITY_HIDDEN) || name.equals(CHILD_ROOT_ENTITY_TYPE_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void nodeClicked(RequestInvocationEvent event, int id, String treeName) {
        m_bReload = true;
        getSession().setAttribute(Constants.SEARCH_WINDOW_PREFIX + Constants.BROWSE_TREE_SELECTED_ENTITY, getModel().getStoredValue(id));
        forwardTo(getRequestContext());
    }
    
    public void turnerClicked(RequestInvocationEvent event, int id, String treeName) {
        try {
            getModel().retrieve(id);
        }
        catch (ModelControlException mce) {
        }
        forwardTo(getRequestContext());
    }
    
    public void handleEntityHrefRequest(RequestInvocationEvent event) {
        CCHref          href            = (CCHref) getChild(CHILD_DEFAULT_HREF);
        String          sHref           = (String) href.getValue();
        int             nSelected       = 1;
        
        try {
            CCDynamicTree tree = (CCDynamicTree) getChild(CHILD_TREE);
            nSelected = getModel().retrieve(Toolbox2.decode(sHref));
            tree.yokeTo(nSelected);
            forwardTo(getRequestContext());
        }
        catch (ModelControlException mce) {
            CCDebug.trace1("", mce);
        }
    }
    
    public void handleDefaultHrefRequest(RequestInvocationEvent event) {
        CCHref          href            = (CCHref) getChild(CHILD_DEFAULT_HREF);
        String          sHref           = Toolbox2.decode((String) href.getValue());
        int             nOldSelected    = getModel().getSelectedNode().getId();
        int             nSelected       = 1;
        
        try {
            nSelected = getModel().retrieve(false);
            
            if (!sHref.equals("a")) {
                nSelected = getModel().retrieve(sHref);
            }
            
            if (nSelected!=nOldSelected) {
                m_bReload = true;
                CCDynamicTree tree = (CCDynamicTree) getChild(CHILD_TREE);
                tree.yokeTo(nSelected);
            }
        }
        catch (ModelControlException mce) {
            CCDebug.trace1("", mce);
        }
        forwardTo(getRequestContext());
    }
          
    public boolean beginViewMenuDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCDropDownMenu dropdown = (CCDropDownMenu) getChild(event.getChildName());
        dropdown.setValue(getModel().isLeavesFiltered()?"0":"1");
        return true;
    }
    
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        if (m_isFirstLoad) {
            getModel().retrieve(getModel().getSelectedNode().getId());
            getModel().getSelectedNode().setExpanded(true);
        } 
        Entity rootEntity = getModel().getRootEntity();
        if (rootEntity != null) {
            ((CCHiddenField)getChild(CHILD_ROOT_ENTITY_HIDDEN)).setValue(rootEntity.getId());
            ((CCHiddenField)getChild(CHILD_ROOT_ENTITY_TYPE_HIDDEN)).setValue(rootEntity.getPolicySourceName());
        }
        String entityId = getModel().getSelectedNode().getValue();
        String entityType = getModel().getNodeType(getModel().getSelectedNode());
        ((CCStaticTextField)getChild(CHILD_SELECTED_ENTITY)).setValue(entityId);
        ((CCStaticTextField)getChild(CHILD_SELECTED_ENTITY_TYPE)).setValue(entityType);        
        ((CCStaticTextField)getChild(CHILD_SELECTED_ENTITY_PATH)).setValue(Toolbox2.getParentagePath(entityId, entityType, false, true, ">"));        
    }
    
    public boolean beginHtmlHeaderDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        super.endDisplay(event); 
        // Reset state when displaying this page for the first time; 
        // otherwise, state will persist until cookies expire. 
        if (event.getChildName().indexOf(CHILD_HEADER) != -1) {
            CCHtmlHeaderTag tag = (CCHtmlHeaderTag) event.getSource();
            tag.resetFocus(m_isFirstLoad); 
            tag.resetScroll(m_isFirstLoad); 
        }
        return true;
    }   

    protected NavigationModel getModel() {
        NavigationModel model = (NavigationModel)getRequestContext().getRequest().getSession().getAttribute(Constants.SEARCH_WINDOW_PREFIX + Constants.BROWSE_TREE_MODEL);
        if (model == null) {
            try {
                model = new NavigationModel(Constants.SEARCH_WINDOW_PREFIX + Constants.BROWSE_TREE_SELECTED_ENTITY);
                model.expandRoot();
            } catch (ModelControlException mce) {
                CCDebug.trace3(mce.toString());
            } catch (SPIException spie) {
                CCDebug.trace3(spie.toString());
            }
            model.setName(Constants.SEARCH_WINDOW_PREFIX + Constants.BROWSE_TREE_MODEL);
            getRequestContext().getRequest().getSession().setAttribute(Constants.SEARCH_WINDOW_PREFIX + Constants.BROWSE_TREE_MODEL, model);
        }
        return model;
    }
}
