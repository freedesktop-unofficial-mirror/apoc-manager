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
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.entities.FindTiledModel;
import com.sun.apoc.manager.entities.FindTiledView;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
public class EntitiesFindResultViewBean extends ViewBeanBase {
    public static final String PAGE_NAME                = "EntitiesFindResult";
    public static final String DEFAULT_DISPLAY_URL      = "/jsp/entities/FindResult.jsp";
    public static final String CHILD_TILED_VIEW         = "EntitiesFindTiledView";
    public static final String CHILD_FORM               = "ResultForm";
    public static final String CHILD_FIND_HREF          = "FindHref";
    public static final String CHILD_FIND_BUTTON        = "FindButton";
    public static final String CHILD_HELP1_TEXT         = "HelpText1";
    public static final String CHILD_HELP2_TEXT         = "HelpText2";
    public static final String CHILD_ENTITYID_HIDDEN    = "EntityId";
    public static final String CHILD_ENTITY_TYPE_HIDDEN = "EntityType";
    public static final String CHILD_FINDSTRING_HIDDEN  = "FindString";
    public static final String CHILD_NONE_FOUND_TEXT    = "NoneFoundMessage";
    private boolean                 m_bFindHref         = false;
    private boolean                 m_bFoundEntities    = false;
    
    public EntitiesFindResultViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        ServletContext context = RequestManager.getRequestContext().getServletContext();
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_TILED_VIEW, FindTiledView.class);
        registerChild(CHILD_FORM, CCForm.class);
        registerChild(CHILD_FIND_HREF, CCHref.class);
        registerChild(CHILD_FIND_BUTTON, CCButton.class);
        registerChild(CHILD_HELP1_TEXT, CCStaticTextField.class);
        registerChild(CHILD_HELP2_TEXT, CCStaticTextField.class);
        registerChild(CHILD_ENTITYID_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_ENTITY_TYPE_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_FINDSTRING_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_NONE_FOUND_TEXT, CCStaticTextField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_TILED_VIEW)) {
            View child = new FindTiledView(this, name);
            return child;
        }
        else if (name.equals(CHILD_FORM)) {
            View child = new CCForm(this, name);
            return child;
        }
        else if (name.equals(CHILD_FIND_HREF)) {
            View child = new CCHref(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_FIND_BUTTON)) {
            View child = new CCButton(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_HELP1_TEXT) || name.equals(CHILD_HELP2_TEXT) || name.equals(CHILD_NONE_FOUND_TEXT)) {
            View child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_ENTITYID_HIDDEN)||name.equals(CHILD_ENTITY_TYPE_HIDDEN)||name.equals(CHILD_FINDSTRING_HIDDEN)) {
            View child = new CCHiddenField(this, name, null);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleFindHrefRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        CCHiddenField entityIdHidden = (CCHiddenField) getChild(CHILD_ENTITYID_HIDDEN);
        CCHiddenField entityTypeHidden = (CCHiddenField) getChild(CHILD_ENTITY_TYPE_HIDDEN);
        CCHref        entityIdHref   = (CCHref) getChild(CHILD_FIND_HREF);
        String        sValue         = (String) entityIdHref.getValue();
        String        sEntityId      = null;
        String        sEntityType    = EnvironmentConstants.USER_SOURCE;
        if (sValue.indexOf("|") != -1) {
            sEntityType = sValue.substring(sValue.indexOf("|") + 1);
            sEntityId = sValue.substring(0, sValue.indexOf("|"));
        } else {
            sEntityId = sValue;
        }
        entityIdHidden.setValue(sEntityId);
        entityTypeHidden.setValue(sEntityType);
        m_bFindHref = true;
        forwardTo(getRequestContext());
    }

    public void handleFindButtonRequest(RequestInvocationEvent event)
    throws ModelControlException, ServletException, IOException {
        CCHiddenField   textField   = (CCHiddenField) getChild(CHILD_FINDSTRING_HIDDEN); 
        CCHiddenField   hiddenField = (CCHiddenField) getChild(CHILD_ENTITYID_HIDDEN); 
        CCHiddenField   hiddenTypeField = (CCHiddenField) getChild(CHILD_ENTITY_TYPE_HIDDEN); 
        FindTiledModel  model       = (FindTiledModel)getModel(FindTiledModel.class);
        model.retrieve((String) hiddenTypeField.getValue(), (String) hiddenField.getValue(), (String) textField.getValue());
        setEntitiesFound(model.getSize()>0);
        forwardTo(getRequestContext());
    }

    public boolean beginHelpDisplay(ChildDisplayEvent event)
    throws ModelControlException {
        return m_bFindHref;
    } 
    
    public boolean beginEntitiesFoundDisplay(ChildDisplayEvent event)
    throws ModelControlException {
        super.beginDisplay(event);
        return (!m_bFindHref && m_bFoundEntities);
    } 

    public boolean beginNoEntitiesFoundDisplay(ChildDisplayEvent event)
    throws ModelControlException {
        return (!m_bFindHref && !m_bFoundEntities);
    } 
    
    public void setEntitiesFound(boolean bFound) {
        m_bFoundEntities = bFound;
    }
}

