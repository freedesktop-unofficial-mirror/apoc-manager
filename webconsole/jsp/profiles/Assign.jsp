<%@ page info="Assign" language="java" %> 
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

<jato:useViewBean className="com.sun.apoc.manager.ProfilesAssignViewBean">

    <cc:header  pageTitle="APOC.pool.add_assignment" copyrightYear="2003"
                baseName="com.sun.apoc.manager.resource.apoc_manager"
                bundleID="apocBundle" isPopup="true" onLoad="javascript:submitAndClose();">

<script type="text/javascript" src="/apoc/js/MainWindow.js"></script>

<script type="text/javascript">
<!--
    var m_OpenerTop = top.opener;
    while (m_OpenerTop.top.opener!=null) {
        m_OpenerTop = m_OpenerTop.top.opener;
    }
    m_OpenerTop = m_OpenerTop.top;

    setInterval("setWindowHandle()", 1000);
    function setWindowHandle() {
        m_OpenerTop.m_AssignWindow= top;
    }
        
    function toggleMenu() {
        var nElementRunner  =0;
        var nCheckedElements=0;
        var okButton        = "ProfilesAssign.OkButton";
        
        while (document.AssignForm.elements[nElementRunner]!=null) {
            if ( (document.AssignForm.elements[nElementRunner].name.indexOf("SelectionCheckbox")!=-1) && 
                 (document.AssignForm.elements[nElementRunner].name.indexOf("jato_boolean") == -1) &&
                 (document.AssignForm.elements[nElementRunner].checked==true) ) {
                nCheckedElements++;
            }
            nElementRunner++;
        }

        // enable "ok" button if at least one profile is selected
        ccSetButtonDisabled(okButton, "AssignForm", (nCheckedElements==0));
    }

    function initSubmit() {
        document.AssignForm.action='../manager/ProfilesAssign?ProfilesAssign.AssignTableView.OkActionHref=a&amp;';
        document.AssignForm.submit();
    }
        
    function submitAndClose() {
        <cc:text name="SubmitAndClose"/>
        opener.document.EntityContentForm.action="../manager/EntityContent?EntityContent.DefaultHref=a";
        opener.document.EntityContentForm.submit();
        window.close();
    }
    
// -->
</script> 

        <cc:secondarymasthead name="Masthead" bundleID="apocBundle"
             src="/apoc/images/popuptitle.gif"/>

        <cc:form name="AssignForm" method="post" onSubmit="javascript:return isSubmitAllowed();" defaultCommandChild='DefaultHref'>

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

            <cc:pagetitle name="AssignTitle" 
                bundleID="apocBundle"
                pageTitleText="APOC.profileavail.title"
                pageTitleHelpMessage="APOC.profileavail.help"
                showPageTitleSeparator="true"
                showPageButtonsTop="true"
                showPageButtonsBottom="true"> 

                <BR>
                
                <jato:containerView name="AssignTableView">

                    <cc:actiontable name="ProfileTable"
                        bundleID="apocBundle"
                        title="APOC.pool.available_profiles"
                        summary="APOC.pool.tableSummary"
                        empty="APOC.pool.empty"
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

                </jato:containerView>

            </cc:pagetitle> 
            </cc:form>

        <script type="text/javascript">
        <!--
            setTimeout('toggleMenu()', 0);
            // -->
        </script> 

    </cc:header>
</jato:useViewBean>  
