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

import java.io.File;
import java.util.Vector;
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
import com.sun.apoc.templates.parsing.TemplateElement;
import com.sun.apoc.templates.parsing.TemplateSection;
import com.sun.apoc.templates.parsing.TemplateRepository;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.sun.apoc.manager.Constants;


public class CategoryTiledModel extends DefaultModel {

    public static final String TURNER_ID_PREFIX = "turner.";
    public static final String IMAGE_ID_PREFIX = "image.";
    public static final String DESCOL_ID_PREFIX = "desCol.";
    public static final String STATUSCOL_ID_PREFIX = "statusCol.";
    
    private ArrayList m_resultsData = null;
    private ArrayList m_categories = null;
    
    /**
     * Creates a new instance of CategoryTiledModel 
     */
    public CategoryTiledModel() {
        super();
    }
    
    public void setData(ArrayList resultsData) {
        m_resultsData = resultsData;
        m_categories = new ArrayList();
        Iterator it = m_resultsData.iterator();
        while (it.hasNext()) {
            ResultsData data = (ResultsData)it.next();
            String path  = data.getPath();
            String catname = path.substring(0, path.indexOf(';'));
            if (!m_categories.contains(catname)) {
                m_categories.add(catname);
            }
        }

        try {
            this.setSize(m_categories.size());
        } catch (ModelControlException mce){
            CCDebug.trace2("Model contol expection caught: "+mce);
        }       
    }
    
    public void retrieve() throws ModelControlException {
        Iterator it = m_categories.iterator();
        int i = 1;
        while (it.hasNext()) {
            String category = (String)it.next();
            String id = Integer.toString(i);
            setValue(CategoryTiledView.CHILD_CATEGORY, category);
            setValue(CategoryTiledView.CHILD_ROW_ID, id);
            setValue(CategoryTiledView.CHILD_TURNER_ID, TURNER_ID_PREFIX + id);
            setValue(CategoryTiledView.CHILD_IMAGE_ID, IMAGE_ID_PREFIX + id);
            i++;
            next();
        }
        first();
    }
    
    public ArrayList getCurrentCategoryData() {
        String currentCategory = (String)getValue(CategoryTiledView.CHILD_CATEGORY);
        ArrayList dataForCategory = new ArrayList();
        Iterator it = m_resultsData.iterator();
        while (it.hasNext()) {
            ResultsData data = (ResultsData)it.next();
            String path  = data.getPath();
            String catname = path.substring(0, path.indexOf(';'));
            if (catname.equals(currentCategory)) {
                dataForCategory.add(data);
            }
        }
        return dataForCategory;
    }
}
