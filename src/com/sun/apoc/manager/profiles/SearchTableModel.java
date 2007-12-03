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
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.ldap.entities.LdapEntityType;
import com.sun.apoc.spi.profiles.Profile;

import com.sun.web.ui.model.CCActionTableModel;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;
public class SearchTableModel extends CCActionTableModel {
    
    public static final String CHILD_NAME_COLUMN1      = "NameColumn1";
    public static final String CHILD_NAME_TEXT1        = "NameText1";
    public static final String CHILD_NAME_HREF1        = "NameHref1";
    public static final String CHILD_NAME_COLUMN2      = "NameColumn2";
    public static final String CHILD_NAME_TEXT2        = "NameText2";
    public static final String CHILD_NAME_HREF2        = "NameHref2";
    public static final String CHILD_NAME_COLUMN3      = "NameColumn3";
    public static final String CHILD_NAME_TEXT3        = "NameText3";
    public static final String CHILD_NAME_HREF3        = "NameHref3";
    
    //
    private String  m_sEntityType   = LdapEntityType.STR_UNKNOWN;
    private int     m_nMaxRows      = 10;
    
    public SearchTableModel() {
        super();
        setMaxRows(m_nMaxRows);
        setActionValue(CHILD_NAME_COLUMN1,   "");
        setActionValue(CHILD_NAME_COLUMN2,   "");
        setActionValue(CHILD_NAME_COLUMN3,   "");
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
            Iterator    profiles        = policyManager.getProfileProvider(LdapEntityType.STR_ALL).getAllProfiles();

            while (profiles.hasNext()) {
                Profile profile = (Profile) profiles.next();
                if (isMatch(profile.getDisplayName(), sPattern)) {
                    sortContainer.put(profile.getDisplayName(), profile.getId());
                }
            } 

            Iterator sortContainerIter = sortContainer.keySet().iterator();
            while (sortContainerIter.hasNext()) {
                appendRow();
                
                // 1st column
                String sDisplayName = (String) sortContainerIter.next();
                String sId          = (String) sortContainer.get(sDisplayName);
                setValue(CHILD_NAME_TEXT1, sDisplayName);
                setValue(CHILD_NAME_HREF1, sId);
                
                // 2nd column
                if (sortContainerIter.hasNext()) {
                    sDisplayName = (String) sortContainerIter.next();
                    sId = (String) sortContainer.get(sDisplayName);
                } else {
                    sDisplayName = null;
                    sId = null;
                }
                setValue(CHILD_NAME_TEXT2, sDisplayName);
                setValue(CHILD_NAME_HREF2, sId);
                
                // 3rd column
                if (sortContainerIter.hasNext()) {
                    sDisplayName = (String) sortContainerIter.next();
                    sId = (String) sortContainer.get(sDisplayName);
                } else {
                    sDisplayName = null;
                    sId = null;
                }
                setValue(CHILD_NAME_TEXT3, sDisplayName);
                setValue(CHILD_NAME_HREF3, sId);
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
}
