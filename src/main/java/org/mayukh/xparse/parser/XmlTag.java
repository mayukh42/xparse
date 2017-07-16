package org.mayukh.xparse.parser;

/**
 * Created by mayukh42 on 6/13/2017.
 *
 * Parent type for all types of XML tags: start-tag, end-tag, inline-tag
 * All tags have a name
 */
public abstract class XmlTag {

    protected String name;

    public XmlTag(String name) {
        this.name = name;
    }

    @Override
    public abstract String toString();

    public String getName() {
        return name;
    }
}
