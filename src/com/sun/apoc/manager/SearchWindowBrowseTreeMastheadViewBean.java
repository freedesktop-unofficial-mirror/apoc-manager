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

import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;

import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.model.CCPageTitleModel;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.spi.environment.EnvironmentConstants;

import com.sun.web.ui.view.masthead.CCSecondaryMasthead;


public class SearchWindowBrowseTreeMastheadViewBean extends ViewBeanBase {
    public static final String PAGE_NAME           = "SearchWindowBrowseTreeMasthead";
    public static final String DEFAULT_DISPLAY_URL = "/jsp/entities/SearchWindowBrowseTreeMasthead.jsp";
    public static final String CHILD_MASTHEAD      = "Masthead";
    public static final String CHILD_TITLE         = "BrowseTreeTitle";
    private CCPageTitleModel  m_titleModel          = null;
    private String m_pageTitleText = "";
    
    public SearchWindowBrowseTreeMastheadViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        m_titleModel = new CCPageTitleModel(); 
        String sEntityType = (String)RequestManager.getRequest().getSession(false).getAttribute(Constants.SEARCH_WINDOW_PREFIX + Constants.BROWSE_TREE_ENTITY_TYPE);
        if(sEntityType.equals(EnvironmentConstants.USER_SOURCE)) {
            m_titleModel.setPageTitleText("APOC.browse.title.org");
            m_pageTitleText = "APOC.browse.title.org";
        } else if (sEntityType.equals(EnvironmentConstants.HOST_SOURCE)) {
            m_titleModel.setPageTitleText("APOC.browse.title.dom");
            m_pageTitleText = "APOC.browse.title.dom";     
        } else {
            m_titleModel.setPageTitleText("APOC.browse.title");
            m_pageTitleText = "APOC.browse.title";
        }
        registerChildren();
    }

    protected void registerChildren() {
        registerChild(CHILD_MASTHEAD, CCSecondaryMasthead.class);
        m_titleModel.registerChildren(this);
    }

    protected View createChild(String name) {
        if (name.equals(CHILD_MASTHEAD)) {
            CCSecondaryMasthead child = new CCSecondaryMasthead(this, name);
            return child;
        }
        else if (name.equals(CHILD_TITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_titleModel, name);
            Toolbox2.setPageTitleHelp(m_titleModel, "APOC.browse.tree.nav", 
                                "APOC.browse.title.text", "gbgcm.html");
            return child;
        }
        else if (m_titleModel.isChildSupported(name)) {
            View child = m_titleModel.createChild(this, name);
            return child;
        }        
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
}

