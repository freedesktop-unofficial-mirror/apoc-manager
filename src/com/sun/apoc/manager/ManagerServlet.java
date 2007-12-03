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

import javax.servlet.ServletException;
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.RequestContextImpl;
import com.iplanet.jato.ViewBeanManager;
import com.iplanet.jato.ModelManager;
import com.iplanet.jato.ModelTypeMap;
import com.sun.web.common.ConsoleServletBase;
import com.sun.web.ui.common.CCDebug;
import com.sun.apoc.templates.parsing.TemplateRepository;


public class ManagerServlet extends ConsoleServletBase {

    public static final String DEFAULT_MODULE_URL = "../manager";
    public static final String TEMPLATES_BASE_DIR = "/packages";
    public static final String TEMPLATE_DTD_PATH  = "/dtd/policytemplate.dtd";
    
    private static ModelTypeMap MODEL_TYPE_MAP;

    public static String PACKAGE_NAME =
	    getPackageName(ManagerServlet.class.getName());

    
    protected void initializeRequestContext(RequestContext requestContext) {
	    super.initializeRequestContext(requestContext);

	    // Set a view bean manager in the request context.  This must be done at
	    // the module level because the view bean manager is module specifc.
	    ViewBeanManager viewBeanManager =
	        new ViewBeanManager(requestContext, PACKAGE_NAME);
	        CCDebug.trace3("PACKAGE_NAME: "+PACKAGE_NAME);
	        ((RequestContextImpl)requestContext).
		        setViewBeanManager(viewBeanManager);
                
            ModelManager modelManager =
				new ModelManager(requestContext, MODEL_TYPE_MAP);
            ((RequestContextImpl)requestContext).setModelManager(modelManager);

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
	    CCDebug.trace3(result);
	    return result;
    }

    
    protected void onUncaughtException(RequestContext requestContext, Exception e) throws ServletException {
        // displays an error message for any uncaught exception
        AlertViewBean alertView = (AlertViewBean) requestContext.getViewBeanManager().getViewBean(AlertViewBean.class);
        alertView.setException(e);
        alertView.forwardTo(requestContext); 
    }
    
    
    public void init() throws ServletException {
        super.init();
        String templateDir = getServletContext().getRealPath(TEMPLATES_BASE_DIR);
        String dtdPath = getServletContext().getRealPath(TEMPLATE_DTD_PATH);
        TemplateRepository templateRepository = 
            TemplateRepository.getDefaultRepository();
        templateRepository.setTemplatesDir(templateDir);
        templateRepository.setDTDLocation(dtdPath);
        templateRepository.initialize();
        MODEL_TYPE_MAP = new ModelTypeMapImpl();
    }
}
