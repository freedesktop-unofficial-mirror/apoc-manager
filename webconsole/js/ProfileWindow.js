//
// DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
// 
// Copyright 2007 Sun Microsystems, Inc. All rights reserved.
// 
// The contents of this file are subject to the terms of either
// the GNU General Public License Version 2 only ("GPL") or
// the Common Development and Distribution License("CDDL")
// (collectively, the "License"). You may not use this file
// except in compliance with the License. You can obtain a copy
// of the License at www.sun.com/CDDL or at COPYRIGHT. See the
// License for the specific language governing permissions and
// limitations under the License. When distributing the software,
// include this License Header Notice in each file and include
// the License file at /legal/license.txt. If applicable, add the
// following below the License Header, with the fields enclosed
// by brackets [] replaced by your own identifying information:
// "Portions Copyrighted [year] [name of copyright owner]"
// 
// Contributor(s):
// 
// If you wish your version of this file to be governed by
// only the CDDL or only the GPL Version 2, indicate your
// decision by adding "[Contributor] elects to include this
// software in this distribution under the [CDDL or GPL
// Version 2] license." If you don't indicate a single choice
// of license, a recipient has the option to distribute your
// version of this file under either the CDDL, the GPL Version
// 2 or to extend the choice of license to its licensees as
// provided above. However, if you add GPL Version 2 code and
// therefore, elected the GPL Version 2 license, then the
// option applies only if the new code is made subject to such
// option by the copyright holder.
//

function submitCurrentSelection(hrefTag) {
    var newLocation = hrefTag.href.substring(hrefTag.href.indexOf('=')+1, hrefTag.href.length);
    window.location ="../manager/PolicySettingsContent?PolicySettingsContent.CategoryHref=" + newLocation;
    return false;
}


function closeWindow() {
    if (unstoredChanges() == true) {
        var agree = confirm(getButtonsFrame().alert1);
        if (agree != true) {
            closeEditorAndRefresh()
            return false;
        }
    } 
    closeEditorAndRefresh()
}

function closeEditorAndRefresh() {
    refreshMainWindow();
    if (window.parent != null) {
        if (window.parent.parent != null) {
            if (window.parent.parent.parent != null) {
                window.parent.parent.parent.close();
            } 
            window.parent.parent.close();
        } 
        window.parent.close();
    } 
    window.close();
}

function closeEditorWindow() {
    if (document.getElementById("isNewProfile") != null && document.getElementById("isNewProfile").value == "true") {
        return true;
    } else if (unstoredChanges() == true) {
        var agree = confirm(getButtonsFrame().alert1);
        if (agree != true) {
            closeEditorAndRefresh()
            return false;
        } else {
            updateDisabledFields();
            return true;
        }
    } else {
        closeEditorAndRefresh();
        return false;
    }
}

function  triggerCloseButton() {
    var doc  = getContentDocument();
    doc.getElementById("CloseEditorButton").click();
}

function getContentForm() {
    var form = null;
    if (window.parent.frames[1].name == 'content') {
        form = window.parent.content.con.contents.document.Form;
    }
    if (window.parent.frames[1].name == 'con') {
        form = window.parent.con.contents.document.Form;
    }
    if (form == null) {
        form = document.ProfileForm;
    }
    if (form == null) {
        form = window.parent.frames['contents'].document.ProfileForm;
    }
    if (form == null) {
        form = document.Form;
    }
    if (form == null) {
        form = window.parent.frames['contents'].document.Form;
    }
    return form;
}


function getButtonsForm() {
    var form = document.ButtonsForm;
    if (form == null) {
        form = window.parent.frames['buttons'].document.ButtonsForm;
    }
    return form;
}

