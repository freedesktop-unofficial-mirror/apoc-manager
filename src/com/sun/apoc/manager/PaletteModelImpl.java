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
import java.lang.Integer;

import com.iplanet.jato.RequestParticipant;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.model.DefaultModel;
import com.iplanet.jato.model.ModelControlException;

import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.common.CCI18N;

import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.cfgtree.policynode.PolicyNode;
import com.sun.apoc.spi.cfgtree.property.Property;
import com.sun.apoc.spi.cfgtree.PolicyTree;

import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.manager.settings.ColorChooserView;
import com.sun.apoc.manager.ProfileWindowFramesetViewBean;
import com.sun.apoc.manager.settings.ColorChooserTag;
import com.sun.apoc.manager.settings.PolicyMgrHelper;

/**
 *
 * @author  lb118646
 */
public class PaletteModelImpl extends DefaultModel 
    implements PaletteModel, RequestParticipant {
    
    public Vector m_ColorsHex = new Vector();
    public Vector m_ColorsName = new Vector();
    public int currentRowNumber;
    private CCI18N m_I18n = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
            
    
    private final int NO_TILES_PER_ROW = 8;
    /** Creates a new instance of PaletteModelImpl */
    public PaletteModelImpl() {
        try {
            // Check to see if the custom color names and hex values have been
            // stored in the the backend
            PolicyManager pmgr              = Toolbox2.getPolicyManager();
            PolicyMgrHelper pmgrHelper  = ProfileWindowFramesetViewBean.getProfileHelper();

            Property nameProp = pmgrHelper.getProperty("com.sun.apoc.manager" +
                                PolicyTree.PATH_SEPARATOR +
                                "colorlistName");
            Property hexProp = pmgrHelper.getProperty("com.sun.apoc.manager" +
                                PolicyTree.PATH_SEPARATOR +
                                "colorlistHex");
            String hexColorList = null;
            if (hexProp!=null)
                hexColorList = hexProp.getValue();

            if (hexColorList == null) {
                    setupHexColors();
            } else {
                    String[] hexColorArray = hexColorList.split(";");
                    for (int j = 0; j<hexColorArray.length; j++) {
                        m_ColorsHex.add(hexColorArray[j]);
                    }
                }
                String nameColorList = null;
                if (nameProp!=null)
                    nameColorList = nameProp.getValue();
                if (nameColorList == null) {
                    setupNameColors();
                } else {
                    String[] nameColorArray = nameColorList.split(";");
                    for (int j = 0; j<nameColorArray.length; j++) {
                        m_ColorsName.add(nameColorArray[j]);
                    }
                }
        } catch (SPIException re) {
                    CCDebug.trace1("RegistryException caught: "+re);		
        }
    }
 
    public void setRequestContext(RequestContext requestContext) {       
        try {
            this.setSize(NO_TILES_PER_ROW);
        } catch (ModelControlException mce) {
            CCDebug.trace3("ModelControlException caught: "+mce);
        }
    } 
    
    public void setRow(int rowNumber) {
        currentRowNumber = rowNumber;
    }
    
    public Object getValue(String str){
        int loc = 0;
        try {
            loc = getLocation();
        } catch (ModelControlException mce) {
            CCDebug.trace3("Model Control Exception caught: "+mce);
        }
        // convert the row number and location in the table to a location in the 
        // vectors that can be used
        int stringListLoc = 0;
        if (currentRowNumber > 0){
            stringListLoc = (currentRowNumber*8)+loc;
        } else {
            stringListLoc = loc;
        }
        
        if (str.equals(PRESET_COLOR)) {
            String colorHex = null;
            if (stringListLoc < m_ColorsHex.size()) {
                colorHex = (String) m_ColorsHex.get(stringListLoc);
            }
            if (colorHex == null)
                colorHex = "#ffffff";
            else {
                if (colorHex.equals(""))
                    colorHex = "#ffffff";
            }
            return colorHex;
            
        } else if (str.equals(PRESET_COLOR_NOHASH)) {
            String colorHex = null;
            if (stringListLoc < m_ColorsHex.size()){
               colorHex = (String) m_ColorsHex.get(stringListLoc);
            }
            if (colorHex == null)
                colorHex = "#ffffff";
            else {
                if (colorHex.equals(""))
                    colorHex = "#ffffff";
            }
            String color = colorHex.substring(1);
            return color;
            
        } else if (str.equals(PRESET_COLOR_NUMBER)) {
            return (new Integer(stringListLoc).toString());
            
        } else if (str.equals(IMAGE)) {
            String imageName = null;
            String hasValue = null;
            if (stringListLoc < m_ColorsName.size()) {
                hasValue = (String) m_ColorsName.get(stringListLoc);
                imageName = "/com_sun_web_ui/images/other/dot.gif";
            }
            if ((hasValue == null) || (hasValue.equals("undefined")))
                imageName = "/apoc/images/x.png";
            else {
                if (hasValue.equals(""))
                    imageName = "/apoc/images/x.png";
            }
            return imageName;
            
        } else if (str.equals(PRESET_COLOR_NAME)) {
            String colorName = null;
            if (stringListLoc < m_ColorsName.size())
                colorName = (String) m_ColorsName.get(stringListLoc);
            if ((colorName == null) || (colorName.equals("undefined")))
                colorName = "";
            return colorName;
            
        } else {
            return super.getValue(str);   
            
        }           
    }
    
    public void updateColors(String names, String values) {
        m_ColorsHex.clear();
        m_ColorsName.clear();
        String[] namesArray = names.split(",");
        String[] valuesArray = values.split(",");
        for (int i=0;i<namesArray.length;i++){
            m_ColorsName.add(i, namesArray[i]);
            m_ColorsHex.add(i, valuesArray[i]);
        }        
    }
        
    public void storeColorsInBackend() {
        PolicyMgrHelper pmgrHelper  = ProfileWindowFramesetViewBean.getProfileHelper();  
        try {
            Property nameProp = pmgrHelper.getProperty("com.sun.apoc.manager" +
                                PolicyTree.PATH_SEPARATOR +
                                "colorlistName");
            Property hexProp = pmgrHelper.getProperty("com.sun.apoc.manager" +
                                PolicyTree.PATH_SEPARATOR +
                                "colorlistHex");        
            if (nameProp == null) {
                nameProp = pmgrHelper.createProperty("com.sun.apoc.manager" +
                                PolicyTree.PATH_SEPARATOR +
                                "colorlistName");
                hexProp = pmgrHelper.createProperty("com.sun.apoc.manager" +
                                PolicyTree.PATH_SEPARATOR +
                                "colorlistHex");   
            }

            nameProp.putString(convertVectorToString(m_ColorsName));
            hexProp.putString(convertVectorToString(m_ColorsHex));

            pmgrHelper.flushAllChanges();
        } catch (SPIException e) {
            CCDebug.trace1("Error saving changes for chooser!", e); 
        }
    }
    
    public void setupHexColors(){
        String[] hexColorsArray = ColorChooserTag.hexColors;
        for (int j = 0; j<hexColorsArray.length; j++) {
            m_ColorsHex.add(hexColorsArray[j]);
        }
    }
    
    //Added in corresponding order to how the hex values were added
    //thus indexing not used
    public void setupNameColors(){
        String[] nameColorsArray = ColorChooserTag.colorNames ;
        for (int j = 0; j<nameColorsArray.length; j++) {
            m_ColorsName.add(nameColorsArray[j]);
        }
    }
    
    private String convertVectorToString(Vector vector) {
        String vectorString = "" ;
        for(int j = 0; j < vector.size() ;j++) {
            vectorString = vectorString.concat((String)vector.elementAt(j) + ";") ;
        }	

        return vectorString ;
    }
    
}

