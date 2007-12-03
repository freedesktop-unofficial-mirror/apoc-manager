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
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.AlertViewBean;
import com.sun.apoc.manager.Toolbox2;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.table.CCActionTable;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;


public class AssignTableView extends RequestHandlingViewBase {
    public static final String             CHILD_PROFILE_TABLE    = "ProfileTable";
    public static final String             CHILD_ACTION_MENU      = "ActionMenu";
    public static final String             CHILD_ACTION_MENU_HREF = "OkActionHref";
    public static final String             CHILD_IMPORTGROUP      = "TabHref";

    protected AssignTableModel            m_Model                = null;
    
    public AssignTableView(View parent, String name) {
        super(parent, name);
        m_Model = getModel();
        registerChildren();
    }
    
    public AssignTableModel getModel() {
        if (m_Model == null) {
            m_Model = (AssignTableModel) getModel(AssignTableModel.class);
            m_Model.setDocument(getRequestContext().getServletContext(), "/jsp/profiles/AssignTable.xml");
        }
        
        return m_Model;
    }
    
    protected void registerChildren() {
        registerChild(CHILD_PROFILE_TABLE, CCActionTable.class);
        registerChild(CHILD_ACTION_MENU_HREF, CCHref.class);
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
        else if (m_Model.isChildSupported(name)) {
            return m_Model.createChild(this, name);
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleOkActionHrefRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        try {
            m_Model.retrieve(Toolbox2.getSelectedEntity());
            ((CCActionTable) getChild(CHILD_PROFILE_TABLE)).restoreStateData();
            updateSelections();
            m_Model.assign();
        }
        catch (ModelControlException mce) {
            Toolbox2.prepareErrorDisplay(mce,
            getRootView().getChild(AlertViewBean.CHILD_ALERT),
            getRootView().getChild(AlertViewBean.CHILD_STACKTRACE));
        }
        getRootView().forwardTo(getRequestContext());
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        m_Model.retrieve(Toolbox2.getSelectedEntity());
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
                    m_Model.setRowSelected(Integer.parseInt(sRowNumber), Boolean.valueOf(sParamValue).booleanValue());
                }
            }
        }
    }
}
