<%@ page info="Entity Search Params" language="java" %> 
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

    <jato:useViewBean className="com.sun.apoc.manager.EntitiesSearchParameterViewBean">

    <cc:header pageTitle="APOC.navigation.search" 
        copyrightYear="2003"
        baseName="com.sun.apoc.manager.resource.apoc_manager"
        bundleID="apocBundle"
        isPopup="true"
        onLoad="javascript:resizeFrame();">

        <%-- with IE 6.0.2600.0000 under XP the target attribute has no effect when 
             sending the form triggered by the RETURN key. The onSubmit JS does the trick--%>
        <cc:form name="SearchForm" 
            method="post"
            target="results"
            onSubmit="javascript: handleSubmit(); return false">
            <cc:secondarymasthead name="Masthead"
                src="/apoc/images/popuptitle.gif"
                alt="APOC.masthead.altText"
                bundleID="apocBundle"/>

            <cc:pagetitle name="SearchParamsTitle" 
                pageTitleText="APOC.search.search"
                pageTitleHelpMessage="APOC.search.param.title.help"
                showPageTitleSeparator="false"
                showPageButtonsTop="true"
                showPageButtonsBottom="false"
                bundleID="apocBundle">
                
                <cc:propertysheet name="SearchParamsSheet" 
                    bundleID="apocBundle" 
                    showJumpLinks="false"/>

                <cc:hidden name="CurrentEntityId" />
                <cc:hidden name="CurrentEntityType" />
                <cc:hidden name="RestrictHidden" defaultValue="false" />
                <cc:hidden name="ResultsHidden" defaultValue="0" />
                <cc:hidden name="ContextHidden" defaultValue="" />
            </cc:pagetitle>

        </cc:form>

        <script type="text/javascript" src="/apoc/js/MainWindow.js"></script>

        <script type="text/javascript">
        <!--
            var m_sEntityTypeMenu   = null;
            var m_sSearchText       = null;
            var m_bRestrict         = null;
            var m_sResultsMenu      = null;
            
//            for(i=0; i<document.SearchForm['EntitiesSearchParameter.EntityTypeMenu'].length; i++) {
//                if (document.SearchForm['EntitiesSearchParameter.EntityTypeMenu'].options[i].defaultSelected == true) {
//                    var previousSelectedValue = document.SearchForm['EntitiesSearchParameter.EntityTypeMenu'].options[i].value;
//                    break;
//                }
//            }
            document.SearchForm.target='results';

            function resizeFrame() {
                top.resizeFrame('<cc:text name="RowLayout" defaultValue="250, *, 50" />');
                document.forms[0]['EntitiesSearchParameter.SearchText'].focus();
                m_sSearchText       = document.forms[0]['EntitiesSearchParameter.SearchText'].value;
                m_sEntityTypeMenu   = document.forms[0]['EntitiesSearchParameter.EntityTypeMenu'].value;
                if (document.forms[0]['EntitiesSearchParameter.RestrictCheckbox']!=null) {
                    m_bRestrict         = document.forms[0]['EntitiesSearchParameter.RestrictCheckbox'].checked;
                    m_sResultsMenu      = document.forms[0]['EntitiesSearchParameter.ResultsMenu'].value;
                    if (m_bRestrict) {
                        document.forms[0]['EntitiesSearchParameter.BrowseButton'].disabled='';
                        document.forms[0]['EntitiesSearchParameter.BrowseButton'].className='Btn2Mni';
                    } else {
                        document.getElementById("SptToggle").style.color='#999';
                    }
                }
            }

            function openBrowseTreeWindow() {
                openWindow(window, null, '/apoc/manager/SearchWindowBrowseTreeIndex?ShowSearch=false&ContextId=<cc:text name="JSContext"/>', 'SearchTreeWindow1', 500, 600, true); 
            } 

            function submitBrowse(entityId, entityType) {
                var f=document.SearchForm;
                if (f != null) {
                    f['EntitiesSearchParameter.CurrentEntityId'].value=entityId;
                    f['EntitiesSearchParameter.CurrentEntityType'].value=entityType;
                    f.action='../manager/EntitiesSearchParameter?EntitiesSearchParameter.NameHref=&amp;';
                    f.target='parameters';
                    f.submit(); 
                }
            }
            
