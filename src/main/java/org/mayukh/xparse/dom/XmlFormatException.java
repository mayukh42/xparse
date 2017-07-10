package org.mayukh.xparse.dom;

/**
 * Created by mayukh42 on 6/14/2017.
 */
public class XmlFormatException extends RuntimeException {

    public XmlFormatException(String message) {
        super("[Invalid XML] " + message);
    }
}
