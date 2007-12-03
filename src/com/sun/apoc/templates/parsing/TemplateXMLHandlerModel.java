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
import com.sun.apoc.manager.ChooserHelper;
import com.sun.web.ui.common.CCDebug;
import com.sun.apoc.manager.ProfileWindowFramesetViewBean;
import com.sun.apoc.manager.settings.PolicyMgrHelper;
import com.sun.apoc.manager.settings.SheetModel;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.cfgtree.property.Property;
import java.util.ArrayList;
import java.util.regex.*;
public class TemplateXMLHandlerModel {
       
    public static final String CONDITION_TYPE = "condition" ;
    public static final String COMMAND_TYPE = "command" ;
    
    private PolicyMgrHelper m_policyMgrHelper = null;
    private SheetModel m_sheetModel = null;
    
    private static final String ENABLED_STRING = ".enabled" ;
    private static final String VALUE_STRING = ".value" ;
    private static final String DISABLED_STRING = ".disabled" ;
    
   
    public TemplateXMLHandlerModel(SheetModel sheetModel) {
        m_sheetModel = sheetModel;
        m_policyMgrHelper = ProfileWindowFramesetViewBean.getProfileHelper();
    }
    
    public String getJavaScript(String str, String type) {
        
        if (type.equals(CONDITION_TYPE)) {
            // XSLT operators used in the templates must be substituted in a condition statement 
            // for the appropriate JavaScript operators
            str = replaceOperators(str) ;
        }
        String processedString = processExpression(str, VALUE_STRING ) ;
        processedString = processExpression(processedString, ENABLED_STRING) ;
        return processedString ;
    }
        
    protected String processExpression(String str, String token) {
        String processedString = str ;
        // Tokenize the string and identify the components
        String[] tokenizedString = str.split(token) ;
        for(int i = 0; i < tokenizedString.length - 1; i++) {
            String propString = extractProperty(tokenizedString[i]) ;
            String opString = extractOperator(tokenizedString[i+1]) ;
            // This tokenized string starts with either the opString or whitepsace followed
            // by the opString. So split the string on the opString with
            // a limit of 2 (i.e split it once in case the valueString contains
            // the opString for some reason) and take the second element
            String[] tokenizedSubString = tokenizedString[i+1].split(opString, 2) ;
            String valueString = extractValue(tokenizedSubString[1]) ; 
            
            // Convert the property name to its html name and replace 
            // all instances of this name in the string            
            String replacementString = generateJavaScript(propString, opString, valueString, token) ;
            if (!(replacementString.equals(""))) {
                processedString = processedString.replaceAll("\\Q"
                                                                + propString 
                                                                + token
                                                                + "\\E"
                                                                + "\\s*" 
                                                                + opString 
                                                                + "\\s*" 
                                                                + "\\Q"
                                                                + valueString 
                                                                + "\\E",
                                                                replacementString) ;
            }
        } 
        return processedString ;
    }
    
    protected String replaceOperators(String str) {
        // Note that the order of the operators in the below arrays
        // is important since "=" must be replaced before "!=" in the 
        // replacement loop.
        String[] xsltOperators = {"\\sand\\s", "\\sor\\s", "=", "!=", "not\\("} ;
        String[] jsOperators = {" && ", " || ", "==", "!", "!("} ;
        
        // Must be careful not to replace operators that appear 
        // within single quotes - i.e. a value string 
        String[] origStrArr = str.split("'") ;
        String[] strArr = str.split("'") ;
      
        for(int i = 0; i < strArr.length; i++) {
            for(int j = 0; j < xsltOperators.length; j++) {
                Pattern pattern = Pattern.compile(xsltOperators[j]);
                Matcher matcher = pattern.matcher(strArr[i]);
                strArr[i] = matcher.replaceAll(jsOperators[j]) ;
            }
            str = str.replaceAll("\\Q" + origStrArr[i] +  "\\E", strArr[i]) ;
            // Since the string is split on single quotes then odd
            // numbered elements of strArr must be values within quotes
            // and operators inside them should not be replaced
            i++ ; 
        }
        return str ;
    }
    
    protected String extractProperty(String str) {
        String[] strArray  = str.split("[^a-zA-Z0-9_.:-]") ;
        str = strArray[(strArray.length -1)] ;
        return str ;
    }
    
