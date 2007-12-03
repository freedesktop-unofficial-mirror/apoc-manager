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
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.apoc.manager.profiles.AssignTableView;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCHiddenField;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.masthead.CCSecondaryMasthead;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import java.io.IOException;
import javax.servlet.ServletException;


public class ProfilesAssignViewBean extends ViewBeanBase {
    public static final String PAGE_NAME            = "ProfilesAssign";
    public static final String DEFAULT_DISPLAY_URL  = "/jsp/profiles/Assign.jsp";
    public static final String CHILD_MASTHEAD       = "Masthead";
    public static final String CHILD_FORM           = "AssignForm";
    public static final String CHILD_ALERT          = "Alert";
    public static final String CHILD_STACKTRACE     = "StackTrace";
    public static final String CHILD_SUBMITCLOSE    = "SubmitAndClose";
    public static final String CHILD_TITLE          = "AssignTitle";
    public static final String CHILD_TABLE_VIEW     = "AssignTableView";
    public static final String CHILD_DEFAULT_HREF   = "DefaultHref";
    
    //
    private CCPageTitleModel  m_titleModel          = null;
    
    public ProfilesAssignViewBean(RequestContext rc) {
        super(PAGE_NAME);
        setRequestContext(rc);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        m_titleModel = new CCPageTitleModel(rc.getServletContext(), "/jsp/profiles/AssignTitle.xml");
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_MASTHEAD,       CCSecondaryMasthead.class);
        registerChild(CHILD_FORM,           CCForm.class);
        registerChild(CHILD_ALERT,          CCAlertInline.class);
        registerChild(CHILD_STACKTRACE,     CCStaticTextField.class);
        registerChild(CHILD_SUBMITCLOSE,    CCStaticTextField.class);
        registerChild(CHILD_TITLE,          CCPageTitle.class);
        registerChild(CHILD_TABLE_VIEW,     AssignTableView.class);
        registerChild(CHILD_DEFAULT_HREF,   CCHref.class);
        m_titleModel.registerChildren(this);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_MASTHEAD)) {
            CCSecondaryMasthead child = new CCSecondaryMasthead(this, name);
            return child;
        }
        else if (name.equals(CHILD_FORM)) {
            View child = new CCForm(this, name);
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
        else if (name.equals(CHILD_SUBMITCLOSE)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_TITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_titleModel, name);
            Toolbox2.setPageTitleHelp(m_titleModel, "APOC.profileavail.assigning", 
                                "APOC.profileavail.help", "gbgpn.html");
            return child;
        }
        else if (m_titleModel.isChildSupported(name)) {
            View child = m_titleModel.createChild(this, name);
            return child;
        }
        else if (name.equals(CHILD_TABLE_VIEW)) {
            AssignTableView child = new AssignTableView(this, name);
            return child;
        }
        else if (name.equals(CHILD_DEFAULT_HREF)) {
            CCHref child = new CCHref(this, name, null);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleDefaultHrefRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        forwardTo(getRequestContext());
    }

    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        if (Toolbox2.getParameter("OkActionHref").length()==0) {
            ((CCStaticTextField) getChild(CHILD_SUBMITCLOSE)).setValue("return true;");
        }
    }
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        return ((alert.getSummary() != null) && (alert.getSummary().length() > 0));
    }

    public boolean beginAssignTitleDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
            CCPageTitle title = (CCPageTitle) getChild(event.getChildName());
            title.getModel().setPageTitleText(Toolbox2.buildEntityTitle("APOC.pool.add_assignment"));
            return true;
    }    
}
