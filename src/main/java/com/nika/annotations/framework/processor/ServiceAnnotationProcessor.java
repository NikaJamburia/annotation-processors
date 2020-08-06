package com.nika.annotations.framework.processor;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("com.nika.annotations.framework.annotation.Service")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ServiceAnnotationProcessor extends AbstractProcessor {
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> services = getServices(roundEnvironment);
        printToFile(services);
        return false;
    }

    private void printToFile(Set<? extends Element> services) {
        try {
            FileObject file = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "resources/service-list.txt");
            Writer writer = file.openWriter();
            services.forEach(service -> {
                try {
                    writer.write(service.asType().toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<? extends Element> getServices(RoundEnvironment roundEnvironment) {
        return roundEnvironment.getElementsAnnotatedWith(
                processingEnv.getElementUtils().getTypeElement("com.nika.annotations.framework.annotation.Service")
        ).stream().filter(element -> ((Element) element).getKind().isClass()).collect(Collectors.toSet());
    }
}
