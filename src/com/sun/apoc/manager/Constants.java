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


/**
 *  This class defines the necessary constants for accessing session 
 *  attributes and query parameters
 */
public abstract class Constants {
    
    // 
    // ---------------------- Session attributes -------------------------------
    //
    
    /**
     * The instance of the Java registry proxy (<code>jproxy</code>)
     */
    public static final String POLICY_MANAGER               = "PolicyManager";
    public static final String POLICY_MANAGER_HELPER        = "PolicyMgrHelper";
    public static final String POLICY_MANAGER_USER          = "PolicyManagerUser";
    public static final String POLICY_MANAGER_NAME          = "PolicyManagerName";
    public static final String SELECTED_MAINWINDOW_TAB      = "SelectedMainWindowTab";
    public static final String SELECTED_ENTITY              = "SelectedEntity";
    public static final String SELECTED_SEARCH_ENTITY       = "SelectedSearchEntity";
    public static final String SELECTED_PROFILE             = "SelectedProfile";
    public static final String FIRST_VISIT                  = "FirstVisit";
    public static final String BROWSE_TREE_ENTITY           = "BrowseTreeEntity";
    public static final String BROWSE_TREE_ENTITY_TYPE      = "BrowseTreeEntityType";
    public static final String BROWSE_TREE_CONTEXT          = "BrowseTreeContext";
    public static final String BROWSE_TREE_SHOW_SEARCH      = "BrowseTreeShowSearch";
    public static final String BROWSE_TREE_SELECTED_ENTITY  = "BrowseTreeSelectedEntity";
    public static final String BROWSE_TREE_RETURN_PATH      = "BrowseTreeReturnPath";
    public static final String BROWSE_TREE_MODEL            = "BrowseTree";
    public static final String SEARCH_WINDOW_PREFIX         = "SearchWindow";
    public static final String PROFILE_SEARCH_PROFILE       = "ProfileSearchProfile";
    public static final String REPORT_TYPE                  = "ReportType";
    public static final String POLICY_GROUP_REPORT_TYPE     = "PolicyGroupReport";
    public static final String ENTITY_REPORT_TYPE           = "EntityReport";
    public static final String AUTH_CONTEXTS                = "AuthorizedContexts";
    public static final String SYNC_ENVIRONMENT             = "SyncEnvironment";
    public static final String BACKEND_TYPES                = "BackendTypes";
    
    
    // ------------------------ content area local

    /**
     * The path to the currently selected configuration node
     * (e.g. something like "org.openoffic.Office.Writer/Contents/Display")
     */
    public static final String NODE_PATH = "NodePath";
    
    /**
     * The path to the last selected template
     * (e.g. /StarOffice7/Advanced")
     */
    public static final String TEMPLATE_PATH = 
            "ContentArea.PolicyTemplateSelection";
    
    /**
     * The name of the selected template (e.g. DisableCommands)
     */
    public static final String TEMPLATE_SELECTION = 
            "ContentArea.PageletView.ContentAreaPolicies.CategoryHref";

    // 
    // ---------------------- Div -------------------------------
    //

    /**
     * The String containing the base name for the manager resource file
     */
    public static final String RES_BASE_NAME = "com.sun.apoc.manager.resource.apoc_manager";
    
    // 
    // ------------------------ DEPRECATED STUFF --------------------------------
    //

    /**
     * The viewed entity id is the entity id selected in the navigation area and viewed in the content area
     */
    public static final String VIEWED_ENTITY_ID = "ViewedEntityId";
    
    /**
     * The scope (either user or host)
     */
    public static final String SCOPE = "Scope";

    /**
     * The entity id for navigation table drill down
     */
    public static final String ENTITY_ID = "NameHref";
    
    /**
     * The current entity id is the entity id pointed to by parentage path 
     */
    public static final String CURRENT_ENTITY_ID = "CurrentEntityId";
    
    /**
     * The selected configuration locale
     */
    public static final String LOCALE_SELECTION = "LocaleSelection";
    
    /**
     * The current scope is user-based configuration
     */
    public static final int SCOPE_USER = 0;

    /**
     * The current scope is host-based configuration
     */
    public static final int SCOPE_HOST = 1;
}
