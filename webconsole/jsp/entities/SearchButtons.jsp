<%@ page info="Entity Search Masthead" language="java" %>
<%@page contentType="text/html;charset=UTF-8"%> 
<%--
 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 
 Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 
 The contents of this file are subject to the terms of either
 the GNU General Public License Version 2 only ("GPL") or
 the Common Development and Distribution License("CDDL")
 (collectively, the "License"). You may not use this file
 except in compliance with the License. You can obtain a copy
 of the License at www.sun.com/CDDL or at COPYRIGHT. See the
 License for the specific language governing permissions and
 limitations under the License. When distributing the software,
 include this License Header Notice in each file and include
 the License file at /legal/license.txt. If applicable, add the
 following below the License Header, with the fields enclosed
 by brackets [] replaced by your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"
 
 Contributor(s):
 
 If you wish your version of this file to be governed by
 only the CDDL or only the GPL Version 2, indicate your
 decision by adding "[Contributor] elects to include this
 software in this distribution under the [CDDL or GPL
 Version 2] license." If you don't indicate a single choice
 of license, a recipient has the option to distribute your
 version of this file under either the CDDL, the GPL Version
 2 or to extend the choice of license to its licensees as
 provided above. However, if you add GPL Version 2 code and
 therefore, elected the GPL Version 2 license, then the
 option applies only if the new code is made subject to such
 option by the copyright holder.
--%>

<%@taglib uri="/WEB-INF/tld/com_iplanet_jato/jato.tld" prefix="jato"%> 
<%@taglib uri="/WEB-INF/tld/com_sun_web_ui/cc.tld" prefix="cc"%>

<jato:useViewBean className="com.sun.apoc.manager.EntitiesSearchButtonsViewBean">

    <cc:header  pageTitle="APOC.navigation.search" 
                copyrightYear="2002"
                baseName="com.sun.apoc.manager.resource.apoc_manager"
                bundleID="apocBundle"
                isPopup="true">

        <cc:form name="ButtonsForm" method="post">
    
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr>
    <td valign='bottom'>
    </td>
    <td align="right" nowrap="nowrap" valign="bottom">
        <div class="TtlBtnBtmDiv">
            <cc:button  name="SearchButton" 
                        bundleID="apocBundle" 
                        type="default" 
                        title="APOC.search.search.help"
                        defaultValue="APOC.navigation.search" 
                        onClick="javascript:top.parameters.document.forms[0]['EntitiesSearchParameter.SearchButton'].click(); return false;" 
                        tabIndex="1" /> 
            <cc:button  name="ResetButton" 
                        bundleID="apocBundle" 
                        type="secondary" 
                        title="APOC.search.reset.help"
                        defaultValue="APOC.navigation.reset" 
                        onClick="javascript:top.parameters.document.forms[0]['EntitiesSearchParameter.ResetButton'].click(); return false;" 
                        tabIndex="2" /> 
            <cc:button  name="CloseButton" 
                        bundleID="apocBundle" 
                        type="secondary" 
                        title="APOC.search.cancel.help"
                        defaultValue="APOC.profilewnd.buttons.close" 
                        onClick="javascript:top.parameters.document.forms[0]['EntitiesSearchParameter.CloseButton'].click(); return false;" 
                        tabIndex="3" /> 
        </div>
    </td>
</tr>
</table>

        </cc:form> 
    </cc:header>
</jato:useViewBean> 