//            function handleSelection(selectTag) {
//                var f=document.SearchForm;
//                if (f != null) {
//                    var selectedValue=selectTag.options[selectTag.selectedIndex].value;
//                    if (selectedValue=="USERID" || previousSelectedValue=="USERID") {
//                        f.action='../manager/EntitiesSearchParameter?EntitiesSearchParameter.NameHref=&amp;jato.pageSession=';
//                        f.target='parameters';
//                        f.submit();
//                    }
//                    else {
//                        previousSelectedValue=selectedValue;
//                    }
//                }
//                return false;
//            }

            function handleReset() {
                document.forms[0]['EntitiesSearchParameter.SearchButton'].disabled='';
                document.forms[0]['EntitiesSearchParameter.SearchButton'].className='Btn1Def';
                top.buttons.document.forms[0]['EntitiesSearchButtons.SearchButton'].disabled='';
                top.buttons.document.forms[0]['EntitiesSearchButtons.SearchButton'].className='Btn1Def';
                
                document.SearchForm['EntitiesSearchParameter.EntityTypeMenu'].selectedIndex='0'; 
                document.SearchForm['EntitiesSearchParameter.SearchText'].value='*'; 
                if (document.SearchForm['EntitiesSearchParameter.RestrictCheckbox']!=null) {
                    document.SearchForm['EntitiesSearchParameter.RestrictCheckbox'].checked=false
                }
                if (document.SearchForm['EntitiesSearchParameter.ResultsMenu']!=null) {
                    document.SearchForm['EntitiesSearchParameter.ResultsMenu'].selectedIndex='0'; 
                }
                document.SearchForm['EntitiesSearchParameter.RestrictHidden'].value='false'; 
                document.SearchForm['EntitiesSearchParameter.ResultsHidden'].value='0';

            }
                        
            function handleSubmit() {
                document.forms[0]['EntitiesSearchParameter.SearchButton'].className='Btn1Dis';
                document.forms[0]['EntitiesSearchParameter.SearchButton'].disabled='true';
                top.buttons.document.forms[0]['EntitiesSearchButtons.SearchButton'].className='Btn1Dis';
                top.buttons.document.forms[0]['EntitiesSearchButtons.SearchButton'].disabled='true';

                var resultsForm = top.results.document.forms[0];
                var sMarkup     = '<html><head><title>empty</title></head><body>\n';
                sMarkup += '<form name="SearchForm" method="post" action="../manager/EntitiesSearchResult">\n';
                var elementRunner = 0;
                while (resultsForm.elements[elementRunner]!=null) {
                    sMarkup += '<input type="hidden" name="'+resultsForm.elements[elementRunner].name+'" value="'+resultsForm.elements[elementRunner].value+'" />\n';
                    elementRunner++;
                } 
                sMarkup += '</form></body></html>';
                
                var newDoc = top.results.document.open("text/html", "replace");
                newDoc.write(sMarkup);
                newDoc.close();
  
                resultsForm = top.results.document.forms[0];
                resultsForm['EntitiesSearchResult.ContextId'].value=document.forms[0]['EntitiesSearchParameter.ContextHidden'].value;
                resultsForm['EntitiesSearchResult.EntityId'].value=document.forms[0]['EntitiesSearchParameter.CurrentEntityId'].value;
                resultsForm['EntitiesSearchResult.EntityType'].value=document.forms[0]['EntitiesSearchParameter.CurrentEntityType'].value;
                resultsForm['EntitiesSearchResult.EntityTypeMenu'].value=document.forms[0]['EntitiesSearchParameter.EntityTypeMenu'].value;
                resultsForm['EntitiesSearchResult.SearchText'].value=document.forms[0]['EntitiesSearchParameter.SearchText'].value;
                resultsForm['EntitiesSearchResult.IsNewSearch'].value='true';
                
                if (document.forms[0]['EntitiesSearchParameter.RestrictCheckbox']!=null) {
                    if (document.forms[0]['EntitiesSearchParameter.RestrictCheckbox'].checked) {
                        resultsForm['EntitiesSearchResult.RestrictCheckbox'].value="true";
                    } else {
                        resultsForm['EntitiesSearchResult.RestrictCheckbox'].value="false";
                    }
                    resultsForm['EntitiesSearchResult.ResultsMenu'].value=document.forms[0]['EntitiesSearchParameter.ResultsMenu'].value;
                } else {
                    resultsForm['EntitiesSearchResult.RestrictCheckbox'].value=document.forms[0]['EntitiesSearchParameter.RestrictHidden'].value;
                    resultsForm['EntitiesSearchResult.ResultsMenu'].value=document.forms[0]['EntitiesSearchParameter.ResultsHidden'].value;
                }
                
                resultsForm.submit();
            }

            function handlePressedKey() {
                var sSearchText     = document.forms[0]['EntitiesSearchParameter.SearchText'].value;
                var sEntityTypeMenu = document.forms[0]['EntitiesSearchParameter.EntityTypeMenu'].value;
                var bRestrict       = null;  
                var sResultsMenu    = null;
                if (document.forms[0]['EntitiesSearchParameter.RestrictCheckbox']!=null) {
                    var bRestrict       = document.forms[0]['EntitiesSearchParameter.RestrictCheckbox'].checked;
                    var sResultsMenu    = document.forms[0]['EntitiesSearchParameter.ResultsMenu'].value;
                    if (m_bRestrict!=bRestrict) {
                        if (bRestrict) {
                            document.forms[0]['EntitiesSearchParameter.BrowseButton'].disabled='';
                            document.forms[0]['EntitiesSearchParameter.BrowseButton'].className='Btn2Mni';
                            document.getElementById("SptToggle").style.color='black';
                        } else {
                            document.forms[0]['EntitiesSearchParameter.BrowseButton'].disabled='disabled';
                            document.forms[0]['EntitiesSearchParameter.BrowseButton'].className='Btn2MniDis';
                            document.getElementById("SptToggle").style.color='#999';
                        }
                    }
                }

                if ((m_sSearchText!=sSearchText) ||  
                    (m_sEntityTypeMenu!=sEntityTypeMenu) ||
                    (m_bRestrict!=bRestrict) ||
                    (m_sResultsMenu!=sResultsMenu)) {
                    m_sSearchText=sSearchText;
                    m_sEntityTypeMenu=sEntityTypeMenu;
                    m_bRestrict=bRestrict;
                    m_sResultsMenu=sResultsMenu;
                    document.forms[0]['EntitiesSearchParameter.SearchButton'].disabled='';
                    document.forms[0]['EntitiesSearchParameter.SearchButton'].className='Btn1Def';
                    top.buttons.document.forms[0]['EntitiesSearchButtons.SearchButton'].disabled='';
                    top.buttons.document.forms[0]['EntitiesSearchButtons.SearchButton'].className='Btn1Def';
                }
            }
         // -->
        </script> 

    </cc:header>

</jato:useViewBean> 

