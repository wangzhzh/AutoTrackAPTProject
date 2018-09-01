package com.sensorsdata.analytics.android.processor;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import com.sensorsdata.analytics.android.annotation.SensorsDataBindView;
import com.squareup.javapoet.JavaFile;

//@AutoService(Processor.class)
@SuppressWarnings("unused")
@SupportedAnnotationTypes({"com.sensorsdata.analytics.android.annotation.SensorsDataBindView"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SensorsDataBindViewProcessor extends AbstractProcessor {
    private Elements mElementUtils;
    private Map<String, SensorsDataClassCreatorFactory> mClassCreatorFactoryMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
    }

//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        HashSet<String> supportTypes = new LinkedHashSet<>();
//        supportTypes.add(SensorsDataBindView.class.getCanonicalName());
//        return supportTypes;
//    }

//    @Override
//    public SourceVersion getSupportedSourceVersion() {
//        return SourceVersion.latestSupported();
//    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        mClassCreatorFactoryMap.clear();
        //得到所有的注解
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(SensorsDataBindView.class);
        for (Element element : elements) {
            VariableElement variableElement = (VariableElement) element;
            //获取类信息
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement();
            //类的完整包名+类名
            String fullClassName = classElement.getQualifiedName().toString();
            SensorsDataClassCreatorFactory proxy = mClassCreatorFactoryMap.get(fullClassName);
            if (proxy == null) {
                proxy = new SensorsDataClassCreatorFactory(mElementUtils, classElement);
                mClassCreatorFactoryMap.put(fullClassName, proxy);
            }
            SensorsDataBindView bindAnnotation = variableElement.getAnnotation(SensorsDataBindView.class);
            //获取 View 的 id
            int id = bindAnnotation.value();
            proxy.putElement(id, variableElement);
        }
        //使用 StringBuilder 创建java文件
//        for (String key : mClassCreatorFactoryMap.keySet()) {
//            SensorsDataClassCreatorFactory proxyInfo = mClassCreatorFactoryMap.get(key);
//            try {
//                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(proxyInfo.getProxyClassFullName(), proxyInfo.getTypeElement());
//                Writer writer = jfo.openWriter();
//                writer.write(proxyInfo.generateJavaCode());
//                writer.flush();
//                writer.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        //使用 javapoet 创建java文件
        for (String key : mClassCreatorFactoryMap.keySet()) {
            SensorsDataClassCreatorFactory proxyInfo = mClassCreatorFactoryMap.get(key);
            JavaFile javaFile = JavaFile.builder(proxyInfo.getPackageName(), proxyInfo.generateJavaCodeWithJavapoet()).build();
            try {
                //　生成文件
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
