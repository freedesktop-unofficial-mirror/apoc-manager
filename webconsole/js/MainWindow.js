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

var m_bAllowSubmit = true;
var m_sFormAction = "";
var m_sSearchText = "";
var submitSearchInterval = null;

function allowSubmit(bIsAllowed) {
    m_bAllowSubmit=bIsAllowed
}

function isSubmitAllowed() {
    var bAllowSubmit=m_bAllowSubmit;
    m_bAllowSubmit=true;
    return bAllowSubmit; 
}

//function setFormAction(sFormAction) {
//    m_sFormAction = sFormAction;
//    document.forms[0].action=m_sFormAction;
//}

//function changeAction() {
//alert("changeAction");
//    if (m_sFormAction.length>0) {
//        document.forms[0].action=m_sFormAction;
//    } else {
//        var nQueryIndex = document.forms[0].action.indexOf('?');
//        if (nQueryIndex>-1) {
//            document.forms[0].action=document.forms[0].action.substring(0,nQueryIndex);
//        }
//    }
//alert(document.forms[0].action);
//}

function handleSearchSelection(sHref) {
    if (parent.opener != null) {
        parent.opener.focus(); 
        parent.opener.entitySearchCallback(sHref); 
        if (parent.opener.name != "navigation") {
            top.window.close();
        }
    } else  if (top.m_OpenerTop != null){
        if (top.m_OpenerTop.navigation != null && top.m_OpenerTop.navigation.entitySearchCallback) {
            top.m_OpenerTop.focus(); 
            top.m_OpenerTop.navigation.entitySearchCallback(sHref); 
        } else {
            alert(SearchTargetAlert);
        }
    } else {
        alert(SearchTargetAlert);
    }
}

function extractByDelimiters(sLeftDelimiter, sRightDelimiter, sString)
{
    var nLeftDelimiter          = 0;
    var nLeftDelimiterLength    = 0;
    var nRightDelimiter         = sString.length+1;

    if ( (sLeftDelimiter!=null) && (sLeftDelimiter.length>0) ) {
        nLeftDelimiterLength = sLeftDelimiter.length;
        nLeftDelimiter = sString.indexOf(sLeftDelimiter);
        if (nLeftDelimiter<0) {
            nLeftDelimiter=0;
        }
    }

    if ( (sRightDelimiter!=null) && (sRightDelimiter.length>0) ) {
        nRightDelimiter = sString.indexOf(sRightDelimiter, nLeftDelimiter+nLeftDelimiterLength);
        if (nRightDelimiter<nLeftDelimiter) {
            nRightDelimiter=sString.length+1;
        }
    }

    return sString.substring(nLeftDelimiter+nLeftDelimiterLength, nRightDelimiter);
}

function getSelectedLink(documentPara) {
    return getSelectedLinkWithFilter(documentPara, ".");
}

function getNextCheckboxPos(documentPara, filterPara, checkboxOptionPos)  {
    checkboxOptionPos++;
    while (documentPara.forms[0].elements[checkboxOptionPos]!=null) {
        if ( (documentPara.forms[0].elements[checkboxOptionPos].name.indexOf("SelectionCheckbox")!=-1) && 
             (documentPara.forms[0].elements[checkboxOptionPos].name.indexOf("jato_boolean")==-1) &&
             (documentPara.forms[0].elements[checkboxOptionPos].name.indexOf(filterPara)!=-1) ) {
            break;
        }
        checkboxOptionPos++;
    }
    return checkboxOptionPos;
}

function getSelectedLinkWithFilter(documentPara, filterPara) {
    var viewLinkPos         = 0;
    var checkboxOptionPos   = 0;
    var selectedLink        = null;

    // set viewLinkPos to 1st View link (same row as 1st option box)
    while (documentPara.links[viewLinkPos]!=null) {
        if ( (documentPara.links[viewLinkPos].href.indexOf(".NameHref")!=-1) && 
             (documentPara.links[viewLinkPos].href.indexOf(filterPara)!=-1) ){
            break;
        }
        viewLinkPos++;
    }

    // set checkboxOptionPos to 1st option box link (same row as 1st view link)
    checkboxOptionPos = getNextCheckboxPos(documentPara, filterPara, checkboxOptionPos);

    // walk through all option boxes and ViewLinks in paralel
    while (documentPara.forms[0].elements[checkboxOptionPos]!=null) {
        if (documentPara.forms[0].elements[checkboxOptionPos].checked==true) {
            selectedLink = documentPara.links[viewLinkPos];
            break;
        }
        checkboxOptionPos = getNextCheckboxPos(documentPara, filterPara, checkboxOptionPos);
        viewLinkPos++;
    }
    return selectedLink;
}

