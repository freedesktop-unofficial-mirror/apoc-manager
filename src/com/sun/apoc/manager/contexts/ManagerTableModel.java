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

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.RequestParticipant;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.environment.EnvironmentMgr;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.model.CCActionTableModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

public class ManagerTableModel extends CCActionTableModel implements RequestParticipant {
    // Child view names (i.e. display fields).
    public static final String CHILD_COLUMN         = "Col";
    public static final String CHILD_STATIC_TEXT    = "Text";
    public static final String CHILD_NAME_COLUMN    = "NameColumn";
    public static final String CHILD_NAME_TEXT      = "NameText";
    public static final String CHILD_NAME_HREF      = "NameHref";
    public static final String CHILD_TYPE_COLUMN    = "TypeColumn";
    public static final String CHILD_TYPE_TEXT      = "TypeText";
    public static final String CHILD_ORGLOC_COLUMN  = "OrgLocationColumn";
    public static final String CHILD_ORGLOC_TEXT    = "OrgLocationText";
    public static final String CHILD_PROLOC_COLUMN  = "ProfileLocationColumn";
    public static final String CHILD_PROLOC_TEXT    = "ProfileLocationText";
    
    public static final String CONFIG_FILE_LOCATION = System.getProperty("file.separator") 
                                                      + "etc" 
                                                      + System.getProperty("file.separator") 
                                                      + "opt" 
                                                      + System.getProperty("file.separator")
                                                      + "SUNWapmcg"
                                                      + System.getProperty("file.separator");
    //
    private RequestContext  m_requestContext;
    private HashMap         m_backendPropertiesMap     = new HashMap();
    private HashMap         m_backendFilesMap          = new HashMap();
    private Properties      m_backendProperties     = new Properties();
    
    public ManagerTableModel() {
        super();
    }
    
    public void setRequestContext(RequestContext requestContext) {
        m_requestContext = requestContext;
        
        setActionValue(CHILD_NAME_COLUMN,    "APOC.contexts.column.name");
        setActionValue(CHILD_TYPE_COLUMN,    "APOC.contexts.column.type");
        setActionValue(CHILD_ORGLOC_COLUMN,  "APOC.contexts.column.locorg");
        setActionValue(CHILD_PROLOC_COLUMN,  "APOC.contexts.column.locpro");
        setMaxRows(10);
    }
    
