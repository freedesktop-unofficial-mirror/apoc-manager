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
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.model.CCPropertySheetModel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.html.CCForm;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.masthead.CCSecondaryMasthead;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import com.sun.web.ui.view.propertysheet.CCPropertySheet;
import java.io.IOException;
import javax.servlet.ServletException;


public class ProfilesNewViewBean extends ViewBeanBase {
    public static final String PAGE_NAME            = "ProfilesNew";
    public static final String DEFAULT_DISPLAY_URL  = "/jsp/profiles/New.jsp";
    public static final String CHILD_ALERT          = "Alert";
    public static final String CHILD_STACKTRACE     = "StackTrace";
    public static final String CHILD_MASTHEAD       = "Masthead";
    public static final String CHILD_FORM           = "NewForm";
    public static final String CHILD_TITLE          = "NewTitle";
    public static final String CHILD_NEW_SHEET      = "NewSheet";
    private CCPropertySheetModel m_sheetModel             = null;
    //
    private CCPageTitleModel  m_titleModel          = null;
    
    public ProfilesNewViewBean(RequestContext rc) {
        super(PAGE_NAME);
        setRequestContext(rc);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        m_titleModel = new CCPageTitleModel(rc.getServletContext(), "/jsp/profiles/NewTitle.xml");
        m_sheetModel = new CCPropertySheetModel(rc.getServletContext(), "/jsp/profiles/NewSheet.xml");
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_MASTHEAD,       CCSecondaryMasthead.class);
        registerChild(CHILD_FORM,           CCForm.class);
        registerChild(CHILD_ALERT,          CCAlertInline.class);
        registerChild(CHILD_STACKTRACE,     CCStaticTextField.class);
        registerChild(CHILD_TITLE,          CCPageTitle.class);
        m_titleModel.registerChildren(this);
        registerChild(CHILD_NEW_SHEET, CCPropertySheet.class);
        m_sheetModel.registerChildren(this);
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
        else if (name.equals(CHILD_TITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_titleModel, name);
            return child;
        }
        else if (m_titleModel.isChildSupported(name)) {
            View child = m_titleModel.createChild(this, name);
            return child;
        }
        else if (name.equals(CHILD_NEW_SHEET)) {
            CCPropertySheet child = new CCPropertySheet(this, m_sheetModel, name);
            return child;
        }
        else if ((m_sheetModel != null) && m_sheetModel.isChildSupported(name)) {
            View child = m_sheetModel.createChild(this, name);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleOkButtonRequest(RequestInvocationEvent event) throws ServletException, IOException {
    }
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        return ((alert.getSummary() != null) && (alert.getSummary().length() > 0));
    }
}
