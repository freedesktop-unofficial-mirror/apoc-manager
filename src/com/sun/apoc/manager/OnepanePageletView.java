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

import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.JspChildDisplayEvent;
import com.sun.apoc.manager.profiles.AvailableView;
import com.sun.web.ui.model.CCTabsModelInterface;
import com.sun.web.ui.view.tabs.CCTabs;
import java.io.IOException;
import javax.servlet.ServletException;

public class OnepanePageletView extends RequestHandlingViewBase {
    
    public static final String  CHILD_TABS                  = "OnepaneTabs";
    public static final String  CHILD_AVAILABLE_VIEW        = "Available";
//    public static final String  CHILD_TASK_VIEW             = "Task";
    private MainWindowTabsModel m_tabsModel                 = null;
    private String              m_viewedEntityString        = null;
    
    public OnepanePageletView(View parent, String name) {
        super(parent, name);
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_TABS, CCTabs.class);
        
        if(((OnepaneViewBean)getRootView()).getSelectedTab().equals(Integer.toString(MainWindowTabsModel.AVAILABLE_TAB_ID))) {
            registerChild(CHILD_AVAILABLE_VIEW, AvailableView.class);
        }
//        else {
//            registerChild(CHILD_TASK_VIEW, TaskView.class);
//        }
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_TABS)) {
            CCTabs child = new CCTabs(this, getTabsModel(), name);
            return child;
        } else if (name.equals(CHILD_AVAILABLE_VIEW)) {
            AvailableView child = new AvailableView(this, name);
            return child;
//        } else if (name.equals(CHILD_TASK_VIEW)) {
//            TaskView child = new TaskView(this, name);
//            return child;
        } else {
            throw new IllegalArgumentException(
            "Invalid child name [" + name + "]");
        }
    }
    
    public void beginDisplay(DisplayEvent event) {
        m_viewedEntityString = Toolbox2.getSelectedEntityId();
    }
    
    public boolean beginOnepaneTabsDisplay(ChildDisplayEvent event){
        getTabsModel().setSelectedNode(Integer.parseInt(((OnepaneViewBean)getRootView()).getSelectedTab()));
        return true;
    }
    
    public boolean beginTabSelectionDisplay(ChildDisplayEvent event)
    throws ServletException, IOException {
        if(((OnepaneViewBean)getRootView()).getSelectedTab().equals(Integer.toString(MainWindowTabsModel.AVAILABLE_TAB_ID))) {
            ((JspChildDisplayEvent) event).getPageContext().include("/jsp/profiles/Available.jsp");
        }
//        else {
//            ((JspChildDisplayEvent) event).getPageContext().include("/jsp/Task.jsp");
//        }
        return true;
    }

    protected CCTabsModelInterface getTabsModel() {
        if (m_tabsModel == null) {
            m_tabsModel = (MainWindowTabsModel) getModel(MainWindowTabsModel.class);
        }
        return m_tabsModel;
    }
}