function getSelectedIds(documentPara, filterPara) {
    var viewLinkPos         = 0;
    var checkboxOptionPos   = 0;
    var selectedLink        = null;
    var selectedIds         = "";

    // set viewLinkPos to 1st View link (same row as 1st option box)
    while (documentPara.links[viewLinkPos]!=null) {
        if ( (documentPara.links[viewLinkPos].href.indexOf(".NameHref")!=-1) && 
             (documentPara.links[viewLinkPos].href.indexOf(filterPara)!=-1) ){
            break;
        }
        viewLinkPos++;
    }

    // set checkboxOptionPos to 1st option box link (same row as 1st view link)
    checkboxOptionPos = getNextCheckboxPos(documentPara, filterPara, checkboxOptionPos);

    // walk through all option boxes and ViewLinks in paralel
    while (documentPara.forms[0].elements[checkboxOptionPos]!=null) {
        if (documentPara.forms[0].elements[checkboxOptionPos].checked==true) {
            // due to a bug in Lockhart jsessionids are sometimes inserted
            // they are removed here to not irritate the extract logic
            selectedLink = documentPara.links[viewLinkPos].href;
            nJsessionPos = selectedLink.indexOf("jsessionid");
            if (nJsessionPos>0) {
                nSemiPos = selectedLink.indexOf(";");
                nQuestPos= selectedLink.indexOf("?");
                selectedLink = selectedLink.substring(0, nSemiPos) + 
                               selectedLink.substring(nQuestPos, selectedLink.length);
            }
            selectedLink = extractByDelimiters("=", "&", selectedLink);
            if (selectedIds.length==0) {
                selectedIds = selectedLink;
            }
            else {
                selectedIds = selectedIds + " " + selectedLink;
            }
        }
        checkboxOptionPos = getNextCheckboxPos(documentPara, filterPara, checkboxOptionPos);
        viewLinkPos++;
    }
    
    return selectedIds;
}

function openWindow(openerWindow, openedWindow, windowHref, windowName, height, width, bGoldenMean) {
    
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
    
    return openedWindow;
}

function openProfileEditorWindow(docOrLink) {

    var selectedLink = docOrLink;

    if (String(docOrLink).indexOf("object")>-1) {
        selectedLink = getSelectedLink(docOrLink);
    }

    var selectedName = extractByDelimiters("=", "&", selectedLink.href);

    openWindow(window, null, '/apoc/manager/ProfileWindowFrameset?ProfileWindowFrameset.SelectedProfile='+selectedName, 'ProfileEditorWindow', 600, 1200);
}


function openProfileEditorWindowAtSection(docOrLink) {

    var selectedLink = docOrLink;

    if (String(docOrLink).indexOf("object")>-1) {
        selectedLink = getSelectedLink(docOrLink);
    }

    var selectedName = extractByDelimiters("=", "%5C%7C", selectedLink.href);
    var category = extractByDelimiters("%5C%7C", "&", selectedLink.href);
    if (window.top.name  == 'ProfileEditorWindow') {
        window.top.location = '/apoc/manager/ProfileWindowFrameset?ProfileWindowFrameset.SelectedProfile='+selectedName+'&PolicySettingsContent.CategoryHref='+category+'&SelectedEditorTab=1';
    } else {
        var win = openWindow(window, null, '/apoc/manager/ProfileWindowFrameset?ProfileWindowFrameset.SelectedProfile='+selectedName+'&PolicySettingsContent.CategoryHref='+category+'&SelectedEditorTab=1', 'ProfileEditorWindow', 600, 1200);
    }
}

