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
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.ProfileWindowViewBean;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.model.CCPropertySheetModel;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.html.CCTextArea;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.propertysheet.CCPropertySheet;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import javax.servlet.ServletException;

public class GeneralSettingsViewBean extends RequestHandlingViewBase {
    
    public static final String CHILD_PAGETITLE          = "PageTitle";
    public static final String CHILD_PROPERTIES         = "Properties";
    public static final String CHILD_HIDDEN_NAME        = "OriginalName";
    public static final String CHILD_HIDDEN_COMMENT     = "OriginalComment";
    public static final String CHILD_HIDDEN_IS_NEW_PROFILE = "IsNewProfile";
    public static final String NAME                     = "Name";
    public static final String COMMENT                  = "Comment";
    public static final String AUTHOR                   = "Author";
    public static final String LAST_MODIFIED            = "ModifiedDate";
    
    private CCPageTitleModel m_titleModel  = null;
    private ProfileWindowModel mEditorModel = null;
    private CCPropertySheetModel m_propertySheetModel = null;
    
    public GeneralSettingsViewBean(View parent, String name) {
        super(parent, name);
        registerChildren();
    }
    
    public void handleSaveButtonRequest(RequestInvocationEvent event)
            throws ServletException, IOException {
        Profile profile = getEditorModel().getProfile();
        if (profile != null) {
            try {
                String  sName   = (String)((CCTextField)getChild(NAME)).getValue();
                String  sComment= (String)((CCTextArea)getChild(COMMENT)).getValue();
                if (!profile.getDisplayName().equals(sName)) {
                    profile.setDisplayName(sName);
                }
                profile.setComment(sComment);
            } catch (SPIException se) {
                getEditorModel().setErrorMessage("Error storing new settings.", se);
            }
        }
        ((CCHiddenField)getChild(CHILD_HIDDEN_IS_NEW_PROFILE)).setValue("false");
        getRootView().forwardTo(getRequestContext());
    }    
    
    public void handleCloseButtonRequest(RequestInvocationEvent event)
                throws ServletException, IOException {
        CCStaticTextField child = (CCStaticTextField)((ProfileWindowViewBean)getViewBean(ProfileWindowViewBean.class)).getChild(ProfileWindowViewBean.CHILD_TAB_CHANGE_SCRIPT);
        child.setValue("closeEditorAndRefresh();");
        try {
            String isNewProfile = (String)(((CCHiddenField)getChild(CHILD_HIDDEN_IS_NEW_PROFILE)).getValue());
            if (isNewProfile.equals("true")) {
                Profile profile = getEditorModel().getProfile();
                if (profile != null) {
                    try {
                        Iterator it = profile.getAssignedEntities();
                        while (it.hasNext()) {
                            Entity entity  = (Entity)it.next();
                            entity.unassignProfile(profile);
                        }
                        profile.getProfileRepository().destroyProfile(profile);
                    } catch (SPIException se) {
                        getEditorModel().setErrorMessage("Error destroying profile.", se);
                    }
                }
                getRootView().forwardTo(getRequestContext());
            } else {
                mapRequestParameters(getRequestContext().getRequest());
                handleSaveButtonRequest(event);
            }
        } catch (ModelControlException ex) {
            CCDebug.trace3(ex.toString());
        } 
    }  
    
