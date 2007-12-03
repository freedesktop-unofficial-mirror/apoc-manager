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

package com.sun.apoc.manager.settings;

import com.iplanet.jato.view.ContainerView;
import com.iplanet.jato.view.RequestHandlingViewBase;
import com.iplanet.jato.view.View;
import com.sun.apoc.templates.parsing.TemplateElement;
import com.sun.apoc.templates.parsing.TemplatePage;
import com.sun.apoc.templates.parsing.TemplateSection;
import com.sun.apoc.templates.parsing.TemplateSet;


public class Sheet extends RequestHandlingViewBase {
    
    public static final String CHILD_SECTION = "PolicySection";
    
    private boolean m_hasSubCategories = false;
    
    public Sheet(ContainerView parent, 
            SheetModel model, String name, boolean hasSubCategories) {
        super(parent, name);
        if (model == null) {
            throw new IllegalArgumentException("Model must not be null.");
        }
        setDefaultModel(model);
        m_hasSubCategories = hasSubCategories;
        registerChildren();
    }
    
    protected void registerChildren() {
        for(int i = 0; i < getModel().getNumRows(); i++) {
            getModel().setRowIndex(i);
            TemplateElement element = (TemplateElement) 
                    getModel().getValue(SheetModel.TEMPLATE_SECTION);
            registerChild(element.getDefaultName(), Section.class);
        }
    }
    
    protected View createChild(String name) {
        TemplatePage page = getModel().getPage();
        TemplateSection section = page.getSection(name); 
        if (section != null) { 
            getModel().setRowIndex(page.getSectionIndex(name));
            
            // The template page consists of sections and sets. Sets are 
            // different from sections as they are just displaying links 
            // (instead of displaying properties in sections).    
            if (section instanceof TemplateSet) {
                SetModel setModel = new SetModel(section);
                Section child = new Section(this, setModel, name);
                getModel().setValue(SheetModel.SECTION_MODEL, setModel);
                return child;
            } else {
                SectionModel sectionModel = new SectionModel(section, getModel());
                Section child = new Section(this, sectionModel, name);
                getModel().setValue(SheetModel.SECTION_MODEL, sectionModel);
                return child;
            }
        } else {
            throw new IllegalArgumentException( 
                "Invalid child name [" + name + "]");
        }
    }
    
    public boolean hasSubCategories() {
        return m_hasSubCategories;
    }
    
    public SheetModel getModel() {
        return (SheetModel) super.getDefaultModel();
    }
}
