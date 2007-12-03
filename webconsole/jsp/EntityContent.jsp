<%@ page info="Content Area" language="java" %> 
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

<jato:useViewBean className="com.sun.apoc.manager.EntityContentViewBean">

<cc:header pageTitle="APOC.masthead.altText" copyrightYear="2003"
 baseName="com.sun.apoc.manager.resource.apoc_manager"
 bundleID="apocBundle">

<script type="text/javascript" src="/apoc/js/MainWindow.js"></script>

<script type="text/javascript">
<!--
    
    function submitNew(name, comment, location) {
         document.EntityContentForm['EntityContent.NewName'].value=name;
         document.EntityContentForm['EntityContent.NewComment'].value=comment;
         document.EntityContentForm['EntityContent.NewLocation'].value=location;
         setFormAction('../manager/EntityContent?EntityContent.AssignedTableView.ActionMenuHref=7&amp;');
         document.EntityContentForm.submit();
     }

    function handleSelection(selectTag) {
        var f=document.EntityContentForm;
        if (f != null) {
        
            var command         = document.forms[0]['EntityContent.CommandHidden'];
            var commandParam    = document.forms[0]['EntityContent.CommandParametersHidden'];
            command.value       = "";
            commandParam.value  = "";
            var selectedCommand = selectTag.options[selectTag.selectedIndex].value;

            if (selectedCommand==1) {
                //rename
                var groupName   = getSelectedLink(document).text;
                groupName       = prompt('<cc:text name="RenameMessage" bundleID="apocBundle" defaultValue="APOC.pool.renameMessage"/>', unescape(groupName));

                if (groupName!=null) {
                    command.value      = selectedCommand+'';
                    commandParam.value = getSelectedIds(document, "AssignedTableView") + " " + groupName;
                }
            }
            else if (selectedCommand==2) {
                //copy and move
                var selectedProfiles = getSelectedIds(document, "AssignedTableView");
                var profile = ""; 
                if (selectedProfiles.indexOf(" ")>-1) {
                    profile = selectedProfiles.substring(0, selectedProfiles.indexOf(" "));
                } else {
                    profile = selectedProfiles;
                }

                document.forms[0]['EntityContent.AssignedTableView.SelectedProfile'].value=profile;
                document.forms[0]['EntityContent.AssignedTableView.copyMoveWizardWindow'].click();
            }
            else if (selectedCommand==5){
                //import
                top.masthead.ImportWindow = openWindow(window, null,  '/apoc/manager/ProfilesImport?ProfilesImport.ImportGroup=EntityContentView', 'importWindow', 360, 500, true);
            }
            else if (selectedCommand==6){
                //export
                var profileId       = getSelectedLink(document).href;
                profileId           = extractByDelimiters("=", "&", profileId);
                command.value       = selectedCommand;
                commandParam.value  = profileId;

                var checkboxRunner = 0;
                while (document.forms[0].elements[checkboxRunner]!=null) {
                    if ( (document.forms[0].elements[checkboxRunner].name.indexOf("SelectionCheckbox")!=-1) &&
                         (document.forms[0].elements[checkboxRunner].name.indexOf("jato_boolean") == -1) && 
                         (document.forms[0].elements[checkboxRunner].checked==true) ) {
                        document.forms[0].elements[checkboxRunner].checked=false;
                        break;
                    }
                    checkboxRunner++;
                }
            }

            if (command.value.length>0) {
                var a=document.EntityContentForm.elements[selectTag.name];
                for (i=0; i<a.length; i++) {
                    a[i].selectedIndex=selectTag.selectedIndex;
                }
                f.action='../manager/EntityContent?EntityContent.AssignedTableView.ActionMenuHref=a';
                f.submit();
            }

            if (selectTag.options!=null) {
                selectTag.options.selectedIndex=0;
            }
            toggleMenu();
            f.action            = "../manager/EntityContent";
            command.value       = "";
            commandParam.value  = "";
        }
    }

    function toggleMenu() {
        var nElementRunner=0;
        var nElements=0;
        var nCheckedElements=0;
        var assignButton    = "EntityContent.AssignedTableView.AssignButton";
        var unassignButton  = "EntityContent.AssignedTableView.UnassignButton";
        var newButton       = "EntityContent.AssignedTableView.NewButton";
        var deleteButton    = "EntityContent.AssignedTableView.DeleteButton";
        var actionMenu      = "EntityContent.AssignedTableView.ActionMenu";
        var wizBtn          = document.forms[0]['EntityContent.AssignedTableView.copyMoveWizardWindow'];
        
        while (document.EntityContentForm.elements[nElementRunner]!=null) {
            if ((document.EntityContentForm.elements[nElementRunner].name.indexOf("SelectionCheckbox")!=-1) &&
                 (document.EntityContentForm.elements[nElementRunner].name.indexOf("jato_boolean") == -1)) {
                nElements++;
                if (document.EntityContentForm.elements[nElementRunner].checked==true) {
                    nCheckedElements++;
                }
            }
            nElementRunner++;
        }
        
        var bIsReadOnly = <cc:text name="JSReadOnly"/>;

        // enable "assign" button if no is selected
        ccSetButtonDisabled(assignButton, "EntityContentForm", (bIsReadOnly||(nCheckedElements!=0)));

        // enable "unassign" button if at least one profile is selected
        ccSetButtonDisabled(unassignButton, "EntityContentForm", (bIsReadOnly||(nCheckedElements==0)));

        // enable "new" button if no profile is selected
        ccSetButtonDisabled(newButton, "EntityContentForm", (bIsReadOnly||(nCheckedElements!=0)));

        // enable "delete" button if at least one profile is selected
        ccSetButtonDisabled(deleteButton, "EntityContentForm", (bIsReadOnly||(nCheckedElements==0)));

        if (wizBtn.disabled == true) {
            ccSetDropDownMenuOptionDisabled(actionMenu, "EntityContentForm", true, 2)            
        } else {
            // enable "copymove" action menu item if exactly one profile is selected
            ccSetDropDownMenuOptionDisabled(actionMenu, "EntityContentForm", (nCheckedElements!=1), 2)
        }

        // enable "rename" action menu item if exactly one profile is selected
        ccSetDropDownMenuOptionDisabled(actionMenu, "EntityContentForm", (bIsReadOnly||(nCheckedElements!=1)), 1);

        // enable "export" action menu item if exactly one profile is selected
        ccSetDropDownMenuOptionDisabled(actionMenu, "EntityContentForm", (nCheckedElements!=1), 4);
        
        // enable "import" action menu item if no profile is selected
        ccSetDropDownMenuOptionDisabled(actionMenu, "EntityContentForm", (bIsReadOnly||(nCheckedElements!=0)), 3);
        
    }
    // -->
