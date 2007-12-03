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
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletException;

public class AvailableView extends RequestHandlingViewBase {
    public static final String CHILD_ALERT          = "Alert";
    public static final String CHILD_STACKTRACE     = "StackTrace";
    public static final String CHILD_DOMTABLE_VIEW  = "AvailableDomainTableView";
    public static final String CHILD_ORGTABLE_VIEW  = "AvailableOrgTableView";
    public static final String CHILD_PROFILE_TITLE  = "AvailableTitle";
    public static final String CHILD_RENAME         = "RenameMessage";
    public static final String CHILD_DOM_DISABLE    = "ConditionalNewDomainDisable";
    public static final String CHILD_ORG_DISABLE    = "ConditionalNewOrgDisable";
    public static final String CHILD_HIDDEN_PROFILE         = "SelectedProfile";
   
    public static final String CHILD_COMMAND_HIDDEN = "CommandHidden";
    public static final String CHILD_PARAMETERS_HIDDEN = "CommandParametersHidden";
    
    public AvailableView(View parent, String name) {
        super(parent, name);
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_ALERT,          CCAlertInline.class);
        registerChild(CHILD_STACKTRACE,     CCStaticTextField.class);
        registerChild(CHILD_PROFILE_TITLE,  CCPageTitle.class);
        registerChild(CHILD_DOMTABLE_VIEW,  AvailableDomainTableView.class);
        registerChild(CHILD_ORGTABLE_VIEW,  AvailableOrgTableView.class);
        registerChild(CHILD_RENAME,         CCStaticTextField.class);
        registerChild(CHILD_DOM_DISABLE,    CCStaticTextField.class);
        registerChild(CHILD_ORG_DISABLE,    CCStaticTextField.class);
        registerChild(CHILD_COMMAND_HIDDEN, CCHiddenField.class);
        registerChild(CHILD_PARAMETERS_HIDDEN,CCHiddenField.class);
        registerChild(CHILD_HIDDEN_PROFILE, CCHiddenField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
            return child;
        } else if (name.equals(CHILD_STACKTRACE) || name.equals(CHILD_RENAME) ||
            name.equals(CHILD_DOM_DISABLE) || name.equals(CHILD_ORG_DISABLE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        } else if (name.equals(CHILD_PROFILE_TITLE)) {
            CCPageTitleModel pageTitleModel = new CCPageTitleModel();
            Toolbox2.setPageTitleHelp(pageTitleModel, "APOC.sync.result.title",
                "APOC.profileall.help", "gbgcd.html");
            CCPageTitle child = new CCPageTitle(this, pageTitleModel, name);
            return child;
        } else if (name.equals(CHILD_DOMTABLE_VIEW)) {
            AvailableDomainTableView child = new AvailableDomainTableView(this, name);
            return child;
        } else if (name.equals(CHILD_ORGTABLE_VIEW)) {
            AvailableOrgTableView child = new AvailableOrgTableView(this, name);
            return child;
        } else if (name.equals(CHILD_COMMAND_HIDDEN) || name.equals(CHILD_PARAMETERS_HIDDEN)) {
            CCHiddenField child = new CCHiddenField(this, name, null);
            return child;
        } else if (name.equals(CHILD_HIDDEN_PROFILE)) {
            CCHiddenField child = new CCHiddenField(this, name, "");
            child.setElementId("SelectedProfileCopyMoveWizard");
            return child;
        } else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        try {
            AvailableTableModel model = (AvailableTableModel) getRequestContext().getModelManager().getModel(AvailableTableModel.class, Integer.toString(hashCode()));
            model.retrieve(null);
        } catch (Exception e) {
            Toolbox2.prepareErrorDisplay(e, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE));
        }
    }
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        
        if ((alert.getSummary() != null) && (alert.getSummary().length() > 0)) {
            return true;
        }
        
        AvailableTableModel model = (AvailableTableModel) getRequestContext().getModelManager().getModel(AvailableTableModel.class, Integer.toString(hashCode()));
        boolean bIsReadOnly = model.hasReadOnlyProfile();
        
        if (bIsReadOnly) {
            alert.setValue(CCAlertInline.TYPE_INFO);
            alert.setType(CCAlertInline.TYPE_INFO);
            CCI18N i18n = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
            alert.setSummary(i18n.getMessage("APOC.policies.info.readonly.maybe"));
        }
        
        return bIsReadOnly;
    }
    
    public boolean beginDisplayDomainTableDisplay(ChildDisplayEvent event)
    throws ServletException, IOException, SPIException {
        PolicyManager policymgr = Toolbox2.getPolicyManager();
        ArrayList sources = new ArrayList(Arrays.asList(policymgr.getSources()));
        return sources.contains(EnvironmentConstants.HOST_SOURCE);
    }
    
    public boolean beginDisplayOrgTableDisplay(ChildDisplayEvent event)
    throws ServletException, IOException, SPIException {
        PolicyManager policymgr = Toolbox2.getPolicyManager();
        ArrayList sources = new ArrayList(Arrays.asList(policymgr.getSources()));
        return sources.contains(EnvironmentConstants.USER_SOURCE);
    } 
    
   public boolean beginConditionalNewDomainDisableDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        AvailableTableModel model = (AvailableTableModel) getRequestContext().getModelManager().getModel(AvailableTableModel.class, Integer.toString(hashCode()));
        return model.isDomainRootReadOnly();
    }
    
    public boolean beginConditionalNewOrgDisableDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        AvailableTableModel model = (AvailableTableModel) getRequestContext().getModelManager().getModel(AvailableTableModel.class, Integer.toString(hashCode()));
        return model.isOrganizationRootReadOnly();
    }
    
    public boolean beginCommandHiddenDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCHiddenField hidden = (CCHiddenField) getChild(event.getChildName());
        hidden.setValue("");
        return true;
    }
    
    public boolean beginCommandParametersHiddenDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCHiddenField hidden = (CCHiddenField) getChild(event.getChildName());
        hidden.setValue("");
        return true;
    }
}