function getContentDocument() {
    var form = null;
    var frame = self;
    if (window.parent.frames[1].name == 'content') {
        form = window.parent.content.cown.contents.document.Form;
        frame = window.parent.content.cown.contents.document;
    }
    if (window.parent.frames[1].name == 'con') {
        form = window.parent.con.contents.document.Form;
        frame = window.parent.con.contents.document;
    }
    if (form == null) {
        form = document.ProfileForm;
        frame = document;
    }
    if (form == null) {
        form = window.parent.frames['contents'].document.ProfileForm;
        frame = window.parent.frames['contents'].document;
    }
    if (form == null) {
        form = document.Form;
        frame = document;
    }
    if (form == null) {
        form = window.parent.frames['contents'].document.Form;
        frame = window.parent.frames['contents'].document;
    }
    return frame;
}

function getButtonsFrame() {
    var form = document.ButtonsForm;
    var frame = self;
    if (form == null) {
        if (window.parent.frames['buttons'] != null) {
            form = window.parent.frames['buttons'].document.ButtonsForm;
            frame = window.parent.frames['buttons'];
        }   
    }
    if (form == null) {
        if (window.parent.frames['content'].frames['con'] != null) {
            form = window.parent.frames['content'].frames['con'].frames['buttons'].document.ButtonsForm;
            frame = window.parent.frames['content'].frames['con'].frames['buttons']; 
        }      
    }
    if (form == null) {
        if (window.parent.parent.frames['content'].frames['con'] != null) {
            form = window.parent.parent.frames['content'].frames['con'].frames['buttons'].document.ButtonsForm;
            frame = window.parent.parent.frames['content'].frames['con'].frames['buttons']; 
        }      
    }
    return frame;
}


function openTemplateHelp() {
    var windowHref = '/apoc/manager/TemplateHelpWindow';
    var openedWindow = null;
    var openerWindow = window;
    var windowName = 'TemplateHelpWindow';
    var height = 600;
    var width = 0.6;
    var bGoldenMean = true;
    
    if (windowHref.charAt(windowHref.length-1)!="&") {
        if (windowHref.indexOf("?")>-1) {
            windowHref=windowHref+"&";
        } else {
            windowHref=windowHref+"?";
        }
    }
    windowHref=windowHref+"com_sun_web_ui_popup=true&";
    
    if ( (openedWindow != null) && (openedWindow.closed==false) ) {
        openedWindow.location.href=windowHref;
    } else {
        if (windowName==null) {
            windowName = "a"+Math.ceil(Math.random()*1000000000).toString();
        }
        if (height<=1) {
            height = screen.height*height;
        }
        if (width<=1) {
            width = screen.width*width;
        }
        var topPos  = 0;
        var leftPos = (screen.width-width)/2;
        if (bGoldenMean) {
            topPos = (screen.height-(screen.height/1.618))-(height/2);
        } else {
            topPos = (screen.height-height)/2;
        }
        if (openerWindow==null) {
            openerWindow=window;
        }
        openedWindow = openerWindow.open(windowHref, windowName, 'height='+height+',width='+width+',top='+topPos+',left='+leftPos+',scrollbars,resizable');
    }
    openedWindow.focus();
}

function getPagePath() {
    return self.opener.getContentDocument().getElementById("HelpPage").value
}

function saveChanges() {
    getContentForm().submit();
}

function switchToTab(index) {
    if (unstoredChanges() == true) {
        var agree = confirm(getButtonsFrame().alert2);
        if (agree == true) {
            updateDisabledFields();
            saveChanges();
            return true;
        } else {
            parent.location.href='../manager/ProfileWindowFrameset?SelectedEditorTab='+index+'&com_sun_web_ui_popup=true&';
            return true;
        }
    } else {
        parent.location.href='../manager/ProfileWindowFrameset?SelectedEditorTab='+index+'&com_sun_web_ui_popup=true&';
        return true;
    }
}

