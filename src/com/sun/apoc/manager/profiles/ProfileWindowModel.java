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

import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.RequestParticipant;
import com.iplanet.jato.model.DefaultModel;

import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.ProfileWindowSettingsTreeViewBean;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.apoc.manager.settings.PolicyMgrHelper;
import com.sun.apoc.templates.parsing.TemplateElement;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.profiles.ProfileRepository;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.view.alert.CCAlert;
public class ProfileWindowModel extends DefaultModel implements RequestParticipant {
    
    // identifiers for the different tabs
    public static final String SETTINGS_TAB  = "1";
    public static final String ASSIGNEES_TAB = "2";
    public static final String GENERAL_TAB   = "3";
    public static final String SUMMARY_TAB   = "4";
    public static final String ADVANCED_TAB  = "5";

    // key for storing the currently selected tab
    public static final String SELECTED_TAB          = "SelectedEditorTab";
    
    // key for storing the currently selected profile
    public static final String SELECTED_PROFILE      = "ProfileWindowFrameset.SelectedProfile";
    
    // key for storing the currently selected category
    public static final String CATEGORY_HREF_SELECTION = "PolicySettingsContent.CategoryHref";
    
    // key for storing the profile manager
    public static final String PROFILE_HELPER        = "ProfileHelper";
    
    // keys for requesting the creation of a new profile
    public static final String NEW_ASSIGNED_PROFILE  = "NewAssignedProfile";
    public static final String NEW_PROFILE           = "NewProfile";
    public static final String NEW_PROFILE_ORG       = "NewProfileOrg";
    public static final String NEW_PROFILE_DOMAIN    = "NewProfileDomain";
    
    // keys for storing error/info messages
    public static final String ALERT_TXT     = "AlertText";
    public static final String ALERT_DETAILS = "AlertDetails";
    public static final String ALERT_TYPE    = "AlertType";
    
    // private members
    private String mSelectedTab = null;
    private Profile mProfile = null;
    private String mSelectedCategory = null;
    private HttpServletRequest mRequest = null;
    private HttpSession mSession = null;
    private String mAnchor = "";
    
    /**
     * Sets the desired tab in the profile editor. The tab selection will be
     * stored as a session attribute.
     * 
     * @param tabId defines the identifier of the tab. This can be SUMMARY_TAB,
     *              SETTINGS_TAB, etc. 
     */
    public void setSelectedTab(String tabId) {
        mSession.setAttribute(SELECTED_TAB, tabId);
        mSelectedTab = tabId;
    }
    
    /**
     * Returns the currently selected tab.
     * 
     * @return the tab id such as SUMMARY_TAB, SETTINGS_TAB, etc.
     */
    public String getSelectedTab() {
        return mSelectedTab;
    }
    
    /**
     * Helper method to determine the currently selected tab via request and
     * session parameters. The method will first search for a SELECTED_TAB 
     * parameter in the request. If this is not available, it will lookup the
     * information in the session. As a last fallback the profile general tab
     * will be used as default.
     * 
     * @return the selected tab id
     */
    protected String determineSelectedTab() {
        String selectedTab = (String) mRequest.getParameter(SELECTED_TAB);
        if (selectedTab == null) {
            selectedTab = (String) mSession.getAttribute(SELECTED_TAB);
        } 
        if (selectedTab == null) {
            selectedTab = GENERAL_TAB;
        }
        return selectedTab;
    }
    
    /** 
     * Sets the path of the currently selected settings category.
     * 
     * @param path
     */
    public void setSelectedCategory(String path) {
        mSession.setAttribute(Constants.TEMPLATE_PATH, path);
        mSelectedCategory = path;
    }
    
    /**
     * Returns the path of the currently selected settings category
     * @return
     */
    public String getSelectedCategory() {
        return mSelectedCategory;
    }
 
    /**
     * Determines the currently selected settings category. This method will
     * first look at the request parameter and afterwards at the session 
     * attribute. The last fallback is to return an empty string.
     * 
     * @return the path of the selected category
     */
    public String determineSelectedCategory() {
        String selectedCategory = (String) mRequest.getParameter(CATEGORY_HREF_SELECTION);
        // if the category parameter is present then we are switching from another 
        // view (i.e. settings summary) so reset the categories tree
        if (selectedCategory != null) {
            ProfileWindowSettingsTreeViewBean.resetTreeModel();
        } else {
           selectedCategory = (String) mSession.getAttribute(Constants.TEMPLATE_PATH);
        }
        if (selectedCategory == null) {
            selectedCategory = "";
        }
        return selectedCategory;
    }
    
