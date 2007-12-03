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

<jato:useViewBean className="com.sun.apoc.manager.SettingsSummaryViewBean">

<!-- Header -->
<cc:header pageTitle="APOC.configrep.title" copyrightYear="2003"
 baseName="com.sun.apoc.manager.resource.apoc_manager"
 bundleID="apocBundle" isPopup="true" >
<style type="text/css">
table.Tbl td.TblTdLytNoBrd {border-top:solid 0px #a8b2b6;border-right:solid 0px #a8b2b6;border-left:solid 0px #a8b2b6;border-bottom:solid 0px #a8b2b6;background-color:#E1E1E1}
table.Tbl td.TblTdLytBrdRgtTop {border-top:solid 0px #a8b2b6;border-right:solid 1px #a8b2b6;border-left:solid 0px #a8b2b6;border-bottom:solid 0px #a8b2b6;background-color:#E1E1E1}
a img {border-style:none} 
</style>
<script type="text/javascript" src="/apoc/js/MainWindow.js"></script>
<script type="text/javascript"><!-- Empty script so IE5.0 Windows will draw table and button borders --></script>

<!-- Page Title (Packages) -->
<cc:pagetitle name="PageTitle" bundleID="apocBundle"
 pageTitleText="APOC.report.settings.summary.title"
 pageTitleHelpMessage="APOC.profilewnd.summary.help"
 showPageTitleSeparator="true"
 showPageButtonsTop="true"
 showPageButtonsBottom="false"/>

<div class="TblMgn">
    <p><h2><jato:text name="NoSections" fireDisplayEvents="true"/></h2></p>
</div>

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
<tr><td>
    <jato:content name="DisplayTables">
        <br/>
        <br/>
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

                <!-- Beginning of table of settings display-->
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
                                    <cc:href name="ProfileValueHref" onClick="javascript:openProfileEditorWindowAtSection(this); return false;">
                                        <span>
                                            <cc:text name="StatusValue" bundleID="apocBundle"/>
                                        </span>
                                    </cc:href>
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


</p>
</cc:header>
</jato:useViewBean> 


