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
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.sun.web.ui.view.html.CCTextField;
import com.sun.web.ui.view.alert.CCAlertInline;
import com.sun.web.ui.view.filechooser.*;

import com.sun.web.ui.model.CCFileChooserModel;
import com.sun.web.ui.model.CCFileChooserModelInterface;

import com.sun.web.ui.view.wizard.CCWizardPage;

import com.sun.web.ui.common.CCDebug;
import javax.servlet.ServletException;

import java.io.File;
import java.io.IOException;

import com.sun.web.ui.common.CCI18N;
import com.iplanet.jato.RequestManager;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.apoc.spi.environment.EnvironmentMgr;
import com.sun.web.ui.view.html.CCHiddenField;
import java.util.Properties;

/**
 * A ContainerView object for the pagelet.
 *
 * @version 
 * @author  
 */
public class Wizard12View extends RequestHandlingViewBase implements CCWizardPage, WizardImplDataUtil {

    // The "logical" name for this page.
    public static final String PAGE_NAME = "Wizard12View";

    // Child view names (i.e. display fields).

    public static final String CHILD_FOLDERCHOOSER =
        "FolderChooser";    
    public static final String CHILD_ALERT =
        "Alert"; 
    public static final String CHILD_IS_FIRST_VIEW =
        "isFirstView12";    
    private CCI18N m_I18n;
    
    /**
     * Construct an instance with the specified properties.
     * A constructor of this form is required
     *
     * @param parent The parent view of this object.
     * @param name This view's name.
     */
    public Wizard12View(View parent, Model model) {
        this(parent, model, PAGE_NAME);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }

    public Wizard12View(View parent, Model model, String name) {
        super(parent, name);
        setDefaultModel(model);
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        registerChildren();
    }

 
    /**
     * Register each child view.
     */
    protected void registerChildren() {
        registerChild(CHILD_FOLDERCHOOSER, CCFileChooser.class);
        registerChild(CHILD_ALERT, CCAlertInline.class);
        registerChild(CHILD_IS_FIRST_VIEW, CCHiddenField.class);
    }

    /**
     * Instantiate each child view.
     */
    protected View createChild(String name) {

        View child = null;
        if (name.equals(CHILD_FOLDERCHOOSER)) {
            // Create a folderchooser child.
            child = new CCFileChooser(this, createFolderModel(), name);
        } else if (name.equals(CHILD_ALERT)) {
            child = new CCAlertInline(this, name, null);
            return child;
        } else if (name.equals(CHILD_IS_FIRST_VIEW)) {
            child = (View)new CCHiddenField(this, name, "true");  
        } else {
            throw new IllegalArgumentException(
            "WizardPage12View : Invalid child name [" + name + "]");
        }
        return child;
    }

    // Create a CCFileChooserModelInterface model.
    // Return the cached model if it already exists
    private CCFileChooserModelInterface createFolderModel() {
        CCFileChooserModel model = null;
        // instantiate the model
        model = new CCFileChooserModel();
        model.setFileListBoxHeight(7);
        model.setAlertChildView(CHILD_ALERT);
        return model;
  } 
    
    /*
     * Get the pagelet to use for the rendering of this instance.
     *
     * @return The pagelet to use for the rendering of this instance.
     */
    public String getPageletUrl() {
        return "/jsp/contexts/Wizard12.jsp";
    }

    public void beginDisplay(DisplayEvent event)
    throws ModelControlException {
        CCFileChooser child = (CCFileChooser) getChild(CHILD_FOLDERCHOOSER); 
        try {
            child.restoreStateData(); 
        } catch (Exception mce) {
            CCDebug.trace3(mce.getMessage());
        }
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        String sessionFilePath = (String)wm.getValue(wm.FILEPATH);
        if (sessionFilePath != null) {
            String sessionFilename = sessionFilePath.substring(sessionFilePath.lastIndexOf("/") + 1);
            if (sessionFilename != null) {
                CCTextField filename = (CCTextField)(child.getChild(child.FILE_NAME_TEXT));
                filename.setValue(sessionFilename);            
            }
        }
        Properties properties = (Properties)wm.getValue(wm.PROPERTIES);
        CCHiddenField isFirstView = (CCHiddenField)getChild(CHILD_IS_FIRST_VIEW);
        if (isFirstView.getValue().equals("true") && properties != null) {
            String oldAssignProviderUrl = properties.getProperty(EnvironmentConstants.ORGANIZATION_PREFIX
                                                    + EnvironmentConstants.ASSIGNMENT_PREFIX
                                                    + EnvironmentConstants.URL_KEY);   

            if (oldAssignProviderUrl != null && oldAssignProviderUrl.startsWith("file")) {
                String oldProviderUrl = oldAssignProviderUrl.substring(0, oldAssignProviderUrl.lastIndexOf("/"));
                String filepath = EnvironmentMgr.getPathFromURL(oldProviderUrl);
                File file = new File(filepath);
                if (file.exists()) {
                    CCTextField filename = (CCTextField)(child.getChild(child.FILE_NAME_TEXT));
                    filename.setValue(filepath.substring(filepath.lastIndexOf("/")+1));
                    filepath = filepath.substring(0, filepath.lastIndexOf("/"));
                    if (filepath.length() == 0) {
                        filepath = "/";
                    }
                    child.getModel().setHomeDirectory(filepath);
                    child.getModel().setCurrentDirectory(filepath);
                }
            } 
        }
        isFirstView.setValue("false");
    }  
    
    public boolean beginDisplayAlertDisplay(ChildDisplayEvent event)
        throws ServletException, IOException {
        return false;
    }
    
    public String getErrorMsg() {
        CCFileChooser child = (CCFileChooser) getChild(CHILD_FOLDERCHOOSER);
        AddContextWizardPageModel wm = (AddContextWizardPageModel)getDefaultModel();
        
        try {
            child.restoreStateData(); 
        } catch (Exception mce) {
            CCDebug.trace3(mce.getMessage());
        }
        String[] file = child.getSelectedResources(); 
        
        // Hacks for Lockhart folderchooser bugs CR6229872 & CR5092619
        // Remove when these bugs are fixed and integrated
        CCTextField filename = (CCTextField)(child.getChild(child.FILE_NAME_TEXT));
        CCTextField rootDir = (CCTextField)(child.getChild(child.LOOK_IN_TEXTFIELD));
        String filepath = (String)rootDir.getValue() + "/" + (String)filename.getValue();
        if (filepath.substring(0, 2).equals("//")) {
            filepath = filepath.substring(1);
        }
       
        File dir = new File(filepath);
        Object[] args = {filepath};
        if (!dir.exists()) {
            return m_I18n.getMessage("APOC.wiz.12.alert1", args);
        }
        if (!dir.isDirectory()) {
            return m_I18n.getMessage("APOC.wiz.12.alert2", args);
        }
        if (!dir.canWrite()) {
            return m_I18n.getMessage("APOC.wiz.12.alert3", args);
        }        

        wm.setValue(wm.FILEPATH, filepath);
        return null;
    }
}


