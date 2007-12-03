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

package com.sun.apoc.manager.report;

import com.sun.apoc.manager.Toolbox2;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;

import com.sun.apoc.spi.cfgtree.policynode.PolicyNode;
import com.sun.apoc.spi.cfgtree.PolicyTree;
import com.sun.apoc.spi.cfgtree.property.Property;
import com.sun.apoc.spi.profiles.Profile;
import com.sun.apoc.spi.cfgtree.readwrite.ReadWritePolicyTreeFactoryImpl;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.policies.Policy;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.cfgtree.XMLStreamable;
import com.sun.apoc.spi.cfgtree.policynode.PolicyNodeImpl;

import com.sun.web.ui.common.CCDebug;
public class PolicyMgrReportHelper {
    
    public static final String SEPARATOR = "/";
    public static final String PENDING   = "pending_";
    public static final String DISABLED  = "disabled_";
    public static final String PENDING_DISABLED = PENDING + DISABLED;
    
    private Profile m_profile = null;
    private LinkedList m_entityList = new LinkedList();
    private LinkedList m_groupList = new LinkedList();
    private HashMap m_reportingTreeCache = new HashMap();
    private HashMap m_definedProps = new HashMap();
    private HashMap m_policyIterators = new HashMap();
    
    public PolicyMgrReportHelper() {
    }
    
    public PolicyMgrReportHelper(Profile profile) {
        initialize();
        try {
            m_profile = profile;
            m_groupList.add(m_profile);
            getPolicyIterators();
        } catch (SPIException e) {
            CCDebug.trace3(e.toString());
        }
    }
    
    public PolicyMgrReportHelper(Entity entity) {
        initialize();
        try {
            Iterator profileIt = entity.getLayeredProfiles();
            while (profileIt.hasNext()) {
                m_groupList.add(0, profileIt.next());
            }
            getPolicyIterators();
        } catch (SPIException e) {
            CCDebug.trace3("Error getting layered profiles for PolicyMgrReportHelper! " +  e.toString());
        }
    }
    
    public PolicyMgrReportHelper(ArrayList entityList) {
        initialize();
        try {
            for (int i = 0; i < entityList.size(); i++) {
                Iterator it = ((Entity)entityList.get(i)).getLayeredProfiles();
                while (it.hasNext()) {
                    m_groupList.add(0, it.next());
                }
            }
            getPolicyIterators();
        } catch (SPIException e) {
            CCDebug.trace3("Error getting layered profiles for PolicyMgrReportHelper! " +  e.toString());
        }  
    }    
    
    public LinkedList getPolicyGroupList() {
        return m_groupList;
    }
    
    public void getPolicyIterators() throws SPIException {
        Profile profile     = null;
        Iterator profileIt = m_groupList.iterator();
        
        while (profileIt.hasNext()) {
            profile =  (Profile)profileIt.next();
            Iterator policyIt  = profile.getPolicies();
            while (policyIt.hasNext()) {
                Policy policy  = (Policy)policyIt.next();
                String id = policy.getId();
                if (m_policyIterators.containsKey(id)) {
                    ArrayList list = (ArrayList)m_policyIterators.get(id);
                    list.add(0, policy);
                    m_policyIterators.put(id, list);
                } else {
                    ArrayList list = new ArrayList();
                    list.add(policy);
                    m_policyIterators.put(id, list);
                }
            }
        }
    }
    
    public void initialize() {
        m_reportingTreeCache.clear();
        m_entityList.clear();   
        m_groupList.clear();
        m_definedProps.clear();
        m_policyIterators.clear();
    }
    
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

    public PolicyTree getTree(Iterator groupIterator, String policyId) throws SPIException {

        PolicyTree policyTree = null;
        
        if (m_reportingTreeCache.containsKey(policyId)) {
            policyTree = (PolicyTree) m_reportingTreeCache.get(policyId);
        } else {
            Iterator policyIterator = getPolicyIterator(policyId);
            if  (policyIterator.hasNext()) {
                ReadWritePolicyTreeFactoryImpl factory =  new ReadWritePolicyTreeFactoryImpl();
                policyTree = factory.getPolicyTree(policyIterator);
            }
            m_reportingTreeCache.put(policyId, policyTree);
        }

        return policyTree;
    }
    
