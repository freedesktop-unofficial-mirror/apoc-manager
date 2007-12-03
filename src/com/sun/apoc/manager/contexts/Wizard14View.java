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
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingViewBase;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCRadioButton;
import com.iplanet.jato.view.html.OptionList;

import com.sun.web.ui.view.wizard.CCWizardPage;

import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Constants;

/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class Wizard14View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil {

    // The "logical" name for this page.
    public static final String PAGE_NAME = "Wizard14View";

    public static final String CHILD_UPDATE_LABEL =
        "MigrateOverwriteLabel";
    public static final String CHILD_MIGRATE =
        "Migrate";
    private CCI18N m_I18n;
    
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public Wizard14View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }

    public Wizard14View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        registerChildren();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Child manipulation methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_UPDATE_LABEL, CCLabel.class);
        registerChild(CHILD_MIGRATE, CCRadioButton.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        if (name.equals(CHILD_UPDATE_LABEL)) {
            child = (View)new CCLabel(this,
            name, m_I18n.getMessage("APOC.wiz.14.existing"));
        } else if (name.equals(CHILD_MIGRATE)) {
            OptionList options = new OptionList();
            options.add(0, m_I18n.getMessage("APOC.wiz.6.yes"), "0");
            options.add(1, m_I18n.getMessage("APOC.wiz.6.no"), "1");
            child = (View)new CCRadioButton(this, name, null);
            ((CCRadioButton)child).setOptions(options);   
            ((CCRadioButton)child).setValue("0"); 
        } else {
        throw new IllegalArgumentException(
            "Wizard14View : Invalid child name [" + name + "]");
        }
        return child;
    }


    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/contexts/Wizard14.jsp";
    }


    public String getErrorMsg() {
        return null;
    }
    
}


