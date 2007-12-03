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
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.html.StaticTextField;
import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import java.io.IOException;
import javax.servlet.ServletException;


public class EntitiesFindIndexViewBean extends ViewBeanBase {
    public static final String PAGE_NAME              = "EntitiesFindIndex";
    public static final String DEFAULT_DISPLAY_URL    = "/jsp/entities/FindIndex.jsp";
    public static final String CHILD_ENTITYID_TEXT    = "EntityIdText";
    public static final String CHILD_TITLE_TEXT       = "FindTitle";
    public static final String CHILD_FIND_HREF        = "FindHref";
    public static final String CHILD_FIND_HIDDEN      = "FindModel";
    
    public EntitiesFindIndexViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_ENTITYID_TEXT, StaticTextField.class);
        registerChild(CHILD_FIND_HREF, CCHref.class);
        registerChild(CHILD_FIND_HIDDEN, CCHiddenField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_ENTITYID_TEXT)) {
            String          sUrlParam = Toolbox2.encode(Toolbox2.getParameter(Constants.ENTITY_ID));
            StaticTextField child = new StaticTextField(this, name, (Object) sUrlParam);
            return child;
        }
        else if (name.equals(CHILD_TITLE_TEXT)) {
            StaticTextField child = new StaticTextField(this, name, "APOC.find.title");
            return child;
        }
        else if (name.equals(CHILD_FIND_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_FIND_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleFindHrefRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        forwardTo(getRequestContext());
    }

    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
//        mapRequestParameters(RequestManager.getRequest());
        String          sId     = (String) ((CCHref) getChild(CHILD_FIND_HREF)).getValue();
        int             nId     = Integer.parseInt(sId);
        StaticTextField text    = (StaticTextField) getChild(CHILD_ENTITYID_TEXT);
        CCHiddenField   hidden  = (CCHiddenField) getChild(CHILD_FIND_HIDDEN);
        String          sModel  = (String) hidden.getValue();
        NavigationModel navModel= (NavigationModel) getRequestContext().getModelManager().getModel(NavigationModel.class, sModel, true, true);
        String entityId = Toolbox2.encode(navModel.getStoredEntityId(navModel.getNodeById(nId).getParent().getId()));
        String entityType = Toolbox2.encode(navModel.getStoredEntityType(navModel.getNodeById(nId).getParent().getId()));
        text.setValue(entityId + "|" + entityType);
    }
}