function displayCategory(link, selectedCategory) {
    var currentCategory = document.getElementById("CurrentCategory").value;
    if (selectedCategory.indexOf("#") != -1) {
        selectedCategory = selectedCategory.substring(0, selectedCategory.indexOf("#"));
        var anchor = selectedCategory.substring(selectedCategory.indexOf("#"), selectedCategory.length);
        if (currentCategory == selectedCategory) {
            return true;
        }
    } else {
        if (currentCategory == selectedCategory) {
            return true;
        }   
    }
    
    if (unstoredChanges() == true) {
        var agree = confirm(getButtonsFrame().alert3);
        if (agree == true) {
            var sHref = link.href;
            if (sHref.indexOf("&isSaveRequired=") != -1) {
                sHref = sHref.substring(0, sHref.indexOf("&isSaveRequired="));
            }
            sHref = sHref + "&isSaveRequired=true";
            link.href = sHref;
            updateDisabledFields();
            saveChanges();
        }
    }
    return true;
}

function enableSaveButton(form) {
    var elements = form.elements
    for(var i=0; i < elements.length; i++) {
       if (elements[i].name.indexOf('SaveButton') != -1) {
           var button = elements[i];
           button.disabled=''; 
           button.className='Btn1';
           break;
       }
    }
}

function disableSaveButton(form) {
    var elements = form.elements
    for(var i=0; i < elements.length; i++) {
       if (elements[i].name.indexOf('SaveButton') != -1) {
           var button = elements[i];
           button.disabled=true; 
           button.className='Btn1Dis';
           break;
       }
    }
}

function unstoredChanges() {
    var needsSave = false;
    var elements = getContentForm().elements
    for(var i=0; i < elements.length; i++) {
       if (elements[i].name.indexOf('SaveButton') != -1) {
           var button = elements[i];
           if (button.disabled == '') {
               needsSave = true;
           }
           break;
       }
    }
    return needsSave;
}


function enableSaveButtons() {
    enableSaveButton(getContentForm());
    enableSaveButton(getButtonsForm());
}

function disableSaveButtons() {
    disableSaveButton(getContentForm());
    disableSaveButton(getButtonsForm());
}

function updateButtonsArea() {
    window.parent.frames['buttons'].location = '../manager/PolicySettingsButtons';
}

function getSelectedSetName(doc, filterPara) {
    var viewLinkPos         = 0;
    var checkboxOptionPos   = 0;
    var selectedLink        = null;

    // set viewLinkPos to 1st View link (same row as 1st option box)

    while (doc.links[viewLinkPos]!=null) {
        if ( (doc.links[viewLinkPos].href.indexOf(".SubPageHref")!=-1) && 
             (doc.links[viewLinkPos].href.indexOf(filterPara)!=-1) ){
            break;
        }
        viewLinkPos++;
    }
    // set checkboxOptionPos to 1st option box link (same row as 1st view link)
    checkboxOptionPos = getNextCheckboxPos(doc, filterPara, checkboxOptionPos);

    // walk through all option boxes and ViewLinks in paralel
    while (doc.forms[0].elements[checkboxOptionPos]!=null) {
        if (doc.forms[0].elements[checkboxOptionPos].checked==true) {
            selectedLink = doc.links[viewLinkPos];
            break;
        }
        checkboxOptionPos = getNextCheckboxPos(doc, filterPara, checkboxOptionPos);
        viewLinkPos++;
    }

    return selectedLink;
}

