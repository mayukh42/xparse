package org.mayukh.xparse.parser;

import java.util.Map;

/**
 * Created by mayukh42 on 6/13/2017.
 */
public class StartTag extends XmlTag {

    StartTag(String name, Map<String, String> attributes) {
        super(name);
        this.attributes = attributes;
    }

    private Map<String, String> attributes;

    // rolling add
    public void addAttribute(String key, String value) {
        if (key != null) attributes.put(key, value);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "<" + name + " " + attributes + " >";
    }
}
