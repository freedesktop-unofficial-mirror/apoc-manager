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

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.apoc.spi.SPIException;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCDropDownMenu;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.tree.CCDynamicTree;
import com.sun.web.ui.view.tree.CCTreeEventHandlerInterface;
import java.io.IOException;
import javax.servlet.ServletException;
public class EntitiesNavigationViewBean extends ViewBeanBase implements CCTreeEventHandlerInterface {
    
    public static final String PAGE_NAME            = "EntitiesNavigation";
    public static final String DEFAULT_DISPLAY_URL  = "/jsp/entities/Navigation.jsp";
    //
    public static final String CHILD_RELOAD         = "ContentConditionalReload";
    public static final String CHILD_ALERT          = "Alert";
    public static final String CHILD_STACKTRACE     = "StackTrace";
    public static final String CHILD_SEARCH_TEXT    = "SearchText";
    public static final String CHILD_SEARCH_BUTTON  = "SearchButton";
    public static final String CHILD_TREE           = "Tree";
    public static final String CHILD_DEFAULT_HREF   = "DefaultHref";
    public static final String CHILD_ENTITY_HREF    = "EntityHref";
    public static final String CHILD_CONTEXT        = "JSContext";
    //
    private String  m_sForcedEntity = null;
    private boolean m_bReload       = false;
    
    public EntitiesNavigationViewBean() throws ModelControlException {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        setRequestContext(RequestManager.getRequestContext());
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_RELOAD, CCStaticTextField.class);
        registerChild(CHILD_CONTEXT, CCStaticTextField.class);
        registerChild(CHILD_SEARCH_TEXT, CCTextField.class);
        registerChild(CHILD_SEARCH_BUTTON, CCButton.class);
        registerChild(CHILD_TREE, CCDynamicTree.class);
        registerChild(CHILD_DEFAULT_HREF, CCHref.class);
        registerChild(CHILD_ENTITY_HREF, CCHref.class);
        registerChild(CHILD_ALERT, CCAlertInline.class);
        registerChild(CHILD_STACKTRACE, CCStaticTextField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_RELOAD)||name.equals(CHILD_CONTEXT)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_SEARCH_TEXT)) {
            CCTextField child = new CCTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_SEARCH_BUTTON)) {
            CCButton child = new CCButton(this, name, null);
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
        else if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_STACKTRACE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }

    public void nodeClicked(RequestInvocationEvent event, int id, String treeName) {
        m_bReload = true;
        getSession().setAttribute(Constants.SELECTED_ENTITY, getModel().getStoredValue(id));
        forwardTo(getRequestContext());
    }
    
    public void turnerClicked(RequestInvocationEvent event, int id, String treeName) {
        try {
            getModel().retrieve(id);
        }
        catch (ModelControlException mce) {
            Toolbox2.prepareErrorDisplay(mce, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE)); 
        }
        forwardTo(getRequestContext());
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
                getSession().setAttribute(Constants.SELECTED_ENTITY, getModel().getStoredValue(nSelected));
                CCDynamicTree tree = (CCDynamicTree) getChild(CHILD_TREE);
                tree.yokeTo(nSelected);
            }
        }
        catch (ModelControlException mce) {
            Toolbox2.prepareErrorDisplay(mce, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE)); 
        }
        
        forwardTo(getRequestContext());
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        getModel().clearDirtyNodeIds();
        Object firstVisit = getSession().getAttribute(Constants.FIRST_VISIT);
        if (firstVisit!=null) {
            getSession().removeAttribute(Constants.FIRST_VISIT);
            try {
                getModel().expandRoot();
            } catch (SPIException spie) {
                CCDebug.trace3(spie.toString());
            } 
        }
    }
    
    public boolean beginContentConditionalReloadDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCStaticTextField script  = (CCStaticTextField) getChild(CHILD_RELOAD);
        
        if (m_bReload){
            script.setValue("parent.content.location.href='/apoc/manager/EntityContent';");
        } else {
            return false;
        }
        return true;
    }
    
    public boolean beginJSContextDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCStaticTextField   context  = (CCStaticTextField) getChild(CHILD_CONTEXT);
        String              sContext = "";
        try {
            sContext = (String) Toolbox2.getPolicyManager().getEnvironment().get(Constants.POLICY_MANAGER_NAME);
       } catch (com.sun.apoc.spi.SPIException se) {
            throw new ServletException(se);
        }
        context.setValue(Toolbox2.encode(sContext));
        return true;
    }
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        return ((alert.getSummary() != null) && (alert.getSummary().length() > 0));
    }
    
    public boolean beginViewMenuDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCDropDownMenu dropdown = (CCDropDownMenu) getChild(event.getChildName());
        dropdown.setValue(getModel().isLeavesFiltered()?"0":"1");
        return true;
    }
    
    protected NavigationModel getModel() {
        return (NavigationModel) getRequestContext().getModelManager().getModel(NavigationModel.class,
        "NavTree", true, true);
    }
}
