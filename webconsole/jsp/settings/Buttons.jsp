<%@ page info="Frameset" language="java" %> 
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
<%@taglib uri="/WEB-INF/apoc.tld" prefix="apoc"%>

<%@taglib uri="/WEB-INF/tld/com_iplanet_jato/jato.tld" prefix="jato"%>
<%@taglib uri="/WEB-INF/tld/com_sun_web_ui/cc.tld" prefix="cc"%>

<jato:useViewBean className="com.sun.apoc.manager.PolicySettingsButtonsViewBean">

<!-- Header -->
<cc:header name="Header"
  pageTitle="module3.title"
  copyrightYear="2004"
  baseName="com.sun.apoc.manager.resource.apoc_manager"
  bundleID="apocBundle"
  isPopup="true">
  
<script type="text/javascript" src="/apoc/js/ProfileWindow.js"></script>  
<script type="text/javascript">
<!-- 
var alert1= "<cc:text name="Alert1"/>";
var alert2= "<cc:text name="Alert2"/>";
var alert3= "<cc:text name="Alert3"/>";
var alert4= "<cc:text name="Alert4"/>";
var alert5= "<cc:text name="Alert5"/>";

function getAlert(alertId) {
    if (alertId == '1') {
        return alert1;
    } else if (alertId == '2') {
        return alert2;
    } else if (alertId == '3') {
        return alert3;
    } else if (alertId == '4') {
        return alert4;
    } else if (alertId == '5') {
        return alert5;
    }
}
        -->
</script> 
  
<cc:form name="ButtonsForm" method="post">    

<!-- Buttons area -->
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr>
    <td valign='bottom'>
    </td>
    <td align="right" nowrap="nowrap" valign="bottom">
        <div class="TtlBtnBtmDiv">
            <cc:button name="SaveButton" bundleID="apocBundle" 
                 defaultValue="APOC.profilewnd.buttons.save" title="APOC.profilewnd.buttons.save"
                 onClick="updateDisabledFields();saveChanges();return false;" 
                 dynamic="true"
                 disabled="true" />
            <cc:button name="CloseButton" bundleID="apocBundle" 
                 defaultValue="APOC.profilewnd.buttons.close" title="APOC.profilewnd.buttons.close" 
                 titleDisabled="APOC.profilewnd.buttons.close" 
                 type="secondary" onClick="triggerCloseButton(); return false;"/>    
        </div>
    </td>
</tr>     
</table>    
<script type="text/javascript">
<!--
    if ((getContentDocument().getElementById("isNewProfile") != null )
                && (getContentDocument().getElementById("isNewProfile").value == "true")) {
        enableSaveButtons();
    }
// -->
</script> 
</cc:form>
</cc:header>
</jato:useViewBean> 
