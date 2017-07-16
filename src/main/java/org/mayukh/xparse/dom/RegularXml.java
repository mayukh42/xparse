package org.mayukh.xparse.dom;

import org.mayukh.xparse.parser.EndTag;
import org.mayukh.xparse.parser.StartTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayukh42 on 6/13/2017.
 *
 * Unified type for xml with start and end tags
 */
public class RegularXml extends XmlElement {

    private EndTag endTag;
    private List<XmlElement> children;
    private String value;

    /* Incomplete XML required by Parser */
    public RegularXml(StartTag startTag) {
        super(startTag);
    }

    /* Nested XML */
    public RegularXml(StartTag startTag, EndTag endTag, List<XmlElement> children) {
        super(startTag);
        this.endTag = endTag;
        this.children = children;
    }

    /* Simple XML */
    public RegularXml(StartTag startTag, EndTag endTag, String value) {
        super(startTag);
        this.endTag = endTag;
        this.value = value;
    }

    public void setEndTag(EndTag endTag) {
        this.endTag = endTag;
    }

    public void setValue(String value) {
        if (children != null) throw new XmlFormatException("Parent XML cannot have both value and children");
        this.value = value;
    }

    public boolean addChild(XmlElement child) {
        if (value != null) throw new XmlFormatException("Parent XML cannot have both children and value");
        if (children == null) children = new ArrayList<>();
        return children.add(child);
    }

    public EndTag getEndTag() {
        return endTag;
    }

    public List<XmlElement> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(startTag.toString());
        if (children == null) {
            // string literal
            builder.append(value != null ? value : "");
        }
        else {
            // nested XML
            for (XmlElement child : children)
                builder.append(child.toString());
        }
        builder.append(endTag.toString());
        return builder.toString();
    }
}
