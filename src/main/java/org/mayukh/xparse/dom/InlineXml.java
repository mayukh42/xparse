package org.mayukh.xparse.dom;

import org.mayukh.xparse.parser.StartTag;

import java.util.Map;

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
        StringBuilder builder = new StringBuilder();
        builder.append(startTag.toString());
        builder.replace(builder.length()-1, builder.length(), " />");
        return builder.toString();
    }
}
