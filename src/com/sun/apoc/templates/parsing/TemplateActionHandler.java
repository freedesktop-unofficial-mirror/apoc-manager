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
import com.sun.apoc.templates.handler.ActionHandlerClassLoader;
import com.sun.web.ui.common.CCDebug;

public class TemplateActionHandler {
    
    private String m_name = null;
    private String m_classname = null;
    private String m_packageDir = null;
    
    public TemplateActionHandler(String name, String classname, String packageDir) {
        m_name = name ;
        m_classname = classname ;
        m_packageDir = packageDir ;
    }
    
    public String getName() {
        return m_name;
    }
    
    public String getClassName() {
        return m_classname;
    }
    
    public String getPackageDir() {
        return m_packageDir;
    }
    
    public Object getClassInstance() {
        Object object = null;
        try {
            ActionHandlerClassLoader loader = new ActionHandlerClassLoader();
            loader.setPackageDir(m_packageDir);
            Class classDefinition = loader.findClass(m_classname);
            object = classDefinition.newInstance();
        } catch (ClassNotFoundException e) {
            System.out.println("getClassInstance(): " + e);
        } catch (InstantiationException e) {
            System.out.println("getClassInstance(): " + e);
        } catch (IllegalAccessException e) {
            System.out.println("getClassInstance(): " + e);
        } 
        return object;
    }
}


