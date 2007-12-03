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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<HTML>
    <HEAD>
        <title>Install Policy Package from Local File</title>
	<meta name="Copyright" content="Copyright &copy; 2003 by Sun Microsystems, Inc. All Rights Reserved.">
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<script type="text/javascript" src="/com_sun_web_ui/js/browserVersion.js"></script>
	<script type="text/javascript" src="/com_sun_web_ui/js/stylesheet.js"></script>
	<script type="text/javascript"><!-- Empty script so IE5.0 Windows will draw table and button borders --></script>
    </HEAD>
<body class="DefBdy">
<!-- Secondary Masthead -->
<table title="" class="MstTbl" width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>
    <td><table title="" width="100%" border="0" cellspacing="10" cellpadding="0">
        <tr>
            <td width="90%" class="MstTdTtl"><div class="MstDivSecTtl">
                <img name="PackagesURL.Masthead.ProdNameImage"
                        src="/apoc/images/popuptitle.png" alt="" ></img></div>
            </td>
            <td width="1%" class="MstTdLogo">
                <img name="PackagesURL.Masthead.SunLogoImage" 
                        src="/com_sun_web_ui/images/other/corplogo_popup.gif"
                        alt="Logo for Sun Microsystems, Inc." height="30" width="130" ></img>
            </td>
        </tr>
    </table></td>
</tr>
</table>

<!-- PageTitle -->

<table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr valign="bottom">
        <td align="left" nowrap="nowrap" valign="bottom">
            <div class="TtlTxtDiv">
            <span class="TtlTxt">Install Policy Package from Local File</span>
            </div>
        </td>
    </tr>
</table>
<table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td><img src="/com_sun_web_ui/images/other/dot.gif"
                alt="" border="0" height="2" width="10" /></td>
        <td class="TtlLin" width="100%">
            <img src="/com_sun_web_ui/images/other/dot.gif"
                alt="" border="0" height="2" width="1" /></td>
    </tr>
</table>
<!-- Success / Failure message -->
<table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td><img src="/com_sun_web_ui/images/other/dot.gif"
                    alt="" border="0" height="30" width="1" /></td>
    </tr>
    <tr>
        <td><div class="MstDivUsr">    
            <span class="ChoLblTxt">File upload was: </span>&nbsp;&nbsp;
            <jsp:useBean id="TheBean" scope="page"
                    class="com.sun.apoc.manager.UploadFileBean" />
            <% boolean upload = TheBean.doUpload(request);%>
            <% if (upload == true) { out.print("successful"); }
                else { out.println("unsuccessful"); }%>
        </td>
    </tr>
</table>

<!-- Form necessary for buttons -->
<form method="post" name="formForButton">
<!-- Horizontal line -->
<table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td><img src="/com_sun_web_ui/images/other/dot.gif"
                    alt=""
                    border="0"
                    height="30"
                    width="1" /></td>
    </tr>
    <tr>
        <td><img src="/com_sun_web_ui/images/other/dot.gif"
                    alt=""
                    broder="0"
                    height="2"
                    width="5" /></td>
        <td class="TtlLin" width="100%"><img
                    src="/com_sun_web_ui/images/other/dot.gif"
                    alt=""
                    border="0"
                    height="2"
                    width="1" /></td>
    </tr>
</table>
<!-- Close Button -->
<table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td align="right" nowrap="nowrap" valign="bottom">
            <div class="TtlBtnBtmDiv">
            <input type="submit" class="Btn1"
                value="Close"
                onmouseover="javascript: if (this.disabled==0) this.className='Btn1Hov'"
                onmouseout="javascript: if (this.disabled==0) this.className='Btn1'"
                onblur="javascript: if (this.disabled==0) this.className='Btn1'"
                onfocus="javascript: if (this.disabled==0) this.classname='Btn1Hov'" 
                onclick="var pkg = window.open('../manager/Packages', 'packagesWindow');top.close();"/></div>
        </td>
    </tr>
</table>
</form>
</body>
</html>



