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

package com.sun.apoc.manager.sync;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.RequestHandlingTiledViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.RequestInvocationEvent;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.table.CCActionTable;
import java.io.IOException;
import javax.servlet.ServletException;

public class ContextTiledView extends RequestHandlingTiledViewBase {
    public static final String              CHILD_PROFILE_TABLE = "SyncTable";
    private             ContextTiledModel   m_contextModel      = null;
    
    public ContextTiledView(View parent, String name) {
        super(parent, name);
        setPrimaryModel(getModel());
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_PROFILE_TABLE, CCActionTable.class);
        ProfileTableModel profileModel = new ProfileTableModel();
        profileModel.setDocument(getSession().getServletContext(), "/jsp/sync/Table.xml");
        profileModel.registerChildren(this);
    }

    protected View createChild(String name) {

        ProfileTableModel profileModel;

        if (m_contextModel==null) {
            profileModel = new ProfileTableModel();
            profileModel.setDocument(getSession().getServletContext(), "/jsp/sync/Table.xml");
        } else {
            profileModel = (ProfileTableModel) m_contextModel.getValue(ContextTiledModel.PROFILES);
        }

        if (name.equals(CHILD_PROFILE_TABLE)) {
            CCActionTable child = new CCActionTable(this, profileModel, name);
            return child;
        } else if (profileModel.isChildSupported(name)) {
                return profileModel.createChild(this, name);
        } else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
    
    public void handleNameHrefRequest(RequestInvocationEvent event)
    throws ServletException, IOException {
        getRootView().forwardTo(getRequestContext());
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        m_contextModel = getModel();
        m_contextModel.runSessionActions();
    }
    
    public boolean beginSyncTableDisplay(ChildDisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        // forces creation of the children again -> correctly updated models
        removeAllChildren();
        return true;
    }

    public boolean beginDiffsHrefDisplay(ChildDisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);

        ProfileTableModel   profileModel= (ProfileTableModel) m_contextModel.getValue(ContextTiledModel.PROFILES);
        String              sDiffsText2 = (String) profileModel.getValue(ProfileTableModel.CHILD_DIFFS_TEXT2);
        if (sDiffsText2.length()==0) {
            String sOnClick = (String) profileModel.getValue(ProfileTableModel.CHILD_DIFFS_HREF_CLICK);
            CCHref href     = (CCHref) getChild("DiffsHref");
            href.setExtraHtml(sOnClick);
            return true;
        } else {
            return false;
        }
    }

    public boolean beginDiffsText2Display(ChildDisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        return !beginDiffsHrefDisplay(event);
    }

    private ContextTiledModel getModel() {
        //changed because this model is executed in a seperate thread now and has thus no access to the session
        ContextTiledModel model = (ContextTiledModel) getSession().getAttribute("ThreadSyncModel");
        if (model==null) {
            model = new ContextTiledModel();
            getSession().setAttribute("ThreadSyncModel", model);
        }
        return model;
    }
}
