<%@ page info="Result" language="java" %> 
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

<jato:useViewBean className="com.sun.apoc.manager.SyncResultViewBean">

    <cc:header pageTitle="APOC.sync.title" copyrightYear="2005"
        baseName="com.sun.apoc.manager.resource.apoc_manager"
        bundleID="apocBundle"
        isPopup="true"
        onLoad="javascript:enableSync(false);enableReset(false);">

        <script type="text/javascript" src="/apoc/js/MainWindow.js"></script> 

        <script type="text/javascript">
        <!--
            var bLeftContext= true;
            
            function enableSync(pbEnable) {
                if (pbEnable) {
                    top.buttons.document.forms[0]['SyncButtons.Sync'].disabled='';
                    top.buttons.document.forms[0]['SyncButtons.Sync'].className='Btn2';
                } else {
                    top.buttons.document.forms[0]['SyncButtons.Sync'].disabled='disabled';
                    top.buttons.document.forms[0]['SyncButtons.Sync'].className='Btn2Dis';
                }
            }
        
            function enableReset(pbEnable) {
                if (pbEnable) {
                    top.buttons.document.forms[0]['SyncButtons.Reset'].disabled='';
                    top.buttons.document.forms[0]['SyncButtons.Reset'].className='Btn2';
                } else {
                    top.buttons.document.forms[0]['SyncButtons.Reset'].disabled='disabled';
                    top.buttons.document.forms[0]['SyncButtons.Reset'].className='Btn2Dis';
                }
            }
        
            function openLeftBrowseTreeWindow() {
                bLeftContext = true;
                openWindow(window, null, '/apoc/manager/BrowseTreeIndex?ContextId=<cc:text name="LeftContextJS"/>&', 'TreeWindow', 500, 600, true);
            } 
            
            function openRightBrowseTreeWindow() {
                bLeftContext = false;
                openWindow(window, null, '/apoc/manager/BrowseTreeIndex?ContextId=<cc:text name="RightContextJS"/>&', 'TreeWindow', 500, 600, true);
            } 
            
            function handleSelection(selectTag) {
                var f=document.ResultsForm;
                if (f != null) {
                    var sChildName = selectTag.name.substring(selectTag.name.lastIndexOf(".")+1, selectTag.name.length);
                    f.action='/apoc/manager/SyncLogin?SyncLogin.'+sChildName+'='+encodeURIComponent(selectTag.value);
                    f.target='_top';
                    f.submit(); 
                }

                return false;
            }

            function submitBrowse(entityId, entityType) {
                if (bLeftContext) {
                    document.ResultsForm.action="/apoc/manager/SyncResult?SyncResult.LeftBrowseHref=" + entityType + "|" + encodeURIComponent(entityId); 
                } else {
                    document.ResultsForm.action="/apoc/manager/SyncResult?SyncResult.RightBrowseHref=" + entityType + "|" + encodeURIComponent(entityId); 
                }
                document.ResultsForm.submit(); 
            }

            function collectActions() {

                var resultsForm     = document.ResultsForm;

                var elementRunner   = 0;
                var leftId          = null;
                var rightId         = null;
                var bIsTicked       = false;
                var sSyncIndicator  = '-';

                // iterate over every SelectionCheckbox;jato_boolean;LeftIdHidden;RightIdHidden set in all tables
                while (document.forms[0].elements[elementRunner]!=null) {
                    // find begin of one quadruple
                    if (document.forms[0].elements[elementRunner].name.indexOf("SelectionCheckbox")!=-1) {
                        // sync quadruple only if checkbox is ticked
                        if (document.forms[0].elements[elementRunner].checked==true) {
                            bIsTicked=true;
                            sSyncIndicator='+';
                        } else {
                            sSyncIndicator='-';
                        }
                        elementRunner++;
                        elementRunner++; // skip the jato_boolean hidden field introduced by Lockhart 2.1
                        sValue = document.forms[0].elements[elementRunner].value;
                        if ((sValue.indexOf('+')==0) || (sValue.indexOf('-')==0)) {
                            sValue = sValue.substring(1, sValue.length);
                        }
                        document.forms[0].elements[elementRunner].value = sSyncIndicator + sValue;
                        elementRunner++;
                        sValue = document.forms[0].elements[elementRunner].value;
                        if ((sValue.indexOf('+')==0) || (sValue.indexOf('-')==0)) {
                            sValue = sValue.substring(1, sValue.length);
                        }
                        document.forms[0].elements[elementRunner].value = sSyncIndicator + sValue;
                    }
                    elementRunner++;
                }
                enableSync(bIsTicked);
                enableReset(bIsTicked);
            }
        // -->
        </script>  
        
        <cc:secondarymasthead   name="Masthead"
                                src="/apoc/images/popuptitle.gif"
                                alt="APOC.masthead.altText"
                                bundleID="apocBundle"/>
        
        <cc:form name="ResultsForm" method="post" defaultCommandChild="DefaultHref">

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
     
            <cc:pagetitle name="SyncResultTitle" 
                pageTitleText="APOC.sync.title"
                pageTitleHelpMessage="APOC.sync.help"
                showPageTitleSeparator="true"
                showPageButtonsTop="true"
                showPageButtonsBottom="true"
                bundleID="apocBundle">
                
                <table cellpadding='0' cellspacing='0' border='0'>
                    <tr><td>
                        <cc:propertysheet name="Contexts" showJumpLinks="false" bundleID="apocBundle"/> 
                    </td></tr>
                    <tr><td><br></td></tr>
                    <tr><td align='right'>
                        <cc:button name="CompareButton"
                            bundleID="apocBundle"
                            type="secondary"
                            title="APOC.sync.compare.help"
                            defaultValue="APOC.sync.compare.button"/> 
                    </td></tr>
                </table>
                <br><br>
                
                <table border="0" width="100%" cellpadding="0" cellspacing="0">
                    <tr>
                        <td>
                            <img src="/com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="10" />
                        </td>
                        <td class="TtlLin" style="background-color:#d0d7da" width="90%">
                            <img src="/com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="1" />
                        </td>
                        <td>
                            <img src="/com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="1" width="10" />
                        </td>
                    </tr>
                </table> 

                <jato:content name="ActionCompleted">

                    <jato:tiledView name="ContextTiledView">
                        <br>
                        <br>
                        <cc:actiontable name="SyncTable"
                            bundleID="apocBundle"
                            title="APOC.sync.result.title"
                            summary="APOC.sync.result.summary"
                            empty="APOC.sync.result.empty"
                            selectionJavascript="setTimeout('collectActions()', 0)"  
                            selectionType="multiple"
                            showAdvancedSortIcon="false"
                            showLowerActions="false"
                            showPaginationControls="false"
                            showPaginationIcon="false"
                            showSelectionIcons="true"
                            showSelectionSortIcon="false"
                            maxRows="10"
                            page="1" />

                        <br>

                    </jato:tiledView>

                    <jato:content name="Help">

                        <table border="0" width="100%" cellpadding="0" cellspacing="0">
                            <tr valign="bottom">
                                <td nowrap="nowrap" valign="bottom">
                                    <div class="TtlTxtDiv">
                                        <h1 class="TtlTxt"><cc:text name="HelpText0" defaultValue="APOC.sync.result.help0" bundleID="apocBundle"/></h1>
                                    </div>
                                </td>
                            </tr>
                        </table>

                        <div class="content-layout">
                            <BR>
                            <cc:text name="HelpText1" defaultValue="APOC.sync.result.help1" bundleID="apocBundle"/>
                            <BR><BR>
                            <cc:text name="HelpText2" defaultValue="APOC.sync.result.help2" bundleID="apocBundle"/>
                            <BR><BR>
                            <cc:text name="HelpText3" defaultValue="APOC.sync.result.help3" bundleID="apocBundle"/>
                            <BR><BR>
                            <cc:text name="HelpText4" defaultValue="APOC.sync.result.help4" bundleID="apocBundle"/>
                            <BR><BR>
                            <cc:text name="HelpText5" defaultValue="APOC.sync.result.help5" bundleID="apocBundle"/>
                            <BR><BR>
                        </div>

                    </jato:content> 
                    
                </jato:content>
                
                <jato:content name="ActionCycling">
                    <cc:hidden name="CycleHidden" defaultValue="runs"/>
                    <BR><BR>
                    <CENTER>
                        <H2><cc:text name="BusyText1" defaultValue="APOC.sync.busymsg.text" bundleID="apocBundle"/></H2>
                        <BR>
                        <cc:text name="CurrentAction" defaultValue=""/>
                        <BR><BR>
                        <cc:text name="ElapsedTime" defaultValue=""/>
                    </CENTER>
                    <script type="text/javascript">
                    <!--
                        setTimeout("document.ResultsForm['SyncResult.CompareButton'].click()", 5000);
                    // -->
                    </script> 
                </jato:content>

            </cc:pagetitle>
            
            <script type="text/javascript">
            <!--
                setTimeout('collectActions()', 0);
            // -->
            </script> 

        </cc:form>
    </cc:header>
</jato:useViewBean> 
