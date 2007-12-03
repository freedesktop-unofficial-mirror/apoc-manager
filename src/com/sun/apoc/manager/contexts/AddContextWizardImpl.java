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

import java.util.Map;
import java.util.ArrayList;

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

import java.io.File;

interface WizardImplDataUtil {

    public String getErrorMsg();
}

interface WizardImplData {


    final String name = "AddContextWizardImpl";    
    final String cancelMsg = "";
    
    final String finishPageId = "10";    
    final String firstPageId = "2";
    final String lastPageId = "11";
    final String resultsPageId = "11";
    
    final int[] pageIds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
    final String[][] defaultFuturePages = { {null}, {null}, {"9", "4", null}, {null}, 
                                     {"9", "4", null}, {null}, {null}, {"4", null},
                                     {"4", null}, {"11"}, {null}, {"10", "11"}, {"10", "11"}, {null}, {"10", "11"} }; 
                                     
    final String[][] futurePages = { {null}, {null}, {null}, {null}, 
                                     {null}, {null}, {null}, {"4", null},
                                     {"4", null}, {"11"}, {null}, {"10", "11"}, {"10", "11"}, {null}, {"10", "11"} };                       

    final String[][] futurePagesHybrid = { {null}, {null}, {null}, {null}, 
                                           {"8", "4", null}, {null}, {"12", null}, {"4", null},
                                           {"4", null}, {"11"}, {null}, {null}, {"10"}, {null}, {null} };               
                                     
    final Class[] pageClass = {
        Wizard1View.class,
        Wizard2View.class,
        Wizard3View.class,
        Wizard4View.class,
        Wizard5View.class,
        Wizard6View.class,
        Wizard7View.class,
        Wizard8View.class,
        Wizard9View.class,
        Wizard10View.class,
        Wizard11View.class,
        Wizard12View.class,
        Wizard13View.class,        
        Wizard14View.class,
        Wizard15View.class
    };
    
    final String[] stepText = {
        null,
        "APOC.wiz.steptext.2",
        "APOC.wiz.steptext.3",
        "APOC.wiz.steptext.4",
        "APOC.wiz.steptext.5",
        "APOC.wiz.steptext.6",
        "APOC.wiz.steptext.7",
        "APOC.wiz.steptext.8",
        "APOC.wiz.steptext.9",
        "APOC.wiz.steptext.10",
        "APOC.wiz.steptext.11",
        "APOC.wiz.steptext.12",
        "APOC.wiz.steptext.13",
        "APOC.wiz.steptext.14",
        "APOC.wiz.steptext.15"
    };
    
    final String[] pageTitle = {
        null,
        "APOC.add.context.wizard.page2.title.text",
        "APOC.add.context.wizard.page3.title.text",
        "APOC.add.context.wizard.page4.title.text",
        "APOC.add.context.wizard.page5.title.text",
        "APOC.add.context.wizard.page6.title.text",
        "APOC.add.context.wizard.page7.title.text",
        "APOC.add.context.wizard.page8.title.text",
        "APOC.add.context.wizard.page9.title.text",
        "APOC.add.context.wizard.page10.title.text",
        "APOC.add.context.wizard.page11.title.text",
        "APOC.add.context.wizard.page12.title.text",
        "APOC.add.context.wizard.page13.title.text",
        "APOC.add.context.wizard.page14.title.text",
        "APOC.add.context.wizard.page15.title.text"   
    };

    final String[][] stepHelp = {
        {null},
        {"APOC.wiz.help.2"},
        {"APOC.wiz.help.3"},
        {"APOC.wiz.help.4"},
        {"APOC.wiz.help.5"},
        {"APOC.wiz.help.6"},
        {"APOC.wiz.help.7"},
        {"APOC.wiz.help.8"},
        {"APOC.wiz.help.9"},
        {"APOC.wiz.help.10"},
        {"APOC.wiz.help.11"},
        {"APOC.wiz.help.12"},
        {"APOC.wiz.help.13"},
        {"APOC.wiz.help.14"},                
        {"APOC.wiz.help.15"}
    };

