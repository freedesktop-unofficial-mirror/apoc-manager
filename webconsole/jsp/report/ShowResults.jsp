<%@ page info="ConfigureReport" language="java" %> 
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
<jato:useViewBean className="com.sun.apoc.manager.ShowResultsViewBean">

<!-- Header -->
<cc:header pageTitle="" copyrightYear="2005"
 baseName="com.sun.apoc.manager.resource.apoc_manager"
 bundleID="apocBundle" onLoad="javascript:beginDisplay(); return false;" isPopup="true" >
<style type="text/css">
table.Tbl td.TblTdLytNoBrd {border-top:solid 0px #a8b2b6;border-right:solid 0px #a8b2b6;border-left:solid 0px #a8b2b6;border-bottom:solid 0px #a8b2b6;background-color:#E1E1E1}
table.Tbl td.TblTdLytBrdRgtTop {border-top:solid 0px #a8b2b6;border-right:solid 1px #a8b2b6;border-left:solid 0px #a8b2b6;border-bottom:solid 0px #a8b2b6;background-color:#E1E1E1}
a img {border-style:none} 
</style>

<script type="text/javascript" src="/apoc/js/MainWindow.js"></script>
<script type="text/javascript" src="/apoc/js/ProfileWindow.js"></script>
<script type="text/javascript"><!-- Empty script so IE5.0 Windows will draw table and button borders --></script>

<cc:form name="ConfigrepForm" method="post">
<script type="text/javascript">
<!--
    var IMAGE_PREFIX = "image.";
    var TURNER_PREFIX = "turner.";
    var TABLE1 = "table1";
    var TABLE2 = "table2";
    


    function submitBrowse(entityId, entityType, entityPath) {
        if(entityType == 'USER') {
            document.ConfigrepForm['ShowResults.OrgEntity'].value = entityId;
        } else if(entityType == 'HOST') {
            document.ConfigrepForm['ShowResults.DomEntity'].value = entityId;        
        } 
        ccSetButtonDisabled('ShowResults.ReportButton', "ConfigrepForm", false);
        var displayPath = formatEntityPath(entityPath);
        updateMergeOrder(entityType, displayPath);
    }

    function formatEntityPath(entityPath) {
        var baseEntity = "";
        var topEntity = "";
        if (entityPath.indexOf("&gt;") != -1) {
            var entities = entityPath.split(" &gt; ");
            baseEntity = entities[entities.length-1];
            topEntity = entities[0];
            return baseEntity + " (" + topEntity + ")";
        } else {
            baseEntity = entityPath;
            topEntity = entityPath;
            return baseEntity + " (" + topEntity + ")";
        }
    }
    
    function updateMergeOrder(entityType, entityPath) {
        var mergeOrder = document.ConfigrepForm['ShowResults.MergeOrder'];
        if (mergeOrder.value == null || mergeOrder.value.length == 0) {
            mergeOrder.value += entityPath + "|" + entityType;
        } else {
            var mergeOrderArray = mergeOrder.value.split("|");
            mergeOrder.value = "";
            var isReplace = false;
            for (var i=0; i < mergeOrderArray.length; i += 2) {
                if (mergeOrderArray[i+1] == entityType) {
                    mergeOrderArray[i] = entityPath;
                    isReplace = true;
                }
                if (i == 0) {
                    mergeOrder.value += mergeOrderArray[i] + "|" + mergeOrderArray[i+1];
                } else {
                    mergeOrder.value += "|" + mergeOrderArray[i] + "|" + mergeOrderArray[i+1];
                }
            }
            if (!isReplace) {
                if (mergeOrder.value.length == 0) {
                    mergeOrder.value += entityPath + "|" + entityType;
                } else {
                    mergeOrder.value += "|" + entityPath + "|" + entityType;
                }                
            }
            
        }
        updateElementsList();
    }
    
    function printPage() {
        window.print();
    }


     
    function beginDisplay() {
   /*     var table = document.getElementById(TABLE2);
        if (table != null) {
            for(var i=0; i < table.rows.length; i++) {
                if (table.rows[i].id != null && table.rows[i].id.length != 0) {
                    var childId = table.rows[i].id;
                    var arr = childId.split(".");
                    var style2 = document.getElementById(childId).style;
                    var childImage  = document.getElementById(IMAGE_PREFIX + childId);
                    if (arr == null || arr.length <= 2) {
                        style2.display ='';
                        if (childImage != null && arr.length==2) {
                            setImage(childImage, "grouprow_collapsed.gif");
                        }
                    } else {
                        style2.display="none";
                    }
                }
            }
        }*/
        return false;
    }

    function showhideprofiles(element) {
        var id = element.id.substring(TURNER_PREFIX.length, element.id.length);
        var table = document.getElementById(TABLE1);
        for(var i=0; i < table.rows.length; i++) {
            if (table.rows[i].id != null && table.rows[i].id.length != 0) {
                var childId = table.rows[i].id;
                if (childId != id && childId.indexOf(id) == 0) {
                    var style2 = document.getElementById(childId).style;
                    if (style2.display == "none") {
                        style2.display = '';
                    } else {
                        style2.display = "none";
                    }
                }
            }
        }
        switchImage(document.getElementById(IMAGE_PREFIX+id));
        return false;
    }

    function showhide(element) {
        var id = element.id.substring(TURNER_PREFIX.length, element.id.length);
        var action = getAction(document.getElementById(IMAGE_PREFIX+id));
        if (action == "hide") {
            hide(id);
        } else {
            show(id, action);
        }
        switchImage(document.getElementById(IMAGE_PREFIX+id));
        return false;
    }

    function hide(id) {
        var table = document.getElementById(TABLE2);
        for(var i=0; i < table.rows.length; i++) {
            if (table.rows[i].id != null && table.rows[i].id.length != 0) {
                var childId = table.rows[i].id;
                if (childId != id && childId.indexOf(id) == 0) {
                    var style2 = document.getElementById(childId).style;
                        style2.display ="none";
                }
            }
        }
        return false;
    }

    function show(id ,action) {
        var table = document.getElementById(TABLE2);
        var children = getChildren(id); 
        for (var i= 0; i < children.length; i++) {
            var childId  = children[i];
            var style2 = document.getElementById(childId).style;
            if(action == 'show'){
                style2.display ='';
            }else{
                style2.display ="none";
            } 
            var childElement = document.getElementById(IMAGE_PREFIX+childId);
            if (childElement != null) {
                childAction = getAction(childElement);
                // SL TODO - CHANGE THIS TO SOMETHING BETTER
                //The getAction() method is used to decide whether to show or hide 
                // the row based on the arrow images position - here we want to 
                // know whether to show or hide a child row using the direct parents
                // arrow image position so we actually want the inverse of getAction()
                if(childAction == "show"){
                    childAction = "hide";
                } else {
                    childAction = "show";
                }
                id = childId;
                show(id, childAction);
            }
        }
        return false;
    }

    function getChildren(id) {
        var prefix  = id + ".";
        var children = new Array();
        var table = document.getElementById(TABLE2);
        for(var i=0; i < table.rows.length; i++) {
            if (table.rows[i].id != null && table.rows[i].id.length != 0) {
                var childId = table.rows[i].id;
                if (childId.indexOf(prefix) == 0 && childId != id) {
                    var suffix = childId.substring(prefix.length, childId.length);
                    if (suffix.indexOf(".") == -1) {
                        children.push(childId);
                    }
                }
            }
        }
        return children;
    }

    function getAction(element) {
        var stem = element.src.substring(0, element.src.lastIndexOf("/")+1);
        var currentImage = element.src.substring(element.src.lastIndexOf("/")+1, element.src.length);
        if (currentImage=="grouprow_collapsed.gif") {
            return "show";
        } else {
            return "hide";
        }
    }

    function switchImage(element) {
        var stem = element.src.substring(0, element.src.lastIndexOf("/")+1);
        var currentImage = element.src.substring(element.src.lastIndexOf("/")+1, element.src.length);
        if (currentImage=="grouprow_collapsed.gif") {
            element.src = stem + "grouprow_expanded.gif";        
        } else {
            element.src= stem + "grouprow_collapsed.gif";       
        }
        return false;
    }

    function setImage(element, image) {
        var stem = element.src.substring(0, element.src.lastIndexOf("/")+1);
        var currentImage = element.src.substring(element.src.lastIndexOf("/")+1, element.src.length);
        element.src = stem + image;
        return false;
    }
    
    function openBrowseTreeWindow() {
        openWindow(window, null, '/apoc/manager/BrowseTreeIndex?ReturnPath=true', 'TreeWindow', 500, 600, true); 
        return false;
    }
    
    function handleListSelect() {
        var list  = document.ConfigrepForm['ShowResults.ElementsInvolvedList'];

        // if only the list width maintainer is present then only allow add
        if (list.options.length == 1) {
            ccSetButtonDisabled("ShowResults.RemoveButton", "ConfigrepForm", true);
            ccSetButtonDisabled("ShowResults.UpButton", "ConfigrepForm", true); 
            ccSetButtonDisabled("ShowResults.DownButton", "ConfigrepForm", true);
            ccSetButtonDisabled("ShowResults.ReportButton", "ConfigrepForm", true);
            list.options[0].selected = false;
        } else {
            ccSetButtonDisabled('ShowResults.ReportButton', "ConfigrepForm", false);
            ccSetButtonDisabled('ShowResults.RemoveButton', "ConfigrepForm", (list.selectedIndex == -1) || (list.options[list.options.length-1].selected));
            var index = 0;
            for (var i=0; i < list.options.length; i++) {
                if (list.options[i].selected) {
                    index++;
                }
            }        
            if (list.options[list.options.length-1].selected || 
                    list.options[0].selected ||
                        list.selectedIndex == -1 ||
                            index > 1) {
                ccSetButtonDisabled('ShowResults.UpButton', "ConfigrepForm", true);
            } else {
                ccSetButtonDisabled('ShowResults.UpButton', "ConfigrepForm", false);
            }
            if (list.options[list.options.length-1].selected || 
                    list.options[list.options.length-2].selected ||
                        list.selectedIndex == -1 ||
                            index > 1) {
                ccSetButtonDisabled('ShowResults.DownButton', "ConfigrepForm", true);
            } else {
                ccSetButtonDisabled('ShowResults.DownButton', "ConfigrepForm", false);
            }
        }
    }
    
    function updateElementsList() {
        var list  = document.ConfigrepForm['ShowResults.ElementsInvolvedList'];
        var mergeOrder = document.ConfigrepForm['ShowResults.MergeOrder'];
        var elements = mergeOrder.value.split("|");
        list.options.length = 0;
        var index = 0;
        for (var i=0; i < elements.length; i += 2) {
            list.options[index] = new Option(elements[i], elements[i+1]);
            index++;
        }
        list.options[list.options.length] = new Option(LIST_WIDTH_MAINTAINER, "Not Valid");
    }
    
    function addElement() {
        openBrowseTreeWindow();
        return false;
    }
    
    function removeElement() {
        var mergeOrder = document.ConfigrepForm['ShowResults.MergeOrder'];       
        var list  = document.ConfigrepForm['ShowResults.ElementsInvolvedList'];

        var index = 0;
        var updatedOptions  = new Array();
        for (var i=0; i < list.options.length-1; i++) {
            if (!list.options[i].selected) {
                updatedOptions[index] = new Option(list.options[i].text, list.options[i].value) ;
                index++;
            }
        }
        list.options.length = 0;
        for (var i=0; i < updatedOptions.length; i++) {
            list.options[i] = new Option(updatedOptions[i].text, updatedOptions[i].value) ;
        }
        mergeOrder.value  = "" ;  
        for (var i=0; i < list.options.length; i++) {
            if (i == 0) {
                mergeOrder.value += list.options[i].text + "|" + list.options[i].value;
            } else {
                mergeOrder.value += "|" + list.options[i].text + "|" + list.options[i].value;
            }
        }          
        list.options[list.options.length] = new Option(LIST_WIDTH_MAINTAINER, "Not Valid");
        handleListSelect();
        return false;
    }   
    
    function moveUpElement() {
        var mergeOrder = document.ConfigrepForm['ShowResults.MergeOrder'];       
        var list  = document.ConfigrepForm['ShowResults.ElementsInvolvedList'];
        for (var i=0; i < list.options.length; i++) {
            if (list.options[i].selected) {
                var tempOption = new Option(list.options[i-1].text, list.options[i-1].value);
                list.options[i-1] = new Option(list.options[i].text, list.options[i].value);
                list.options[i] = new Option(tempOption.text, tempOption.value);
                list.options[i-1].selected = true;
                handleListSelect();
                break;
            }
        }
        mergeOrder.value  = "" ;     
        for (var i=0; i < list.options.length-1; i++) {
            if (i == 0) {
                mergeOrder.value += list.options[i].text + "|" + list.options[i].value;
            } else {
                mergeOrder.value += "|" + list.options[i].text + "|" + list.options[i].value;
            }
        }     
        return false;
    } 

    function moveDownElement() {
        var mergeOrder = document.ConfigrepForm['ShowResults.MergeOrder'];       
        var list  = document.ConfigrepForm['ShowResults.ElementsInvolvedList'];
        for (var i=0; i < list.options.length; i++) {
            if (list.options[i].selected) {
                var tempOption = new Option(list.options[i+1].text, list.options[i+1].value) ;
                list.options[i+1] = new Option(list.options[i].text, list.options[i].value);
                list.options[i] = new Option(tempOption.text, tempOption.value);
                list.options[i+1].selected = true;
                handleListSelect();
                break;
            }
        }
        mergeOrder.value  = "" ;     
        for (var i=0; i < list.options.length-1 ; i++) {
            if (i == 0) {
                mergeOrder.value += list.options[i].text + "|" + list.options[i].value;
            } else {
                mergeOrder.value += "|" + list.options[i].text + "|" + list.options[i].value;
            }
        }        
        return false;
    }    
-->
</script>

      
    <!-- Masthead -->
    <cc:secondarymasthead name="SecondaryMH" bundleID="apocBundle" />

    <!-- Page Title (Packages) -->
    <cc:pagetitle name="PageTitle" bundleID="apocBundle"
     pageTitleText="APOC.configrep.title"
     pageTitleHelpMessage="APOC.configrep.help"
     showPageTitleSeparator="true"
     showPageButtonsTop="true"
     showPageButtonsBottom="false">
    <br>

    <table width="90%" id="propertyTable" align="center" border="0" cellpadding="0" cellspacing="5">
        <tr>
            <td align="left" colspan="2">
                <cc:label name="ElementsInvolvedListLabel" styleLevel="2"
                        elementName="ElementsInvolvedListLabel"
                        bundleID="apocBundle"/>
           </td>
        </tr>
        <tr>           
           <td align="left" width="30%" rowspan="5">
                <cc:selectablelist name="ElementsInvolvedList"
                        bundleID="apocBundle" title="APOC.report.elements.table.title"
                        onChange="handleListSelect(); return false;"
                        dynamic="true"
                        multiple="true"
                        size="7"
                />
            </td>
            <td align="left" valign="top">            
                <cc:button  name="AddButton"
                    bundleID="apocBundle" 
                    dynamic="true"
                    defaultValue="APOC.report.add.element"
                    type="primary"
                    onClick="addElement() ;return false;"
                />
            </td>
        </tr>
        <tr>
            <td align="left" valign="top">
                <cc:button  name="RemoveButton"
                    bundleID="apocBundle"
                    dynamic="true"
                    defaultValue="APOC.report.remove.element"
                    type="primary"
                    onClick="removeElement(); return false;"
                />
            </td>
        </tr>
        <tr>
            <td align="left" valign="top">
                <br/>
            </td>
        </tr>
        <tr>
            <td align="left" valign="top">
                <br/>
            </td>
        </tr>
        <tr>
            <td align="left" valign="top">
                <br/>
            </td>
        </tr>
    </table>
        
                        
            

    </cc:pagetitle>

    <!-- Hidden Fields -->
    <cc:hidden name="OrgEntity" />
    <cc:hidden name="DomEntity" />
    <cc:hidden name="MergeOrder" />
<table border="0" width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td colspan="3">
            <img src="/com_sun_web_ui/images/other/dot.gif" alt="" border="0" height="30" width="1" />
        </td>
    </tr>
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
<br>
<br>

<jato:content name="DisplayReport">

<!-- The profiles contributing to this report -->
<p>
    <div class="TtlTxtDiv">
        <h1><cc:text name="ReportInfo" bundleID="apocBundle" /></h1>
    </div>
</p>
<p>
    <div class="TtlTxtDiv">
        <h2><cc:text name="CreationInfo" bundleID="apocBundle"/></h2>
    </div>
</p>

<p>
    <div class="TtlTxtDiv">
        <h2><jato:text name="NoSections" fireDisplayEvents="true"/></h2>
    </div>
</p>
<br/>
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
<br/>    
  
<table width="95%" border="0" align="center" cellpadding="0" cellspacing="0">
<tr>
    <td>
        <cc:actiontable name="ElementsInvolvedTable"
            bundleID="apocBundle"
            title="APOC.report.elements.table.heading"
            summary="APOC.report.elements.table.summary"
            maxRows="5"
            showAdvancedSortIcon="false"
            showLowerActions="false"
            showPaginationControls="false"
            showPaginationIcon="false"
            showSelectionIcons="false"
            showSelectionSortIcon="false"
            page="1" />
    </td>
</tr>
</table>

<br/> 
<br/> 
<table width="95%" border="0" align="center" cellpadding="0" cellspacing="0">
<tr><td>
    <jato:content name="DisplayProfilesTable">
    <div class="TblMgn">
        <table name="table1" id="table1" class="Tbl" style="border:solid 1px #a8b2b6;" cellpadding="0" cellspacing="0">
            <caption class="TblTtlTxt" style="border-left:solid 0px;border-right:solid 0px;">
                <cc:text name="ProfilesInvolvedTitle" bundleID="apocBundle"/>
            </caption>
            <tr id="0">
                <th class="TblColHdrCl1" align="left" scope="col">
               <!--     <a id="turner.0" href="" onClick="showhideprofiles(this); return false;">
                        <img id="image.0" src="/apoc/images/grouprow_expanded.gif" alt="Click to Collapse Group" width="10" height="11" />
                    </a> -->
                    <span class="TblHdrTxt">
                        <cc:text name="EntityInvolvedTitle" bundleID="apocBundle"/>
                    </span>
                </th>
            </tr>
            <tr id="0.1">
                <td class="TblTdLyt" align="left">
                    <cc:text name="ProfilesInvolved" bundleID="apocBundle"/>
                </td>
            </tr> 
        </table>
    </div>
    </jato:content>
        <br/>
        <br/>
        <jato:content name="DisplayTables">            
        <div class="TblMgn">
            <table name="table2" id="table2" class="Tbl" style="border:solid 1px #a8b2b6;" cellpadding="0" cellspacing="0">
                <caption class="TblTtlTxt" style="border-left:solid 0px;border-right:solid 0px;">
                    <cc:text name="SettingsSummaryTitle" bundleID="apocBundle"/>
                </caption>
                <tr>
                    <th class="TblColHdrCl1" align="center" scope="col" width="6%" colspan="2" >
                         <span class="TblHdrTxt">
                        </span>
                    </th>
                    <th id="nameCol" class="TblColHdr" align="left" scope="col" width="32%">
                        <table class="TblHdrTbl" cellpadding="0" cellspacing="0">
                            <tr>
                                <td align="left">
                                    <span class="TblHdrTxt">
                                        <cc:text name="NameHeading" bundleID="apocBundle"/>
                                    </span>
                                </td>
                            </tr>
                        </table>
                    </th>
                    <th id="valueCol" class="TblColHdr" align="left" scope="col" width="32%">
                        <table class="TblHdrTbl" cellpadding="0" cellspacing="0">
                            <tr>
                                <td align="left">
                                    <span class="TblHdrTxt">
                                        <cc:text name="ValueHeading" bundleID="apocBundle"/>
                                    </span>
                                </td>
                            </tr>
                        </table>
                    </th>
                    <th id="statusCol" class="TblColHdr" align="left" scope="col" width="30%" nowrap="nowrap">
                        <table class="TblHdrTbl" cellpadding="0" cellspacing="0">
                            <tr>
                                <td align="left">
                                    <span class="TblHdrTxt">
                                        <cc:text name="StatusHeading" bundleID="apocBundle"/>
                                    </span>
                                </td>
                            </tr>
                        </table>
                    </th>
                </tr>

                <!-- Beginning of table of settings display -->
                <jato:tiledView name="CategoryTiledView" fireChildDisplayEvents="true">    
                    <tr id='<cc:text name="RowID" bundleID="apocBundle"/>'>
                        <th class="TblColHdrCl1" align="left" scope="colgroup" colspan="5">
                         <!--   <a id='<cc:text name="TurnerID" bundleID="apocBundle"/>' href="" onClick="showhide(this); return false;">
                                <img id='<cc:text name="ImageID" bundleID="apocBundle"/>' src="/apoc/images/grouprow_expanded.gif" alt="Click to Collapse Group" width="10" height="11" />
                            </a>   -->
                            <span class="TblHdrTxt">
                                <cc:text name="Category" bundleID="apocBundle"/>
                            </span>
                        </th>
                    </tr>
                    <jato:tiledView name="SectionTiledView" fireChildDisplayEvents="true">    
                        <tr id='<cc:text name="RowID" bundleID="apocBundle"/>'>
                            <td class="TblTdLytBrdRgtTop" width="3%" nowrap="nowrap">
                            </td>
                            <th class="TblColHdrCl1" align="left" scope="colgroup" colspan="4">
                            <!--    <a id='<cc:text name="TurnerID" bundleID="apocBundle"/>' href="" onClick="showhide(this); return false;">
                                    <img id='<cc:text name="ImageID" bundleID="apocBundle"/>' src="/apoc/images/grouprow_expanded.gif" alt="Click to Collapse Group" width="10" height="11" />
                                </a> -->
                                <span class="TblHdrTxt">
                                    <cc:text name="Section" bundleID="apocBundle"/>
                                </span>
                            </th>
                        </tr>
                        <jato:tiledView name="PropertyTiledView" fireChildDisplayEvents="true">    
                            <tr id='<cc:text name="RowID" bundleID="apocBundle"/>' >
                                <td class="TblTdLytNoBrd" align="left" width="3%"><span></span></td>
                                <td class="TblTdLytBrdRgtTop" align="left" width="3%"><span></span></td>
                                <td class="TblTdLyt" align="left">
                                    <span>
                                        <cc:text name="NameValue" bundleID="apocBundle"/>
                                    </span>
                                </td>
                                <td class="TblTdLyt" align="left">
                                    <span>
                                        <cc:text name="ValueValue" bundleID="apocBundle"/>
                                    </span>
                                </td>
                                <td class="TblTdLyt" align="left">
                                    <span>
                                        <cc:text name="StatusValue" bundleID="apocBundle"/>
                                    </span>
                                    <span>
                                        <cc:href name="ProfileValueHref" onClick="javascript:openProfileEditorWindowAtSection(this); return false;">
                                            <cc:text name="ProfileValue" bundleID="apocBundle"/>
                                        </cc:href>                                
                                    </span>
                                    <br/>
                                    <span>
                                        <cc:text name="StatusValueProtection" bundleID="apocBundle"/>
                                    </span>
                                </td>
                            </tr>
                        </jato:tiledView>
                    </jato:tiledView>
                </jato:tiledView>   
            </table>
            <p>&nbsp;</p>
            <p>&nbsp;</p>
        </div>
    </jato:content>
</td></tr>
</table>
</jato:content>
<script type="text/javascript">
<!--
    var LIST_WIDTH_MAINTAINER = "___________________________________";
    var windowTitle = '<cc:text name="WindowTitle" bundleID="apocBundle"/>';
    if (windowTitle.length == 0 ) {
        windowTitle = '<cc:text name="WindowTitle" defaultValue="APOC.configrep.title" bundleID="apocBundle"/>'
    }
    parent.document.title = windowTitle;
    
    ccSetButtonDisabled("ShowResults.RemoveButton", "ConfigrepForm", true);
    ccSetButtonDisabled("ShowResults.UpButton", "ConfigrepForm", true); 
    ccSetButtonDisabled("ShowResults.DownButton", "ConfigrepForm", true);
    updateElementsList();
    
//-->
</script>
<input type="hidden" name="com_sun_web_ui_popup" value="true" />
</cc:form>
</cc:header>
</jato:useViewBean> 

