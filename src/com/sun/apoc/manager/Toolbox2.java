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

import com.iplanet.jato.ModelManager;
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.Model;
import com.iplanet.jato.util.RootCauseException;
import com.iplanet.jato.view.View;
import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.iplanet.sso.SSOTokenManager;
import com.sun.apoc.manager.contexts.ManagerTableModel;
import com.sun.management.services.authentication.UserPrincipal;
import com.sun.management.services.authentication.PasswordCredential;

import com.sun.apoc.manager.entities.NavigationModel;
import com.sun.apoc.templates.parsing.TemplatePage;
import com.sun.apoc.templates.parsing.TemplateProperty;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.apoc.templates.parsing.TemplateSection;
import com.sun.apoc.manager.profiles.ProfileWindowModel;
import com.sun.apoc.manager.report.PolicyMgrReportHelper;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.entities.Leaf;
import com.sun.apoc.spi.entities.Node;
import com.sun.apoc.spi.entities.Role;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.management.services.common.ConsoleInfo;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.common.CCSystem;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.web.ui.view.alert.CCAlertFullPage;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCStaticTextField;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
public abstract class Toolbox2 {
    
    // ---
    // --- Debug Helpers
    // ---
    
    public static void stamp(String sMessage) {
        long               nNow    = System.currentTimeMillis();
        HttpServletRequest request = RequestManager.getRequest();
        Long               nStamp  = (Long) request.getAttribute("myTimeStamp");
        if (nStamp==null) {
            nStamp = new Long(nNow);
        }
        
        SimpleDateFormat dateFormat= new SimpleDateFormat("HH:mm:ss.SSS");
        DecimalFormat decimalFormat= new DecimalFormat("0000.#");
        
        CCDebug.trace3("---+++--- "+
            dateFormat.format(new Date(nNow))+
            "+"+
            decimalFormat.format(nNow-nStamp.longValue())+
            " "+
            sMessage);
        
        nStamp = new Long(nNow);
        request.setAttribute("myTimeStamp", nStamp);
    }
    
    public static void prepareErrorDisplay(Throwable throwable, View aAlert, View aTrace) {
        CCAlert           alert     = (CCAlert) aAlert;
        CCStaticTextField trace     = (CCStaticTextField) aTrace;
        String            sSummary  = getSummary(throwable);
        String            sTrace    = getChainedStackTrace(throwable);

        alert.setValue(CCAlert.TYPE_ERROR);
        
        if (alert instanceof CCAlertInline) {
            ((CCAlertInline) alert).setSummary(sSummary);
            ((CCAlertInline) alert).setDetail("");
        }
        
        if (alert instanceof CCAlertFullPage) {
            ((CCAlertFullPage)alert).setSummary(sSummary);
            ((CCAlertFullPage)alert).setDetail("");
        }

        trace.setValue(sTrace);

        CCDebug.trace1(sSummary);
        CCDebug.trace1(sTrace);
        
    }
    
    public static String getChainedStackTrace(Throwable throwable) {

        StringBuffer        sChainedTrace   = new StringBuffer();
        Throwable           scout           = throwable;
        LinkedList          chain           = new LinkedList();

        while (scout != null) {
            
            throwable = scout;
            chain.add(scout);
            
            if (throwable instanceof ServletException) {
                scout = ((ServletException) throwable).getRootCause();
            }
            else if (throwable instanceof RootCauseException) {
                scout = ((RootCauseException) throwable).getRootCause();
            }
            else {
                scout = throwable.getCause();
            }
        }
        
        int nLevel = chain.size()-1;
        
        while (nLevel>=0) {
            
            throwable = (Throwable) chain.get(nLevel);

            sChainedTrace.append("\n");
            sChainedTrace.append(throwable.toString());
            sChainedTrace.append("\n");

            StackTraceElement[] stack           = throwable.getStackTrace();
            int                 nStackPos       = stack.length-1;
            int                 nScoutStackPos  = 0;

            if (nLevel>0) {
                scout = (Throwable) chain.get(nLevel-1);
                StackTraceElement[] scoutStack = scout.getStackTrace();
                nScoutStackPos = scoutStack.length-1;

                while ((nScoutStackPos>=0) && ((nStackPos>=0))) {
                    if (!scoutStack[nScoutStackPos].equals(stack[nStackPos])) {
                        break;
                    }
                    nScoutStackPos--;
                    nStackPos--;
                }
            }
            
            for (int stackRunner=0; stackRunner<=nStackPos; stackRunner++) {
                sChainedTrace.append("  ");
                if (stackRunner<10) {
                    sChainedTrace.append("  ");
                } else if (stackRunner<100) {
                    sChainedTrace.append(" ");
                }
                sChainedTrace.append(stackRunner);
                sChainedTrace.append(" ");
                sChainedTrace.append(stack[stackRunner].toString());
                sChainedTrace.append("\n");
            }
            
            if ((nLevel>0) && (nStackPos!=stack.length-1)) {
                sChainedTrace.append("      ... continued in the following exception's stack at element # ");
                sChainedTrace.append(nScoutStackPos+1);
                sChainedTrace.append("\n");
            }
            nLevel--;
        }
        return sChainedTrace.toString();
    }
    
