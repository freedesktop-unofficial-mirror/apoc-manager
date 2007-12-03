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
import com.iplanet.jato.view.html.Option;
import com.iplanet.jato.view.html.OptionList;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Domain;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.entities.Host;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.profiles.ProfileRepository;
import com.sun.web.ui.model.CCOrderableListModel;
import java.util.Iterator;
public class PrioritizationListModel extends CCOrderableListModel {
    
    public static String NAME_TEXT = "NameText";
    public static String NAME_HREF = "NameHref";
    
    public PrioritizationListModel() {
        super();
    }
    
    public void setRequestContext(RequestContext requestContext) {
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
        
        try {
            Applicability use = Applicability.USER;
            
            if (entity.getPolicySourceName().equals(EnvironmentConstants.HOST_SOURCE)) {
                use = Applicability.HOST;
            } 
            
            ProfileRepository   repository= entity.getProfileRepository();
            Iterator            profiles  = repository.getProfiles(use);
            OptionList          options   = new OptionList();
            
            while (profiles.hasNext()) {
                Profile profile = (Profile) profiles.next();
                options.add(new Option(profile.getDisplayName(), profile.getId()));
            }

            setSelectedOptionList(options);
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
}
