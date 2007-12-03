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

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.ContainerView;
import com.iplanet.jato.view.DisplayFieldImpl;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.iplanet.jato.util.HtmlUtil;
import com.iplanet.jato.view.BasicCommandField;
import com.iplanet.jato.view.event.DisplayEvent;

import com.sun.apoc.manager.AlertViewBean;
import com.sun.apoc.manager.Toolbox2;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.table.CCActionTable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.model.CCWizardWindowModel;
import com.sun.web.ui.model.CCWizardWindowModelInterface;
import com.sun.web.ui.view.wizard.CCWizardWindow;

import com.sun.apoc.manager.Constants;
import com.sun.apoc.spi.environment.EnvironmentConstants;

abstract public class AvailableTableView extends RequestHandlingViewBase {
    public static final String      CHILD_PROFILE_TABLE     = "AvailableTable";
    public static final String      CHILD_ACTION_MENU       = "ActionMenu";
    public static final String      CHILD_ACTION_MENU_HREF  = "ActionMenuHref";
    public static final String      CHILD_IMPORTGROUP       = "TabHref";
    public static final String      NEW_ACTION              = "0";
    public static final String      RENAME_ACTION           = "1";
    public static final String      EDITLOC_ACTION          = "2";
    public static final String      EDITPRIOS_ACTION        = "3";
    public static final String      SHOWALL_ACTION          = "4";
    public static final String      IMPORT_ACTION           = "5";
    public static final String      EXPORT_ACTION           = "6";
    public static final String      DELETE_ACTION           = "7";
    protected AvailableTableModel   m_Model                 = null;

    public static CopyMoveWizardPageModel  m_wizardPageModel;
    public static final String             WIZARDPAGEMODELNAME          = "copyMovePageModelName";
    public static final String             WIZARDPAGEMODELNAME_PREFIX   = "CopyMoveWizardPageModel";
    public static final String             WIZARDIMPLNAME               = "copyMoveWizardImplName";
    public static final String             WIZARDIMPLNAME_PREFIX        = "CopyMoveWizardImpl";
    public static final String             CHILD_WIZARDWINDOW           = "copyMoveWizardWindow";
    public static final String             CHILD_FRWD_TO_CMDCHILD       = "copyMoveForwardToVb";
        
    private String                  m_pageModelName;
    private String                  m_wizardImplName;
    private boolean                 m_wizardLaunched        = false;
    
    public AvailableTableView(View parent, String name) {
        super(parent, name);
        m_Model = getModel();
        registerChildren();
    }
    
    public AvailableTableModel getModel() {
        if (m_Model == null) {
            m_Model = (AvailableTableModel) getRequestContext().getModelManager().getModel(AvailableTableModel.class, Integer.toString(hashCode()));
            m_Model.setDocument(getRequestContext().getServletContext(), "/jsp/profiles/AvailableTable.xml");
        }
        return m_Model;
    }

