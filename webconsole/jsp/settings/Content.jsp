<%@ page info="Frameset" language="java" %> 
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

<%@taglib uri="/WEB-INF/tld/com_iplanet_jato/jato.tld" prefix="jato"%>
<%@taglib uri="/WEB-INF/tld/com_sun_web_ui/cc.tld" prefix="cc"%>

<jato:useViewBean className="com.sun.apoc.manager.PolicySettingsContentViewBean">

<!-- Header --> 
<cc:header name="Header"
  pageTitle="module3.title"
  copyrightYear="2004"
  baseName="com.sun.apoc.manager.resource.apoc_manager"
  bundleID="apocBundle"
  preserveScroll="true"
  preserveFocus="true"
  >
  
<script type="text/javascript" src="/apoc/js/ProfileWindow.js"></script>
<script type="text/javascript" src="/apoc/js/MainWindow.js"></script>
<script type="text/javascript">
    updateButtonsArea();
    <cc:text name="RefreshNavigationTreeScript"/>
    var invalidHex='<cc:text name="InvalidHex" />';
    var localizedNotSet = '<cc:text name="LocalizedNotSet" />';
</script>    
  
<cc:form name="Form" method="post" defaultCommandChild="SaveButton">  
  
<a name="top" id="top"></a>

<%-- Alert --%>
<jato:content name="AlertArea">
    <div class="ConMgn">                            
        <br/><cc:alertinline name="Alert" bundleID="apocBundle"/>
    </div>
</jato:content>
  
<cc:pagetitle name="PageTitle" bundleID="apocBundle"
  pageTitleText="APOC.profilewnd.settings.title"
  showPageTitleSeparator="true"
  pageTitleHelpMessage="APOC.profilewnd.settings.title.help"
  showPageButtonsTop="true"
  showPageButtonsBottom="false">  
  
<!-- Jump Links section -->
<apoc:JumpLinks name="JumpLinksSection"/>  
  
<!-- Policies Navigation Table -->
<jato:content name="PoliciesNavigationArea">
<br>
<a name="Policies"/>
<cc:actiontable
    name="PoliciesNavigationTable"
    bundleID="apocBundle"
    title="APOC.policies.title"
    summary="APOC.policies.tableSummary"
	selectionType="no select" 
    showAdvancedSortIcon="false"
    showLowerActions="false"
    showPaginationControls="false"
    showPaginationIcon="false"
    showSelectionIcons="false"
    maxRows="20"
    page="1"/>
</jato:content>  

<!-- Settings area -->
<apoc:template name="PolicyTemplateContent"/>

