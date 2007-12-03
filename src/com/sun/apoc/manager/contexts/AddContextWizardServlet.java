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

import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestContextImpl;
import com.iplanet.jato.ViewBeanManager;

import com.sun.web.ui.servlet.wizard.WizardWindowServlet;

/**
 * The application developer must create a Servlet subclass of
 * com.sun.web.ui.servlet.wizard.WizardWindowServlet.
 * This is because the wizard requires resource bundles that
 * that can only be loaded by the application context class
 * loader. Creating a wizard Servlet subclass ensures that
 * the application can load its resource bundle.
 *
 * @version 1.4 10/01/03
 * @author  Sun Microsystems, Inc.
 *
 */
public class AddContextWizardServlet extends
	com.sun.web.ui.servlet.wizard.WizardWindowServlet {

	public static final String DEFAULT_MODULE_URL = "../wizard";

	// Constructor
	public AddContextWizardServlet() {
	    super();
	}

	public String getModuleURL() {
	    // The superclass can be configured from init params specified at
	    // deployment time.  If the superclass has been configured with
	    // a different module URL, it will return a non-null value here.
	    // If it has not been configured with a different URL, we use our
	    // (hopefully) sensible default.
	    String result = (super.getModuleURL() != null)
		? super.getModuleURL()
		: DEFAULT_MODULE_URL;

	    return result;
	}
}


