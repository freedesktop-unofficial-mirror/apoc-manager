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

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.profiles.AssignedTableModel;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.profiles.Applicability;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.profiles.ProfileRepository;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.masthead.CCSecondaryMasthead;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
public class ProfilesImportViewBean extends ViewBeanBase {
    public static final String PAGE_NAME           = "ProfilesImport";
    public static final String DEFAULT_DISPLAY_URL = "/jsp/profiles/Import.jsp";
    public static final String CHILD_MASTHEAD      = "Masthead";
    public static final String CHILD_ALERT         = "Alert";
    public static final String CHILD_STACKTRACE    = "StackTrace";
    public static final String CHILD_PAGETITLE     = "ImportTitle";
    public static final String CHILD_CHOOSETEXT    = "ChooseText";
    public static final String CHILD_DEFAULT_VAL   = "DefaultValue";
    public static final String CHILD_IMPORTGROUP   = "ImportGroup";
    public static final String CHILD_TABHREF       = "TabHref";
    public static final String CHILD_TABSELECTION  = "TabSelection";
    public static final String CHILD_CONDITIONAL_JS= "ConditionalJS";
    public static final String CHILD_EXISTING_NAMES= "ExistingNames";
    public static final String CHILD_OVERWRITE_ALERT= "OverwriteAlert";
    
    public static final String PROFILE_SOURCE = "ProfileSource";
    
    private CCPageTitleModel   m_pagetitleModel    = null;
    private boolean            m_bImport           = false;
    private boolean            m_bShowAlert        = false;
    private Iterator           m_existingProfiles = null;
    private Iterator           m_existingProfileAssignments = null;
    private String  sQuery = null;
    