    protected void registerChildren() {
        registerChild(CHILD_PAGETITLE, CCPageTitle.class);
        registerChild(CHILD_PROPERTIES, CCPropertySheet.class);
        registerChild(CHILD_HIDDEN_NAME, CCHiddenField.class);
        registerChild(CHILD_HIDDEN_COMMENT, CCHiddenField.class);
        registerChild(CHILD_HIDDEN_IS_NEW_PROFILE, CCHiddenField.class);
        getPageTitleModel().registerChildren(this);
        getPropertiesModel().registerChildren(this);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_PAGETITLE)) {
            CCPageTitle child = new CCPageTitle(this, getPageTitleModel(), name);
            return child;
            
        } else if (name.equals(CHILD_HIDDEN_NAME)) {
            CCHiddenField child = new CCHiddenField(this, name, "");
            return child;
 
        } else if (name.equals(CHILD_HIDDEN_COMMENT)) {
            CCHiddenField child = new CCHiddenField(this, name, "");
            return child;
            
        } else if (name.equals(CHILD_HIDDEN_IS_NEW_PROFILE)) {
            CCHiddenField child = new CCHiddenField(this, name, "false");
            return child;
            
        } else if (name.equals(CHILD_PROPERTIES)) {
            CCPropertySheet child = new CCPropertySheet(this, getPropertiesModel(), name);
            return child;            
        
        } else if (getPageTitleModel().isChildSupported(name)) {
            return getPageTitleModel().createChild(this, name);
            
        } else if (getPropertiesModel().isChildSupported(name)) {
            return getPropertiesModel().createChild(this, name);
            
        } else {
            throw new IllegalArgumentException(
            "Invalid child name [" + name + "]");
        }
    }
    
    protected CCPageTitleModel getPageTitleModel() {
        if (m_titleModel == null) {
            m_titleModel = new CCPageTitleModel(
            RequestManager.getRequestContext().getServletContext(),
            "/jsp/profiles/GeneralSettingsPageTitle.xml");
        }
        return m_titleModel;
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        // initialize the property sheet values
        try {
            Profile profile = getEditorModel().getProfile();
            
            if (profile != null) {
                Date                date        = new Date(profile.getLastModified());
                SimpleDateFormat    format      = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss z");
                String              sDate       = format.format(date);
                Entity              entity      = profile.getProfileRepository().getEntity();
                
                m_propertySheetModel.setValue(NAME, profile.getDisplayName());
                m_propertySheetModel.setValue(COMMENT, profile.getComment());
                m_propertySheetModel.setValue(AUTHOR, profile.getAuthor());
                m_propertySheetModel.setValue(LAST_MODIFIED, sDate);
                ((CCHiddenField)getChild(CHILD_HIDDEN_NAME)).setValue(profile.getDisplayName());
                ((CCHiddenField)getChild(CHILD_HIDDEN_COMMENT)).setValue(profile.getComment());
                if (getEditorModel().hasAlert()) {
                    ((CCHiddenField)getChild(CHILD_HIDDEN_IS_NEW_PROFILE)).setValue("true");
                }
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
        
        boolean bIsReadOnly = getEditorModel().isReadOnlyProfile();
        if (!getEditorModel().hasAlert() && bIsReadOnly) {
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            getEditorModel().setInfoMessage(i18n.getMessage("APOC.policies.info.readonly.access"), "");
        }
        
        CCTextField edit = (CCTextField) getChild("Name");
        CCTextArea  area = (CCTextArea)  getChild("Comment");
        edit.setDisabled(bIsReadOnly);
        area.setDisabled(bIsReadOnly);
        
        // try to reserve some extra space for the alert to prevent scrolling
        // decrease the comment area if necessary
        if (getEditorModel().hasAlert()) {
            area.setRows(4);
        }
    }
    
    public boolean beginPageTitleDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCPageTitle title = (CCPageTitle) getChild(event.getChildName());
        title.getModel().setPageTitleText(Toolbox2.buildProfileTitle("APOC.profilewnd.general.tab"));
        
        Toolbox2.setPageTitleHelp(getPageTitleModel(), "APOC.profilewnd.general.tab", 
                                "APOC.profilewnd.general.help", "gbgcg.html");
       
        return true;
    }
    
    public boolean beginSaveButtonDisplay(ChildDisplayEvent event) 
            throws ModelControlException {        
        // do not show the save button for read-only profiles
        return !getEditorModel().isReadOnlyProfile();
    }
    
    protected CCPropertySheetModel getPropertiesModel() {
        if (m_propertySheetModel == null) {
            m_propertySheetModel = new CCPropertySheetModel(
            RequestManager.getRequestContext().getServletContext(),
            "/jsp/profiles/GeneralSettingsPropertySheet.xml");
        }
        return m_propertySheetModel;
    }
    
    private ProfileWindowModel getEditorModel() {
        if (mEditorModel == null) {
            mEditorModel = (ProfileWindowModel) getModel(ProfileWindowModel.class);
        }
        return mEditorModel;
    }
}

