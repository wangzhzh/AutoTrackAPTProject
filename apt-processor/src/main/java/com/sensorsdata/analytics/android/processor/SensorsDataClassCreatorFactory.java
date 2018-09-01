package com.sensorsdata.analytics.android.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by SensorsData
 */
public class SensorsDataClassCreatorFactory {
    private String mBindingClassName;
    private String mPackageName;
    private TypeElement mTypeElement;
    private Map<Integer, VariableElement> mVariableElementMap = new HashMap<>();

    SensorsDataClassCreatorFactory(Elements elementUtils, TypeElement classElement) {
        this.mTypeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();
        String className = mTypeElement.getSimpleName().toString();
        this.mPackageName = packageName;
        this.mBindingClassName = className + "_SensorsDataViewBinding";
    }

    public void putElement(int id, VariableElement element) {
        mVariableElementMap.put(id, element);
    }

    /**
     * 创建 Java 代码
     *
     * @return String
     */
    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("/**\n" +
                " * Auto Created by SensorsData APT\n" +
                " */\n");
        builder.append("package ").append(mPackageName).append(";\n");
        builder.append('\n');
        builder.append("public class ").append(mBindingClassName);
        builder.append(" {\n");

        generateBindViewMethods(builder);
        builder.append('\n');
        builder.append("}\n");
        return builder.toString();
    }

    /**
     * 加入Method
     *
     * @param builder StringBuilder
     */
    private void generateBindViewMethods(StringBuilder builder) {
        builder.append("\tpublic void bindView(");
        builder.append(mTypeElement.getQualifiedName());
        builder.append(" owner ) {\n");
        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            String viewName = element.getSimpleName().toString();
            String viewType = element.asType().toString();
            builder.append("\t\towner.");
            builder.append(viewName);
            builder.append(" = ");
            builder.append("(");
            builder.append(viewType);
            builder.append(")(((android.app.Activity)owner).findViewById( ");
            builder.append(id);
            builder.append("));\n");
        }
        builder.append("  }\n");
    }

    /**
     * 使用 javapoet 创建 Java 代码
     * javapoet
     *
     * @return TypeSpec
     */
    public TypeSpec generateJavaCodeWithJavapoet() {
        TypeSpec bindingClass = TypeSpec.classBuilder(mBindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethodsWithJavapoet())
                .build();
        return bindingClass;

    }

    /**
     * 使用 javapoet 创建 Method
     *
     * @return MethodSpec
     */
    private MethodSpec generateMethodsWithJavapoet() {
        ClassName owner = ClassName.bestGuess(mTypeElement.getQualifiedName().toString());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bindView")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(owner, "owner");

        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            String viewName = element.getSimpleName().toString();
            String viewType = element.asType().toString();
            methodBuilder.addCode("owner." + viewName + " = " + "(" + viewType + ")(((android.app.Activity)owner).findViewById( " + id + "));");
        }
        return methodBuilder.build();
    }


    public String getPackageName() {
        return mPackageName;
    }

    public String getProxyClassFullName() {
        return mPackageName + "." + mBindingClassName;
    }

    public TypeElement getTypeElement() {
        return mTypeElement;
    }
}
