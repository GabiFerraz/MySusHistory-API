package com.api.mysushistory.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static java.lang.String.format;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(
    packages = "com.api.mysushistory",
    importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {
  /* DOMAIN */
  @ArchTest
  private static final ArchRule DOMAIN_DOES_NOT_DEPEND_ON_ANYONE =
      classes()
          .that()
          .resideInAPackage("..core.domain..")
          .should()
          .onlyDependOnClassesThat()
          .resideInAnyPackage("..core.domain..", "..core.dto..", "java..", "lombok..", "jakarta..")
          .as("Domain should be independent")
          .allowEmptyShould(true);

  /* USECASE */
  @ArchTest
  private static final ArchRule USECASE_MAY_ONLY_BE_ACCESSED_BY_ENTRYPOINT =
      layeredArchitecture()
          .consideringAllDependencies()
          .layer("usecase")
          .definedBy("..core.usecase..")
          .layer("config")
          .definedBy("..entrypoint.config..")
          .layer("controller")
          .definedBy("..entrypoint.controller..")
          .layer("presenter")
          .definedBy("..presenter..")
          .whereLayer("usecase")
          .mayOnlyBeAccessedByLayers("config", "controller", "presenter")
          .allowEmptyShould(true);

  /* GATEWAY */
  @ArchTest
  private static final ArchRule GATEWAY_MAY_ONLY_BE_ACCESSED_BY_USE_CASE_AND_INFRA_GATEWAY =
      layeredArchitecture()
          .consideringAllDependencies()
          .layer("core_gateway")
          .definedBy("..core.gateway..")
          .layer("infra_gateway")
          .definedBy("..infra.gateway..")
          .layer("usecase")
          .definedBy("..core.usecase..")
          .whereLayer("core_gateway")
          .mayOnlyBeAccessedByLayers("infra_gateway", "usecase")
          .allowEmptyShould(true);

  @ArchTest
  private static final ArchRule GATEWAY_MUST_BE_INTERFACE =
      classes().that().resideInAPackage("..core.gateway..").should().beInterfaces();

  @ArchTest
  private static final ArchRule GATEWAY_SHOULD_NOT_DEPEND_ON_EXTERNAL_PACKAGES =
      classes()
          .that()
          .resideInAPackage("core.gateway..")
          .should()
          .onlyDependOnClassesThat()
          .resideInAnyPackage("..core.domain..", "java..", "lombok..", "jakarta..")
          .as("Gateway interfaces should be independent of external classes.")
          .allowEmptyShould(true);

  /* CONTROLLER */
  @ArchTest
  private static final ArchRule REST_CONTROLLERS_MUST_RESIDE_IN_ENTRYPOINT_PACKAGE =
      classes()
          .that()
          .areAnnotatedWith(RestController.class)
          .should()
          .resideInAPackage("..entrypoint.controller..")
          .as(
              "Controllers should reside in a package 'com.api.mysushistory.entrypoint.controller'.")
          .allowEmptyShould(true);

  @ArchTest
  private static final ArchRule LAYER_ENTRYPOINT_ARE_RESPECTED =
      layeredArchitecture()
          .consideringAllDependencies()
          .layer("controller")
          .definedBy("..entrypoint.controller..")
          .whereLayer("controller")
          .mayNotBeAccessedByAnyLayer()
          .allowEmptyShould(true);

  @ArchTest
  private static final ArchRule CONTROLLER_SHOULD_RETURN_A_PRESENTER_RESPONSE =
      classes()
          .that()
          .areAnnotatedWith(RestController.class)
          .should(buildControllerReturnAPresenterRule());

  private static ArchCondition<JavaClass> buildControllerReturnAPresenterRule() {
    return new ArchCondition<>("return a presenter response") {
      @Override
      public void check(JavaClass javaClass, ConditionEvents events) {
        final var publicMethods = this.getAllPublickMethods(javaClass);

        publicMethods.forEach(
            publicMethod ->
                this.getResponseClassFromMethod(publicMethod)
                    .ifPresent(
                        responseClass -> {
                          final var responseClassIsNotFromPackagePresenter =
                              !responseClass
                                  .getPackageName()
                                  .equals("com.api.mysushistory.presenter.response");
                          if (responseClassIsNotFromPackagePresenter) {
                            events.add(
                                SimpleConditionEvent.violated(
                                    javaClass,
                                    format(
                                        "Method=[%s] in Class=[%s] not return a presenter response=[%s]",
                                        publicMethods.getClass().getName(),
                                        javaClass.getSimpleName(),
                                        responseClass.getName())));
                          }
                        }));
      }

      private Optional<JavaClass> getResponseClassFromMethod(final JavaMethod publicMethod) {
        return publicMethod.getReturnType().toErasure().getAllRawInterfaces().stream()
            .filter(
                rawType ->
                    !List.of("org.springframework.http", "java.lang", "java.util")
                        .contains(rawType.getPackageName()))
            .findFirst();
      }

      private List<JavaMethod> getAllPublickMethods(final JavaClass javaClass) {
        return javaClass.getMethods().stream()
            .filter(
                mainMethods ->
                    mainMethods.getModifiers().stream()
                        .anyMatch(modifier -> "PUBLIC".equals(modifier.name())))
            .toList();
      }
    };
  }

  /* INFRA */
  @ArchTest
  private static final ArchRule LAYER_INFRA_ARE_RESPECTED =
      layeredArchitecture()
          .consideringAllDependencies()
          .layer("infra")
          .definedBy("..infra..")
          .layer("main")
          .definedBy("com.api.mysushistory..")
          .whereLayer("infra")
          .mayOnlyBeAccessedByLayers("main")
          .allowEmptyShould(true);

  /* GENERAL */
  @ArchTest
  private static final ArchRule INTERFACES_MUST_NOT_BE_PLACED_IN_IMPLEMENTATION_PACKAGES =
      noClasses()
          .that()
          .resideInAPackage("..impl..")
          .should()
          .beInterfaces()
          .allowEmptyShould(true);
}
