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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.util.NonSyncStringBuffer;
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.event.JspDisplayEvent;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.templates.parsing.TemplateElement;
import com.sun.apoc.templates.parsing.TemplateSet;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.view.html.CCCheckBox;
import com.sun.web.ui.view.table.CCActionTable;
import com.sun.web.ui.taglib.table.CCActionTableTag;
import com.sun.web.ui.taglib.html.CCCheckBoxTag;
import com.sun.web.ui.taglib.common.CCDescriptorTagBase;
import com.sun.web.ui.taglib.common.CCTagBase;


public class SheetTag extends CCDescriptorTagBase {

    public SheetTag() {
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
    	checkChildType(view, Sheet.class);
    	Sheet field = (Sheet) view;
    	SheetModel model = field.getModel();
    	if (model == null) {
    	    throw new IllegalArgumentException("Model cannot be null");
    	}
    	setParent(parent);
    	setPageContext(pageContext);
        CCI18N i18N = new CCI18N(getRequestContext(), Constants.RES_BASE_NAME);
    	NonSyncStringBuffer buffer = new NonSyncStringBuffer(DEFAULT_BUFFER_SIZE);
    
    	try {
    	    field.beginDisplay(new JspDisplayEvent(this, pageContext));
            if ((model.getNumRows() > 0) && field.hasSubCategories()) {
                appendBackToTopLink(buffer, i18N);
            }
            
            if (model.getNumRows() > 0) appendPageLegend(buffer);
            
            for(int i = 0; i < model.getNumRows(); i++) {
                model.setRowIndex(i);
                TemplateElement element = (TemplateElement) model.getValue(SheetModel.TEMPLATE_SECTION);
                appendSectionAnchor(buffer, element.getDefaultName());
                
                Section sectionChild = (Section) field.getChild(element.getDefaultName());
                SectionTiledView tiledView = (SectionTiledView) sectionChild.getChild(Section.CHILD_SECTION_TILED_VIEW);
                appendSectionBorder(buffer);
                appendSectionStart(buffer, (String) model.getValue(SheetModel.TEMPLATE_SECTION_NAME),
                        field, element, pageContext);
                if (element instanceof TemplateSet) {
                    appendSetTable(buffer, sectionChild);
                } else {
                    appendProperties(buffer, tiledView, pageContext, i18N);
                }
                appendSectionEnd(buffer);
                if (model.getNumRows() > 0) {
                    appendBackToTopLink(buffer, i18N);
                }
            }
        } catch (ModelControlException e) {
    	    throw new JspException(e.getRootCause());
        }
    	return buffer.toString();
    }
    
    
    protected void appendPageLegend(NonSyncStringBuffer buffer) {
        String defaultMsg = Toolbox2.getI18n("APOC.profiles.default.msg");
        buffer.append("<div align=\"right\" class=\"LblRqdDiv\"");
        buffer.append("style=\"margin-top:5px;margin-right:10px;margin-bottom:5px;margin-left:0px\">");
        buffer.append("&#8225 ");
        buffer.append(defaultMsg);
        buffer.append("</div>");
    }
    
    
    protected void appendSetTable(NonSyncStringBuffer buffer, Section section) 
            throws JspException {
        CCActionTableTag tableTag = new CCActionTableTag();
        tableTag.setBundleID("apocBundle");
        tableTag.setShowPaginationControls("false");
        tableTag.setShowPaginationIcon("false");
        tableTag.setShowAdvancedSortIcon("false");
        tableTag.setShowLowerActions("false");
        tableTag.setSummary("APOC.properties.tableSummary");
        tableTag.setShowSelectionIcons("true");
        tableTag.setShowSelectionSortIcon("false");
        tableTag.setSelectionJavascript("javascript: setTimeout(toggleSetButtons(this), 0);");
        CCActionTable tableChild = (CCActionTable) section.getChild(Section.CHILD_SECTION_TABLE);
        buffer.append(tableTag.getHTMLString(getParent(), pageContext, tableChild));
    }
    
        
    protected void appendProperties(NonSyncStringBuffer buffer, SectionTiledView tiledView, 
                PageContext pageContext, CCI18N i18N) throws JspException {
        SectionModel tiledViewModel = (SectionModel) tiledView.getPrimaryModel();
        String qualName = tiledView.getQualifiedName();
        if (qualName.indexOf("[") != -1) {
            qualName = qualName.substring(0, qualName.indexOf("["));
        }
        String overwriteColLabel = qualName + ".overwriteLabelCol";
        buffer.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" title=\"\">");
        buffer.append("<tr>");
        buffer.append("<td valign=\"top\">&nbsp;</td>");
        buffer.append("<td valign=\"top\">&nbsp;</td>");
        buffer.append("<td valign=\"top\">&nbsp;</td>");
        buffer.append("<td id=\"");
        buffer.append(overwriteColLabel);
        buffer.append("\" valign=\"top\"><span class=\"LblLev2Txt\">");
        buffer.append(i18N.getMessage("APOC.profilewnd.settings.overwrite"));
        buffer.append("</span></td>");
        buffer.append("</tr>");
        
