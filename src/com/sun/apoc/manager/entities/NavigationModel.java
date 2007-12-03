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

package com.sun.apoc.manager.entities;
import com.iplanet.jato.RequestManager;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Constants;
import com.sun.apoc.spi.PolicyManager;
import com.sun.apoc.manager.SyncLoginViewBean;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.manager.contexts.ManagerTableModel;
import com.sun.apoc.manager.profiles.CopyMoveWizardPageModel;
import com.sun.apoc.spi.SPIException;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.entities.Leaf;
import com.sun.apoc.spi.entities.Node;
import com.sun.apoc.spi.entities.Role;
import com.sun.apoc.spi.entities.User;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.web.ui.common.CCDebug;
import com.sun.web.ui.model.CCNavNode;
import com.sun.web.ui.model.CCNavNodeInterface;
import com.sun.web.ui.model.CCTreeModel;
import com.sun.web.ui.model.CCTreeModelInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
//implements RequestParticipant
public class NavigationModel extends CCTreeModel {
    // Child view names (i.e. display fields).
    public static final int    NODE_ROOT            = 0;
    public static final int    NODE_ROOT_ORG        = 1;
    public static final int    NODE_ROOT_DOMAIN     = 2;
    public static final int    NODE_SUBTREE_ROOT    = 3;
    public static final String DISPLAY_THRESHOLD_KEY= "DisplayThreshold";
    public static final String DISPLAY_GRACE_KEY    = "DisplayGrace";
    public static final String DISPLAY_SHOW_ASSIGNED= "ShowAssignedProfiles";
    public static final String DISPLAY_SMART_TURNER = "SmartTurner";
    
    private PolicyManager   m_policyManager;
    private boolean     m_bFilterLeaves     = false;
    private boolean     m_bSparseBrowsing   = true;
    private HashSet     m_syncedNodesDirs   = new HashSet();
    private HashSet     m_syncedNodesLeaves = new HashSet();
    private int         m_nIdCounter        = 10;
    private int         m_nDisplayThreshold = 10;
    private int         m_nDisplayGrace     = 15;
    private boolean     m_bShowAssigned     = false;
    private boolean     m_bSmartTurner      = false;
    private String      m_SelectedEntityConstant = null;
    private Entity      m_rootEntity        = null;
    
    //
    private LinkedList m_dirtyNodeIds   = new LinkedList();
    
    public NavigationModel() throws ModelControlException {
        super();
        m_SelectedEntityConstant = Constants.SELECTED_ENTITY;
        setUpVariables();
    }
    
    public NavigationModel(String sessionConstant) throws ModelControlException {
        super();
        m_SelectedEntityConstant = sessionConstant;
        setUpVariables();
    }   
    
