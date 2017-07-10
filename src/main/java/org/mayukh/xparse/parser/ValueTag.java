package org.mayukh.xparse.parser;

/**
 * Created by mayukh42 on 6/14/2017.
 *
 * A wrapper on String literals to simplify parsing tags
 */
public class ValueTag extends XmlTag {

    ValueTag(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
