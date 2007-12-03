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

package com.sun.apoc.manager.entities;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.Toolbox2;
import com.sun.web.ui.view.table.CCActionTable;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;

public class MemberTableView extends RequestHandlingViewBase {
    public static final String              CHILD_MEMBER_TABLE   = "MemberTable";
    protected           MemberTableModel    m_Model            = null;
    
    public MemberTableView(View parent, String name) throws ModelControlException {
        super(parent, name);
        m_Model = (MemberTableModel) getModel(MemberTableModel.class);
        m_Model.setDocument(getRequestContext().getServletContext(), "/jsp/entities/MemberTable.xml");
        m_Model.retrieve();
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_MEMBER_TABLE, CCActionTable.class);
        m_Model.registerChildren(this);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_MEMBER_TABLE)) {
            CCActionTable child = new CCActionTable(this, m_Model, name);
            return child;
        }
        else if (m_Model.isChildSupported(name)) {
            return m_Model.createChild(this, name);
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleViewActionRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        getRootView().forwardTo(getRequestContext());
    }
    
    public boolean beginChildDisplay(ChildDisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        
        if (event.getChildName().equals(CHILD_MEMBER_TABLE)) {
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
                String sChildName = getChild(CHILD_MEMBER_TABLE).getQualifiedName() + ".stateData";
                Map    stateData = (Map) getRootView().getPageSessionAttribute(sChildName);
                
                if (stateData != null) {
                    stateData.put("showPaginationControls", new Boolean((nSize > (nMaxRows * 5))));
                }
            }
        }
        
        return true;
    }
}