    public ProfilesImportViewBean(RequestContext rc) {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        m_pagetitleModel = new CCPageTitleModel(rc.getServletContext(),
        "/jsp/profiles/ImportTitle.xml");
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_MASTHEAD,       CCSecondaryMasthead.class);
        registerChild(CHILD_ALERT,          CCAlertInline.class);
        registerChild(CHILD_STACKTRACE,     CCStaticTextField.class);
        registerChild(CHILD_PAGETITLE,      CCPageTitle.class);
        registerChild(CHILD_CHOOSETEXT,     CCStaticTextField.class);
        registerChild(CHILD_DEFAULT_VAL,    CCStaticTextField.class);
        registerChild(CHILD_TABSELECTION,   CCStaticTextField.class);
        registerChild(CHILD_CONDITIONAL_JS, CCStaticTextField.class);
        registerChild(CHILD_EXISTING_NAMES, CCStaticTextField.class);
        registerChild(CHILD_OVERWRITE_ALERT, CCStaticTextField.class);
        registerChild(CHILD_TABHREF,        CCHref.class);
        registerChild(CHILD_IMPORTGROUP,    CCHref.class);
        m_pagetitleModel.registerChildren(this);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_MASTHEAD)) {
            CCSecondaryMasthead child = new CCSecondaryMasthead(this, name);
            child.setSrc("/apoc/images/popuptitle.gif");
            return child;
        }
        else if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_STACKTRACE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_PAGETITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_pagetitleModel, name);
            Toolbox2.setPageTitleHelp(m_pagetitleModel, "APOC.groupimport.importing", 
                                    "APOC.groupimport.help", "gbgps.html"); 
            return child;
        }
        else if (name.equals(CHILD_CHOOSETEXT)) {
            CCI18N i18N = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            String chooseText = i18N.getMessage("APOC.groupimport.choosetext");
            CCStaticTextField child = new CCStaticTextField(this, name, chooseText);
            return child;
        }
        else if (name.equals(CHILD_DEFAULT_VAL)) {
            CCI18N i18N = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            String defaultText = i18N.getMessage("APOC.groupimport.defaulttext");
            CCStaticTextField child = new CCStaticTextField(this, name, defaultText);
            return child;
        }
        else if (name.equals(CHILD_TABSELECTION) || name.equals(CHILD_CONDITIONAL_JS)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_OVERWRITE_ALERT)) {
            CCI18N i18N = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            String defaultText = i18N.getMessage("APOC.importwnd.js.overwrite_alert");
            CCStaticTextField child = new CCStaticTextField(this, name, defaultText);
            return child;
        } 
        else if (name.equals(CHILD_EXISTING_NAMES)) {
            CCStaticTextField child = new CCStaticTextField(this, name, "");
            return child;
        }
        else if (name.equals(CHILD_TABHREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_IMPORTGROUP)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }
        else if (m_pagetitleModel.isChildSupported(name)) {
            return m_pagetitleModel.createChild(this, name);
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        Iterator profiles = getProfilesIterator();
        String existingNames = "";
        while (profiles.hasNext()) {
            Profile profile = (Profile)profiles.next();
            String profileName  = profile.getDisplayName();
            existingNames = existingNames + "|" + profileName;
        }

        CCStaticTextField namesChild = (CCStaticTextField) getChild(CHILD_EXISTING_NAMES);
        namesChild.setValue(existingNames) ;
        String profileSource = Toolbox2.getParameter("ImportGroup");
        if (profileSource != null && profileSource.length() != 0) {
            if (profileSource.equals("AvailableDomainTableView")) {
                RequestManager.getSession().setAttribute(PROFILE_SOURCE, EnvironmentConstants.HOST_SOURCE);
            } else if (profileSource.equals("AvailableOrgTableView")) {
                RequestManager.getSession().setAttribute(PROFILE_SOURCE, EnvironmentConstants.USER_SOURCE);
            } else {
                RequestManager.getSession().setAttribute(PROFILE_SOURCE, null);
            }
        }
    }
    
    public boolean beginConditionalJSDisplay(ChildDisplayEvent event) throws ModelControlException {
        CCStaticTextField   conditionalJS = (CCStaticTextField) getChild(event.getChildName());
        String              sText;
        if (m_bImport) {
            sText = "self.opener.document.forms[0].submit(); window.close();";
        } else {
            sText = "document.importForm.action='../manager/ProfilesImport?ProfilesImport.TabHref="+Toolbox2.getParameter("ImportGroup")+"'";
        }
        conditionalJS.setValue(sText);
        return true;
    }
    
    public boolean beginConditionalFormDisplay(ChildDisplayEvent event) throws ModelControlException {
        return !m_bImport;
    }
    
    public boolean beginAlertDisplay(ChildDisplayEvent event) throws ModelControlException {
        return m_bShowAlert;
    }
    
    public void handleImportGroupRequest(RequestInvocationEvent event) {
        forwardTo(getRequestContext());
    }
    
    public void handleTabHrefRequest(RequestInvocationEvent event) throws IOException, ModelControlException {
        
        try {
            HttpServletRequest request = getRequestContext().getRequest();
            ServletInputStream in   = request.getInputStream();
            byte[]             line = new byte[128];
            int                i    = in.readLine(line, 0, 128);
            
            if (i > 2) {
                int    boundaryLength = i - 2;
                String boundary = new String(line, 0, boundaryLength);
                String sGroupName = "";
                
                while (i != -1) {
                    String newLine = new String(line, 0, i);
                    
                    if (newLine.startsWith("Content-Disposition: form-data")) {
                        
                        if (newLine.indexOf("l10nFilename")!=-1) {
                            i = in.readLine(line, 0, 128);
                            i = in.readLine(line, 0, 128);
                            sGroupName = extractFilename(line, i-2);
                        }
                        else if (newLine.indexOf("filename")!=-1) {
                            i = in.readLine(line, 0, 128);
                            i = in.readLine(line, 0, 128);
                            
                            AssignedTableModel model = (AssignedTableModel) getModel(AssignedTableModel.class);
                            PolicyManager pmgr = Toolbox2.getPolicyManager();
                            String source = (String)RequestManager.getSession().getAttribute(PROFILE_SOURCE);
                            if (source!=null && source.equals(EnvironmentConstants.HOST_SOURCE)) {
                                model.importGroup(sGroupName, pmgr.getRootEntity(EnvironmentConstants.HOST_SOURCE), Applicability.HOST, false, in);
                            } else if (source!=null && source.equals(EnvironmentConstants.USER_SOURCE)) {
                                model.importGroup(sGroupName, pmgr.getRootEntity(EnvironmentConstants.USER_SOURCE), Applicability.USER, false, in);
                            } else  {
                                Entity selectedEntity = Toolbox2.getSelectedEntity();
                                Applicability use = Applicability.HOST;
                                if (selectedEntity.getPolicySourceName().equals(EnvironmentConstants.USER_SOURCE)) {
                                    use = Applicability.USER;
                                }
                                model.importGroup(sGroupName, Toolbox2.getSelectedEntity(), use, true, in);
                            }
                            
                            m_bImport = true;
                            break;
                        }
                    }
                    
                    i = in.readLine(line, 0, 128);
                }
            }
        } catch (ModelControlException se) {
            Toolbox2.prepareErrorDisplay(se, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE));
            m_bShowAlert =true;
        } catch (SPIException spie) {
            Toolbox2.prepareErrorDisplay(spie, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE));
            m_bShowAlert =true;
        }
        forwardTo(getRequestContext());
    }
    
    private String extractFilename(byte[] name, int length) throws IOException {
        if (name == null) {
            return null;
        }
        
        String sFilename = "";
        
        try {
            ByteArrayInputStream    bais = new ByteArrayInputStream(name, 0, length);
            InputStreamReader       isr  = new InputStreamReader(bais, "UTF-8");
            BufferedReader          br   = new BufferedReader(isr);
            
            sFilename = br.readLine();
            
        }
        catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
        
        int  pos = sFilename.lastIndexOf("\\");
        
        if (pos == -1) {
            pos = sFilename.lastIndexOf("/");
        }
        
        if (pos != -1) {
            sFilename = sFilename.substring(pos + 1);
        }
        
        int dotLoc = sFilename.lastIndexOf(".");
        
        if (dotLoc != -1) {
            sFilename = sFilename.substring(0, dotLoc);
        }
        
        return sFilename;
    }
    
  

    private Iterator getProfilesIterator() {
        Iterator profiles = null;
        try {
            Profile newProfile = null;
            String sQuery = getRequestContext().getRequest().getQueryString();
            String sModifier = sQuery.substring(sQuery.indexOf("=")+1);
            if (sQuery.indexOf("&") != -1) {
                sModifier = sQuery.substring(sQuery.indexOf("=")+1, sQuery.indexOf("&"));
            }
            PolicyManager pmgr = Toolbox2.getPolicyManager();
            Entity entity = null;
            if (sModifier!=null && sModifier.equals("AvailableDomainTableView")) {
                entity = pmgr.getRootEntity(EnvironmentConstants.HOST_SOURCE);
            } else if (sModifier!=null && sModifier.equals("AvailableOrgTableView")) {
                entity = pmgr.getRootEntity(EnvironmentConstants.USER_SOURCE);
            } else {
                entity = Toolbox2.getSelectedEntity();
            }
            
            Entity writableEntity = entity;
            Applicability use = Applicability.USER;

            while (entity!=null) {
                ProfileRepository repository = entity.getProfileRepository();
                if (!repository.isReadOnly()) {
                    writableEntity = entity;
                }
                entity = entity.getParent();
            }

            if (writableEntity.getPolicySourceName().equals(EnvironmentConstants.HOST_SOURCE)) {
                use = Applicability.HOST;
            } 
            ProfileRepository repository = writableEntity.getProfileRepository();
            profiles    = repository.getProfiles(use);
        } catch (SPIException spie) {
            CCDebug.trace3(spie.toString());
        } 
        return profiles;
    }
    
    private void setExistingProfileAssignments(Iterator it) {
        m_existingProfileAssignments = it;
    }
    
    private Iterator getExistingProfilesAssignments() {
        return m_existingProfileAssignments;
    }
}