    protected String extractOperator(String str) {
        String[] operatorsArray2 = {"==", "!=", "<=", ">=", "()", "&&", "||"} ;
        String[] operatorsArray1 = {"=", "<", ">", "!"} ;
        String op = str ;
        int index = -1 ;

        // Check for 2 character operators
        for(int i = 0; i < operatorsArray2.length; i++) {
            index = str.indexOf(operatorsArray2[i]) ;
            if (index != -1) {
                op = operatorsArray2[i] ;
                break ;
            }
        }
        //Check for 1 character operators
        if (index == -1) {
            for(int i = 0; i < operatorsArray1.length; i++) {
                index = str.indexOf(operatorsArray1[i]) ;
                if (index != -1) {
                    op = operatorsArray1[i] ;
                    break ;
                }
            }
        }
        return op ;
    }
    
    protected String extractValue(String str) {
        // Remove leading whitespace
        String[] strArray  = str.split("\\s") ;
        int index = 0 ;
        for(int i=0; strArray[i].length() == 0; i++) {
            index = i+1 ;
        }
        str = str.substring(index) ;

        // Allow any chars within quotes
        if (str.charAt(0) =='\'') {
            str = str.substring(0, str.indexOf('\'', 1) + 1) ;
        } else {
            // Leading whitespace is removed and quotes are not present 
            // therefore the first character of the value
            // string should be at index 0. Therefore split on the not-allowed 
            // characters and return the first element in the array
            try{
                strArray  = str.split("[^a-zA-Z0-9_.:-]") ;
                str = strArray[0] ;
            } catch (PatternSyntaxException pse) {
                CCDebug.trace3(pse.getMessage()) ;
            }
        }
        
        return str ;
        
    } 
    
    private String generateJavaScript(String propName, String operator, String value, String qualifier) {
        String start                =   "document.Form['PolicySettingsContent.PolicyTemplateContent." ;
        String middle               =   ".PoliciesSectionTiledView[" ;
        String end                  =   "].PropertyValue']" ;
        TemplatePage page           =   m_sheetModel.getPage() ;
        String javascript           =   "" ;
        
        // Find the index of the required property in the section and create the html name 
        // which it will have
        TemplateSection section = null ;
        TemplateProperty prop = null ;
        int index = 0; 
        boolean sectionFound = false ;
        for(int i = 0; (i < page.getSections().size()) && (!sectionFound); i++) {
            for(int j = 0; (page.getSection(i).getProperties() != null) && (j < page.getSection(i).getProperties().size()); j++) {
                if (page.getSection(i).getProperty(j).getDefaultName().equals(propName)) {
                    section = page.getSection(i) ;
                    index = j ;
                    prop = page.getSection(i).getProperty(j) ;
                    sectionFound = true ;                    
                    break ;
                }
            }
        } 

        String indexString = (new Integer(index)).toString() ;
        


        // Remove quotes from the valueString since only textfields and stringLists require them
        // Add quotes for these visualTypes when needed
        if ((value.charAt(0) == '\'') && (value.charAt(value.length() - 1)=='\'')) {
            value = value.substring(1, value.length()-1) ;
        }
        
        // Convert the enabled string to .disabled and reverse logical 
        // operators for use in javascript funcs
        if (qualifier.equals(ENABLED_STRING)) {
            qualifier = DISABLED_STRING ;
            if (operator.equals("!=")) {
                operator = "==" ;
            }
            else if (operator.equals("==")) {
                operator =  "!=" ;
            }
            else {
                operator = "=!" ;
            }   
        }
        
        
        if (prop != null) {
            Property node = null ;
            try {
                node = m_policyMgrHelper.getProperty(prop.getDataPath());
            } catch (SPIException ex) {
                CCDebug.trace3("Error getting property node for handler property");
            }
            

            // Deal with read-only nodes
            // If the node is read-only then the *.PropertyValue element is not present. 
            // So need to check if the stored value satisfies the condition and return "true" 
            // if that's the case, otherwise "false" can be returned otherwise.
            if ((node != null) && (node.isReadOnly())) {
                javascript = generateJSForReadOnly(prop, propName, operator, value, qualifier,
                                            start, section.getDefaultName(), middle,
                                            indexString, end, node);
            
            // Deal with non-read-only nodes.
            // Different types visualTypes require different handling
            } else if (((node != null) && (!(node.isReadOnly()))) || (node == null)) {
                if (prop.getVisualType().equals(TemplateProperty.CHECKBOX)){
                    javascript = generateJSForCheckbox(prop, propName, operator, value, qualifier,
                                                        start, section.getDefaultName(), middle,
                                                        indexString, end);
                        
                } else if (prop.getVisualType().equals(TemplateProperty.RADIOBUTTONS)) {
                    javascript = generateJSForRadioButtons(prop, propName, operator, value, qualifier,
                                                            start, section.getDefaultName(), middle,
                                                            indexString, end);
            
                } else if (prop.getVisualType().equals(TemplateProperty.COMBOBOX)) {
                    javascript = generateJSForCombobox(prop, propName, operator, value, qualifier,
                                                        start, section.getDefaultName(), middle,
                                                        indexString, end);
                
                } else if (prop.getVisualType().equals(TemplateProperty.STRING_LIST)) {
                    javascript = generateJSForStringList(prop, propName, operator, value, qualifier,
                                                            start, section.getDefaultName(), middle,
                                                            indexString, end);
            
                } else if (prop.getVisualType().equals(TemplateProperty.COLOR_CHOOSER)) {
                    javascript = generateJSForColorChooser(prop, propName, operator, value, qualifier,
                                                            start, section.getDefaultName(), middle,
                                                            indexString, end);
                    
                } else if (prop.getVisualType().equals(TemplateProperty.CHOOSER)) {
                    javascript = generateJSForChooser(prop, propName, operator, value, qualifier,
                                                            start, section.getDefaultName(), middle,
                                                            indexString, end);
                    
                } else if ((prop.getVisualType().equals(TemplateProperty.TEXTFIELD))
                                || (prop.getVisualType().equals(TemplateProperty.TEXTFIELD))) {
                    javascript = generateJSForTextfield(prop, propName, operator, value, qualifier,
                                                            start, section.getDefaultName(), middle,
                                                            indexString, end);
                }
                
                if (javascript.equals("")) {
                    javascript = generateJSGeneric(prop, propName, operator, value, qualifier,
                                                            start, section.getDefaultName(), middle,
                                                            indexString, end);                    
                }
            }
        }
        return javascript ;
    }           
    
