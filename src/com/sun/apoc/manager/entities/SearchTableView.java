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
import com.sun.apoc.manager.EntitiesSearchResultViewBean;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.ldap.entities.LdapEntityType;

import com.sun.web.ui.model.CCActionTableModelInterface;
import com.sun.web.ui.view.table.CCActionTable;
import java.util.Map;


public class SearchTableView extends RequestHandlingViewBase {
//
    public static final String               CHILD_ENTITY_TABLE = "EntityTable";
    protected SearchTableModel m_Model = null;

    public SearchTableView(View parent, String name) {
        super(parent, name);
        m_Model = (SearchTableModel) getModel(SearchTableModel.class);
       
        if (Toolbox2.getParameter("EntityTypeMenu").equals(LdapEntityType.STR_USERID)) {
            m_Model.setDocument(getRequestContext().getServletContext(), "/jsp/entities/SearchTableUser.xml");

            String sPattern   = Toolbox2.getParameter("SearchText");

            if (sPattern.length() > 0) {
                m_Model.setPrimarySortName(SearchTableModel.CHILD_USERID_TEXT);
                m_Model.setPrimarySortOrder(CCActionTableModelInterface.ASCENDING);
                m_Model.setSecondarySortName(SearchTableModel.CHILD_NAME_TEXT);
                m_Model.setSecondarySortOrder(CCActionTableModelInterface.ASCENDING);
            }
        }
        else {
            m_Model.setDocument(getRequestContext().getServletContext(), "/jsp/entities/SearchTable.xml");
        }

        registerChildren();
    }

    protected void registerChildren() {
        registerChild(CHILD_ENTITY_TABLE, CCActionTable.class);
        m_Model.registerChildren(this);
    }

    protected View createChild(String name) {
        if (name.equals(CHILD_ENTITY_TABLE)) {
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

    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        ((CCActionTable) getChild(CHILD_ENTITY_TABLE)).restoreStateData();
        String sEntity      = Toolbox2.getParameter(EntitiesSearchResultViewBean.CHILD_ENTITYID_HIDDEN);
        String sEntityType  = Toolbox2.getParameter(EntitiesSearchResultViewBean.CHILD_ENTITY_TYPE_HIDDEN);
        String sType        = Toolbox2.getParameter(EntitiesSearchResultViewBean.CHILD_TYPE_HIDDEN);
        String sSource = EnvironmentConstants.HOST_SOURCE;
        if (sType.equals("ORG")) {
            m_Model.setTitle("APOC.search.organizationsFound");
            sSource = EnvironmentConstants.USER_SOURCE;
        }
        else if (sType.equals("USERID")) {
            m_Model.setTitle("APOC.search.usersFound");
            sSource = EnvironmentConstants.USER_SOURCE;
        }
        else if (sType.equals("ROLE")) {
            m_Model.setTitle("APOC.search.rolesFound");
            sSource = EnvironmentConstants.USER_SOURCE;
        }
        else if (sType.equals("DOMAIN")) {
            m_Model.setTitle("APOC.search.domainsFound");
            sSource = EnvironmentConstants.HOST_SOURCE;
        }
        else if (sType.equals("HOST")) {
            m_Model.setTitle("APOC.search.hostsFound");
            sSource = EnvironmentConstants.HOST_SOURCE;
        }
        String sSearchText  = Toolbox2.getParameter(EntitiesSearchResultViewBean.CHILD_SEARCHTEXT_HIDDEN);
        String sRestrict    = Toolbox2.getParameter(EntitiesSearchResultViewBean.CHILD_RESTRICT_HIDDEN);
        String sResults     = Toolbox2.getParameter(EntitiesSearchResultViewBean.CHILD_RESULTS_HIDDEN);
        String sContext     = Toolbox2.getParameter(EntitiesSearchResultViewBean.CHILD_CONTEXT_HIDDEN);
        boolean bRestrict   = Boolean.valueOf(sRestrict).booleanValue();
        m_Model.retrieve(sEntity, sEntityType, sType, sSource, sSearchText, sResults, (!bRestrict), sContext);
    }

    public boolean beginChildDisplay(ChildDisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);

        if (event.getChildName().equals(CHILD_ENTITY_TABLE)) {
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
                String sChildName = getChild(CHILD_ENTITY_TABLE).getQualifiedName() + ".stateData";
                Map    stateData = (Map) getRootView().getPageSessionAttribute(sChildName);

                if (stateData != null) {
                    stateData.put("showPaginationControls", new Boolean((nSize > (nMaxRows * 5))));
                    stateData.put("showPaginationControls", new Boolean(true));
                }
            }
       }
       return true;
    }
}
