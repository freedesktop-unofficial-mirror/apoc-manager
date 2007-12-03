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
import com.iplanet.jato.view.event.DisplayEvent;
import com.iplanet.jato.view.event.ChildDisplayEvent;
import com.iplanet.jato.model.ModelControlException;
import com.iplanet.jato.model.DefaultModel;

import com.sun.web.ui.common.CCDebug;

/**
 *
 * @author  lb118646
 */
public class PaletteRowTiledView extends RequestHandlingTiledViewBase {
    
    public static final String CHILD_TD_TILEDVIEW =
        "PaletteTDTiledView";
    
    private static final int NO_ROWS_PALETTE = 12;

    private DefaultModel m_PaletteRowModel = null;
    
    /** Creates a new instance of PaletteRowTiledView */
    public PaletteRowTiledView(View parent, String name) {
        super(parent, name);
        setPrimaryModel(getPaletteRowModel());
        registerChildren();
    }
    
    protected void registerChildren() {
        registerChild(CHILD_TD_TILEDVIEW, PaletteTDTiledView.class);
    }
    
    protected View createChild(String name) {
        if (name.equals(CHILD_TD_TILEDVIEW)) {
            PaletteTDTiledView child = new PaletteTDTiledView(this, 
                                            CHILD_TD_TILEDVIEW);
            return child;
            
        } else {
            throw new IllegalArgumentException( 
                "Invalid child name [" + name + "]");
        }
    }
    
    public void beginDisplay(DisplayEvent event) throws ModelControlException {
        super.beginDisplay(event);
        resetTileIndex();
    }
    
    public boolean beginPaletteTDTiledViewDisplay (ChildDisplayEvent event) {
        PaletteTDTiledView child = (PaletteTDTiledView) getChild(CHILD_TD_TILEDVIEW);
        PaletteModel model = (PaletteModel) child.getPrimaryModel();
        model.setRow(getDisplayIndex());
        return true;
    }
    
    private DefaultModel getPaletteRowModel() {
        if (m_PaletteRowModel == null) {
            m_PaletteRowModel = new DefaultModel("PaletteRowModel");
            try {
                m_PaletteRowModel.setSize(NO_ROWS_PALETTE);
            } catch (ModelControlException mce) {
                CCDebug.trace2("ModelControlException caught");
            }
        }
        return m_PaletteRowModel;
    }
}