function closePopups() {
    if (top.m_SearchWindow != null) {
        top.m_SearchWindow.close();
    } 
    if (top.m_ReportWindow != null) {
        top.m_ReportWindow.close();
    } 
    if (top.m_ProfileWindow != null) {
        top.m_ProfileWindow.close();
    } 
    if (top.m_BrowseTreeWindow != null) {
        top.m_BrowseTreeWindow.close();
    }
    if (top.m_SearchWindowBrowseTreeWindow != null) {
        top.m_SearchWindowBrowseTreeWindow.close();
    }
    if (top.m_ImportWindow != null) {
        top.m_ImportWindow.close();
    } 
    if (top.m_AssignWindow != null) {
        top.m_AssignWindow.close();
    }
    if (top.m_SyncWindow != null) {
        top.m_SyncWindow.close();
    }
    if (top.m_ChooserWindow != null) {
        top.m_ChooserWindow.close();
    }
    if (top.m_ColorChooserWindow != null) {
        top.m_ColorChooserWindow.close();
    }
    if (top.m_ContextLoginWindow != null) {
        top.m_ContextLoginWindow.close();
    }
    if (top.m_CopyMoveContextLoginWindow != null) {
        top.m_CopyMoveContextLoginWindow.close();
    }
    if (top.m_ProfileSearchWindow != null) {
        top.m_ProfileSearchWindow.close();
    } 
    if (top.m_CopyMoveWindow != null) {
        top.m_CopyMoveWindow.close();
    }
    if (top.m_ContextWizardWindow != null) {
        top.m_ContextWizardWindow.close();
    }
}

function openSearchWindow(sContext, sSearchText, sRestrictedEntity, sRestrictedEntityType) {

    var bLoad = true;
    if ( (top.m_SearchWindow!=null) && (top.m_SearchWindow.closed==false) ) {
        top.m_SearchWindow.focus();
        var sContext2 = top.m_SearchWindow.top.parameters.document.forms[0]['EntitiesSearchParameter.ContextHidden'].value;
        bLoad = (sContext!=sContext2);
    }
    
    
    if (bLoad) {
        top.m_ParamsLoaded=false;
        top.m_ResultsLoaded=false;
        top.m_ButtonsLoaded=false;
        var encodedRestrictedEntity = encodeURIComponent(sRestrictedEntity);
        var encodedRestrictedEntityType = encodeURIComponent(sRestrictedEntityType);
        top.m_SearchWindow=openWindow(window, null, '/apoc/manager/EntitiesSearchIndex?EntitesSearchIndex.ButtonSearchHref=a&ContextId='+sContext+'&RestrictedEntity='+encodedRestrictedEntity+'&RestrictedEntityType='+encodedRestrictedEntityType, 'searchWindow', 0.9, 700);
        top.m_SearchWindow.focus();
        top.m_SearchWindow.m_CurrentOpener=window;
    }

    if (sSearchText!=null && sSearchText.length>0) {
        m_sSearchText = sSearchText;
        submitSearchInterval = setInterval(submitSearchWindow, 250);
    }
}

function submitSearchWindow() {
    if ( (top.m_ParamsLoaded==true) &&
         (top.m_ResultsLoaded==true) && 
         (top.m_ButtonsLoaded==true) ) {
        clearInterval(submitSearchInterval);
        top.m_SearchWindow.top.parameters.document.forms[0]['EntitiesSearchParameter.SearchText'].value=m_sSearchText;
        top.m_SearchWindow.top.parameters.document.forms[0]['EntitiesSearchParameter.SearchButton'].disabled='';
        top.m_SearchWindow.top.parameters.document.forms[0]['EntitiesSearchParameter.SearchButton'].click();
    }
}

function taskButtonClick(taskButtonName) {
    var thisForm      = document.forms[0];
    var elementRunner = 0;
    if (thisForm != null) {
        while (thisForm.elements[elementRunner]!=null) {
            if (thisForm.elements[elementRunner].name.indexOf("."+taskButtonName)!=-1) {
                break;
            }
        }
    }
    if (thisForm.elements[elementRunner].disabled==false) {
        thisForm.elements[elementRunner].click();
    }
    return false;
}
    
