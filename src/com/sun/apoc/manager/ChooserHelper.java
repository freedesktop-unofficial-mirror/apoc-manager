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

package com.sun.apoc.manager;

import java.util.Vector;
import java.util.Arrays;
import java.lang.Integer;

import com.iplanet.jato.RequestParticipant;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.model.DefaultModel;
import com.iplanet.jato.model.ModelControlException;

import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.common.CCI18N;

import com.iplanet.jato.view.html.OptionList;
import com.iplanet.jato.view.html.Option;

import com.sun.apoc.spi.cfgtree.property.Property;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.SPIException;

import com.sun.apoc.templates.parsing.TemplateRepository ;
import com.sun.apoc.templates.parsing.TemplateCategory ;
import com.sun.apoc.templates.parsing.TemplatePage ;
import com.sun.apoc.templates.parsing.TemplateSection ;
import com.sun.apoc.templates.parsing.TemplateProperty ;
import com.sun.apoc.templates.parsing.TemplatePropertyConstraint;

import com.sun.apoc.manager.settings.PolicyMgrHelper;

import java.net.URLEncoder;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author  sl146471
 */
public class ChooserHelper {

    private ArrayList m_elementsList            = new ArrayList();
    private ArrayList m_statesList              = new ArrayList();
    private TemplateProperty m_chooser          = null;
    private TemplateProperty m_definedChooser   = null;
    private String m_definedChooserPath         = null;
    private boolean m_saveRequired              = false;    

    //String used to maintain constant list width as recommended by HCI guidelines
    public static final String LIST_WIDTH_STRING = "______________________________" ;
    public static final String SYSTEM_ADDED_FLAG = " (*)" ;       
    public static final String LIST_SEPARATOR = ";" ; 
    private static final String STATELIST_DATAPATH = "_states" ;
    

    public ChooserHelper(String chooserId) {
        m_chooser = Toolbox2.getProperty(chooserId);
        m_definedChooser = m_chooser;
        m_definedChooserPath = chooserId;
        initChooserHelper();
    }

    private void initChooserHelper() {
        // initialize the TemplateProperty which defines this choosers attrs
        String extendsChooser = m_chooser.getExtendsChooser();
        if(extendsChooser != null) {
            m_definedChooser = Toolbox2.getProperty(extendsChooser); 
            m_definedChooserPath = extendsChooser;
        }
        
        try {
            //initialize the value and states lists
            PolicyMgrHelper pmgrHelper  = ProfileWindowFramesetViewBean.getProfileHelper();        
            Property elementsProp       = pmgrHelper.getProperty(m_definedChooser.getChooserPath());
            Property statesProp         = pmgrHelper.getProperty(m_definedChooser.getChooserPath() + STATELIST_DATAPATH);
            String[] elementsList       = null ;
            String[] statesList         = null ;        

            if(elementsProp != null) {
                elementsList = elementsProp.getValue().split(LIST_SEPARATOR);
                statesList = statesProp.getValue().split(LIST_SEPARATOR);            
            } else {
                elementsList = getDefaultElementsList();
                statesList   = getDefaultStatesList(elementsList.length);
            }

            for (int i = 0; i < elementsList.length; i++) {
                m_elementsList.add(elementsList[i]);
                m_statesList.add(statesList[i]);
            }
        } catch (SPIException e) {
            CCDebug.trace1("Error initializing chooser settings!", e); 
        }
    }

    private String[] getDefaultElementsList() {
        return getSortedConstraints(m_definedChooser.getConstraints());
    }
    
    private String[] getDefaultStatesList(int size) {
        String[] statesList = new String[size];
        for(int i = 0; i < size; i++) {
            statesList[i] = "false";
        }
        return statesList;
    }
    