    protected void registerChildren() {
        registerChild(CHILD_PROFILE_TABLE, CCActionTable.class);
        registerChild(CHILD_ACTION_MENU_HREF, CCHref.class);
        registerChild(CHILD_WIZARDWINDOW, CCWizardWindow.class);
        registerChild(CHILD_FRWD_TO_CMDCHILD, BasicCommandField.class);
        m_Model.registerChildren(this);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_PROFILE_TABLE)) {
            CCActionTable child = new CCActionTable(this, m_Model, name);
            return child;
        }
        else if (name.equals(CHILD_ACTION_MENU_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_WIZARDWINDOW)) {
            Boolean launched = (Boolean) getRootView().getPageSessionAttribute("COPYMOVEWIZARDLAUNCHED");
            boolean init = launched == null || launched.booleanValue() == false;
            CCWizardWindowModel wizWinModel = createModel(init);
            CCWizardWindow child = new CCWizardWindow(this, wizWinModel, name, null);
            child.setDisabled(!init);
            return child;
        } else if (name.equals(CHILD_FRWD_TO_CMDCHILD)) {
            BasicCommandField bcf = new BasicCommandField(this, name);
            return bcf;
        }
        else if (m_Model.isChildSupported(name)) {
            return m_Model.createChild(this, name);
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleNameHrefRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        getRootView().forwardTo(getRequestContext());
    }
    
    public void handleActionMenuHrefRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        // The state data is not used to retrieve the selections of the action table 
        // but the ids of the selected profiles and other data is transfered in an extra
        // hidden field. This allows increase the speed by nearly factor 2
        // as we have to retrieve the data only once, after the actions took place
        View   command      = ((ContainerView) getParent()).getChild(AvailableView.CHILD_COMMAND_HIDDEN);
        View   parameters   = ((ContainerView) getParent()).getChild(AvailableView.CHILD_PARAMETERS_HIDDEN);
        String sCommand     = (String) ((DisplayFieldImpl)command).getValue();
        String sParameters  = (String) ((DisplayFieldImpl)parameters).getValue();
        
        try {
            if (sCommand.equals(DELETE_ACTION)) {
                StringTokenizer tokenizer   = new StringTokenizer(sParameters, " ");
                LinkedList      profiles    = new LinkedList();
                
                while (tokenizer.hasMoreTokens()) {
                    String sProfile = tokenizer.nextToken();
                    profiles.add(Toolbox2.decode(sProfile));
                }
                
                m_Model.destroy(profiles);
            }
            else if (sCommand.equals(RENAME_ACTION)) {
                int    nSeparatorPos= sParameters.indexOf(" ");
                String sProfileId   = sParameters.substring(0, nSeparatorPos);
                String sNewName     = sParameters.substring(nSeparatorPos+1);
                m_Model.rename(Toolbox2.decode(sProfileId), sNewName);
            }
            else if (sCommand.equals(EXPORT_ACTION)) {
                // I cannot use the ServletOutputStream directly because an exception thrown
                // by the SPI will disrupt that stream
                ByteArrayOutputStream streamCache = new ByteArrayOutputStream();
                m_Model.exportGroup(Toolbox2.decode(sParameters), streamCache);
                
                String sQuery = getRequestContext().getRequest().getQueryString();
                String profileSource = EnvironmentConstants.USER_SOURCE;
                if (sQuery.indexOf("AvailableDomainTableView") != -1) {
                    profileSource = EnvironmentConstants.HOST_SOURCE;
                }
                HttpServletResponse response = getRequestContext().getResponse();
                response.setHeader("Content-Type", "application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=" + Toolbox2.getDisplayName(profileSource, Toolbox2.decode(sParameters)) + ".zip;");

                ServletOutputStream stream = response.getOutputStream();
                stream.write(streamCache.toByteArray());
                stream.flush();
                stream.close();
            }
        }
        catch (ModelControlException mce) {
            Toolbox2.prepareErrorDisplay(mce,
            ((ContainerView)getParent()).getChild(AlertViewBean.CHILD_ALERT),
            ((ContainerView)getParent()).getChild(AlertViewBean.CHILD_STACKTRACE));
        }
        getRootView().forwardTo(getRequestContext());
    }
    
    public void handleCopyMoveWizardWindowRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        m_wizardLaunched = true;
        getRootView().forwardTo(getRequestContext());
    }
    

    public void handleCopyMoveForwardToVbRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        m_wizardLaunched = false;
        getRootView().forwardTo(getRequestContext());
    }
    
    public boolean beginChildDisplay(ChildDisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        
        if (event.getChildName().equals(CHILD_PROFILE_TABLE)) {
            int    nSize    = m_Model.getSize();
            int    nMaxRows = m_Model.getMaxRows();
            String sQuery   = getRequestContext().getRequest().getQueryString();
            
            if ((sQuery != null) && (sQuery.indexOf(CCActionTable.CHILD_PAGINATION_HREF) != -1)) {
                getRootView().setPageSessionAttribute("userPagination", new Boolean(true));
            }
            
            Object userPagination = getRootView().getPageSessionAttribute("userPagination");
            
            m_Model.setShowPaginationIcon(nSize > nMaxRows);
            m_Model.setShowPaginationControls(nSize > (nMaxRows * 5));
            
            if ((userPagination == null)) {
                String sChildName = getChild(CHILD_PROFILE_TABLE).getQualifiedName() + ".stateData";
                Map    stateData = (Map) getRootView().getPageSessionAttribute(sChildName);
                
                if (stateData != null) {
                    stateData.put("showPaginationControls", new Boolean((nSize > (nMaxRows * 5))));
                }
            }
        }
        return true;
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        getRootView().setPageSessionAttribute(WIZARDPAGEMODELNAME, getWizardPageModelName(WIZARDPAGEMODELNAME_PREFIX));
        getRootView().setPageSessionAttribute(WIZARDIMPLNAME, getWizardImplName(WIZARDIMPLNAME_PREFIX));
        getRootView().setPageSessionAttribute("COPYMOVEWIZARDLAUNCHED", new Boolean(m_wizardLaunched));
        ((CCWizardWindow)getChild(CHILD_WIZARDWINDOW)).setDisabled(m_wizardLaunched);
    }    
    
    private String getWizardPageModelName(String prefix) {
        
        if (m_pageModelName == null) {
            m_pageModelName = (String)
            getRootView().getPageSessionAttribute(WIZARDPAGEMODELNAME);
            if (m_pageModelName == null) {
                m_pageModelName = prefix + "_" + HtmlUtil.getUniqueValue();
                getRootView().setPageSessionAttribute(WIZARDPAGEMODELNAME, m_pageModelName);
            }
        }
        
        return m_pageModelName;
    }
    
    private String getWizardImplName(String prefix) {
        
        if (m_wizardImplName == null) {
            m_wizardImplName =
            (String)getRootView().getPageSessionAttribute(WIZARDIMPLNAME);
            if (m_wizardImplName == null) {
                m_wizardImplName = prefix + "_" + HtmlUtil.getUniqueValue();
                getRootView().setPageSessionAttribute(WIZARDIMPLNAME, m_wizardImplName);
            }
        }
        
        return m_wizardImplName;
    }

    
    private CCWizardWindowModel createModel(boolean init) {
        CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
        
        CCWizardWindowModel wizWinModel = new CCWizardWindowModel();
        wizWinModel.clear();
        if (init) {
            wizWinModel.setValue(CCWizardWindowModelInterface.MASTHEAD_ALT,
            "APOC.masthead.altText");
            wizWinModel.setValue(CCWizardWindowModelInterface.BASENAME,
            Constants.RES_BASE_NAME);
            wizWinModel.setValue(CCWizardWindowModelInterface.BUNDLEID,
            "apocBundle");
            wizWinModel.setValue(CCWizardWindowModelInterface.TITLE,
            "APOC.wiz.copy.title");
            wizWinModel.setValue(CCWizardWindowModelInterface.WINDOW_HEIGHT,
            new Integer(594));
            wizWinModel.setValue(CCWizardWindowModelInterface.WINDOW_WIDTH,
            new Integer(825));
            
            wizWinModel.setValue(
            CCWizardWindowModelInterface.WIZARD_REFRESH_CMDCHILD,
            getQualifiedName() + "." + CHILD_FRWD_TO_CMDCHILD);
 
            wizWinModel.setValue(
            CCWizardWindowModelInterface.WIZARD_CLASS_NAME,
            "com.sun.apoc.manager.profiles.CopyMoveWizardImpl");
 
            wizWinModel.setValue(CCWizardWindowModelInterface.WIZARD_NAME,
            getWizardImplName(WIZARDIMPLNAME_PREFIX));
            
            wizWinModel.setValue(WIZARDPAGEMODELNAME,
            getWizardPageModelName(WIZARDPAGEMODELNAME_PREFIX));
            
        }
        
        return wizWinModel;
    }    
}
