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

import com.sun.web.ui.common.CCDebug;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;


public class UploadFileBean {
    
    public static final String PAGE_NAME            = "UploadFile";
    public static final String DEFAULT_DISPLAY_URL  = "/jsp/UploadFile.jsp";
    public static final String CHILD_MASTHEAD       = "Masthead";
    public static final String CHILD_UPLOADED       = "Uploaded";
    private static final String PACKAGES_DIR        = "apoc/packages/";
    private String filename, filepath, dirname;
    
    public boolean doUpload(HttpServletRequest request) throws IOException {
        
        // Create the input stream to read the request
        ServletInputStream in = request.getInputStream();
        byte[] line = new byte[128];
        int i = in.readLine(line, 0, 128);
        if (i < 3)
            return false;
        int boundaryLength = i - 2;
        String boundary = new String(line, 0, boundaryLength);
        
        while( i != -1) {
            String newLine = new String(line, 0, i);
            if (newLine.startsWith("Content-Disposition: form-data; name=\"")) {
                if (newLine.indexOf("filename=\"") != -1) {
                    setFilename(new String(line, 0, i-2));
                    if (filename == null){
                        CCDebug.trace3("The filename is null");
                        return false;
                    }
                    // next is the file content
                    i = in.readLine(line, 0, 128);
                    // blank line
                    i = in.readLine(line, 0, 128);
                    
                    ZipEntry entry = null;
                    ZipInputStream zipIn = new ZipInputStream((InputStream) in);
                    
                    while ((entry = zipIn.getNextEntry()) != null){
                        // The entry is a directory - make a directory
                        if (entry.isDirectory()){
                            // test is there multiple directories listed in the
                            // entry
                            makeMultipleDirs(entry);
                            File dir = new File(PACKAGES_DIR
                            + File.separator
                            + dirname
                            + File.separator
                            + entry.getName());
                            boolean created = dir.mkdir();
                            continue;
                        } else {
                            // The entry is a file
                            int count;
                            FileOutputStream fileOut = null;
                            BufferedOutputStream buffOut = null;
                            int BUFFER = 2048;
                            byte[] data = new byte[BUFFER];
                            
                            makeMultipleDirs(entry);
                            File newFile = new File(PACKAGES_DIR
                            + File.separator
                            + dirname
                            + File.separator
                            + entry.getName());
                            try {
                                fileOut = new FileOutputStream(newFile);
                            } catch (FileNotFoundException fnfe) {
                                CCDebug.trace3("FileNotFoundException: "+fnfe);
                            }
                            buffOut = new BufferedOutputStream(fileOut, BUFFER);
                            while ((count = zipIn.read(data, 0, BUFFER)) != -1) {
                                buffOut.write(data, 0, count);
                            }
                            buffOut.flush();
                            buffOut.close();
                        }//close else
                    }// close while
                }// close if
            }//close if
            i = in.readLine(line, 0, 128);
        }//close while
        return true;
    }
    
    private void setFilename(String s) {
        if (s == null)
            return;
        int pos = s.indexOf("filename=\"");
        if (pos != -1) {
            filepath = s.substring(pos+10, s.length()-1);
            pos = filepath.lastIndexOf("\\");
            if (pos != -1)
                filename = filepath.substring(pos+1);
            else
                filename = filepath;
            int dotLoc = filename.lastIndexOf(".");
            String extension = filename.substring(dotLoc+1, filename.length());
            int equal = extension.compareToIgnoreCase("zip");
            
            if (equal != 0){
                // The extension is not .zip case insensitive
                CCDebug.trace3("The file extension: "+extension
                + "is not a valid extension");
                return;
            }
            dirname = filename.substring(0, dotLoc);
            File dirFile = new File(PACKAGES_DIR
            + File.separator
            + dirname);
            if (dirFile.exists() == false){
                dirFile.mkdir();
            }
        }
    }
    
    private void makeMultipleDirs(ZipEntry entry){
        int checkDirs = entry.getName().indexOf(File.separator);
        while (checkDirs != -1){
            String directoryString = entry.getName().substring(0, checkDirs);
            File directory = new File(PACKAGES_DIR
            + File.separator
            + dirname
            + File.separator
            + directoryString);
            if (directory.exists() == false){
                directory.mkdir();
            }
            checkDirs = directoryString.indexOf(File.separator);
        }
    }
}