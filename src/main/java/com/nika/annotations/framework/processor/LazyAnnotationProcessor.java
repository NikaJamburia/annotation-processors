package com.nika.annotations.framework.processor;

import com.nika.annotations.framework.annotation.Lazy;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("com.nika.annotations.framework.annotation.Lazy")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class LazyAnnotationProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Collection<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(Lazy.class);
        annotatedElements.forEach(this::generateLazyClass);
        return false;
    }

    private void generateLazyClass(Element lazyClass) {
        PackageElement packageElement = (PackageElement) lazyClass.getEnclosingElement();

        String packageName = packageElement.getQualifiedName().toString();
        String serviceName = lazyClass.getSimpleName().toString();
        String methods = getMethods(lazyClass);

        Template lazyServiceTemplate = getTemplateFrom("template/lazyService.vm");
        VelocityContext lazyServiceContext = lazyServiceContext(packageName, serviceName, methods);

        try {
            JavaFileObject file = filer.createSourceFile(packageName + ".Lazy" + serviceName);
            Writer writer = file.openWriter();
            lazyServiceTemplate.merge(lazyServiceContext, writer);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getMethods(Element lazyClass) {
        StringBuilder methods = new StringBuilder();

        ElementFilter.methodsIn(lazyClass.getEnclosedElements())
            .forEach(method -> {
                if (isPublic(method)) {
                    String methodName = method.getSimpleName().toString();
                    String params = getMethodSignatureParams(method.getParameters());
                    String paramsWithoutTypes = getParamNames(method.getParameters());
                    String returnType = method.getReturnType().toString();

                    String templatePath = getMethodTemplatePath(returnType);

                    Template methodTemplate = getTemplateFrom(templatePath);
                    VelocityContext methodContext = methodContext(methodName, params, paramsWithoutTypes, returnType);

                    StringWriter result = new StringWriter();
                    methodTemplate.merge(methodContext, result);
                    methods.append(result.toString());
                    methods.append("\n");
                }
            });

        return methods.toString();
    }

    private String getMethodTemplatePath(String returnType) {
        if (returnType.equals("void")) {
            return "template/voidServiceMethod.vm";
        }
        return "template/returnServiceMethod.vm";
    }

    private boolean isPublic(ExecutableElement method) {
        return method.getModifiers().contains(Modifier.PUBLIC);
    }

    private String getParamNames(List<? extends VariableElement> parameters) {
        StringBuilder result = new StringBuilder();
        for (VariableElement parameter : parameters) {
            result.append(parameter.getSimpleName().toString());
            result.append(", ");
        }
        return StringUtils.chop(result.toString().trim());
    }

    private String getMethodSignatureParams(List<? extends VariableElement> parameters) {
        StringBuilder result = new StringBuilder();
        for (VariableElement parameter : parameters) {
            result.append(parameter.asType().toString());
            result.append(" ");
            result.append(parameter.getSimpleName().toString());
            result.append(", ");
        }
        return StringUtils.chop(result.toString().trim());
    }

    private Template getTemplateFrom(String pathToTemplate) {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();
        return velocityEngine.getTemplate(pathToTemplate);
    }

    private VelocityContext methodContext(String methodName, String params, String paramsWithoutTypes, String returnType) {
        VelocityContext context = new VelocityContext();
        context.put("methodName", methodName);
        context.put("params", params);
        context.put("paramsWithoutTypes", paramsWithoutTypes);
        context.put("returnType", returnType);
        return context;
    }

    private VelocityContext lazyServiceContext(String packageName, String serviceName, String overridenMethods) {
        VelocityContext context = new VelocityContext();
        context.put("packageName", packageName);
        context.put("serviceName", serviceName);
        context.put("overridenMethods", overridenMethods);
        return context;
    }

}