function requestSetPropertyName(buttonTag, alertText, isReplace, document) {
    var defaultValue = "";
    if (isReplace=="true") {
        defaultValue = getSelectedSetName(document, ".").text;
    }
    if (unstoredChanges() == true) {
        var agree = confirm(getButtonsFrame().alert4);
        if (agree == true) {
            var f=getContentForm();
            f.elements['SaveAllPage'].value = "true";

            var returnValue = prompt(alertText, defaultValue);
            if (returnValue=='' || returnValue==null) return false;
            var doReplace = 0;
            for (i = 0; i < returnValue.length; i++) {
                if (returnValue[i] == '<') {
                    doReplace++;
                }
            }
            if (doReplace != 0) {
                for(i=0;i<doReplace;i++) {
                    returnValue = returnValue.replace("<", " ");
                }
            } 
            f.elements['UserInput'].value = returnValue;
            updateDisabledFields();
            return true;
        } else {
            var f=getContentForm();
            f.elements['SaveAllPage'].value = "false";
            var returnValue = prompt(alertText, defaultValue);
            if (returnValue=='' || returnValue==null) return false;
            var doReplace = 0;
            for (i = 0; i < returnValue.length; i++) {
                if (returnValue[i] == '<') {
                    doReplace++;
                }
            }
            if (doReplace != 0) {
                for(i=0;i<doReplace;i++) {
                    returnValue = returnValue.replace("<", " ");
                }
            } 
            f.elements['UserInput'].value = returnValue;
            updateDisabledFields();
            return true;
        } 
    } else {
        var returnValue = prompt(alertText, defaultValue);
        if (returnValue=='' || returnValue==null) return false;
        var f=getContentForm();
        var doReplace = 0;
        for (i = 0; i < returnValue.length; i++) {
            if (returnValue[i] == '<') {
                doReplace++;
            }
        }
        if (doReplace != 0) {
            for(i=0;i<doReplace;i++) {
                returnValue = returnValue.replace("<", " ");
            }
        } 
        f.elements['UserInput'].value = returnValue;
        updateDisabledFields();
        return true;
    }
}

function extractSectionName(elementName) {
    var section = elementName;
    var pos = section.indexOf('.PoliciesSectionTiledView');
    if (pos == -1) {
        pos = section.lastIndexOf('.');
    }
    section = section.substring(0, pos);    
    section = section.substring(section.lastIndexOf('.')+1, section.length);
    return section
}

function setTargetAnchor(elementName) {
    var form = document.Form;
    var pos = form.action.lastIndexOf('#');
    if (pos != -1) {
        form.action = form.action.substring(0, pos);
    }
    form.action=form.action + '#' + extractSectionName(elementName);
}

function addRequestParameter(name, value) {
    var form = document.Form;
    form.action=form.action + '?' + name + '=' + value + '&';
}

function sendStatusAreaRequest(name, value) {
    window.parent.frames['buttons'].location = '../manager/PolicySettingsButtons' + '?' + name + '=' + value + '&';
}

function toggleDisabledState(sectionName) {
    var enableButton = 'PolicySettingsContent.PolicyTemplateContent.'+ sectionName + '.EnableButton';
    var disableButton = 'PolicySettingsContent.PolicyTemplateContent.'+ sectionName + '.DisableButton';
    var actionMenu   = 'PolicySettingsContent.PolicyTemplateContent.'+ sectionName + '.ActionMenu';
    var elementName = 'PolicySettingsContent.PolicyTemplateContent.' + sectionName + '.PoliciesSectionTable.SelectionCheckbox';
    var form = document.Form;
    var disabled = true;
    
    for (i = 0; i < form.elements.length; i++) {
         var e = form.elements[i];
    
         if ((e.name.indexOf(elementName) != -1) && 
                (e.name.indexOf('jato_boolean') == -1)) {
             if (e.checked) {
                 disabled = false;
                 break;
             }
         }
    }
    
    // Toggle action button disabled state.
    ccSetDropDownMenuOptionDisabled(actionMenu, "Form", disabled, 0);
    ccSetDropDownMenuOptionDisabled(actionMenu, "Form", disabled, 1);
    ccSetDropDownMenuOptionDisabled(actionMenu, "Form", disabled, 2);
    ccSetDropDownMenuOptionDisabled(actionMenu, "Form", disabled, 3);
    ccSetDropDownMenuOptionDisabled(actionMenu, "Form", disabled, 4);
    ccSetButtonDisabled(enableButton, "Form", disabled);
    ccSetButtonDisabled(disableButton, "Form", disabled);
}

