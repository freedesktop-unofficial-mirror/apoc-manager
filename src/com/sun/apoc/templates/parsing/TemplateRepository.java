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

package com.sun.apoc.templates.parsing;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.LinkedList;
import java.util.StringTokenizer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.web.ui.common.CCDebug;
import com.sun.apoc.manager.resource.ResourceRepository;


public class TemplateRepository {
    
    public static final String TEMPLATE_PATH_SEPARATOR = "/";
    public static final String SET_PREFIX = "SET_";
    public static final String SET_INDEX_SEPARATOR = "|";            

    public static final String DEFAULT_PACKAGE_DIR = "/packages";
    public static final String DEFAULT_TEMPLATE_DIR =  File.separator + "templates" + File.separator;
    
    public static final String TEMPLATE_EXTENSION   = ".xml";
    public static final String JAR_EXTENSION        = ".jar";
    public static final String PROPERTIES_EXTENSION = ".properties";
    public static final String ROOT_CATEGORY_NAME   = "Policies";

    private static TemplateRepository m_defaultRepository = null;
    private static Thread m_initializerThread = null;
    
    private File    m_templatesDir = null;
    private File    m_DTDLocation  = null;
    private boolean m_bInitialized = false;
    
    private SAXParser m_parser = null;
    private TemplateCategory m_templateRoot = null;

    
    public static TemplateRepository getDefaultRepository() {
        if (m_defaultRepository == null) {
            m_defaultRepository = new TemplateRepository();
        }
        return m_defaultRepository;
    }
    
    
    protected TemplateRepository() {
        CCDebug.initTrace();
    }
    
    
    public HashMap getTopLevelCategories() {
        isInitialized();
        return m_templateRoot.getSubCategories();
    }
    
    
    public TemplateCategory getCategory(String path) {
        return getCategory(path, TemplateElement.GLOBAL_SCOPE);
    }
    
    
    public TemplateCategory getCategory(String path, byte scope) {
        isInitialized();
        StringTokenizer tokenizer = new StringTokenizer(path, 
            TEMPLATE_PATH_SEPARATOR);
        TemplateCategory category = m_templateRoot;
        TemplateCategory subCategory = null;
        while (tokenizer.hasMoreTokens()) {
            String defaultName = tokenizer.nextToken();
            // special handling for sets
            if (!defaultName.startsWith(SET_PREFIX)) {
                subCategory = category.getSubCategory(defaultName);
            } else {
                TemplatePage page = (TemplatePage) category;
                TemplateSet set = (TemplateSet) 
                    page.getSection(defaultName.substring(SET_PREFIX.length(),
                                    defaultName.indexOf(SET_INDEX_SEPARATOR)));
                subCategory = set.getPage();                
            }
            // error checking and fallback to last found category
            if ((subCategory != null) && (subCategory.isInScope(scope))) {
                category = subCategory;
            } else {
                CCDebug.trace1("Category " + path + " not found!");
                break;
            }
        }
        return category;
    }
    
    
    public TemplatePage getPage(String path) {
        return (TemplatePage) getCategory(path);
    }
    
    
    public TemplatePage getPage(String path, byte scope) {
        return (TemplatePage) getCategory(path, scope);    
    }
    
    
    public void initialize() {
        CCDebug.trace2("Initializing template repository.");
        m_templateRoot = new TemplatePage(ROOT_CATEGORY_NAME, null, 
                                          "APOC.policies.root", null, null);
        try {
            m_parser = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException e) {
            CCDebug.trace1("Error could not initialize template parser!", e);
        } catch (SAXException e) {
            CCDebug.trace1("Error could not initialize template parser!", e);
        }
        m_initializerThread = new Thread() {
            public void run() {
                examineDirectory(getTemplatesDir());
                m_parser = null;
                m_bInitialized = true;
            }
        };
        m_initializerThread.start();
    }
    
    
    public boolean isInitialized() {
        try {
            m_initializerThread.join();
        } catch (InterruptedException e) {}
        return m_bInitialized;
    }
    
    
    public void update() {
        CCDebug.trace2("Update template repository.");
        m_bInitialized = false;
        initialize();
    }
    
    
    public void setTemplatesDir(String path) {
        CCDebug.trace3("Setting templates directory to " + path + "!");
        m_templatesDir = new File(path);
    }
    
    
    public void setTemplateDir(File dir) {
        CCDebug.trace3("Setting templates directory to " + dir.getName() + "!");
        m_templatesDir = dir;
    }
    
    
    public void setDTDLocation(String path) {
        CCDebug.trace3("Setting DTD location to " + path + "!");
        m_DTDLocation = new File(path);
    }
    
    
    public void setDTDLocation(File location) {
        CCDebug.trace3("Setting DTD location to " + location.getName() + "!");
        m_DTDLocation = location;
    }
    
    
    public File getTemplatesDir() {
        if (m_templatesDir == null) {
            setTemplatesDir(DEFAULT_PACKAGE_DIR);
            CCDebug.trace3("No templates directory specified. Using default location (" + DEFAULT_PACKAGE_DIR + ").");
        }
        if (!m_templatesDir.exists()) {
            CCDebug.trace1("Templates directory not found. Assuming fresh start. Creating templates directory.");
            m_templatesDir.mkdir();
        }
        return m_templatesDir;
    }
    
    
    protected void examineDirectory(File dir) {
        CCDebug.trace3("Examine directory " + dir.getAbsolutePath());
        File[] entries = dir.listFiles();
        for(int i = 0; i < entries.length; i++) {
            if (entries[i].isDirectory()) {
                examineDirectory(entries[i]);
            } else {
                if (entries[i].getName().endsWith(TEMPLATE_EXTENSION)) {
                    CCDebug.trace2("Found template " + entries[i].getName());
                    parseTemplate(entries[i]);
                } else if (entries[i].getName().endsWith(JAR_EXTENSION) ||
                       entries[i].getName().endsWith(PROPERTIES_EXTENSION)) {
                    CCDebug.trace2("Found resource file " + entries[i].getName());
                    ResourceRepository.getDefaultRepository().addResource(entries[i]);
                }
            }
        }
    }
    
