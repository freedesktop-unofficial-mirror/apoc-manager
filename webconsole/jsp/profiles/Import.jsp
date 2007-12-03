<%@ page info="ProfilesImport" language="java" %> 
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
<jato:useViewBean className="com.sun.apoc.manager.ProfilesImportViewBean">

<!-- Header -->
<cc:header  pageTitle="APOC.groupimport.title"
            bundleID="apocBundle"
            baseName="com.sun.apoc.manager.resource.apoc_manager"
            copyrightYear="2003"
            isPopup="true" 
            onLoad="javascript:onLoadAction()">

<script type="text/javascript">
<!--
var existingNames = "<cc:text name="ExistingNames"/>";
var overwrite = "<cc:text name="OverwriteAlert"/>";

var m_OpenerTop = top.opener;
while (m_OpenerTop.top.opener!=null) {
    m_OpenerTop = m_OpenerTop.top.opener;
}
m_OpenerTop = m_OpenerTop.top;

setInterval("setWindowHandle()", 1000);
function setWindowHandle() {
    m_OpenerTop.m_ImportWindow= top;
}

function onLoadAction() {
    <cc:text name="ConditionalJS" defaultValue="return true;" />
}

function isNameExisting() {
    var newName = document.forms[0]['filename'].value;
    var pos = newName.lastIndexOf("\\");
    if (pos == -1) {
        pos = newName.lastIndexOf("/");
    }

    if (pos != -1) {
        newName = newName.substring(pos + 1, newName.length);
    }
        
    var dotLoc = newName.lastIndexOf(".");
    if (dotLoc != -1) {
        newName = newName.substring(0, dotLoc);
    }

    var names = existingNames.split('|');
    for (i=0; i < names.length; i++) {
        if (newName == names[i]) {
            return true;
        }
    }
    return false;
}

// -->
</script> 

<jato:content name="ConditionalForm">

<!-- Secondary Masthead -->
<cc:secondarymasthead name="Masthead" bundleID="apocBundle" />

<!-- /apoc/jsp/UploadFile.jsp -->
<form action="../manager/ProfilesImport?ProfilesImport.TabHref=a"
      enctype="multipart/form-data" 
      method="post"
      target="importWindow"
      onSubmit="javascript:document.forms[0]['l10nFilename'].value=document.forms[0]['filename'].value; return true;" 
      name="importForm">

<jato:content name="DisplayAlert">
    <div class="content-layout">
        <BR>
        <cc:alertinline name="Alert" bundleID="apocBundle"/>
        <BR>
        <!-- 
            <cc:text name="StackTrace"/>
        -->
    </div>
</jato:content>

<!-- PageTitle -->
<cc:pagetitle name="ImportTitle" bundleID="apocBundle"
            pageTitleText="APOC.groupimport.title"
            pageTitleHelpMessage="APOC.groupimport.help"
            showPageTitleSeparator="true"
            showPageButtonsTop="false"
            showPageButtonsBottom="true">

<!-- non-lockhart code to implement the file upload facility -->
<table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td><img src="/com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="30" width="1" /></td>
    </tr>
    <tr>
        <td>
            <div class="MstDivUsr">    
                <span class="ChoLblTxt">
                    <label for="fileinput">
                        &nbsp;&nbsp;&nbsp;&nbsp;<cc:text name="ChooseText" bundleID="apocBundle"/>
                    </label>
                </span>
                &nbsp;&nbsp;
                <input type="hidden" name="l10nFilename" value="" /> 
                <input  name="filename" id="fileinput" type="file" 
                        onclick="javascript:setTimeout('switchStateImportButton()', 0)" 
                        onkeypress="javascript:setTimeout('switchStateImportButton()', 0)"
                        value="<cc:text name='DefaultValue' />"/>
            </div>
        </td>
    </tr>
</table>
</p>

</cc:pagetitle>

    <script type="text/javascript">
    <!--
    function switchStateImportButton() {
        var value = document.forms[0]['filename'].value;
        if(value == "") {
            document.forms[0]['ProfilesImport.ImportButton'].disabled=true;
            document.forms[0]['ProfilesImport.ImportButton'].className="Btn1Dis";
        } else {
            document.forms[0]['ProfilesImport.ImportButton'].disabled=false;
            document.forms[0]['ProfilesImport.ImportButton'].className="Btn1";
        }
    }

    switchStateImportButton()
    // -->
    </script> 

    
</form>

</jato:content>

</cc:header>
</jato:useViewBean>

