<%@ page info="Manager" language="java" %> 
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

<jato:useViewBean className="com.sun.apoc.manager.RemoteDesktopNavigationTreeViewBean">

    <cc:header  pageTitle="Browse Tree Title" copyrightYear="2005"
                baseName="com.sun.apoc.manager.resource.apoc_manager"
                bundleID="apocBundle" 
                isPopup="true">
                
                
         <script type="text/javascript" src="/apoc/js/MainWindow.js"></script>   

         <script type="text/javascript">
         <!-- 
            function getEntityId() {
                return '<cc:text name="SelectedEntity"/>' ;              
            }
         
            function getEntityType() {
                return '<cc:text name="SelectedEntityType"/>' ;              
            }
            
            function handleSelection(selectTag) {
                var form=document.BrowseTreeForm;
                if (form != null) {
                    form.action='/apoc/manager/RemoteDesktopNavigationTree?RemoteDesktopNavigationTree.DefaultHref=a&amp;';
                    form.submit();
                }
            }

            function entitySearchCallback(sHref) {
                var form        = document.BrowseTreeForm;
                var sEntityId   = extractByDelimiters("=", "&", sHref);
                form.action="/apoc/manager/RemoteDesktopNavigationTree?RemoteDesktopNavigationTree.DefaultHref="+sEntityId; 
                form.submit(); 
            }

         -->
        </script>

        <cc:form name="BrowseTreeForm" method="post" onSubmit="javascript:return false;">
               <br>
                <table width="80%" align=center>
                    <tr>
                        <td>
                            <table width='100%' align=center>
                                <tr>
                                    <td height='30'>
                                                <cc:button name="SearchButton"
                                                    bundleID="apocBundle" 
                                                    defaultValue="APOC.search.search"
                                                    type="secondary"
                                                    onClick="javascript:openSearchWindow(); return false;"/>
                                                    &nbsp;&nbsp;&nbsp;&nbsp;
 
                                    </td>
                                </tr>
                                <tr>
                                    <td height='100' valign='top' class='TreBdy'>
                                        <cc:dtree name="Tree" bundleID="apocBundle" />
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
        </cc:form>
    </cc:header>
</jato:useViewBean>  
