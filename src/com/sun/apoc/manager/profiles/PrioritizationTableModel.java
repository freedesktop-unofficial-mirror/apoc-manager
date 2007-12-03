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
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Domain;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.entities.Host;
import com.sun.apoc.spi.entities.Organization;
import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.profiles.ProfileRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class PrioritizationTableModel extends ProfileModel {
    
    public PrioritizationTableModel() {
        super();
    }
    
    public void setRequestContext(RequestContext requestContext) {
        super.setRequestContext(requestContext);
        
        setActionValue(CHILD_NAME_COLUMN,       "APOC.navigation.name");
        setActionValue(CHILD_AUTHOR_COLUMN,     "APOC.profilemgr.author");
        setActionValue(CHILD_LASTMOD_COLUMN,    "APOC.profilemgr.lastMod");

        setPrimarySortName(CHILD_PRIO_TEXT);
    }
    
    public void retrieve(Profile profile) throws ModelControlException {
        try {
            if (profile != null) {
                retrieve(profile.getProfileRepository().getEntity());
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    public void retrieve(Entity entity) throws ModelControlException {
        clear();
        
        try {
            Applicability use = Applicability.USER;
            
            if ((entity instanceof Domain) || (entity instanceof Host)) {
                use = Applicability.HOST;
            }
            
            ProfileRepository   repository= entity.getProfileRepository();
            Iterator            profiles  = repository.getProfiles(use);
            
            while (profiles.hasNext()) {
                
                appendRow();
                
                Profile             profile     = (Profile) profiles.next();
                Date                date        = new Date(profile.getLastModified());
                SimpleDateFormat    format      = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss z");
                String              sDate       = format.format(date);
                
                setValue(CHILD_NAME_TEXT,       profile.getDisplayName());
                setValue(CHILD_NAME_HREF,       profile.getId());
                setValue(CHILD_LASTMOD_TEXT,    sDate);
                setValue(CHILD_PRIO_TEXT,       new Integer(profile.getPriority()));
                
                if (profile.getAuthor() != null) {
                    setValue(CHILD_AUTHOR_TEXT,     profile.getAuthor());
                }
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
}
