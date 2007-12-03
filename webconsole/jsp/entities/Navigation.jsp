<%@ page info="Navigation" language="java" %> 
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

    <jato:useViewBean className="com.sun.apoc.manager.EntitiesNavigationViewBean">

    <cc:header  pageTitle="APOC.navigation.area" copyrightYear="2003"
                baseName="com.sun.apoc.manager.resource.apoc_manager"
                bundleID="apocBundle"
                styleClass="TreWhtBdy"
                preserveScroll="true"
                onLoad="javascript:view()">

        <script type="text/javascript" src="/apoc/js/MainWindow.js"></script>   

        <script type="text/javascript">
        <!--

            domain          = new Image();
            host            = new Image();
            org             = new Image();
            role            = new Image();
            user            = new Image();
            domainAssigned  = new Image();
            hostAssigned    = new Image();
            orgAssigned     = new Image();
            roleAssigned    = new Image();
            userAssigned    = new Image();
            domain.src          = "../images/domain.png";
            host.src            = "../images/host.png";
            org.src             = "../images/org.png";
            role.src            = "../images/role.png";
            user.src            = "../images/user.png";
            domainAssigned.src  = "../images/domainAssigned.png";
            hostAssigned.src    = "../images/hostAssigned.png";
            orgAssigned.src     = "../images/orgAssigned.png";
            roleAssigned.src    = "../images/roleAssigned.png";
            userAssigned.src    = "../images/userAssigned.png";

            function switchImage(nodeId,isAssigned) {
                var imageNode;

                if (nodeId<0) {
                    // change image of currently highlighted node
                    var imgNodes = document.getElementsByName("EntitiesNavigation.Tree.ChildImage");
                    var imgNode  = null;

                    for(nodeRunner = 0; nodeRunner < imgNodes.length; nodeRunner++) { 
                        imgNode     = imgNodes[nodeRunner];
                        srcAttribute= imgNode.getAttribute("src");
                        if ( (srcAttribute.indexOf("com_sun_web_ui")<0) && (imgNode.parentNode.parentNode.className=="TreSelRow") ) {
                            imageNode = imgNode;
                            break;
                        }
                    }
                } else {
                    // change image of any other node
                    var hrefNodes = document.getElementsByName("EntitiesNavigation.Tree.NodeHref");
                    var hrefNode  = null;

                    for(nodeRunner = 0; nodeRunner < hrefNodes.length; nodeRunner++) { 
                        hrefNode     = hrefNodes[nodeRunner];
                        hrefAttribute= hrefNode.getAttribute("href");

                        if (hrefAttribute.indexOf("EntitiesNavigation.Tree.NodeHref="+nodeId)>-1) {
                            imageNode = hrefNode.firstChild;
                            break;
                        }
                    }
                }

                if (imageNode!=null) {
                    var assignedPos = imageNode.src.indexOf("Assigned");
                    var newSrc;
                    if ((assignedPos>0) && (isAssigned!=true)) {
                        newSrc = imageNode.src.substring(0,assignedPos);
                        newSrc = newSrc+imageNode.src.substring(assignedPos+8,imageNode.src.length);
                        imageNode.src = newSrc;
                    } else if ((assignedPos<0) && (isAssigned==true)) {
                        dotPos = imageNode.src.lastIndexOf(".");
                        newSrc = imageNode.src.substring(0,dotPos)+"Assigned";
                        newSrc = newSrc+imageNode.src.substring(dotPos,imageNode.src.length);
                        imageNode.src = newSrc;
                    }
                }
            }

            function view() {
                <cc:text name="ContentConditionalReload" defaultValue="" />
            }
            
            function entitySearchCallback(sHref) {
                submitNavigationArea(sHref);
            }

            function submitNavigationArea(sHref) {
                var form        = document.EntitiesNavigationForm;
                var sEntityId   = extractByDelimiters("=", "&", sHref);
                form.action="/apoc/manager/EntitiesNavigation?EntitiesNavigation.DefaultHref="+sEntityId; 
                form.submit(); 
            }
            
            function handleKeys() {
                if  (window.event && window.event.keyCode == 13) {
                    handleSearchButton();
                    return false;
                }
                return true;
            }

            function handleSearchButton() {
                openSearchWindow('<cc:text name="JSContext" defaultValue=""/>', document.forms[0]['EntitiesNavigation.SearchText'].value, '', '');
                document.forms[0]['EntitiesNavigation.SearchText'].value="";
            }

        // -->
        </script> 

        <cc:form name="EntitiesNavigationForm" method="post">

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

            <table width="100%" border="0" cellspacing="0" cellpadding="15" bgcolor="#FFFFFF">
                <tr><td align="left">
                    <cc:textfield  name="SearchText"
                        bundleID="apocBundle"
                        title="APOC.navigation.edit.help"
                        dynamic="false" 
                        onKeyPress="return handleKeys()"/>
                    <cc:button name="SearchButton"
                        bundleID="apocBundle" 
                        defaultValue="APOC.search.search"
                        type="secondary"
                        title="APOC.navigation.search.help"
                        onClick="javascript: handleSearchButton(); return false;"/>
                </td></tr>
            </table>

            <cc:dtree name="Tree" bundleID="apocBundle" />

        </cc:form>

    </cc:header>

</jato:useViewBean> 