    /**
     * Sets the profile that is loaded/opened via the profile editor.
     * 
     * @param profile
     */    
    public void setProfile(Profile profile) {
        Profile previousProfile = (Profile) mSession.getAttribute(SELECTED_PROFILE);
        if (previousProfile == null) {
            resetNavigationState();
        } else if (profile == null) {
            resetNavigationState();
        } else if (!profile.getId().equals(previousProfile.getId())) {
            resetNavigationState();
        }
        
        mSession.setAttribute(PROFILE_HELPER, new PolicyMgrHelper(Toolbox2.getPolicyManager(), profile));
        mSession.setAttribute(SELECTED_PROFILE, profile);
        mProfile = profile;
    }
    
    protected void resetNavigationState() {
        ProfileWindowSettingsTreeViewBean.resetTreeModel();
        String selectedCategory  = determineSelectedCategory();
        if (selectedCategory != null && selectedCategory.indexOf(TemplateRepository.SET_PREFIX) != -1) {
            selectedCategory = selectedCategory.substring(0, selectedCategory.indexOf(TemplateRepository.SET_PREFIX) - 1);
            setSelectedCategory(selectedCategory);
        }
//      setSelectedCategory("");
    }
    
    /**
     * Returns the currently opened profile.
     * 
     * @return
     */
    public Profile getProfile() {
        return mProfile;
    }
    
    /** 
     * Helper method that retrieves the currently selected profile from a 
     * request parameter. This might also result in the creation of a brand 
     * new profile.
     *  
     * @return the currently edited profile
     */
    protected Profile determineProfile() {
        Profile selectedProfile = null;
        try {
            // detect currenty selected profile via submitted profile id  
            String selectedProfileId = (String) mRequest.getParameter(SELECTED_PROFILE);
            if (selectedProfileId == null) {

                // no profile id specified, take now a look into the session
                selectedProfile = (Profile) mSession.getAttribute(SELECTED_PROFILE);

            } else {
                PolicyManager mgr = Toolbox2.getPolicyManager();
                
                // profile id has been specified. First, check, if we need to 
                // create a new profile
                if ( (selectedProfileId.equals(NEW_ASSIGNED_PROFILE) || 
                     (selectedProfileId.startsWith(NEW_PROFILE))) ) {
                    setSelectedTab(GENERAL_TAB);
                    
                    // create new profile
                    if (selectedProfileId.equals(NEW_PROFILE_ORG)) {
                        selectedProfileId = createNewProfile(mgr.getRootEntity(EnvironmentConstants.USER_SOURCE), false);
                    } else if (selectedProfileId.equals(NEW_PROFILE_DOMAIN)) {
                        selectedProfileId = createNewProfile(mgr.getRootEntity(EnvironmentConstants.HOST_SOURCE), false);
                    } else {
                        Entity selectedEntity = Toolbox2.getSelectedEntity();
                        boolean autoAssign = selectedProfileId.equals(NEW_ASSIGNED_PROFILE);
                        selectedProfileId = createNewProfile(selectedEntity, autoAssign);
                    }
                }
                
                // retrieve the selected profile 
                selectedProfile = mgr.getProfile(selectedProfileId);
            }
        } catch (SPIException ex) {
            setErrorMessage("Could not create new profile.", ex);
        }
        return selectedProfile;
    }
    
    /**
     * Creates a brand new profile in the top most available repository. The
     * name of the profile is set to "New Profile" or "NewProfile1", etc.
     * 
     * @param automaticAssignment if set to true, the new profile will 
     * automatically be assigned to the currently selected entity.
     * 
     * @return the id of the newly created profile
     * @throws SPIException  
     */
    public String createNewProfile(Entity selectedEntity, boolean automaticAssignment) 
            throws SPIException {
        Profile newProfile = null;
        CCI18N i18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);  
        String domainsString = i18n.getMessage("APOC.profilewnd.lc_domains");
        String hostsString = i18n.getMessage("APOC.profilewnd.lc_hosts");
        String orgsString = i18n.getMessage("APOC.profilewnd.lc_orgs");
        String usersString = i18n.getMessage("APOC.profilewnd.lc_users");
        // determine applicability of new profile
        Applicability applicability = Applicability.USER;
         if (selectedEntity.getPolicySourceName().equals(EnvironmentConstants.HOST_SOURCE)) {
            applicability = Applicability.HOST;
            Object[] args = {domainsString, hostsString};
            setInfoMessage(i18n.getMessage("APOC.profilewnd.creating_new"), i18n.getMessage("APOC.profilewnd.general.new_profile_instructions", args));
        } else {
            applicability = Applicability.USER;
            Object[] args = {orgsString, usersString};
            setInfoMessage(i18n.getMessage("APOC.profilewnd.creating_new"), i18n.getMessage("APOC.profilewnd.general.new_profile_instructions", args));         
        } 
        
        // determine the top most repository with write access for the 
        // currently logged in user
        ProfileRepository writeableRepository = null;
        for (Entity entity = selectedEntity; entity != null; entity = entity.getParent()) {
            ProfileRepository repository = entity.getProfileRepository();
            if (!repository.isReadOnly()) {
                writeableRepository = repository;
            }
        }
        
