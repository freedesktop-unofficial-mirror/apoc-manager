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


package com.sun.apoc.templates.handler;
import com.sun.web.ui.common.CCDebug;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ActionHandlerClassLoader extends ClassLoader {

    private String m_packageDir = null;
    
    public ActionHandlerClassLoader() {
        super(ActionHandlerClassLoader.class.getClassLoader());
    }

    public Class findClass(String name) throws ClassNotFoundException {
        Class c = null;
        try {
            File libDir = new File("./apoc/packages/" + m_packageDir + "/lib/");
            File[] files = libDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.getName().substring(file.getName().lastIndexOf(".")).equals(".jar")) {
                    JarFile jar = new JarFile(file);
                    String jarEntryName = name;
                    if (name.endsWith(".class")) {
                        jarEntryName = name + ".class";
                    }
                    JarEntry entry = jar.getJarEntry(jarEntryName + ".class");
                    if (entry != null) {
                        InputStream input = jar.getInputStream(entry);
                        byte[] classBytes = new byte[input.available()];
                        input.read(classBytes);
                        c = defineClass(name, classBytes, 0, classBytes.length);
                        jar.close();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            CCDebug.trace3(e.toString());
        }

        return c;
    }
    
    public void setPackageDir(String packageDir) {
        m_packageDir = packageDir;
    }
}
