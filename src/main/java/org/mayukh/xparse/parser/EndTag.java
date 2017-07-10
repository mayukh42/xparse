package org.mayukh.xparse.parser;

/**
 * Created by mayukh42 on 6/13/2017.
 */
public class EndTag extends XmlTag {

    public EndTag(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "</" + name + ">";
    }
}
