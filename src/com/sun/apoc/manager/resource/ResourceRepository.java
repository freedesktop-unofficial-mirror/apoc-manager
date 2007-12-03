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


package com.sun.apoc.manager.resource;

import com.sun.web.ui.common.CCDebug;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import javax.servlet.http.HttpServletRequest;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.util.NonSyncStringBuffer;


public class ResourceRepository {
    
    public static final String CACHED_BUNDLE = "CachedResourceBundle";
    
    public static final String PROPS_SUFFIX = ".properties";
    public static final String JAR_SUFFIX   = ".jar";
    
    public static final String RES_DIRNAME       = "/res/";
    public static final String CLASSES_DIRNAME   = "/classes/";
    
    private static ResourceRepository m_defaultRepository = null;
    private HashMap m_resourceBundles = null;
    
    
    public static ResourceRepository getDefaultRepository() {
        if (m_defaultRepository == null) {
            m_defaultRepository = new ResourceRepository();
        }
        return m_defaultRepository;
    }
    
    
    protected ResourceRepository() {
        CCDebug.initTrace();
        m_resourceBundles = new HashMap();
    }
    
    
    public void addResource(File file) {
        if (file.exists()) {
            try {    
                CCDebug.trace3("Examine resources of file " + file.getName());
                if (file.getName().endsWith(PROPS_SUFFIX)) {
                    String path = file.getPath();
                    if (path.indexOf(CLASSES_DIRNAME) != -1) {
                        path = path.substring(path.indexOf(CLASSES_DIRNAME) + CLASSES_DIRNAME.length());
                        m_resourceBundles.put(path, file);
                    } else if (path.indexOf(RES_DIRNAME) != -1) {
                        path = path.substring(path.indexOf(RES_DIRNAME) + RES_DIRNAME.length());
                        m_resourceBundles.put(path, file);
                    }
                } else if (file.getName().endsWith(JAR_SUFFIX)) {
                    JarFile jarFile = new JarFile(file);
                    Enumeration en = jarFile.entries();
                    while (en.hasMoreElements()) {
                        ZipEntry entry = (ZipEntry) en.nextElement();
                        if (entry.getName().endsWith(PROPS_SUFFIX)) {
                            m_resourceBundles.put(entry.getName(), jarFile);
                        }
                    }
                }
            } catch (IOException e) {
                CCDebug.trace1("Could not load " + file.getName(), e);
            }
        }
    }
    
    
    // Helper method: Traverse through the prefered locale list and try to find 
    // a matching resource bundle
    public ResourceBundle getBundle(String baseName) {
        HttpServletRequest request = RequestManager.getRequest();
        ResourceBundle bundle = null;
        
        // try to find a cached resource bundle
        NonSyncStringBuffer key = new NonSyncStringBuffer(CACHED_BUNDLE);
        key.append(baseName);
        bundle = (ResourceBundle) request.getAttribute(key.toString());
        
        // okay, it is not in the cache, thus we need to find a matching 
        // resource bundle
        if (bundle == null) {
            ArrayList locales = (ArrayList) Collections.list(request.getLocales());
            locales.add(new Locale(""));
            locales.add(new Locale("en"));
            locales.add(new Locale("en", "US"));
            for (int i = 0; i < locales.size(); i++) {
                Locale locale = (Locale) locales.get(i);
                bundle = getBundle(baseName, locale);
                if (bundle != null) {
                    // cache the found bundle
                    request.setAttribute(key.toString(), bundle);
                    break;
                }
            }
        }
        return bundle;
    }
    

    // Helper method to create resource bundle suffixes.
    static private String getSuffix(String language, String country,
        String variant) {
        NonSyncStringBuffer buffer = new NonSyncStringBuffer();
        String ext = "_";
        if (language.length() > 0)
            buffer.append(ext + language);
        if (country.length() > 0)
            buffer.append(ext + country);
        if (variant.length() > 0)
            buffer.append(ext + variant);
        return buffer.toString();
    }    
    
    
    // Get a matching resource bundle for the specified locale    
    public ResourceBundle getBundle(String baseName, Locale locale) {
        ResourceBundle bundle = null;
        if (baseName != null) {
            // Create a list of ordered locale suffixes to test.
            ArrayList suffix = new ArrayList();
    
            // Add language_country_variant suffix.
            if (locale.getLanguage().length() > 0
                && locale.getCountry().length() > 0
                && locale.getVariant().length() > 0) {
                suffix.add(getSuffix(locale.getLanguage(), locale.getCountry(),
                locale.getVariant()));
            }
            // Add language_country suffix.
            if (locale.getLanguage().length() > 0
                && locale.getCountry().length() > 0) {
                suffix.add(getSuffix(locale.getLanguage(), locale.getCountry(),
                ""));
            }
            // Add language suffix.
            if (locale.getLanguage().length() > 0) {
                suffix.add(getSuffix(locale.getLanguage(), "", ""));
            }
            // Add default suffix.
            suffix.add("");
                        
            // Load the resource bundle best matching suffix list.
            for (int i = 0; i < suffix.size(); i++) {
                String fullBaseName =
                    new NonSyncStringBuffer(baseName).append(suffix.get(i)).toString();
                bundle = loadBundle(fullBaseName);
                if (bundle != null) {
                    break;
                }
            }
        }
        return bundle;            
    }
    

    // Load the resource bundle from the jar file
    public ResourceBundle loadBundle(String bundleName) {
        PropertyResourceBundle bundle = null;
        String normalizedName = bundleName.replace('.', '/');
        normalizedName = new StringBuffer(normalizedName).append(PROPS_SUFFIX).toString();
        Object entry = m_resourceBundles.get(normalizedName);
        if (entry != null) {
            try {
                InputStream is = null;
                if (entry instanceof JarFile) {
                    JarFile jarFile = (JarFile) entry;
                    is = jarFile.getInputStream(new ZipEntry(normalizedName));
                } else {
                    is = new FileInputStream((File) entry);
                }    
                bundle = new PropertyResourceBundle(is);
            } catch (IOException e) {
                CCDebug.trace1("Could not load " + bundleName, e);
            }
        }
        return bundle;
    }
    
    
    // Return the localized message for the specified key and locale    
    public String getMessage(String key, String baseName, Locale locale) {
        String message = null;
        ResourceBundle bundle = getBundle(baseName, locale);
        if (bundle != null) {
            try {
                message = bundle.getString(key);
                return message;
            } catch (MissingResourceException e) {
                CCDebug.trace3("Could not retrieve resource for key: " +  key);
            }
        }
        return key;
    }
    
    
    // Return the localized message for the specified key
    public String getMessage(String key, String baseName) {
        String message = getValidMessage(key, baseName);
        if (message != null) {
            return message;
        } else {
            return key;
        }    
    }
    
    
    // Return the localized message for the specified key (or null if not exist)
    public String getValidMessage(String key, String baseName) {
        String message = null;
        ResourceBundle bundle = getBundle(baseName);
        if (bundle != null) {
            try {
                message = bundle.getString(key);
                return message;
            } catch (MissingResourceException e) {
                // silently ignore this as the caller will be notified via
                // a null return value
            }
        }
        return null;
    }
    
    
    // Deprecated. Should be removed with the next release.
    public String getValidMessage(String key, String baseName, HttpServletRequest request) {
        return getValidMessage(key, baseName);
    }
    
    
    public String getMessage(String key, String baseName, HttpServletRequest request) {
        return getMessage(key, baseName);
    }
}

