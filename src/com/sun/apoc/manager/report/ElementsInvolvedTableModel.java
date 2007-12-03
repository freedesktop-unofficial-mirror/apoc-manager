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

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestParticipant;
import com.iplanet.jato.model.ModelControlException;
import com.sun.apoc.manager.Toolbox2;
import com.sun.apoc.spi.entities.Entity;
import com.sun.apoc.spi.environment.EnvironmentConstants;
import com.sun.web.ui.model.CCActionTableModel;
import java.util.ArrayList;

public class ElementsInvolvedTableModel extends CCActionTableModel {
    // Child view names (i.e. display fields).
    public static final String CHILD_ELEMENTS_INVOLVED_COLUMN    = "ElementsInvolvedColumn";
    public static final String CHILD_ELEMENTS_INVOLVED_TEXT      = "ElementsInvolvedValue";

    //
    private RequestContext  m_requestContext;
    
    public ElementsInvolvedTableModel() {
        super();
        setActionValue(CHILD_ELEMENTS_INVOLVED_COLUMN,    "Elements");
    }
    
    public void retrieve(ArrayList elements, ArrayList elementTypes) throws ModelControlException {
        clear();
        try {
            for (int i = 0; i < elements.size(); i++) {
                appendRow();
                String hrefValue="BrowseHref";
                String elementId = Toolbox2.decode((String)elements.get(i));
                String elementType = (String)elementTypes.get(i);
                Entity entity = Toolbox2.getPolicyManager().getEntity(elementType, elementId);
                setValue(CHILD_ELEMENTS_INVOLVED_TEXT,   Toolbox2.getParentagePath(entity, false, true, ">"));
            }

        } catch (Exception e) {
            throw new ModelControlException(e);
        }
        
    }
       
}

