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
<jato:containerView name="Assignees">

<script type="text/javascript" src="/apoc/js/MainWindow.js"></script>

<script type="text/javascript">
<!--
    function toggleMenu() {
        var nElementRunner=0;
        var nCheckedElements=0;
        var assignButton    = "ProfileWindow.TabContent.Assignees.AssignButton";
        var unassignButton  = "ProfileWindow.TabContent.Assignees.UnassignButton";
        
        while (document.ProfileForm.elements[nElementRunner]!=null) {
            if ( (document.ProfileForm.elements[nElementRunner].name.indexOf("SelectionCheckbox")!=-1)  && 
                 (document.ProfileForm.elements[nElementRunner].name.indexOf("jato_boolean") == -1) &&
                 (document.ProfileForm.elements[nElementRunner].checked==true) ) {
                nCheckedElements++;
            }
            nElementRunner++;
        }

        var bIsReadOnly = <cc:text name="JSReadOnly"/>;

        // enable "assign" button if no profile is selected
        ccSetButtonDisabled(assignButton, "ProfileForm", (bIsReadOnly||(nCheckedElements!=0)));
        // enable "unassign" button if at least one profile is selected
        ccSetButtonDisabled(unassignButton, "ProfileForm", (bIsReadOnly||(nCheckedElements==0)));
    }

    function submitBrowse(entityId, entityType) {
        document.ProfileForm['ProfileWindow.TabContent.Assignees.AssigneeEntityId'].value=entityId;
        document.ProfileForm['ProfileWindow.TabContent.Assignees.AssigneeEntityType'].value=entityType;
        document.ProfileForm.action="../manager/ProfileWindow?ProfileWindow.TabContent.Assignees.AssignHref=a"; 
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
	pageTitleText="APOC.profilewnd.assignees.title" 
	showPageTitleSeparator="true"
	pageTitleHelpMessage="APOC.profilewnd.assignees.help"
	showPageButtonsTop="true"
	showPageButtonsBottom="false">
	
<br>

<!-- Action Table -->
<cc:actiontable
	name="ActionTable"
  	bundleID="apocBundle"
  	title="APOC.profilewnd.assignees.title"
  	empty="APOC.profilewnd.assignees.noorgs"
  	selectionType="multiple"
    selectionJavascript="setTimeout('toggleMenu()', 0)" 
   	showAdvancedSortIcon="false"
  	showLowerActions="false"
  	showPaginationControls="false"
  	showPaginationIcon="false"
  	showSelectionIcons="true"
  	maxRows="25"/>
	
</cc:pagetitle>

<script type="text/javascript">
<!--
    setTimeout('toggleMenu()', 0);
    // -->
</script> 

<cc:hidden name="AssigneeEntityId" defaultValue="a" />
<cc:hidden name="AssigneeEntityType" defaultValue="a" />

</jato:containerView>
</jato:pagelet>