        // define a profile name
        String profileName = generateDefaultProfileName(writeableRepository, applicability);
        
        // create the new profile 
        newProfile = writeableRepository.createProfile(profileName, applicability);
        
        // and eventually assign it to selected entity
        if (automaticAssignment) {
            selectedEntity.assignProfile(newProfile);
        }
        NavigationModel navigationModel = (NavigationModel) RequestManager.getRequestContext().getModelManager().getModel(
        NavigationModel.class, "NavTree", true, true);

        navigationModel.setAssignedState(selectedEntity, true);
        return newProfile.getId();
    }
    
    /**
     * Helper method that tries to generate a default profile name that is not
     * already used by other profiles in the same repository and with the same
     * applicability. The method starts with "NewProfile" as name and will then
     * continue with "NewProfile1", "NewProfile2", etc. until a new name is 
     * found.
     * 
     * @param repository The repository for the new profile
     * @param applicability
     * @return generated new profile name
     * @throws SPIException
     */
    protected String generateDefaultProfileName(
            ProfileRepository repository, Applicability applicability) 
            throws SPIException {
        String defaultProfileName = "NewProfile";
        
        // collect the names of the existing profiles
        HashSet usedDisplayNames = new HashSet();
        Iterator it = repository.getProfiles(applicability);
        while(it.hasNext()) {
            Profile profile = (Profile) it.next();
            usedDisplayNames.add(profile.getDisplayName());
        }
        
        // try to find a profile name that is not used yet 
        String profileName = defaultProfileName;
        for(int i = 1; usedDisplayNames.contains(profileName); i++) {
            StringBuffer buffer = new StringBuffer(defaultProfileName);
            buffer.append(i);
            profileName = buffer.toString();
        }
        return profileName;
    }
    
    /**
     * Returns the scope of the settings.
     * 
     * @return
     * @throws SPIException
     */
    public byte getSettingsScope() {
        if (getProfile() != null && (Applicability.USER.equals(getProfile().getApplicability()))) {
            return TemplateElement.USER_SCOPE;
        } else {
            return TemplateElement.HOST_SCOPE;
        }
    }
    
    /** 
     * Checks, if the currently logged in user can edit the opened/selected 
     * profile
     * 
     * @return true if the profile is read-only.
     */
    public boolean isReadOnlyProfile() {
        boolean result = true;
        try {
            result = getProfile().getProfileRepository().isReadOnly();
        } catch (SPIException ex) {
            // ignore the exception for now
        }
        return result;
    }
    
    /**
     * Sets the message text and details for an info
     */
    public void setInfoMessage(String message, String details) {
        mSession.setAttribute(ALERT_TXT, message);
        mSession.setAttribute(ALERT_DETAILS, details);
        mSession.setAttribute(ALERT_TYPE, CCAlert.TYPE_INFO);
    }
    
    /**
     * Sets the message text and details for an alert 
     */
    public void setErrorMessage(String message, String details) {
        mSession.setAttribute(ALERT_TXT, message);
        mSession.setAttribute(ALERT_DETAILS, details);
        mSession.setAttribute(ALERT_TYPE, CCAlert.TYPE_ERROR);
    }
    
    /** 
     * Set error message with details from provided exception
     */
    public void setErrorMessage(String message, SPIException ex) {
        setErrorMessage(message, ex.getLocalizedMessage(mRequest.getLocale()));
    }
    
    public void setAnchor(String anchor) {
        mSession.setAttribute("ANCHOR", anchor);
    }
       
    /** 
     * Returns true, if any errors occured
     */
    public boolean hasAlert() {
        return getAlertMessage() != null;
    }
    
    /**
     * Returns the alert message
     */
    public String getAlertMessage() {
        return (String) mSession.getAttribute(ALERT_TXT);
    }
    
    /** 
     * Return the type of the alert
     */
    public String getAlertType() {
        return (String) mSession.getAttribute(ALERT_TYPE);
    }
    
    /**
     * Returns the alert details
     */
    public String getAlertDetails() {
        return (String) mSession.getAttribute(ALERT_DETAILS);
    }    
    
    /**
     * Retrieves the profile manager from the session
     * 
     * @return the profile manager
     */
    public PolicyMgrHelper getProfileHelper() {
        return (PolicyMgrHelper) mSession.getAttribute(PROFILE_HELPER);
    }

    public String getAnchor() {
        return (String) mSession.getAttribute("ANCHOR");
    }

       
    /**
     * This method just implements the RequestParticipant interface. It is 
     * automatically called by the JATO framework and initializes the model.
     * As part of the initialization the selected profile will be retrieved (or 
     * eventually a new one will be created) and the selected tab is determined.
     */
    public void setRequestContext(RequestContext requestContext) {
        mRequest = requestContext.getRequest();
        mSession = mRequest.getSession();
        setProfile(determineProfile());
        setSelectedTab(determineSelectedTab());
        setSelectedCategory(determineSelectedCategory());
    }
}
