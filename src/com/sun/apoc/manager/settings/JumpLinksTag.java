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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.util.NonSyncStringBuffer;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.JspDisplayEvent;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.templates.parsing.TemplateElement;
import com.sun.apoc.manager.resource.ResourceRepository;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.common.CCStyle;
import com.sun.web.ui.taglib.common.CCDescriptorTagBase;
import com.sun.web.ui.taglib.common.CCTagBase;


public class JumpLinksTag extends CCDescriptorTagBase {
    
    private CCI18N m_I18N = null;

    public JumpLinksTag() {
	    super();
    }

    
    public void reset() {
	    super.reset();
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
    
    	checkChildType(view, JumpLinks.class);
    	JumpLinks field = (JumpLinks) view;
    	SheetModel model = field.getModel();
    	if (model == null) {
    	    throw new IllegalArgumentException("Model cannot be null");
    	}
    	
    	setParent(parent);
    	setPageContext(pageContext);
        m_I18N = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
    	NonSyncStringBuffer buffer = 
            new NonSyncStringBuffer(DEFAULT_BUFFER_SIZE);
    
    	try {
    	    field.beginDisplay(new JspDisplayEvent(this, pageContext));
            int count = 0;
            boolean hasSubCategories = field.hasSubCategories();
            if (hasSubCategories) {
                count++;
            } 
            count += model.getNumRows();
            if (count <= 1) {
                return "<br>";
            }
            String names [] = new String [count];
            String anchors [] = new String [count];
            int i = 0;
            if (hasSubCategories) {
                names[i] = m_I18N.getMessage("APOC.policies.subcategories"); 
                anchors[i] = "Policies";
                i++;
            }
            for (int index = 0; i < count; i++, index++) {
                model.setRowIndex(i);
                TemplateElement element = (TemplateElement) model.getValue(SheetModel.TEMPLATE_SECTION);
                ResourceRepository resources = ResourceRepository.getDefaultRepository();
                names[i] = resources.getMessage(element.getResourceId(), 
                                               element.getResourceBundle(), 
                                               (HttpServletRequest) pageContext.getRequest());
                anchors[i] = element.getDefaultName(); 
            }
            // start jump table part
            buffer.append("<div class=\"" +
                CCStyle.CONTENT_JUMP_SECTION_DIV +
                "\">\n<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
                "<tr>\n");

            // This implements the layout for the jump links as specified by
            // HCI Admin. Should have 2, 3, or 4 links per row depending on the
            // number of total links. Should never have any more that 4 links
            // per row. At least that's what the current guideline says.
            if (count < 5)
                appendJumpLinks(buffer, names, anchors, count, 2);
            else if (count < 10)
                appendJumpLinks(buffer, names, anchors, count, 3);
            else
                appendJumpLinks(buffer, names, anchors, count, 4);

            // end jump table part
            buffer.append("</tr></table></div>");
        } catch (ModelControlException e) {
    	    throw new JspException(e.getRootCause());
        }
    	return buffer.toString();
    }
    
    
    protected void setTagAttributes(View child, CCTagBase tag, String element) 
	    throws JspException { 
    }
    
    
    private void appendJumpLinks(NonSyncStringBuffer buffer, String [] names,
        String [] anchors, int count, int linksPerRow) throws JspException {

        int numRows = (count+linksPerRow-1) / linksPerRow;
        for (int k = 0; k < numRows; k++) {
            if (k != 0)
            buffer.append("</tr><tr>");
            for (int i = k; i < k + linksPerRow*numRows; i = i + numRows) {
                if (i >= count) break;
                
                Object[] replace = new Object[1];
                replace[0] = names[i]; 
                
                // Todo use Lockhart tags here
                buffer.append("<td><div class=\"ConJmpLnkDiv\">");
                buffer.append("<a href=\"#");
                buffer.append(anchors[i]);
                buffer.append(" \" class=\"JmpLnk\" title=\"");
                buffer.append(m_I18N.getMessage("APOC.policies.jumpDown.tooltip", replace));
                buffer.append("\" >");
                buffer.append("<img src=\"/com_sun_web_ui/images/href/to_anchor.gif\" alt=\"\" border=\"0\" height=\"10\" width=\"16\" />");
                buffer.append(names[i]);
                buffer.append("</a></div></td>");
            }
        }
    }
}
