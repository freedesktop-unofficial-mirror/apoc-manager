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
import com.iplanet.jato.view.View;
import com.iplanet.jato.view.RequestHandlingTiledViewBase;
import com.iplanet.jato.view.html.StaticTextField;
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.ChildDisplayEvent;


public class SectionTiledView extends RequestHandlingTiledViewBase {
    
    public static final String CHILD_ROW_ID =
        "RowID";
    public static final String CHILD_TURNER_ID =
        "TurnerID";
    public static final String CHILD_IMAGE_ID =
        "ImageID";
    public static final String CHILD_SECTION =
        "Section";
    public static final String CHILD_PROPERTY_TILEDVIEW =
        "PropertyTiledView";
    
    private SectionTiledModel m_tiledModel = null;
    
    /**
     * Creates a new instance of ContentsSectionTiledView 
     */
    public SectionTiledView(View parent, String name) {
        super(parent, name);
        setPrimaryModel(getModel());
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_ROW_ID, StaticTextField.class);
        registerChild(CHILD_TURNER_ID, StaticTextField.class);
        registerChild(CHILD_IMAGE_ID, StaticTextField.class);
        registerChild(CHILD_SECTION, StaticTextField.class);
        registerChild(CHILD_PROPERTY_TILEDVIEW, PropertyTiledView.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_SECTION)) {
            StaticTextField child = new StaticTextField(this, getPrimaryModel(), name, null);
            return child;
        
        } else if (name.equals(CHILD_ROW_ID)) {
            StaticTextField child = new StaticTextField(this, getPrimaryModel(), name, null);
            return child;
            
        } else if (name.equals(CHILD_TURNER_ID)) {
            StaticTextField child = new StaticTextField(this, getPrimaryModel(), name, null);
            return child;
            
        } else if (name.equals(CHILD_IMAGE_ID)) {
            StaticTextField child = new StaticTextField(this, getPrimaryModel(), name, null);
            return child;           
        
        } else if (name.equals(CHILD_PROPERTY_TILEDVIEW)) {
            PropertyTiledView child = new PropertyTiledView(this, name);
            return child;
            
        } else {
            throw new IllegalArgumentException( 
                "Invalid child name [" + name + "]");
        }
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        if (getPrimaryModel() == null){
            throw new ModelControlException("Primary Model is null");
        }
        super.beginDisplay(event);
        resetTileIndex();
        getModel().retrieve();
    }
    
    public boolean beginPropertyTiledViewDisplay (ChildDisplayEvent event) {
        PropertyTiledView child = (PropertyTiledView) getChild(CHILD_PROPERTY_TILEDVIEW);
        PropertyTiledModel model = (PropertyTiledModel) child.getPrimaryModel();
        String superId = getModel().getId();
        model.setData(getModel().getCurrentSectionData(), superId);
        return true;
    }

    protected SectionTiledModel getModel(){
        if (m_tiledModel == null) {
            m_tiledModel = (SectionTiledModel) getModel(SectionTiledModel.class);
        }
        return m_tiledModel;
    }
}
