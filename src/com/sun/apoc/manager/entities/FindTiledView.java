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

import com.iplanet.jato.view.RequestHandlingTiledViewBase;
import com.iplanet.jato.view.View;
import com.sun.web.ui.view.html.CCHref;
import com.sun.web.ui.view.html.CCStaticTextField;


public class FindTiledView extends RequestHandlingTiledViewBase {

    public FindTiledView(View parent, String name) {
        super(parent, name);
        setPrimaryModel(getModel(FindTiledModel.class));
        registerChildren();
    } 
    
    protected void registerChildren() {
        registerChild(FindTiledModel.CHILD_RESULT1_HREF, CCHref.class);
        registerChild(FindTiledModel.CHILD_RESULT2_HREF, CCHref.class);
        registerChild(FindTiledModel.CHILD_RESULT3_HREF, CCHref.class);
        registerChild(FindTiledModel.CHILD_RESULT1_TEXT, CCStaticTextField.class);
        registerChild(FindTiledModel.CHILD_RESULT2_TEXT, CCStaticTextField.class);
        registerChild(FindTiledModel.CHILD_RESULT3_TEXT, CCStaticTextField.class);
    }

    protected View createChild(String name) {
        if (name.equals(FindTiledModel.CHILD_RESULT1_HREF) ||
        name.equals(FindTiledModel.CHILD_RESULT2_HREF) ||
        name.equals(FindTiledModel.CHILD_RESULT3_HREF)) {
            CCHref child = new CCHref(this, getPrimaryModel(), name, null);
            return child;
        }
        else if (name.equals(FindTiledModel.CHILD_RESULT1_TEXT) ||
        name.equals(FindTiledModel.CHILD_RESULT2_TEXT) ||
        name.equals(FindTiledModel.CHILD_RESULT3_TEXT)) {
            CCStaticTextField child = new CCStaticTextField(this, getPrimaryModel(), name, null);
            return child;
        }
        else {
            throw new IllegalArgumentException("Invalid child name [" + name + "]");
        }
    }
}

