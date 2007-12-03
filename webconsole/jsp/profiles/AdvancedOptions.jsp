<%@ page info="ContentAreaPolicies" language="java" %> 
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


<jato:pagelet>
    <jato:containerView name="AdvancedOptions">

    <script type="text/javascript" src="/apoc/js/MainWindow.js"></script>

    <script type="text/javascript">
    <!--
        function submitBrowse(entityId, entityType) {
            document.ProfileForm['ProfileWindow.TabContent.AdvancedOptions.GeneralEntityId'].value=entityId;
            document.ProfileForm['ProfileWindow.TabContent.AdvancedOptions.GeneralEntityType'].value=entityType;
            document.ProfileForm.action="../manager/ProfileWindow?ProfileWindow.TabContent.AdvancedOptions.RelocateButton=a";
            document.ProfileForm.submit();
        }

        function openBrowseTreeWindow() {
            openWindow(window, null, '/apoc/manager/BrowseTreeIndex?EntityId=<cc:text name="JSEntityId"/>&EntityType=<cc:text name="JSEntityType"/>&', 'TreeWindow', 500, 600, true); 
        }
         
    // -->
    </script> 

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
 
        <cc:pagetitle name="PageTitle" bundleID="apocBundle"
            pageTitleText="APOC.profilewnd.advanced.tab" 
            showPageTitleSeparator="true"
            pageTitleHelpMessage="APOC.profilewnd.advanced.pagetitle_help"
            showPageButtonsTop="true"
            showPageButtonsBottom="false">

            <cc:propertysheet name="Properties" bundleID="apocBundle" showJumpLinks="false"/>
            
            <table width='100%' cellpadding='25' cellspaceing='0'><tr><td>
                <cc:orderablelist name="PrioritizationList" bundleID="apocBundle" label="APOC.profilewnd.advanced.merge_order"/>
            </td></tr></table>

        </cc:pagetitle>

        <cc:hidden name="GeneralEntityId" defaultValue="a" />
        <cc:hidden name="GeneralEntityType" defaultValue="" />

        <script type="text/javascript">
        <!--
            var oldListValue = "";
            
            function checkForChange() {
                var currentListValue = "";
                var orderedList = document.forms[0]['ProfileWindow.TabContent.AdvancedOptions.PrioritizationList.SelectedListBox'];
                for(i=0; i<orderedList.length; i++) {
                    currentListValue = currentListValue + orderedList.options[i].value;
                }
                if (oldListValue.length==0) {
                    oldListValue = currentListValue;
                } else {
                    if (oldListValue!=currentListValue) {
                        enableSaveButtons();
                        clearInterval(checkInterval);
                    }
                }
            }
            
            var bIsReadOnly = <cc:text name="JSReadOnly" />;
            
            if (bIsReadOnly) {
                document.forms[0]['ProfileWindow.TabContent.AdvancedOptions.PrioritizationList.SelectedListBox'].disabled=true;
                document.forms[0]['ProfileWindow.TabContent.AdvancedOptions.PrioritizationList.SelectedListBox'].className="LstDis";
            } else {
                var checkInterval = setInterval('checkForChange()',500);
            }

            document.ProfileForm.action="../manager/ProfileWindow?ProfileWindow.TabContent.AdvancedOptions.SaveButton=APOC.profilewnd.buttons.save";

            updateButtonsArea();
        // -->
        </script> 

    </jato:containerView>
</jato:pagelet>
