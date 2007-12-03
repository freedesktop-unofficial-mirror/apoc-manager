<%@ page info="Available" language="java" %> 
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

<jato:pagelet>
    <jato:containerView name="Task">

        <script type="text/javascript" src="/apoc/js/MainWindow.js"></script>

        <script type="text/javascript">
        <!--
            var sProfileEditorTab = "";
        
            function openSyncWindow() {
                openWindow(window, null, '/apoc/manager/SyncLogin?SyncLogin.LeftContext=<cc:text name="CurrentContext"/>&SyncLogin.RightContext=<cc:text name="CurrentContext"/>', 'SyncWindow', 0.8, 0.5);
            }
            
            function openProfileSearchWindow() {
                openWindow(window, null, '/apoc/manager/ProfilesSearchIndex', 'searchProfileWindow', 800, 500);
            }
            
            function profileSearchCallback(sProfileId) {
                openWindow(window, null, '/apoc/manager/ProfileWindowFrameset?ProfileWindowFrameset.SelectedProfile='+sProfileId+'&SelectedEditorTab='+sProfileEditorTab, 'ProfileEditorWindow', 600, 0.9);
            }
            
            function applicabilityCallback(sApplicability) {
                openWindow(window, null, '/apoc/manager/ProfileWindowFrameset?ProfileWindowFrameset.SelectedProfile='+sApplicability, 'ProfileEditorWindow', 600, 0.9);
            }
        // -->
        </script>  

        <%--
        This is a workaround for IE 6:
        using <BR> or equivalents will create a gray area instead of a invisible vertical space
        --%>
        <table><tr><td>
            <img src="/com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="10" width="1" />
        </td></tr></table>

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
        
        <cc:pagetitle name="TaskTitle" 
            pageTitleText="Tasks"
            showPageTitleSeparator="true"
            pageTitleHelpMessage="This tab page lists common tasks." 
            showPageButtonsTop="false"
            showPageButtonsBottom="false"
            bundleID="apocBundle">

            <br>
            
            <table width='100%' cellpadding='0' cellspacing='0'>
                <tr>
                    <td width='2%'><br></td>
                    <td width='47%'><br></td>
                    <td width='2%'><br></td>
                    <td width='47%'><br></td>
                    <td width='2%'><br></td>
                </tr>
                <tr>
                    <td><br></td>
                    <td valign='top'>
                        <table width='100%' cellpadding='5' cellspacing='0'>
                            <tr>
                                <td bgcolor='#C4CBD1'>
                                    <b>Configuration Repositories</b>
                                </td>
                            </tr>
                            <tr>
                                <td bgcolor='#E2E7EA' valign='top'>
                                    <cc:href name="AddContext" onClick="javascript:document.forms[0]['Onepane.OnepanePageletView.Task.AddContextButton'].click(); return false;">
                                        <cc:text name="AddContextText" bundleID="apocBundle" defaultValue="Create a Configuration Repository"/>
                                    </cc:href>
                                    <br>
                                    To be able to configure your applications you need a place to store your configuration data.<br>
                                    This storage is called configuration repository.  Use this task button to create a configuration repository.
                                    <br><br>
                                    <cc:href name="SyncContext" onClick="javascript:openSyncWindow(); return false;">
                                        <cc:text name="SyncContextText" bundleID="apocBundle" defaultValue="Synchronize Two Configuration Repositories"/>
                                    </cc:href>
                                    <br>
                                    The synchronization feature allows you to synchronize profiles between two configuration repositories.<br>
                                    This feature is especially useful if you test the settings first using a test configuration repository before you apply them to the production environment.<br>
                                    Use this button to synchronize two arbitrary configuration repositories.
                                </td>
                            </tr>
                        </table>
                        <br>
                        <table width='100%' cellpadding='5' cellspacing='0'>
                            <tr>
                                <td bgcolor='#C4CBD1'>
                                    <b>Verification</b>
                                </td>
                            </tr>
                            <tr>
                                <td bgcolor='#E2E7EA' valign='top'>
                                    <cc:href name="ReportEntity" onClick="javascript:openWindow(window, null, '/apoc/manager/ShowResultsFrameset', 'reportWindow', 0.9, 800); return false;">
                                        <cc:text name="ReportEntityText" bundleID="apocBundle" defaultValue="Create a Report of an Organization or a Domain"/>
                                    </cc:href>
                                    <br>
                                    This task button opens the report window listing all settings for selected organizations or domains.
                                    <br><br>
                                    <cc:href name="RemoteDesktop" onClick="javascript:openWindow(window, null, '/apoc/manager/RemoteDesktopFrameset?EntityType=DOMAIN&', 'RemoteDesktopWindow', 350, 500, true);  return false;">
                                        <cc:text name="RemoteDesktopText" bundleID="apocBundle" defaultValue="Remote Desktop"/>
                                    </cc:href>
                                    <br>
                                    This task allows you to open a graphical console on another machine.
                                </td>
                            </tr>
                        </table>
                    </td>
                    <td><br></td>
                    <td valign='top'>
                        <table width='100%' cellpadding='5' cellspacing='0'>
                            <tr>
                                <td bgcolor='#C4CBD1'>
                                    <b>Profiles</b>
                                </td>
                            </tr>
                            <tr>
                                <td bgcolor='#E2E7EA' valign='top'>
                                    <cc:href name="CreateProfile" onClick="javascript:openWindow(window, null, '/apoc/manager/ProfilesApplicability', 'ApplicabilityWindow', 300, 500, true); return false;">
                                        <cc:text name="AddContextText" bundleID="apocBundle" defaultValue="Create a Profile"/>
                                    </cc:href>
                                    <br>
                                    A profile is a named container for configuration settings. It allows you to group configuration settings.<br>
                                    Use this task button to create a profile.
                                    <br><br>
                                    <cc:href name="EditProfile" onClick="javascript:sProfileEditorTab='1'; openProfileSearchWindow(); return false;">
                                        <cc:text name="EditProfileText" bundleID="apocBundle" defaultValue="Edit a Profile"/>
                                    </cc:href>
                                    <br>
                                    This task button opens the profile editor so you can change the configuration settings of a profile.
                                    <br><br>
                                    <cc:href name="AssignProfile" onClick="javascript:sProfileEditorTab='2'; openProfileSearchWindow(); return false;">
                                        <cc:text name="AssignProfileText" bundleID="apocBundle" defaultValue="Edit Assignments of a Profile"/>
                                    </cc:href>
                                    <br>
                                    This task button opens the profile editor so you can change the assignments of a profile.
                                    <br><br>
                                    <cc:href name="ReportProfile" onClick="javascript:sProfileEditorTab='4'; openProfileSearchWindow(); return false;">
                                        <cc:text name="ReportProfileText" bundleID="apocBundle" defaultValue="Display all settings of a profile"/>
                                    </cc:href>
                                    <br>
                                    This task button opens the profile editor listing all settings for the selected profile.
                                    <br><br>
                                    <cc:href name="CopyProfile" onClick="javascript:document.forms[0]['Onepane.OnepanePageletView.Task.copyMoveWizardWindow'].click(); return false;">
                                        <cc:text name="CopyProfileText" bundleID="apocBundle" defaultValue="Copy and Move a Profile"/>
                                    </cc:href>
                                    <br>
                                    This task button opens a wizard which allows you to either copy or move a profile.
                                </td>
                            </tr>
                        </table>
                    </td>
                    <td><br></td>
                </tr>
            </table>
            <cc:wizardwindow name="AddContextButton" bundleID="apocBundle" defaultValue="invisible" 
                             extraHtml="style='width:1; visibility:hidden; background-image:none; background:#FFF; color:#FFF; padding:0px; margin:0px; border-style:none;'"
                             onMouseOver="return true" onMouseOut="return true" onBlur="return true" onFocus="return true"/>
            <cc:wizardwindow name="copyMoveWizardWindow" bundleID="apocBundle" defaultValue="invisible" 
                             extraHtml="style='width:1; visibility:hidden; background-image:none; background:#FFF; color:#FFF; padding:0px; margin:0px; border-style:none;'"
                             onMouseOver="return true" onMouseOut="return true" onBlur="return true" onFocus="return true"/>           
        </cc:pagetitle>
    </jato:containerView> 
</jato:pagelet> 

