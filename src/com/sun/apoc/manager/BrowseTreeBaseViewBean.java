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
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.view.html.OptionList;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.view.html.*;
import com.iplanet.jato.model.ModelControlException;


import javax.servlet.ServletException;



public class BrowseTreeBaseViewBean extends ViewBeanBase {
    public static final String PAGE_NAME                    = "BrowseTreeBase";
    public static final String DEFAULT_DISPLAY_URL          = "/jsp/entities/BrowseTreeBase.jsp";
    public static final String CHILD_FORM                   = "BrowseTreeFormBase";    
    public static final String CHILD_OK_BUTTON              = "OkButton";    
    public static final String CHILD_CANCEL_BUTTON          = "CancelButton";    
        
    public BrowseTreeBaseViewBean(RequestContext rc) throws ModelControlException {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_FORM,           CCForm.class);
        registerChild(CHILD_OK_BUTTON,      CCButton.class);
        registerChild(CHILD_CANCEL_BUTTON,  CCButton.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_FORM)) {
            View child = new CCForm(this, name);
            return child;
        }
        if (name.equals(CHILD_OK_BUTTON)) {
            CCButton child =  new CCButton(this, name, null); 
            return child; 
        }
        if (name.equals(CHILD_CANCEL_BUTTON)) {
            CCButton child =  new CCButton(this, name, null); 
            return child; 
        }        
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        CCButton okBtn = (CCButton)getChild(CHILD_OK_BUTTON);
        String returnPath = (String)getSession().getAttribute(Constants.BROWSE_TREE_RETURN_PATH);
        if (returnPath != null && returnPath.equals("true")) {
            okBtn.setExtraHtml("onClick=\"javascript:top.opener.focus(); top.opener.submitBrowse(top.browsetree.getEntityId(), top.browsetree.getEntityType(), top.browsetree.getEntityPath()); top.window.close(); return false;\"");
        } else {
            okBtn.setExtraHtml("onClick=\"javascript:top.opener.focus(); top.opener.submitBrowse(top.browsetree.getEntityId(), top.browsetree.getEntityType()); top.window.close(); return false;\"");

        }
    }
}


