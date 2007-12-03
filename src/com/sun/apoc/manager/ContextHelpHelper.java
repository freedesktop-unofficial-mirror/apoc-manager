/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either
 * the GNU General Public License Version 2 only ("GPL") or
 * the Common Development and Distribution License("CDDL")
 * (collectively, the "License"). You may not use this file
 * except in compliance with the License. You can obtain a copy
 * of the License at www.sun.com/CDDL or at COPYRIGHT. See the
 * License for the specific language governing permissions and
 * limitations under the License. When distributing the software,
 * include this License Header Notice in each file and include
 * the License file at /legal/license.txt. If applicable, add the
 * following below the License Header, with the fields enclosed
 * by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by
 * only the CDDL or only the GPL Version 2, indicate your
 * decision by adding "[Contributor] elects to include this
 * software in this distribution under the [CDDL or GPL
 * Version 2] license." If you don't indicate a single choice
 * of license, a recipient has the option to distribute your
 * version of this file under either the CDDL, the GPL Version
 * 2 or to extend the choice of license to its licensees as
 * provided above. However, if you add GPL Version 2 code and
 * therefore, elected the GPL Version 2 license, then the
 * option applies only if the new code is made subject to such
 * option by the copyright holder.
 */

package com.sun.apoc.manager;

import com.iplanet.jato.RequestManager;
import com.iplanet.jato.util.HtmlUtil;
import com.iplanet.jato.util.NonSyncStringBuffer;
import com.sun.management.services.registration.Help;
import com.sun.management.services.registration.MgmtAppRegistrationServiceFactory;
import com.sun.management.services.registration.RegistrationInfo;
import com.sun.web.ui.common.CCClientSniffer;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.common.CCI18N;
import com.sun.web.ui.common.CCStyle;
import com.sun.web.ui.common.CCSystem;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

// Note that this entire class is needed only because of a bug in Lockhart 3.x
// in which the helpFileName attribute of the helpwindow tag is ignored. Hence 
// most of this code is a rehashed version of the methods in CCHelpWindowTag from v2.2.6
public class ContextHelpHelper {
    // Parameter names.
    public static final String PARAM_APPNAME        = "appName";
    public static final String PARAM_HELPFILE        = "helpFile";
    public static final String PARAM_MASTHEADTITLE    = "mastheadTitle";
    public static final String PARAM_PAGETITLE        = "pageTitle";
    public static final String PARAM_WINDOWTITLE    = "windowTitle";
    public static final String PARAM_SHOWCLOSEBUTTON    = "showCloseButton";
    public static final String PARAM_HELPLOGOWIDTH    = "helpLogoWidth";
    public static final String PARAM_HELPLOGOHEIGHT    = "helpLogoHeight";

    // Set default window values.
    private static final String DEFAULT_HEIGHT  = "500";
    private static final String DEFAULT_WIDTH   = "750";
    private static final String DEFAULT_HELPDIR = "html";
    private static final String DEFAULT_TARGET  = "helpWindow";

        // Set default window values.
    private static final String HELP2_DEFAULT_HEIGHT = "500";
    private static final String HELP2_DEFAULT_WIDTH  = "750";
    private static final String HELP2_DEFAULT_TARGET = "help2_Window";

    // Display URLs.
    private static final String HELP2_DISPLAY_URL = "/cchelp2/Help2";

    private String mAppName;
    private String mBundleId;
    private String mFilename;
    private String mTooltip;
    private String mText;
    private String mUrlStart;
    private String mDisplayUrl;
    private CCClientSniffer mClientSniffer;
        
    public ContextHelpHelper(String appName, String bundleId, String filename, 
                             String tooltip, String text) {
        mAppName = appName;
        mBundleId = bundleId;
        mFilename = filename;
        mTooltip = tooltip;
        mText = text;
        mUrlStart = getUrlStart();
        mDisplayUrl = getDisplayUrl();
        mClientSniffer = getClientSniffer();
    }