    public void retrieve() throws ModelControlException {
        clear();
        try {
            File dir = new File(CONFIG_FILE_LOCATION);
            if (dir.exists()) {
                FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".properties");
                    }
                };
                HashMap backendTypes = (HashMap)RequestManager.getRequestContext().getRequest().getSession(true).getAttribute(Constants.BACKEND_TYPES);
                File[] files = dir.listFiles(filter);
                int i = 0;
                while (i < files.length) {
                    File properties = files[i];
                    if (properties.canRead()) {
                        FileInputStream input = new FileInputStream(properties) ;
                        Properties backendProperties = new Properties();
                        backendProperties.load(input);
                        input.close();
                        if (backendProperties.getProperty("Backend") != null) {
                            String sName = backendProperties.getProperty("Backend");
                            String sProviderURL = null;
                            String sProviderType = "";
                            String sProfileProviderURL = null;
                            String sProfileProviderType = "";

                            sProviderURL = backendProperties.getProperty(EnvironmentConstants.ORGANIZATION_PREFIX 
                                                                            + EnvironmentConstants.URL_KEY);
                            sProfileProviderURL = backendProperties.getProperty(EnvironmentConstants.ORGANIZATION_PREFIX 
                                                                                                + EnvironmentConstants.PROFILE_PREFIX 
                                                                                                + EnvironmentConstants.URL_KEY);
                            if (sProviderURL == null) {
                                sProviderURL = backendProperties.getProperty(EnvironmentConstants.URL_KEY);
                            }
                            if (sProfileProviderURL == null) {
                                sProfileProviderURL = sProviderURL;
                            }
                            if (sProviderURL != null) {
                                if (sProviderURL.indexOf(":") != -1 ) {
                                    sProviderType = sProviderURL.substring(0, sProviderURL.indexOf(":"));
                                }                                  
                                if (sProfileProviderURL.indexOf(":") != -1 ) {
                                    sProfileProviderType = sProfileProviderURL.substring(0, sProfileProviderURL.indexOf(":"));
                                }
                                
                                if (!sProviderType.equals(sProfileProviderType)) {
                                    sProviderType = "APOC.contexts.hybrid.text";
                                }                     
                                
                                if (backendTypes == null) {
                                    backendTypes = new HashMap();
                                    RequestManager.getRequestContext().getRequest().getSession(true).setAttribute(Constants.BACKEND_TYPES, backendTypes);
                                }
                                backendTypes.put(sName, sProviderType);                                
                                // If its a file-based backend then no credentials are necessary
                                // so save them to session (empty strings) so they can be used everywhere
                                // i.e. in the sync dialog, copymove etc.
                                if (sProviderType.startsWith("file") ) {
                                    backendTypes.put(sName, sProviderType);
                                    HashMap authorizedContexts = (HashMap) RequestManager.getSession().getAttribute(Constants.AUTH_CONTEXTS);
                                    if (authorizedContexts==null) {
                                        authorizedContexts = new HashMap();
                                        RequestManager.getRequestContext().getRequest().getSession(true).setAttribute(Constants.AUTH_CONTEXTS, authorizedContexts);
                                    }
                                    if (!(authorizedContexts.containsKey(sName))) {
                                        String[] authentification = new String[]{"", ""};
                                        authorizedContexts.put(sName, authentification);
                                    }                                
                                } 
                            }
                            
                            appendRow();
                            setValue(CHILD_NAME_TEXT,   sName);
                            setValue(CHILD_NAME_HREF,   sName);
                            setValue(CHILD_TYPE_TEXT,   sProviderType);
                            setValue(CHILD_ORGLOC_TEXT, sProviderURL);
                            setValue(CHILD_PROLOC_TEXT, sProfileProviderURL);
                            m_backendPropertiesMap.put(sName, backendProperties);
                            m_backendFilesMap.put(sName, files[i].getAbsolutePath());
                        }
                    }
                    i++;
                }
            }
        } catch (Exception e) {
            throw new ModelControlException(e);
        }
        
    }
       
    public void delete() throws ModelControlException {
        try {
            LinkedList  scheduledProfiles   = new LinkedList();
            int         currentLocation     = getLocation();
            for (int nModelRunner = 0; nModelRunner < getNumRows(); nModelRunner++) {
                if (isRowSelected(nModelRunner)) {
                    setLocation(nModelRunner);

                    String      sContextName = (String) getValue(CHILD_NAME_TEXT);
                    //Try to delete from the default location otherwise try the legacy storage location
                    if (!(m_backendPropertiesMap.isEmpty())) {
                        String      filePath     = (String) m_backendFilesMap.get(sContextName);
                        if (filePath != null) {
                            File fileToDelete = new File(filePath);
                            if (fileToDelete.exists()) {
                                fileToDelete.delete();
                            }
                        }
                        m_backendPropertiesMap.remove(sContextName);
                        m_backendFilesMap.remove(sContextName);
                    }
                    // Remove any cached credentials from the session
                    HashMap authorizedContexts = (HashMap) RequestManager.getSession().getAttribute(Constants.AUTH_CONTEXTS);
                    if (authorizedContexts!=null) {
                        if (authorizedContexts.containsKey(sContextName)) {
                            authorizedContexts.remove(sContextName);
                        }
                    }
                    setRowSelected(false);
                }
            }
            setLocation(currentLocation);

        } catch (Exception e) {
            throw new ModelControlException(e);
        }

    }
    
    public void rename(String sNewName) throws ModelControlException {
        try {
            LinkedList  scheduledProfiles   = new LinkedList();
            int         currentLocation     = getLocation();
            for (int nModelRunner = 0; nModelRunner < getNumRows(); nModelRunner++) {
                if (isRowSelected(nModelRunner)) {
                    setLocation(nModelRunner);
                    
                    String      sContextName = (String) getValue(CHILD_NAME_TEXT);
                    if (!(m_backendPropertiesMap.isEmpty())) {
                        String      filePath     = (String) m_backendFilesMap.get(sContextName);
                        Properties  properties   = (Properties) m_backendPropertiesMap.get(sContextName);
                        properties.setProperty("Backend", sNewName);
                        m_backendPropertiesMap.put(sNewName, properties);
                        m_backendPropertiesMap.remove(sContextName);
                        m_backendFilesMap.put(sNewName, filePath);
                        m_backendFilesMap.remove(sContextName);
                        if (filePath != null) {
                            File propertiesFile = new File(filePath);
                            if (propertiesFile.exists()) {
                                propertiesFile.delete();
                            }
                            File newFile = new File(filePath);
                            FileOutputStream output = new FileOutputStream(newFile);

                            properties.store(output, "");
                        }
                    }                    
                    // Remove any cached credentials from the session
                    HashMap authorizedContexts = (HashMap) RequestManager.getSession().getAttribute(Constants.AUTH_CONTEXTS);
                    if (authorizedContexts!=null) {
                        if (authorizedContexts.containsKey(sContextName)) {
                            authorizedContexts.put(sNewName, authorizedContexts.get(sContextName));
                            authorizedContexts.remove(sContextName);
                        }
                    }
                    
                    setRowSelected(false);
                    break;
                }
            }
            setLocation(currentLocation);
            
        } catch (Exception e) {
            throw new ModelControlException(e);
        }
    }
    
}
