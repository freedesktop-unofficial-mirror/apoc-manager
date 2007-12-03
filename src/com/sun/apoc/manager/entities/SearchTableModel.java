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

package com.sun.apoc.manager.entities;

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestParticipant;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Domain;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.entities.Node;
import com.sun.apoc.spi.entities.Organization;
import com.sun.apoc.spi.entities.Role;
import com.sun.apoc.spi.entities.User;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.ldap.entities.LdapEntityType;
import com.sun.apoc.spi.ldap.entities.LdapNode;
import com.sun.apoc.spi.ldap.environment.LdapEnvironmentMgr;

import com.sun.web.ui.model.CCActionTableModel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class SearchTableModel extends CCActionTableModel implements RequestParticipant {
    
    public static final String CHILD_NAME_TEXT      = "NameText";
    public static final String CHILD_NAME_HREF      = "NameHref";
    public static final String CHILD_NAME_COLUMN    = "NameColumn";
    public static final String CHILD_VIEW_ACTION    = "ViewAction";
    public static final String CHILD_USERID_COLUMN  = "UserIdColumn";
    public static final String CHILD_USERID_TEXT    = "UserIdText";
    public static final String CHILD_TYPE_TEXT      = "TypeText";
    public static final String CHILD_TYPE_COLUMN    = "TypeColumn";
    public static final String CHILD_PATH_TEXT      = "PathText";
    public static final String CHILD_PATH_COLUMN    = "PathColumn";
    public static final String CHILD_FILTER_MENU    = "FilterMenu";
    
    private int       m_nSearchLimit  = 200;
    private PolicyManager m_policyManager = null;
    private String    m_sType   = "";
    
    public SearchTableModel() {
        super();
    }
    
    public void setRequestContext(RequestContext requestContext) {
        setActionValue(CHILD_NAME_COLUMN,   "APOC.navigation.name");
        setActionValue(CHILD_TYPE_COLUMN,   "APOC.navigation.type");
        setActionValue(CHILD_PATH_COLUMN,   "APOC.search.path");
        setActionValue(CHILD_USERID_COLUMN, "APOC.search.userid");
    }
    
    public void retrieve(String sEntity, String sEntityType, String sType, String sSource, String sSearchString, String sMaxRows, boolean bGlobalSearch, String sContext) throws ModelControlException {
        clear();
        
        m_sType = sType;
        
        if (sSearchString.length() == 0) {
            sSearchString = "*";
        }
        sSearchString = stripQuotes(sSearchString);
        if (!sSearchString.startsWith("*")) {
            sSearchString = "*"+sSearchString;
        }
        if (!sSearchString.endsWith("*")) {
            sSearchString = sSearchString+"*";
        }
        
        if ( (sMaxRows!=null) && (sMaxRows.length()>0) && (!sMaxRows.equals("defval"))) {
            int nMaxRows = Integer.parseInt(sMaxRows);
            setMaxRows(nMaxRows);
        }
        
        try {
            PolicyManager m_policyManager = Toolbox2.getPolicyManager();
            if (sContext!=null && sContext.length()>0) {
                m_policyManager = Toolbox2.createPolicyManager(sContext, null, null, false);
            }

            //TPF_TODO: shouldn't be LDAP-specific here
            LdapEnvironmentMgr envMgr = new LdapEnvironmentMgr(m_policyManager.getEnvironment());
            m_nSearchLimit = envMgr.getSearchResultSizeLimit();
            LinkedList  resultList  = new LinkedList();
            Iterator    results     = resultList.iterator();
            Entity      entity      = m_policyManager.getEntity(sEntityType, sEntity);

            if (bGlobalSearch) {
                entity = m_policyManager.getRootEntity(sSource);
            }
            if (sType.equals(LdapEntityType.STR_ORG) && (entity instanceof Organization)) {
                results = ((Organization)entity).findSubOrganizations(sSearchString, true);
                addToModel(results, false);
            }
            else if (sType.equals(LdapEntityType.STR_DOMAIN) && (entity instanceof Domain)) {
                results = ((Domain)entity).findSubDomains(sSearchString, true);
                addToModel(results, false);
            }
            else if (sType.equals(LdapEntityType.STR_HOST) && (entity instanceof Domain)) {
                results = ((Domain)entity).findHosts(sSearchString, true);
                addToModel(results, false);
            }
            else if (sType.equals(LdapEntityType.STR_ROLE) && ((entity instanceof Role) || (entity instanceof Organization))) {
                //TPF_TODO: Katell: add findRoles to the Node interface
                //          the impl below is ok as long as only the ldap backend supports roles
                if (entity instanceof LdapNode) {
                    results = ((LdapNode)entity).findRoles(sSearchString, true);
                }
                addToModel(results, false);
            }
            else if (sType.equals(LdapEntityType.STR_USERID) && (entity instanceof Organization)) {
                results = ((Organization)entity).findUsers(sSearchString, true);
                addToModel(results, false);
                if (!sSearchString.equals("*")) {
                    searchUsers(entity, sSearchString);
                }
            }
            if (bGlobalSearch) {
                if (sType.equals(LdapEntityType.STR_ALL)) {
                    entity = m_policyManager.getRootEntity(EnvironmentConstants.HOST_SOURCE);
                    results = ((Node)entity).findEntities(sSearchString, true);
                    addToModel(results, bGlobalSearch);
                    entity = m_policyManager.getRootEntity(EnvironmentConstants.USER_SOURCE);
                    if ((entity instanceof Node) && (m_nSearchLimit>getSize())) {
                        results = ((Node)entity).findEntities(sSearchString, true);
                        addToModel(results, bGlobalSearch);
                        if ((entity instanceof Organization) && (!sSearchString.equals("*")) && (m_nSearchLimit>getSize())) {
                            searchUsers(entity, sSearchString);
                        }
                    }
                }
            } else {
                if (sType.equals(LdapEntityType.STR_ALL) && (entity instanceof Domain)) {
                    results = ((Domain)entity).findSubDomains(sSearchString, true);
                    addToModel(results, false);
                    results = ((Domain)entity).findHosts(sSearchString, true);
                    addToModel(results, false);
                }
                else if (sType.equals(LdapEntityType.STR_ALL) && (entity instanceof Organization)) {
                    results = ((Node)entity).findEntities(sSearchString, true);
                    addToModel(results, bGlobalSearch);
                    if ((entity instanceof Organization) && (!sSearchString.equals("*")) && (m_nSearchLimit>getSize())) {
                        searchUsers(entity, sSearchString);
                    }
                }            
                 else if (sType.equals(LdapEntityType.STR_ALL) && (entity instanceof Node)) {
                    results = ((Node)entity).findEntities(sSearchString, true);
                    addToModel(results, false);
                }                  
            }
        }
        catch (Exception ex) {
            throw new ModelControlException(ex);
        }
    }
    
    private void searchUsers(Entity entity, String sSearchString)
    throws SPIException, ModelControlException {
        
        LinkedList      resultList  = new LinkedList();
        Iterator        results     = resultList.iterator();
        StringTokenizer tokens      = new StringTokenizer(sSearchString, ",");
        if (tokens.countTokens()==2) {
            String sSurName     = tokens.nextToken().trim();
            String sGivenName   = tokens.nextToken().trim();
            if (m_nSearchLimit>getSize()) {
                Iterator    results1    = ((Organization)entity).findUsers("(givenname="+sGivenName+")", true);
                Iterator    results2    = ((Organization)entity).findUsers("(sn="+sSurName+")", true);
                HashSet     setIdUser   = new HashSet();
                
                while (results1.hasNext()) {
                    User user = (User) results1.next();
                    setIdUser.add(user.getUserId());
                }
                while (results2.hasNext()) {
                    User user = (User) results2.next();
                    if (setIdUser.contains(user.getUserId())) {
                        resultList.add(user);
                    }
                }
                
                addToModel(resultList.iterator(), true);
            }
            if (m_nSearchLimit>getSize()) {
                results = ((Organization)entity).findUsers("(cn="+sGivenName+" "+sSurName+")", true);
                addToModel(results, true);
            }
        } else {
            if (m_nSearchLimit>getSize()) {
                results = ((Organization)entity).findUsers("(givenname="+sSearchString+")", true);
                addToModel(results, true);
            }
            if (m_nSearchLimit>getSize()) {
                results = ((Organization)entity).findUsers("(sn="+sSearchString+")", true);
                addToModel(results, true);
            }
            if (m_nSearchLimit>getSize()) {
                results = ((Organization)entity).findUsers("(cn="+sSearchString+")", true);
                addToModel(results, true);
            }
        }
    }
    
    private void addToModel(Iterator entities, boolean bForceUnique)
    throws SPIException, ModelControlException {

        while (entities.hasNext() && m_nSearchLimit>getSize()) {
            boolean bAlreadyStored  = false;
            Entity  entity          = (Entity) entities.next();
            String  sEntityIdToAdd  = entity.getId();
            
            if (bForceUnique) {
                int  currentLocation = getLocation();
                for (int nModelRunner = 0; nModelRunner < getNumRows(); nModelRunner++) {
                    setLocation(nModelRunner);
                    String sEntityId = (String) getValue(CHILD_VIEW_ACTION);
                    if (sEntityIdToAdd.equals(sEntityId)) {
                        bAlreadyStored = true;
                        break;
                    }
                }
                setLocation(currentLocation);
            }
            
            if (!bAlreadyStored) {
                
                appendRow();
                
                String sUserId      = "-";
                String sDisplayName = entity.getDisplayName(Toolbox2.getLocale());

                if (entity instanceof User) {
                    sUserId = ((User)entity).getUserId();
                    if (!m_sType.equals(LdapEntityType.STR_USERID)) {
                        sDisplayName = sDisplayName + " [" + sUserId + "]"; 
                    }
                }
                String entityType = entity.getPolicySourceName();
                // Name column
                setValue(CHILD_NAME_TEXT, sDisplayName);
                //            setValue(CHILD_NAME_HREF, entity.getId());
                setValue(CHILD_VIEW_ACTION, sEntityIdToAdd + "|" + entityType);
                
                // User id column
                setValue(CHILD_USERID_TEXT, sUserId);
                
                // Type column
                setValue(CHILD_TYPE_TEXT, Toolbox2.getEntityTypeResource(entity));
                
                // Path column
                setValue(CHILD_PATH_TEXT, Toolbox2.getParentagePath(entity, true, false, "/", m_policyManager));
            }
        }
    }
    
    private String stripQuotes(String sQuoted) {
        sQuoted = sQuoted.trim();
        
        if (sQuoted.length()>=2) {
            if (sQuoted.substring(0,1).equals("\"") && sQuoted.substring(sQuoted.length()-1).equals("\"")) {
                sQuoted=sQuoted.substring(1, sQuoted.length()-1);
            } else if (sQuoted.substring(0,1).equals("'") && sQuoted.substring(sQuoted.length()-1).equals("'")) {
                sQuoted=sQuoted.substring(1, sQuoted.length()-1);
            }
        }
        
        return sQuoted;
    }
}
