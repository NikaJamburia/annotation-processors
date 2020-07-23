package com.nika.annotations.framework.processor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"javax.persistence.Embedded", "javax.persistence.Embeddable"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JpaEmbeddedProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        checkIfNonEmbeddableElementIsEmbedded(roundEnv);
        checkIfEmbeddableElementIsEmbeddedWithoutAnnotation(roundEnv);
        return false;
    }

    private void checkIfEmbeddableElementIsEmbeddedWithoutAnnotation(RoundEnvironment roundEnv) {
        getElementsAnnotatedWith("javax.persistence.Embeddable", roundEnv)
                .forEach(embeddableElement -> checkEntitiesForEmbeddedFields(embeddableElement, roundEnv));
    }

    private void checkEntitiesForEmbeddedFields(Element embeddableElement, RoundEnvironment roundEnv) {
        getElementsAnnotatedWith("javax.persistence.Entity", roundEnv)
                .forEach(entity ->
                        getEnclosedFieldsByType(embeddableElement.asType(), entity)
                                .forEach(field -> checkFieldForEmbeddedAnnotation(field, entity))
                );
    }

    private void checkFieldForEmbeddedAnnotation(Element embeddedElement, Element entity) {
        if (embeddedElement.getAnnotation(Embedded.class) == null) {
            messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "Embeddable " + embeddedElement.getSimpleName() + " is used in entity " + entity.getSimpleName() + " without @Embedded",
                    embeddedElement
            );
        }
    }

    private List<? extends Element> getEnclosedFieldsByType(TypeMirror type, Element entity) {
        return entity.getEnclosedElements().stream()
            .filter(enclosed -> enclosed.getKind().equals(ElementKind.FIELD))
            .filter(enclosed -> enclosed.asType().equals(type))
            .collect(Collectors.toList());
    }

    private Set<? extends Element> getElementsAnnotatedWith(String annotation, RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(
                processingEnv.getElementUtils().getTypeElement(annotation)
        );
    }

    private void checkIfNonEmbeddableElementIsEmbedded(RoundEnvironment roundEnv) {
        getElementsAnnotatedWith("javax.persistence.Embedded", roundEnv)
                .forEach(this::checkMatchingEmbeddableClass);
    }

    private void checkMatchingEmbeddableClass(Element embeddedField) {
        if (embeddedField.getKind() == ElementKind.FIELD) {
            TypeMirror embeddedFieldType = embeddedField.asType();
            Element embeddedElement = processingEnv.getTypeUtils().asElement(embeddedFieldType);

            if (embeddedElement.getAnnotation(Embeddable.class) == null) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "Element " + embeddedElement.getSimpleName() + " is used here as embedded, but is not annotated with @Embeddable",
                        embeddedField);
            }
        }
        else {
            throw new IllegalArgumentException();
        }
    }
}
