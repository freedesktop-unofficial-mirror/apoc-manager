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

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.profiles.ProfileRepository;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
public class AvailableTableModel extends ProfileModel {
    
    public static final String CHILD_ACCESS_HIDDEN = "AccessHidden";
    
    private static LinkedList   m_allProfiles;
    private static HashMap      m_accessCache;
    private static Boolean      m_organizationRootReadOnly;
    private static Boolean      m_domainRootReadOnly;
    
    public AvailableTableModel() {
        super();
    }
    
    public void setRequestContext(RequestContext requestContext) {
        super.setRequestContext(requestContext);
        
        setActionValue(CHILD_NAME_COLUMN,       "APOC.navigation.name");
        setActionValue(CHILD_AUTHOR_COLUMN,     "APOC.profilemgr.author");
        setActionValue(CHILD_LASTMOD_COLUMN,    "APOC.profilemgr.lastMod");
    }
    
    public void rename(String sProfileId, String sNewName) throws ModelControlException {
        try {
            Profile profile = Toolbox2.getPolicyManager().getProfile(sProfileId);
            profile.setDisplayName(sNewName);
        } catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    public void destroy(LinkedList profiles) throws ModelControlException {
        try {
            NavigationModel navigationModel = (NavigationModel) m_requestContext.getModelManager().getModel(NavigationModel.class, "NavTree", true, true);
            Iterator        profilesIter    = profiles.iterator();
            while (profilesIter.hasNext()) {
                String              sProfile            = (String) profilesIter.next();
                Profile             profile             = Toolbox2.getPolicyManager().getProfile(sProfile);
                ProfileRepository   repository          = profile.getProfileRepository();
                Iterator            assignedEntities    = profile.getAssignedEntities();
                
                while (assignedEntities.hasNext()) {
                    Entity          entity          = (Entity) assignedEntities.next();
                    Iterator        assignedProfiles= entity.getAssignedProfiles();
                    assignedProfiles.next();
                    navigationModel.setAssignedState(entity, assignedProfiles.hasNext());
                    entity.unassignProfile(profile);
                }
                
                repository.destroyProfile(profile);
            }
        } catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    public void retrieve(Entity entity) throws ModelControlException {
        try {
            m_allProfiles = new LinkedList();
            m_accessCache = new HashMap();
            m_organizationRootReadOnly = null;
            m_domainRootReadOnly = null;
            Iterator profiles = Toolbox2.getPolicyManager().getAllProfiles();
            ArrayList sources = new ArrayList(Arrays.asList(Toolbox2.getPolicyManager().getSources()));
            while (profiles.hasNext()) {
                Profile profile = (Profile) profiles.next();
                m_allProfiles.add(profile);
                if (!m_accessCache.containsKey(profile)) {
                    m_accessCache.put(profile, new Boolean(profile.getProfileRepository().isReadOnly()));
                    if ((sources.contains(EnvironmentConstants.USER_SOURCE))
                            && (m_organizationRootReadOnly==null) 
                            && (Toolbox2.getPolicyManager().getRootEntity(EnvironmentConstants.USER_SOURCE).equals(profile.getProfileRepository().getEntity()))){
                        m_organizationRootReadOnly = (Boolean) m_accessCache.get(profile);
                    } else if ((sources.contains(EnvironmentConstants.HOST_SOURCE))
                            && (m_domainRootReadOnly==null) 
                            && (Toolbox2.getPolicyManager().getRootEntity(EnvironmentConstants.HOST_SOURCE).equals(profile.getProfileRepository().getEntity()))){
                        m_domainRootReadOnly = (Boolean) m_accessCache.get(profile);
                    }
                }
            }
            if (sources.contains(EnvironmentConstants.USER_SOURCE) && m_organizationRootReadOnly==null) {
                m_organizationRootReadOnly = new Boolean(Toolbox2.getPolicyManager().getRootEntity(EnvironmentConstants.USER_SOURCE).getProfileRepository().isReadOnly());
            }
            if (sources.contains(EnvironmentConstants.HOST_SOURCE) && m_domainRootReadOnly==null) {
                m_domainRootReadOnly = new Boolean(Toolbox2.getPolicyManager().getRootEntity(EnvironmentConstants.HOST_SOURCE).getProfileRepository().isReadOnly());
            }
        } catch (Exception e) {
            throw new ModelControlException(e);
        }
    }
    
    public void fillModel(Applicability aplicty) throws ModelControlException {
        clear();
        
        try {
            Iterator profiles = m_allProfiles.listIterator();
            while (profiles.hasNext()) {
                Profile profile = (Profile) profiles.next();
                if ( profile.getApplicability().equals(aplicty) ) {
                    
                    appendRow();
                    Date                date        = new Date(profile.getLastModified());
                    SimpleDateFormat    format      = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss z");
                    String              sDate       = format.format(date);
                    
                    setValue(CHILD_NAME_TEXT,       profile.getDisplayName());
                    setValue(CHILD_NAME_HREF,       profile.getId());
//                    setValue(CHILD_NAME_HREF,       profile.getId()+"&amp;AccessInfo="+((Boolean)m_accessCache.get(profile)).toString());
                    setValue(CHILD_LASTMOD_TEXT,    sDate);
                    setValue(CHILD_ACCESS_HIDDEN,   ((Boolean)m_accessCache.get(profile)).toString());
                    if (profile.getAuthor() != null) {
                        setValue(CHILD_AUTHOR_TEXT, profile.getAuthor());
                    }
                }
            }
        } catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    public boolean hasReadOnlyProfile() {
        Iterator iter = m_accessCache.values().iterator();
        while (iter.hasNext()) {
            if (((Boolean)iter.next()).booleanValue()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isOrganizationRootReadOnly() {
        return m_organizationRootReadOnly==null ? false : m_organizationRootReadOnly.booleanValue();
    }
    
    public boolean isDomainRootReadOnly() {
        return m_domainRootReadOnly==null ? false : m_domainRootReadOnly.booleanValue();
    }

}