    private String generateJSForReadOnly(TemplateProperty prop, String propName, String operator, 
                                            String value, String qualifier, String start, String section, 
                                            String middle, String indexString, String end, Property node) {

        String js = "" ;
        if (qualifier.equals(VALUE_STRING)) {
            String storedValue = null ;
            try {
                storedValue = node.getValue();
            } catch (SPIException spie) {
                CCDebug.trace3(spie.toString());
            }
            if (storedValue == null) {
                storedValue = prop.getDefaultValue() ;
            }
            
            if ((operator.equals("==")) && (storedValue.equals(value))) {
                js = "true" ;
            } else if ((operator.equals("!=")) && !(storedValue.equals(value))) {
                js = "true" ;
            } else {
                js = "false" ; 
            }

        } else if (qualifier.equals(DISABLED_STRING)) {
            // A string-list must be specially handled because a read-only string-list
            // is displayed as a scrollable list just without the add/remove capability.
            // So disabling a string list requires setting it to .disabled which greys it 
            // out and disallows scrolling
            if (prop.getVisualType().equals(TemplateProperty.STRING_LIST)) {
                StringBuffer b = new StringBuffer();
                start = "document.Form['ListPolicySettingsContent.PolicyTemplateContent." ;
                b.append(start)
                    .append(section)
                    .append(middle)
                    .append(indexString)
                    .append(end)
                    .append(qualifier)
                    .append(operator)
                    .append(value) ;
                js = b.toString() ;
            } else {
                js = "false";
            }
        }        
        return js ;
    }
    
    private String generateJSForCheckbox(TemplateProperty prop, String propName, String operator, 
                                            String value, String qualifier, String start, String section, 
                                            String middle, String indexString, String end) {
        String js = "" ;
        if (qualifier.equals(VALUE_STRING)) {
   /*         qualifier = ".checked" ;
            StringBuffer b = new StringBuffer();
            b.append(start)
                .append(section)
                .append(middle)
                .append(indexString)
                .append(end)
                .append(qualifier)
                .append(operator)
                .append(value) ;
            js = b.toString() ;*/
            String selectedIndex = "1";
            if (value.equals("false")) {
                selectedIndex = "2";
            }
            StringBuffer b = new StringBuffer();
            b.append(start)
                .append(section)
                .append(middle)
                .append(indexString)
                .append(end)
                .append(".options[") 
                .append(selectedIndex)
                .append("]")
                .append(".selected")
                .append(operator)
                .append("true") ;
            js = b.toString() ;            
        }  
        return js ;
    }
    
