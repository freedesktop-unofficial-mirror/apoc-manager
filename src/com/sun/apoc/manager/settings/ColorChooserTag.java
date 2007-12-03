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

package com.sun.apoc.manager.settings;

import java.net.URLEncoder;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import com.iplanet.jato.util.NonSyncStringBuffer;
import com.iplanet.jato.view.View;

import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.taglib.common.CCDisplayFieldTagBase;

import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.cfgtree.policynode.PolicyNode;
import com.sun.apoc.spi.cfgtree.property.Property;
import com.sun.apoc.spi.cfgtree.PolicyTree;

import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.manager.settings.ColorChooserView;
import com.sun.apoc.manager.settings.SectionModel;
import com.sun.apoc.manager.ProfileWindowFramesetViewBean;




public class ColorChooserTag extends CCDisplayFieldTagBase {
   
    public static final String[] colorNames = {
    "lightsalmon", "salmon", "redlight", "red", "darkred", "redbrown", "redblack", 
    "black",
    "lightlemon", "lemon", "yellowlight", "yellow", "darkyellow", "yellowbrown", "yellowblack",
    "nearlyblack",
    "lightlime", "lime", "greenlight", "green", "darkgreen", "greenbrown", "greenblack",
    "lightestblack",
    "lightbabyblue", "babyblue", "brightbluelight", "brightblue",  "darkbrightblue", "brightbluebrown", "brightblueblack", 
    "darkgrey",
    "lightpurpleblue", "purpleblue", "bluelight", "blue", "darkblue", "bluebrown", "blueblack",
    "lightgrey",
    "lightbabypink", "babypink", "pinklight", "pink", "darkpink", "pinkbrown", "pinkblack",
    "white"};
    public static final String[] hexColors = { 
    "#ffc0c0", "#ff8080", "#ff4040", "#ff0000", "#c00000", "#800000", "#400000", "#000000", 
    "#ffffc0", "#ffff80", "#ffff40", "#ffff00", "#c0c000", "#808000", "#404000", "#202020", 
    "#c0ffc0", "#80ff80", "#40ff40", "#00ff00", "#00c000", "#008000", "#004000", "#404040",
    "#c0ffff", "#80ffff", "#40ffff", "#00ffff", "#00c0c0", "#008080", "#004040", "#808080",
    "#c0c0ff", "#8080ff", "#4040ff", "#0000ff", "#0000c0", "#000080", "#000040", "#c0c0c0",
    "#ffc0ff", "#ff80ff", "#ff40ff", "#ff00ff", "#c000c0", "#800080", "#400040", "#ffffff"};

    private boolean m_isValueSet = true;
    
    public ColorChooserTag() {
        super();
    }
    
