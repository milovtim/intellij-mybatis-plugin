package com.seventh7.mybatis.definitionsearch;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiTypeParameterListOwner;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import com.seventh7.mybatis.service.JavaService;
import org.jetbrains.annotations.NotNull;


/**
 * @author yanglin
 */
public class MapperDefinitionSearch extends QueryExecutorBase<XmlElement, PsiElement> {

    public MapperDefinitionSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull PsiElement element, @NotNull final Processor<XmlElement> consumer) {
        if (!(element instanceof PsiTypeParameterListOwner)) {
            return;
        }

        JavaService.getInstance(element.getProject())
                .processPsiElement(element, domElement -> consumer.process(domElement.getXmlElement()));
    }
}
