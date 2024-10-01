package ru.itmo.mit.implementor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class JustImplementor implements Implementor {
    static final Logger LOGGER = Logger.getLogger(JustImplementor.class.getName());
    final String outputDirectory;

    public JustImplementor(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public String implementFromDirectory(String directoryPath, String className) throws ImplementorException {
        try {
            Class<?> clazz = loadClassFromDirectory(directoryPath, className);
            return implementClass(clazz);
        } catch (ClassNotFoundException | MalformedURLException e) {
            throw new ImplementorException("Class not found: " + className, e);
        }
    }

    @Override
    public String implementFromStandardLibrary(String className) throws ImplementorException {
        try {
            Class<?> clazz = Class.forName(className);
            return implementClass(clazz);
        } catch (ClassNotFoundException e) {
            throw new ImplementorException("Class not found: " + className, e);
        }
    }

    private Class<?> loadClassFromDirectory(String directoryPath, String className) throws ClassNotFoundException, MalformedURLException {
        File directory = new File(directoryPath);
        URL[] urls = {directory.toURI().toURL()};
        ClassLoader loader = new URLClassLoader(urls);
        return loader.loadClass(className);
    }

    private String implementClass(Class<?> clazz) throws ImplementorException {
        String packageName = clazz.getPackage() != null ? clazz.getPackage().getName() : "";
        if (packageName.startsWith("java.")) {
            packageName = ""; // Use default package for standard library classes
        }
        String className = clazz.getSimpleName() + "Impl";

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .superclass(clazz.isInterface() ? Object.class : clazz);

        if (clazz.isInterface()) {
            classBuilder.addSuperinterface(clazz);
        }

        addConstructors(clazz, classBuilder);
        addMethods(clazz, classBuilder);

        TypeSpec newClass = classBuilder.build();
        JavaFile javaFile = JavaFile.builder(packageName, newClass).build();

        try {
            File outputDir = Paths.get(outputDirectory).toFile();
            javaFile.writeTo(outputDir);
            return packageName.isEmpty() ? className : packageName + "." + className;
        } catch (IOException e) {
            throw new ImplementorException("Error writing to output directory", e);
        }
    }


    private void addConstructors(Class<?> clazz, TypeSpec.Builder classBuilder) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (java.lang.reflect.Modifier.isPrivate(constructor.getModifiers())) {
                continue;
            }
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC);

            for (Parameter parameter : constructor.getParameters()) {
                constructorBuilder.addParameter(parameter.getType(), parameter.getName());
            }

            StringBuilder superCall = new StringBuilder("super(");
            for (int i = 0; i < constructor.getParameterCount(); i++) {
                if (i > 0) {
                    superCall.append(", ");
                }
                superCall.append(constructor.getParameters()[i].getName());
            }
            superCall.append(")");

            constructorBuilder.addStatement(superCall.toString());
            classBuilder.addMethod(constructorBuilder.build());
        }
    }

    private String getMethodSignature(Method method) {
        StringBuilder signature = new StringBuilder(method.getName());
        signature.append("(");
        for (Parameter parameter : method.getParameters()) {
            signature.append(parameter.getType().getCanonicalName()).append(",");
        }
        signature.append(")");
        return signature.toString();
    }

    private String getDefaultValue(Class<?> returnType) {
        if (returnType == boolean.class) {
            return "false";
        } else if (returnType == char.class) {
            return "'\\0'";
        } else if (returnType.isPrimitive()) {
            return "0";
        } else {
            return "null";
        }
    }

    private void addMethods(Class<?> clazz, TypeSpec.Builder classBuilder) {
        Set<String> implementedMethods = new HashSet<>();
        while (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (java.lang.reflect.Modifier.isStatic(method.getModifiers()) || java.lang.reflect.Modifier.isFinal(method.getModifiers())) {
                    continue;
                }
                if (java.lang.reflect.Modifier.isPrivate(method.getModifiers())) {
                    continue;
                }

                String methodSignature = getMethodSignature(method);
                if (implementedMethods.contains(methodSignature)) {
                    continue;
                }
                implementedMethods.add(methodSignature);

                MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getName())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(method.getReturnType());

                for (Parameter parameter : method.getParameters()) {
                    methodBuilder.addParameter(parameter.getType(), parameter.getName());
                }

                for (Class<?> exceptionType : method.getExceptionTypes()) {
                    methodBuilder.addException(exceptionType);
                }

                if (method.getReturnType() != void.class) {
                    methodBuilder.addStatement("return $L", getDefaultValue(method.getReturnType()));
                }

                methodBuilder.addAnnotation(Override.class);
                classBuilder.addMethod(methodBuilder.build());
            }
            clazz = clazz.getSuperclass();
        }
    }
}


