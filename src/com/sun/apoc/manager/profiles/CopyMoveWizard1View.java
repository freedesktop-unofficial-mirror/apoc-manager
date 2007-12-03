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
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingViewBase;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCHiddenField;

import com.sun.web.ui.view.wizard.CCWizardPage;

import com.sun.web.ui.common.CCDebug;

import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.Toolbox2;

import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class CopyMoveWizard1View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil{

    // The "logical" name for this page.
    public static final String PAGE_NAME = "CopyMoveWizard1View";

    // Child view names (i.e. display fields).

    public static final String CHILD_CONTEXT_LABEL =
        "SourceContextLabel";
    public static final String CHILD_CONTEXT =
        "SourceContext";
    public static final String CHILD_CONTEXT_BUTTON =
        "ContextBrowseButton";
    public static final String CHILD_PROFILE_LABEL =
        "SourceProfileLabel";
    public static final String CHILD_PROFILE =
        "SourceProfile";
    public static final String CHILD_PROFILE_BUTTON =
        "ProfileBrowseButton";
    public static final String CHILD_HIDDEN_PROFILE =
        "NewSelectedProfile";
    public static final String CHILD_HIDDEN_CONTEXT =
        "SelectedContext";

    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public CopyMoveWizard1View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
    }

    public CopyMoveWizard1View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        registerChildren();
    }

    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_CONTEXT_LABEL, CCLabel.class);
        registerChild(CHILD_CONTEXT, CCStaticTextField.class);
        registerChild(CHILD_CONTEXT_BUTTON, CCButton.class);
        registerChild(CHILD_PROFILE_LABEL, CCLabel.class);
        registerChild(CHILD_PROFILE, CCStaticTextField.class);
        registerChild(CHILD_PROFILE_BUTTON, CCButton.class);
        registerChild(CHILD_HIDDEN_PROFILE, CCHiddenField.class);
        registerChild(CHILD_HIDDEN_CONTEXT, CCHiddenField.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        View child = null;
        if (name.equals(CHILD_CONTEXT_LABEL)) {
            child = (View)new CCLabel(this, name, null);
        } else if (name.equals(CHILD_CONTEXT)) {
            child = (View)new CCStaticTextField(this, name, null);
        } else if (name.equals(CHILD_CONTEXT_BUTTON)) {
            child = (View)new CCButton(this, name, null);
        } else if (name.equals(CHILD_PROFILE_LABEL)) {
            child = (View)new CCLabel(this, name, null);            
        } else if (name.equals(CHILD_PROFILE)) {
            child = (View)new CCStaticTextField(this, name, null);
        } else if (name.equals(CHILD_PROFILE_BUTTON)) {
            child = (View)new CCButton(this, name, null);
        } else if (name.equals(CHILD_HIDDEN_PROFILE)) {
            String profileId = (String)wm.getValue(CopyMoveWizardOverviewView.CHILD_HIDDEN_PROFILE);
            child = (View)new CCHiddenField(this, name, profileId);
        } else if (name.equals(CHILD_HIDDEN_CONTEXT)) {
            child = (View)new CCHiddenField(this, name, null);
        } else {
        throw new IllegalArgumentException(
            "CopyMoveWizard1 : Invalid child name [" + name + "]");
        }
        return child;
    }
    
    public void beginDisplay(DisplayEvent event) 
            throws ModelControlException {

        CCStaticTextField selectedProfile = (CCStaticTextField) getChild(CHILD_PROFILE);
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        PolicyManager sourcePmgr = (PolicyManager) RequestManager.getRequest().getSession(false).getAttribute(wm.SOURCE_POLICYMGR);
        if (sourcePmgr == null) {
            sourcePmgr = Toolbox2.getPolicyManager();
            RequestManager.getRequestContext().getRequest().getSession(true).setAttribute(wm.SOURCE_POLICYMGR, sourcePmgr);
        }
        CCHiddenField hiddenProfile    = (CCHiddenField) getChild(CHILD_HIDDEN_PROFILE);
        String profileId = (String)hiddenProfile.getValue();
        if ((profileId != null) && (profileId.length() != 0)) {
            try {
                Profile profile =  sourcePmgr.getProfile(Toolbox2.decode(profileId));
                selectedProfile.setValue(profile.getDisplayName());
                wm.setValue(wm.SOURCE_PROFILE, profile);
            } catch (SPIException spie) {
                CCDebug.trace3(spie.toString());
            }
        } else {
            wm.setValue(wm.SOURCE_PROFILE, null);
            selectedProfile.setValue("");
        }

        try {
            String context = (String)sourcePmgr.getEnvironment().get(Constants.POLICY_MANAGER_NAME);
            CCStaticTextField contextNameField = (CCStaticTextField)getChild(CHILD_CONTEXT);
            contextNameField.setValue(context);
            CCButton selectContextBtn = (CCButton)getChild(CHILD_CONTEXT_BUTTON);
            selectContextBtn.setExtraHtml("onClick=\"javascript:openSelectContextWindow('" + context + "', '" + wm.SOURCE_POLICYMGR + "');return false;\"");
            CCHiddenField hiddenContext    = (CCHiddenField) getChild(CHILD_HIDDEN_CONTEXT);
            hiddenContext.setValue(context);
        } catch (SPIException e) {
            CCDebug.trace3(e.toString());
        }
    }


    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/profiles/CopyMoveWizard1.jsp";
    }

    public String getErrorMsg() {
        String emsg = null;
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        Profile profile = (Profile)wm.getValue(wm.SOURCE_PROFILE);
        if (profile == null) {
            emsg = "APOC.wiz.copy.1.alert";
        } else {
            try {
                Entity entity = profile.getProfileRepository().getEntity();            
                wm.setValue(wm.PROFILE_HOST_ENTITY, entity);
            } catch (SPIException spie) {
                CCDebug.trace3(spie.toString());
            }
        }

        return emsg;
    }
}