<input type="hidden" name="lastChange" value=""/>
<input type="hidden" name="UserInput" value=""/>
<input type="hidden" name="SaveAllPage" value=""/>
<script type="text/javascript">
//  BEGIN <---- Script and functions used for XMLHandlers ---->
<cc:text name="XmlHandlerText" />
//  END      <---- Script and functions used for XMLHandlers ---->
</script>
<cc:hidden name="DisabledFieldsValues" elementId="disabledFieldValues" />
<cc:hidden name="ProtectedChoosers" elementId="protectedChoosers"/>
<cc:hidden name="ProtectedChoosersType" elementId="protectedChoosersType"/>
<cc:hidden name="OriginalValues" elementId="originalValues" />
<cc:hidden name="HelpPage" elementId="HelpPage" />
<input type="hidden" name="com_sun_web_ui_popup" value="true" />
<script type="text/javascript">
    <!--
    updateValues(true);

    var prefix1 = "PolicySettingsContent.PolicyTemplateContent." ;
    var prefix2 = "].PropertyValue" ;
    var clearPrefix = "clear";
    var listPrefix  = "List";
    var elements = document.Form.elements;
    for(var i=0; i < elements.length; i++) {
       if ((elements[i].name.indexOf(prefix1) == 0) 
                && (elements[i].name.indexOf(prefix2) == (elements[i].name.length - prefix2.length))) {
                toggleOverwriteCheckbox(elements[i]);
       }
    } 
    toggleOverwriteLabel();
    
    function toggleSetButtons(checkbox) {
      var nElementRunner=0;
        var nElements=0;
        var nCheckedElements=0;
        
        while (document.Form.elements[nElementRunner]!=null) {
            if ((document.Form.elements[nElementRunner].name.indexOf("SelectionCheckbox")!=-1) && 
                    (document.Form.elements[nElementRunner].name.indexOf("jato_boolean") == -1)) {
                nElements++;
                if (document.Form.elements[nElementRunner].checked==true) {
                    nCheckedElements++;
                }
            }
            nElementRunner++;
        }
        var checkBoxName = checkbox.name;
        var deleteBtnName = checkBoxName.substring(0, checkBoxName.lastIndexOf('.'));
        deleteBtnName = deleteBtnName.substring(0, deleteBtnName.lastIndexOf('.'));
        deleteBtnName = deleteBtnName + ".DeleteButton";
        var renameBtnName = checkBoxName.substring(0, checkBoxName.lastIndexOf('.'));
        renameBtnName = renameBtnName.substring(0, renameBtnName.lastIndexOf('.'));
        renameBtnName = renameBtnName + ".RenameButton";
        ccSetButtonDisabled(deleteBtnName, "Form", nCheckedElements==0);
        ccSetButtonDisabled(renameBtnName, "Form", nCheckedElements!=1);
        return "";
    }
    
    function toggleOverwriteCheckbox(field) {
        var fieldName = field.name
        if (field.type == "radio") {
            if (field.checked == true) {
                if (field.value == localizedNotSet) {
                    fieldName = fieldName.substring(0, fieldName.lastIndexOf('.'));
                    fieldName = fieldName + ".Enforced";
                    document.Form[fieldName].style.display="none";                
                } else {
                    fieldName = fieldName.substring(0, fieldName.lastIndexOf('.'));
                    fieldName = fieldName + ".Enforced";
                    document.Form[fieldName].style.display="";            
                }
            }
        } else {
            if (field.value != localizedNotSet) {
                fieldName = fieldName.substring(0, fieldName.lastIndexOf('.'));
                fieldName = fieldName + ".Enforced";
                document.Form[fieldName].style.display="";
            } else {
                fieldName = fieldName.substring(0, fieldName.lastIndexOf('.'));
                fieldName = fieldName + ".Enforced";
                document.Form[fieldName].style.display="none";       
            }        
        }

        toggleOverwriteLabel();
        return "";
    }

    function toggleOverwriteCheckboxFromId(fieldName) {
        if (document.Form[fieldName].value != localizedNotSet) {
            fieldName = fieldName.substring(0, fieldName.lastIndexOf('.'));
            fieldName = fieldName + ".Enforced";
            document.Form[fieldName].style.display="";
        } else {
            fieldName = fieldName.substring(0, fieldName.lastIndexOf('.'));
            fieldName = fieldName + ".Enforced";
            document.Form[fieldName].style.display="none";       
        }        

        toggleOverwriteLabel();
        return "";
    }

    function toggleOverwriteLabel() {
        var sectionIds = new Array();    
        var allSectionIds = new Array(); 
        var elements = document.Form.elements;
        for(var i=0; i < elements.length; i++) {
            var fieldName = elements[i].name;
            if ((fieldName.indexOf(prefix1) == 0) 
                    && (fieldName.indexOf(prefix2) == (fieldName.length - prefix2.length))) {
                fieldName = fieldName.substring(0, fieldName.lastIndexOf('.'));
                fieldName = fieldName + ".Enforced";
                var sectionId = fieldName.substring(0, fieldName.indexOf("["));
                var noAddRequired = false;
                for (var j = 0; j < allSectionIds.length; j++) {
                    if (allSectionIds[j] == sectionId) {
                        noAddRequired = true;
                    }
                }
                if (noAddRequired == false) {
                    allSectionIds[allSectionIds.length] = sectionId;
                }
                if (document.Form[fieldName].style.display=="") {
                    var noAddRequired = false;
                    for (var j = 0; j < sectionIds.length; j++) {
                        if (sectionIds[j] == sectionId) {
                            noAddRequired = true;
                        }
                    }
                    if (noAddRequired == false) {
                        sectionIds[sectionIds.length] = sectionId;
                    }
                }
            }
        }
        for (var k = 0; k < allSectionIds.length; k++) {
           document.getElementById(allSectionIds[k] + ".overwriteLabelCol").style.display="none";
        }        
        for (var k = 0; k < sectionIds.length; k++) {
           document.getElementById(sectionIds[k] + ".overwriteLabelCol").style.display="";
        }
    }

    <cc:text name="AlertJavascript" escape="false"/>
    <cc:text name="JumpToScript"/>
    <cc:text name="CloseWindowScript"/>      
  //-->
</script>    
  
</cc:pagetitle>
</cc:form>
</cc:header>
</jato:useViewBean> 
