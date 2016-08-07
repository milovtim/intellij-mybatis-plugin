package com.seventh7.mybatis.dom;

import com.intellij.psi.PsiClass;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.seventh7.mybatis.dom.model.Association;
import com.seventh7.mybatis.dom.model.Collection;
import com.seventh7.mybatis.dom.model.ParameterMap;
import com.seventh7.mybatis.dom.model.ResultMap;
import org.jetbrains.annotations.NotNull;

/**
 * @author yanglin
 */
public final class MapperBacktrackingUtils {

    private MapperBacktrackingUtils() {
        throw new UnsupportedOperationException();
    }

    public static PsiClass getPropertyClazz(XmlAttributeValue attributeValue) {
        DomElement domElement = DomUtil.getDomElement(attributeValue);
        if (null == domElement) {
            return null;
        }

        Collection collection = DomUtil.getParentOfType(domElement, Collection.class, true);
        if (null != collection && !isWithinSameTag(collection, attributeValue)) {
            return collection.getOfType().getValue();
        }

        Association association = DomUtil.getParentOfType(domElement, Association.class, true);
        if (null != association && !isWithinSameTag(association, attributeValue)) {
            return association.getJavaType().getValue();
        }

        ParameterMap parameterMap = DomUtil.getParentOfType(domElement, ParameterMap.class, true);
        if (null != parameterMap && !isWithinSameTag(parameterMap, attributeValue)) {
            return parameterMap.getType().getValue();
        }

        ResultMap resultMap = DomUtil.getParentOfType(domElement, ResultMap.class, true);
        if (null != resultMap && !isWithinSameTag(resultMap, attributeValue)) {
            return resultMap.getType().getValue();
        }
        return null;
    }

    private static boolean isWithinSameTag(@NotNull DomElement domElement, @NotNull XmlElement xmlElement) {
        XmlTag xmlTag = PsiTreeUtil.getParentOfType(xmlElement, XmlTag.class);
        return domElement.getXmlTag().equals(xmlTag);
    }

}
