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
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.sun.apoc.spi.environment.EnvironmentConstants;

import com.sun.web.ui.view.html.CCLabel;
import com.sun.web.ui.view.html.CCDropDownMenu;
import com.iplanet.jato.view.html.OptionList;

import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.Toolbox2;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.model.CCWizardWindowModel;
import com.sun.web.ui.model.CCWizardWindowModelInterface;
import com.sun.web.ui.view.html.CCTextField;

import com.sun.web.ui.view.wizard.CCWizardPage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class Wizard2View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil {

    // The "logical" name for this page.
    public static final String PAGE_NAME = "Wizard2View";

    // Child view names (i.e. display fields).
    public static final String CHILD_NAME_LABEL =
        "NameLabel";
    public static final String CHILD_NAME_FIELD =
        "NameField";
    public static final String CHILD_BACKEND_TYPE_LABEL =
        "BackendTypeLabel";
    public static final String CHILD_BACKEND_TYPE =
        "BackendType";
    private CCI18N m_I18n;
    
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public Wizard2View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        String selectedContext = (String)Toolbox2.getParameter("SelectedContext");
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        if (wm.getValue(wm.WIZARD_TITLE) == null) {
            if (selectedContext != null && selectedContext.length() != 0) {
                wm.setValue(wm.WIZARD_TITLE, m_I18n.getMessage("APOC.wiz.edit.title"));
            } else {
                wm.setValue(wm.WIZARD_TITLE, m_I18n.getMessage("APOC.wiz.title"));
            }
        }
    }

    public Wizard2View(View parent, Model model, String name) {
        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        String selectedContext = (String)Toolbox2.getParameter("SelectedContext");
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        if (wm.getValue(wm.WIZARD_TITLE) == null) {
            if (selectedContext != null && selectedContext.length() != 0) {
                wm.setValue(wm.WIZARD_TITLE, m_I18n.getMessage("APOC.wiz.edit.title"));
            } else {
                wm.setValue(wm.WIZARD_TITLE, m_I18n.getMessage("APOC.wiz.title"));
            }
        }
        registerChildren();
    }


    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_NAME_LABEL, CCLabel.class);
        registerChild(CHILD_NAME_FIELD, CCTextField.class);
        registerChild(CHILD_BACKEND_TYPE_LABEL, CCLabel.class);
        registerChild(CHILD_BACKEND_TYPE, CCDropDownMenu.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        if (name.equals(CHILD_NAME_LABEL)) {
            child = (View)new CCLabel(this,
                name, m_I18n.getMessage("APOC.wiz.2.name"));
        } else if (name.equals(CHILD_NAME_FIELD)) {
            child = (View)new CCTextField(this, name, null);
        } else if (name.equals(CHILD_BACKEND_TYPE_LABEL)) {
            child = (View)new CCLabel(this,
                name, m_I18n.getMessage("APOC.wiz.2.type"));
        } else if (name.equals(CHILD_BACKEND_TYPE)) {
            OptionList options = new OptionList();
            AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
            options.add(0, m_I18n.getMessage("APOC.wiz.2.ldap"), "0");
            options.add(1, m_I18n.getMessage("APOC.wiz.2.file"), "1");
            options.add(2, m_I18n.getMessage("APOC.wiz.2.hybrid"), "2");               
            child = (View)new CCDropDownMenu(this, name, null);
            ((CCDropDownMenu)child).setOptions(options);
        } else {
        throw new IllegalArgumentException(
            "WizardPage2View : Invalid child name [" + name + "]");
        }
        return child;
    }


    /**
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/contexts/Wizard2.jsp";
    }

    public void beginDisplay(DisplayEvent event)
    throws ModelControlException {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        String selectedContext = (String)Toolbox2.getParameter("SelectedContext");
        CCTextField nameField = (CCTextField)getChild(CHILD_NAME_FIELD);
        String nameValue = (String) nameField.getValue();
        if (nameValue == null) {
            if (selectedContext != null) {
                try {
                    File dir = new File(ManagerTableModel.CONFIG_FILE_LOCATION);
                    if (dir.exists()) {
                        FilenameFilter filter = new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.endsWith(".properties");
                            }
                        };
                        File[] files = dir.listFiles(filter);
                        int i = 0;
                        while (i < files.length) {
                            File properties = files[i];
                            if (properties.canRead()) {
                                FileInputStream input = new FileInputStream(properties) ;
                                Properties backendProperties = new Properties();
                                backendProperties.load(input);
                                input.close();
                                if (backendProperties.getProperty("Backend") != null) {
                                    String contextName                 = backendProperties.getProperty("Backend");
                                    if (contextName.equals(selectedContext)) {
                                        populateWizardModel(backendProperties);
                                        wm.setValue(wm.WIZARD_CONFIG_FILE, properties);
                                        break;
                                    }
                                }
                            }
                            i++;
                        }
                    }
                } catch (IOException ioe) {
                    CCDebug.trace3(ioe.toString());
                }
            }
        }

    
        Properties properties = (Properties)wm.getValue(wm.PROPERTIES);
        CCDropDownMenu menu = (CCDropDownMenu)getChild(CHILD_BACKEND_TYPE);
        String menuValue = (String)menu.getValue();
        if (menuValue == null) {
            if (properties != null) {
                String entityUrl = properties.getProperty(EnvironmentConstants.ORGANIZATION_PREFIX
                                                            + EnvironmentConstants.URL_KEY);
                String profileUrl = properties.getProperty(EnvironmentConstants.ORGANIZATION_PREFIX
                                                            + EnvironmentConstants.PROFILE_PREFIX
                                                            + EnvironmentConstants.URL_KEY);
                if (entityUrl != null && profileUrl != null) {
                    if (entityUrl.startsWith("ldap")
                                && profileUrl.startsWith("ldap")) {
                        menu.setValue("0");
                    } else if (entityUrl.startsWith("file")
                                && profileUrl.startsWith("file")) {
                        menu.setValue("1");
                    } else if (entityUrl.startsWith("ldap")
                                && profileUrl.startsWith("file")) {
                        menu.setValue("2");
                    }
                }
            }
        }    
    
    
    }
    
    public String getErrorMsg() {
        String emsg = null;
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        String value = (String)wm.getWizardValue(CHILD_NAME_FIELD);        
        if (value == null || value.length() == 0) {
            emsg = m_I18n.getMessage("APOC.wiz.2.alert");
        }        
        return emsg;
    }
    
    private void populateWizardModel(Properties properties) {
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        if (!(properties.isEmpty())) {
            wm.setValue(wm.PROPERTIES, properties);
            String name = properties.getProperty("Backend");
            CCTextField nameField = (CCTextField)getChild(CHILD_NAME_FIELD);
            nameField.setValue(name);
        }                    
    }
}


