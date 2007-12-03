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
import com.sun.apoc.spi.profiles.Profile;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.view.html.CCStaticTextField;


public class ProfileWindowFramesetViewBean extends ViewBeanBase {

    public static final String CHILD_TITLE           = "Title";
    public static final String PAGE_NAME             = "ProfileWindowFrameset";
    public static final String DEFAULT_DISPLAY_URL   = "/jsp/profiles/ProfileWindowFrameset.jsp";
    
    private ProfileWindowModel mModel = null; 
    
    
    public ProfileWindowFramesetViewBean() {
    	super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
    }

    protected void registerChildren() {
        registerChild(CHILD_TITLE, CCStaticTextField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_TITLE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, "");
            return child;
            
        } else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    
    public boolean beginTitleDisplay(ChildDisplayEvent event) 
            throws ModelControlException {
        // set window title
        CCStaticTextField child = (CCStaticTextField) getChild(CHILD_TITLE);
        Profile pg = getModel().getProfile();
        if (pg != null) {
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            child.setValue(i18n.getMessage("APOC.profiles.profile") + " " + pg.getDisplayName());
        }
        return true;
    }
    
    public boolean beginTabsOnlyDisplay(ChildDisplayEvent event) 
            throws ModelControlException {
        if (getModel().getProfile() == null) {
            return false;
        } else {
            return getModel().getSelectedTab().equals(ProfileWindowModel.SETTINGS_TAB);
        }
    }
    
    public boolean beginTabsAndButtonsDisplay(ChildDisplayEvent event) 
            throws ModelControlException {
        if (getModel().getProfile() == null) {
            return true;
        } else {       
            return !getModel().getSelectedTab().equals(ProfileWindowModel.SETTINGS_TAB);
        }
    }
    
    private ProfileWindowModel getModel() {
        if (mModel == null) {
            mModel = (ProfileWindowModel) getModel(ProfileWindowModel.class);
        }
        return mModel;
    }
            
    // TODO: Remove this - method has been migrated to new ProfileWindowModel    
    public static com.sun.apoc.manager.settings.PolicyMgrHelper getProfileHelper() {
        ProfileWindowModel mModel = (ProfileWindowModel) com.iplanet.jato.RequestManager.getRequestContext().getModelManager().getModel(ProfileWindowModel.class);
        return mModel.getProfileHelper();
    }
    
    // TODO: Remove this - method has been migrated to new ProfileWindowModel
    public static com.sun.apoc.spi.profiles.Profile getSelectedProfile() {
        ProfileWindowModel mModel = (ProfileWindowModel) com.iplanet.jato.RequestManager.getRequestContext().getModelManager().getModel(ProfileWindowModel.class);
        return mModel.getProfile();
    }
    
    // TODO: Remove this - method has been migrated to new ProfileWindowModel
    public static String SELECTED_PROFILE_ID = ProfileWindowModel.SELECTED_PROFILE;
}    