    private String[] getSortedConstraints(Vector constraints) {
        String[] constraintsArray = null;
        if (constraints != null) {
            constraintsArray = new String[constraints.size()];
            for(int j=0; j < constraints.size(); j++) {
                TemplatePropertyConstraint constraint = (TemplatePropertyConstraint) constraints.elementAt(j);
                constraintsArray[j] = constraint.getValue();
            }
            Arrays.sort(constraintsArray, String.CASE_INSENSITIVE_ORDER);
        } else {
            constraintsArray = new String[0];
        }
        return constraintsArray;
    }    
    
 
    public void saveChanges() {
        String chooserDataPath = m_definedChooser.getChooserPath();
        PolicyMgrHelper pmgrHelper  = ProfileWindowFramesetViewBean.getProfileHelper();  
        try {
            Property elementsProp = pmgrHelper.getProperty(chooserDataPath);
            Property statesProp = pmgrHelper.getProperty(chooserDataPath + STATELIST_DATAPATH);        
            if (elementsProp == null) {
                elementsProp = pmgrHelper.createProperty(chooserDataPath);
                statesProp = pmgrHelper.createProperty(chooserDataPath + STATELIST_DATAPATH);   
            }
            elementsProp.putString(getElementsStringList());
            statesProp.putString(getStatesStringList());

            pmgrHelper.flushAllChanges();
        } catch (SPIException e) {
            CCDebug.trace1("Error saving changes for chooser!", e); 
        }
    }
    
    public String getElementsStringList() {
        String elementsStringList = "";
        for(int i = 0; i < m_elementsList.size(); i++) {
            elementsStringList = elementsStringList.concat((String)m_elementsList.get(i)).concat(";");  
        }
        return elementsStringList;
    }
 
    public String getStatesStringList() {
        String statesStringList = "";
        for(int i = 0; i < m_statesList.size(); i++) {
            statesStringList = (statesStringList.concat((String)m_statesList.get(i))).concat(";");    
        }
        return statesStringList;        
    }
    
    public ArrayList getElementsList() {
        return m_elementsList;
    }
    
    public ArrayList getStatesList() {
        return m_statesList;
    }
    
    public TemplateProperty getDefiningChooser() {
        return m_definedChooser;
    }
    
    public String getDefiningChooserPath() {
        return m_definedChooserPath;
    }
    
    public String getDefaultElementsStringList() {
        String[] defaults = getDefaultElementsList();
        String elementsStringList = "";
        for(int i = 0; i < defaults.length; i++) {
            elementsStringList = elementsStringList.concat(defaults[i]).concat(";");  
        }
        return elementsStringList;
    }
    
    public void setElementsList(String values, String separator) {
        m_elementsList.clear();
        String[] valuesArr = values.split(separator);
        for(int i = 0; i < valuesArr.length; i++) {
            m_elementsList.add(valuesArr[i]);
        }
    }
    
    public void setStatesList(String states, String separator) {
        m_statesList.clear();
        String[] statesArr = states.split(separator);
        for(int i = 0; i < statesArr.length; i++) {
            m_statesList.add(statesArr[i]);
        }
    }
    
    public void setElementsList(ArrayList values) {
        m_elementsList.clear();
        m_elementsList = values;
    }
    
    public void setStatesList(ArrayList states) {
        m_statesList.clear();
        m_statesList = states;
    }
    

    public void setSystemElements(ArrayList values) {
        for (int i = 0; i < values.size(); i++) {
            String value = (String)values.get(i);
            addToSortedList(value);
        }
        if (m_saveRequired) {
            saveChanges();
            m_saveRequired = false;
        }
    }
    
    
    private void addToSortedList(String value) {
        // Add elements to the sorted list in order - if an element is already 
        // present then break out of loop.
        for(int i = 0; i < m_elementsList.size(); i++) {
            String storedValue = (String)m_elementsList.get(i);
            if(value.compareToIgnoreCase(storedValue) == 0) {
                break;
            } 
            else if(value.compareToIgnoreCase(storedValue) < 0) {
                m_elementsList.add(i, value);
                m_statesList.add(i, "true");
                m_saveRequired = true;
                break;
            } 
            else if(i == (m_elementsList.size() - 1)) {
                m_elementsList.add(value);
                m_statesList.add("true");
                m_saveRequired = true;
                break;
            }
        }
    }    
    
}
