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

import java.util.HashMap;
import java.util.Iterator;

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.RequestParticipant;
import com.sun.apoc.templates.parsing.TemplateCategory;
import com.sun.apoc.templates.parsing.TemplatePage;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.sun.web.ui.model.CCActionTableModel;


public class ProfileWindowSettingsCategoriesTableModel extends CCActionTableModel 
        implements RequestParticipant {

    public static final String CATEGORY_NAME      = "CategoryName";
    public static final String CATEGORY_HREF      = "CategoryHref";
    public static final String CATEGORY_DESCR     = "CategoryDescription";
    
    private ProfileWindowModel mEditorModel = null;
    private RequestContext mRequestContext = null;
    private TemplatePage mTemplatePage = null;
    
    public ProfileWindowSettingsCategoriesTableModel() {
        super(RequestManager.getRequestContext().getServletContext(),
            "/jsp/categories/Navigation.xml");
        setActionValue("NameColumn", "APOC.policies.nameCol");    
        setActionValue("DescriptionColumn", "APOC.policies.commentCol");
    }

    public void setRequestContext(RequestContext ctx) {
        mRequestContext = ctx;
        String templatePath = getEditorModel().getSelectedCategory();
        TemplateRepository templateRepository = TemplateRepository.getDefaultRepository();
        mTemplatePage = templateRepository.getPage(templatePath, getEditorModel().getSettingsScope());
        getRowList().clear();
        if ((mTemplatePage != null) && (mTemplatePage.hasSubCategories())) {
            HashMap subCategories = mTemplatePage.getSubCategories();
            Iterator it = subCategories.values().iterator();
            ResourceRepository resources = ResourceRepository.getDefaultRepository();
            while(it.hasNext()) {
                TemplateCategory tiledCategory = (TemplateCategory) it.next();
                if (tiledCategory.isInScope(getEditorModel().getSettingsScope())) {
                    appendRow();
                    // set category name
                    String name = resources.getMessage(
                                        tiledCategory.getResourceId(), 
                                        tiledCategory.getResourceBundle(), 
                                        RequestManager.getRequest());
                    setValue(CATEGORY_NAME, name);
                    
                    // set category description
                    String description = "";
                    if (tiledCategory.getDescriptionId() != null) {
                        description = resources.getMessage(
                                        tiledCategory.getDescriptionId(), 
                                        tiledCategory.getResourceBundle(), 
                                        RequestManager.getRequest());
                    }
                    setValue(CATEGORY_DESCR, description);
                    
                    // set category path
                    StringBuffer path = new StringBuffer(templatePath);
                    path.append(TemplateRepository.TEMPLATE_PATH_SEPARATOR);
                    path.append(tiledCategory.getDefaultName());
                    setValue(CATEGORY_HREF, path.toString());
                }
            }
        }
    }
    
    public TemplatePage getSelectedTemplatePage() {
        return mTemplatePage;
    }
    
    public boolean hasSubCategories() {
        if (mTemplatePage != null) {
            return mTemplatePage.hasSubCategories();
        } else {
            return true;
        }
    }
    
    private ProfileWindowModel getEditorModel() {
        if (mEditorModel == null) {
            mEditorModel = (ProfileWindowModel) mRequestContext.getModelManager().getModel(ProfileWindowModel.class);
        }
        return mEditorModel;
    }
}