    public String getHelpLink() {
        Help help = getHelp();
        String height = "";
        String status = "";
        String width =  "";
        String windowTitle = "";
        String logoWidth = "";
        String logoHeight = "";
        if (help != null) {
            height = help.getHeight();
            status = help.getStatus();
            width =  help.getWidth();
            windowTitle = help.getWindowTitle();
            logoWidth = help.getHelpLogoWidth();
            logoHeight = help.getHelpLogoHeight();
        }
        String pageTitle = "help.pagetitle";

        NonSyncStringBuffer buffer =  new NonSyncStringBuffer();        
        buffer.append("<a")
            .append(" href=\"")
            .append(mDisplayUrl.toString());

        NonSyncStringBuffer paramsBuffer =
        new NonSyncStringBuffer();
        addEncodedParameter(paramsBuffer, PARAM_APPNAME, mAppName);
        String file = getLocalizedTextFile(mFilename);
        addEncodedParameter(paramsBuffer, PARAM_HELPFILE, file);
        addEncodedParameter(paramsBuffer, PARAM_WINDOWTITLE, Toolbox2.getI18n(windowTitle));
        addEncodedParameter(paramsBuffer, PARAM_PAGETITLE, Toolbox2.getI18n(pageTitle));
        addEncodedParameter(paramsBuffer, PARAM_HELPLOGOWIDTH, logoWidth);
        addEncodedParameter(paramsBuffer, PARAM_HELPLOGOHEIGHT, logoHeight);
        buffer.append(paramsBuffer.toString());
        buffer.append("\"");
        appendAttribute(buffer, "class", CCStyle.HELP_PAGE_LINK);
        appendAttribute(buffer, "target", DEFAULT_TARGET);
        appendAttribute(buffer, "title", Toolbox2.getI18n(mTooltip));
        if (status != null) {
            NonSyncStringBuffer tmpBuffer = new NonSyncStringBuffer();

            tmpBuffer.append("javascript:")
            .append("window.status='")
            .append(Toolbox2.getI18n(status))
            .append("'; return true");

            appendAttribute(buffer, "onmouseover", tmpBuffer.toString());
            appendAttribute(buffer, "onfocus", tmpBuffer.toString());

            tmpBuffer = new NonSyncStringBuffer("javascript: window.status=''; return true");

            appendAttribute(buffer, "onmouseout", tmpBuffer.toString());
            appendAttribute(buffer, "onblur", tmpBuffer.toString());
        }

        appendAttribute(buffer, "onclick", getOpenWindowJavascript(
            "", 
            DEFAULT_TARGET,
            Integer.parseInt((height != null) 
            ? height : DEFAULT_HEIGHT),
            Integer.parseInt((width != null)
            ? width : DEFAULT_WIDTH),
            "scrollbars,resizable"));

        buffer.append(">");
        buffer.append(mText);
        buffer.append("</a>");
        return buffer.toString();
    }


    private void appendAttribute(NonSyncStringBuffer buffer, String name, String value) {
        buffer.append(" ")
            .append(name)
            .append("=\"")
            .append(HtmlUtil.escapeQuotes(value))
            .append("\"");
    }
        
    private String getOpenWindowJavascript(String url, String name,
        int height, int width, String features) {
        NonSyncStringBuffer buffer = new NonSyncStringBuffer();

        buffer.append("javascript:var win = window.open('")
            .append(url)
            .append("','")
            .append(name)
            .append("','")
            .append("height=")
            .append(height)
            .append(",width=")
            .append(width)
            .append(",top='+((screen.height-(screen.height/1.618))-(")
            .append(height)
            .append("/2))+',left='+((screen.width-")
            .append(width)
            .append(")/2)+'");

        if (features != null) {
            buffer.append(",").append(features);
            }

        buffer.append("')");
        buffer.append(";win.focus()");
        return buffer.toString();
    }
    
    private void addEncodedParameter(NonSyncStringBuffer buf, String name, String value) {
        if (buf == null || name == null || value == null) {
            return;
        }

        try {
            buf.append((buf.length() == 0) ? "?" : "&");
            buf.append(name)
            .append("=")
            .append(URLEncoder.encode(value, CCI18N.UTF8_ENCODING));
        } catch (UnsupportedEncodingException ex) {
        }
    }

