package com.seventh7.mybatis.intention;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.seventh7.mybatis.service.JavaService;
import com.seventh7.mybatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
abstract class JavaFileIntentionChooser implements IntentionChooser {

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if (!(file instanceof PsiJavaFile))
            return false;
        PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
        return null != element && JavaUtils.isElementWithinInterface(element) && isAvailable(element);
    }

    public abstract boolean isAvailable(@NotNull PsiElement element);

    boolean isPositionOfMethodDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiMethod;
    }

    boolean isPositionOfInterfaceDeclaration(@NotNull PsiElement element) {
        return element.getParent() instanceof PsiClass;
    }

    boolean isTargetPresentInXml(@NotNull PsiElement element) {
        return JavaService.getInstance(element.getProject()).findWithFindFirstProcessor(element).isPresent();
    }

}
