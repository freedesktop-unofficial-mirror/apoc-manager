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


package com.sun.apoc.manager.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

import com.iplanet.jato.model.DefaultModel;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.RequestParticipant;
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestManager; 

import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.common.CCI18N;

import com.sun.apoc.templates.parsing.TemplateCategory;
import com.sun.apoc.templates.parsing.TemplatePage;
import com.sun.apoc.templates.parsing.TemplateSection;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.sun.apoc.manager.Constants;


public class SectionTiledModel extends DefaultModel  {
        
    private ArrayList m_resultsData = null;
    private CCI18N m_I18n = null;
    private String m_Id = "1";
    /**
     * Creates a new instance of SectionTiledModel 
     */
    public SectionTiledModel() {
        super();
        m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
    }
    
    public void setData(ArrayList resultsData, String id) {
        m_resultsData = resultsData;
        m_Id = id; 
        try {
            this.setSize(m_resultsData.size());
        } catch (ModelControlException mce){
            CCDebug.trace2("Model contol expection caught: "+mce);
        } 
    }
    
    public String getId() {
        String superId = m_Id + "." + Integer.toString(this.getRowIndex()+1);
        return superId;            
    }
    
    public void retrieve() throws ModelControlException {
        Iterator it = m_resultsData.iterator();
        ResourceRepository resourceRepository  = ResourceRepository.getDefaultRepository();
        int i = 1;
        while (it.hasNext()) {
            String id = Integer.toString(i);
            String superId = m_Id + ".";
            ResultsData data = (ResultsData)it.next();
            String rawPagePath = data.getPath();
            String pagePath = rawPagePath.substring(rawPagePath.indexOf(";")+1);
            pagePath = pagePath.replaceAll(";", " > ");
            pagePath = pagePath + " > ";
            String sectionName = resourceRepository.getMessage(data.getSection().getResourceId(),
                                                    data.getSection().getResourceBundle(),
                                                    RequestManager.getRequest());            

            String section = pagePath + sectionName;

            if (data.isASet()) {
                String queriedName = rawPagePath.substring(rawPagePath.lastIndexOf(";")+1);
                section = rawPagePath.substring(rawPagePath.indexOf(";")+1, rawPagePath.lastIndexOf(";"));
                section = section.replaceAll(";", " > ");
                section = section + " > " + queriedName + ":" + sectionName;                    
            }
            
            setValue(SectionTiledView.CHILD_SECTION, section);
            setValue(SectionTiledView.CHILD_ROW_ID, superId + id);
            setValue(SectionTiledView.CHILD_TURNER_ID, CategoryTiledModel.TURNER_ID_PREFIX + superId + id);
            setValue(SectionTiledView.CHILD_IMAGE_ID, CategoryTiledModel.IMAGE_ID_PREFIX + superId + id);
            i++;
            next();
        }
        first();
    }

    public ResultsData getCurrentSectionData() {
        return (ResultsData)m_resultsData.get(this.getRowIndex());
    }
}
