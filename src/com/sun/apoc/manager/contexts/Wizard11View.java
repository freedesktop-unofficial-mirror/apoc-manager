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


package com.sun.apoc.manager.contexts;

import com.iplanet.jato.model.Model;
import com.iplanet.jato.model.DefaultModel;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.JspDisplayEvent;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.DisplayField;
import com.iplanet.jato.view.RequestHandlingViewBase;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.html.CCCheckBox;
import com.sun.web.ui.view.html.CCStaticTextField;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.alert.CCAlert;
import com.sun.web.ui.view.alert.CCAlertFullPage;

import com.sun.web.ui.view.wizard.CCWizardPage;

import javax.servlet.ServletException;

import com.sun.web.ui.common.CCDebug;

import java.io.IOException;

import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Constants;

/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class Wizard11View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil {

    // The "logical" name for this page.
    public static final String PAGE_NAME = "Wizard11View";

    // Child view names (i.e. display fields).

    public static final String CHILD_SUMMARY_TITLE =
        "SummaryTitle";
    public static final String CHILD_SUMMARY_TEXT =
        "SummaryText";
    public static final String CHILD_ALERT =
        "Alert";
    private CCI18N m_I18n;
    private boolean isEditWizard;
    
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public Wizard11View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        if (wm.getValue(wm.WIZARD_CONFIG_FILE) != null) {
            isEditWizard = true;
        }
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }

    public Wizard11View(View parent, Model model, String name) {

        super(parent, name);
        setDefaultModel(model);
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        if (wm.getValue(wm.WIZARD_CONFIG_FILE) != null) {
            isEditWizard = true;
        }
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

        View child = null;
        if (name.equals(CHILD_SUMMARY_TITLE)) {
            String label = "APOC.wiz.11.title";
            if (isEditWizard) {
                label = "APOC.wiz.11.edit.title";
            }
            child = (View)new CCLabel(this,
                name, m_I18n.getMessage(label));
        } else if (name.equals(CHILD_SUMMARY_TEXT)) {
            String label = "APOC.wiz.11.text";
            if (isEditWizard) {
                label = "APOC.wiz.11.edit.text";
            }
            child = (View)new CCLabel(this,
                name, m_I18n.getMessage(label));
        } else if (name.equals(CHILD_ALERT)) {
            AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();   
            child = new CCAlertInline(this, name, null);
            ((CCAlertInline)child).setValue(CCAlertFullPage.TYPE_ERROR);
            ((CCAlertInline)child).setDetail((String)wm.getValue(wm.FAIL_MESSAGE));
            String label = "APOC.wiz.11.alert.title";
            if (isEditWizard) {
                label = "APOC.wiz.11.alert.edit.title";
            }
            ((CCAlertInline)child).setSummary(m_I18n.getMessage(label));         
            return child;
        } else {
            throw new IllegalArgumentException(
                "WizardPage11View : Invalid child name [" + name + "]");
        }
        return child;
    }

    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();        
        String success = (String)wm.getValue(wm.SUCCESS);   

        boolean displayAlert = true;
        if (success.equals("true")) {
            displayAlert = false;
        }

        return displayAlert;
    }
    
    public boolean beginDisplaySummaryTitleDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();        
        String success = (String)wm.getValue(wm.SUCCESS);   

        boolean displayTitle = false;
        if (success.equals("true")) {
            displayTitle = true;
        }
        return displayTitle;
    }   

    public boolean beginDisplaySummaryTextDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();        
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
        return "/jsp/contexts/Wizard11.jsp";
    }


    public String getErrorMsg() {
        return null;
    }
}


