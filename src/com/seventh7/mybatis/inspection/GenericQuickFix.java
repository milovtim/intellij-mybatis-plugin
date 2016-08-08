package com.seventh7.mybatis.inspection;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public abstract class GenericQuickFix<T extends PsiElement> implements LocalQuickFix {

    private final SmartPsiElementPointer<T> methodPointer;

    protected GenericQuickFix(T element) {
        this.methodPointer = SmartPointerManager.getInstance(element.getProject())
                .createSmartPsiElementPointer(element);
    }

    protected T getElement() {
        return methodPointer.getElement();
    }


    @NotNull
    @Override
    public String getFamilyName() {
        return getName();
    }

}