    final String[] stepInstruction = {
        null,
        "APOC.wiz.instruction.2",
        "APOC.wiz.instruction.3",
        "APOC.wiz.instruction.4",
        "APOC.wiz.instruction.5",
        "APOC.wiz.instruction.6",
        "APOC.wiz.instruction.7",
        "APOC.wiz.instruction.8",
        "APOC.wiz.instruction.9",
        "APOC.wiz.instruction.10",
        "APOC.wiz.instruction.11",
        "APOC.wiz.instruction.12",
        "APOC.wiz.instruction.13",
        "APOC.wiz.instruction.14",
        "APOC.wiz.instruction.15"   
    };
    
     final String[] placeHolders = {
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null    
     };  
    
    
}

public class AddContextWizardImpl implements WizardImplData,
    WizardInterface, WizardInterfaceExt{

    private AddContextWizardPageModel thePageModel;

    private String getParameter(String name,
        RequestContext requestContext) {

        Map parameterMap = requestContext.getRequest().getParameterMap();

        // Need the qualified name. Not sure how to get it
        //
        String[] values = (String[])parameterMap.get(name);
        return values == null ? null : values[0];
    }


    private void getWizardPageModel(RequestContext requestContext) {

        String modelInstanceName = getParameter(
        ManagerTableView.WIZARDPAGEMODELNAME,
        requestContext);
        
        ModelManager mm = requestContext.getModelManager();
        thePageModel = (AddContextWizardPageModel)mm.getModel(
        com.sun.apoc.manager.contexts.AddContextWizardPageModel.class,
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
        AddContextWizardImpl wiz = new AddContextWizardImpl(requestContext);
        return wiz;
    }

    public AddContextWizardImpl(RequestContext requestContext) {
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
        String backendType = null;
        switch (nextPageId) {
 //       case 1:
  //          nextPageId = 2;
  //          break;

        case 2:
            resetFuturePages();
            backendType = (String)thePageModel.getValue(
            Wizard2View.CHILD_BACKEND_TYPE);
            if (backendType.equals("1")) {
                nextPageId = 12;
            } else {
                nextPageId = 3;
            }
            if (backendType.equals("0")) {
                String[] nextpages6 = {"15", "10", "11" };
                futurePages[5] = nextpages6;         
                futurePages[6] = nextpages6;  
            } else if (backendType.equals("2")) {
                String[] nextpages4 = {"6", "12", "10", "11" };
                futurePages[3] = nextpages4;
                String[] nextpages6 = {"12", "10", "11" };
                futurePages[5] = nextpages6;
                futurePages[6] = nextpages6;
            }
            // if its an edit wizard set helps accordingly
            if (thePageModel.getValue(thePageModel.WIZARD_CONFIG_FILE) != null) {
                stepHelp[9][0] = "APOC.wiz.help.edit.10";
                stepInstruction[9] = "APOC.wiz.instruction.edit.10";                
            } else {
                stepHelp[9][0] = "APOC.wiz.help.10";
                stepInstruction[9] = "APOC.wiz.instruction.10";                 
            }
            break;

        case 3:
            if (isLDAPServerTypeKnown()) {
                if (isBaseDNKnown()) {
                    stepInstruction[8] = "APOC.wiz.instruction.8";                      
                } else {
                    stepInstruction[8] = "APOC.wiz.instruction.9";    
                }
                nextPageId = 9;
            } else {
                nextPageId = 5;
            }
            break;

        case 4:
            backendType = (String)thePageModel.getValue(
            Wizard2View.CHILD_BACKEND_TYPE);
            nextPageId = 6; 
            if (backendType.equals("0") && isExistingInstallation()) {
                if(isApoc1Installation()) {
                    nextPageId = 14;
                } else {
                    nextPageId = 10;
                }
            } 
            break;

        case 5:
            if (isBaseDNKnown()) {
                stepInstruction[nextPageId-1] = "APOC.wiz.instruction.8";                      
            } else {
                stepInstruction[nextPageId-1] = "APOC.wiz.instruction.9";    
            }
            nextPageId = 9;
            break;
       
        case 6:
            String isAdaptMetaConfig = (String)thePageModel.getValue(
            Wizard6View.CHILD_ADAPT_METACONFIG);
            if (isAdaptMetaConfig.equals("1")) {
                nextPageId = 7;
            } else {
                backendType = (String)thePageModel.getValue(
                Wizard2View.CHILD_BACKEND_TYPE);
                if (backendType.equals("2")) {
                    nextPageId = 12;
                } else  {
                    nextPageId = 15;
                    stepHelp[nextPageId-1][0] = "APOC.wiz.help.15";
                    stepInstruction[nextPageId-1] = "APOC.wiz.instruction.15"; 
                }
            }
            break;

        case 7:
           backendType = (String)thePageModel.getValue(
            Wizard2View.CHILD_BACKEND_TYPE);
            if (backendType.equals("2")) {
                nextPageId = 12;
            } else {
                nextPageId = 15;
                stepHelp[nextPageId-1][0] = "APOC.wiz.help.15";
                stepInstruction[nextPageId-1] = "APOC.wiz.instruction.15"; 
            }
            break;

        case 8:
            nextPageId = 4;
            break;

        case 9:
            nextPageId = 4;
            break;
        
        case 10:
            nextPageId = 11;
            break;

        case 11:
            break;

        case 12:
            nextPageId = 10;
            String root = (String)thePageModel.getValue(thePageModel.FILEPATH);
            if (root != null) {
                File dir = new File(root);
                String[] children = dir.list();
                if (children != null) {
                    for (int i=0; i<children.length; i++) {
                        // Get filename of file or directory
                        String filename = children[i];
                        if ((filename.equals("profiles")) 
                                || (filename.equals("assignments"))
                                    || (filename.equals("entities.txt"))
                                        || (filename.equals("OrganizationMapping.properties"))
                                            || (filename.equals("UserProfileMapping.properties"))) { 
                                        nextPageId = 13;
                        }
                    }
                }
            }
            break;

        case 13:
            nextPageId = 10;
            break;
            
        case 14:
            String migrateProfiles = (String)thePageModel.getValue(Wizard14View.CHILD_MIGRATE);
            if (migrateProfiles.equals("1")) {
                nextPageId = 10;
            } else {
                nextPageId = 15;
                stepHelp[nextPageId-1][0] = "APOC.wiz.help.15a";
                stepInstruction[nextPageId-1] = "APOC.wiz.instruction.15a";                
            }
            break;       

        case 15:
            nextPageId = 10;
            break;  
            
        default:
            break;
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
        String title = (String)thePageModel.getValue(thePageModel.WIZARD_TITLE);
        if (title == null) {
            title = "APOC.wiz.title";
        }
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
        String backendType = (String)thePageModel.getValue(Wizard2View.CHILD_BACKEND_TYPE);  
        if ((backendType != null) && (backendType.equals("2"))) {
     //       nextPages = futurePagesHybrid[pageIdToStep(currentPageId)];
        }
        if (!currentPageId.equals(lastPageId)) {
            futureSteps = new String[nextPages.length];
            for(int i = 0; i < nextPages.length; i++) {
                if (nextPages[i] == null) {
                    futureSteps[i] = "APOC.wiz.placeholder";  
                } else {
                    int futurePage = Integer.parseInt(nextPages[i]);
                    futureSteps[i] = stepText[futurePage - 1];
                }
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
	if ((pageId != null) && (pageIdToStep(pageId) == 4 
                                    || pageIdToStep(pageId) == 6 
                                    || pageIdToStep(pageId) == 12
                                    || pageIdToStep(pageId) == 18)){
            return true;
        } else {
            return false;
        }
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
    
    private boolean isExistingInstallation() {
        String existingInstall = (String)thePageModel.getWizardValue(thePageModel.EXISTING_INSTALL);
        if (existingInstall.equals("true")) {
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isApoc1Installation() {
        String isApoc1 = (String)thePageModel.getWizardValue(thePageModel.APOC1_INSTALL);
        if (isApoc1.equals("true")) {
            return true;
        } else {
            return false;
        }
    }    
    
    private boolean isBaseDNExisting() {
        return true;
    }
    
    private boolean isLDAPServerTypeKnown() {
        String backendType = (String)thePageModel.getValue(thePageModel.VENDOR_ID);
        if (backendType != null) { 
            return true;
        }
        return false;
    }
    
    private boolean isBaseDNKnown() {
        ArrayList baseDNs = (ArrayList)thePageModel.getValue(thePageModel.BASEDN_LIST);
        if (baseDNs != null) { 
            return true;
        }
        return false;
    }

    private void resetFuturePages() {
        for (int i = 0; i < defaultFuturePages.length; i++) {
            futurePages[i] = defaultFuturePages[i];                
        }
    }
}



