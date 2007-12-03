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

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.html.StaticTextField;
import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.web.ui.common.CCI18N;

public class ThreepaneViewBean extends ViewBeanBase {
    
    public static final String PAGE_NAME            = "Threepane";
    public static final String DEFAULT_DISPLAY_URL  = "/jsp/Threepane.jsp";
    public static final String CHILD_PRODNAME       = "ProdName";
    public static final String CHILD_DESC           = "Desc";
    public static final String CHILD_NOFRAMES       = "NoFrames";
    
    public ThreepaneViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_PRODNAME, StaticTextField.class);
        registerChild(CHILD_DESC, StaticTextField.class);
        registerChild(CHILD_NOFRAMES, StaticTextField.class);
    }
    
    
    protected View createChild(String name) {
        if (name.equals(CHILD_PRODNAME)) {
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            String prodName = i18n.getMessage("APOC.masthead.altText");
            StaticTextField child = new StaticTextField(this, CHILD_PRODNAME, prodName);
            return child;
            
        } else if (name.equals(CHILD_DESC)) {
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            String prodName = i18n.getMessage("APOC.framelongdesc");
            StaticTextField child = new StaticTextField(this, CHILD_DESC, prodName);
            return child;
            
        } else if (name.equals(CHILD_NOFRAMES)) {
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            String noFrames = i18n.getMessage("APOC.noframes");
            StaticTextField child = new StaticTextField(this, CHILD_NOFRAMES, noFrames);
            return child;
            
        } else {
            throw new IllegalArgumentException(
            "Invalid child name [" + name + "]");
        }
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        // Adds the NavTree to the session if not already there.
        // Eliminates a race between the nav area and the content area view beans.
        // Both need the "NavTree" model and both schedule the model for
        // addition to the session which is done by the ModelManager -
        // unfortunately _after_ the request is done. 
        // As the two requestst for the areas are issued quite simultaneously 
        // the model is created twice because the second request queries 
        // the model before the first request has finished.
        getRequestContext().getModelManager().getModel(NavigationModel.class, "NavTree", true, true);
        super.beginDisplay(event);
    }
}


