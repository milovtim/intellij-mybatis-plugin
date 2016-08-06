package com.seventh7.mybatis.service;

import com.google.common.base.Optional;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import com.seventh7.mybatis.dom.model.IdDomElement;
import com.seventh7.mybatis.dom.model.Mapper;
import com.seventh7.mybatis.util.JavaUtils;
import com.seventh7.mybatis.util.MapperUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
@SuppressWarnings("Guava")
public class JavaService {

    private JavaPsiFacade javaPsiFacade;

    private EditorService editorService;

    public JavaService(Project project) {
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
        this.editorService = EditorService.getInstance(project);
    }

    public static JavaService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, JavaService.class);
    }

    public Optional<PsiClass> getReferenceClazzOfPsiField(@NotNull PsiElement field) {
        if (!(field instanceof PsiField)) {
            return Optional.absent();
        }
        PsiType type = ((PsiField) field).getType();
        return type instanceof PsiClassReferenceType ?
                Optional.fromNullable(((PsiClassReferenceType) type).resolve()) :
                Optional.absent();
    }

    public Optional<DomElement> findStatement(@Nullable PsiMethod method) {
        CommonProcessors.FindFirstProcessor<DomElement> processor = new CommonProcessors.FindFirstProcessor<>();
        processPsiElement(method, processor);
        return processor.isFound() ? Optional.fromNullable(processor.getFoundValue()) : Optional.absent();
    }

    private void processPsiMethod(@NotNull PsiMethod psiMethod, @NotNull Processor<IdDomElement> processor) {
        PsiClass psiClass = psiMethod.getContainingClass();
        if (null == psiClass) return;
        String id = psiClass.getQualifiedName() + "." + psiMethod.getName();
        for (Mapper mapper : MapperUtils.findMappers(psiMethod.getProject())) {
            for (IdDomElement idDomElement : mapper.getDaoElements()) {
                if (MapperUtils.getIdSignature(idDomElement).equals(id)) {
                    processor.process(idDomElement);
                }
            }
        }
    }

    public void processPsiClass(@NotNull PsiClass psiClass, @NotNull final Processor<Mapper> processor) {
        final String ns = psiClass.getQualifiedName();
        MapperUtils.findMappers(psiClass.getProject()).stream()
                .filter(mapper -> MapperUtils.getNamespace(mapper).equals(ns))
                .forEach(processor::process);
    }

    @SuppressWarnings("unchecked")
    public void processPsiElement(@NotNull PsiElement target, @NotNull Processor<? extends DomElement> processor) {
        if (target instanceof PsiMethod) {
            Processor<IdDomElement> idDomElementProcessor = (Processor<IdDomElement>) processor;
            processPsiMethod((PsiMethod) target, idDomElementProcessor);
        } else if (target instanceof PsiClass) {
            Processor<Mapper> mapperProcessor = (Processor<Mapper>) processor;
            processPsiClass((PsiClass) target, mapperProcessor);
        }
    }

    public <T extends DomElement> Optional<T> findWithFindFirstProcessor(@NotNull PsiElement target) {
        CommonProcessors.FindFirstProcessor<T> processor = new CommonProcessors.FindFirstProcessor<>();
        processPsiElement(target, processor);
        return Optional.fromNullable(processor.getFoundValue());
    }

    void importClazz(PsiJavaFile file, String clazzName) {
        if (!JavaUtils.hasImportClazz(file, clazzName)) {
            Optional<PsiClass> clazz = JavaUtils.findClazz(file.getProject(), clazzName);
            PsiImportList importList = file.getImportList();
            if (clazz.isPresent() && null != importList) {
                PsiElementFactory elementFactory = javaPsiFacade.getElementFactory();
                PsiImportStatement statement = elementFactory.createImportStatement(clazz.get());
                importList.add(statement);
                editorService.format(file, statement);
            }
        }
    }
}

