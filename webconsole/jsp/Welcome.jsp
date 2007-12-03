<%@page info="Root Onepane" language="java"%>
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

<jato:useViewBean className="com.sun.apoc.manager.WelcomeViewBean">

<%-- Header --%>
<cc:header pageTitle="APOC.masthead.altText" copyrightYear="2003"
 baseName="com.sun.apoc.manager.resource.apoc_manager"
 bundleID="apocBundle">

<script type="text/javascript" src="/apoc/js/MainWindow.js"></script> 

<script type="text/javascript">
<!--
    params=window.location.search;
    params=params.substr(1);
    if (params.indexOf("ClosePopups") != -1) {
        setTimeout("closePopups() ;",1000);
    }

    function handleEditButtonRequest() {
        ccSetButtonDisabled("Welcome.ManagerTableView.wizardWindow", "WelcomeForm", false); 
        var contextName = getSelectedContext(document).text;
        var f           = document.WelcomeForm;
        var wizardBtn = f["Welcome.ManagerTableView.wizardWindow"];
        var editBtn = f["Welcome.ManagerTableView.EditButton"];
        var wizardClick = wizardBtn.onclick;
        var str = new String(wizardClick);
        str = str.substring(str.indexOf(":")+1, str.lastIndexOf(";")+1);
        var wizardClickNew = str.replace("?", "?SelectedContext=" + contextName);
        // Change window title for edit wizard 
        wizardClickNew = wizardClickNew.replace("APOC.wiz.title", "APOC.wiz.edit.title");
        wizardBtn.onclick=new Function ("evt", wizardClickNew);
        ccSetButtonDisabled("Welcome.ManagerTableView.EditButton", "WelcomeForm", true);
        wizardBtn.click();

    }
    
    function getSelectedContext(documentPara) {
        var viewLinkPos         = 0;
        var checkboxOptionPos   = 0;
        var selectedLink        = null;

        // set viewLinkPos to 1st View link (same row as 1st option box)
        while (documentPara.links[viewLinkPos]!=null) {
            if ( (documentPara.links[viewLinkPos].href.indexOf(".NameHref")!=-1) && 
                 (documentPara.links[viewLinkPos].href.indexOf(".")!=-1) ){
                break;
            }
            viewLinkPos++;
        }

        // set checkboxOptionPos to 1st option box link (same row as 1st view link)
        checkboxOptionPos = getNextCheckboxPos(documentPara, ".", checkboxOptionPos);

        // walk through all option boxes and ViewLinks in paralel
        while (documentPara.forms[0].elements[checkboxOptionPos]!=null) {
            if (documentPara.forms[0].elements[checkboxOptionPos].checked==true) {
                selectedLink = documentPara.links[viewLinkPos];
                break;
            }
            checkboxOptionPos = getNextCheckboxPos(documentPara, ".", checkboxOptionPos);
            viewLinkPos++;
        }
        return selectedLink;
    }   

    function getSelectedContextIds(documentPara, filterPara) {
        var viewLinkPos         = 0;
        var checkboxOptionPos   = 0;
        var selectedLink        = null;
        var selectedIds         = "";

        // set viewLinkPos to 1st View link (same row as 1st option box)
        while (documentPara.links[viewLinkPos]!=null) {
            if ( (documentPara.links[viewLinkPos].href.indexOf(".NameHref")!=-1) && 
                 (documentPara.links[viewLinkPos].href.indexOf(filterPara)!=-1) ){
                break;
            }
            viewLinkPos++;
        }

        // set checkboxOptionPos to 1st option box link (same row as 1st view link)
        checkboxOptionPos = getNextCheckboxPos(documentPara, filterPara, checkboxOptionPos);

        // walk through all option boxes and ViewLinks in paralel
        while (documentPara.forms[0].elements[checkboxOptionPos]!=null) {
            if (documentPara.forms[0].elements[checkboxOptionPos].checked==true) {
                // due to a bug in Lockhart jsessionids are sometimes inserted
                // they are removed here to not irritate the extract logic
                selectedLink = documentPara.links[viewLinkPos].href;
                nJsessionPos = selectedLink.indexOf("jsessionid");
                if (nJsessionPos>0) {
                    nSemiPos = selectedLink.indexOf(";");
                    nQuestPos= selectedLink.indexOf("?");
                    selectedLink = selectedLink.substring(0, nSemiPos) + 
                                   selectedLink.substring(nQuestPos, selectedLink.length);
                }
                selectedLink = extractByDelimiters("=", "&", selectedLink);
                if (selectedIds.length==0) {
                    selectedIds = selectedLink;
                }
                else {
                    selectedIds = selectedIds + " " + selectedLink;
                }
            }
            checkboxOptionPos = getNextCheckboxPos(documentPara, filterPara, checkboxOptionPos);
            viewLinkPos++;
        }

        return selectedIds;
    }    
    
    function handleSelection(selectedLink) {
        var willSubmit  = false;
        var f           = document.WelcomeForm;
        if (f != null) {
            if (selectedLink.name.indexOf("RemoveButton")!=-1) {
                // remove
                willSubmit = confirm('<cc:text name="RemoveMessage" bundleID="apocBundle" defaultValue="APOC.pool.removeContextMessage"/>');

                allowSubmit(willSubmit);
                return willSubmit;
                
            } else if (selectedLink.name.indexOf("RenameButton")!=-1) {
                // rename
                var contextName = getSelectedContext(document).text;
                    contextName = prompt('<cc:text name="RenameMessage" bundleID="apocBundle" defaultValue="APOC.pool.renameContextMessage"/>', unescape(contextName));

                if (contextName!=null) {
                    willSubmit=true;
                    f['Welcome.SelectedContext'].value=contextName;
                }

                allowSubmit(willSubmit);
                return willSubmit;

            } else if (selectedLink.name.indexOf("SyncButton")!=-1) {
                // sync
                var selectedContexts = getSelectedContextIds(document, "ManagerTableView");
                var leftContext = ""; 
                var rightContext = "";
                if (selectedContexts.indexOf(" ")>-1) {
                    leftContext = selectedContexts.substring(0, selectedContexts.indexOf(" "));
                    rightContext = selectedContexts.substring(selectedContexts.indexOf(" ")+1, selectedContexts.length);
                } else {
                    leftContext = selectedContexts;
                    rightContext = selectedContexts;
                }
                openWindow(window, null, '/apoc/manager/SyncLogin?SyncLogin.LeftContext='+leftContext+'&SyncLogin.RightContext='+rightContext, 'SyncWindow', 0.8, 0.5);

                allowSubmit(willSubmit)
                return willSubmit;

            }
        }
    }
    
    function toggleMenu() {
        var nElementRunner=0;
        var nElements=0;
        var nCheckedElements=0;
        var deleteButton    = "Welcome.ManagerTableView.RemoveButton";
        var renameButton    = "Welcome.ManagerTableView.RenameButton";
        var syncButton      = "Welcome.ManagerTableView.SyncButton";
        var editButton      = "Welcome.ManagerTableView.EditButton";
        while (document.WelcomeForm.elements[nElementRunner]!=null) {
            if ( (document.WelcomeForm.elements[nElementRunner].name.indexOf("SelectionCheckbox")!=-1)  &&
                 (document.WelcomeForm.elements[nElementRunner].name.indexOf("jato_boolean") == -1)) {
                nElements++;
                if (document.WelcomeForm.elements[nElementRunner].checked==true) {
                    nCheckedElements++;
                }
            }
            nElementRunner++;
        }
        
        // enable "delete" button if at least one profile is selected
        ccSetButtonDisabled(deleteButton, "WelcomeForm", (nCheckedElements==0));

        // enable "rename" button if exactly one profile is selected
        ccSetButtonDisabled(renameButton, "WelcomeForm", (nCheckedElements!=1));
        
        // enable "edit" button if exactly one profile is selected
        ccSetButtonDisabled(editButton, "WelcomeForm", (nCheckedElements!=1));
        
        // enable "sync" button item if one or two profiles are selected
        ccSetButtonDisabled(syncButton, "WelcomeForm", ((nCheckedElements!=1) && (nCheckedElements!=2)));
    }

    
