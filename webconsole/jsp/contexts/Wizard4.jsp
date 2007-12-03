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
        function switchStateAnonAccess() {
              var f=document.wizWinForm;
              if ( f.elements['WizardWindow.Wizard.Wizard4View.AnonAccess'].checked == true) {
                  f.elements['WizardWindow.Wizard.Wizard4View.NonAnonPassword'].disabled=true;
                  f.elements['WizardWindow.Wizard.Wizard4View.NonAnonUserDN'].disabled=true;
              } else {
                  f.elements['WizardWindow.Wizard.Wizard4View.NonAnonPassword'].disabled=false;
                  f.elements['WizardWindow.Wizard.Wizard4View.NonAnonUserDN'].disabled=false;
              }
          }

        function wizardPageInit() {
              WizardWindow_Wizard.setFocusElementName("WizardWindow.Wizard.Wizard4View.AnonAccess");
              switchStateAnonAccess();      
        }

        WizardWindow_Wizard.pageInit = wizardPageInit;
        document.onkeypress = onKeyPress;
        function onKeyPress(event){
            if (event.keyCode==13) {
                var f=document.wizWinForm;
                f.elements['WizardWindow.Wizard.nextButton'].click() ;
            }
        }

        var m_OpenerTop = top.opener;
        while (m_OpenerTop.top.opener!=null) {
            m_OpenerTop = m_OpenerTop.top.opener;
        }
        m_OpenerTop = m_OpenerTop.top;

        setInterval("setWindowHandle()", 1000);
        function setWindowHandle() {
            m_OpenerTop.m_ContextWizardWindow= top;
        }        
        </script>
    &nbsp;
    </td>
    <td valign="top" align="left" rowspan="1" colspan="1">
        <cc:checkbox name ="AnonAccess" onChange="javascript:switchStateAnonAccess();" bundleID="apocBundle"/>
        <cc:label name="AnonAccessLabel" styleLevel="3"
            elementName="AnonAccessLabel"
            bundleID="apocBundle"/>       
    </td>
</tr>   
<tr>
    <td valign="top" align="left" rowspan="1" colspan="1">
    <cc:label name="NonAnonUserDNLabel" styleLevel="2"
            elementName="NonAnonUserDNLabel"
            bundleID="apocBundle"/>
    </td>
    <td valign="top" align="left" rowspan="1" colspan="1">
        <cc:textfield name ="NonAnonUserDN" bundleID="apocBundle"/>
    </td>
</tr>
<tr>
    <td valign="top" align="left" rowspan="1" colspan="1">
        <cc:label name="NonAnonPasswordLabel" styleLevel="2"
            elementName="NonAnonPasswordLabel"
            bundleID="apocBundle"/>
    </td>
    <td valign="top" align="left" rowspan="1" colspan="1">       
        <cc:password name ="NonAnonPassword" bundleID="apocBundle"/>
    </td>
</tr>
</jato:pagelet>
