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
    <jato:containerView name="Available">

<script type="text/javascript" src="/apoc/js/MainWindow.js"></script>

<script type="text/javascript">
<!--
    function submitNew(name, comment, location) {
         document.OnepaneForm['Onepane.OnepanePageletView.Available.NewName'].value=name;
         document.OnepaneForm['Onepane.OnepanePageletView.Available.NewComment'].value=comment;
         document.OnepaneForm['Onepane.OnepanePageletView.Available.NewLocation'].value=location;
         document.OnepaneForm['Onepane.OnepanePageletView.Available.CommandHidden'].value=0;
         document.OnepaneForm.action='../manager/Onepane?Onepane.OnepanePageletView.Available.AvailableTableView.ActionMenuHref=a';
         document.OnepaneForm.submit();
    }

    function handleSelection(selectTag) {
        var f=document.OnepaneForm;
        if (f != null) {

            var filter = "AvailableDomainTableView";
            if (selectTag.name.indexOf(filter)==-1) {
                filter = "AvailableOrgTableView";
            }
            var command         = document.forms[0]['Onepane.OnepanePageletView.Available.CommandHidden'];
            var commandParam    = document.forms[0]['Onepane.OnepanePageletView.Available.CommandParametersHidden'];
            command.value       = "";
            commandParam.value  = "";
            
            if (selectTag.name.indexOf("DeleteButton")!=-1) {
                // delete
                command.value       = "7";
                commandParam.value  = getSelectedIds(document, filter);
            }
            else {
                selectedCommand=selectTag.options[selectTag.selectedIndex].value;

                if (selectedCommand==1) {
                    //rename
                    var groupName   = getSelectedLinkWithFilter(document, filter).text;
                    groupName       = prompt('<cc:text name="RenameMessage" bundleID="apocBundle" defaultValue="APOC.pool.renameMessage"/>', unescape(groupName));
                    if (groupName!=null) {
                        command.value      = selectedCommand+'';
                        commandParam.value = getSelectedIds(document, filter) + " " + groupName;
                    }
                }
                 else if (selectedCommand==2) {
                    //copy and move
                    var selectedProfiles = getSelectedIds(document, filter);
                    var profile = ""; 
                    if (selectedProfiles.indexOf(" ")>-1) {
                        profile = selectedProfiles.substring(0, selectedProfiles.indexOf(" "));
                    } else {
                        profile = selectedProfiles;
                    }
                    var selectedProfile = "Onepane.OnepanePageletView.Available.SelectedProfile";
                    var copyMoveButton = "Onepane.OnepanePageletView.Available."+filter+".copyMoveWizardWindow";
                    document.forms[0][selectedProfile].value=profile;
                    document.forms[0][copyMoveButton].click();
                }
                else if (selectedCommand==5){
                    //import
                    openWindow(window, null, '/apoc/manager/ProfilesImport?ProfilesImport.ImportGroup='+filter, 'importWindow', 360, 500, true);
                }
                else if (selectedCommand==6){
                    //export
                    var profileId       = getSelectedLinkWithFilter(document, filter).href;
                    profileId           = extractByDelimiters("=", "&", profileId);
                    command.value       = selectedCommand+'';
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
            }

            if (command.value.length>0) {
                var a=document.OnepaneForm.elements[selectTag.name];
                for (i=0; i<a.length; i++) {
                    a[i].selectedIndex=selectTag.selectedIndex;
                }
                f.action='../manager/Onepane?Onepane.OnepanePageletView.Available.'+filter+'.ActionMenuHref=a';
                f.submit();
            }
            
            if (selectTag.options!=null) {
                selectTag.options.selectedIndex=0;
            }
            toggleMenu(filter);
            f.action            = "../manager/Onepane";
            command.value       = "";
            commandParam.value  = "";
        }
    }

    function toggleMenu(filter) {
        var nElementRunner  = 0;
        var nElements       = 0;
        var nCheckedElements= 0;
        var newButton       = "Onepane.OnepanePageletView.Available."+filter+".NewButton";
        var deleteButton    = "Onepane.OnepanePageletView.Available."+filter+".DeleteButton";
        var actionMenu      = "Onepane.OnepanePageletView.Available."+filter+".ActionMenu";
        var wizDomBtnName   = "Onepane.OnepanePageletView.Available.AvailableDomainTableView.copyMoveWizardWindow";
        var wizOrgBtnName   = "Onepane.OnepanePageletView.Available.AvailableOrgTableView.copyMoveWizardWindow";

        var wizDomBtn       = document.forms[0][wizDomBtnName];
        var wizOrgBtn       = document.forms[0][wizOrgBtnName];

       var bIsReadOnly     = false;
        
        while (document.OnepaneForm.elements[nElementRunner]!=null) {
            if ( (document.OnepaneForm.elements[nElementRunner].name.indexOf("SelectionCheckbox")!=-1) &&
                 (document.OnepaneForm.elements[nElementRunner].name.indexOf("jato_boolean") ==-1) &&
                 (document.OnepaneForm.elements[nElementRunner].name.indexOf(filter)!=-1) ) {
                nElements++;
                if (document.OnepaneForm.elements[nElementRunner].checked == true) {
                    nCheckedElements++;
                    while ((document.OnepaneForm.elements[nElementRunner]!=null) && 
                           (document.OnepaneForm.elements[nElementRunner].name.indexOf("AccessHidden")==-1)) {
                        nElementRunner++;
                    }
                    if (document.OnepaneForm.elements[nElementRunner].value=="true") {
                        bIsReadOnly = true;
                    }
                }
            }
            nElementRunner++;
        }

        if (filter=="AvailableDomainTableView") {
            <cc:text name="ConditionalNewDomainDisable" defaultValue="bIsReadOnly=true;" />
        } else if (filter=="AvailableOrgTableView") {
            <cc:text name="ConditionalNewOrgDisable" defaultValue="bIsReadOnly=true;" />
        }
        // enable "new" button if no profile is selected
        ccSetButtonDisabled(newButton, "OnepaneForm", (bIsReadOnly||(nCheckedElements!=0)));

        // enable "delete" button if at least one profile is selected
        ccSetButtonDisabled(deleteButton, "OnepaneForm", (bIsReadOnly||(nCheckedElements==0)));
     
        if (((wizDomBtn != null) && (wizDomBtn.disabled == true)) 
                || ((wizOrgBtn != null) && (wizOrgBtn.disabled == true))) {
            ccSetDropDownMenuOptionDisabled("Onepane.OnepanePageletView.Available.AvailableDomainTableView.ActionMenu", "OnepaneForm", true, 2)            
            ccSetDropDownMenuOptionDisabled("Onepane.OnepanePageletView.Available.AvailableOrgTableView.ActionMenu", "OnepaneForm", true, 2)  
        } else {
            // enable "copymove" action menu item if exactly one profile is selected
            ccSetDropDownMenuOptionDisabled(actionMenu, "OnepaneForm", (nCheckedElements != 1), 2) 
        }

        // enable "rename" action menu item if exactly one profile is selected
        ccSetDropDownMenuOptionDisabled(actionMenu, "OnepaneForm", (bIsReadOnly||(nCheckedElements!=1)), 1);

        // enable "export" action menu item if exactly one profile is selected
        ccSetDropDownMenuOptionDisabled(actionMenu, "OnepaneForm", (nCheckedElements!=1), 4);
        
        // enable "import" action menu item if no profile is selected
        ccSetDropDownMenuOptionDisabled(actionMenu, "OnepaneForm", (bIsReadOnly||(nCheckedElements!=0)), 3);
        
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
        
        <cc:pagetitle name="AvailableTitle" 
            pageTitleText="APOC.profileall.title"
            showPageTitleSeparator="true"
            pageTitleHelpMessage="APOC.profileall.help" 
            showPageButtonsTop="false"
            showPageButtonsBottom="false"
            bundleID="apocBundle">

            <br>
            <jato:content name="DisplayDomainTable">
                <jato:containerView name="AvailableDomainTableView">
                    <cc:wizardwindow name="copyMoveWizardWindow" bundleID="apocBundle" defaultValue="invisible" 
                        extraHtml="style='width:1; visibility:hidden; background-image:none; background:#FFF; color:#FFF; padding:0px; margin:0px; border-style:none;'"
                        onMouseOver="return true" onMouseOut="return true" onBlur="return true" onFocus="return true"/> 
                    <cc:actiontable name="AvailableTable"
                        bundleID="apocBundle"
                        title="APOC.profileall.domtable.title"
                        summary="APOC.pool.tableSummary"
                        empty="APOC.profileassi.table.empty"
                        selectionType="multiple"
                        selectionJavascript="setTimeout('toggleMenu(\&quot;AvailableDomainTableView\&quot;)', 0)" 
                        showAdvancedSortIcon="false"
                        showLowerActions="false"
                        showPaginationControls="false"
                        showPaginationIcon="false"
                        showSelectionIcons="true"
                        showSelectionSortIcon="false"
                        maxRows="10"
                        page="1" />
                </jato:containerView>

            </jato:content>
            
            <jato:content name="DisplayOrgTable">           
                <jato:containerView name="AvailableOrgTableView">
                    <cc:wizardwindow name="copyMoveWizardWindow" bundleID="apocBundle" defaultValue="invisible" 
                        extraHtml="style='width:1; visibility:hidden; background-image:none; background:#FFF; color:#FFF; padding:0px; margin:0px; border-style:none;'"
                        onMouseOver="return true" onMouseOut="return true" onBlur="return true" onFocus="return true"/> 

                    <cc:actiontable name="AvailableTable"
                        bundleID="apocBundle"
                        title="APOC.profileall.orgtable.title"
                        summary="APOC.pool.tableSummary"
                        empty="APOC.profileassi.table.empty"
                        selectionType="multiple"
                        selectionJavascript="setTimeout('toggleMenu(\&quot;AvailableOrgTableView\&quot;)', 0)" 
                        showAdvancedSortIcon="false"
                        showLowerActions="false"
                        showPaginationControls="false"
                        showPaginationIcon="false"
                        showSelectionIcons="true"
                        showSelectionSortIcon="false"
                        maxRows="10"
                        page="1" />
                </jato:containerView>
            </jato:content>         
                   
        </cc:pagetitle>
        <cc:hidden name="SelectedProfile" defaultValue="" />
        <cc:hidden name="CommandHidden" defaultValue="" />
        <cc:hidden name="CommandParametersHidden" defaultValue="" />

    </jato:containerView> 
    <br>
    <br>
<script type="text/javascript">
<!--
    setTimeout('toggleMenu("AvailableDomainTableView")', 0);
    setTimeout('toggleMenu("AvailableOrgTableView")', 0);
    // -->
</script> 
</jato:pagelet> 

