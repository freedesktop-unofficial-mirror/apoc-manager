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
 
<%@ page language="java" %> 
<%@ page import="com.iplanet.jato.view.ViewBean" %>
<%@taglib uri="/jato.tld" prefix="jato"%> 
<%@taglib uri="/cc.tld" prefix="cc"%>

<!--
  // The wizard framework provides a javascript object instance
  // called WizardWindow_Wizard. This object has methods that
  // allow the pagelet to disable/enable buttons and set the
  // page's initial focus.
  //
  // References to the methods of WizardWindow_Wizard if not conditional
  // during runtime, that must result in effects when the page is displayed
  // must be placed in a Javascript function and that function name
  // must be assigned to WizardWindow_Wizard.pageInit.
  // The framework will call WizardWindow_Wizard.pageInit() after the
  // wizard tag has been rendered.
  //
  // script statements must appear within at least the first
  // <td> element since HTML validation objects to the appearance
  // of <script> betwee the <table> element and the first <tr>
  // element.
-->
<jato:pagelet>

<cc:i18nbundle id="apocBundle"
 baseName="com.sun.apoc.manager.resource" />

<%-- For now assume we're still presenting the components in a table
     which is output by the framework and components are
     in rows and cells
--%>   
<tr>
    <td valign="top" align="left" rowspan="1" colspan="1">
    <script type="text/javascript">
      function wizardPageInit() {
          WizardWindow_Wizard.setFocusElementName(
            "WizardWindow.Wizard.nextButton");
      }
      WizardWindow_Wizard.pageInit = wizardPageInit;
      document.onkeypress = onKeyPress;
      function onKeyPress(event){
          if (event.keyCode==13) {
              var f=document.wizWinForm;
              f.elements['WizardWindow.Wizard.finishButton'].click() ;
          }
      }
    </script>
    <cc:label name="NameLabel" styleLevel="2"
            elementName="NameLabel"
            bundleID="apocBundle"/>
    </td>
    <td valign="top" align="left" rowspan="1" colspan="1">
    <cc:label name="NameValue" styleLevel="3"
            elementName="NameValue"
            bundleID="apocBundle"/>
    </td>
</tr>
<tr>
    <td valign="top" align="left" rowspan="1" colspan="1">

    <cc:label name="TypeLabel" styleLevel="2"
            elementName="TypeLabel"
            bundleID="apocBundle"/>
    </td>
    <td valign="top" align="left" rowspan="1" colspan="1">
    <cc:label name="TypeValue" styleLevel="3"
            elementName="TypeValue"
            bundleID="apocBundle"/>
    </td>
</tr>
<jato:content name="DisplayLDAPInfo">
    <tr>
        <td valign="top" align="left" rowspan="1" colspan="1">

        <cc:label name="HostLabel" styleLevel="2"
                elementName="HostLabel"
                bundleID="apocBundle"/>
        </td>
        <td valign="top" align="left" rowspan="1" colspan="1">
        <cc:label name="HostValue" styleLevel="3"
                elementName="HostValue"
                bundleID="apocBundle"/>
        </td>
    </tr>
    <tr>
        <td valign="top" align="left" rowspan="1" colspan="1">

        <cc:label name="PortLabel" styleLevel="2"
                elementName="PortLabel"
                bundleID="apocBundle"/>
        </td>
        <td valign="top" align="left" rowspan="1" colspan="1">
        <cc:label name="PortValue" styleLevel="3"
                elementName="PortValue"
                bundleID="apocBundle"/>
        </td>
    </tr>
    <tr>
        <td valign="top" align="left" rowspan="1" colspan="1">

        <cc:label name="BaseDnLabel" styleLevel="2"
                elementName="BaseDnLabel"
                bundleID="apocBundle"/>
        </td>
        <td valign="top" align="left" rowspan="1" colspan="1">
        <cc:label name="BaseDnValue" styleLevel="3"
                elementName="BaseDnValue"
                bundleID="apocBundle"/>
        </td>
    </tr>
    <tr>
        <td valign="top" align="left" rowspan="1" colspan="1">

        <cc:label name="UserDnLabel" styleLevel="2"
                elementName="UserDnLabel"
                bundleID="apocBundle"/>
        </td>
        <td valign="top" align="left" rowspan="1" colspan="1">
        <cc:label name="UserDnValue" styleLevel="3"
                elementName="UserDnValue"
                bundleID="apocBundle"/>
        </td>
    </tr>
    <tr>
        <td valign="top" align="left" rowspan="1" colspan="1">

        <cc:label name="VendorLabel" styleLevel="2"
                elementName="VendorLabel"
                bundleID="apocBundle"/>
        </td>
        <td valign="top" align="left" rowspan="1" colspan="1">
        <cc:label name="VendorValue" styleLevel="3"
                elementName="VendorValue"
                bundleID="apocBundle"/>
        </td>
    </tr>
    <jato:content name="DisplayMigrateProfs">
        <tr>
            <td valign="top" align="left" rowspan="1" colspan="1">
            <cc:label name="MigrateProfsLabel" styleLevel="2"
                    elementName="MigrateProfsLabel"
                    bundleID="apocBundle"/>
            </td>
            <td valign="top" align="left" rowspan="1" colspan="1">
            <cc:label name="MigrateProfsValue" styleLevel="3"
                    elementName="MigrateProfsValue"
                    bundleID="apocBundle"/>
            </td>
        </tr>
    </jato:content>
    <jato:content name="DisplayMetaConfig">
        <tr>
            <td valign="top" align="left" rowspan="1" colspan="1">

            <cc:label name="MetaConfigurationLabel" styleLevel="2"
                    elementName="MetaConfigurationLabel"
                    bundleID="apocBundle"/>
            </td>
            <td valign="top" align="left" rowspan="1" colspan="1">
                 <cc:textarea name="MetaConfigurationValue" rows="5" cols="40" bundleID="apocBundle"/>
            </td>
        </tr>
    </jato:content>
</jato:content>
<jato:content name="DisplayFileBasedInfo">
    <tr>
        <td valign="top" align="left" rowspan="1" colspan="1">

        <cc:label name="FilepathLabel" styleLevel="2"
                elementName="FilepathLabel"
                bundleID="apocBundle"/>
        </td>
        <td valign="top" align="left" rowspan="1" colspan="1">
        <cc:label name="FilepathValue" styleLevel="3"
                elementName="FilepathValue"
                bundleID="apocBundle"/>
        </td>
    </tr>
</jato:content>
    <table width="99%" border="0" cellspacing="10" cellpadding="0">   
        <tr>
            <td><br>
            </td>
        </tr>
        <tr>
            <td >
                <i><cc:label name="NoActionLabel" styleLevel="3"
                    elementName="NoActionLabel"
                    bundleID="apocBundle"/></i>

            </td>
        </tr>
    </table>
</jato:pagelet>
