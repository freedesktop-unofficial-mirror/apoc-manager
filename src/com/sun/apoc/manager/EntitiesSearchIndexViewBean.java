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
import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.view.html.CCStaticTextField;


public class EntitiesSearchIndexViewBean extends ViewBeanBase {
    public static final String PAGE_NAME              = "EntitiesSearchIndex";
    public static final String DEFAULT_DISPLAY_URL    = "/jsp/entities/SearchIndex.jsp";
    public static final String CHILD_CONTEXT_TEXT     = "JSContext";
    public static final String CHILD_RESTRICTED_ENTITY= "JSRestrictedEntity";
    public static final String CHILD_RESTRICTED_TYPE  = "JSRestrictedEntityType";
    public static final String CHILD_TITLE_STATIC     = "SearchTitle";
    
    public EntitiesSearchIndexViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_CONTEXT_TEXT, CCStaticTextField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_CONTEXT_TEXT)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_RESTRICTED_ENTITY)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_RESTRICTED_TYPE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_TITLE_STATIC)) {
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            String sSearch = i18n.getMessage("APOC.search.search");
            CCStaticTextField child = new CCStaticTextField(this, CHILD_TITLE_STATIC, sSearch);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        String sContext = Toolbox2.getQuery("ContextId");
        if (sContext.length()==0) {
            sContext = (String) getSession().getAttribute(Constants.POLICY_MANAGER_NAME);
        }
        CCStaticTextField context  = (CCStaticTextField) getChild(CHILD_CONTEXT_TEXT);
        context.setValue(sContext);
        String sRestrictedEntity = Toolbox2.getQuery("RestrictedEntity");
        CCStaticTextField restrictedEntity  = (CCStaticTextField) getChild(CHILD_RESTRICTED_ENTITY);
        restrictedEntity.setValue(Toolbox2.encode(sRestrictedEntity));
        String sRestrictedEntityType = Toolbox2.getQuery("RestrictedEntityType");
        CCStaticTextField restrictedEntityType  = (CCStaticTextField) getChild(CHILD_RESTRICTED_TYPE);
        restrictedEntityType.setValue(Toolbox2.encode(sRestrictedEntityType));
    }
}
