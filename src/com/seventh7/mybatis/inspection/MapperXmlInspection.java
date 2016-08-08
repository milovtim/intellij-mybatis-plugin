package com.seventh7.mybatis.inspection;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;

/**
 * @author yanglin
 */
public class MapperXmlInspection extends BasicDomElementsInspection<DomElement> {

    public MapperXmlInspection() {
        super(DomElement.class);
    }
}