function updateSaveStatus(element) {
    var contentForm = getContentForm();
    var currentValues = updateValues(false);
    var originalValues = contentForm['PolicySettingsContent.OriginalValues'].value;
    if (currentValues != originalValues) {
        setTargetAnchor(element);
        enableSaveButtons();
    } else {
        disableSaveButtons();
    }
}

function updateGeneralSettingsSaveStatus() {
    var currentNameValue = document.getElementById("psLbl1").value;
    var currentCommentValue = document.getElementById("psLbl2").value;
    var originalNameValue = document.getElementById("originalName").value;
    var originalCommentValue = document.getElementById("originalComment").value;
    var allCurrentValues = currentNameValue + ";" + currentCommentValue;
    var allOriginalValues = originalNameValue + ";" + originalCommentValue;
    if (allCurrentValues != allOriginalValues) {
        enableSaveButtons();
    } else {
        disableSaveButtons();
    }
    document.getElementById("isNewProfile").value = "false";
}

function updateValues(isOriginalLoad) {
    var prefix1 = "PolicySettingsContent.PolicyTemplateContent." ;
    var prefix2 = ".PoliciesSectionTiledView[" ;
    var prefix3 = "].PropertyValue" ;
    var enforcedPrefix = ".Enforced";

    var valueList = "" ;
    var contentForm = getContentForm();
    for(var i = 0; i < contentForm.elements.length; i++) {
        var element = contentForm.elements[i] ;
        if((element.name.indexOf(prefix1) == 0) 
                && (element.name.indexOf(prefix2) != -1) 
                    && (element.name.indexOf(prefix3) == element.name.length-prefix3.length)) {
            var value = element.value ;

            // Need to handle checkboxes differently since checkbox.value always returns true, 
            // its necessary to check the .checked value of a checkbox 
            if(element.type == "checkbox" && element.checked != true) {
                value = "false" ;
            }

            // Need to deal with radiobuttons individually and see if they are checked 
            // and only add them to the list if they are checked
            if(element.type == "radio") {
                if(element.checked == true) {
                    value = element.value ;
                    valueList = valueList  + value + "|";
                }
            } else {
                valueList = valueList + value + "|" ;
            }
        } else if (element.name.indexOf(enforcedPrefix) == element.name.length-enforcedPrefix.length) {
            if(element.checked != true) {
                value = "false" ;
            } else {
                value = "true" ;
            }
            valueList = valueList + value + "|" ;
        }
    }
    if (isOriginalLoad) {
        contentForm['PolicySettingsContent.OriginalValues'].value = valueList ;
    }
    return valueList;
}

function isSaveNecessary() {
    var wnd = window.parent.frames['buttons'];
    var form = wnd.document.Form;
    var saveButton = form.elements['SettingsButtons.SaveButton'];
    if (saveButton.disabled == false) {
        var answer = confirm("Sie haben mehrere Aenderungen auf dieser Seite gemacht.\nMoechten Sie diese Aenderungen jetzt speichern?");
        if (answer == true) {
            window.document.Form.submit();
            return true;
        }
    }
    return false;
}

function requestUserInput(buttonTag) {
    var returnValue = prompt(getButtonsFrame().alert5,'');
    if (returnValue=='' || returnValue==null) return false;
    var f=document.Form;
    f.elements['UserInput'].value = returnValue;
    return true;
}

function addNewListItem(valueFieldId) {
    if (requestUserInput(null) != true) return;
    var f = document.Form;
    var separator = f.elements['Separator' + valueFieldId].value;
    var newEntry = f.elements['UserInput'].value
    var selection = f.elements['List' + valueFieldId];
    if (!(selection.options[0].value == localizedNotSet)) {
        for(var i = selection.length; i > 0; i--) {
            selection[i] = new Option(selection.options[i-1].value, selection.options[i-1].value);
        }
    }
    selection[0]  = new Option(newEntry, newEntry);
    selection.selectedIndex = 0;
    
    var hidden = f.elements[valueFieldId];
    var buffer = newEntry;
    var text = hidden.value;
    if (text.length > 0) {
        if (!(text == localizedNotSet)) {
            buffer = buffer + separator + text;
        }
    }
    hidden.value = buffer;
    updateSaveStatus(hidden.name);
    toggleOverwriteCheckboxFromId(valueFieldId);
}


