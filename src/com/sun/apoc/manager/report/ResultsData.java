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
import com.sun.apoc.templates.parsing.TemplateSection;

public class ResultsData {
    
    
    private TemplateSection m_section = null;
    private ArrayList m_templateProperties = null;
    private ArrayList m_properties = null;
    private String m_path = null;
    private String m_linkPath = null;
    private boolean m_isASet = false;
    
    /**
     * Creates a new instance of ResultsData 
     */
    public ResultsData(TemplateSection section, 
                       ArrayList templateProperties, 
                       ArrayList properties, 
                       String path,
                       String linkPath,
                       boolean isASet) {

        m_section = section;
        m_templateProperties = templateProperties;
        m_properties = properties;
        m_path = path;
        m_linkPath = linkPath;
        m_isASet = isASet;
    }
    
    public TemplateSection getSection() {
        return m_section;
    }
    
    public ArrayList getTemplatePropertyList() {
        return m_templateProperties;
    }
    
    public ArrayList getPropertyList() {
        return m_properties;
    }
    
    public String getPath() {
        return m_path;
    }
    
    public String getLinkPath() {
        return m_linkPath;
    }  
    
    public boolean isASet() {
        return m_isASet;
    }    
    
}
