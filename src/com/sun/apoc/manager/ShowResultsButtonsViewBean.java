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
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.sun.apoc.manager.profiles.ProfileWindowModel;
import com.sun.apoc.manager.settings.Section;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCForm;
import com.iplanet.jato.RequestManager;
import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;

public class ShowResultsButtonsViewBean extends ViewBeanBase {

    public static final String PAGE_NAME           = "ReportButtons";
    public static final String DEFAULT_DISPLAY_URL = "/jsp/report/ShowResultsButtons.jsp";
    public static final String CHILD_PRINT_BUTTON   = "PrintButton";
    public static final String CHILD_CLOSE_BUTTON  = "CloseButton";
    public static final String CHILD_FORM          = "Form";
    
    private ProfileWindowModel mEditorModel = null;
    
    public ShowResultsButtonsViewBean() {
    	super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
    }

    protected void registerChildren() {
        registerChild(CHILD_PRINT_BUTTON, CCButton.class);
        registerChild(CHILD_CLOSE_BUTTON, CCButton.class);
        registerChild(CHILD_FORM, CCForm.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_PRINT_BUTTON)) {
            CCButton child = new CCButton(this, name, null);
            return child;
            
        } else if (name.equals(CHILD_CLOSE_BUTTON)) {
            CCButton child = new CCButton(this, name, null);
            return child;
        
        } else if (name.equals(CHILD_FORM)) {
            CCForm child = new CCForm(this, name);
            return child;

        } else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
}    