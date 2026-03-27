package com.sergeev.conscious_citizen_server;

import com.tngtech.archunit.core.domain.JavaClass;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest
class ModulithStructureTest {

    @Test
    void verifyModularStructure() {
        ApplicationModules modules = ApplicationModules.of(ConsciousCitizenServerApplication.class,
                JavaClass.Predicates.resideInAPackage("..api.."));
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        ApplicationModules modules = ApplicationModules.of(ConsciousCitizenServerApplication.class);
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }
}