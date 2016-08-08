package com.seventh7.mybatis.inspection;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.DomElement;
import com.seventh7.mybatis.annotation.Annotation;
import com.seventh7.mybatis.dom.model.Select;
import com.seventh7.mybatis.generate.StatementGenerator;
import com.seventh7.mybatis.locator.MapperLocator;
import com.seventh7.mybatis.service.JavaService;
import com.seventh7.mybatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * @author yanglin
 * @author milovtim
 */
@SuppressWarnings("Guava")
public class MapperMethodInspection extends MapperInspection {

    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(@NotNull PsiMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (noMapperAndNoAnnotationForMethod(method))
            return EMPTY_ARRAY;

        List<ProblemDescriptor> res = Lists.newArrayListWithExpectedSize(2);

        ProblemDescriptor statementExistsProblem = checkStatementExists(method, manager, isOnTheFly);
        if (Objects.nonNull(statementExistsProblem)) {
            res.add(statementExistsProblem);
        }

        ProblemDescriptor resultTypeProblem = checkResultType(method, manager, isOnTheFly);
        if (Objects.nonNull(resultTypeProblem)) {
            res.add(resultTypeProblem);
        }

        return Iterables.toArray(res, ProblemDescriptor.class);
    }

    private boolean noMapperAndNoAnnotationForMethod(@NotNull PsiMethod method) {
        return !MapperLocator.getInstance(method.getProject()).process(method) ||
                JavaUtils.isAnyAnnotationPresent(method, Annotation.STATEMENT_SYMMETRIES);
    }

    private ProblemDescriptor checkResultType(PsiMethod method, InspectionManager manager, boolean isOnTheFly) {
        Optional<DomElement> ele = JavaService.getInstance(method.getProject()).findStatement(method);
        if (ele.isPresent()) {
            DomElement domElement = ele.get();
            if (domElement instanceof Select) {
                Select select = (Select) domElement;
                Optional<PsiClass> target = StatementGenerator.getSelectResultType(method);
                PsiClass clazz = select.getResultType().getValue();
                PsiIdentifier ide = method.getNameIdentifier();
                if (null != ide && null == select.getResultMap().getValue()) {
                    if (target.isPresent() && (null == clazz || !target.get().equals(clazz))) {
                        return manager.createProblemDescriptor(ide, "Result type not match for select id=\"#ref\"",
                                new ResultTypeQuickFix(select, target.get()), ProblemHighlightType.GENERIC_ERROR,
                                isOnTheFly);
                    } else if (!target.isPresent() && null != clazz) {
                        return manager.createProblemDescriptor(ide, "Result type not match for select id=\"#ref\"",
                                (LocalQuickFix) null, ProblemHighlightType.GENERIC_ERROR, isOnTheFly);
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private ProblemDescriptor checkStatementExists(
            PsiMethod method, InspectionManager manager, boolean isOnTheFly
    ) {
        PsiIdentifier ide = method.getNameIdentifier();
        if (!JavaService.getInstance(method.getProject()).findStatement(method).isPresent() && null != ide) {
            return manager.createProblemDescriptor(ide, "Statement with id=\"#ref\" not defined in mapper xml",
                            new StatementNotExistsQuickFix(method), ProblemHighlightType.GENERIC_ERROR, isOnTheFly);
        }
        return null;
    }

}