        for (int z = 0; z < tiledViewModel.getNumRows(); z++) {
            tiledViewModel.setRowIndex(z);
            
            PropertyTag propTag = new PropertyTag();
            PropertyView propChild = (PropertyView) tiledView.getChild(SectionTiledView.CHILD_PROPERTY_VALUE);
            
            CCCheckBoxTag checkBoxTag = new CCCheckBoxTag();
            CCCheckBox checkBox = (CCCheckBox) tiledView.getChild(SectionTiledView.CHILD_ENFORCED_VALUE);
            StringBuffer buffer2 = new StringBuffer();
            buffer2.append("javascript:setTimeout(\"updateSaveStatus('");
            buffer2.append(checkBox.getQualifiedName());
            buffer2.append("');\",0);");
            checkBoxTag.setOnClick(buffer2.toString());
            checkBoxTag.setTitle(i18N.getMessage("APOC.profilewnd.settings.overwrite"));
            
            buffer.append("<tr>");
            buffer.append("<td valign=\"top\">");
            buffer.append("<div class=\"ConTblCl1Div\">");
            buffer.append("<span class=\"LblLev2Txt\">");
            buffer.append("<label for=\"psLbl1\">");
            buffer.append(tiledViewModel.getValue(SectionModel.PROPERTY_NAME));
            buffer.append(":</label>");
            buffer.append("</span>");
            buffer.append("</div>");
            buffer.append("</td>");
            buffer.append("<td valign=\"top\">");
            buffer.append("<div class=\"ConTblCl2Div\">");
            buffer.append("<span id=\"psLbl1\" class=\"ConDefTxt\">");
            buffer.append(propTag.getHTMLString(this, pageContext, propChild));
            buffer.append("</span>");
            buffer.append("</div>");
            buffer.append("</td>");
            
            buffer.append("<td valign=\"top\" width=\"20px\">&nbsp;</td>");
            
            buffer.append("<td align=\"center\" valign=\"top\">");
            buffer.append("<div class=\"ConTblCl2Div\">");
            buffer.append("<span id=\"psLbl1\" class=\"ConDefTxt\">");
            buffer.append(checkBoxTag.getHTMLString(this, pageContext, checkBox));
            buffer.append("</span>");
            buffer.append("</div>");
            buffer.append("</td>");
            
            buffer.append("</tr>");
        }
        buffer.append("</table>");
    }
    
    protected void appendSectionBorder(NonSyncStringBuffer buffer) {
        buffer.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" title=\"\">");
        buffer.append("<tr>");
        buffer.append("<td><img src=\"/com_sun_web_ui/images/other/dot.gif\" alt=\"\" border=\"0\" height=\"1\" width=\"10\"/></td>");
        buffer.append("<td class=\"ConLin\" width=\"100%\">");
        buffer.append("<img src=\"/com_sun_web_ui/images/other/dot.gif\" alt=\"\" border=\"0\" height=\"1\" width=\"1\"/>");
        buffer.append("</td>");
        buffer.append("</tr>");
        buffer.append("</table>");
    }
    
    protected void appendSectionStart(NonSyncStringBuffer buffer, String sectionTitle, Sheet field, 
            TemplateElement element, PageContext pageContext) throws JspException {
        if (isIe()) {
            buffer.append("<fieldset class=\"ConFldSet\">");
            buffer.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" title=\"\">");
            buffer.append("<tr>");
            buffer.append("<td>");
            buffer.append("<legend class=\"ConFldSetLgd\">");
            buffer.append(sectionTitle);
            buffer.append("</legend>");
            buffer.append("</td>");
            buffer.append("</tr>");
            buffer.append("</table>");
        } else {
            buffer.append("<div class=\"ConFldSetDiv\">");
            buffer.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" title=\"\">");
            buffer.append("<tr>");
            buffer.append("<td>");
            buffer.append("<div class=\"ConFldSetLgdDiv\">");
            buffer.append(sectionTitle);
            buffer.append("</div>");
            buffer.append("</td>");
            buffer.append("</tr>");
            buffer.append("</table>");
        }
    }
    
    protected void appendSectionEnd(NonSyncStringBuffer buffer) {
        if (isIe()) {
            buffer.append("</fieldset>");
        } else {
            buffer.append("</div>");
        }
    }
    
    protected void appendSectionAnchor(NonSyncStringBuffer buffer, String anchorName) {
        buffer.append("<a name=\"");
        buffer.append(anchorName);
        buffer.append("\"/>");
    }
    
    protected void appendBackToTopLink(NonSyncStringBuffer buffer, CCI18N i18N) {
        buffer.append("<div class=\"ConJmpTopDiv\"><a href=\"#top\" class=\"JmpTopLnk\" title=\"");
        buffer.append(i18N.getMessage("APOC.policies.backToTop.tooltip"));
        buffer.append("\">");
        buffer.append("<img src=\"/com_sun_web_ui/images/href/to_top.gif\" alt=\"\" border=\"0\" height=\"10\" width=\"11\" />");
        buffer.append(i18N.getMessage("APOC.ca.backtop"));                
        buffer.append("</a></div>");
    }
    
    protected void setTagAttributes(View child, CCTagBase tag, String element) 
	    throws JspException { 
    }
}