function removeListItem(valueFieldId) {
    var f=document.Form;
    var selection = f.elements['List' + valueFieldId];
    var separator = f.elements['Separator' + valueFieldId].value;
    if (selection.selectedIndex != -1) {
        var hidden = f.elements[valueFieldId];
        for(var i=0; i < selection.length; i++) {
            if (selection[i].selected == true) {
                selection[i] = null;
                i = i - 1;
            }
        }
        selection.selectedIndex = -1;
        var buffer = '';
        for(var i = 0; i < selection.length; i++) {
            buffer = buffer + selection.options[i].value;
            if (i < selection.length -1) {
                buffer = buffer + separator;
            }
        }
        hidden.value = buffer;
        updateSaveStatus(hidden.name);
        toggleOverwriteCheckboxFromId(valueFieldId);
        if (selection.length == 0) clearList(valueFieldId);
    }
}

function clearList(valueFieldId) {
    var f=document.Form;
    var selection = f.elements['List' + valueFieldId];
    var hidden = f.elements[valueFieldId];
    for(var i=0; i < selection.length; i++) {
        selection[i] = null;
        i = i - 1;
    }
    selection.selectedIndex = -1;
    var buffer = localizedNotSet;
    selection[0] = new Option(buffer, buffer);
    hidden.value = buffer;
    updateSaveStatus(hidden.name);
    toggleOverwriteCheckboxFromId(valueFieldId);
}

function setCurrentCategory(catName) {
    var hiddenCat = window.parent.parent.frames.tree.document.getElementById("CurrentCategory");
    var hiddenCatValue = hiddenCat.value;
    if (hiddenCatValue.indexOf("#") == -1) {
        hiddenCat.value = catName;
    } else {
        var mainCat = hiddenCatValue.substring(0, hiddenCatValue.indexOf("#"));
        if (catName != mainCat) {
            hiddenCat.value = catName;
        }
    }
}

function refreshNavigationTree() {
    window.parent.parent.frames.tree.document.TreeForm.submit();
}

function refreshSettingsArea() {
    window.parent.frames['con'].location = '../manager/PolicySettingsFrameset';
}

function refreshMainWindow() {

    var topWindow = top.m_OpenerTop;
    if (topWindow.content != null ) {
        topWindow.content.document.forms[0].submit();
    } else if (topWindow.document.forms[0] != null && topWindow.document.forms[0].name == "OnePaneForm") {
        topWindow.document.forms[0].submit();
    }
}

function jumpTo(anchor) {
    if (window.parent.frames['con'] != null) {
        window.parent.frames['con'].frames['contents'].location.hash=anchor;
    } else {
        window.location.hash=anchor;
    }
}

function resetField(clearButton) {
    var f = document.Form;
    var fieldId = clearButton.name;
    fieldId = fieldId.substring(0, fieldId.length - 5);
    f.elements[fieldId].value = localizedNotSet;
    updateSaveStatus(fieldId);
    toggleOverwriteCheckboxFromId(fieldId);
}

function setDefaultFieldValue(id, value) {
    var f = document.Form;
    f.elements[id].value = value;
    updateSaveStatus(id);  
    toggleOverwriteCheckboxFromId(id);
}

function setListToDefault(listName, defaultValue, listSeparator) {
    var values = defaultValue.split(listSeparator);
    var contentForm = getContentForm();
    contentForm[listName].value = defaultValue;
    var listSetting = contentForm['List'+ listName];
    if (listSetting.options != null) {
        listSetting.options.length = 0;
    }
    for(var i = 0; i < values.length; i++) {
        listSetting.options[i] = new Option(values[i], values[i]);
    }
    updateSaveStatus(listName); 
    toggleOverwriteCheckboxFromId(listName);
}

