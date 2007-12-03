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
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.AlertViewBean;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.web.ui.view.table.CCActionTable;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class PrioritizationTableView extends RequestHandlingViewBase {
    public static final String      CHILD_PRIO_TABLE    = "PrioritizationTable";
    public static final String      MOVE_UP_ACTION      = "0";
    public static final String      MOVE_DOWN_ACTION    = "1";
    
    private PrioritizationTableModel m_prioModel = null;
    private ProfileWindowModel mEditorModel = null;
    
    public PrioritizationTableView(View parent, String name) {
        super(parent, name);
        m_prioModel = (PrioritizationTableModel) getModel(PrioritizationTableModel.class);
        m_prioModel.setDocument(getSession().getServletContext(), "/jsp/profiles/PrioritizationTable.xml");
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_PRIO_TABLE, CCActionTable.class);
        m_prioModel.registerChildren(this);
    }
    
    protected View createChild(String name) {
        
        if (name.equals(CHILD_PRIO_TABLE)) {
            CCActionTable child = new CCActionTable(this, m_prioModel, name);
            return child;
        } else {
            if (m_prioModel.isChildSupported(name)) {
                return m_prioModel.createChild(this, name);
            }
        }
        throw new IllegalArgumentException("Invalid child name [" + name + "]");
    }
    
    public void handleUpButtonRequest(RequestInvocationEvent event) throws ServletException, IOException {
        try {
            m_prioModel.retrieve(getEditorModel().getProfile());
            ((CCActionTable) getChild(CHILD_PRIO_TABLE)).restoreStateData();
            updateSelections();
            m_prioModel.changePriority(1);
        }
        catch (ModelControlException mce) {
            Toolbox2.prepareErrorDisplay(mce,
            ((ContainerView)getParent()).getChild(AlertViewBean.CHILD_ALERT),
            ((ContainerView)getParent()).getChild(AlertViewBean.CHILD_STACKTRACE));
        }
        getRootView().forwardTo(getRequestContext());
    }
    
    public void handleDownButtonRequest(RequestInvocationEvent event) throws ServletException, IOException {
        try {
            m_prioModel.retrieve(getEditorModel().getProfile());
            ((CCActionTable) getChild(CHILD_PRIO_TABLE)).restoreStateData();
            updateSelections();
            m_prioModel.changePriority(-1);
        }
        catch (ModelControlException mce) {
            Toolbox2.prepareErrorDisplay(mce,
            ((ContainerView)getParent()).getChild(AlertViewBean.CHILD_ALERT),
            ((ContainerView)getParent()).getChild(AlertViewBean.CHILD_STACKTRACE));
        }
        getRootView().forwardTo(getRequestContext());
    }
    
    public void handleNameHrefRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        getRootView().forwardTo(getRequestContext());
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        m_prioModel.retrieve(getEditorModel().getProfile());
    }
    
    private void updateSelections() {
        HttpServletRequest request         = getRequestContext().getRequest();
        Enumeration        aParamNamesEnum = request.getParameterNames();
        
        while (aParamNamesEnum.hasMoreElements()) {
            String sParamName = (String) aParamNamesEnum.nextElement();
            int    nOptionPos = sParamName.indexOf(CCActionTable.CHILD_SELECTION_CHECKBOX);
            
            if (nOptionPos != -1) {
                String sParamValue = request.getParameter(sParamName);
                String sRowNumber  = sParamName.substring(nOptionPos+CCActionTable.CHILD_SELECTION_CHECKBOX.length());
                
                if (sRowNumber.indexOf(".")==-1) {
                    m_prioModel.setRowSelected(Integer.parseInt(sRowNumber), Boolean.valueOf(sParamValue).booleanValue());
                }
            }
        }
    }
    
    private ProfileWindowModel getEditorModel() {
        if (mEditorModel == null) {
            mEditorModel = (ProfileWindowModel) getModel(ProfileWindowModel.class);
        }
        return mEditorModel;
    }
}
