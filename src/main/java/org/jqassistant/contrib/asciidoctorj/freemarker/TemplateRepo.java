package org.jqassistant.contrib.asciidoctorj.freemarker;

import freemarker.template.Template;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;

public interface TemplateRepo {

    /**
     * load the in "templates-path" folder available templates into the template List of this IncludeProcessor; if the wanted template does not exist in custom template location, return the corresponding default template
     * @param attributes attributes containing at least the templatesPath property
     * @param templateName file name of the wanted template
     * @return the Template with corresponding name; either loaded from custom location or default
     */
    Template findTemplate(ProcessAttributes attributes, String templateName); //TODO: load with custom maven dependencies

}