    private String generateJSForRadioButtons(TemplateProperty prop, String propName, String operator, 
                                            String value, String qualifier, String start, String section, 
                                            String middle, String indexString, String end) {

        String js = "" ;
        if (qualifier.equals(VALUE_STRING)) {
            qualifier = ".checked" ;
            String selectedIndex = null; 
            if (prop.getConstraints() != null) {
                for(int i=0; i < prop.getConstraints().size(); i++) {
                    TemplatePropertyConstraint constraint = 
                            (TemplatePropertyConstraint) prop.getConstraints().elementAt(i);
                    String constraintValue = constraint.getValue();
                    if (value.equals(constraintValue)) {
                        // + 1 below because --Not Set-- is always the first radioButton
                        selectedIndex = (new Integer(i+1)).toString() ;
                        break ;
                    }
                }
            }
            StringBuffer b = new StringBuffer();
            StringBuffer radio = new StringBuffer() ;
            radio.append(start)
                .append(section)
                .append(middle)
                .append(indexString)
                .append(end) ;
            b.append(radio.toString())
                .append("[")
                .append(selectedIndex)
                .append("]")
                .append(qualifier)
                .append(operator)
                .append("true") ;
            js = b.toString() ;

        } else if (qualifier.equals(DISABLED_STRING)) {
            StringBuffer b = new StringBuffer();
            StringBuffer radio = new StringBuffer() ;
            radio.append(start)
                    .append(section)
                    .append(middle)
                    .append(indexString)
                    .append(end) ;
            for(int i = 0; i < prop.getConstraints().size(); i++) { //prop.getConstraints().size() -1 
                b.append(radio.toString())
                    .append("[")
                    .append((new Integer(i).toString()))
                    .append("]")
                    .append(qualifier)
                    .append("=") ;
            }
            b.append(radio.toString())
                .append("[")
                .append(new Integer((prop.getConstraints().size() )).toString()) //prop.getConstraints().size()-1
                .append("]")
                .append(qualifier)
                .append(operator)
                .append(value) ;
            js = b.toString() ;
        }
        
        return js ;
    }
    
    private String generateJSForCombobox(TemplateProperty prop, String propName, String operator, 
                                            String value, String qualifier, String start, String section, 
                                            String middle, String indexString, String end) {

        String js = "" ;
        if (qualifier.equals(VALUE_STRING)) {
            String selectedIndex = null; 
            if (prop.getConstraints() != null) {
                for(int i=0; i < prop.getConstraints().size(); i++) {
                    TemplatePropertyConstraint constraint = 
                            (TemplatePropertyConstraint) prop.getConstraints().elementAt(i);
                    String constraintValue = constraint.getValue();
                    if (value.equals(constraintValue)) {
                        // + 1 below because --Not Set-- is always the first radioButton
                        selectedIndex = (new Integer(i + 1)).toString() ;
                        break ;
                    }
                }
            }
            StringBuffer b = new StringBuffer();
            b.append(start)
                .append(section)
                .append(middle)
                .append(indexString)
                .append(end)
                .append(".options[")
                .append(selectedIndex)
                .append("].selected")
                .append(operator)
                .append("true") ;
            js = b.toString() ;
        } 
        return js ;
    }

    private String generateJSForStringList(TemplateProperty prop, String propName, String operator, 
                                            String value, String qualifier, String start, String section, 
                                            String middle, String indexString, String end) {
                                                
                                                
        String js = "" ;
        if (qualifier.equals(VALUE_STRING)) {
            start = "document.Form['PolicySettingsContent.PolicyTemplateContent." ;
            StringBuffer b = new StringBuffer();
            if (operator.equals("=")) {
                b.append("document.Form['ListPolicySettingsContent.PolicyTemplateContent.") 
                    .append(section)
                    .append(middle)
                    .append(indexString)
                    .append(end)
                    .append(".options.length = 0; ")
                    .append("displayListItems('PolicySettingsContent.PolicyTemplateContent.") 
                    .append(section)
                    .append(middle)
                    .append(indexString)
                    .append("].PropertyValue', '")
                    .append(value)
                    .append("')") ;
            
            } else {
                b.append(start)
                    .append(section)
                    .append(middle)
                    .append(indexString)
                    .append(end)
                    .append(qualifier)
                    .append(operator)
                    .append(value) ;
            }
            js = b.toString() ;
        
        } else if (qualifier.equals(DISABLED_STRING)) {
            start = "document.Form['ListPolicySettingsContent.PolicyTemplateContent." ;
            StringBuffer b = new StringBuffer();
            b.append(start)
                .append(section)
                .append(middle)
                .append(indexString)
                .append(end)
                .append(qualifier)
                .append(operator)
                .append(value)
                .append("; document.getElementById('PolicySettingsContent.PolicyTemplateContent.")
                .append(section)
                .append(middle)
                .append(indexString)
                .append("].PropertyValue.AddButton')" ) 
                .append(qualifier)
                .append(operator)
                .append(value)
                .append("; document.getElementById('PolicySettingsContent.PolicyTemplateContent.") 
                .append(section)
                .append(middle)
                .append(indexString)
                .append("].PropertyValue.RemoveButton')" ) 
                .append(qualifier)
                .append(operator)
                .append(value) ;
            js = b.toString() ;
        }       
        return js ;
    }    

