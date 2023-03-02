package org.jqassistant.contrib.asciidoctorj;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.Options;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AsciidoctorConceptsAndConstraintTest {
    @Test
    void loadExtension() throws IOException {
        Asciidoctor asciidoctor = Asciidoctor.Factory.create();

        Options opt = Options.builder().attributes(Attributes.builder().attribute("report-path", "src/test/resources/jqassistant-report.xml").build()).build();

        //System.out.println(Files.readString(Path.of(URI.create("src/test/resources/conceptsAndConstraintsGoal"))));

        assert(Files.readString(Paths.get("src/test/resources/conceptsAndConstraintsGoal")).equals(asciidoctor.convert("include::jQA:ConceptsAndConstraints[report-path = \"src/test/resources/jqassistant-report.xml\", id = \"jmolecules-ddd:AggregateRootExtend\"]", opt)));
    }
}
