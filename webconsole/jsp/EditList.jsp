<%@page info="Edit List Dialog" language="java"%>
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

<jato:useViewBean className="com.sun.apoc.manager.EditListViewBean">



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<cc:i18nbundle id="apocBundle" baseName="com.sun.apoc.manager.resource.apoc_manager" />
<html>
    <head>
        <title><cc:text name="WindowTitle" /></title>
        <meta name="Copyright" content="Copyright &copy; 2003 by Sun Microsystems, Inc. All Rights Reserved.">
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <script type="text/javascript" src="/com_sun_web_ui/js/browserVersion.js"></script>
        <script type="text/javascript" src="/com_sun_web_ui/js/stylesheet.js"></script>
        <script type="text/javascript"><!-- Empty script so IE5.0 Windows will draw table and button borders --></script>
    </head>

    <body class="DefBdy">
    

    <script type="text/javascript">
       <!--
        var theMasthead = self.opener.parent.parent.parent.opener;
        var protectedText="<cc:text name='ProtectedAlert' />";
        var selectedText="<cc:text name='SelectedAlert' />";
        var LIST_WIDTH_MAINTAINER='<cc:text name="ListWidthMaintainer" />';
        var SYSTEM_ADDED_FLAG='<cc:text name="SystemAddedFlag" />';
        var UNDEFINED_VALUE="<cc:text name='UndefinedValue' />";
		   

        var m_OpenerTop = top.opener;
        while (m_OpenerTop.top.opener!=null) {
            m_OpenerTop = m_OpenerTop.top.opener;
        }
        m_OpenerTop = m_OpenerTop.top;

        setInterval("setWindowHandle()", 1000);
        function setWindowHandle() {
            m_OpenerTop.m_ChooserWindow= top;
        }


        function updateContentArea() {
            var l = document.ChooserForm['EditList.List'] ;
            var optionsArray = new Array() ;
            var elements = "" ;
            var states = "" ;

            //Add the UNDEFINED_VALUE option at start
            optionsArray[0] = UNDEFINED_VALUE;
            //Create array of options from the existing edit list (minus the last 
            //element which is the width maintainer string
            for(var n=0; n < l.length - 1; n++) {
                var lastIndex = l.options[n].text.length ;
                var last4Chars = l.options[n].text.slice(lastIndex-4, lastIndex) ;
                //Remove the system-added tag from the font name if its there
                if(last4Chars.indexOf(SYSTEM_ADDED_FLAG) == 0) {
                    optionsArray[n+1] = l.options[n].text.slice(0, lastIndex-4) ;
                    elements = elements.concat(optionsArray[n+1].concat(";")) ;
                    states = states.concat("true;") ;
                }
                else {
                    optionsArray[n+1] = l.options[n].text;
                    elements = elements.concat(optionsArray[n+1].concat(";"));
                    states = states.concat("false;") ;
                }
            }
            self.opener.updateChoosers(optionsArray) ;

            document.getElementById("hiddenelements").value = elements ;
            document.getElementById("hiddenstates").value = states ;
        }

        function updateAfterAdd() {
            var l = document.ChooserForm['EditList.List'] ;
            var t = document.ChooserForm['EditList.AddTextField'] ;
            var addedValueIndex = 0 ;

            if(t.value.length == 0) {
            return;
            }

            var optionsArray = new Array() ;

            //Create array of options from the existing edit list (minus the last 
            //element which is the width maintainer string
            for(var n=0; n < l.length - 1; n++) {
            optionsArray[n] = l.options[n].text;
            //If the value is already in the list then highlight it in the list and exit
            if((l.options[n].value == t.value) || (l.options[n].value == t.value.concat(SYSTEM_ADDED_FLAG))) {
                l.options[n].selected = true
                return;
            }
            }
            optionsArray[optionsArray.length] = t.value ;
            optionsArray.sort(compareCaseInsensitive) ;

            //Create a new options array from the sorted optionsArray and assign it
            //to the edit list
            for(var m=0; m < optionsArray.length ; m++) {
            l.options[m] = new Option(optionsArray[m], optionsArray[m]) ;
            if(optionsArray[m] == t.value) {
                addedValueIndex = m ;
            }
            }

            l.options[l.length] = new Option(LIST_WIDTH_MAINTAINER, LIST_WIDTH_MAINTAINER) ;
            t.value = "" ;
            l.focus() ;
            l.options[addedValueIndex].selected = true ;
        }	    


        function updateAfterRemove() {
            var l = document.ChooserForm['EditList.List'] ;
            var indexToRemove = l.selectedIndex ;

            l.options[indexToRemove] = null ;
        }      


        function updateAfterRestore(chooserDefaults) {
            var l = document.ChooserForm['EditList.List'] ;
            //Get defaults string and split it into array - the last element of this 
            //array will be empty since the Defaults string ends in the separator ";"
            var defaults = chooserDefaults.split(";") ;
            l.options.length = 0  ;

            //Create a new options array from the default values and assign it
            //to the edit list
            for(var n=0; n < defaults.length - 1; n++) {
            l.options[n] = new Option(defaults[n], defaults[n]) ;
            }

            l.options[l.length] = new Option(LIST_WIDTH_MAINTAINER, LIST_WIDTH_MAINTAINER) ;
        }	    

           function isRemoveAllowed() {
            var indexToRemove = document.ChooserForm['EditList.List'].selectedIndex ;
                var indexOfLastElement = document.ChooserForm['EditList.List'].length -1 ;
                //Ensure that a value has been selected, that it is not the constant list 
                //width maintainer string and that it is neither a selected or protected value for this
            //page before attempting to remove any values

            if(indexToRemove == -1) {
            return false ;
            }
            else if(indexToRemove == indexOfLastElement) {
            return false ;
            }	    
            else if(isElementProtected(indexToRemove)) {
                    alert(protectedText) ;
                    return false ;
                } 
                else if(isElementSelected(indexToRemove)) {
            alert(selectedText) ;
                    return false ;
                } 
            return true ;
           }


           function isElementProtected(index) {
                var selectedElement = document.ChooserForm['EditList.List'].options[index].text ;
                var protectedElementString = self.opener.document.getElementById("protectedChoosers").value ;
                var protectedElementsArray = protectedElementString.split(";") ;
                var protectedTypeString = self.opener.document.getElementById("protectedChoosersType").value ;
                var protectedTypeArray = protectedTypeString.split(";") ;
                var currentChooserID = self.opener.getChooserID() ;
                var filteredArray = new Array() ;


            //Remove system added flag from the selected element if present
            var lastIndex = selectedElement.length ;
            var last4Chars = selectedElement.slice(lastIndex-4, lastIndex) ;
            if(last4Chars.indexOf(SYSTEM_ADDED_FLAG) == 0) {
            selectedElement = selectedElement.slice(0, lastIndex-4) ;
            }

            for(var i = 0; i < protectedTypeArray.length; i++) {
                    if(protectedTypeArray[i] == currentChooserID) {
                filteredArray.push(protectedElementsArray[i]) ;
            }
            }

            for(var i = 0; i < filteredArray.length; i++) {	    
            var str = filteredArray[i] ;
                    if((str.length != 0) && (selectedElement == str)) {
                        return true
                    }
                }
                return false
            }

            function isElementSelected(index) {
                var selectedElement = document.ChooserForm['EditList.List'].options[index].text ;
                var f = self.opener.document.Form ;
            var clickedChooserName = self.opener.getChooserName() ;
            var cutStringPoint = clickedChooserName.lastIndexOf(".") ;
            var chooserBtnName = clickedChooserName.substring(cutStringPoint + 1, clickedChooserName.length) ;

            //Remove system added flag from the selected element if present
            var lastIndex = selectedElement.length ;
            var last4Chars = selectedElement.slice(lastIndex-4, lastIndex) ;
            if(last4Chars.indexOf(SYSTEM_ADDED_FLAG) == 0) {
            selectedElement = selectedElement.slice(0, lastIndex-4) ;
            }

            //Runs through all elements in content area and checks if they are chooser buttons of 
            //of the correct type(i.e of the type that clicked the 'Edit' button) - 
            //then uses the chooser button name to create the drop-down menu name
            for(var i=0; i < f.elements.length; i++) {
            cutStringPoint = f.elements[i].name.lastIndexOf(".") ;
            elementName = f.elements[i].name.substring(cutStringPoint + 1, f.elements[i].name.length) ;
            if(elementName == chooserBtnName) {
                var cutStringPoint = f.elements[i].name.lastIndexOf(".");
                var dropDownMenuName = f.elements[i].name.substring(0, cutStringPoint).concat(".PropertyValue") ;
                var selectedListIndex = f[dropDownMenuName].selectedIndex ;
                var selectedListName = f[dropDownMenuName].options[selectedListIndex].text ;
                if(selectedElement == selectedListName) {
                return true ;
                }
            }
            }
                return false ;
            }


        function compareCaseInsensitive(a, b) {
            var anew = a.toLowerCase();
            var bnew = b.toLowerCase();
            if (anew < bnew) return -1;
            if (anew > bnew) return 1;
            return 0;
        }
     

    //-->
    </script> 
	


    <cc:form name="ChooserForm" method="post" defaultCommandChild="OKButton">

	<cc:secondarymasthead name="SecondaryMH" bundleID="apocBundle" />

            <cc:pagetitle name="EditListTitle" 
                pageTitleHelpMessage="APOC.page.chooser.list.help"
                showPageTitleSeparator="true"
                showPageButtonsTop="false"
                showPageButtonsBottom="true"
                bundleID="apocBundle">
		
            <br>
       
            <table title="" border="0" cellpadding="5" cellspacing="0" align="center">
		    <tr>
		    <td colspan="2">
                <cc:textfield name="AddTextField"
                    bundleID="apocBundle"
                    dynamic="true"
                    size="25"
                />
                &nbsp;&nbsp;
                <cc:button  name="AddButton"
                    bundleID="apocBundle" 
                    defaultValue="APOC.chooser.add"
                    type="primaryMini"
                    onClick="updateAfterAdd() ;return false;"
                />
		    </td>
		    </tr>
                    <tr>
                    <td>
		    <cc:selectablelist name="List" 
                        bundleID="apocBundle" title="List"
                        dynamic="true"
                        multiple="false"
                        size="5"
		    />
		    </td>
		    <td>
			<cc:button  name="RemoveButton"
                    bundleID="apocBundle" 
                    defaultValue="APOC.policies.delete.button"
                    type="secondaryMini"
                    onClick="javascript:if(isRemoveAllowed()) 
                                updateAfterRemove();
                                return false;"
			/>
		    </td>
		    </tr>
   		    </table>
            <br>
            <table title="" border="0" cellpadding="5" cellspacing="0" align="center">
            <tr>
            <td>
            <cc:text name="StaticText" bundleID="apocBundle" defaultValue="APOC.chooser.system_added" />
            </td>
            </tr>
            </table>
	    </cc:pagetitle>
	    <cc:hidden name="HiddenElements" elementId="hiddenelements" />
	    <cc:hidden name="HiddenStates" elementId="hiddenstates" />
	    <cc:hidden name="HiddenChooserDataPath" elementId="hiddendatapath" />
	    <input type="hidden" name="EditList.PageTitle.com_sun_web_ui_popup" value="true"/> 
            <input type="hidden" name="com_sun_web_ui_popup" value="true" />
	</cc:form>
</body>
</html>
</jato:useViewBean>

