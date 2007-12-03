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

package com.sun.apoc.manager.profiles;

import java.io.IOException;
import javax.servlet.ServletException;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.JspDisplayEvent;
import com.sun.apoc.manager.profiles.GeneralSettingsViewBean;
import com.sun.apoc.manager.SettingsSummaryViewBean;
import com.sun.web.ui.common.CCDebug;

public class ProfileWindowPageletView extends RequestHandlingViewBase {
    
    public static final String CHILD_ASSIGNEES = "Assignees";
    public static final String CHILD_GENERAL   = "GeneralSettings";
    public static final String CHILD_SUMMARY   = "SettingsSummary";
    public static final String CHILD_ADVANCED  = "AdvancedOptions";
    
    private ProfileWindowModel mModel = null;

    public ProfileWindowPageletView(View parent, String name) {
        super(parent, name);
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_GENERAL, GeneralSettingsViewBean.class);
        registerChild(CHILD_ASSIGNEES, AssigneesViewBean.class);
        registerChild(CHILD_SUMMARY, SettingsSummaryViewBean.class);
        registerChild(CHILD_ADVANCED, AdvancedOptionsViewBean.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_GENERAL)) {
            GeneralSettingsViewBean child = new GeneralSettingsViewBean(this, name);
            return child;
            
        } else if (name.equals(CHILD_ASSIGNEES)) {
            AssigneesViewBean child = new AssigneesViewBean(this, name);
            return child;

        } else if (name.equals(CHILD_SUMMARY)) {
            SettingsSummaryViewBean child = new SettingsSummaryViewBean();
            return child;
            
        } else if (name.equals(CHILD_ADVANCED)) {
            AdvancedOptionsViewBean child = new AdvancedOptionsViewBean(this, name);
            return child;    
            
        } else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }

    public void beginDisplay(DisplayEvent event) {
        String  url = null;
        String selectedTab = getModel().getSelectedTab();
        if (selectedTab != null) {
            if (selectedTab.equals(ProfileWindowModel.ASSIGNEES_TAB)) {
                url = "/jsp/profiles/Assignees.jsp";
            } else if (selectedTab.equals(ProfileWindowModel.GENERAL_TAB)) {
                url = "/jsp/profiles/GeneralSettings.jsp";    
            } else if (selectedTab.equals(ProfileWindowModel.SUMMARY_TAB)) {
                url = "/jsp/profiles/SettingsSummary.jsp";    
            } else if (selectedTab.equals(ProfileWindowModel.ADVANCED_TAB)) {
                url = "/jsp/profiles/AdvancedOptions.jsp";    
            }
            if (url != null) {
                try {
                    ((JspDisplayEvent) event).getPageContext().include(url);
                } catch (ServletException ex) {
                    CCDebug.trace1(ex.toString());
                } catch (IOException ex) {
                    CCDebug.trace1(ex.toString());
                }
            }
        }
    }
    
    private ProfileWindowModel getModel() {
        if (mModel == null) {
            mModel = (ProfileWindowModel) getModel(ProfileWindowModel.class);
        }
        return mModel;
    }
}
