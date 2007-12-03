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


package com.sun.apoc.manager.profiles;

import java.util.Map;

import com.iplanet.jato.ModelManager;
import com.iplanet.jato.RequestContext;
import com.iplanet.jato.model.Model;
import com.iplanet.jato.RequestManager;

import com.sun.web.ui.model.wizard.WizardInterface;
import com.sun.web.ui.model.wizard.WizardInterfaceExt;
import com.sun.web.ui.model.wizard.WizardEvent;
import com.sun.web.ui.common.CCI18N;

import com.sun.apoc.manager.Constants;
import com.sun.web.ui.common.CCDebug;

interface WizardImplDataUtil {

    public String getErrorMsg();
}

interface WizardImplData {


    final String name = "CopyMoveWizardImpl";    
    final String title = "APOC.wiz.copy.title";
    final String cancelMsg = "";
    
    final String finishPageId = "5";    
    final String firstPageId = "1";
    final String lastPageId = "6";
    final String resultsPageId = "6";
    
    final int[] pageIds = {1, 2, 3, 4, 5, 6};
    final String[][] futurePages = { {"2", "3", "4", "5", "6"}, {"3", "4", "5", "6"}, {"4", "5", "6"}, {"5", "6"}, {"6"}, {null} };                       

                                     
    final Class[] pageClass = {
        CopyMoveWizardOverviewView.class,
        CopyMoveWizard1View.class,
        CopyMoveWizard2View.class,
        CopyMoveWizard3View.class,
        CopyMoveWizard4View.class,
        CopyMoveWizard5View.class
    };
    
    final String[] stepText = {
        "APOC.wiz.copy.steptext.overview",
        "APOC.wiz.copy.steptext.1",
        "APOC.wiz.copy.steptext.2",
        "APOC.wiz.copy.steptext.3",
        "APOC.wiz.steptext.10",
        "APOC.wiz.steptext.11"
    };
    
    final String[] pageTitle = {
        null,
        null,       
        null,
        null,
        null,
        null 
    };

    final String[][] stepHelp = {
        {""},
        {"APOC.wiz.copy.help.1"},
        {"APOC.wiz.copy.help.2"},
        {"APOC.wiz.copy.help.3"},
        {"APOC.wiz.help.10"},
        {"APOC.wiz.help.11"}
    };

    final String[] stepInstruction = {
        "",
        "APOC.wiz.copy.instruction.1",
        "APOC.wiz.copy.instruction.2",
        "APOC.wiz.copy.instruction.3",
        "APOC.wiz.copy.instruction.4",
        "APOC.wiz.instruction.11"
    };
    
     final String[] placeHolders = {
        null,
        null,
        null,
        null,
        null,
        null
    };  
    
    
}

public class CopyMoveWizardImpl implements WizardImplData, WizardInterface, WizardInterfaceExt{

    private CopyMoveWizardPageModel thePageModel;

    private String getParameter(String name,
        RequestContext requestContext) {

        Map parameterMap = requestContext.getRequest().getParameterMap();

        // Need the qualified name. Not sure how to get it
        //
        String[] values = (String[])parameterMap.get(name);
        return values == null ? null : values[0];
    }


    private void getWizardPageModel(RequestContext requestContext) {

        String modelInstanceName = getParameter(AssignedTableView.WIZARDPAGEMODELNAME, requestContext);
        ModelManager mm = requestContext.getModelManager();
        thePageModel = (CopyMoveWizardPageModel)mm.getModel(
        com.sun.apoc.manager.profiles.CopyMoveWizardPageModel.class,
        modelInstanceName, false, false);
        thePageModel.selectWizardContext();

    }

    private boolean validate(WizardEvent wizardEvent) {

        WizardImplDataUtil view = (WizardImplDataUtil)
        wizardEvent.getView();

        String emsg = view.getErrorMsg();
        boolean haveError = (emsg != null);
        if (haveError) {
            wizardEvent.setSeverity(WizardEvent.ACKNOWLEDGE);
            wizardEvent.setErrorMessage(emsg);
        }
        return !haveError;
    }

    public static WizardInterface create(RequestContext requestContext) {
        CopyMoveWizardImpl wiz = new CopyMoveWizardImpl(requestContext);
        return wiz;
    }

    public CopyMoveWizardImpl(RequestContext requestContext) {
        getWizardPageModel(requestContext);
    }

    public String getPageName(String pageId) {
        return null;
    }
    
    public Model getPageModel(String pageId) {
        Model m = null;
        try {
            m = thePageModel;
        } catch (Exception e) {
            dumpexception(e);
        }
        return m;
    }

    public Class getPageClass(String pageId) {
        Class c = null;
        try {
            c = pageClass[pageIdToStep(pageId)];
        } catch (Exception e) {
            dumpexception(e);
        }
        return c;
    }

