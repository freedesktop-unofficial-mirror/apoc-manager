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

public abstract class TemplateElement implements LocalizedElement {
    
    public static final String GLOBAL = "global";
    public static final String USER   = "user";
    public static final String HOST   = "host";
    
    public static final byte USER_SCOPE   = 1;
    public static final byte HOST_SCOPE   = 2;
    public static final byte GLOBAL_SCOPE = 3;
    
    private String m_defaultName    = null;
    private String m_resourceBundle = null;
    private String m_resourceId     = null;
    private byte   m_scope          = GLOBAL_SCOPE;

    
    public TemplateElement(String defaultName, String scope, String resourceId, 
            String resourceBundle) {
        m_defaultName = defaultName;
        if (resourceId != null) {
            m_resourceId  = resourceId;
        } else {
            m_resourceId = defaultName;
        }
        m_resourceBundle = resourceBundle;
        setScope(scope);
    }


    protected void setScope(String scope) {
        if (scope != null) {
            if (scope.indexOf(USER) != -1) { 
                m_scope = USER_SCOPE;
            } else if (scope.indexOf(HOST) != -1) {
                m_scope = HOST_SCOPE;
            }
        }
    }
    
    
    public final String getDefaultName() {
        return m_defaultName;
    }

    
    public final String getResourceBundle() {
        return m_resourceBundle;
    }

    
    public final String getResourceId() {
        return m_resourceId;
    }
    
    
    public final boolean hasUserScope() {
        return (m_scope & USER_SCOPE) != 0;    
    }

    
    public final boolean hasHostScope() {
        return (m_scope & HOST_SCOPE) != 0;
    }
    
    
    public final boolean hasGlobalScope() {
        return (m_scope & GLOBAL_SCOPE) != 0;
    }
    
    
    public final boolean isInScope(byte scope) {
        return (m_scope & scope) != 0;
    }
    
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append(" - ");
        buffer.append(getDefaultName());
        buffer.append(" (");
        buffer.append(getResourceId());
        buffer.append(", ");
        buffer.append(getResourceBundle());
        buffer.append(") ");
        return buffer.toString();
    }
}
