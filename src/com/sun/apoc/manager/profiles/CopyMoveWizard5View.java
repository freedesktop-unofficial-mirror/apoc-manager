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

import com.iplanet.jato.model.Model;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingViewBase;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.alert.CCAlertFullPage;

import com.sun.web.ui.view.wizard.CCWizardPage;

import javax.servlet.ServletException;

import java.io.IOException;
import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.profiles.Profile;


/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class CopyMoveWizard5View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil {

    // The "logical" name for this page.
    public static final String PAGE_NAME = "CopyMoveWizard5View";

    // Child view names (i.e. display fields).

    public static final String CHILD_SUMMARY_TITLE =
        "SummaryTitle";
    public static final String CHILD_SUMMARY_TEXT =
        "SummaryText";
    public static final String CHILD_ALERT =
        "Alert";
    private CCI18N m_I18n;
    
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public CopyMoveWizard5View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }

    public CopyMoveWizard5View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        registerChildren();
    }


    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_SUMMARY_TITLE, CCLabel.class);
        registerChild(CHILD_SUMMARY_TEXT, CCLabel.class);
        registerChild(CHILD_ALERT, CCAlertInline.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        View child = null;
        if (name.equals(CHILD_SUMMARY_TITLE)) {
            String action = (String)wm.getWizardValue(CopyMoveWizard2View.CHILD_ACTION);
            String value = "";
            if (action.equals("0")) {
                value = m_I18n.getMessage("APOC.wiz.copy.results.copied");
            } else {
                value = m_I18n.getMessage("APOC.wiz.copy.results.moved");
            }
            Object[] args = {value};    
            String title = m_I18n.getMessage("APOC.wiz.copy.results.title", args);
            child = (View)new CCLabel(this,
                name, title);
        } else if (name.equals(CHILD_SUMMARY_TEXT)) {
            String action = (String)wm.getWizardValue(CopyMoveWizard2View.CHILD_ACTION);
            String actionString = "";
            if (action.equals("0")) {
                actionString = m_I18n.getMessage("APOC.wiz.copy.results.copied.lc");
            } else {
                actionString = m_I18n.getMessage("APOC.wiz.copy.results.moved.lc");
            }
            Profile profile =  (Profile)wm.getWizardValue(wm.SOURCE_PROFILE);
            Entity targetEntity = (Entity)wm.getWizardValue(wm.TARGET_ENTITY);
            String targetEntityName = Toolbox2.getParentagePath(
                                        targetEntity,
                                        false,
                                        true,
                                        ">");
            String displayName = profile.getDisplayName();
            Object[] args = {displayName, actionString, targetEntityName};
            String doAssignments = (String)wm.getValue(CopyMoveWizard2View.CHILD_ASSIGNMENTS);
            String text = "";
            if (doAssignments.equals("true")) {
                text = m_I18n.getMessage("APOC.wiz.copy.results.text.assigned", args);
            } else {
                text = m_I18n.getMessage("APOC.wiz.copy.results.text.unassigned", args);
            }
            child = (View)new CCLabel(this,
                name, text);
        } else if (name.equals(CHILD_ALERT)) {
            child = new CCAlertInline(this, name, null);
            String action = (String)wm.getWizardValue(CopyMoveWizard2View.CHILD_ACTION);
            String value = "";
            if (action.equals("0")) {
                value = m_I18n.getMessage("APOC.wiz.copy.copy");
            } else {
                value = m_I18n.getMessage("APOC.wiz.copy.move");
            }
            Object[] args = {value};    
            String summary = m_I18n.getMessage("APOC.wiz.copy.results.alert.title", args);
            ((CCAlertInline)child).setValue(CCAlertFullPage.TYPE_ERROR);
            ((CCAlertInline)child).setDetail("alert");
            ((CCAlertInline)child).setSummary(summary);         
            return child;
        } else {
            throw new IllegalArgumentException(
                "CopyMoveWizard5 : Invalid child name [" + name + "]");
        }
        return child;
    }

    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();       
        String success = (String)wm.getValue(wm.SUCCESS);   

        boolean displayAlert = true;
        if (success.equals("true")) {
            displayAlert = false;
        }

        return displayAlert;
    }
    
    public boolean beginDisplaySummaryTitleDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();     
        String success = (String)wm.getValue(wm.SUCCESS);   

        boolean displayTitle = false;
        if (success.equals("true")) {
            displayTitle = true;
        }
        return displayTitle;
    }   

    public boolean beginDisplaySummaryTextDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        CopyMoveWizardPageModel wm = (CopyMoveWizardPageModel)getDefaultModel();
        String success = (String)wm.getValue(wm.SUCCESS);   

        boolean displayText = false;
        if (success.equals("true")) {
            displayText = true;
        }
        return displayText;
    }    
    
    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/profiles/CopyMoveWizard5.jsp";
    }


    public String getErrorMsg() {
        return null;
    }
}





