package com.seventh7.mybatis.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.seventh7.mybatis.generate.StatementGenerator;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public class StatementNotExistsQuickFix extends GenericQuickFix<PsiMethod> {

    public StatementNotExistsQuickFix(@NotNull PsiMethod method) {
        super(method);
    }

    @NotNull
    @Override
    public String getName() {
        return "Generate statement";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        StatementGenerator.applyGenerate(getElement());
    }
}
