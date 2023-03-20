package org.jqassistant.contrib.asciidoctorj.freemarker;

import freemarker.template.Template;
import org.jqassistant.contrib.asciidoctorj.processors.attributes.ProcessAttributes;

public interface TemplateRepo {

    /**
     * Load the in template with the corresponding file name.
     * The first call to this function tries to set the custom template location (if the "templatesPath" property in attributes is set). Otherwise, it only sets the default location for template loading. Subsequent calls skip this part.
     * If custom template location is set, it tries first to load the template from there.
     * If template does not exist there it loads the corresponding template from default location.
     *
     * @param attributes may contain the templatesPath property (if not set on first call, the repo defaults to default template location)
     * @param templateName file name of the wanted template
     * @return the Template with corresponding name; either loaded from custom or default location
     */
    Template findTemplate(ProcessAttributes attributes, String templateName);

}