    private String getLocalizedTextFile(String file) {
        if (file == null) {
            return null;
        }

        NonSyncStringBuffer buffer = new NonSyncStringBuffer(256);
        String localizedFile = null;
        buffer.append(mAppName)
            .append(CCSystem.URL_SEPARATOR)
            .append(DEFAULT_HELPDIR);

        // if the buffer doesn't start with "/", prepend it
        if (!buffer.toString().startsWith(CCSystem.URL_SEPARATOR)) {
            buffer.insert(0, CCSystem.URL_SEPARATOR);
        }

        HttpServletRequest request = RequestManager.getRequestContext().getRequest();
        String localAppName = request.getContextPath();
        String prefixedPath = buffer.toString();

        file = "help" + CCSystem.URL_SEPARATOR + file;

        // Create a list of locales to test.
        ArrayList locales = new ArrayList();
        locales.add(CCI18N.getTagsLocale(RequestManager.getRequestContext().getRequest()));
        locales.add(Locale.ENGLISH);

        prefixedPath = prefixedPath.substring(localAppName.length());
        ServletContext appContext = RequestManager.getRequestContext().getServletContext();

        for (int i = 0; i < locales.size(); i++) {
            String urlPath = prefixedPath + CCSystem.URL_SEPARATOR +
                (Locale) locales.get(i) + CCSystem.URL_SEPARATOR + file;

            urlPath = urlPath.replaceAll(
                CCSystem.URL_SEPARATOR + CCSystem.URL_SEPARATOR + "*",
                CCSystem.URL_SEPARATOR);

            String testPath = urlPath;
            int index = testPath.indexOf("?");
            if (index >= 0) {
                testPath = urlPath.substring(0, index);
            }
            try {
                if (appContext.getResource(testPath) != null) {
                    localizedFile = mUrlStart + localAppName + urlPath;
                    break;
                }
            } catch (MalformedURLException muex) {
                CCDebug.trace3("getResource failed for " + urlPath);
            }
        }

        return localizedFile;
    }

    private boolean isIe() {
        return getClientSniffer().isIe();
    }
    
    private boolean isNav4() {
        return getClientSniffer().isNav4();
    }

    private boolean isGecko() {
        return getClientSniffer().isGecko();
    }   

    private String getDisplayUrl() {
        NonSyncStringBuffer displayURL =  new NonSyncStringBuffer(mUrlStart);
        // Set the display URL to the JSP associated with the correct
        // browser.
        String appContext = RequestManager.getRequestContext().getRequest().getContextPath();
        if (isIe()) {
            displayURL.append(appContext + 
                HELP2_DISPLAY_URL + "Ie");
        } else if (isNav4()) {
            displayURL.append(appContext + 
                HELP2_DISPLAY_URL + "Nav4");
        } else if (isGecko()) {
            displayURL.append(appContext + 
                HELP2_DISPLAY_URL + "Nav6up");
        } else {
            // Default display will be for Nav6up
            displayURL.append(appContext + 
                HELP2_DISPLAY_URL + "Nav6up");
        }       
        return displayURL.toString();
    }
    
    private Help getHelp() {
        Help help = null;
        String pluginName = null;
        String appDir = null;
        try {
            if (mAppName != null) {
                pluginName = MgmtAppRegistrationServiceFactory.getPluginName(mAppName);
                if (pluginName != null) {
                    appDir = MgmtAppRegistrationServiceFactory.getRegisteredAppDir(pluginName);
                    RegistrationInfo regInfo = MgmtAppRegistrationServiceFactory. getRegistrationInfo(pluginName);
                    if (regInfo != null) {
                        help = regInfo.getManagementApp().getHelp();
                    } 
                }
            }
        } catch (NoClassDefFoundError e) {
            CCDebug.trace3("MgmtAppRegistrationServiceFactory not found");
        }
        return help;
    }
        
    private String getUrlStart() {
        HttpServletRequest request = RequestManager.getRequestContext().getRequest();
        String port = Integer.toString(request.getServerPort());
        NonSyncStringBuffer urlbuffer = new NonSyncStringBuffer(request.getScheme());
        urlbuffer.append("://")
           .append(request.getServerName())
           .append(":")
           .append(port);
        return urlbuffer.toString();
    }
    
    private CCClientSniffer getClientSniffer() {
        if (mClientSniffer == null) {
            mClientSniffer = new CCClientSniffer(RequestManager.getRequestContext().getRequest());
        }
        return mClientSniffer;
    }
}