function updateDisabledFields() {
    var prefix1 = "PolicySettingsContent.PolicyTemplateContent." ;
    var prefix1Length =  prefix1.length ;
    var prefix2 = ".PoliciesSectionTiledView[" ;
    var prefix2Length = prefix2.length ;
    var prefix3 = "].PropertyValue" ;
    var disabledValues = "" ;
    var contentForm = getContentForm();
    for(var i = 0; i < contentForm.elements.length; i++) {
        var element = contentForm.elements[i] ;

        if(element.disabled == true) {
            if((element.name.indexOf(prefix2) != -1) && (element.name.indexOf(prefix3) != -1)) {
                var sectionName = element.name.substring(prefix1Length, element.name.indexOf(prefix2)) ;
                var index = element.name.substring(prefix1Length + sectionName.length + prefix2Length, element.name.indexOf(prefix3)) ;
                var value = element.value ;

                // Need to handle checkboxes differently since checkbox.value always returns true, 
                // its necessary to check the .checked value of a checkbox 
                if(element.type == "checkbox" && element.checked != true) {
                    value = "false" ;
                }

                // Need to deal with radiobuttons individually and see if they are checked 
                if(element.type == "radio") {
                    if(element.checked == true) {
                        value = element.value ;
                        disabledValues = disabledValues + sectionName + "|" + index + "|" + value + "|" ;
                    }   
                } else {
                    disabledValues = disabledValues + sectionName + "|" + index + "|" + value + "|" ;
                }
            }
        }
    }
    if (contentForm['PolicySettingsContent.DisabledFieldsValues'] != null) {
        contentForm['PolicySettingsContent.DisabledFieldsValues'].value = disabledValues ;
    }
}

//******** BEGIN Color Chooser Functions
var colorFieldName = null ;

function setColorChooserName(buttonName) {
    var cutStringPoint = buttonName.lastIndexOf(".");
    colorFieldName = buttonName.substring(0, cutStringPoint).concat(".ColorNameDropDown") ;
    return true ;
}

function getColorChooserName() {
    return colorFieldName ;
}


function updateColorChoosers(colorNames, colorValues, localizedNoName) {
    var colorNamesLength = 0;
    var contentDocForm = document.Form ;

    //Runs through all elements in content area and checks if they are colorchoosers buttons
    //then uses the colorchooser button name to create the drop-down menu name
    for(var i=0; i < contentDocForm.elements.length; i++) {
        if(contentDocForm.elements[i].name.search("ColorNameDropDown") != -1) {
            var dropDownMenuName = contentDocForm.elements[i].name;
            var selectedColorIndex = contentDocForm[dropDownMenuName].selectedIndex;
            var selectedColorName = contentDocForm[dropDownMenuName].options[selectedColorIndex].text;
            //Adds default options to drop-down menu
            contentDocForm[dropDownMenuName].options[0] = new Option(localizedNotSet, localizedNotSet);
            contentDocForm[dropDownMenuName].options[1] = new Option(localizedNoName, "");
            if(selectedColorName==contentDocForm[dropDownMenuName].options[0].text) {
                selectedColorIndex = 0 ;
            }
            if(selectedColorName==contentDocForm[dropDownMenuName].options[1].text) {
                selectedColorIndex = 1 ;
            }
            for(var j=0; j < colorNames.length; j++){
                contentDocForm[dropDownMenuName].options[j+2] = new Option(colorNames[j], colorValues[j]);
                if(selectedColorName==contentDocForm[dropDownMenuName].options[j+2].text) {
                    selectedColorIndex = j+2 ;
                }
            }
            //Resets the selected index to its correct value after adding option
            contentDocForm[dropDownMenuName].options[selectedColorIndex].selected=true;
            contentDocForm[dropDownMenuName].options.length=colorNames.length + 2;
        }
    }
}


