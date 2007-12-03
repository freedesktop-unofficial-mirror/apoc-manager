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

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;


public class TemplatePage extends TemplateCategory {
    
    private List m_sections = null;
    private List m_xmlHandlers = null;
    private List m_actionHandlers = null;
    private HashMap m_index = null;
    private HashMap m_xmlHandler_index = null;
    private HashMap m_actionHandler_index = null;
    private String m_dataPath = null;
    private String m_helpFile = null;
    private String m_packageName = null;

    
    
    public TemplatePage(String defaultName, String scope, String resourceId, 
            String resourceBundle, TemplateCategory parent) {
        super(defaultName, scope, resourceId, resourceBundle, parent);
    }
    
    public TemplatePage(String defaultName, String scope, String resourceId, 
            String resourceBundle, String helpFile, TemplateCategory parent) {
        super(defaultName, scope, resourceId, resourceBundle, parent);
        m_helpFile = helpFile;
    }
    
    public TemplatePage(String defaultName, String scope, String resourceId, 
            String resourceBundle, String helpFile, TemplateCategory parent, 
            String templatePackage) {
        super(defaultName, scope, resourceId, resourceBundle, parent);
        m_helpFile = helpFile;
        m_packageName = templatePackage;
    }
    
    public List getSections() {
        return m_sections;
    }
    
    
    public TemplateSection getSection(int index) {
        return (TemplateSection) m_sections.get(index);
    }
    
    
    public TemplateSection getSection(String name) {
        return (TemplateSection) m_index.get(name);
    }

    public TemplateXMLHandler getXMLHandler(int index) {
        return (TemplateXMLHandler) m_xmlHandlers.get(index);
    }    
    
    public TemplateXMLHandler getXMLHandler(String name) {
        return (TemplateXMLHandler) m_xmlHandler_index.get(name);
    }

    public TemplateActionHandler getActionHandler(int index) {
        return (TemplateActionHandler) m_actionHandlers.get(index);
    }    
    
    public TemplateActionHandler getActionHandler(String name) {
        return (TemplateActionHandler) m_actionHandler_index.get(name);
    }    
    
    public int getSectionIndex(String name) {
        Iterator it = m_sections.iterator();
        int result = 0;
        while (it.hasNext()) {
            TemplateSection section = (TemplateSection) it.next();
            if (section.getDefaultName().equals(name)) {
                return result;
            }
            result++;
        }
        return -1;
    }
    
    
    void addSection(TemplateSection section) {
        if (m_sections == null) {
            m_sections = new LinkedList();
            m_index = new HashMap();
        }
        m_sections.add(section);
        m_index.put(section.getDefaultName(), section);
    }
    
    public void addXMLHandler(TemplateXMLHandler handler) {
        if (m_xmlHandlers == null) {
            m_xmlHandlers = new LinkedList();
            m_xmlHandler_index = new HashMap();
        }
        m_xmlHandlers.add(handler);
        m_xmlHandler_index.put(handler.getName(), handler);
    }

    public void addActionHandler(TemplateActionHandler handler) {
        if (m_actionHandlers == null) {
            m_actionHandlers = new LinkedList();
            m_actionHandler_index = new HashMap();
        }
        m_actionHandlers.add(handler);
        m_actionHandler_index.put(handler.getName(), handler);
    }    
    
    public String getDataPath() {
        return m_dataPath;
    }
    
    
    public void setDataPath(String path) {
        m_dataPath = path;
    }

    public String getHelpFile() {
        return m_helpFile;
    }
    
    
    public String getTemplatePackageName() {
        return m_packageName;
    }
    
    
    public boolean hasVisibleContent(byte scope) {
        Iterator it = getSections().iterator();
        while (it.hasNext()) {
            TemplateSection section = (TemplateSection) it.next();
            if (section.isInScope(scope)) {
                if (section.hasVisibleContent(scope)) {
                    return true;
                }
            }
        }
        return false;
    }
}
    