    private String generateJSForColorChooser(TemplateProperty prop, String propName, String operator, 
                                            String value, String qualifier, String start, String section, 
                                            String middle, String indexString, String end) {

        String js = "" ;
        if (qualifier.equals(DISABLED_STRING)) {
            StringBuffer b = new StringBuffer();
            b.append(start)
                .append(section)
                .append(middle)
                .append(indexString)
                .append(end)
                .append(qualifier)
                .append("=")
                .append(start)
                .append(section)
                .append(middle)
                .append(indexString)
                .append("].ColorNameDropDown']")
                .append(qualifier)
                .append("=") 
                .append(start)
                .append(section)
                .append(middle)
                .append(indexString)
                .append("].EditButton']") 
                .append(qualifier)
                .append(operator)
                .append(value) ;
            js = b.toString() ;
        }    
        return js ;
    } 
    
    private String generateJSForChooser(TemplateProperty prop, String propName, String operator, 
                                            String value, String qualifier, String start, String section, 
                                            String middle, String indexString, String end) {
                                                
                                                
        String js = "" ;
        if (qualifier.equals(VALUE_STRING)) {
            String extendsChooser = prop.getExtendsChooser() ;
            String chooserDataPath = null ;
            String chooserPath = null ;
            TemplateProperty linkedProp = null ;
            ChooserHelper helper = new ChooserHelper(prop.getDataPath());
            ArrayList constraintsList =  helper.getElementsList();

            String selectedIndex = null; 
            for(int i=0; i < constraintsList.size(); i++) {
                String constraintValue = (String) constraintsList.get(i);
                if (value.equals(constraintValue)) {
                    // + 1 below because --Not Set-- is always the first radioButton
                    selectedIndex = (new Integer(i + 1)).toString() ;
                    break ;
                }
            }
                      
            StringBuffer b = new StringBuffer();
            b.append(start)
                .append(section)
                .append(middle)
                .append(indexString)
                .append(end)
                .append(".options[")
                .append(selectedIndex)
                .append("].selected")
                .append(operator)
                .append("true") ;
            js = b.toString() ;

        } else if (qualifier.equals(DISABLED_STRING)) {
            StringBuffer b = new StringBuffer();
            b.append(start)
                .append(section)
                .append(middle)
                .append(indexString)
                .append(end)
                .append(qualifier)
                .append("=")
                .append(start)
                .append(section)
                .append(middle)
                .append(indexString)
                .append("].EditButton']")
                .append(qualifier)
                .append(operator)
                .append(value) ;
           js = b.toString() ;
        }
        return js ;
    } 
    

    private String generateJSForTextfield(TemplateProperty prop, String propName, String operator, 
                                            String value, String qualifier, String start, String section, 
                                            String middle, String indexString, String end) {
    
        String js = "" ;
        // Add quotes around the value because we are dealing with a textfield
        if (qualifier.equals(VALUE_STRING)) { 
            StringBuffer b = new StringBuffer();
            b.append(start)
                .append(section)
                .append(middle)
                .append(indexString)
                .append(end)
                .append(qualifier)
                .append(operator)
                .append("'")
                .append(value)
                .append("'") ;
            js = b.toString() ;
        } 
        return js ;
    }    
    
    
    
    
    private String generateJSGeneric(TemplateProperty prop, String propName, String operator, 
                                            String value, String qualifier, String start, String section, 
                                            String middle, String indexString, String end) {
    
        String js = "" ;
        StringBuffer b = new StringBuffer();
        b.append(start)
            .append(section)
            .append(middle)
            .append(indexString)
            .append(end)
            .append(qualifier)
            .append(operator)
            .append(value) ;
        js = b.toString() ;
        return js ;
    }
}