    protected String getHTMLStringInternal(Tag parent, PageContext pageContext,
            View view) throws JspException {
        if (parent == null) {
            throw new IllegalArgumentException("parent cannot be null.");
        } else if (pageContext == null) {
            throw new IllegalArgumentException("pageContext cannot be null.");
        } else if (view == null) {
            throw new IllegalArgumentException("view cannot be null.");
        }

        checkChildType(view, ColorChooserView.class);
        ColorChooserView field = (ColorChooserView) view;
        CCI18N i18N = new CCI18N(pageContext, "apocBundle");
        String notSet = SectionModel.getUndefinedValue();
                
        setParent(parent);
        setPageContext(pageContext);
        // get value
        String value = (String) field.getValue();
   
        NonSyncStringBuffer buffer = 
            new NonSyncStringBuffer(DEFAULT_BUFFER_SIZE);    
        
        String forId = field.getQualifiedName();
        String qualNameEncoded = URLEncoder.encode(forId);
        
        // color chooser area
        buffer.append("<table title=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
        buffer.append("<tr>\r\n");
        // editable textfield for normal use
        if (field.isEnabled()) {
            StringBuffer buffer2 = new StringBuffer();
            buffer2.append("javascript: var qualName='")
                    .append(qualNameEncoded)
                    .append("'; ")
                    .append("setTimeout('updateSaveStatus(this.name), 0')")
                    .append("; setTimeout(toggleOverwriteCheckbox(this), 0);");

            
            buffer.append("<td valign=\"top\" style=\"border:none;padding:0px\">");
            buffer.append("<input name=\"")
                    .append(field.getQualifiedName()).append("\"");
            buffer.append(" type=\"text\" value=\"").append(value).append("\"");
            buffer.append(" size=\"15\" onChange=\"")
                    .append(buffer2.toString())
                    .append(" checkValidHexValue(this.name, qualName);\" ")
                    .append("onKeyPress=\"")
                    .append(buffer2.toString())
                    .append("\" id=\"newColor")
                    .append(qualNameEncoded).append("\" />");
        } else {
        // readonly gets a text field
            buffer.append("<td valign=\"bottom\" style=\"border:none;padding:0px\">");
            buffer.append(value);
        }
        buffer.append("</td>\r\n");
        // Space between value and color square
        buffer.append("<td valign=\"top\" style=\"border:none;padding:0px\">");
        buffer.append("<img src=\"/com_sun_web_ui/images/other/dot.gif\" alt=\"\" border=\"0\" height=\"1\" width=\"10\"/>");
        buffer.append("</td>\r\n");
        // color square
        buffer.append("<td valign=\"top\" style=\"border:none;padding:0px;background-color:")
                .append(value)
                .append(";\" ");
        buffer.append("id=\"colorsquare")
                .append(qualNameEncoded);
        buffer.append("\" height=\"20\" width=\"29\" border=\"0\">");
        buffer.append("<img src=\"/com_sun_web_ui/images/other/dot.gif\" alt=\"\" border=\"0\" height=\"20\" width=\"29\"/>");
        buffer.append("</td>\r\n"); 
        //Space between color square and drop down menu
        buffer.append("<td valign=\"top\" style=\"border:none;padding:0px\">");
        buffer.append("<img src=\"/com_sun_web_ui/images/other/dot.gif\" alt=\"\" border=\"0\" height=\"1\" width=\"10\"/>");
        buffer.append("</td>\r\n");
        
        if (field.isEnabled()) {
            // Drop down name menu
            buffer.append("<td valign=\"top\" style=\"border:none;padding:0px\">");
            buffer.append("<select name=\"")
                .append(field.getQualifiedName().substring(0, field.getQualifiedName().lastIndexOf(".")).concat(".ColorNameDropDown"))
                .append("\" ");
            buffer.append("id=\"colorNameDropDown")
                    .append(qualNameEncoded).append("\"");
            buffer.append(" onChange=\"javascript:setFromDropDown('")
                    .append(qualNameEncoded).append("');\">\n");
            
            // Check to see if names are stored in the backend
            try {
                // Get the Hex values as well so that the current value can be
                // found in the hex values list and displayed as the selected
                // name
                String nameColorList        = null;
                // nameColorArray is of length +2 beacuse need to add the 'not set' 
                // option and the 'no name' option
                String[] nameColorArray     = new String[colorNames.length+2];
                String hexColorList         = null;
                String[] hexColorArray      = null;
                String localizedNoName      = i18N.getMessage("APOC.setcolor.noname") ;       
                PolicyManager pmgr              = Toolbox2.getPolicyManager();
                PolicyMgrHelper pmgrHelper  = ProfileWindowFramesetViewBean.getProfileHelper();
                
                Property nameProp   = pmgrHelper.getProperty("com.sun.apoc.manager" +
                                      PolicyTree.PATH_SEPARATOR +
                                      "colorlistName");
                Property hexProp    =  pmgrHelper.getProperty("com.sun.apoc.manager" +
                                      PolicyTree.PATH_SEPARATOR +
                                      "colorlistHex");
                if (nameProp != null) {
                    nameColorList           = nameProp.getValue();
                    nameColorList           = notSet + ";" + localizedNoName + ";" + nameColorList;
                    nameColorArray          = nameColorList.split(";");
                } else {
                    nameColorArray[0]       = notSet ;
                    nameColorArray[1]       = localizedNoName ;
                    System.arraycopy(colorNames, 0, nameColorArray, 2, colorNames.length);
                }
                if (hexProp != null) {
                    hexColorList            = hexProp.getValue();
                    hexColorArray           = hexColorList.split(";");
                } else {
                    hexColorArray          = hexColors;
                }
                 boolean selectedHasName = false;
                // To sort put them into a hashmap so that the names relate to the
                // hex values #b5056344
                HashMap nameHex = new HashMap(nameColorArray.length);
                nameHex.put((String)nameColorArray[0], (String)nameColorArray[0]);
                nameHex.put((String)nameColorArray[1], "");
                for (int i = 2; i<nameColorArray.length; i++) {
                    nameHex.put((String)nameColorArray[i], (String)hexColorArray[i-2]);
                }

                Arrays.sort(nameColorArray, 2, nameColorArray.length);
                StringBuffer optionsBuffer = new StringBuffer();
                if(m_isValueSet) {
                    for (int j = 2; j<nameColorArray.length; j++) {
                        String lowercasehex = null;
                        String name = nameColorArray[j];

                        lowercasehex = ((String)nameHex.get(name)).toLowerCase();

                        // need to get the location in the hex array where the current
                        // value is stored and set that location to be selected in the 
                        // names drop down
                        if (!name.equals("") && name != null) {
                            optionsBuffer.append("<option value=\"");
                            if (lowercasehex.equals(value.toLowerCase())) {
                                optionsBuffer.append(lowercasehex)
                                        .append("\" selected >");
                                selectedHasName = true;
                            } else {
                                optionsBuffer.append(lowercasehex)
                                        .append("\">");
                            }
                            optionsBuffer.append(name);
                            optionsBuffer.append("</option>\n");
                       }
                    }
                    StringBuffer tempBuffer = new StringBuffer();
                    tempBuffer.append("<option value=\"")
                                .append(nameColorArray[0])
                                .append("\">") ;
                    tempBuffer.append(nameColorArray[0]);
                    tempBuffer.append("</option>\n");
                    tempBuffer.append("<option value=\"");
                    if (selectedHasName) {
                        tempBuffer.append("\">") ;
                    } else {
                        tempBuffer.append("\" selected >");                                  
                    }
                    tempBuffer.append(nameColorArray[1]);
                    tempBuffer.append("</option>\n");
                    optionsBuffer.insert(0, tempBuffer.toString());
                } else {
                    optionsBuffer.append("<option value=\"")
                                .append(nameColorArray[0])
                                .append("\" selected >") ;
                    optionsBuffer.append(nameColorArray[0]);
                    optionsBuffer.append("</option>\n");
                    optionsBuffer.append("<option value=\"")
                                .append("\">") ;
                    optionsBuffer.append(nameColorArray[1]);
                    optionsBuffer.append("</option>\n");
                    for (int j = 2; j<nameColorArray.length; j++) {
                        String lowercasehex = null;
                        String name = nameColorArray[j];

                        lowercasehex = ((String)nameHex.get(name)).toLowerCase();

                        // need to get the location in the hex array where the current
                        // value is stored and set that location to be selected in the 
                        // names drop down
                        if (!name.equals("") && name != null) {
                            optionsBuffer.append("<option value=\"");
                                optionsBuffer.append(lowercasehex)
                                        .append("\">");
                        }
                        optionsBuffer.append(name);
                        optionsBuffer.append("</option>\n");
                    }
                }
                buffer.append(optionsBuffer.toString());
            } catch (SPIException re) {
                CCDebug.trace1("RegistryException caught: "+re);		
            }
            buffer.append("</select>");
            buffer.append("</td>\r\n");
            
            // original value field
            buffer.append("<input name=\"")
                .append(field.getQualifiedName().substring(0, field.getQualifiedName().lastIndexOf(".")).concat(".OriginalValue"))
                .append("\" type=\"hidden\"")
                .append("value=\"")
                .append(value)
                .append("\" />");
            
            // Space between drop down menu and Edit button
            buffer.append("<td valign=\"top\" style=\"border:none;padding:0px\">");
            buffer.append("<img src=\"/com_sun_web_ui/images/other/dot.gif\" alt=\"\" border=\"0\" height=\"1\" width=\"10\"/>");
            buffer.append("</td>\r\n");
            // As passing the initial value to display there needs to be some 
            // shown thus setting the empty string value to white
            if (value.equals("")){
                value = "#ffffff";
            }
            if (value != null)
                value = value.substring(1);

            
            // edit button
            buffer.append("<td align=\"center\" valign=\"top\" style=\"border:none;padding:0px\">\r\n");
            buffer.append("");
            buffer.append("<input name=\"")
                .append(field.getQualifiedName().substring(0, field.getQualifiedName().lastIndexOf(".")).concat(".EditButton"))
                .append("\" type=\"submit\"")
                .append("title=\"")
                .append(i18N.getMessage("APOC.setcolor.edit.alt"))
                .append("\" class=\"Btn2\" value=\"");
            buffer.append(i18N.getMessage("APOC.policies.edit.button"));
            buffer.append("\" onclick=\"var color = document.getElementById('newColor")
                .append(qualNameEncoded).append("').value;")
                .append("if(color[0]=='#') {color = color.substring(1)};color = encodeURI(color);\n")
                .append("var selIndex = document.getElementById('colorNameDropDown")
                .append(qualNameEncoded).append("').selectedIndex;\n")
                .append("var name = document.getElementById('colorNameDropDown")
                .append(qualNameEncoded).append("').options[selIndex].text; if((name == '")
                .append(i18N.getMessage("APOC.setcolor.noname"))
                .append("') || (name == '")
                .append(notSet)
                .append("')) { name ='';};\n")
                .append("parent.parent.parent.opener.EditColorWindow = window.open(")
                .append("'../manager/EditColor?OrgColor='+color+")
                .append("'&Prop=").append(qualNameEncoded)
                .append("&Name='+name,");
            buffer.append("'editColorWindow', 'height=560,width=580,top='+((screen.height-(screen.height/1.618))-(500/2))+',left='+((screen.width-750)/2)+',scrollbars=no,resizable=yes');");
            buffer.append("parent.parent.parent.opener.EditColorWindow.focus();");
            buffer.append("setColorChooserName('")
                .append(field.getQualifiedName())
                .append("');return false;\"");
            buffer.append(" onmouseover=\"javascript:if (this.disabled==0) this.className='Btn2Hov';window.status=this.title;return true;\"")
                .append("onmouseout=\"javascript:if (this.disabled==0) this.className='Btn2';window.status='';return true;\"")
                .append("onblur=\"javascript: if (this.disabled==0) this.className='Btn2'\"")
                .append("onfocus=\"javascript: if (this.disabled==0) this.className='Btn2Hov'\"/>\r\n");
            buffer.append("");
            buffer.append("</td>");

        }
        buffer.append("</tr>");
        buffer.append("</table>");
        
        return buffer.toString();
   }
 
   public void setValueSet(boolean isValueSet) {
       m_isValueSet = isValueSet;
   }

}
