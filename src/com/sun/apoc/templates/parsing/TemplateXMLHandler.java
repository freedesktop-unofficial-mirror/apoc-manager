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
import java.util.ArrayList;



public class TemplateXMLHandler {
    
    private String m_name = null ;
    private String m_type = null ;
    private List m_tests = null ;
    private List m_commands = null ;
    private List m_whenCommands = null ;
    private List m_otherCommands = null ;

    public TemplateXMLHandler(String name) {
	m_name = name ;
    }
    
    public void addType(String type) {
	m_type = type ;
    }
    
    public void addTest(String test) {
        if (m_tests == null) {
            m_tests = new ArrayList();
        }
        m_tests.add(test);
    }

    public void addCommand(String command) {
        if (m_commands == null) {
            m_commands = new ArrayList();
        }
        m_commands.add(command);
    }

    public void addNewCommandList() {
	if (m_whenCommands == null) {
            m_whenCommands = new ArrayList();
        }
	m_whenCommands.add(new ArrayList()) ;
    }
    
    public void addWhenCommand(String command) {
        if (m_whenCommands == null) {
            m_whenCommands = new ArrayList();
        }
	((ArrayList)m_whenCommands.get(m_whenCommands.size()-1)).add(command) ;
    }

    public void addOtherwiseCommand(String command) {
        if (m_otherCommands == null) {
            m_otherCommands = new ArrayList();
        }
        m_otherCommands.add(command);
    }    
    
    public String getName() {
	return m_name ;
    }
    
    public String getType() {
	return m_type ;
    }
    
    public List getTests() {
	return m_tests ;
    }   
    
    public String getTest(int index) {
        return (String) m_tests.get(index);
    }   
    
    public List getCommands() {
	return m_commands;
    }
        
    public String getCommand(int index) {
        return (String) m_commands.get(index);
    }
    
    public List getWhenCommandLists() {
        return m_whenCommands;
    }
        
    public List getWhenCommandList(int index) {
        return (List
	) m_whenCommands.get(index);
    }
    
    public List getOtherwiseCommands() {
        return m_otherCommands;
    }
        
    public String getOtherwiseCommand(int index) {
        return (String) m_otherCommands.get(index);
    }    
}


