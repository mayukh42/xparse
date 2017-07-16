package org.mayukh.xparse.parser;

import java.util.Map;

/**
 * Created by mayukh42 on 6/13/2017.
 */
public class StartTag extends XmlTag {

    public StartTag(String name) {
        super(name);
        this.attributes = null;
    }

    public StartTag(String name, Map<String, String> attributes) {
        super(name);
        this.attributes = attributes;
    }

    private Map<String, String> attributes;

    // rolling add
    public void addAttribute(String key, String value) {
        if (attributes != null && key != null) attributes.put(key, value);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('<').append(name);
        if (attributes != null) {
            builder.append(' ');
            for (String key : attributes.keySet())
                builder.append(key).append("=\"").append(attributes.get(key)).append("\" ");
            builder.replace(builder.length()-1, builder.length(), "");
        }
        builder.append('>');
        return builder.toString();
    }
}