    // This can't be called unless firstPage is set.
    public String getFirstPageId() {
        return firstPageId;
    }

    public String getNextPageId(String pageId) {
        int nextPageId = Integer.parseInt(pageId);
        if (!isFinishPageId(pageId)) {
            nextPageId++;
        }
        return Integer.toString(nextPageId);
    }

    public String getName() {
        return name;
    }

    // It should already have a name
    // The framework needs one to identify it.
    // Should be immutable.
    //
    public void setName(String name) {
    }

    /**
      * The wizard framework no longer posts an alert
     * when the user revisits a previously seen page.
     * This method should always return false.
     *
     */
    public boolean warnOnRevisitStep() {
        return false;
    }

    public String getResourceBundle() {
        CCI18N m_I18n  = new CCI18N(RequestManager.getRequestContext(), Constants.RES_BASE_NAME);
        return Constants.RES_BASE_NAME;
    }

    public String getTitle() {
        return title;
    }

    // This is just using the stepText
    // need better source for the example
    //
    public String getStepTitle(String pageId) {
        return stepText[pageIdToStep(pageId)];
    }


    public String[] getFuturePages(String currentPageId) {
        String[] nextPages = futurePages[pageIdToStep(currentPageId)];
        return nextPages;
    }

    public String[] getFutureSteps(String currentPageId) {
        String[] nextPages = futurePages[pageIdToStep(currentPageId)];
        String[] futureSteps = null;

        if (!currentPageId.equals(lastPageId)) {
            futureSteps = new String[nextPages.length];
            for(int i = 0; i < nextPages.length; i++) {
                int futurePage = Integer.parseInt(nextPages[i]);
                futureSteps[i] = stepText[futurePage - 1];
            }
        } 
        return futureSteps;
    }


    public String getStepInstruction(String pageId) {
        String s = null;
        try {
            s = stepInstruction[pageIdToStep(pageId)];
        } catch (Exception e) {
            dumpexception(e);
        }
        return s;
    }

    public String getStepText(String pageId) {
        return stepText[pageIdToStep(pageId)];
    }

    public String[] getStepHelp(String pageId) {
        String[] s = null;
        try {
            s = stepHelp[pageIdToStep(pageId)];
        } catch (Exception e) {
            dumpexception(e);
        }
        return s;
    }

    public boolean isFinishPageId(String pageId) {
        return pageId.equals(finishPageId);
    }

    public boolean hasPreviousPageId(String pageId) {
        return pageIdToStep(getFirstPageId()) != pageIdToStep(pageId);
    }

    // Events
    public boolean done(String wizardName) {
        thePageModel.selectDefaultContext();

        return true;
    }

    public boolean nextStep(WizardEvent wizardEvent) {
 	    boolean result = validate(wizardEvent);
	    if (result == false) {
            return result;
	    }
        return result;
    }

    public boolean previousStep(WizardEvent wizardEvent) {
        return !wizardEvent.getPageId().equals(getFirstPageId());
    }

    public boolean gotoStep(WizardEvent wizardEvent) {
        return true;
    }

    public boolean finishStep(WizardEvent wizardEvent) {
        boolean result = validate(wizardEvent);
	    if (!result) {
            return result;
	    }
	    return true;
	}


    public boolean cancelStep(WizardEvent wizardEvent) {
        thePageModel.clearWizardData();
        return true;
    }

    public void closeStep(WizardEvent wizardEvent) {
    }

    public boolean isSubstep(String pageId) {
	    return false;
	}
        
    // Display a confirmation message if one is specified
    public String getCancelPrompt(String pageId) {
        return cancelMsg;
    }

    public String getPlaceholderText(String pageId) {
        String s = null;
        try {
            s = placeHolders[pageIdToStep(pageId)];
        } catch (Exception e) {
            dumpexception(e);
        }
        return s;
    }

    public boolean helpTab(WizardEvent wizardEvent) {
        return true;
    }

    public boolean stepTab(WizardEvent wizardEvent) {
        return true;
    }

    public boolean canBeStepLink(String pageId) {
        return true;
    }

    public String getResultsPageId(String pageId) {
        return resultsPageId;
    }

    // Helper methods
    //
    private String stepToPageId(int step) {
        return Integer.toString(step + 1);
    }

    private int pageIdToStep(String pageId) {
        return Integer.parseInt(pageId) - 1;
    }

    public String toString() {
        return name;
    }

    private void dumpexception(Exception e) {
        CCDebug.trace3(
        com.sun.web.ui.common.CCDebug.getClassMethod(1) +
        " : " + e.getMessage());
    }

    private boolean isTrue(String tOrf) {
        return tOrf == null ? false : tOrf.equalsIgnoreCase("true");
    }
    

}



