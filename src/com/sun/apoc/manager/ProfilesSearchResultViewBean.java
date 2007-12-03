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

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.profiles.SearchTiledModel;
import com.sun.apoc.manager.profiles.SearchTiledView;
import com.sun.web.ui.view.html.CCButton;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ProfilesSearchResultViewBean extends ViewBeanBase {
    public static final String PAGE_NAME                = "ProfilesSearchResult";
    public static final String DEFAULT_DISPLAY_URL      = "/jsp/profiles/SearchResult.jsp";
    public static final String CHILD_TILED_VIEW         = "ProfilesSearchTiledView";
    public static final String CHILD_FORM               = "ResultForm";
    public static final String CHILD_SEARCH_HREF        = "SearchHref";
    public static final String CHILD_SEARCH_BUTTON      = "SearchButton";
    public static final String CHILD_SEARCHSTRING_HIDDEN= "SearchString";
    public static final String CHILD_HELP1_TEXT         = "HelpText1";
    public static final String CHILD_HELP2_TEXT         = "HelpText2";
    private boolean            m_bSearchHref            = false;
    private boolean            m_bFoundProfiles         = false;
    
    public ProfilesSearchResultViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        ServletContext context = RequestManager.getRequestContext().getServletContext();
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_TILED_VIEW, SearchTiledView.class);
        registerChild(CHILD_FORM, CCForm.class);
        registerChild(CHILD_SEARCH_HREF, CCHref.class);
        registerChild(CHILD_SEARCH_BUTTON, CCButton.class);
        registerChild(CHILD_HELP1_TEXT, CCStaticTextField.class);
        registerChild(CHILD_HELP2_TEXT, CCStaticTextField.class);
        registerChild(CHILD_SEARCHSTRING_HIDDEN, CCHiddenField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_TILED_VIEW)) {
            View child = new SearchTiledView(this, name);
            return child;
        }
        else if (name.equals(CHILD_FORM)) {
            View child = new CCForm(this, name);
            return child;
        }
        else if (name.equals(CHILD_SEARCH_HREF)) {
            View child = new CCHref(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_SEARCH_BUTTON)) {
            View child = new CCButton(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_HELP1_TEXT) || name.equals(CHILD_HELP2_TEXT)) {
            View child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_SEARCHSTRING_HIDDEN)) {
            View child = new CCHiddenField(this, name, null);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleSearchHrefRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        m_bSearchHref = true;
        forwardTo(getRequestContext());
    }

    public void handleSearchButtonRequest(RequestInvocationEvent event)
    throws ModelControlException, ServletException, IOException {
        CCHiddenField   textField   = (CCHiddenField) getChild(CHILD_SEARCHSTRING_HIDDEN); 
        SearchTiledModel  model       = (SearchTiledModel)getModel(SearchTiledModel.class);
        model.retrieve((String) textField.getValue() );
        setEntitiesFound(model.getSize()>0);
        forwardTo(getRequestContext());
    }

    public boolean beginHelpDisplay(ChildDisplayEvent event)
    throws ModelControlException {
        return m_bSearchHref;
    } 
    
    public boolean beginEntitiesFoundDisplay(ChildDisplayEvent event)
    throws ModelControlException {
        super.beginDisplay(event);
        return (!m_bSearchHref && m_bFoundProfiles);
    } 

    public boolean beginNoEntitiesFoundDisplay(ChildDisplayEvent event)
    throws ModelControlException {
        return (!m_bSearchHref && !m_bFoundProfiles);
    } 

    public void setEntitiesFound(boolean bFound) {
        m_bFoundProfiles = bFound;
    }
}