// -->
</script>  

<a name="top" id="top"/>

<cc:form name="WelcomeForm" method="post" onSubmit="javascript:return isSubmitAllowed();">

    <cc:primarymasthead name="Masthead" bundleID="apocBundle"/>

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
    
    <cc:pagetitle name="ManagerTitle" 
        bundleID="apocBundle"
        pageTitleText="APOC.contexts.title"
        pageTitleHelpMessage="APOC.contexts.help"
        showPageTitleSeparator="true"
        showPageButtonsTop="false"
        showPageButtonsBottom="true"> 
        
    <br>

  
        <jato:containerView name="ManagerTableView">

            <cc:actiontable name="ContextTable"
                bundleID="apocBundle"
                title="APOC.contexts.table.title"
                summary="APOC.contexts.table.summary"
                empty="APOC.contexts.table.empty"
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

            <cc:hidden name="WizardLaunched"/>        
        </jato:containerView>   
        <br>
        <br>
    </cc:pagetitle> 
    <cc:hidden name="Anchor"/>
    <cc:hidden name="HelpLocale"/>
    <cc:hidden name="SelectedContext"/>

</cc:form>

<script type="text/javascript">
<!--
    setTimeout('toggleMenu()', 0);
// -->
</script> 

</cc:header>

</jato:useViewBean> 
