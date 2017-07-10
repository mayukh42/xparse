package org.mayukh.xparse.dom;

import org.mayukh.xparse.parser.StartTag;

/**
 * Created by mayukh42 on 6/13/2017.
 *
 * Inline XML cannot be a parent. A parent must have a start and end tags, while inline only has a start tag
 */
public class InlineXml extends XmlElement {

    public InlineXml(StartTag startTag) {
        super(startTag);
    }

    @Override
    public String toString() {
        String inlineStr = startTag.toString();
        return inlineStr + " (inline)" ;
    }
}
