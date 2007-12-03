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

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.RequestParticipant;
import com.iplanet.jato.model.DefaultModel;
import com.sun.apoc.templates.parsing.TemplatePage;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.apoc.templates.parsing.TemplateSection;
import com.sun.apoc.manager.profiles.ProfileWindowModel;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.manager.ProfileWindowFramesetViewBean;


public class SheetModel extends DefaultModel implements RequestParticipant {

    public static final String TEMPLATE_SECTION        = "TemplateSection";  
    public static final String TEMPLATE_SECTION_NAME   = "TemplateSectionName";
    public static final String TEMPLATE_SECTION_ANCHOR = "TemplateSectionAnchor";
    public static final String SECTION_MODEL           = "TemplateSectionModel";
        
    private TemplatePage m_page = null;
    private String mSetIndex = null;
    private ProfileWindowModel mEditorModel = null;
    private RequestContext mRequestContext = null;
    private ArrayList m_choosersIds = new ArrayList();
    private ArrayList m_xmlHandlers = new ArrayList();
    private HashMap m_choosers = new HashMap();
    
    public TemplatePage getPage() {
        return m_page;
    }
    
    public boolean hasSections() {
        if (m_page.getSections() == null) {
            return false;
        } else {
            return m_page.getSections().size() > 0;
        }
    }    
    
    public String getSetIndex() {
        return mSetIndex;
    }
                                     
    public void setRequestContext(RequestContext ctx) {
        mRequestContext = ctx;
        m_choosersIds = new ArrayList();
        m_xmlHandlers = new ArrayList();
        m_choosers = new HashMap();
        TemplateRepository repository = TemplateRepository.getDefaultRepository();
        String templatePath = getEditorModel().getSelectedCategory();
        
        int setIndexPos = templatePath.indexOf(TemplateRepository.SET_INDEX_SEPARATOR);
        if (setIndexPos != -1) {
            mSetIndex = templatePath.substring(setIndexPos+1);
        }
        
        getRowList().clear();
        m_page = repository.getPage(templatePath, getEditorModel().getSettingsScope());
        if ((m_page != null) && (m_page.getSections() != null)) {
            Iterator it = m_page.getSections().iterator();
            while(it.hasNext()) {
                TemplateSection section = (TemplateSection) it.next();
                if (section.isInScope(getEditorModel().getSettingsScope())) {
                    appendRow();
                    ResourceRepository resources = ResourceRepository.getDefaultRepository();
                    String name = resources.getMessage(
                                        section.getResourceId(), 
                                        section.getResourceBundle(), 
                                        RequestManager.getRequest());
                    
                    setValue(TEMPLATE_SECTION, section);
                    setValue(TEMPLATE_SECTION_NAME, name);
                    setValue(TEMPLATE_SECTION_ANCHOR, section.getDefaultName());
                }
            }
        }
    }
    
    public void storeChanges() throws SPIException {
        for(int i = 0; i < getNumRows(); i++) {
            setRowIndex(i);
            Object model = getValue(SECTION_MODEL);
            if (model instanceof SetModel) {
                
            } else {
                SectionModel sectionModel = (SectionModel) model;
                sectionModel.storeChanges();    
            }
        }
        // up to now the changes are only in the cached policy trees
        // the next command will make the changes permanent
        ProfileWindowFramesetViewBean.getProfileHelper().flushAllChanges();                                       
    }
    
    public ArrayList getChooserIds() {
        if (m_choosersIds.size() == 0) {
            return null;
        }
        return m_choosersIds;    
    }
    
    public ArrayList getChooserValues(String id) {
        if (m_choosers.containsKey(id)) {
            return (ArrayList)m_choosers.get(id);
        } else {
            return null;
        }
    }
    
    public void addToChooserLists(String id, String value) {
        int index = m_choosersIds.indexOf(id);
        ArrayList chooserList = new ArrayList();        
        if (index == -1) {
            m_choosersIds.add(0, id);
            chooserList.add(value);
            m_choosers.put(id, chooserList);
        } else {
            chooserList = (ArrayList)m_choosers.get(id);
            chooserList.add(value);
            m_choosers.put(id, chooserList);
        }       
        
    }
    
    public void resetChoosers() {
        m_choosersIds.clear();
        m_choosers.clear();
    }

    public ArrayList getXmlHandlers() {
        if (m_xmlHandlers.size() == 0) {
            return null;
        }
        return m_xmlHandlers;    
    }    
    
    public void addToXmlHandlerList(String handlerText) {
        m_xmlHandlers.add(handlerText);
    }
    
    public void resetXmlHandlers() {
        m_xmlHandlers.clear();
    }
     

    public ProfileWindowModel getEditorModel() {
        if (mEditorModel == null) {
            mEditorModel = (ProfileWindowModel) mRequestContext.getModelManager().getModel(ProfileWindowModel.class);
        }
        return mEditorModel;
    }
}
