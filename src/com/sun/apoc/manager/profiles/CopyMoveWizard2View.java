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


package com.sun.apoc.manager.profiles;

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.Model;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.html.OptionList;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.spi.SPIException;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCCheckBox;
import com.sun.web.ui.view.html.CCRadioButton;

import com.sun.web.ui.view.wizard.CCWizardPage;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.web.ui.common.CCI18N;/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class CopyMoveWizard2View extends RequestHandlingViewBase
    implements CCWizardPage, WizardImplDataUtil{

    // The "logical" name for this page.
    public static final String PAGE_NAME = "CopyMoveWizard2View";

    // Child view names (i.e. display fields).

    public static final String CHILD_ACTION_LABEL =
        "SelectActionLabel";
    public static final String CHILD_ACTION =
        "SelectAction";
    public static final String CHILD_ASSIGNMENTS =
        "Assignments";
    public static final String CHILD_ASSIGNMENTS_LABEL =
        "AssignmentsLabel";
    private CCI18N m_I18n;

    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public CopyMoveWizard2View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }

    public CopyMoveWizard2View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        registerChildren();
    }

    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_ACTION_LABEL, CCLabel.class);
        registerChild(CHILD_ACTION, CCRadioButton.class);
        registerChild(CHILD_ASSIGNMENTS, CCCheckBox.class);
        registerChild(CHILD_ASSIGNMENTS_LABEL, CCLabel.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        if (name.equals(CHILD_ACTION_LABEL)) {
            child = (View)new CCLabel(this, name, null);

        } else if (name.equals(CHILD_ACTION)) {
            OptionList options = new OptionList();
            options.add(0, "APOC.wiz.copy.copy", "0");
            options.add(1, "APOC.wiz.copy.move", "1");
            child = (View)new CCRadioButton(this, name, null);
            ((CCRadioButton)child).setOptions(options);   
   //         ((CCRadioButton)child).setValue("0"); 

        } else if (name.equals(CHILD_ASSIGNMENTS)) {
            child = (View)new CCCheckBox(this, name,
            "true", "false", true);
        
        } else if (name.equals(CHILD_ASSIGNMENTS_LABEL)) {
            child = (View)new CCLabel(this, name, null);
            
        } else {
        throw new IllegalArgumentException(
            "CopyMoveWizard2 : Invalid child name [" + name + "]");
        }
        return child;
    }


    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/profiles/CopyMoveWizard2.jsp";
    }

    public String getErrorMsg() {
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        wm.setValue(CHILD_ACTION, (String)((CCRadioButton)getChild(CHILD_ACTION)).getValue());
        wm.setValue(CHILD_ASSIGNMENTS, (String)((CCCheckBox)getChild(CHILD_ASSIGNMENTS)).getValue());
        String emsg = null;
        PolicyManager sourcePmgr = (PolicyManager) RequestManager.getRequest().getSession(false).getAttribute(wm.SOURCE_POLICYMGR);
        try {
            String action = (String)wm.getValue(CHILD_ACTION);
            if (action.equals("1") && isProfileRepositoryReadOnly()) {
                String username = (String)sourcePmgr.getEnvironment().get(EnvironmentConstants.USER_KEY);
                String profile = ((Profile)wm.getWizardValue(wm.SOURCE_PROFILE)).getDisplayName();
                Object[] args = {username, profile};
                emsg = m_I18n.getMessage("APOC.wiz.copy.2.alert", args);
            }
        } catch (SPIException ex) {}
        return emsg;
    }
    
    private boolean isProfileRepositoryReadOnly() throws SPIException {
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        PolicyManager sourcePmgr = (PolicyManager) RequestManager.getRequest().getSession(false).getAttribute(wm.SOURCE_POLICYMGR);
        String[] sourceNames = sourcePmgr.getSources();
        for (int i=0; i < sourceNames.length; i++) {
            if (sourcePmgr.getDefaultProfileRepository(sourceNames[i]).isReadOnly()) {
                return true;
            }
        }
        return false;
    }
}