    protected void parseTemplate(File template) {
        try {
            CCDebug.trace3("Start parsing of " + template.getName());
            m_parser.parse(template, new TemplateHandler(m_templateRoot, 
                                        getTemplatePackageName(template)));
        } catch (IOException e) {
           CCDebug.trace1("Error opening template " + template.getName() + "!", e);
        } catch (SAXException e) {
            CCDebug.trace1("Error parsing template " + template.getName() + "!", e);
        }
    }
    
    
    protected String getTemplatePackageName(File template) {
        String path = template.getAbsolutePath();
        int pos = path.indexOf(DEFAULT_TEMPLATE_DIR);
        if (pos == -1) return null;
        path = path.substring(0, pos);
        pos = path.lastIndexOf(File.separator);
        if (pos == -1) return null;
        path = path.substring(pos + 1);
        return path.toString();
    }
    
    
    protected void getContent(StringBuffer buffer, TemplateCategory category, int level) {
        if (category.hasSubCategories()) {
            Set set = category.getSubCategories().keySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                String name = (String) it.next();
                for(int i = 0; i < level; i++) {
                    buffer.append("       ");
                }
                TemplateCategory subCategory = category.getSubCategory(name);
                buffer.append(subCategory.getDefaultName());
                buffer.append(" (");
                buffer.append(subCategory.getResourceId());
                buffer.append(", ");
                buffer.append(subCategory.getResourceBundle());
                buffer.append(") ");
                buffer.append("\r\n");
                getContent(buffer, category.getSubCategory(name), level + 1);
            }
        }
    }
    
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(super.toString());
        buffer.append("\r\n");
        getContent(buffer, m_templateRoot, 0);
        buffer.append("\r\n");
        return buffer.toString();
    }
    
    
      class TemplateHandler extends DefaultHandler {
        
        public static final String CATEGORY_TAG     = "category";
        public static final String PAGE_TAG         = "page";
        public static final String SECTION_TAG      = "section";
        public static final String SET_TAG          = "set";
        public static final String PROPERTY_TAG     = "property";
        public static final String RES_IMPORT_TAG   = "resImport";
        public static final String HELP_IMPORT_TAG  = "helpImport";
        public static final String INLINE_HELP_TAG  = "inlineHelp";
        public static final String ENUMERATION_TAG  = "enumeration";
        public static final String VALUE_TAG        = "value";
        public static final String VISUAL_TAG       = "visual";
        public static final String CHECKBOX_TAG     = "checkBox";
        public static final String CHOOSER_TAG      = "chooser";        
        public static final String RES_ID_ATTR      = "apt:resId";
        public static final String LABEL_ATTR       = "apt:label";
        public static final String NAME_ATTR        = "apt:name";
        public static final String PACKAGE_ATTR     = "apt:packagePath";
        public static final String FILE_ATTR        = "apt:filePath";
        public static final String VISUAL_TYPE_ATTR = "apt:visualType";
        public static final String VISUAL_TYPE_ATTR2= "apt:type";
        public static final String DATA_PATH_ATTR   = "apt:dataPath";
        public static final String VALUE_ATTR       = "oor:value";
        public static final String SEPARATOR_ATTR   = "oor:separator";
        public static final String TYPE_ATTR        = "oor:type";
        public static final String SCOPE_ATTR       = "apt:scope";
        public static final String STORE_ATTR       = "apt:storeDefault";
        public static final String INLINE_HELP_ATTR = "apt:inlineHelp";
        public static final String ONLINE_HELP_ATTR = "apt:onlineHelp";
        public static final String LABEL_POST_ATTR  = "apt:labelPost"; 
        public static final String NIL_ATTR         = "xsi:nil";
        public static final String EXTRA_HTML_ATTR  = "apt:extraHtml";
        public static final String CHOOSER_PATH_ATTR= "apt:dataPath";
        public static final String EXTENDS_CHOOSER_ATTR= "apt:extendsChooser";        
        public static final String LABEL_POPUP_ATTR = "apt:labelPopup";                
        public static final String XML_HANDLER_TAG  = "xmlHandler";
        public static final String ACTION_HANDLER_TAG= "actionHandler";
        public static final String EVENT_TAG        = "event";
        public static final String ACTION_TAG       = "action";
        public static final String WHEN_TAG         = "when";
        public static final String OTHERWISE_TAG    = "otherwise";
        public static final String COMMAND_TAG      = "command";
        public static final String XML_HANDLER_ATTR = "apt:xmlHandler";
        public static final String ACTION_HANDLER_ATTR = "apt:actionHandler";
        public static final String HANDLER_TYPE_ATTR= "apt:type";        
        public static final String TEST_ATTR        = "apt:test";
        public static final String CLASS_ATTR       = "apt:class";
        
        public static final String TEMPLATE_SYSTEM_ID = "policytemplate.dtd";

        private String m_packageName        = null;
        private String m_resourceBundle     = null;
        private String m_helpFile           = null;
        private StringBuffer m_buffer       = null;
        private LinkedList m_context        = null;

        
        public TemplateHandler(TemplateCategory templateRoot, 
                    String packageName) {
            m_buffer = new StringBuffer();
            m_context = new LinkedList();
            m_context.add(templateRoot);
            m_packageName = packageName;
        }

        
        protected Object getCurrentContext() {
            return (m_context.getLast());
        }
        
        
        public void startElement(String uri, String localName, 
                String qName, Attributes attr) throws SAXException {
            if (qName.equals(CATEGORY_TAG)) {
                String name = attr.getValue(NAME_ATTR);
                TemplateCategory template = (TemplateCategory) getCurrentContext();
                TemplateCategory subCategory = template.getSubCategory(name);
                if (subCategory == null) {
                    String label = attr.getValue(RES_ID_ATTR);
                    if (label == null) {
                        label = attr.getValue(LABEL_ATTR);
                    } 
                    subCategory = new TemplatePage(name, 
                                         attr.getValue(SCOPE_ATTR),
                                         label, 
                                         m_resourceBundle, template);
                    subCategory.setDescriptionId(attr.getValue(INLINE_HELP_ATTR));                     
                    template.addSubCategory(subCategory);
                }
                m_context.add(subCategory);
                
            } else if (qName.equals(PAGE_TAG)){    
                String name = attr.getValue(NAME_ATTR);
                if (getCurrentContext() instanceof TemplateSet) {
                    TemplateSet set = (TemplateSet) getCurrentContext();
                    String label = attr.getValue(RES_ID_ATTR);
                    if (label == null) {
                        label = attr.getValue(LABEL_ATTR);
                    } 
                    if (attr.getValue(ONLINE_HELP_ATTR) != null) {
                        m_helpFile = attr.getValue(ONLINE_HELP_ATTR);
                    }
                    TemplatePage page = new TemplatePage(name,
                                                attr.getValue(SCOPE_ATTR), 
                                                label, 
                                                m_resourceBundle, m_helpFile, 
                                                null, 
                                                m_packageName);
                    page.setDataPath(set.getDataPath());  
                    page.setDescriptionId(attr.getValue(INLINE_HELP_ATTR));                          
                    set.setPage(page);
                    m_context.add(page);

                } else {
                    TemplateCategory template = (TemplateCategory) getCurrentContext();
                    TemplateCategory subCategory = template.getSubCategory(name);
                    if (subCategory == null) {
                        String label = attr.getValue(RES_ID_ATTR);
                        if (label == null) {
                            label = attr.getValue(LABEL_ATTR);
                        }
                        if (attr.getValue(ONLINE_HELP_ATTR) != null) {
                            m_helpFile = attr.getValue(ONLINE_HELP_ATTR);
                        } 
                        subCategory = new TemplatePage(name,
                                             attr.getValue(SCOPE_ATTR), 
                                             label, 
                                             m_resourceBundle, m_helpFile, 
                                             template,
                                             m_packageName);
                        subCategory.setDescriptionId(attr.getValue(INLINE_HELP_ATTR));                     
                        template.addSubCategory(subCategory);
                    }
                    m_context.add(subCategory);
                }
                
            } else if (qName.equals(SECTION_TAG)) {
                String label = attr.getValue(RES_ID_ATTR);
                if (label == null) {
                    label = attr.getValue(LABEL_ATTR);
                }     
                TemplateSection section = new TemplateSection(
                                                attr.getValue(NAME_ATTR),
                                                attr.getValue(SCOPE_ATTR), 
                                                label, 
                                                m_resourceBundle);
                TemplatePage page = (TemplatePage) getCurrentContext();
                page.addSection(section);
                m_context.add(section);        
                
            } else if (qName.equals(XML_HANDLER_TAG)) {
                String label = attr.getValue(NAME_ATTR);
                TemplateXMLHandler handler = new TemplateXMLHandler(label);
                TemplatePage page = (TemplatePage) getCurrentContext();
                page.addXMLHandler(handler);
                m_context.add(handler);        

            } else if (qName.equals(ACTION_HANDLER_TAG)) {
                String name = attr.getValue(NAME_ATTR);
                String classname = attr.getValue(CLASS_ATTR);
                String packageDir = m_packageName;
                TemplateActionHandler handler = new TemplateActionHandler(name, classname, packageDir);
                TemplatePage page = (TemplatePage) getCurrentContext();
                page.addActionHandler(handler);
                m_context.add(handler);  
                
            } else if (qName.equals(SET_TAG)) {
                String label = attr.getValue(RES_ID_ATTR);
                if (label == null) {
                    label = attr.getValue(LABEL_ATTR);
                } 
                TemplateSet set = new TemplateSet(attr.getValue(NAME_ATTR),
                                        attr.getValue(SCOPE_ATTR), 
                                        label, 
                                        m_resourceBundle,
                                        attr.getValue(DATA_PATH_ATTR),
                                        attr.getValue(LABEL_POPUP_ATTR));
                TemplatePage page = (TemplatePage) getCurrentContext();                        
                page.addSection(set);
                m_context.add(set);    
                
            } else if (qName.equals(PROPERTY_TAG)) {
                String label = attr.getValue(RES_ID_ATTR);
                if (label == null) {
                    label = attr.getValue(LABEL_ATTR);
                }     
                TemplateProperty property = new TemplateProperty(
                                                    attr.getValue(NAME_ATTR),
                                                    attr.getValue(SCOPE_ATTR), 
                                                    label, 
                                                    m_resourceBundle,
                                                    attr.getValue(DATA_PATH_ATTR), 
                                                    attr.getValue(VISUAL_TYPE_ATTR),
                                                    attr.getValue(TYPE_ATTR),
                                                    attr.getValue(STORE_ATTR),
                                                    attr.getValue(XML_HANDLER_ATTR),
                                                    attr.getValue(ACTION_HANDLER_ATTR),
                                                    attr.getValue(EXTRA_HTML_ATTR));
                property.setDescriptionId(attr.getValue(INLINE_HELP_ATTR));
                TemplateSection section = (TemplateSection) getCurrentContext();                                    
                section.addProperty(property);
                m_context.add(property);
                
            } else if (qName.equals(VISUAL_TAG)) {
                TemplateProperty property = (TemplateProperty) getCurrentContext();
                property.setVisualType(attr.getValue(VISUAL_TYPE_ATTR2));    
                
            } else if (qName.equals(CHECKBOX_TAG)) {
                TemplateProperty property = (TemplateProperty) getCurrentContext();
                property.setVisualType(TemplateProperty.CHECKBOX);
                //#b5055105# support for localization of label
                String label = attr.getValue(LABEL_POST_ATTR);
                property.setLabelPost(label);
                
            } else if (qName.equals(CHOOSER_TAG)) {
                TemplateProperty property = (TemplateProperty) getCurrentContext();
                property.setVisualType(TemplateProperty.CHOOSER);
                property.setExtendsChooser(attr.getValue(EXTENDS_CHOOSER_ATTR));    
                property.setChooserPath(attr.getValue(CHOOSER_PATH_ATTR));    
                property.setLabelPopup(attr.getValue(LABEL_POPUP_ATTR));    
                
            } else if (qName.equals(RES_IMPORT_TAG)) {
                m_resourceBundle = attr.getValue(PACKAGE_ATTR);
                
            } else if (qName.equals(HELP_IMPORT_TAG)) {
                m_helpFile = attr.getValue(FILE_ATTR);
            
            } else if (qName.equals(INLINE_HELP_TAG)) {
                NarratedElement element = (NarratedElement) getCurrentContext();
                element.setDescriptionId(attr.getValue(RES_ID_ATTR)); 
            
            } else if (qName.equals(ENUMERATION_TAG)) {
                TemplateProperty property = (TemplateProperty) getCurrentContext();
                String label = attr.getValue(RES_ID_ATTR);
                if (label == null) {
                    label = attr.getValue(LABEL_ATTR);
                }
                property.addConstraint(attr.getValue(VALUE_ATTR), label);

            } else if (qName.equals(VALUE_TAG)) {
                TemplateProperty property = (TemplateProperty) getCurrentContext();
                String separator = attr.getValue(SEPARATOR_ATTR);
                property.setSeparator(separator);
                property.setDefaultNilValue(attr.getValue(NIL_ATTR));
                m_buffer.setLength(0);
            
            } else if (qName.equals(ACTION_TAG)) {
                m_context.add(ACTION_TAG) ;
                
            } else if (qName.equals(EVENT_TAG)) {
                TemplateXMLHandler handler = (TemplateXMLHandler) getCurrentContext();
                handler.addType(attr.getValue(HANDLER_TYPE_ATTR));
            
            } else if (qName.equals(WHEN_TAG)) {
                TemplateXMLHandler handler = (TemplateXMLHandler) m_context.get(m_context.size() - 2);
                handler.addTest(attr.getValue(TEST_ATTR));
                handler.addNewCommandList() ;
                m_context.add(WHEN_TAG) ;

            } else if (qName.equals(OTHERWISE_TAG)) {
                m_context.add(OTHERWISE_TAG) ;
                
            } else if (qName.equals(COMMAND_TAG)) {
                m_buffer.setLength(0);
            }
        }
        
        
        public void characters(char[] chars, int start, int len) 
                throws SAXException {
            m_buffer.append(chars, start, len);        
        }
        
        
        public void endElement(String uri, String localName, String qName) 
                throws SAXException {
            
            if (qName.equals(PROPERTY_TAG) 
                || qName.equals(SET_TAG)
                || qName.equals(SECTION_TAG)
                || qName.equals(OTHERWISE_TAG)
                || qName.equals(WHEN_TAG)
                || qName.equals(ACTION_TAG)
                || qName.equals(XML_HANDLER_TAG)                
                || qName.equals(ACTION_HANDLER_TAG) 
                || qName.equals(PAGE_TAG)
                || qName.equals(CATEGORY_TAG)) {    
                m_context.removeLast();
                
            } else if (qName.equals(VALUE_TAG)) {
                TemplateProperty property = (TemplateProperty) getCurrentContext();
                if (!property.hasDefaultNilValue()) {
                    property.setDefaultValue(m_buffer.toString());
                }
                m_buffer.setLength(0);    

            } else if (qName.equals(COMMAND_TAG)) {
                if(((String)getCurrentContext()).equals(ACTION_TAG)) {
                    TemplateXMLHandler handler = (TemplateXMLHandler) m_context.get(m_context.size() - 2) ;
                    handler.addCommand(m_buffer.toString());
                    m_buffer.setLength(0);    
                } 
                else if(((String)getCurrentContext()).equals(WHEN_TAG)) {
                    TemplateXMLHandler handler = (TemplateXMLHandler) m_context.get(m_context.size() - 3) ;
                    handler.addWhenCommand(m_buffer.toString());
                    m_buffer.setLength(0);   
                }
                else if(((String)getCurrentContext()).equals(OTHERWISE_TAG)) {
                    TemplateXMLHandler handler = (TemplateXMLHandler) m_context.get(m_context.size() - 3) ;
                    handler.addOtherwiseCommand(m_buffer.toString());
                    m_buffer.setLength(0);   
                }

            }
        
        }
        
        
        public InputSource resolveEntity (String publicId, String systemId) {
            if ((systemId.endsWith(TEMPLATE_SYSTEM_ID)) && (m_DTDLocation != null)) {
                try {
                    FileInputStream stream = new FileInputStream(m_DTDLocation);
                    return new InputSource(stream);
                } catch (FileNotFoundException e) {
                    CCDebug.trace1("Could not find DTD!", e);
                    return null;
                }
            } else {
                return null;
            }
        }
    }
}

