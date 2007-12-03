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

import com.sun.apoc.manager.settings.PolicyMgrHelper;
import com.sun.apoc.manager.settings.SectionModel;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.cfgtree.property.Property;
import java.util.Iterator;

/**
 * <p>Implements the ActionHandler interface.</p> 
 * <p>The default behavior for the handleLoad/handleSave methods are implemented here.</p> 
 * <p>For example the default behavior for the handleLoad method is to run through 
 * all the properties in a template with the specified ActionHandler, retrieve 
 * the properties value from the backend and set the value of this property in 
 * the frontend accordingly.</p>
 * <p>Override these methods if you want to perform non-default actions such as 
 * combining multiple backend property values into a single frontend property 
 * value or vice versa.</p>
 * <p>For simple actions in which you still require the default 
 * handleLoad/handleSave behavior but wish to convert the properties value to 
 * another single value then override the 
 * performLoadConversion/performSaveConversion methods.</p>
 */
public class BaseActionHandler implements ActionHandler {

    public void handleLoad(HandlerContext context) throws SPIException {
        PolicyMgrHelper mgr = context.getPolicyMgrHelper();
        String localizedNotSet = context.getLocalizedUndefinedValue();
        
        // iterate over all properties and retrieve the correct values
        Iterator it = context.getProperties();
        while (it.hasNext()) {
            ActionHandlerProperty element = (ActionHandlerProperty) it.next();
            
            // try to retrieve the value from the repository
            Property property = mgr.getProperty(element.getDataPath());
            String value = localizedNotSet;
            if (property != null) {
                value = property.getValue();
            } 
            element.setValue(performLoadConversion(value));
        }
    }
    
    /**
     * Override this method to perform a simple conversion action on a properties value
     * without having to handle the load operation from the backend. 
     * 
     * @param    value - the raw value of the property from the backend.
     * @return   value - the converted value of the property to be loaded in the frontend.
     */      
    protected String performLoadConversion(String value) {
        return value;
    }
 
    public void handleSave(HandlerContext context) throws SPIException {
        PolicyMgrHelper mgr = context.getPolicyMgrHelper();
        String localizedNotSet = context.getLocalizedUndefinedValue();
        // iterate over all properties and retrieve the correct values
        Iterator it = context.getProperties();
        while (it.hasNext()) {

            ActionHandlerProperty element = (ActionHandlerProperty) it.next();
            String newValue = element.getValue();
            String previousValue = localizedNotSet;
            Property property = mgr.getProperty(element.getDataPath());
            if (property != null) {
                previousValue = property.getValue();
            }
            if (!previousValue.equals(newValue)) {
                
                if ((localizedNotSet.equals(newValue))
                        && (!element.isEnforced())) {     
                    mgr.removeProperty(element.getDataPath());
                } else {
                    if (property == null) {
                        property = mgr.createProperty(element.getDataPath());
                    }
                    property.put(performSaveConversion(newValue), element.getDataType());
                }
            }
         }
     }

    /**
     * Override this method to perform a simple conversion action on a properties value
     * without having to handle the save operation to the backend. 
     * 
     * @param    value - the value of the property entered in the frontend.
     * @return   value - the converted value of the property to be saved in the backend.
     */    
     protected String performSaveConversion(String value) {
        return value;
     }
}
