package com.seventh7.mybatis.inspection;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.util.xml.GenericAttributeValue;
import com.seventh7.mybatis.dom.model.Select;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public class ResultTypeQuickFix extends GenericQuickFix<PsiClass> {

    private Select select;

    public ResultTypeQuickFix(@NotNull Select select, @NotNull PsiClass target) {
        super(target);
        this.select = select;
    }

    @NotNull
    @Override
    public String getName() {
        return "Correct resultType";
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        GenericAttributeValue<PsiClass> resultType = select.getResultType();
        resultType.setValue(getElement());
    }
}
