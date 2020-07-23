package com.nika.annotations.framework.processor;

import com.nika.annotations.framework.annotation.Service;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.lang.model.util.ElementFilter.*;
import static org.apache.commons.lang.StringUtils.*;

@SupportedAnnotationTypes("com.nika.annotations.framework.annotation.Service")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class InjectedServiceAnnotationProcessor extends AbstractProcessor {
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
//        checkInjectedServicesForSetters(roundEnvironment.getElementsAnnotatedWith(Service.class));
        Set<? extends Element> fieldsWithServiceAnnotation = findFieldsWithServiceAnnotation(roundEnvironment);

        checkIfInjectionsAreInServices(fieldsWithServiceAnnotation);
        checkInjectedServicesForSetters(fieldsWithServiceAnnotation);
        return false;
    }

    private void checkIfInjectionsAreInServices(Set<? extends Element> fieldsWithServiceAnnotation) {
        fieldsWithServiceAnnotation
                .forEach(field -> {
                    if (field.getEnclosingElement().getAnnotation(Service.class) == null) {
                        throwCompilationError(field, field.asType().toString() + " Can not be injected into class that is not annotated with @Service");
                    }
                });
    }

    private Set<? extends Element> findFieldsWithServiceAnnotation(RoundEnvironment roundEnvironment) {
        return roundEnvironment.getElementsAnnotatedWith(
                processingEnv.getElementUtils().getTypeElement("com.nika.annotations.framework.annotation.Service"))
                .stream()
                .filter(element -> element.getKind().isField())
                .collect(Collectors.toSet());
    }

    private void checkInjectedServicesForSetters(Set<? extends Element> fieldsWithServiceAnnotation) {
        fieldsWithServiceAnnotation
                .forEach(field -> {
                    Optional<ExecutableElement> setter = findSetterFor(field);
                    if (!setter.isPresent()) {
                        throwCompilationError(field, "No setter found for injected service " +  field.getSimpleName().toString());
                    }

                });
    }

    private void throwCompilationError(Element field, String errorText) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                errorText,
                field
        );
    }

    private Optional<ExecutableElement> findSetterFor(Element field) {
        return methodsIn(field.getEnclosingElement().getEnclosedElements())
                .stream()
                .filter(method -> method.getSimpleName().toString().equals("set" + capitalize(field.getSimpleName().toString())))
                .findFirst();
    }
}
