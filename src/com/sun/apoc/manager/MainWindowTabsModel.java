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
import com.iplanet.jato.RequestParticipant;
import com.sun.web.ui.model.CCNavNode;
import com.sun.web.ui.model.CCTabsModel;

public class MainWindowTabsModel extends CCTabsModel implements RequestParticipant {
    
//    public static final int TASKS_TAB_ID    = 1;
    public static final int ASSIGNED_TAB_ID = 1; //2;
    public static final int AVAILABLE_TAB_ID= 2; //3;
    
    public void setRequestContext(RequestContext requestContext){

        // Add tasks node
//        CCNavNode tasksTab = new CCNavNode(TASKS_TAB_ID,
//        "Common Tasks",
//        "Shows a list of common tasks.",
//        "Shows a list of common tasks.");
//        tasksTab.setOnClick("javascript: top.location.href='/apoc/manager/Onepane?Onepane.OnepanePageletView.OnepaneTabs.TabHref="+TASKS_TAB_ID+"'; return false;");
//        addNode(tasksTab);
        
        // Add assigned profiles node
        CCNavNode assignedTab = new CCNavNode(ASSIGNED_TAB_ID,
        "APOC.tabs.tree.title",
        "APOC.tabs.tree.help",
        "APOC.tabs.tree.help");
        assignedTab.setOnClick("javascript: top.location.href='/apoc/manager/Threepane'; return false;");
        addNode(assignedTab);
        
        // Add all profiles node
        CCNavNode allTab = new CCNavNode(AVAILABLE_TAB_ID,
        "APOC.tabs.all.title",
        "APOC.tabs.all.help",
        "APOC.tabs.all.help");
        allTab.setOnClick("javascript: top.location.href='/apoc/manager/Onepane?Onepane.OnepanePageletView.OnepaneTabs.TabHref="+AVAILABLE_TAB_ID+"'; return false;");
        addNode(allTab);
        



//        setSelectedNode(TASKS_TAB_ID);
        setSelectedNode(ASSIGNED_TAB_ID);
    }
}