    public static String getSummary(Throwable throwable) {
        String      sSummary;
        Throwable   scout = throwable;
        
        while ((scout != null) && !(throwable instanceof SPIException)) {
            throwable = scout;
            
            if (throwable instanceof ServletException) {
                scout = ((ServletException) throwable).getRootCause();
            }
            else if (throwable instanceof RootCauseException) {
                scout = ((RootCauseException) throwable).getRootCause();
            }
            else {
                scout = throwable.getCause();
            }
        }

        if (throwable instanceof SPIException) {
            sSummary = ((SPIException) throwable).getLocalizedMessage(getLocale());
        } else {
            sSummary = getI18n("APOC.policies.error.general");
        }
        
        return sSummary;
    }
    
    // ---
    // --- String Helpers
    // ---
    
    public static boolean isMatch(String sData, String sFilter) {
        int             nCurrentDataPosition = 0;
        int             nMatchPosition = 0;
        StringTokenizer aTokenizer     = new StringTokenizer(sFilter, "*");
        String          sSubMatcher    = "";
        
        while (aTokenizer.hasMoreTokens()) {
            sSubMatcher        = (String) aTokenizer.nextElement();
            nMatchPosition     = sData.indexOf(sSubMatcher, nCurrentDataPosition);
            
            if ((nCurrentDataPosition == 0) && (!sFilter.startsWith("*")) && (nMatchPosition != 0)) {
                return false;
            }
            
            if (nMatchPosition == -1) {
                return false;
            }
            
            nCurrentDataPosition = nMatchPosition + sSubMatcher.length();
        }
        
        if (sFilter.endsWith("*") || (nCurrentDataPosition == sData.length())) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public static String getI18n(String sBundleKey) {
        CCI18N i18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        return i18n.getMessage(sBundleKey);
    }
    
    public static String getI18n(String sBundleKey, Object[] args) {
        CCI18N i18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        return i18n.getMessage(sBundleKey, args);
    }
    
    public static Locale getLocale() {
        CCI18N         i18n    = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        ResourceBundle bundle  = i18n.getResourceBundle();
        return bundle.getLocale();
    }
    
    // ---
    // --- Servlet Helpers
    // ---
    
    public static String getParameter(String sPartialKey) {
        String             sParamValue;
        HttpServletRequest request         = RequestManager.getRequest();
        Enumeration        aParamNamesEnum = request.getParameterNames();
        
        sParamValue = request.getParameter(sPartialKey);
        
        if (sParamValue == null) {
            sParamValue = "";
            
            while (aParamNamesEnum.hasMoreElements()) {
                String sParamName = (String) aParamNamesEnum.nextElement();
                
                if (sParamName.endsWith("." + sPartialKey)) {
                    sParamValue = request.getParameter(sParamName);
                    
                    // break out only if some content was found, go ahead searching if not
                    if ((sParamValue != null) && (sParamValue.length() > 0)) {
                        break;
                    }
                }
            }
        }
        
        return sParamValue;
    }
    
    public static String getQuery(String sPartialKey) {
        String sQuery = RequestManager.getRequest().getQueryString();
        sQuery = decode(sQuery);
        if (sPartialKey==null || sPartialKey.length()==0) {
            return sQuery;
        }
        
        int nKeyPos = sQuery.indexOf(sPartialKey);
        if (nKeyPos<0) {return "";}
        
        int nValueBeginPos = sQuery.indexOf("=", nKeyPos)+1;
        if (nValueBeginPos==0) {return "";}

        int nValueEndPos = sQuery.indexOf("&", nValueBeginPos);
        if (nValueEndPos<0) {
            nValueEndPos =  sQuery.length();
        }
        
        return sQuery.substring(nValueBeginPos, nValueEndPos);
    }
    
    static String getCharacterEncoding() {
        CCI18N.initContentType(RequestManager.getRequest(), RequestManager.getResponse());
        String enc = RequestManager.getResponse().getCharacterEncoding();
        if (enc == null) {
            enc = "UTF-8";
        }
        return enc;
    }
    
    public static String decode(String string) {
        if (string == null) {
            return null;
        }
        
        try {
            string = URLDecoder.decode(string, getCharacterEncoding());
        }
        catch (UnsupportedEncodingException uee) {
            CCDebug.trace1("", uee);
        }
        
        return string;
    }
    
    public static String encode(String string) {
        if (string == null) {
            return null;
        }
        
        try {
            string = URLEncoder.encode(string, getCharacterEncoding());
        }
        catch (UnsupportedEncodingException uee) {
            CCDebug.trace1("", uee);
        }
        
        return string;
    }
    
    // ---
    // --- Policy Manager Helpers
    // ---
    
    public static PolicyManager getPolicyManager() {
        HttpServletRequest request = RequestManager.getRequest();
        
        //        if ((request.getSession(false) == null) ||
        //        (request.getSession(false).getAttribute(Constants.POLICY_MANAGER) == null)) {
        //            initPolicyManager(RequestManager.getRequestContext());
        //        }
        
     //   PolicyMgr policyManager = (PolicyMgr) request.getSession(false).getAttribute(Constants.POLICY_MANAGER);
        PolicyManager policyManager = (PolicyManager) request.getSession(false).getAttribute(Constants.POLICY_MANAGER);
        return policyManager;
    }
    
    public static PolicyMgrReportHelper getPolicyMgrHelper() {
        HttpServletRequest request = RequestManager.getRequest();
        return (PolicyMgrReportHelper) request.getSession(false).getAttribute(Constants.POLICY_MANAGER_HELPER);
    }
    
    public static void setPolicyManager(String sBackendname, String sUsername, String sPassword)
    throws SPIException, FileNotFoundException, IOException, SSOException {
        
        RequestContext  requestContext  = RequestManager.getRequestContext();
        PolicyManager       oldPolicyManager= (PolicyManager) requestContext.getRequest().getSession(true).getAttribute(Constants.POLICY_MANAGER);
        PolicyManager       policyManager   = createPolicyManager(sBackendname, sUsername, sPassword, true);
        
        if (oldPolicyManager!=null) {
            oldPolicyManager.close();
            Model navModel = requestContext.getModelManager().getModel(NavigationModel.class, "NavTree", true, false);
            requestContext.getModelManager().removeFromSession(navModel);
            requestContext.getRequest().getSession(true).removeAttribute(Constants.SELECTED_ENTITY);
        }
        
        requestContext.getRequest().getSession(true).setAttribute(Constants.POLICY_MANAGER_NAME, sBackendname);
        requestContext.getRequest().getSession(true).setAttribute(Constants.POLICY_MANAGER, policyManager);
    }
    
    public static void unsetPolicyManager() throws SPIException {
        RequestContext  requestContext  = RequestManager.getRequestContext();
        PolicyManager       oldPolicyManager= (PolicyManager) requestContext.getRequest().getSession(true).getAttribute(Constants.POLICY_MANAGER);
        
        if (oldPolicyManager!=null) {
            oldPolicyManager.close();
            Model navModel = requestContext.getModelManager().getModel(NavigationModel.class, "NavTree", true, false);
            requestContext.getModelManager().removeFromSession(navModel);
            requestContext.getRequest().getSession(true).removeAttribute(Constants.SELECTED_ENTITY);
            requestContext.getRequest().getSession(true).removeAttribute(Constants.POLICY_MANAGER_NAME);
            requestContext.getRequest().getSession(true).removeAttribute(Constants.POLICY_MANAGER);
        }
    }
    
    public static PolicyManager createPolicyManager(String sBackendname, String sUsername, String sPassword, boolean bStoreAuthenticated)
    throws SPIException, FileNotFoundException, IOException, SSOException {

        Properties backendProperties = new Properties();
        Properties selectedBackendProperties = new Properties();
        RequestContext  requestContext = RequestManager.getRequestContext();
        File dir = new File(ManagerTableModel.CONFIG_FILE_LOCATION);
        if (dir.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".properties");
                }
            };
            File[] files = dir.listFiles(filter);
            int i = 0;
            while (i < files.length) {
                File properties = files[i];
                if (properties.canRead()) {
                    FileInputStream input = new FileInputStream(properties) ;
                    backendProperties = new Properties();
                    backendProperties.load(input);
                    input.close();
                    if (backendProperties.getProperty("Backend") != null) {
                        String sName = backendProperties.getProperty("Backend");
                        if (sName.equals(sBackendname)) {
                            Enumeration allKeys = backendProperties.keys();
                            while (allKeys.hasMoreElements()) {
                                String sKey = (String) allKeys.nextElement();
                                if (!(sKey.equals("Backend"))){
                                    selectedBackendProperties.put(sKey, backendProperties.get(sKey));
                                }
                            }                            
                            break;
                        }
                    }
                }
                i++;
            }
        }
        PolicyManager policyManager = null;

        HashMap authorizedContexts = (HashMap) requestContext.getRequest().getSession(true).getAttribute(Constants.AUTH_CONTEXTS);
        if (authorizedContexts==null) {
            authorizedContexts = new HashMap();
            requestContext.getRequest().getSession(true).setAttribute(Constants.AUTH_CONTEXTS, authorizedContexts);
        }

        if (sUsername==null || sUsername.length()==0) {

            if (authorizedContexts.containsKey(sBackendname)) {
                sUsername = ((String[]) authorizedContexts.get(sBackendname))[0];
                sPassword = ((String[]) authorizedContexts.get(sBackendname))[1];
            } else {
                SSOTokenManager tokenmanger     = SSOTokenManager.getInstance();
                SSOToken        token           = tokenmanger.createSSOToken(requestContext.getRequest());
                Subject         subject         = token.getSubject();

                if (subject != null) {
                    Set      principals     = subject.getPrincipals(UserPrincipal.class);
                    Set      credentials    = subject.getPrivateCredentials(PasswordCredential.class);
                    Iterator principalIter  = principals.iterator();
                    Iterator credentialIter = credentials.iterator();

                    while ((principalIter.hasNext()) && (sUsername == null)) {
                        sUsername = ((UserPrincipal) principalIter.next()).getUserName();
                    }

                    while ((credentialIter.hasNext()) && (sPassword == null)) {
                        sPassword = new String(((PasswordCredential) credentialIter.next()).getUserPassword());
                    }
                }
            }
        }

        selectedBackendProperties.setProperty(EnvironmentConstants.USER_KEY, sUsername);
        selectedBackendProperties.setProperty(EnvironmentConstants.CREDENTIALS_KEY, sPassword);
        selectedBackendProperties.setProperty(Constants.POLICY_MANAGER_NAME, sBackendname);

        policyManager = new PolicyManager(selectedBackendProperties);
        // Test for read permissions - need to at least be able to read from the LDAP server
        policyManager.getRootEntity(policyManager.getSources()[0]).getParent();
        
        //authorization successful

        if (bStoreAuthenticated) {
            String[] authentification = new String[]{sUsername, sPassword};
            authorizedContexts.put(sBackendname, authentification);
        }

        return policyManager;         

    }
     

    
    public static Iterator getBackendNames() {
        
        TreeSet    backendNames    = new TreeSet();  
        Properties backendProperties = new Properties();
        RequestContext  requestContext = RequestManager.getRequestContext();
        File dir = new File(ManagerTableModel.CONFIG_FILE_LOCATION);
        try {
            if (dir.exists()) {
                FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".properties");
                    }
                };
                File[] files = dir.listFiles(filter);
                int i = 0;
                while (i < files.length) {
                    File properties = files[i];
                    if (properties.canRead()) {
                        FileInputStream input = new FileInputStream(properties) ;
                        backendProperties = new Properties();
                        backendProperties.load(input);
                        input.close();
                        if (backendProperties.getProperty("Backend") != null) {
                            String sName = backendProperties.getProperty("Backend");
                            backendNames.add(sName);
                        }
                    }
                    i++;
                }
            }
        }
        catch (Exception e) {
            CCDebug.trace1("", e);
        }
        
        Iterator it = backendNames.iterator();
        return it;
    }
    
    public static String getBackendType(String backendName) {
        HashMap backendTypes = (HashMap) RequestManager.getRequestContext().getRequest().getSession(true).getAttribute(Constants.BACKEND_TYPES);       
        return backendTypes != null ? (String)backendTypes.get(backendName) : null;
    }
    
    public static Entity getSelectedEntity() {
        HttpSession httpSession = RequestManager.getRequest().getSession(true);
        Entity      entity      = (Entity) httpSession.getAttribute(Constants.SELECTED_ENTITY);
        
        //this is clearly a side effect, but all other solutions are even worse
        if (entity==null) {
            ModelManager    modelManager    = RequestManager.getRequestContext().getModelManager();
            NavigationModel navModel        = (NavigationModel) modelManager.getModel(NavigationModel.class,"NavTree", true, true);
            httpSession.setAttribute(Constants.SELECTED_ENTITY, navModel.getStoredValue(navModel.NODE_ROOT_DOMAIN));
        }
        
        return entity;
    }
    
    public static String getSelectedEntityId() {
        String             sEntity = "";
        
        Entity entity = getSelectedEntity();
        
        if (entity!=null) {
            sEntity = entity.getId();
        }
        
        return sEntity;
    }
    
    public static String getOrganizationRoot() {
        String sUserRoot = "";
        
        try {
            sUserRoot = getPolicyManager().getRootEntity(EnvironmentConstants.USER_SOURCE).getId();
        }
        catch (SPIException se) {
            CCDebug.trace1("", se);
        }
        
        return sUserRoot;
    }
    
    public static String getDomainRoot() {
        String sHostRoot = "";
        
        try {
            sHostRoot = getPolicyManager().getRootEntity(EnvironmentConstants.HOST_SOURCE).getId();
        }
        catch (SPIException se) {
            CCDebug.trace1("", se);
        }
        
        return sHostRoot;
    }
    
    public static String getDisplayName(Entity entity) {
        
        String sDisplayName = "";
        
        try {
            if (entity!=null) {
                if (entity.equals(getPolicyManager().getRootEntity(EnvironmentConstants.USER_SOURCE))) {
                    sDisplayName = getI18n("APOC.navigation.organizations");
                }
                else if (entity.equals(getPolicyManager().getRootEntity(EnvironmentConstants.HOST_SOURCE))) {
                    sDisplayName = getI18n("APOC.navigation.domains");
                } else {
                    sDisplayName = entity.getDisplayName(getLocale());
                }
            }
        }
        catch (SPIException se) {
            CCDebug.trace1("", se);
        }
        
        return sDisplayName;
    }
    
    public static String getDisplayName(String source, String sId) {
        
        String sDisplayName = "";
        
        try {
            Entity entity = getPolicyManager().getEntity(source, sId);
            
            if (entity!=null) {
                sDisplayName = getDisplayName(entity);
            } else {
                Profile profile = getPolicyManager().getProfile(sId);
                if (profile!=null) {
                    sDisplayName = profile.getDisplayName();
                } else {
                    sDisplayName = (String) RequestManager.getRequest().getSession(true).getAttribute(Constants.POLICY_MANAGER_NAME);
                }
            }
        }
        catch (SPIException se) {
            CCDebug.trace1("", se);
        }
        
        return sDisplayName;
    }
    
    public static String buildEntityTitle(String sStaticPart) {
        String         sEntityTitle;
        StringBuffer   entityTitle    = new StringBuffer();
        String         sResourceValue = sStaticPart;
        
        try {
            Entity entity = getSelectedEntity();
            
            if (entity==null) {
                entityTitle.append(getPolicyManager().getRootEntity(EnvironmentConstants.USER_SOURCE).getDisplayName(getLocale()));
            }
            else if (entity.equals(getPolicyManager().getRootEntity(EnvironmentConstants.USER_SOURCE))) {
                entityTitle.append(getI18n("APOC.navigation.organizations"));
            }
            else if (entity.equals(getPolicyManager().getRootEntity(EnvironmentConstants.HOST_SOURCE))) {
                entityTitle.append(getI18n("APOC.navigation.domains"));
            } else {
                entityTitle.append(entity.getDisplayName(getLocale()));
            }
            
            if ((sStaticPart!=null) && (sStaticPart.length()>0)) {
                entityTitle.append(" - ");
                try {
                    sResourceValue = getI18n(sStaticPart);
                    if (sResourceValue != null) {
                        entityTitle.append(sResourceValue);
                    }
                }
                catch (java.lang.Exception e) {
                    entityTitle.append(sStaticPart);
                }
            }
        }
        catch (java.lang.Exception e) {
            CCDebug.trace1("", e);
        }
        
        return entityTitle.toString();
    }
    
    public static String buildProfileTitle(String sStaticPart) {
        String         sEntityTitle;
        CCI18N         i18n           = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        ResourceBundle bundle         = i18n.getResourceBundle();
        StringBuffer   entityTitle    = new StringBuffer();
        String         sResourceValue = sStaticPart;
        Profile        profile        = ProfileWindowFramesetViewBean.getSelectedProfile();
        
        try {
            sResourceValue = bundle.getString(sStaticPart);
            if (sResourceValue != null) {
                entityTitle.append(sResourceValue);
            }
        }
        catch (java.lang.Exception e) {
            entityTitle.append(sStaticPart);
        }
        
        if (profile != null) {
            entityTitle.append(getI18n("APOC.profile.name.text",new Object[]{profile.getDisplayName()}));
        }
        
        return entityTitle.toString();
    }
    
   
    public static String getEntityTypeResource(Entity entity) {
        String sResource = "";
        String source = entity.getPolicySourceName() ;
        
        if  (entity instanceof Node) {
            if (source.equals(EnvironmentConstants.USER_SOURCE)) {
                sResource = "APOC.navigation.organization";
            }
            else if (source.equals(EnvironmentConstants.HOST_SOURCE)) {
                sResource = "APOC.navigation.domain";
            }
        }
        else if (entity instanceof Leaf) {
            if (source.equals(EnvironmentConstants.USER_SOURCE)) {
                sResource = "APOC.navigation.user";
            }
            else if (source.equals(EnvironmentConstants.HOST_SOURCE)) {
                sResource = "APOC.navigation.host";
            }
        }
        else if (entity instanceof Role) {
            sResource = "APOC.navigation.role";
        }
        return sResource;
    }
    
    public static String getEntityTypeString(Entity entity) {
        String entityType = EnvironmentConstants.USER_SOURCE;
        if (isDomainSubtype(entity)) {
            entityType = EnvironmentConstants.HOST_SOURCE;
        } 
        return entityType;
    }
    
    public static ListIterator getParentPath(String sEntityType, String sEntityId) {
        return getParentPath(sEntityType, sEntityId, getPolicyManager());
    }
    
    public static ListIterator getParentPath(String sEntityType, String sEntityId, PolicyManager policyManager) {
        LinkedList  list    = new LinkedList();
        Entity      entity  = null;
        
        try {
            entity = policyManager.getEntity(sEntityType, sEntityId);
        }
        catch (SPIException se) {
            CCDebug.trace1("", se);
        }
        
        return getParentPath(entity, policyManager);
    }
    
    public static ListIterator getParentPath(Entity entity, PolicyManager policyManager) {
        LinkedList list = new LinkedList();
        
        try {
            while ( (entity = entity.getParent()) !=null) {
                list.add(0,entity);
            }
        }
        catch (Exception e) {
            CCDebug.trace1("", e);
        }
        
        return list.listIterator();
    }
    
    public static String getParentagePath(String sEntity, String sEntityType, boolean showServer, boolean showSelf, String sSeparator) {
        return getParentagePath(sEntity, sEntityType, showServer, showSelf, sSeparator, true);
    }   

    public static String getParentagePath(String sEntity, String sEntityType, boolean showServer, boolean showSelf, String sSeparator, boolean bLocalise) {
        Entity entity = null;
        try {
            if (getPolicyManager() != null) {
                entity = getPolicyManager().getEntity(sEntityType, sEntity);
            } else {
                return "";
            }
        }
        catch (SPIException se) {
            CCDebug.trace1("", se);
        }
        return getParentagePath(entity, showServer, showSelf, sSeparator, getPolicyManager(), bLocalise);
    }
        
    public static String getParentagePath(Entity entity, boolean showServer, boolean showSelf, String sSeparator) {
        return getParentagePath(entity, showServer, showSelf, sSeparator, getPolicyManager());
    }
    
    public static String getParentagePath(Entity entity, boolean showServer, boolean showSelf, String sSeparator, PolicyManager policyManager) {
        return getParentagePath(entity, showServer, showSelf, sSeparator, policyManager, true);
    }
    
    public static String getParentagePath(Entity entity, boolean showServer, boolean showSelf, String sSeparator, PolicyManager policyManager, boolean bLocalise) {
        StringBuffer sPath = new StringBuffer();
        
        if (sSeparator==null) {
            sSeparator=">";
        }
        
        if (showServer) {
            try {
                String sBackendname = (String) policyManager.getEnvironment().get(Constants.POLICY_MANAGER_NAME);
                sPath.append(sBackendname);
            }
            catch (Exception e) {
                CCDebug.trace1("", e);
            }
        }
        
        try {
            if (entity!=null) {
                //possible because the display name for org and dom roots are the same
                Iterator    iterator        = entity.getAncestorNames(getLocale());
                boolean     bFirstParticle  = true;
                String source = entity.getPolicySourceName() ;

                while (iterator.hasNext()) {
                    String sDisplayName = (String) iterator.next();
                    if (bFirstParticle) {
                        bFirstParticle=false;
                        if (source.equals(EnvironmentConstants.HOST_SOURCE)) {
                            //getI18n requires request context, sync thread doesn't have any
                            sDisplayName = bLocalise?getI18n("APOC.navigation.domains"):"Domain";
                        } else if (source.equals(EnvironmentConstants.USER_SOURCE)){
                            sDisplayName = bLocalise?getI18n("APOC.navigation.organizations"):"Organization";
                        }
                    }
                    if (sPath.length()>0) {
                        sPath.append(" ");
                        sPath.append(sSeparator);
                        sPath.append(" ");
                    }
                    sPath.append(sDisplayName);
                }
                
                if (showSelf) {
                    String sDisplayName = "";
                    if (entity.equals(policyManager.getRootEntity(EnvironmentConstants.USER_SOURCE))) {
                        //getI18n requires request context, sync thread doesn't have any
                        sDisplayName = bLocalise?getI18n("APOC.navigation.organizations"):"Organization";
                    } else if (entity.equals(policyManager.getRootEntity(EnvironmentConstants.HOST_SOURCE))){
                        sDisplayName = bLocalise?getI18n("APOC.navigation.domains"):"Domain";
                    } else {
                        sDisplayName = entity.getDisplayName(getLocale());
                    }
                    if (sPath.length()>0) {
                        sPath.append(" ");
                        sPath.append(sSeparator);
                        sPath.append(" ");
                    }
                    sPath.append(sDisplayName);
                }
            }
        }
        catch (SPIException se) {
            CCDebug.trace1("", se);
        }
        
        return sPath.toString();
    }
    
    public static boolean isSubType(String aEntityType, String sEntityId) {
        Entity      entity          = null;
        PolicyManager   policyManager   = getPolicyManager();
        
        try {
            entity = policyManager.getEntity(aEntityType, sEntityId);
        }
        catch (Exception e) {
            CCDebug.trace1("", e);
            return false;
        }
        if (entity == null) {
            return false;
        }
        return isSubType(aEntityType, entity);
    }
    
     public static boolean isSubType(String aEntityType, Entity entity) {
        if (aEntityType.equals(EnvironmentConstants.HOST_SOURCE)) {
            return isDomainSubtype(entity); 
        } else if (aEntityType.equals(EnvironmentConstants.USER_SOURCE)) {
            return isOrganizationSubtype(entity); 
        }
        return false;
    }
     
    public static boolean isDomainSubtype(Entity entity) {
        return entity.getPolicySourceName().equals(EnvironmentConstants.HOST_SOURCE);
    }
    
    public static boolean isOrganizationSubtype(Entity entity) {
        return entity.getPolicySourceName().equals(EnvironmentConstants.USER_SOURCE);
    }
    
    public static LinkedList getEntityList(Entity entity) {
        LinkedList  list            = new LinkedList();
        list.add(entity);
        
        try {
            while ( (entity = entity.getParent()) !=null) {
                list.add(0,entity);
            }
        }
        catch (Exception e) {
            CCDebug.trace1("", e);
        }
        return list;
    }
    
    public static LinkedList getPolicyGroupList(Iterator entityIterator) {
        Entity entity       = null;
        LinkedList  list    = new LinkedList();
        
        try {
            while ( (entity = (Entity)entityIterator.next()) != null) {
                Iterator it = entity.getAssignedProfiles();
                while (it.hasNext()){
                    list.add(0,it.next());
                }
            }
        }
        catch (Exception e) {
            CCDebug.trace1("", e);
        }
        return list;
    }

    public static void setPageTitleHelp(CCPageTitleModel model, String title, String help, String helpfile) {
        CCI18N i18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        StringBuffer buffer = new StringBuffer();
        Object[] args = {i18n.getMessage(title)};
        // Due to a bug in Lockhart 3.x, the helpwindow tag no longer works properly with JATO components.
        // Therefore need to generate our own context sensitive help links using ContextHelpHelper class.
        /*buffer.append("<helpwindow appName=\"apoc\" bundleID=\"apocBundle\" helpType=\"help2\" helpTooltip=\"APOC.help.helpTooltip\" helpFileName=\"")
            .append(helpfile)
            .append("\"> ")
            .append(i18n.getMessage("APOC.help.more.on", args))
            .append("</helpwindow>");
        String helpLink = buffer.toString();*/
        ContextHelpHelper helper = new ContextHelpHelper("apoc", "apocBundle", helpfile, "APOC.help.helpTooltip", i18n.getMessage("APOC.help.more.on", args));
        String helpLink = helper.getHelpLink();
        String helpText = i18n.getMessage(help);
        model.setPageTitleHelpMessage(helpText + " " + helpLink);        
    }
    
    public static String getPropertyPath(TemplateProperty prop) {
        // Returns the absolute path (in apt:name attributes) of this property using '/' as separator.
        String propPath = null ;
        RequestContext cxt = RequestManager.getRequestContext();
        TemplateRepository tempRep = TemplateRepository.getDefaultRepository() ;
        ProfileWindowModel editorModel = (ProfileWindowModel) cxt.getModelManager().getModel(ProfileWindowModel.class);
        String pagePath = editorModel.getSelectedCategory();
        TemplatePage page = tempRep.getPage(pagePath) ;
        TemplateSection section = null ;
        boolean sectionFound = false ;
        for(int i = 0; (i < page.getSections().size()) && (!sectionFound); i++) {
            for(int j = 0; (page.getSection(i).getProperties() != null) && (j < page.getSection(i).getProperties().size()); j++) {
                if(page.getSection(i).getProperty(j).getDefaultName().equals(prop.getDefaultName())) {
                    section = page.getSection(i) ;
                    sectionFound = true ;
                    break ;
                }
            }
        }
        propPath = pagePath + "/" + section.getDefaultName() + "/" + prop.getDefaultName() ;
        return propPath ;
    }
    
    
    public static TemplateProperty getProperty(String propPath) {
        // Given an absolute path (in apt:name attributes) to a property, returns the
        // TemplateProperty object pointed to by the path
        TemplateRepository tempRep = TemplateRepository.getDefaultRepository() ;
        
        int index = propPath.lastIndexOf('/');
        String pagePath = propPath.substring(0, index);
        index = pagePath.lastIndexOf('/');
        pagePath = pagePath.substring(0, index);
        TemplatePage tempPage = tempRep.getPage(pagePath) ;
        
        String[] splitString = propPath.split("/") ;
        String sectionName = splitString[splitString.length-2];
        String propertyName= splitString[splitString.length-1];
        
        TemplateSection tempSection = tempPage.getSection(sectionName) ;
        TemplateProperty tempProp = null ;
        for(int i = 0; i < tempSection.getProperties().size(); i++) {
            if(tempSection.getProperty(i).getDefaultName().equals(propertyName)) {
                tempProp = tempSection.getProperty(i) ;
                break ;
            }
        }
        return tempProp ;
    }

}
