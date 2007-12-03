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

package com.sun.apoc.manager.settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;

import com.sun.apoc.spi.cfgtree.policynode.PolicyNode;
import com.sun.apoc.spi.cfgtree.PolicyTree;
import com.sun.apoc.spi.cfgtree.property.Property;
import com.sun.apoc.spi.cfgtree.readwrite.ReadWritePolicyTreeConverterImpl;
import com.sun.apoc.spi.cfgtree.readwrite.ReadWritePolicyTreeFactoryImpl;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.spi.policies.Policy;
import com.sun.apoc.spi.profiles.Profile;

/**
 * A helper class containing convenience methods for common SPI operations 
 */ 
public class PolicyMgrHelper {
    
    public static final String SEPARATOR = "/";
    
    private PolicyManager m_mgr = null;
    private Profile m_pg = null;
    private HashMap m_treeCache = new HashMap();
    
    public PolicyMgrHelper() {
    }
    
    public PolicyMgrHelper(PolicyManager mgr, Profile pg) {
        initialize(mgr, pg);
    }

    /**
     * Returns the PolicyMgrHelper object.
     */       
    public PolicyManager getManager() {
        return m_mgr;
    }
    
    /**
     * Returns the current Profile object.
     */   
    public Profile getPolicyGroup() {
        return m_pg;
    }
    
    public void initialize(PolicyManager mgr, Profile pg) {
        m_mgr = mgr;
        m_pg = pg;
        m_treeCache.clear();
    }


    /**
     * Returns the Property from the backend specified by the path 
     * parameter.
     *
     * @param   path - the dataPath defining the property to return
     * @return  an SPI Property object or null if it does not exist
     * @throws   <code>SPIException</code> if error occurs
     */   
    public Property getProperty(String path) throws SPIException {
        String propertyName = decodePath(path.substring(path.lastIndexOf(SEPARATOR) + 1, path.length()));
        String nodePath = path.substring(0, path.lastIndexOf(SEPARATOR));
        PolicyNode node = getNode(nodePath);
        if (node != null) {
            return node.getProperty(propertyName);
        } else {
            return null;
        }
    }

    /**
     * Compares 2 Property objects for equality
     *
     * @param   2 SPI Property objects for comparisonthe dataPath defining the property to return
     * @return  true if the SPI Propertys are equal
     * @throws   <code>SPIException</code> if error occurs
     */     
    public boolean equalProperties(Property prop1, Property prop2) throws SPIException {
        if ((prop1 == null) || (prop2 == null)) {
            return false;
        } else if (prop1.isProtected() != prop2.isProtected()) {
            return false;
        } else if (prop1.getValue() == null) {
            if (prop2.getValue() != null) {
                return false;
            }
        } else if (!prop1.getValue().equals(prop2.getValue())) {
            return false;
        }
        return true;
    }

    /**
     * Creates the specified Property in the backend.
     *
     * @param   path - the dataPath defining the property to create
     * @return  an SPI Property object
     * @throws   <code>SPIException</code> if error occurs
     */     
    public Property createProperty(String path) throws SPIException {
        String propertyName = decodePath(path.substring(path.lastIndexOf(SEPARATOR) + 1, path.length()));
        String nodePath = path.substring(0, path.lastIndexOf(SEPARATOR));
        PolicyNode node = createNode(nodePath);
        if (node != null) {
            return node.addProperty(propertyName);
        } else {
            return null;
        }
    }

    /**
     * Removes the specified Property from the backend.
     *
     * @param   path - the dataPath defining the property to remove
     * @throws   <code>SPIException</code> if error occurs
     */     
    public void removeProperty(String path) throws SPIException {
        String propertyName = decodePath(path.substring(path.lastIndexOf(SEPARATOR) + 1, path.length()));
        String nodePath = path.substring(0, path.lastIndexOf(SEPARATOR));
        PolicyNode node = getNode(nodePath);
        if (node != null) {
            node.removeProperty(propertyName);
        } 
    }

    /**
     * Checks for the existance of the specified node in the backend.
     *
     * @param   path - the dataPath defining the node to check for
     * @return  true if node exists in the backend
     * @throws   <code>SPIException</code> if error occurs
     */     
    public boolean nodeExists(String path) throws SPIException {
        String policyId = path.substring(0, path.indexOf(SEPARATOR));
        PolicyTree policyTree = getTree(policyId);
        if (policyTree != null) {
            return policyTree.nodeExists(path);
        } else {
            return false;            
        }
    }

