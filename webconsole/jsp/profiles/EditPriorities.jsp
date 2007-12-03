<%@ page info="Edit Priorities" language="java" %> 
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

    <jato:useViewBean className="com.sun.apoc.manager.ProfilesEditPrioritiesViewBean">

    <cc:header  pageTitle="APOC.prios.title" copyrightYear="2003"
                baseName="com.sun.apoc.manager.resource.apoc_manager"
                bundleID="apocBundle" isPopup="true" onLoad="javascript:conditionalClose()">

        <script type="text/javascript">
        <!--
            function conditionalClose() {
                if (document.PrioForm==null) {
                    self.opener.parent.navigation.document.NavigationAreaForm.action="../manager/NavigationArea?NavigationArea.DefaultHref='default'";
                    self.opener.parent.navigation.document.NavigationAreaForm.submit();
                    window.close();
                }
            }

            function moveup(list) {
                var begin = -1;
                for (i = 0; i < list.options.length; i++) {
                    if (list.options[i].selected) {
                        if (i - 1 > begin) {
                            iotext = list.options[i-1].text;
                            iovalue = list.options[i-1].value;
                            list.options[i-1].text      = list.options[i].text;
                            list.options[i-1].value     = list.options[i].value;
                            list.options[i-1].selected  = true;
                            list.options[i].text        = iotext;
                            list.options[i].value       = iovalue;
                            list.options[i].selected    = false;
                            begin = i - 1; 
                        } else {
                            begin = i;
                        }
                    }
                }
            }

            function movedown(list) {
                var end = list.options.length-1;
                for (i = end; i >= 0; i=i-1) {
                    if (list.options[i].selected) {
                        if (i < end) {
                            iotext = list.options[i].text;
                            iovalue = list.options[i].value;
                            list.options[i].text        = list.options[i+1].text;
                            list.options[i].value       = list.options[i+1].value;
                            list.options[i].selected    = false;
                            list.options[i+1].text      = iotext;
                            list.options[i+1].value     = iovalue;
                            list.options[i+1].selected  = true;
                            end = i + 1;
                        } else {
                            end = i;
                        }
                    }
                }
            }

            function ableButtons(list, upButton, downButton) {
                var canUp=false;
                var canDown=false;
                for (i = 0; i < list.options.length; i++) {
                    if (list.options[i].selected==true) {
                        if ( (i>0) && (list.options[i-1].selected==false) ) {
                            canUp=true;
                        }
                        if ( (i<list.options.length-1) && (list.options[i+1].selected==false) ) {
                            canDown=true;
                        }
                    }
                }
                if (canUp) {
                    upButton.className="Btn1";
                    upButton.disabled=0;
                }
                else {
                    upButton.className="Btn1Dis";
                    upButton.disabled=1;
                }
                if (canDown) {
                    downButton.className="Btn1";
                    downButton.disabled=0;
                }
                else {
                    downButton.className="Btn1Dis";
                    downButton.disabled=1;
                }
            }

            function setNewPrios(list, field) {
                var newPrioString = list.options[0].text;
                for (i = 1; i < list.options.length; i++) {
                    newPrioString=newPrioString+"|"+list.options[i].text;
                }
                field.value = newPrioString;
            }
        // -->
        </script> 

        <jato:content name="FirstShow">

            <cc:secondarymasthead name="Masthead" bundleID="apocBundle"
                 src="/apoc/images/popuptitle.gif"/>

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

            <cc:form name="PrioForm" method="post">

                <cc:pagetitle name="SearchParamsTitle" 
                    bundleID="apocBundle"
                    pageTitleText="APOC.prios.title"
                    pageTitleHelpMessage="APOC.prios.help"
                    showPageTitleSeparator="true"
                    showPageButtonsTop="false"
                    showPageButtonsBottom="true">
                    <br>
                    <center>
                    <table title="" border="0" cellpadding="0" cellspacing="0">
                    <tr>
                    <td valign="top">
                        <cc:selectablelist name="PrioritiesList"
                                           bundleID="apocBundle"
                                           dynamic="true"
                                           multiple="true"
                                           size="15"
                                           onChange="javascript:ableButtons(this, document.PrioForm['NavigationAreaPoolPriorities.MoveUp'], document.PrioForm['NavigationAreaPoolPriorities.MoveDown']); return false;"/>
                    </td>
                    <td align="center" valign="top">
                    <table title="" class="AddRmvBtnTbl" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                    <td align="center"><img src="/com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="10" /></td>
                    <td style="width:120px" align="center">
                    <div class="AddRmvHrzWin">
                        <cc:button name="MoveUp"
                            bundleID="apocBundle" 
                            defaultValue="APOC.prios.up"
                            type="primary"
                            disabled="true"
                            onClick="javascript:moveup(document.PrioForm['NavigationAreaPoolPriorities.PrioritiesList']); ableButtons(document.PrioForm['NavigationAreaPoolPriorities.PrioritiesList'], this, document.PrioForm['NavigationAreaPoolPriorities.MoveDown']); return false;"/>
                    </div>
                    <div class="AddRmvHrzWin">
                        <cc:button name="MoveDown"
                            bundleID="apocBundle" 
                            defaultValue="APOC.prios.down"
                            type="primary"
                            disabled="true"
                            onClick="javascript:movedown(document.PrioForm['NavigationAreaPoolPriorities.PrioritiesList']); ableButtons(document.PrioForm['NavigationAreaPoolPriorities.PrioritiesList'], document.PrioForm['NavigationAreaPoolPriorities.MoveUp'], this); return false;"/>
                    </div>
                    </td>
                    </tr>
                    </table>
                    </td>
                    </tr>
                    </table>
                    </center>
                </cc:pagetitle>

                <cc:hidden name="NewPrios"/>
                <cc:hidden name="ViewLinkHidden"/>

            </cc:form>

        </jato:content>

    </cc:header>

</jato:useViewBean> 
