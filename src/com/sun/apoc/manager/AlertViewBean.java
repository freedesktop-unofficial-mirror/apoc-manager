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
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.ViewBeanBase;
import com.iplanet.jato.view.event.DisplayEvent;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.model.CCPageTitleModel;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.web.ui.view.alert.CCAlertFullPage;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.pagetitle.CCPageTitle;
import java.io.PrintWriter;
import java.io.StringWriter;

public class AlertViewBean extends ViewBeanBase {
    public static final String PAGE_NAME           = "Alert";
    public static final String DEFAULT_DISPLAY_URL = "/jsp/Alert.jsp";
    public static final String CHILD_TITLE         = "AlertTitle";
    public static final String CHILD_ALERT         = "Alert";
    public static final String CHILD_TRACEHINT     = "TraceHint";
    public static final String CHILD_STACKTRACE    = "StackTrace";
    private Throwable          m_exception         = null;
    private CCPageTitleModel   m_titleModel        = null;
    
    public AlertViewBean(RequestContext rc) {
        super(PAGE_NAME);
        setRequestContext(rc);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        m_titleModel = new CCPageTitleModel(
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<!DOCTYPE pagetitle SYSTEM \"tags/dtd/pagetitle.dtd\">" + 
        "<pagetitle>" +
        "</pagetitle>");
        m_titleModel.setAlertIconType(CCAlert.TYPE_ERROR);
    }
    
    protected void registerChildren() {
        registerChild(CHILD_TITLE, CCPageTitle.class);
        registerChild(CHILD_ALERT, CCAlertFullPage.class);
        registerChild(CHILD_TRACEHINT, CCStaticTextField.class);
        registerChild(CHILD_STACKTRACE, CCStaticTextField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_TITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_titleModel, name);
            return child;
        }
        else if (name.equals(CHILD_ALERT)) {
            CCAlertFullPage child = new CCAlertFullPage(this, name, null);
            return child;
        }
        else if (name.equals(CHILD_STACKTRACE) || name.equals(CHILD_TRACEHINT)) {
            CCStaticTextField child = new CCStaticTextField(this, name, null);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void setException(Exception exception) {
        m_exception = exception;
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        Toolbox2.prepareErrorDisplay(m_exception, getChild(CHILD_ALERT), getChild(CHILD_STACKTRACE));
    }
}