    private void setUpVariables() throws ModelControlException {
        initTree(null, null, null);
        PolicyManager pmgr = Toolbox2.getPolicyManager();
        if (pmgr != null) {
            try {
                String policyMgrName = (String)pmgr.getEnvironment().get(Constants.POLICY_MANAGER_NAME);
                File dir = new File(ManagerTableModel.CONFIG_FILE_LOCATION);
                if (dir.exists()) {
                    FilenameFilter filter = new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.endsWith(".properties");
                        }
                    };
                    File[] files = dir.listFiles(filter);
                    int i = 0;
                    while (i < files.length) {
                        File properties = files[i];
                        if (properties.canRead()) {
                            FileInputStream input = new FileInputStream(properties) ;
                            Properties backendProperties = new Properties();
                            backendProperties.load(input);
                            input.close();
                            if (backendProperties.getProperty("Backend") != null) {
                                String sName = backendProperties.getProperty("Backend");
                                if (sName.equals(policyMgrName)) {
                                    try {
                                        if (backendProperties.containsKey(DISPLAY_THRESHOLD_KEY)) {
                                            m_nDisplayThreshold = Integer.parseInt(backendProperties.getProperty(DISPLAY_THRESHOLD_KEY));
                                        }
                                        if (backendProperties.containsKey(DISPLAY_GRACE_KEY)) {
                                            m_nDisplayGrace = Integer.parseInt(backendProperties.getProperty(DISPLAY_GRACE_KEY));
                                        }
                                        if (backendProperties.containsKey(DISPLAY_SHOW_ASSIGNED)) {
                                            m_bShowAssigned = Boolean.valueOf(backendProperties.getProperty(DISPLAY_SHOW_ASSIGNED)).booleanValue();
                                        }
                                        if (backendProperties.containsKey(DISPLAY_SMART_TURNER)) {
                                            m_bSmartTurner = Boolean.valueOf(backendProperties.getProperty(DISPLAY_SMART_TURNER)).booleanValue();
                                        }
                                    } catch (NumberFormatException ex) {
                                        CCDebug.trace3(ex.toString());
                                    }
                                }
                            }
                        }
                        i++;
                    }
                }
            } catch (Exception e) {
                throw new ModelControlException(e);
            }
        }        
    }
    
    public void initTree(String sContext, String sRootEntityType, String sRootEntity) throws ModelControlException {
        try {
            //Toolbox2.stamp("initTree begin " + sContext);
            // TPF_TODO:
            // we may want to have a more generic "policyMgrs in use" list
            // instead of restricting this to the sync env
            HashMap syncEnv         = (HashMap) RequestManager.getSession().getAttribute(Constants.SYNC_ENVIRONMENT);
            String  sLeftContext    = (syncEnv==null)?null:(String) syncEnv.get(SyncLoginViewBean.ENV_LEFT_CONTEXTNAME);
            String  sRightContext   = (syncEnv==null)?null:(String) syncEnv.get(SyncLoginViewBean.ENV_RIGHT_CONTEXTNAME);
            PolicyManager  sCopyMovePmgr = (PolicyManager) RequestManager.getRequest().getSession(false).getAttribute(CopyMoveWizardPageModel.TARGET_POLICYMGR);
            
            String sCopyMoveContext = null;
            if (sCopyMovePmgr != null) {
                sCopyMoveContext = (String)sCopyMovePmgr.getEnvironment().get(Constants.POLICY_MANAGER_NAME);
            }
            if ((sContext!=null) && sContext.equals(sLeftContext)) {
                m_policyManager = (PolicyManager) syncEnv.get(SyncLoginViewBean.ENV_LEFT_CONTEXT);
            } else if ((sContext!=null) && sContext.equals(sRightContext)) {
                m_policyManager = (PolicyManager) syncEnv.get(SyncLoginViewBean.ENV_RIGHT_CONTEXT);
            } else if ((sContext!=null) && (sContext.equals(sCopyMoveContext))) {
                m_policyManager = sCopyMovePmgr;
            } else {
                m_policyManager = Toolbox2.getPolicyManager();
            }
            if (m_policyManager==null) {
                return;
            }
            
            m_syncedNodesDirs.clear() ;
            m_syncedNodesLeaves.clear() ;
            removeAllNodes();
            setType(CCTreeModelInterface.TITLE_TYPE);
            CCNavNode node = null;
            ArrayList sources = new ArrayList(Arrays.asList(m_policyManager.getSources()));            
            boolean isNodeSelected = false ;
            if (sRootEntity==null || sRootEntity.length()==0 ) {
                for (int i = 0; i < sources.size(); i++) {
                    String sourceName = (String)sources.get(i);
                    String[] labelAttrs = getRootLabelAttributes(sourceName);
                    int rootId = this.getRootId(sourceName);
                    addNode(m_policyManager.getRootEntity(sourceName).getId(), rootId, labelAttrs[0], labelAttrs[1], labelAttrs[2], sourceName);
                    if (isNodeSelected == false) {
                        setSelectedNode(rootId); 
                        isNodeSelected = true;
                        RequestManager.getSession().setAttribute(m_SelectedEntityConstant, m_policyManager.getRootEntity(sourceName));
                    }
                }
            } else {
                boolean isASubtree = true;
                for (int i = 0; i < sources.size(); i++) {
                    String sourceName = (String)sources.get(i);
                    String[] labelAttrs = getRootLabelAttributes(sourceName);
                    int rootId = this.getRootId(sourceName);
                    if (m_policyManager.getRootEntity(sourceName).getId().equals(sRootEntity)) {
                        isASubtree = false;
                        m_rootEntity = m_policyManager.getRootEntity(sourceName);
                        addNode(sRootEntity, rootId, labelAttrs[0], labelAttrs[1],
                                labelAttrs[2], sourceName); 
                        setSelectedNode(rootId);
                        RequestManager.getSession().setAttribute(m_SelectedEntityConstant, m_policyManager.getRootEntity(sourceName));
                    }
                  
                } 
                if (isASubtree) {
                    node = new CCNavNode(NODE_SUBTREE_ROOT);
                    node.setLabel(m_policyManager.getEntity(sRootEntityType, sRootEntity).getDisplayName(Toolbox2.getLocale()));
                    node.setValue(m_policyManager.getEntity(sRootEntityType, sRootEntity).getId());
                    node.setValue("ENTITY", m_policyManager.getEntity(sRootEntityType, sRootEntity));
                    node.setValue(sRootEntity);
                    addNode(node);
                    setSelectedNode(NODE_SUBTREE_ROOT);
                    m_rootEntity = m_policyManager.getEntity(sRootEntityType, sRootEntity);
                }
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
        //Toolbox2.stamp("initTree end " + sContext);
    }
    
    public boolean isRootEntity(String sRootEntity) throws SPIException {
        ArrayList sources = new ArrayList(Arrays.asList(m_policyManager.getSources()));
        for (int i=0; i < sources.size(); i++) {
            String source = (String)sources.get(i);
            if (m_policyManager.getRootEntity(source).getId().equals(sRootEntity)) {
                return true;
            }
        }
        return false;
    }
    
    
    public void addNode(String sRootEntity, int sId, String sLabel, String sStatus, String sTooltip, String sEntityType) 
        throws SPIException {
        CCNavNode node = null;
        node = new CCNavNode(sId);
        node.setLabel(Toolbox2.getI18n(sLabel));
        node.setStatus(Toolbox2.getI18n(sStatus));
        node.setTooltip(Toolbox2.getI18n(sTooltip));
        node.setValue("ENTITY", m_policyManager.getRootEntity(sEntityType));
        node.setValue(sRootEntity);
        node.setExpanded(true);
        addNode(node);
     //   if ( m_policyManager.getRootEntity(sEntityType).getId().equals(sRootEntity) ) {
     //       setSelectedNode(sId);
     //   }        
    }
    
    public void expandRoot() throws ModelControlException, SPIException {
        String[] sSources = {EnvironmentConstants.HOST_SOURCE, EnvironmentConstants.USER_SOURCE} ;
        if (m_policyManager != null) {
            sSources = m_policyManager.getSources();
        }
        for(int i = 0; i < sSources.length; i++) {
            if (sSources[i].equals(EnvironmentConstants.USER_SOURCE)) {
                retrieve(NODE_ROOT_ORG);
            } else if (sSources[i].equals(EnvironmentConstants.HOST_SOURCE)) {
                retrieve(NODE_ROOT_DOMAIN);
            }
        }
    }
    
    public void setSparseBrowsing(boolean bIsSparse) {
        m_bSparseBrowsing = bIsSparse;
    }
    
    public void setAssignedState(Entity entity, boolean bIsAssigned) {
        String sSource = entity.getPolicySourceName();
        CCNavNodeInterface sNode = null; 
        if (sSource.equals(EnvironmentConstants.USER_SOURCE)) {
            sNode = getNodeById(NODE_ROOT_ORG);
        } else if (sSource.equals(EnvironmentConstants.HOST_SOURCE)) {
            sNode = getNodeById(NODE_ROOT_DOMAIN);
        } 
        CCNavNodeInterface node = findChildByValue(sNode, entity.getId(), true);
        if ( (node!=null) && (((Boolean)node.getValue("ASSIGNED")).booleanValue() != bIsAssigned) ) {
            setTypeSpecifics(node, entity, bIsAssigned && m_bShowAssigned);
            if (node.getVisible()) {
                m_dirtyNodeIds.add(new Integer(node.getId()));
            }
        }
    }
    
    public boolean getAssignedState(int nNodeId) {
        return (m_bShowAssigned)?((Boolean)getNodeById(nNodeId).getValue("ASSIGNED")).booleanValue():false;
    }
    
    public Iterator getDirtyNodeIds() {
        return m_dirtyNodeIds.iterator();
    }
    
    public void clearDirtyNodeIds() {
        m_dirtyNodeIds.clear();
    }
    
    public boolean isLeavesFiltered() {
        return m_bFilterLeaves;
    }
    
    public void filterLeaves(boolean filterLeaves) {
        m_bFilterLeaves = filterLeaves;
    }
    
    public String getViewedNode() {
        return getSelectedNode().getValue();
    }
    
    public Object getStoredValue(int nTreeId) {
        return getNodeById(nTreeId).getValue("ENTITY");
    }
    
    public String getStoredEntityId(int nTreeId) {
        Entity entity = (Entity) getNodeById(nTreeId).getValue("ENTITY");
        return entity.getId();
    }
    
    public String getStoredEntityType(int nTreeId) {
        Entity entity = (Entity) getNodeById(nTreeId).getValue("ENTITY");
        return entity.getPolicySourceName();
    }   
    
    public String getNodeType(CCNavNodeInterface node) {
        String nodeType = "";
        Entity entity = (Entity) node.getValue("ENTITY");
        if(entity != null) {
            nodeType = entity.getPolicySourceName();
        }
        return nodeType;
    }
    
    public Entity getRootEntity() {
        // Returns the root entity if this is a subtree otherwise null
        return m_rootEntity;
    }
    
    public void setTypeSpecifics(CCNavNodeInterface node, Entity entity, boolean bIsAssigned) {
        if (node.getVisible()) {
            
            String sImage   = "";
            String sTooltip = "";
            String source = entity.getPolicySourceName() ;
            
            if (entity instanceof Role) {
                if (bIsAssigned) {
                    sImage = "../images/roleAssigned.png";
                    sTooltip = Toolbox2.getI18n("APOC.navigation.role.with");
                } else {
                    sImage = "../images/role.png";
                    sTooltip = Toolbox2.getI18n("APOC.navigation.role");
                }
            }
            else if (source.equals(EnvironmentConstants.USER_SOURCE)) {
                if (entity instanceof Node) {
                    if (bIsAssigned) {
                        sImage = "../images/orgAssigned.png";
                        sTooltip = Toolbox2.getI18n("APOC.navigation.organization.with");
                    } else {
                        sImage = "../images/org.png";
                        sTooltip = Toolbox2.getI18n("APOC.navigation.organization");
                    }
                }
                else if (entity instanceof Leaf) {
                    if (bIsAssigned) {
                        sImage = "../images/userAssigned.png";
                        sTooltip = Toolbox2.getI18n("APOC.navigation.user.with");
                    } else {
                        sImage = "../images/user.png";
                        sTooltip = Toolbox2.getI18n("APOC.navigation.user");
                    }
                }
            }
            else if (source.equals(EnvironmentConstants.HOST_SOURCE)) {
                if (entity instanceof Node) {
                    if (bIsAssigned) {
                        sImage = "../images/domainAssigned.png";
                        sTooltip = Toolbox2.getI18n("APOC.navigation.domain.with");
                    } else {
                        sImage = "../images/domain.png";
                        sTooltip = Toolbox2.getI18n("APOC.navigation.domain");
                    }
                }
                else if (entity instanceof Leaf) {
                    if (bIsAssigned) {
                        sImage = "../images/hostAssigned.png";
                        sTooltip = Toolbox2.getI18n("APOC.navigation.host.with");
                    } else {
                        sImage = "../images/host.png";
                        sTooltip = Toolbox2.getI18n("APOC.navigation.host");
                    }
                }
            }
            node.setValue("ASSIGNED", new Boolean(bIsAssigned));
            node.setImage(sImage);
            node.setTooltip(sTooltip);
        }
    }
    
    public int retrieve(boolean bFilterLeaves) throws ModelControlException {
        //Toolbox2.stamp("retrieve begin " + bFilterLeaves);
        try {
            if (bFilterLeaves!=m_bFilterLeaves) {
                m_bFilterLeaves=bFilterLeaves;
                Iterator nodes = getNodes().iterator();
                while (nodes.hasNext()) {
                    CCNavNodeInterface node = (CCNavNodeInterface) nodes.next();
                    if (m_bFilterLeaves==false) {
                        addLeaves(node);
                    } else {
                        removeLeaves(node);
                    }
                }
            }
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
        //Toolbox2.stamp("retrieve end " + bFilterLeaves);
        return getSelectedNode().getId();
    }
    
    public int retrieve(String sEntity) throws ModelControlException {
        //Toolbox2.stamp("retrieve begin " + sEntity);
        
        String sEntityType = EnvironmentConstants.USER_SOURCE;
        if (sEntity.indexOf("|") != -1) {
            sEntityType = sEntity.substring(sEntity.indexOf("|") + 1);
            sEntity = sEntity.substring(0, sEntity.indexOf("|"));
        }
        
        CCNavNodeInterface pathNode  = null;
        if (sEntityType.equals(EnvironmentConstants.USER_SOURCE)) {
            pathNode  = getNodeById(NODE_ROOT_ORG);
        } else if (sEntityType.equals(EnvironmentConstants.HOST_SOURCE)) {
            pathNode  = getNodeById(NODE_ROOT_DOMAIN);
        } 
        if (pathNode == null) {
            pathNode = getNodeById(NODE_SUBTREE_ROOT);
        }
        CCNavNodeInterface pathNodeTemp = null;
        
        try {
            Iterator pathEntities = Toolbox2.getParentPath(sEntityType, sEntity);
            Entity   pathEntity   = null;
            
            if (pathEntities.hasNext()) {
                pathEntity = (Entity) pathEntities.next();
            }
            
            while (pathEntities.hasNext()) {
                
                pathEntity = (Entity) pathEntities.next();
                
                if (pathNode.getNumChildren() == 0) {
                    retrieve(pathNode.getId());
                }
                
                pathNodeTemp = findChildByValue(pathNode, pathEntity.getId(), false);
                
                // SPI returns only a limited number of results,
                // maybe the entity in question was not among them
                if (pathNodeTemp==null) {
                    pathNode = addChild(pathNode, pathEntity);
                } else {
                    pathNode = pathNodeTemp;
                }
            }
            
            if (pathNode.getNumChildren() == 0) {
                retrieve(pathNode.getId());
            }
            
            pathNodeTemp = findChildByValue(pathNode, sEntity, false);

            // SPI returns only a limited number of results,
            // maybe the entity in question was not among them
            if (pathNodeTemp==null) {
                pathNode = addChild(pathNode, m_policyManager.getEntity(sEntityType, sEntity));
            } else {
                pathNode = pathNodeTemp;
            }            
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
        //Toolbox2.stamp("retrieve end " + sEntity);
        return pathNode.getId();
    }
    
    public void retrieve(int nTreeId) throws ModelControlException {
        //Toolbox2.stamp("retrieve begin " + nTreeId);

        CCNavNodeInterface node = getNodeById(nTreeId);
        if (node == null) {
            return;
        }
        
        if (m_bFilterLeaves && m_syncedNodesDirs.contains(node)) {
            return;
        }
        
        if (!m_bFilterLeaves && m_syncedNodesDirs.contains(node) && m_syncedNodesLeaves.contains(node)) {
            //        if (!m_bFilterLeaves && m_syncedNodesLeaves.contains(node)) {
            return;
        }
        
        try {
            
            Entity entity = null;
            
            if (nTreeId == NODE_ROOT_ORG) {
                entity = m_policyManager.getRootEntity(EnvironmentConstants.USER_SOURCE);
            }
            else if (nTreeId == NODE_ROOT_DOMAIN) {
                entity = m_policyManager.getRootEntity(EnvironmentConstants.HOST_SOURCE);
            }
             
            else {
                entity = (Entity)node.getValue("ENTITY");
            }

            if (entity instanceof Leaf) {
                return;
            }
            
            if ((entity instanceof Node) && (!((Node) entity).hasChildren())) {
                return;
            }
            addChildren(node,((Node) entity).getChildren());
        }
        catch (SPIException se) {
            throw new ModelControlException(se);
        }
        
        m_syncedNodesDirs.add(node);
        
        if (!m_bFilterLeaves) {
            m_syncedNodesLeaves.add(node);
        }
    }
    
    private void addLeaves(CCNavNodeInterface node) throws SPIException {
        //Toolbox2.stamp("addLeaves begin " + node.getLabel());
        Iterator nodeChildren = node.getChildren().iterator();
        
        while (nodeChildren.hasNext()) {
            addLeaves((CCNavNodeInterface)nodeChildren.next());
        }
        
        if (!m_syncedNodesLeaves.contains(node)) {
            Entity entity = (Entity) node.getValue("ENTITY");
            
            addChildren(node, ((Node) entity).getChildren());
            m_syncedNodesLeaves.add(node);
        }
        //Toolbox2.stamp("addLeaves end " + node.getLabel());
    }
    
    // called only for parents which have 0 children
    private void addChildren(CCNavNodeInterface node, Iterator childrenIter) throws SPIException {
        //Toolbox2.stamp("addChildren begin " + node.getId());
        TreeSet     entityChildren  = new TreeSet(new EntityComparator());
        while (childrenIter.hasNext()) {
            entityChildren.add(childrenIter.next());
        }
        
        boolean     bShowEllipsis   = false;
        int         nBacklog        = 0;
        LinkedList  newChildren     = new LinkedList();
        
        childrenIter = entityChildren.iterator();
        while (childrenIter.hasNext()) {
            Entity child = (Entity) childrenIter.next();
            if ( (m_bFilterLeaves==false && !m_syncedNodesLeaves.contains(node)) || (child instanceof Node) ) {
                if (findChildByValue(node, child.getId(), false)==null) {
                    newChildren.add(child);
                    nBacklog++;
                    if (m_bSparseBrowsing && (nBacklog+node.getNumChildren() > m_nDisplayGrace)) {
                        bShowEllipsis = true;
                        nBacklog = m_nDisplayThreshold-node.getNumChildren();
                        break;
                    }
                }
            }
        }
        
        if (nBacklog>0) {
            node.setAcceptsChildren(true);
        }
        
        for (int nBacklogRunner=0; nBacklogRunner<nBacklog; nBacklogRunner++) {
            CCNavNodeInterface newNode = createChild((Entity)newChildren.get(nBacklogRunner));
            newNode.setParent(node);
        }
        
        if (bShowEllipsis) {
//            String      sDisplayName    = Toolbox2.getI18n("APOC.navigation.ellipsis", new Object[]{Integer.toString(entityChildren.size()-node.getNumChildren())});
            String      sDisplayName    = Toolbox2.getI18n("APOC.navigation.ellipsis");
            CCNavNode   ellipsisNode    = new CCNavNode(m_nIdCounter++);
            
            ellipsisNode.setLabel(sDisplayName);
            ellipsisNode.setStatus(sDisplayName);
            ellipsisNode.setAcceptsChildren(false);
            //node.setValue("ENTITY", entity);
            ellipsisNode.setValue("ellipsis");
            ellipsisNode.setValue("ASSIGNED", new Boolean(false));
            ellipsisNode.setImage("../images/search.gif");
            ellipsisNode.setTooltip(Toolbox2.getI18n("APOC.navigation.openfind"));
            ellipsisNode.setParent(node);
            ellipsisNode.setOnClick("var newWindow = window.open('/apoc/manager/EntitiesFindIndex?EntitiesFindIndex.FindHref="+ellipsisNode.getId()+"&EntitiesFindIndex.FindModel="+getName()+"', 'findWindow', 'height=800,width=500,top='+((screen.height-800)/2)+',left='+((screen.width-500)/2)+',scrollbars,resizable'); newWindow.focus(); return false;");
            m_syncedNodesLeaves.add(ellipsisNode);
        }
        //Toolbox2.stamp("addChildren end " + node.getId());
    }
    
    // called only for parents which have children already
    private CCNavNodeInterface addChild(CCNavNodeInterface node, Entity entity) throws SPIException {
        CCNavNodeInterface  newChild        = createChild(entity);
        List                nodeChildren    = node.getChildren();
        LinkedList          nodeChildren2   = new LinkedList(nodeChildren); // to circumvent ConcurrentModificationException
        Iterator            nodeChildren2Iter=nodeChildren2.iterator();
        EntityComparator    entityComparator= new EntityComparator();
        CCNavNodeInterface  nodeChild      = null;
        Entity              nodeChildEntity= null;
        
        node.setAcceptsChildren(true);
        
        // insert the newChild at the correct place in the subnode list of nodes
        // due to restrictions in the NavNode interface only possible by reassigning
        // the parent for all subnodes in the correct order
        while (nodeChildren2Iter.hasNext()) {
            nodeChild       = (CCNavNodeInterface) nodeChildren2Iter.next();
            nodeChildEntity = (Entity) nodeChild.getValue("ENTITY");
            if ( (newChild.getParent()==null) && 
            ((nodeChildEntity==null) || (entityComparator.compare(entity, nodeChildEntity)<0)) ) {
                newChild.setParent(node);
            }
            nodeChild.setParent(node);
        }
        
//        // decrease number of not displayed entries displayed by the ellipsis node by one
//        String sLabel = nodeChild.getLabel();
//        sLabel = sLabel.replaceAll("[^0-9]","");
//        int nNumber = Integer.parseInt(sLabel);
//        sLabel = Toolbox2.getI18n("APOC.navigation.ellipsis", new Object[]{Integer.toString(nNumber-1)});
//        nodeChild.setLabel(sLabel);
//        nodeChild.setStatus(sLabel);
        
        return newChild;
    }
    
    private CCNavNodeInterface createChild(Entity entity) throws SPIException {
        //Toolbox2.stamp("createChild begin " + entity.getDisplayName());
        String      sDisplayName    = entity.getDisplayName(Toolbox2.getLocale());
        CCNavNode   node            = new CCNavNode(m_nIdCounter++);
        boolean     bAcceptsChildren= !m_bSmartTurner;
        if (entity instanceof Leaf) {
            bAcceptsChildren = false;
        } else if ((m_bSmartTurner)) {
            Iterator subChildren = ((Node)entity).getChildren();
            if (subChildren.hasNext()) {
                if (m_bFilterLeaves) {
                    while (subChildren.hasNext()) {
                        Entity subChild = (Entity)subChildren.next();
                        if (subChild instanceof Node) {
                            bAcceptsChildren = true;
                            break;
                        }
                    }
                } else {
                    bAcceptsChildren=true;
                }
            }
        }
        
        if (entity instanceof User) {
            sDisplayName = sDisplayName + " [" + ((User)entity).getUserId() + "]";
        }
        
        node.setLabel(sDisplayName);
        node.setStatus(sDisplayName);
        node.setAcceptsChildren(bAcceptsChildren);
        node.setValue("ENTITY", entity);
        node.setValue(entity.getId());
        
        boolean bIsAssigned = false;
        if (m_bShowAssigned) {
            Iterator assignedProfiles = entity.getAssignedProfiles();
            bIsAssigned = assignedProfiles.hasNext();
        }
        setTypeSpecifics(node, entity, bIsAssigned);

        //Toolbox2.stamp("createChild end " + entity.getDisplayName());
        return node;
    }
    
    private void removeLeaves(CCNavNodeInterface node) throws SPIException {
        Iterator    nodeChildren    = node.getChildren().iterator();
        boolean     bRemovedChild   = false;
        
        while (nodeChildren.hasNext()) {
            CCNavNodeInterface nodeChild = (CCNavNodeInterface) nodeChildren.next();
            Entity entity = (Entity) nodeChild.getValue("ENTITY");
            if (entity instanceof Node) {
                removeLeaves(nodeChild);
            } else {
                if (nodeChild.equals(getSelectedNode())) {
                    setSelectedNode(node);
                    RequestManager.getSession().setAttribute(m_SelectedEntityConstant, entity);
                }
                nodeChildren.remove();
                bRemovedChild=true;
                if (node.getNumChildren()==0) {
                    node.setAcceptsChildren(false);
                }
            }
        }
        if (bRemovedChild) {
            // removing the node from list only if at least one node was removed
            // keeps that node in the sync list forever. Result:
            // a node without children that are of type "leaf" is asked for leaf
            // children only once (performance)
            m_syncedNodesLeaves.remove(node);
        }
    }
    
    private CCNavNodeInterface findChildByValue(CCNavNodeInterface parent, String sSearchString, boolean bRecursive) {
        if (parent.getChildren() != null) {
            Iterator children = parent.getChildren().iterator();

            while (children.hasNext()) {
                CCNavNodeInterface child = (CCNavNodeInterface) children.next();

                if (child.getValue().equals(sSearchString)) {
                    return child;
                }

                if (bRecursive) {
                    child = findChildByValue(child, sSearchString, bRecursive);
                    if (child!=null) {
                        return child;
                    }
                }
            }
        }
        return null;
    }
    
    class EntityComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            // place EntityContainers first
            int nComparison = -1;
            int no1 = getPriority(o1);
            int no2 = getPriority(o2);
            if (no1<no2) {
                nComparison = -1;
            } else if (no1==no2) {
                nComparison = ( ((Entity)o1).getDisplayName(Toolbox2.getLocale()).compareTo(((Entity)o2).getDisplayName(Toolbox2.getLocale())) );
            } else {
                nComparison = 1;
            }
            return nComparison;
        }
    }
    
    private int getPriority(Object entity) {
        if (entity instanceof Entity) {
            String source = ((Entity) entity).getPolicySourceName() ;

            if (source.equals(EnvironmentConstants.HOST_SOURCE)) {

            }
            if (entity instanceof Node) {
                if (source.equals(EnvironmentConstants.HOST_SOURCE)) {return 1;}
                if (source.equals(EnvironmentConstants.USER_SOURCE)) {return 2;}
            }
            if (entity instanceof Role) {return 3;}
            if (source.equals(EnvironmentConstants.HOST_SOURCE)) {return 4;}
            if (source.equals(EnvironmentConstants.USER_SOURCE)) {return 5;}
        }
        return 99;
    }

    private String[] getRootLabelAttributes(String entityType) {
        if (entityType.equals(EnvironmentConstants.HOST_SOURCE)) {
            String[] attrs = {"APOC.navigation.domains", "APOC.navigation.domains.help",
                            "APOC.navigation.domains.help"};
            return attrs;                       
        } else if (entityType.equals(EnvironmentConstants.USER_SOURCE)) {
            String[] attrs = {"APOC.navigation.organizations", "APOC.navigation.organizations.help",
                            "APOC.navigation.organizations.help"};
            return attrs;             
        }
        return null;
    }
    
    private int getRootId(String entityType) {
        if (entityType.equals(EnvironmentConstants.HOST_SOURCE)) {
            return NODE_ROOT_DOMAIN;            
        }
        return NODE_ROOT_ORG;
    }        
}
