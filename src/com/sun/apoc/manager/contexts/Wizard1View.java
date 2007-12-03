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


package com.sun.apoc.manager.contexts;

import com.iplanet.jato.model.Model;
import com.iplanet.jato.model.DefaultModel;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.JspDisplayEvent;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.DisplayField;
import com.iplanet.jato.view.RequestHandlingViewBase;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.html.CCCheckBox;

import com.sun.web.ui.view.wizard.CCWizardPage;

import com.sun.web.ui.common.CCDebug;
/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class Wizard1View extends RequestHandlingViewBase
    implements CCWizardPage, WizardImplDataUtil{

    // The "logical" name for this page.
    public static final String PAGE_NAME = "Wizard1View";

    // Child view names (i.e. display fields).

    public static final String CHILD_NAME_LABEL =
        "NameLabel";
    public static final String CHILD_NAME_FIELD =
        "NameField";

    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public Wizard1View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
    }

    public Wizard1View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        registerChildren();
    }

    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_NAME_LABEL, CCLabel.class);
        registerChild(CHILD_NAME_FIELD, CCTextField.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        if (name.equals(CHILD_NAME_LABEL)) {
            child = (View)new CCLabel(this,
                name, "Repository Name: ");
        } else if (name.equals(CHILD_NAME_FIELD)) {
            child = (View)new CCTextField(this, name, null);
        } else {
        throw new IllegalArgumentException(
            "Wizard1View : Invalid child name [" + name + "]");
        }
        return child;
    }


    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/contexts/Wizard1.jsp";
    }

    public String getErrorMsg() {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
	    String value = (String)wm.getWizardValue(CHILD_NAME_FIELD);        
        String emsg = null;
        if (value == null || value.length() == 0) {
            emsg = "Please enter a name for the new configuration repository.";
        }        
        return emsg;
    }
}


