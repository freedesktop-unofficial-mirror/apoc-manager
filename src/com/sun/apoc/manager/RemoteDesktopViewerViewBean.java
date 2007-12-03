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

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.RequestManager;

import com.sun.web.ui.view.masthead.CCSecondaryMasthead;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.iplanet.jato.RequestContext;
import com.sun.web.ui.view.html.*;
import com.iplanet.jato.view.event.DisplayEvent;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.SPIException;
import com.sun.web.ui.common.CCDebug;

import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.Iterator;


import java.io.IOException;


public class RemoteDesktopViewerViewBean extends ViewBeanBase {
    public static final String PAGE_NAME           = "RemoteDesktopViewer";
    public static final String DEFAULT_DISPLAY_URL = "/jsp/remote/RemoteDesktopViewer.jsp";
    public static final String CHILD_MASTHEAD      = "Masthead";
    public static final String CHILD_TITLE         = "BrowseTreeTitle";
    public static final String CHILD_ALERT         = "Alert";
    public static final String CHILD_JAVASCRIPT    = "Javascript";
    private CCPageTitleModel  m_titleModel         = null;
    
    public RemoteDesktopViewerViewBean() {
        super(PAGE_NAME);
        setDefaultDisplayURL(DEFAULT_DISPLAY_URL);
        registerChildren();
    }

    protected void registerChildren() {
        registerChild(CHILD_MASTHEAD, CCSecondaryMasthead.class);
        registerChild(CHILD_ALERT, CCAlertInline.class);
        getPageTitleModel().registerChildren(this);
    }

    protected View createChild(String name) {
        if (name.equals(CHILD_MASTHEAD)) {
            CCSecondaryMasthead child = new CCSecondaryMasthead(this, name);
            return child;
        }
        else if (name.equals(CHILD_TITLE)) {
            CCPageTitle child = new CCPageTitle(this, m_titleModel, name);
            return child;
        } 
        else if (name.equals(CHILD_JAVASCRIPT)) {
            CCStaticTextField child =  new CCStaticTextField(this, name, null);
            child.setValue("");
            return child;
            
        } 
        else if (name.equals(CHILD_ALERT)) {
            CCAlertInline child = new CCAlertInline(this, name, null);
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
    
    protected CCPageTitleModel getPageTitleModel() {
        if (m_titleModel == null) {
            m_titleModel = new CCPageTitleModel(
            RequestManager.getRequestContext().getServletContext(),
            "/jsp/remote/RemoteDesktopViewerPageTitle.xml");
        }
        return m_titleModel;
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        String sEntityId = (String)RequestManager.getRequest().getSession(false).getAttribute(Constants.BROWSE_TREE_ENTITY);
        String sHostName = null;
        try {
            Entity host = Toolbox2.getPolicyManager().getEntity(sEntityId);
            if (host != null) {
                sHostName = host.getDisplayName(Toolbox2.getLocale());
            }
        } catch (SPIException ex) {
            CCDebug.trace1("Could not retrieve host name", ex);
        }
        CCAlertInline alert = (CCAlertInline) getChild(CHILD_ALERT);
        if (isConnectionAllowed(sHostName)) {
            alert.setSummary("Connecting to " + sHostName);
            alert.setDetail("Established a connection to http://" +sHostName+ ":5800. The full initialization may take a few seconds.");    

            CCStaticTextField child = (CCStaticTextField) getChild(CHILD_JAVASCRIPT);
            child.setValue("window.parent.frames['viewer'].location = 'http://"+sHostName+":5800';");
            
        } else {
            alert.setType(CCAlertInline.TYPE_ERROR);
            alert.setSummary("Can not connect to " +sHostName);
            alert.setDetail("Connection to http://"+sHostName+":5800 has been refused.");
        }
    }
    
    protected boolean isConnectionAllowed(String hostname) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(hostname, 5800));
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}

