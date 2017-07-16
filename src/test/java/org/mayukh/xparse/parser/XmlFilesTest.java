package org.mayukh.xparse.parser;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mayukh.xparse.dom.XmlElement;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by mayukh42 on 6/20/2017.
 *
 * Random xml files picked up from the web, which are well-formed. Note that HTML is not supported, since it can
 *  have <p>This is a <b>paragraph</b> text</p>, i.e. nested xml element within a string literal.
 *
 * Redirects stdout and stderr to remove clutter from console and keep output more organized.
 */
public class XmlFilesTest {

    private static PrintStream ps = null;
    private static OutputStream out = null;

    @BeforeClass
    public static void setup() {
        try {
            Path outputPath = Paths.get("target/", "parser-output.txt");
            out = Files.newOutputStream(outputPath);
            ps = new PrintStream(out);  // no buffered service
            System.setOut(ps);
            System.setErr(ps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void cleanup() {
        try {
            if (out != null) out.close();
            if (ps != null) ps.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final String location = "src/test/resources/xml/";

    private void parseXmlFile(String file) {
        Path filePath = Paths.get(location, file);
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

    @Test
    public void parseBooks() {
        parseXmlFile("books.xml");
    }

    @Test
    public void parseGarments() {
        parseXmlFile("garments.xml");
    }

    @Test
    public void parseBeans() {
        parseXmlFile("beans-config.xml");
    }
}
