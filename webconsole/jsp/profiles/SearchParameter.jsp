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

    <jato:useViewBean className="com.sun.apoc.manager.ProfilesSearchParameterViewBean">

    <cc:header pageTitle="APOC.navigation.search" copyrightYear="2003"
        baseName="com.sun.apoc.manager.resource.apoc_manager"
        bundleID="apocBundle"
        isPopup="true"
        onLoad="javascript:handleLoad(); return true">

        <script type="text/javascript" src="/apoc/js/MainWindow.js"></script>

        <script type="text/javascript">
        <!--
            var m_sOldFindString = "";
            var m_OpenerTop;
            
            function handleLoad() {
                document.forms[0]['ProfilesSearchParameter.SearchText'].focus();
                m_sOldFindString = document.forms[0]['ProfilesSearchParameter.SearchText'].value;
                m_OpenerTop=top.opener.top;
            }
            
            function handleSubmit() {
                document.forms[0]['ProfilesSearchParameter.SearchButton'].className='Btn1Dis';
                document.forms[0]['ProfilesSearchParameter.SearchButton'].disabled='true';

                var resultsForm = top.results.document.forms[0];
                var newDoc      = top.results.document.open("text/html", "replace");
                var sMarkup     = '<html><head><title>empty</title></head><body>';
                sMarkup += '<form name="ResultForm" method="post" action="../manager/ProfilesSearchResult">';
                sMarkup += '<input type="hidden" name="ProfilesSearchResult.SearchString" value="*" />';
                sMarkup += '<input type="hidden" name="jato.defaultCommand" value="SearchButton" />';
                sMarkup += '<input type="hidden" name="com_sun_web_ui_popup" value="true" />';
                sMarkup += '</form></body></html>';
                newDoc.write(sMarkup);
                newDoc.close();
  
                resultsForm = top.results.document.forms[0];
                resultsForm['ProfilesSearchResult.SearchString'].value=document.forms[0]['ProfilesSearchParameter.SearchText'].value;
                resultsForm.submit();
            }
            
            function handlePressedKey() {
                var sNewFindString =document.forms[0]['ProfilesSearchParameter.SearchText'].value;
                if (sNewFindString!=m_sOldFindString) {
                    m_sOldFindString=sNewFindString;
                    document.forms[0]['ProfilesSearchParameter.SearchButton'].disabled='';
                    document.forms[0]['ProfilesSearchParameter.SearchButton'].className='Btn1Def';
                }
            }
        // -->
        </script> 

        <cc:form name="ParameterForm"
            method="post"
            onSubmit="javascript: handleSubmit(); return false"
            defaultCommandChild="SearchButton" >

            <cc:secondarymasthead name="Masthead"
                src="/apoc/images/popuptitle.gif"
                alt="APOC.masthead.altText"
                bundleID="apocBundle"/> 
            
                <cc:propertysheet name="SearchParams" 
                    bundleID="apocBundle" 
                    showJumpLinks="false"/>

        </cc:form>

    </cc:header>

</jato:useViewBean> 
