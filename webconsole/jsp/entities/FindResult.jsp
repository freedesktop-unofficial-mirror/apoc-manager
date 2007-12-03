<%@ page info="Navigator" language="java" %> 
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

    <jato:useViewBean className="com.sun.apoc.manager.EntitiesFindResultViewBean">
    
    <cc:header pageTitle="Find" copyrightYear="2003"
        baseName="com.sun.apoc.manager.resource.apoc_manager"
        bundleID="apocBundle"
        isPopup="true">

        <cc:form name="ResultForm" method="post" defaultCommandChild="FindButton">

            <jato:content name="Help">

                <div class="content-layout">
                    <BR>
                    <cc:text name="HelpText1" defaultValue="APOC.search.help.text1" bundleID="apocBundle"/>
                    <BR><BR>
                    <cc:text name="HelpText2" defaultValue="APOC.search.help.text2" bundleID="apocBundle"/>
                </div>

            </jato:content>

            <jato:content name="EntitiesFound">
                <br><br>
                <table width='100%' cellspacing='0' cellpadding='0'>
                    <tr>
                        <td width='2%'><br></td>
                        <td width='96%'>
                            <table width='100%' cellspacing='0' cellpadding='0'>
                                <jato:tiledView name="EntitiesFindTiledView">
                                    <tr>
                                        <td style='border-right:solid 1px #D8D8D8; padding: 1px 4px 1px 4px'>
                                            <cc:href name="Result1Href" onClick="javascript:parent.opener.focus(); parent.opener.entitySearchCallback(this.href); top.window.close(); return false;">
                                                <cc:text name="Result1Text"/>
                                            </cc:href>
                                        </td>
                                        <td style='border-right:solid 1px #D8D8D8; padding: 1px 4px 1px 4px'>
                                            <cc:href name="Result2Href" onClick="javascript:parent.opener.focus(); parent.opener.entitySearchCallback(this.href); top.window.close(); return false;">
                                                <cc:text name="Result2Text"/>
                                            </cc:href>
                                        </td>
                                        <td style='padding: 1px 4px 1px 4px'>
                                            <cc:href name="Result3Href" onClick="javascript:parent.opener.focus(); parent.opener.entitySearchCallback(this.href); top.window.close(); return false;">
                                                <cc:text name="Result3Text"/>
                                            </cc:href>
                                        </td>
                                    </tr>
                                </jato:tiledView>
                            </table>
                        </td>
                        <td width='2%'><br></td>
                    </tr>
                </table>
            </jato:content>

            <jato:content name="NoEntitiesFound">
                <br><br>
                <center><b><cc:text name="NoneFoundMessage" defaultValue="APOC.policies.set.table.empty" bundleID="apocBundle"/></b></center>
            </jato:content>

            <cc:hidden name="EntityId" />
            <cc:hidden name="EntityType" />
            <cc:hidden name="FindString" />
        
        </cc:form>

    </cc:header>

</jato:useViewBean> 
