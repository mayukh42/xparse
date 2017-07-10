package org.mayukh.xparse.parser;

import org.mayukh.xparse.dom.XmlElement;
import org.mayukh.xparse.dom.XmlFormatException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by mayukh42 on 6/13/2017.
 *
 * Tests for Xparse: Java XML Parser
 */
public class ParserTest {

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    @Before
    public void insertLine() {
        System.out.println();
    }

    @Test
    public void parseSimple() {
        String data = "<bean>MyBean</bean>";
        XmlElement xml = Parser.parse(data);
        System.out.println("Parsed XML: \n" + xml);
    }

    @Test
    public void parseInline() {
        String data = "<setter-param name=\"name\" type=\"java.lang.String\" value=\"Polo\" />";
        XmlElement xml = Parser.parse(data);
        System.out.println("Parsed XML: \n" + xml);
    }

    @Test
    public void parseNested() {
        String data = "<servlet>\n" +
                "\t<servlet-name>DispatchServlet</servlet-name>\n" +
                "\t<url-pattern>*.do</url-pattern>\n" +
                "</servlet>";
        XmlElement xml = Parser.parse(data);
        System.out.println("Parsed XML: \n" + xml);
    }

    @Test
    public void parseModerate() {
        String data = "<bean name=\"myBattery\" type=\"org.mayukh.javaee.model.Battery\">\n" +
                "\t<setter-param name=\"name\" type=\"java.lang.String\" value=\"Amaron\" />\n" +
                "\t<setter-param name=\"chargeLeft\" type=\"java.lang.Double\" value=\"40.0\" />\n" +
                "\t<testValue>TestValue</testValue>\n" +
                "</bean>";
        XmlElement xml = Parser.parse(data);
        System.out.println("Parsed XML: \n" + xml);
    }

    @Test(expected = XmlFormatException.class)
    public void onlyStartTag() {
        String data = "<beans>";
        XmlElement xml = Parser.parse(data);
        System.out.println("Parsed XML: \n" + xml);
    }

    @Test
    public void onlyEndTag() {
        String data = "</beans>";

        thrownException.expect(XmlFormatException.class);
        thrownException.expectMessage("end tag created without start tag");
        XmlElement xml = Parser.parse(data);
        System.out.println("Parsed XML: \n" + xml);
    }

    @Test
    public void noRoot() {
        String data = "the quick brown fox jumps over the lazy dog";

        thrownException.expect(XmlFormatException.class);
        thrownException.expectMessage("root node could not be created");
        XmlElement xml = Parser.parse(data);
        System.out.println("Parsed XML: \n" + xml);
    }

    @Test
    public void parseCommentSimple() {
        String data = "<servlet>\n" +
                "\t<servlet-name>Dispatch a Servlet</servlet-name>\n" +
                "\t<!-- this is a comment -->\n" +
                "\t<url-pattern>*.do</url-pattern>\n" +
                "</servlet>";
        XmlElement xml = Parser.parse(data);
        System.out.println("Parsed XML: \n" + xml);
    }

    @Test
    public void parseCommentEmbedded() {
        String data = "<servlet>\n" +
                "\t<servlet-name>Dispatch<!-- this is a comment -->Serv<!-- take <!-- that -->let</servlet-name>\n" +
                "\t<url-pattern>*.do</url-pattern>\n" +
                "</servlet>";
        XmlElement xml = Parser.parse(data);
        System.out.println("Parsed XML: \n" + xml);
    }

    @Test
    public void preprocess() {
        String data = "<servlet>\n" +
                "\t<servlet-name>Dispatch<!-- this is a comment -->Serv<!-- another -->let</servlet-name>\n" +
                "\t<url-pattern>*.do</url-pattern>\n" +
                "\t<!-- this is a comment -->\n" +
                "</servlet>";
        String processed = Parser.preprocess(data);
        System.out.println("Preprocessed data: " + processed);
    }

    @Test
    public void parseXmlFile() {
        String location = "src/test/resources/xml/";
        Path filePath = Paths.get(location, "beans-config.xml");
        byte[] fileContentsInBytes = null;
        try {
            fileContentsInBytes = Files.readAllBytes(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String data = new String(fileContentsInBytes);
        XmlElement xml = Parser.parse(data);
        System.out.println("Parsed XML: \n" + xml);
    }
}
