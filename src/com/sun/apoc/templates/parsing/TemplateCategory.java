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

package com.sun.apoc.templates.parsing;

import java.util.HashMap;

public class TemplateCategory extends TemplateElement 
                              implements NarratedElement {
    
    private HashMap m_subCategories = null;
    private String m_descriptionId = null;
    private TemplateCategory m_parent = null;
    
    
    public TemplateCategory(String defaultName, String scope, String resourceId, 
            String resourceBundle, TemplateCategory parent) {
        super(defaultName, scope, resourceId, resourceBundle);
        m_parent = parent;
    }
    
    
    public boolean hasSubCategories() {
        return m_subCategories != null;
    }
    
    
    public HashMap getSubCategories() {
        return m_subCategories;
    }
    
    
    public TemplateCategory getSubCategory(String name) {
        if (m_subCategories != null) {
            return (TemplateCategory) m_subCategories.get(name);
        } else {
            return null;
        }
    }

    
    void addSubCategory(TemplateCategory category) {
        if (m_subCategories == null) {
            m_subCategories = new HashMap();
        }
        if (!m_subCategories.containsKey(category.getDefaultName())) {
            m_subCategories.put(category.getDefaultName(), category);
        }
    }
    
    
    public String getDescriptionId() {
        return m_descriptionId;
    }
    
    
    public void setDescriptionId(String resourceId) {
        m_descriptionId = resourceId;
    }
    
    
    public TemplateCategory getParent() {
        return m_parent;
    }
    
    
    public String getPath() {
        StringBuffer path = new StringBuffer();
        for (TemplateCategory category = this; category != null; category = category.getParent()) {
            String name = category.getDefaultName();
            if (!name.equals(TemplateRepository.ROOT_CATEGORY_NAME)) {
                path.insert(0, name);
                path.insert(0, TemplateRepository.TEMPLATE_PATH_SEPARATOR);
            }
        }
        return path.toString();
    }
    
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(getDefaultName());
        buffer.append(" (");
        buffer.append(getResourceId());
        buffer.append(", ");
        buffer.append(getResourceBundle());
        buffer.append(") ");
        return buffer.toString();
    }
}