    public Property getReportProperty(String dataPath) 
            throws SPIException {

        Property property               = null;
        String propertyName             = dataPath.substring(dataPath.lastIndexOf('/')+1);
        String policyId                 = dataPath.substring(0, dataPath.indexOf('/'));
        PolicyTree policyTree           = getTree(m_groupList.iterator(), policyId); 
        String policyNodePath           = dataPath.substring(0, dataPath.lastIndexOf('/')+1);
        
        if (policyTree != null) {
            PolicyNode policyNode  = policyTree.getNode(policyNodePath);
            if (policyNode != null) {
                property      = policyNode.getProperty(propertyName);
            }
        }
        return property;
    }

    public HashMap getAllDefinedProperties() 
            throws SPIException {
            
        Iterator policyIds = m_policyIterators.keySet().iterator();
        while (policyIds.hasNext()) {
            String id = (String) policyIds.next();
            PolicyTree tree = getTree(m_groupList.iterator(), id);
            recursiveGetProperties(tree, "");
        }
        return m_definedProps;
    }
    
    public void recursiveGetProperties(PolicyTree policyTree, String nodePath) 
            throws SPIException {
        PolicyNode node = null;
        if (nodePath.length() == 0) {
            node = policyTree.getRootNode();    
        } else {
            String pathSoFar = policyTree.getRootNode().getAbsolutePath() + nodePath + "/";
            node = policyTree.getNode(pathSoFar);
        }
        String encodedSlash = Toolbox2.encode("/");
        String[] propNames = node.getPropertyNames();
        String[] childNames = node.getChildrenNames();
        if (propNames != null && propNames.length != 0) { 
            for (int i = 0; i < propNames.length; i++) {
                String dataPath = policyTree.getRootNode().getAbsolutePath() + nodePath + "/" + propNames[i].replaceAll("/", encodedSlash);
                m_definedProps.put(dataPath, node.getProperty(propNames[i]));
            }
        }
        if (childNames != null && childNames.length != 0) {
            for (int j = 0; j < childNames.length; j++) {
                recursiveGetProperties(policyTree, nodePath + "/" + childNames[j].replaceAll("/", encodedSlash));                
            }            
        }
    }
    
    public boolean isASetNode(String dataPath)             
            throws SPIException {
        boolean isASetNode = false;
        PolicyNode policyNode = getReportPolicyNode(dataPath);
        if (policyNode != null) {
            String attributes = ((PolicyNodeImpl)policyNode).getAttributes(XMLStreamable.POLICY_SCHEMA);               
            if(attributes.indexOf("oor:op") != -1) {
                isASetNode = true;
            }
        }
        return isASetNode;
    }
    
    public PolicyNode getReportPolicyNode(String dataPath)
            throws SPIException {

        PolicyNode policyNode           = null;       
        String policyNodePath           = dataPath.substring(0, dataPath.lastIndexOf('/')+1);
        String policyId                 = dataPath.substring(0, dataPath.indexOf('/'));
        PolicyTree policyTree           = getTree(m_groupList.iterator(), policyId);
        if (policyTree != null) {
            policyNode  = policyTree.getNode(policyNodePath);
        }
     
        return policyNode;
    }
    
    public Profile getReportProfile(String profileId) {
        Profile profile  = null;
        Iterator it = m_groupList.iterator();
        while (it.hasNext()) {
            Profile nextProfile = (Profile)it.next();
            if (nextProfile.getId().equals(profileId)) {
                profile = nextProfile;
                break;
            }
        }
        return profile;
    }
    
    private Iterator getPolicyIterator(String id) {
        ArrayList list = (ArrayList)m_policyIterators.get(id);
        if (list == null) {
            list = new ArrayList();
        }
        return list.iterator();
    }    
}