function setFromDropDown(prop) {
    var dropdown = document.getElementById("colorNameDropDown"+prop);
    var index = dropdown.selectedIndex;
    var value = dropdown.options[index].value;
    document.getElementById("newColor"+prop).value=value;
    document.getElementById("colorsquare"+prop).style.backgroundColor=value;
    document.Form.elements[unescape(prop)].onchange();
}

function checkValidHexValue(field, qualName){
    var hex = document.Form.elements[field].value;
    if (hex == localizedNotSet) {
        updateColorChooserExtras(qualName, localizedNotSet);
    } else {
        var hexNoHash;
        var cutStringPoint = field.lastIndexOf(".");
        var origValueField = field.substring(0, cutStringPoint).concat(".OriginalValue") ;
        var origValue = document.Form.elements[origValueField].value;

        if (hex.substring(0,1)=="#") {
            hexNoHash = hex.substring(1);
        } else {
            hexNoHash = hex;
            if (hex.length != 0) {
                hex = "#" + hex;
                document.Form.elements[field].value = hex;
            }
        }
        if (hexNoHash.length != 6 && hexNoHash.length != 0){
            alert(invalidHex);
            document.Form.elements[field].value=origValue;
            hex=origValue;
        } else {
            hexNoHash = hexNoHash.toLowerCase();
            hexchars="0123456789abcdef";
            out=0;
            for (a=hexNoHash.length-1;a>=0;a--)  {
                var aChar = hexNoHash.charAt(a);
                aChar = aChar.toLowerCase();
                index = hexchars.indexOf(aChar);
                if (index==-1)
                    out = -1;
            }
            if (out==-1) {
                alert(invalidHex);
                document.Form.elements[field].value=origValue;
                hex=origValue;
            }
        }
        updateColorChooserExtras(qualName, hex)
    }
}

function updateColorChooserExtras(qualName, hexValue) {
    if (hexValue == localizedNotSet) {
        document.getElementById("colorNameDropDown"+qualName).selectedIndex=0;
       document.getElementById("colorsquare"+qualName).style.backgroundColor="";
    } else {
        document.getElementById("colorsquare"+qualName).style.backgroundColor=hexValue;
        var dropDown = document.getElementById("colorNameDropDown"+qualName);
        document.getElementById("colorNameDropDown"+qualName).selectedIndex=1;
        for(i=0; i < dropDown.length; i++) {
            if(hexValue.toLowerCase() == dropDown.options[i].value) {
                document.getElementById("colorNameDropDown"+qualName).selectedIndex=i;
                break;
            }
        }
    }
    toggleOverwriteCheckbox(document.getElementById("newColor" + qualName));
}

//************* END Color Chooser Functions

//************* START Chooser Functions
var chooserName = null ;
var chooserID = null ;

function setChooserName(buttonName) {
    chooserName = buttonName ;
    return true ;
}


function setChooserID(id) {
    chooserID = id;
    return true ;
}


function getChooserID() {
    return chooserID ;
}

function getChooserName() {
    return chooserName ;
}

function updateChoosers(optionsArray) {
    var f = document.Form ;
    var cutStringPoint = chooserName.lastIndexOf(".") ;
    chooserBtnName = chooserName.substring(cutStringPoint + 1, chooserName.length) ;
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
            f[dropDownMenuName].options.length = 0 ;
            //Adds new options to drop-down menu
            for(var j=0; j < optionsArray.length; j++){
                f[dropDownMenuName].options[j] = new Option(optionsArray[j], optionsArray[j]) ;
                if(selectedListName.match(f[dropDownMenuName].options[j].text)) {
                    selectedListIndex = j ;
                }
            }
            //Resets the selected index to its correct value after adding option
            f[dropDownMenuName].options[selectedListIndex].selected=true;
        }
    }    
}

function compareCaseInsensitive(a, b) {
    var anew = a.toLowerCase();
    var bnew = b.toLowerCase();
    if (anew < bnew) return -1;
    if (anew > bnew) return 1;
    return 0;
//************* END Chooser Functions
}
