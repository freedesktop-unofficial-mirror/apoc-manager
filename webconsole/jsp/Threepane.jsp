<%@page info="Root Frameset" language="java"%>
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

<jato:useViewBean className="com.sun.apoc.manager.ThreepaneViewBean">

<html>
    <head>
        <title><jato:text name="ProdName"/></title>
    </head>

    <frameset rows="180, *" border="2" framespacing="2">

    	<frame name="masthead" title="Masthead" scrolling="no"
            marginwidth="0" marginheight="0" border="1" framespacing="1"
            src="/apoc/manager/Masthead" longdesc="<jato:text name='Desc'/>">
    
            <frameset cols="30%, *" border="2" framespacing="2">
    
                <frame name="navigation" title="Navigator"
                    scrolling="AUTO" marginwidth="0" marginheight="0" border="1"
                    framespacing="1" src="/apoc/manager/EntitiesNavigation" longdesc="<jato:text name='Desc'/>">
                <frame name="content" title="Content" scrolling="AUTO"
                    marginwidth="0" marginheight="0" border="1" framespacing="1"
                    src="/apoc/manager/EntityContent" longdesc="<jato:text name='Desc'/>">

            </frameset>

    </frameset>

    <noframes>
    	<body bgcolor="#FFFFFF" text="#000000">
            <jato:text name="NoFrames" />
        </body>
    </noframes>

</html>
</jato:useViewBean>

