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

import com.iplanet.jato.util.NonSyncStringBuffer;
import com.iplanet.jato.view.View;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.taglib.common.CCDisplayFieldTagBase;
import java.net.URLEncoder;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

public class SpinPropertyTag extends CCDisplayFieldTagBase {
    
    protected static final String ATTRIB_UPTITLE    = "upTitle";
    protected static final String ATTRIB_DOWNTITLE  = "downTitle";
    protected static final String ATTRIB_UPACTION   = "upAction";
    protected static final String ATTRIB_DOWNACTION = "downAction";
    protected static final String ATTRIB_SIZE       = "size";

    public SpinPropertyTag() {
        super();
    }
    
    public void reset() {
	    super.reset();
    }

    public String getUpTitle() {
        return (String) getValue(ATTRIB_UPTITLE);
    }
    
    public void setUpTitle(String value) {
        setValue(ATTRIB_UPTITLE, value);
    }

    public String getDownTitle() {
        return (String) getValue(ATTRIB_DOWNTITLE);
    }
    
    public void setDownTitle(String value) {
        setValue(ATTRIB_DOWNTITLE, value);
    }

    public String getUpAction() {
        return (String) getValue(ATTRIB_UPACTION);
    }
    
    public void setUpAction(String value) {
        setValue(ATTRIB_UPACTION, value);
    }

    public String getDownAction() {
        return (String) getValue(ATTRIB_DOWNACTION);
    }
    
    public void setDownAction(String value) {
        setValue(ATTRIB_DOWNACTION, value);
    }

    public String getSize() {
        return (String) getValue(ATTRIB_SIZE);
    }
    
    public void setSize(String value) {
        setValue(ATTRIB_SIZE, value);
    }

    protected String getHTMLStringInternal(Tag parent, PageContext pageContext, View view) throws JspException {
        if (parent == null) {
            throw new IllegalArgumentException("parent cannot be null.");
        } else if (pageContext == null) {
            throw new IllegalArgumentException("pageContext cannot be null.");
        } else if (view == null) {
            throw new IllegalArgumentException("view cannot be null.");
        }

        checkChildType(view, SpinProperty.class);
        setParent(parent);
        setPageContext(pageContext);

        SpinProperty            field           = (SpinProperty) view;
        CCI18N                  i18N            = new CCI18N(pageContext, "apocBundle");
        String                  value           = (String) field.getValue();
        NonSyncStringBuffer     buffer          = new NonSyncStringBuffer(DEFAULT_BUFFER_SIZE);    
        String                  qualName        = field.getQualifiedName();
        String                  qualNameEncoded = URLEncoder.encode(qualName);
        
        buffer.append("<table cellpadding=\"0\" cellspacing=\"2\" border=\"0\" width=\"100%\">\r\n");
        buffer.append(" <tr>\r\n");
        buffer.append("  <td align=\"left\">\r\n");
        buffer.append("   <span class=\"LblLev2Txt\"><label for=\""+qualName+"Value\">"+i18N.getMessage((String)field.getValue())+"</label></span>\r\n");
        buffer.append("  </td>\r\n");
        buffer.append("  <td align=\"right\">\r\n");
        buffer.append("   <input type=\"text\" name=\""+qualName+"Value\" id=\""+qualName+"Value\" value=\"\" size=\""+getSize()+"\" class=\"TxtFld\" tabindex=\""+getTabIndex()+"\" onchange=\""+getOnChange()+"\" />\r\n");
        buffer.append("  </td>\r\n");
        buffer.append("  <td align=\"right\">\r\n");
        buffer.append("   <table cellpadding=\"0\" cellspacing=\"2\" border=\"0\" width=\"100%\">\r\n");
        buffer.append("    <tr>\r\n");
        buffer.append("     <td>\r\n");
        buffer.append("      <a href=\"javascript:"+getUpAction()+"\" tabindex=\""+(Integer.parseInt(getTabIndex())+1)+"\" title=\""+i18N.getMessage(getUpTitle())+"\" onmouseover=\"window.status=this.title;return true;\" onmouseout=\"window.status='';return true;\">\r\n");
        buffer.append("       <img alt=\""+i18N.getMessage(getUpTitle())+"\" src=\"/apoc/images/increase.png\" border=\"0\" height=\"6\" width=\"9\" /></a>\r\n");
        buffer.append("     </td>\r\n");
        buffer.append("    </tr>\r\n");
        buffer.append("    <tr>\r\n");
        buffer.append("     <td>\r\n");
        buffer.append("      <img alt=\"\" src=\"/com_sun_web_ui/images/other/dot.gif\" height=\"1\" width=\"2\" />\r\n");
        buffer.append("     </td>\r\n");
        buffer.append("    </tr>\r\n");
        buffer.append("    <tr>\r\n");
        buffer.append("     <td>\r\n");
        buffer.append("      <a href=\"javascript:"+getDownAction()+"\" tabindex=\""+(Integer.parseInt(getTabIndex())+2)+"\" title=\""+i18N.getMessage(this.getDownTitle())+"\" onmouseover=\"window.status=this.title;return true;\" onmouseout=\"window.status='';return true;\">\r\n");
        buffer.append("       <img alt=\""+i18N.getMessage(getUpTitle())+"\" src=\"/apoc/images/decrease.png\" border=\"0\" height=\"6\" width=\"9\" /></a>\r\n");
        buffer.append("     </td>\r\n");
        buffer.append("    </tr>\r\n");
        buffer.append("   </table>\r\n");
        buffer.append("  </td>\r\n");
        buffer.append(" </tr>\r\n");
        buffer.append("</table>\r\n");

        return buffer.toString();
   }
}
