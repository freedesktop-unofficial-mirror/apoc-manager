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


package com.sun.apoc.templates.handler;

import com.sun.apoc.templates.parsing.TemplateProperty;
import com.sun.apoc.manager.settings.SectionModel;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.cfgtree.DataType;
import com.sun.apoc.spi.cfgtree.property.Property;

/**
 * <p>An ActionHandlerProperty is an object returned from a HandlerContext which is
 * used to get/set the various fields for a property involved in an ActionHandler
 * operation. </p>
 */
public class ActionHandlerProperty {
    
    private TemplateProperty mTemplateProperty = null;
    private String mValue = null;
    private boolean mIsEnforced = false;
    
    public ActionHandlerProperty(TemplateProperty tempProp, String value, boolean isEnforced) {
        mTemplateProperty = tempProp;
        mValue = value;
        mIsEnforced = isEnforced;
    }
    
    /**
     * Returns the value of the property being loaded or saved depending on the 
     * action in question.
     *
     * @return   the value of the property.
     */  
    public String getValue() {
        return mValue;
    }

    /**
     * Returns the name of the property as specified in the policy template. 
     *
     * @return   the name of the property.
     */     
    public String getName() {
        return mTemplateProperty.getDefaultName();
    }

    /**
     * Returns the dataPath of the property as specified in the policy template.
     *
     * @return   the dataPath of the property.
     */     
    public String getDataPath() {
        return mTemplateProperty.getDataPath();
    }
    
    /**
     * Returns the visualType of the property as specified in the policy template.
     *
     * @return   the visualType of the property.
     */      
    public String getVisualType() {
        return mTemplateProperty.getVisualType();
    }
    
    /**
     * Returns the data type (oor:type) of the property as specified in the 
     * policy template.
     *
     * @return   the data type of the property.
     */
    public DataType getDataType() {
        return SectionModel.getDataType(mTemplateProperty);
    }

    /**
     * Returns true if the property value is enforced in this profile. 
     */
    public boolean isEnforced() {
        return mIsEnforced;
    }

    /**
     * Sets the value of the property being loaded or saved depending on the 
     * action in question.
     *
     * @param    value - the value of the property to set.
     */      
    public void setValue(String value) {
        mValue = value;
    }
}
