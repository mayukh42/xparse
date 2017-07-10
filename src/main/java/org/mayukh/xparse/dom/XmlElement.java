package org.mayukh.xparse.dom;

import org.mayukh.xparse.parser.StartTag;

/**
 * Created by mayukh42 on 6/13/2017.
 *
 * Parent type for all XML element types
 * XML := inline | simple | nested
 *
 * inline   := start-tag (auto-closed)
 * simple   := start-tag :: value :: end-tag
 * nested   := start-tag :: XML* :: end-tag
 */
public abstract class XmlElement {

    /* All Xml Elements must have a start tag */
    final StartTag startTag;

    XmlElement(StartTag startTag) {
        this.startTag = startTag;
    }

    public StartTag getStartTag() {
        return startTag;
    }
}
