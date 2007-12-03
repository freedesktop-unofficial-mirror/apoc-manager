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

import com.sun.web.ui.taglib.common.CCDisplayFieldTagBase;
import com.iplanet.jato.util.NonSyncStringBuffer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import com.iplanet.jato.view.View;
import com.sun.apoc.templates.parsing.TemplateProperty;
import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;
import com.sun.web.ui.common.CCI18N;


public class ListTag extends CCDisplayFieldTagBase {
    
    private static final short MIN_SIZE = 5;
    private static final short MAX_SIZE = 12;
    

    public ListTag() {
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

        checkChildType(view, ListView.class);
        ListView field = (ListView) view;
        CCI18N i18N = new CCI18N(pageContext, "apocBundle");
        String notSet = SectionModel.getUndefinedValue();
        
        setParent(parent);
        setPageContext(pageContext);
        SectionModel model = (SectionModel) field.getModel();
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        TemplateProperty prop = (TemplateProperty) model.getValue(SectionModel.PROPERTY);
        String separator = prop.getSeparator();
        if (separator == null) {
            separator = " ";
        }       
        // parse values
        String values = (String) field.getValue();
        LinkedList list = new LinkedList(); 
        if (values.equals(notSet)) {
            list.add(values);   
        } else {
            if (values.length() > 0) {
               String options = values; //old with []: values.substring(1, values.length() - 1);
               StringTokenizer st = new StringTokenizer(options, separator);
               while(st.hasMoreTokens()) {
                   list.add(st.nextToken());
               }
               Collections.sort(list);
            }
        }
        
        // calculate list size
        int size = MIN_SIZE;
        if (list.size() > MIN_SIZE) {
            if (list.size() < MAX_SIZE) {
                size = list.size();
            } else {
                size = MAX_SIZE;
            }
        }
        
        NonSyncStringBuffer buffer = 
            new NonSyncStringBuffer(DEFAULT_BUFFER_SIZE);    

        
        // list area
        buffer.append("<table title=\"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
        buffer.append("<tr>\r\n");
        buffer.append("<td valign=\"top\" style=\"border:none;padding:0px\">");
        buffer.append("<select name=\"");
        buffer.append("List");
        buffer.append(field.getQualifiedName());
        buffer.append("\" class=\"Lst\" size=\"");
        buffer.append(size);
        buffer.append("\" multiple=\"multiple\">\r\n");
        addOptions(buffer, list);
        buffer.append("</select>");
        buffer.append("</td>\r\n");
        
        if (field.isEnabled()) {
            // new and delete button
            buffer.append("<td align=\"center\" valign=\"top\" style=\"border:none;padding:0px\">\r\n");
            buffer.append("<table title=\"\" class=\"AddRmvBtnTbl\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:none\">\r\n");
            buffer.append("<tr>\r\n");
            buffer.append("<td align=\"center\" style=\"border:none;padding:0px\">");
            buffer.append("<img src=\"/com_sun_web_ui/images/other/dot.gif\" alt=\"\" border=\"0\" height=\"1\" width=\"10\"/>");
            buffer.append("</td>\r\n");
            buffer.append("<td style=\"width:80px;border:none;padding:0px\" align=\"center\">\r\n");
            buffer.append("<input name=\"AddButton\" type=\"submit\" class=\"Btn1\" value=\"");
            buffer.append(i18N.getMessage("APOC.policies.new.button"));
            buffer.append("\" id=\"" + field.getQualifiedName() + ".AddButton\"");
            buffer.append("\" onclick=\"");
            buffer.append("addNewListItem('");
            buffer.append(field.getQualifiedName());
            buffer.append("'); return false;\" onmouseover=\"javascript: if (this.disabled==0) this.className='Btn1Hov'\" onmouseout=\"javascript: if (this.disabled==0) this.className='Btn1'\" onblur=\"javascript: if (this.disabled==0) this.className='Btn1'\" onfocus=\"javascript: if (this.disabled==0) this.className='Btn1Hov'\"/>\r\n");
            if (isNav()) {
                buffer.append("<br>");
            }
            buffer.append("<div style=\"margin-top:3px;width:80px\">\r\n");
            buffer.append("<input name=\"RemoveButton\" type=\"submit\" class=\"Btn2\" value=\"");
            buffer.append(i18N.getMessage("APOC.policies.delete.button"));
            buffer.append("\" id=\"" + field.getQualifiedName() + ".RemoveButton");	    
            buffer.append("\" onclick=\"");
            buffer.append("removeListItem('");
            buffer.append(field.getQualifiedName());
            buffer.append("'); return false;\" onmouseover=\"javascript: if (this.disabled==0) this.className='Btn2Hov'\" onmouseout=\"javascript: if (this.disabled==0) this.className='Btn2'\" onblur=\"javascript: if (this.disabled==0) this.className='Btn2'\" onfocus=\"javascript: if (this.disabled==0) this.className='Btn2Hov'\"/>\r\n");
            buffer.append("</div>\r\n");
            if (isNav()) {
                buffer.append("<br>");
            }
            buffer.append("<div style=\"margin-top:3px;width:80px\">\r\n");
            buffer.append("<input name=\"RemoveButton\" type=\"submit\" class=\"Btn2\" value=\"");
            buffer.append(i18N.getMessage("APOC.policies.actions.clear"));
            buffer.append("\" id=\"" + field.getQualifiedName() + ".ClearButton");	    
            buffer.append("\" onclick=\"");
            buffer.append("clearList('");
            buffer.append(field.getQualifiedName());
            buffer.append("'); return false;\" onmouseover=\"javascript: if (this.disabled==0) this.className='Btn2Hov'\" onmouseout=\"javascript: if (this.disabled==0) this.className='Btn2'\" onblur=\"javascript: if (this.disabled==0) this.className='Btn2'\" onfocus=\"javascript: if (this.disabled==0) this.className='Btn2Hov'\"/>\r\n");
            buffer.append("</div>\r\n");
            
            buffer.append("</td>\r\n");
            buffer.append("<td align=\"center\" style=\"border:none;\">");
            buffer.append("<img src=\"/com_sun_web_ui/images/other/dot.gif\" alt=\"\" border=\"0\" height=\"1\" width=\"10\"/>");
            buffer.append("</td>");
            buffer.append("</tr>");
            buffer.append("</table></td>");
        }
        buffer.append("</tr>");
        buffer.append("</table>");
        // separator hidden field
        buffer.append("<input type=\"hidden\" name=\"");
        buffer.append("Separator");
        buffer.append(field.getQualifiedName());
        buffer.append("\" value=\"");
        buffer.append(separator);
        buffer.append("\" />");                   
        // hidden field
        StringBuffer buffer2 = new StringBuffer();
        buffer2.append("javascript:ssetTimeout('updateSaveStatus(this.name), 0')");
        buffer2.append("setTimeout(toggleOverwriteCheckbox(this), 0);");

        buffer.append("<input type=\"hidden\" name=\"");
        buffer.append(field.getQualifiedName());
        buffer.append("\" value=\"");
        buffer.append(field.getValue());
        buffer.append("\" onChange=\"");
        buffer.append(buffer2.toString());
        buffer.append("\" onKeyPress=\"");
        buffer.append(buffer2.toString());
        buffer.append("\" />"); 
        return buffer.toString();
   }
   
   
   protected void addOptions(NonSyncStringBuffer buffer, LinkedList list) {
       Iterator it = list.iterator();
       while(it.hasNext()) {
           String value = (String) it.next();
           buffer.append("<option value=\"");
           buffer.append(value);
           buffer.append("\">");
           buffer.append(value);
           buffer.append("</option>\r\n");
       }
   }
}
