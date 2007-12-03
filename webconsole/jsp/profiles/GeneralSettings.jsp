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

<jato:pagelet>
    <jato:containerView name="GeneralSettings">
    
        <script type="text/javascript">
            updateButtonsArea();
        </script>  

        <cc:pagetitle name="PageTitle" bundleID="apocBundle"
            pageTitleText="APOC.profilewnd.general.title" 
            showPageTitleSeparator="true"
            pageTitleHelpMessage="APOC.profilewnd.general.help"
            showPageButtonsTop="true"
            showPageButtonsBottom="false">
  
            <!-- Property Sheet -->
            <cc:propertysheet name="Properties" showJumpLinks="false" bundleID="apocBundle"/>	
            <cc:hidden name="OriginalName" elementId="originalName" />
            <cc:hidden name="OriginalComment" elementId="originalComment" />
            <cc:hidden name="IsNewProfile" elementId="isNewProfile" />
        </cc:pagetitle>
        <script type="text/javascript">
        <!--
            if (document.getElementById("isNewProfile").value == "true") {
                enableSaveButtons();
            }
        // -->
        </script> 
    </jato:containerView>
</jato:pagelet>