    /**
     * Returns the specified PolicyNode from the backend.
     *
     * @param   path - the dataPath defining the PolicyNode to return
     * @return  an SPI PolicyNode object or null if it does not exist
     * @throws   <code>SPIException</code> if error occurs
     */     
    public PolicyNode getNode(String path) throws SPIException {
        int pos = path.indexOf(SEPARATOR);
        String policyId = path;
        if (pos != -1) {
            policyId = path.substring(0, path.indexOf(SEPARATOR));
        }
        PolicyTree policyTree = getTree(policyId);
        if (policyTree != null) {
            return policyTree.getNode(path);
        } else {
            return null;            
        }
    }
    
    /**
     * Creates the specified PolicyNode in the backend.
     *
     * @param   path - the dataPath defining the PolicyNode to create
     * @return  an SPI PolicyNode object
     * @throws   <code>SPIException</code> if error occurs
     */     
    public PolicyNode createNode(String path) throws SPIException {
        return createNode(path, false);
    }
    
    /**
     * Creates the specified PolicyNode in the backend. This node will have the oor:replace 
     * attribute as used by template set elements.
     *
     * @param   path - the dataPath defining the PolicyNode to create
     * @return  an SPI PolicyNode object
     * @throws   <code>SPIException</code> if error occurs
     */      
    public PolicyNode createReplaceNode(String path) throws SPIException {
        return createNode(path, true);
    }

    /**
     * Creates the specified PolicyNode in the backend.
     *
     * @param   path - the dataPath defining the PolicyNode to create
     * @param   replace - whether to add the oor:replace attribute to the node
     * @return  an SPI PolicyNode object
     * @throws   <code>SPIException</code> if error occurs
     */      
    public PolicyNode createNode(String path, boolean replace) throws SPIException {
        int pos = path.indexOf(SEPARATOR);
        String policyId = path;
        if (pos != -1) {
            policyId = path.substring(0, path.indexOf(SEPARATOR));
        }
        PolicyTree policyTree = getTree(policyId);
        if (policyTree != null) {
            if (replace) {
                return policyTree.createReplaceNode(path);
            } else {
                return policyTree.createNode(path);
            }
        } else {
            return null;            
        }
    }

    /**
     * Writes all newly created/edited PolicyNodes/Propertys to the backend.
     *
     * @throws   <code>SPIException</code> if error occurs
     */     
    public void flushAllChanges() throws SPIException {
        Iterator it = m_treeCache.keySet().iterator();
        while (it.hasNext()) {
            PolicyTree policyTree = getTree((String) it.next());
            if (policyTree.hasBeenModified()) {
                ReadWritePolicyTreeConverterImpl converter = new ReadWritePolicyTreeConverterImpl();
                Policy policy = converter.getPolicy(policyTree);
                getPolicyGroup().storePolicy(policy);
            }
        }
    }
 
    /**
     * Removes from the cache any PolicyNodes/Propertys objects which were created/edited since the last
     * call to flushAllChanges(). 
     */     
    public void resetAllChanges() {
        m_treeCache.clear();
    }
    
    /**
     * Returns the specified PolicyTree object.
     *
     * @param   policyId - the policy path defining the PolicyTree to return
     * @return  an SPI PolicyTree object
     * @throws   <code>SPIException</code> if error occurs
     */     
    public PolicyTree getTree(String policyId) throws SPIException {
        PolicyTree policyTree = null;
        if (m_treeCache.containsKey(policyId)) {
            policyTree = (PolicyTree) m_treeCache.get(policyId);
        } else {
            ReadWritePolicyTreeFactoryImpl factory =  new ReadWritePolicyTreeFactoryImpl();
            Policy policy = null;
            if (getPolicyGroup() != null) {
                policy = getPolicyGroup().getPolicy(policyId);
            }
            if (policy == null) {
                policy = new Policy(policyId, m_pg.getId(), null);
            }
            Vector policies = new Vector();
            policies.add(policy);
            policyTree = factory.getPolicyTree(policies.iterator());
            m_treeCache.put(policyId, policyTree);
        }
        return policyTree;
    }
    
    
    // The below methods are just helpers to implement the enconding
    // decoding of path elements. A similar functionality is already available
    // within the SPI - but not public API yet. 
    public static String decodePath(String pathElement) throws SPIException {
        try {
            if (pathElement.indexOf('%') == -1) {
                return pathElement;
            } else {
                return URLDecoder.decode(pathElement, "UTF-8");
            }
        } catch (UnsupportedEncodingException ex) {
            throw new SPIException(ex);
        }
    }
    
    
    public static String encodePath(String pathElement) throws SPIException {
        try {
            if (pathElement.indexOf('/') != -1) {
                return URLEncoder.encode(pathElement, "UTF-8");
            } else {
                return pathElement;
            }
        } catch (UnsupportedEncodingException ex) {
            throw new SPIException(ex);
        }
    }
}
