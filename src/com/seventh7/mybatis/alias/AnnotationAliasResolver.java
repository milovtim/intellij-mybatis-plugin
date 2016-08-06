package com.seventh7.mybatis.alias;

import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import com.seventh7.mybatis.annotation.Annotation;
import com.seventh7.mybatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

/**
 * @author yanglin
 */
public class AnnotationAliasResolver extends AliasResolver {

    private static final Function<PsiClass, AliasDesc> FUN = psiClass -> {
        java.util.Optional<String> txtOpt = JavaUtils.getAnnotationValueText_(psiClass, Annotation.ALIAS);
        if (!txtOpt.isPresent()) {
            return null;
        }
        AliasDesc ad = new AliasDesc();
        ad.setAlias(txtOpt.get());
        ad.setClazz(psiClass);
        return ad;
    };

    public AnnotationAliasResolver(Project project) {
        super(project);
    }

    public static final AnnotationAliasResolver getInstance(@NotNull Project project) {
        return project.getComponent(AnnotationAliasResolver.class);
    }

    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        Optional<PsiClass> clazz = Annotation.ALIAS.toPsiClass(project);
        if (clazz.isPresent()) {
            Collection<PsiClass> res = AnnotatedElementsSearch.searchPsiClasses(clazz.get(), GlobalSearchScope.allScope(project)).findAll();
            return Sets.newHashSet(Collections2.transform(res, FUN::apply));
        }
        return Collections.emptySet();
    }

}
