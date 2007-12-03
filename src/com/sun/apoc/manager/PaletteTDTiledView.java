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

import com.iplanet.jato.view.RequestHandlingTiledViewBase;
import com.iplanet.jato.view.View;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.view.event.DisplayEvent;

import com.sun.web.ui.view.html.CCStaticTextField;

/**
 *
 * @author  lb118646
 */
public class PaletteTDTiledView extends RequestHandlingTiledViewBase {
    
    public static final String CHILD_PRESET_COLOR =
        "PresetColor";
    public static final String CHILD_PRESET_COLOR_NOHASH =
        "PresetColorNoHash";
    public static final String CHILD_PRESET_COLOR_NO =
        "PresetColorNo";
    public static final String CHILD_IMAGE =
        "Image";
    public static final String CHILD_PRESET_COLOR_NAME =
        "PresetColorName";
    
    private PaletteModel m_PaletteModel = null;
    
    /** Creates a new instance of PaletteTDTiledView */
    public PaletteTDTiledView(View parent, String name) {
        super(parent, name);
        setPrimaryModel(getPaletteModel());
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_PRESET_COLOR, CCStaticTextField.class);
        registerChild(CHILD_PRESET_COLOR_NOHASH, CCStaticTextField.class);
        registerChild(CHILD_PRESET_COLOR_NO, CCStaticTextField.class);
        registerChild(CHILD_IMAGE, CCStaticTextField.class);
        registerChild(CHILD_PRESET_COLOR_NAME, CCStaticTextField.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_PRESET_COLOR)) {
            CCStaticTextField child = new CCStaticTextField(this, 
                                            getPrimaryModel(),
                                            CHILD_PRESET_COLOR,
                                            PaletteModel.PRESET_COLOR,
                                            "");
            return child;
            
        } else if (name.equals(CHILD_PRESET_COLOR_NOHASH)) {
            CCStaticTextField child = new CCStaticTextField(this, 
                                            getPrimaryModel(),
                                            CHILD_PRESET_COLOR_NOHASH,
                                            PaletteModel.PRESET_COLOR_NOHASH,
                                            "");
            return child;
            
        } else if (name.equals(CHILD_PRESET_COLOR_NO)) {
            CCStaticTextField child = new CCStaticTextField(this, 
                                            getPrimaryModel(),
                                            CHILD_PRESET_COLOR_NO,
                                            PaletteModel.PRESET_COLOR_NUMBER,
                                            "");
            return child;
            
        } else if (name.equals(CHILD_IMAGE)) {
            CCStaticTextField child = new CCStaticTextField(this, 
                                            getPrimaryModel(),
                                            CHILD_IMAGE,
                                            PaletteModel.IMAGE,
                                            "");
            return child;
            
        } else if (name.equals(CHILD_PRESET_COLOR_NAME)) {
            CCStaticTextField child = new CCStaticTextField(this, 
                                            getPrimaryModel(),
                                            CHILD_PRESET_COLOR_NAME,
                                            PaletteModel.PRESET_COLOR_NAME,
                                            "");
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
    }
       
    protected PaletteModel getPaletteModel(){
        if (m_PaletteModel == null) {
            m_PaletteModel = (PaletteModel) getModel(PaletteModel.class);
        }
        return m_PaletteModel;
    }
}