</script> 

<a name="top" id="top"/>

<cc:form name="EntityContentForm" method="post" onSubmit='javascript:return isSubmitAllowed();' defaultCommandChild='DefaultHref'>

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

        <cc:pagetitle name="AssignedTitle" 
            pageTitleText="APOC.pool.sets"
            showPageTitleSeparator="true"
            pageTitleHelpMessage="APOC.profile.page.title" 
            showPageButtonsTop="false"
            showPageButtonsBottom="false"
            bundleID="apocBundle">
        </cc>
            
            <br>
            <div class="content-layout">             
            <cc:button  name="ResultButton"
                        bundleID="apocBundle" 
                        type="secondary" 
                        title="APOC.pool.report_button.help"
                        defaultValue="APOC.profileassi.showResults" 
                        onClick="javascript:allowSubmit(false); openWindow(window, null, '/apoc/manager/ShowResultsFrameset', 'reportWindow', 0.9, 900);" />  
            </div>
            <br>
            
            <jato:containerView name="AssignedTableView">

                <cc:actiontable name="AssignedTable"
                    bundleID="apocBundle"
                    title="APOC.policysets.title"
                    summary="APOC.pool.tableSummary"
                    empty="APOC.profileassi.table.empty"
                    selectionType="multiple"
                    selectionJavascript="setTimeout('toggleMenu()', 0)" 
                    showAdvancedSortIcon="false"
                    showLowerActions="false"
                    showPaginationControls="false"
                    showPaginationIcon="false"
                    showSelectionIcons="true"
                    showSelectionSortIcon="false"
                    maxRows="10"
                    page="1" />

                <script type="text/javascript">
                <!--
                    <cc:text name="ConditionalOpenEditor" defaultValue="" /> 
               // -->
                </script> 
                <cc:wizardwindow name="copyMoveWizardWindow" bundleID="apocBundle" defaultValue="invisible" 
                     extraHtml="style='width:0; visibility:hidden; background-image:none; background:#FFF; color:#FFF; padding:0px; margin:0px; border-style:none;'"
                     onMouseOver="return true" onMouseOut="return true" onBlur="return true" onFocus="return true"/> 
                <cc:hidden name="SelectedProfile" defaultValue="" />
            </jato:containerView>

            <jato:content name="AssignedAboveView">
            
                <br><br>

                <jato:containerView name="AssignedAboveTableView">

                    <cc:actiontable name="AssignedAboveTable"
                        bundleID="apocBundle"
                        title="APOC.profile.inherited.table"
                        selectionType="no select"
                        showAdvancedSortIcon="false"
                        showLowerActions="false"
                        showPaginationControls="false"
                        showPaginationIcon="false"
                        showSelectionIcons="false"
                        showSelectionSortIcon="false"
                        maxRows="10"
                        page="1" />

                </jato:containerView>
            
            </jato:content>

            <jato:content name="ConditionalMemberTable">
            
                <br><br>

                <jato:containerView name="MemberTableView">

                    <cc:actiontable name="MemberTable"
                        bundleID="apocBundle"
                        selectionType="no select"
                        showAdvancedSortIcon="false"
                        showLowerActions="false"
                        showPaginationControls="false"
                        showPaginationIcon="false"
                        showSelectionIcons="false"
                        showSelectionSortIcon="false"
                        maxRows="10"
                        page="1" />

                </jato:containerView>
            
            </jato:content>

        </cc:pagetitle>
        <cc:hidden name="NewName" defaultValue="a" />
        <cc:hidden name="NewComment" defaultValue="a" />
        <cc:hidden name="NewLocation" defaultValue="a" />
        <cc:hidden name="CommandHidden" defaultValue="" />
        <cc:hidden name="CommandParametersHidden" defaultValue="" />

<script type="text/javascript">
<!--
    setTimeout('toggleMenu()', 0);
    <cc:text name="NavigationConditionalImageSwap" defaultValue="" />
// -->

</script> 
 
    <cc:hidden name="Anchor"/>
    <cc:hidden name="HelpLocale"/>

</cc:form>
</cc:header>
</jato:useViewBean> 
