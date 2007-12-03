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


package com.sun.apoc.manager.contexts;

import com.iplanet.jato.model.DefaultModel;
import com.iplanet.jato.model.InvalidContextException;

/**
 * This is an example model implementation designed to
 * share data collected during a wizard session and the
 * application that originated the wizard session.
 *
 * There are two contexts in this model.
 * The DEFAULT context which all DefaultModel's have
 * and a WIZARD_CONTEXT. The WIZARD_CONTEXT is created
 * to allow the application to discard the data collected
 * during a wizard session vs. preserving it as the
 * the actual data used by the application.
 */
public class AddContextWizardPageModel extends DefaultModel {

    final String WIZARD_CONTEXT = "WIZARD_CONTEXT";
    public static final String CONNECTION   = "LDAPConnection";
    public static final String VENDOR_ID    = "VendorId";
    public static final String BASEDN_LIST  = "BaseDnList";
    public static final String BASEDN       = "BaseDn";
    public static final String METACONFIG   = "MetaConfiguration";
    public static final String NO_ACTION    = "NoAction";
    public static final String FILEPATH     = "FilePath";
    public static final String SUCCESS      = "Success";
    public static final String FAIL_MESSAGE = "FailMessage";     
    public static final String EXISTING_INSTALL = "ExistingInstall";
    public static final String APOC1_INSTALL = "Apoc1Install";
    public static final String POLICYMGR = "PolicyManager";   
    public static final String ENTITY_TYPE = "EntityType";  
    public static final String WIZARD_CONFIG_FILE = "WizardConfigFile";
    public static final String PROPERTIES = "WizardProperties";
    public static final String WIZARD_TITLE = "WizardTitle";
    
	public AddContextWizardPageModel() {
	    this(null);
	}

	public AddContextWizardPageModel(String name) {
	    super();
	    setName(name);
	    addContext(WIZARD_CONTEXT);
	    setUseDefaultValues(false);
	}

	public void selectWizardContext() {
	    try {
		selectContext(WIZARD_CONTEXT);
	    } catch (InvalidContextException e) {
		// this means that the wizard has not set any
		// values in the model
		//
		// Should never fail.
	    }
	}

	public void selectDefaultContext() {
	    try {
		selectContext(DEFAULT_CONTEXT_NAME);
	    } catch (InvalidContextException e) {
		// this means that the wizard has not set any
		// values in the model
		//
		// Should never fail.
	    }
	}

	public void clearWizardData() {
	    try {
		selectContext(WIZARD_CONTEXT);
		clear();
	    } catch (InvalidContextException e) {
		// This should never fail
	    }

	    // Restore the default context.
	    try {
		selectContext(DefaultModel.DEFAULT_CONTEXT_NAME);
	    } catch (InvalidContextException e) {
		// never fails.
	    }
	}

	/**
	 * This is an example on how to get data set by the wizard
	 * from the model. More sophisticated models may need more
	 * sophisticated techniques. This method called from the
	 * beginDisplay methods of the container views to set
	 * a field's value. For simplicity we assume only single
	 * valued values.
	 */
	public Object getWizardValue(String fieldName) {

	    // Remember the current context
	    //
	    String cntxt = getCurrentContextName();

	    // Set the model to retrieve data from the WIZARD_CONTEXT
	    // in the model
	    //
	    try {
		selectContext(WIZARD_CONTEXT);
	    } catch (InvalidContextException e) {
		// this means that the wizard has not set any
		// This should never fail
	    }
	    Object obj = getValue(fieldName);

	    // Restore the default context.
	    try {
		selectContext(DefaultModel.DEFAULT_CONTEXT_NAME);
	    } catch (InvalidContextException e) {
		// never fails.
	    }
	    // If there is no wizard value return the default
	    // context value
	    //
	    if (obj == null) {
		obj = getValue(fieldName);
	    }

	    // Restore the original context
	    //
	    try {
		selectContext(cntxt);
	    } catch (InvalidContextException e) {
		// this means that the wizard has not set any
		// values in the model
		//
		// Should never fail.
	    }

	    return obj;
	}
}



