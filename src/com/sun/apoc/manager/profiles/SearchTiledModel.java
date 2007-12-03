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
import com.iplanet.jato.model.DefaultModel;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.profiles.Profile;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;public class SearchTiledModel extends DefaultModel {
    
    public static final String  CHILD_RESULT1_HREF  = "Result1Href";
    public static final String  CHILD_RESULT2_HREF  = "Result2Href";
    public static final String  CHILD_RESULT3_HREF  = "Result3Href";
    public static final String  CHILD_RESULT1_TEXT  = "Result1Text";
    public static final String  CHILD_RESULT2_TEXT  = "Result2Text";
    public static final String  CHILD_RESULT3_TEXT  = "Result3Text";
    
    public SearchTiledModel() {
        super();
    }
    
    public void retrieve(String sPattern) throws ModelControlException {
        PolicyManager policyManager = (PolicyManager) RequestManager.getRequest().getSession(false).getAttribute(CopyMoveWizardPageModel.SOURCE_POLICYMGR);

        clear();
        
        if ( (sPattern==null) || (sPattern.length() == 0) ) {
            sPattern = "*";
        } else {
            sPattern = stripQuotes(sPattern);
        }
        
        try {
            TreeMap     sortContainer   = new TreeMap();
            String selectedProfileId = (String)RequestManager.getRequest().getSession(false).getAttribute(Constants.PROFILE_SEARCH_PROFILE);
            String sSource  = null;
            if (selectedProfileId != null && selectedProfileId.length() != 0) {
                sSource = policyManager.getProfile(selectedProfileId).getProfileRepository().getEntity().getPolicySourceName();
            }
            Iterator profiles = null;
            if (sSource != null) {
                profiles = policyManager.getProfileProvider(sSource).getAllProfiles();
            } else {
                profiles = policyManager.getAllProfiles();
            }

            while (profiles.hasNext()) {
                Profile profile = (Profile) profiles.next();
                if (isMatch(profile.getDisplayName(), sPattern)) {
                    sortContainer.put(profile.getDisplayName(), profile.getId());
                }
            } 

            if (sortContainer.size()==0) {return;}
            
            Object []   resultArray     = sortContainer.keySet().toArray();
            int         nColumnLength   = (resultArray.length+2)/3;
            int         column1Runner    = 0;
            int         column2Runner    = nColumnLength;
            int         column3Runner    = resultArray.length-(resultArray.length-nColumnLength)/2;
            while ( column1Runner<nColumnLength ) {
                appendRow();
                
                // 1st column
                String sDisplayName = (String) resultArray[column1Runner];
                String sId          = (String) sortContainer.get(sDisplayName); 
                setValue(CHILD_RESULT1_TEXT, sDisplayName);
                setValue(CHILD_RESULT1_HREF, sId);
                
                // 2nd column
                if (column2Runner<(resultArray.length-(resultArray.length-nColumnLength)/2)) {
                    sDisplayName = (String) resultArray[column2Runner];
                    sId          = (String) sortContainer.get(sDisplayName); 
                } else {
                    sDisplayName    = null;
                    sId             = null;
                }
                setValue(CHILD_RESULT2_TEXT, sDisplayName);
                setValue(CHILD_RESULT2_HREF, sId);
                
                // 3rd column
                if (column3Runner<resultArray.length) {
                    sDisplayName = (String) resultArray[column3Runner];
                    sId          = (String) sortContainer.get(sDisplayName); 
                } else {
                    sDisplayName    = null;
                    sId             = null;
                }
                setValue(CHILD_RESULT3_TEXT, sDisplayName);
                setValue(CHILD_RESULT3_HREF, sId);
                column1Runner++;
                column2Runner++;
                column3Runner++;
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
    }
    
    private boolean isMatch(String sData, String sFilter)
    {
        int nCurrentDataPosition = 0;
        int nMatchPosition = 0;
        StringTokenizer aTokenizer = new StringTokenizer(sFilter, "*");
        String sSubMatcher = "";
        while(aTokenizer.hasMoreTokens()) 
        {
            sSubMatcher = (String)aTokenizer.nextElement();
            nMatchPosition = sData.indexOf(sSubMatcher, nCurrentDataPosition);
            if(nCurrentDataPosition == 0 && !sFilter.startsWith("*") && nMatchPosition != 0)
                return false;
            if(nMatchPosition == -1)
                return false;
            nCurrentDataPosition = nMatchPosition + sSubMatcher.length();
        }
        return sFilter.endsWith("*") || nCurrentDataPosition == sData.length();
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
    
    protected NavigationModel getModel() {
        return (NavigationModel) RequestManager.getRequestContext().getModelManager().getModel(NavigationModel.class,
        "NavTree", true, true);
    }
}
