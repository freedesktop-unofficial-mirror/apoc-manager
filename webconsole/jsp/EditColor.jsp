<%@ page info="EditColor" language="java" %>
<%@ page contentType="text/html;charset=UTF-8" %>
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

<jato:useViewBean className="com.sun.apoc.manager.EditColorViewBean">

<!-- Header -->
 <cc:header pageTitle="APOC.setcolor.title" copyrightYear="2003" baseName="com.sun.apoc.manager.resource.apoc_manager" bundleID="apocBundle" isPopup="true" onLoad="setCursor(cursorPos);setCol(curcol);updateName();setInitialCursorPos();">
  <cc:stylesheet />
  
    <script type="text/javascript" src="/apoc/js/EditColor.js"></script>
    <script type="text/javascript">
    <!--
        var m_OpenerTop = top.opener;
        while (m_OpenerTop.top.opener!=null) {
            m_OpenerTop = m_OpenerTop.top.opener;
        }
        m_OpenerTop = m_OpenerTop.top;

        setInterval("setWindowHandle()", 1000);
        function setWindowHandle() {
            m_OpenerTop.m_BrowseTreeWindow= top;
        }
    // -->
    </script> 
  <cc:form name="EditColorForm" method="post" defaultCommandChild="SetColorButton">

   <!-- Masthead -->
   <cc:secondarymasthead name="SecondaryMH" bundleID="apocBundle" />

   <!-- Page Title (Edit Color) -->
   <cc:pagetitle name="PageTitle" bundleID="apocBundle" pageTitleHelpMessage="APOC.setcolor.introtext" pageTitleText="APOC.setcolor.title" showPageTitleSeparator="true" showPageButtonsTop="false" showPageButtonsBottom="true">

    <!-- Layout Table with three columns -->
    <div class="MstDivUsr">
     <table align="center" cellpadding="0" cellspacing="0" border="0" title="" width="97%">
      <tr>
       <td valign="top" align="left" width="128">
        <table cellpadding="0" cellspacing="6" border="0" width="100%" title="">
         <!-- Original Color heading and sample -->
         <tr>
          <td>
           <cc:label name="OriginalHeading" bundleID="apocBundle" defaultValue="APOC.setcolor.original.heading" elementId='bgorgcolor' />
          </td>
         </tr>
         <tr>
          <td bgcolor="<cc:text name='BgOrgColor' bundleID='apocBundle' defaultValue='#ffffff' />" height="30" width="128">
           <img src="/com_sun_web_ui/images/other/dot.gif" alt="<cc:text name='AltBgOrgColor' />" width="128" height="10" border="0" onMouseOver="window.status=this.alt;return true;" onMouseOut="window.status=''" id="bgorgcolor" />
          </td>
         </tr>
         <!-- Restore Original Color button -->
         <tr>
          <td>
           <table cellpadding="0" cellspacing="0" border="0" width="100%" title="">
            <tr>
             <td>
              <img src="/com_sun_web_ui/images/other/dot.gif" alt="" width="1" height="6" border="0" />
             </td>
            </tr>
            <tr>
             <td align="left">
              <cc:button name="RestoreOrigColorButton" bundleID="apocBundle" type="secondary" defaultValue="APOC.setcolor.remove.button" onClick="restoreOrigColorButton();return false;" tabIndex="1" />
             </td>
            </tr>
           </table>
          </td>
         </tr>
         <!-- Color Wheel -->
         <tr>
          <td class="TblTdHdr" align="left">
           <span class="LblLev2Txt">
            <cc:label name="WheelHeading" bundleID="apocBundle" defaultValue="APOC.setcolor.wheel.heading" />
           </span>
          </td>
         </tr>
         <tr>
          <td>
           <img id="colorpic" src="/apoc/images/colors.png" alt="<cc:text name='AltWheel' />" width="128" height="128" border="0" onClick="setFromImage(event);" onMouseDown="setFromImage(event);" onDragOver="setFromImage(event);" onMouseOver="window.status=this.alt;return true;" onMouseOut="window.status=''" />
          </td>
         </tr>
         <!-- Luminance Adjuster -->
         <tr>
          <td>
           <table border=0 cellspacing=0 cellpadding=0 width=128 height=14 id="slider" summary="<cc:text name='SummarySlider' />" onClick="setFromSlider(event);" onMouseDown="setFromSlider(event);" onDragOver="setFromSlider(event);">
            <tr>
             <tr>
              <td>
               <img src="/com_sun_web_ui/images/other/dot.gif" height="5" alt="" />
              </td>
             </tr>
             <td>
              <table border=0 cellspacing=0 cellpadding=0 width=128 height=14>
               <tr>
                <script type="text/javascript">
                 for (i=0;i<128;i++)
                 document.write(
                 "<td id=\"sc"+(i+1)+"\" height=\"14\" width=\"1\"></td>"
                 );
                </script>
               </tr>
              </table>
             </td>
            </tr>
           </table>
          </td>
         </tr>
        </table>
       </td>
       <td valign="top" align="center">
        <img src="/com_sun_web_ui/images/other/dot.gif" alt="" width="15" height="1" border="0" />
       </td>
       <td valign="top" align="center">
        <table cellpadding="0" cellspacing="6" border="0" width="100%" title="">
         <!-- Current Color heading and sample -->
         <tr>
          <td>
           <cc:label name="ColorHeading" bundleID="apocBundle" defaultValue="APOC.setcolor.color.heading" elementId="colorheading" />
          </td>
         </tr>
         <tr>
          <td bgcolor="<cc:text name='BgColor' bundleID='apocBundle' />" height="30" width="170" id="thecell">
           <img src="/com_sun_web_ui/images/other/dot.gif" alt="<cc:text name='AltBgColor' />" width="170" height="30" border="0" onMouseOver="window.status=this.alt;return true;" onMouseOut="window.status=''" id="colorheading" />
          </td>
         </tr>
         <!-- Name, RGB, HSL and Hex entry fields -->
         <tr>
          <td>
           <table cellpadding="0" cellspacing="5" border="0" width="100%" title="">
            <tr>
             <td colspan=3>
              <table cellpadding="0" cellspacing="2" border="0" width="100%">
               <tr>
                <td align="left">
                 <cc:label name="NameLabel" bundleID="apocBundle" defaultValue="APOC.navigation.name.label" elementName="NameValue" />
                </td>
                <td align="right">
                 <cc:textfield name="NameValue" tabIndex="2" />
                </td>
               </tr>
              </table>
             </td>
            </tr>
            <tr>
             <td width="45%" align="right">
              <apoc:spinProperty name="Red" defaultValue="APOC.setcolor.red.heading" upTitle="APOC.setcolor.red.increase" upAction="incrementColor('Red')" downTitle="APOC.setcolor.red.decrease" downAction="decrementColor('Red')" size="3" tabIndex="3" onChange="setFromRGB()" />
             </td>
             <td width="10%" align="right"></td>
             <td width="45%" align="right">
              <apoc:spinProperty name="Hue" defaultValue="APOC.setcolor.hue.heading" upTitle="APOC.setcolor.hue.increase" upAction="incrementColor('Hue')" downTitle="APOC.setcolor.hue.decrease" downAction="decrementColor('Hue')" size="3" tabIndex="12" onChange="setFromHSL()" />
             </td>
            </tr>
            <tr>
             <td width="45%" align="right">
              <apoc:spinProperty name="Green" defaultValue="APOC.setcolor.green.heading" upTitle="APOC.setcolor.green.increase" upAction="incrementColor('Green')" downTitle="APOC.setcolor.green.decrease" downAction="decrementColor('Green')" size="3" tabIndex="6" onChange="setFromRGB()" />
             </td>
             <td width="10%" align="right"></td>
             <td width="45%" align="right">
              <apoc:spinProperty name="Sat" defaultValue="APOC.setcolor.sat.heading" upTitle="APOC.setcolor.sat.increase" upAction="incrementColor('Saturation')" downTitle="APOC.setcolor.sat.decrease" downAction="decrementColor('Saturation')" size="3" tabIndex="15" onChange="setFromHSL()" />
             </td>
            </tr>
            <tr>
             <td width="45%" align="right">
              <apoc:spinProperty name="Blue" defaultValue="APOC.setcolor.blue.heading" upTitle="APOC.setcolor.blue.increase" upAction="incrementColor('Blue')" downTitle="APOC.setcolor.blue.decrease" downAction="decrementColor('Blue')" size="3" tabIndex="9" onChange="setFromRGB()" />
             </td>
             <td width="10%" align="right"></td>
             <td width="45%" align="right">
              <apoc:spinProperty name="Lum" defaultValue="APOC.setcolor.lum.heading" upTitle="APOC.setcolor.lum.increase" upAction="incrementColor('Luminance')" downTitle="APOC.setcolor.lum.decrease" downAction="decrementColor('Luminance')" size="3" tabIndex="18" onChange="setFromHSL()" />
             </td>
            </tr>
            <tr>
             <td colspan="3">
              <table cellpadding="0" cellspacing="0" border="0" width="100%">
               <tr>
                <td align="left">
                 <cc:label name="HtmlLabel" bundleID="apocBundle" defaultValue="APOC.setcolor.html.heading" elementName="HtmlValue" />
                </td>
                <td align="right">
                 <cc:textfield name="HtmlValue" onChange="setFromHTML()" tabIndex="21" />
                </td>
               </tr>
              </table>
             </td>
            </tr>
           </table>
          </td>
          <!-- Add and Replace buttons -->
          <tr>
           <td align="left">
            <cc:button name="AddButton" bundleID="apocBundle" type="secondary" defaultValue="APOC.setcolor.add.button" onClick="javascript:if (document.getElementById('imgCol'+cursorPos).alt=='') { if(isColorNameUnique()) { addButton(); } else { alert(duplicateNameText); } } else { alert(cantAddText); } return false;" tabIndex="22" />
           </td>
          </tr>
          <tr>
           <td align="left">
            <cc:button name="ReplaceButton" bundleID="apocBundle" type="secondary" defaultValue="APOC.setcolor.replace.button" onClick="javascript:if (cursorPos>paletteLength-1) { alert(replaceText); } else { replaceButton(); } return false;" tabIndex="22"></cc:button>
           </td>
          </tr>
         </tr>
        </table>
       </td>
       <td valign="top" align="center">
        <img src="/com_sun_web_ui/images/other/dot.gif" alt="" width="25" height="1" border="0" />
       </td>
       <td valign="top" align="left" width="128">
        <table cellpadding="0" cellspacing="6" border="0" title="" width="100%">
         <!-- Color Palette -->
         <tr>
          <td>
           <cc:label name="PaletteHeading" bundleID="apocBundle" defaultValue="APOC.setcolor.palette.heading" elementId="paletteheading" />
          </td>
         </tr>
         <tr>
          <td>
           <!-- Custom Colors -->
           <table border=1 cellpadding=0 cellspacing=0>
            <jato:tiledView name="PaletteRowTiledView">
             <tr height=14>
              <jato:tiledView name="PaletteTDTiledView">
               <td height=14 width=14 bgcolor="<cc:text name='PresetColor' />" id="defCol<cc:text name='PresetColorNo' />" title="<cc:text name='PresetColorName' />">
                <a href="javascript:tileNumber=<cc:text name='PresetColorNo' />;setCurrentColorTo(tileNumber);">
                 <img src="<cc:text name='Image' />" width=14 height=14 border=0 id="imgCol<cc:text name='PresetColorNo' />" alt="<cc:text name='PresetColorName' />" onmouseover="window.status=this.alt;return true;" onmouseout="window.status='';return true;"></a>
               </td>
              </jato:tiledView>
             </tr>
            </jato:tiledView>
           </table>
          </td>
         </tr>
         <!-- Delete and Restore colors buttons -->
         <tr>
          <td align="left">
           <cc:button name="RemoveButton" bundleID="apocBundle" type="secondary" defaultValue="APOC.setcolor.remove.button" onClick="javascript:removeButton();return false;" tabIndex="24" />
          </td>
         </tr>
         <tr>
          <td align="left">
           <cc:button name="RestoreButton" bundleID="apocBundle" type="secondary" defaultValue="APOC.setcolor.restore.button" onClick="javascript: if (confirm(questionText)) { restoreButton(); } return false;" tabIndex="25" />
          </td>
         </tr>
        </table>
       </td>
      </tr>
     </table>
    </div>
   </cc:pagetitle>

   <script type="text/javascript">
   <!--
    // Set string for confirm question up so it can be used later
    questionText="<cc:text name='RestoreConfirm' />";
    replaceText="<cc:text name='ReplaceAlert' />";
    cantAddText="<cc:text name='AddAlert' />";
    removeText="<cc:text name='RemoveAlert' />";
    duplicateNameText="<cc:text name='NameAlert' />";
    //Set the localized string for no color name
    var localizedNoName = "<cc:text name='LocalizedNoName' />";
    // Let the two palette arrays only represent what is seen on screen on the palette
    paletteColors = new Array(96);
    paletteNames = new Array(96);
    paletteLength = 96;
    for (i=0;i<96;i++){
     elementTD = document.getElementById("defCol"+i);
     elementImg = document.getElementById("imgCol"+i);
     elementBgColor = elementTD.bgColor;
     elementName = elementImg.alt;
     paletteNames[i] = elementName;
     paletteColors[i] = elementBgColor;

     if (elementName!="" && paletteLength!=96)
     paletteLength = 96;
     if (elementName=="" && paletteLength==96)
     paletteLength = i;
    }
    //-->
   </script>

   <cc:hidden name="HiddenValues" elementId="hiddenvalues" />
   <cc:hidden name="HiddenNames" elementId="hiddennames" />
  </cc:form>
  <img id="cross" src="/apoc/images/cross.gif" style="position:absolute; left:0px; top:0px" alt="">
  <img id="sliderarrow" src="/apoc/images/arrow.gif" style="position:absolute; left:0px top:0px" alt="">

 </cc:header>
</jato:useViewBean>
