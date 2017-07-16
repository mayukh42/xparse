package org.mayukh.xparse.parser;

import org.mayukh.xparse.dom.InlineXml;
import org.mayukh.xparse.dom.RegularXml;
import org.mayukh.xparse.dom.XmlElement;
import org.mayukh.xparse.dom.XmlFormatException;

import java.util.*;

/**
 * Created by mayukh42 on 6/13/2017.
 *
 * XML Parser: Checks for a well-formed XML from a given stream of chars, in O(n) time, using a state machine.
 * TODO: Schema Validation
 */
public class Parser {

    /** preprocess(String)
     * Remove comments and newlines using a binary state machine
     */
    public static String preprocess(String raw) {
        StringBuilder processed = new StringBuilder();
        Tag state = null;
        int length = raw.length();

        for (int i = 0; i < length; ) {
            char c = raw.charAt(i);
            if (c == '<' && i <= length-4 && raw.charAt(i+1) == '!' &&
                    raw.charAt(i+2) == '-' && raw.charAt(i+3) == '-') {
                // enter COMMENT state
                state = Tag.COMMENT;
                i += 4;
            }
            else if (c == '-' && i <= length-3 && raw.charAt(i+1) == '-' && raw.charAt(i+2) == '>' &&
                    Objects.equals(state, Tag.COMMENT)) {
                // exit COMMENT state
                state = null;
                i += 3;
            }
            else if (state == null && c != '\r' && c != '\n' && c != '\t') {
                processed.append(c);
                i++;
            }
            else i++;
        }

        return processed.toString();
    }

    /**
     * tags
     *  <a />
     *  <a>.*</a>
     *  Avoid regex
     *
     * Also handles embedded comments within the substring from which tag is created, sneaky chaps who slip through
     * the parser's COMMENT mode. So the input string possibly contains comments.
     */
    private static XmlTag parseTag(String tag) {
        if (tag.charAt(0) == '<' && tag.charAt(tag.length()-1) == '>') {
            if (tag.length() < 3) return null;

            if (tag.charAt(tag.length()-2) == '/') {    // inline tag
                String[] tokens = getTokens(tag.substring(1, tag.length()-2));
                String tagName = null;
                if (tokens[0].length() > 0) tagName = tokens[0].trim();
                return new StartTag(tagName, getAttributesMap(tokens));
            }
            else if (tag.charAt(1) == '/') {    // end tag
                return new EndTag(tag.substring(2, tag.length()-1));
            }
            else {      // start tag
                String[] tokens = getTokens(tag.substring(1, tag.length()-1));
                String tagName = null;
                if (tokens[0].length() > 0) tagName = tokens[0].trim();
                return new StartTag(tagName, getAttributesMap(tokens));
            }
        }
        return new ValueTag(tag);  // if not surrounded by <>, it must be a string literal
    }

    /**
     * convert what is between < and > or /> to name, key=value... array
     */
    private static String[] getTokens(String tag) {
        String[] unquoted = tag.split("\"");
        int numTokens = 1 + unquoted.length/2;    // integer division
        String[] tokens = new String[numTokens];
        String[] first = unquoted[0].split(" ");
        tokens[0] = first[0];   // a well-formed xml tag must have at least one token

        if (numTokens > 1) {
            tokens[1] = first[1] + unquoted[1];
            for (int i = 2; i < unquoted.length-1; i = i + 2)
                tokens[1 + i/2] = unquoted[i] + unquoted[i+1];
        }

        return tokens;
    }

    /**
     * get key, value pairs from attribute tokens in start tag
     */
    private static Map<String, String> getAttributesMap(String[] tokens) {
        Map<String, String> attrMap = new HashMap<>();
        for (int i = 1; i < tokens.length && tokens[i].length() >= 5; i++) {
            String[] kv = tokens[i].split("=");
            attrMap.put(kv[0].trim(), unquote(kv[1]).trim());
        }
        return attrMap;
    }

    /**
     * unquote a string
     */
    private static String unquote(String quotedStr) {
        String quoted = quotedStr.trim();
        if (quoted.length() < 2 || quoted.charAt(0) != '"' || quoted.charAt(quoted.length()-1) != '"') return quoted;
        return quoted.substring(1, quoted.length()-1);
    }

    /**
     * C-style state machine to parse XML documents into a DOM. Returns the root element.
     * DOM stack: Push only when start tag is parsed, pop only when end tag is parsed.
     */
    public static XmlElement parse(String xmlData) {
        String data = preprocess(xmlData);
        /** Stacks: mode (tag 'state' of the state machine)
         * startPos (for parsing range of the stream)
         *      required for O(n) pass of the data
         * domTree (to get parent)
         */
        Deque<Tag> mode = new LinkedList<>();
        Deque<XmlElement> domTree = new LinkedList<>();
        Deque<Integer> startPos = new LinkedList<>();

        XmlElement root = null;
        for (int i = 0; i < data.length(); ) {
            char c = data.charAt(i);
            if (c == '<' && mode.isEmpty()) {
                // first start tag
                startPos.push(i);
                mode.push(Tag.START);
                i++;
            }
            else if (c == '<' && data.charAt(i+1) == '/' && mode.peek().equals(Tag.OPEN)) {
                // end tag
                startPos.push(i);
                mode.push(Tag.END);
                i += 2;
            }
            else if (c == '<' && mode.peek().equals(Tag.OPEN)) {
                // nested tag start
                startPos.push(i);
                mode.push(Tag.START);
                i++;
            }
            else if (c == '>' && mode.peek().equals(Tag.START)) {
                // end of start tag
                i++;
                mode.pop();
                StartTag startTag;
                try {
                    startTag = (StartTag) parseTag(data.substring(startPos.pop(), i).trim());
                } catch (ClassCastException e) {
                    throw new XmlFormatException("end tag created without start tag");
                }
                mode.push(Tag.OPEN);

                RegularXml openXml = new RegularXml(startTag);
                domTree.push(openXml);
                if (root == null) root = openXml;
            }
            else if (c == '/' && data.charAt(i+1) == '>' && mode.peek().equals(Tag.START)) {
                // inline end
                mode.pop();
                i += 2;
                StartTag inlineTag = (StartTag) parseTag(data.substring(startPos.pop(), i).trim());

                // enough data to create inline xml element and add to parent
                XmlElement element = new InlineXml(inlineTag);
                if (root == null) root = element;   // tree has only this element
                else {
                    RegularXml parent = (RegularXml) domTree.peek();  // inline XML can only be a child of parent
                    parent.addChild(element);
                }
            }
            else if (c == '>' && mode.peek().equals(Tag.END)) {
                i++;
                mode.pop();  // pop END
                mode.pop();  // pop OPEN which was pushed at the end of start tag
                EndTag endTag = (EndTag) parseTag(data.substring(startPos.pop(), i).trim());

                RegularXml current = (RegularXml) domTree.pop();
                if (!current.getStartTag().getName().equals(endTag.getName()))
                    throw new XmlFormatException("Start and End tags do not match!");
                current.setEndTag(endTag);

                if (domTree.peek() != null) {
                    RegularXml parent = (RegularXml) domTree.peek();  // add to children list of parent if exists
                    parent.addChild(current);
                }
            }
            else if (Objects.equals(Tag.OPEN, mode.peek()) && isStringLiteral(c)) {
                /* String between start and end tags. mode.isEmpty is checked for corner case of a literal input
                    An allowed char literal can start a string literal when mode is OPEN.
                 */
                startPos.push(i);
                i++;
                mode.push(Tag.VALUE);
            }
            else if (Objects.equals(Tag.VALUE, mode.peek()) && (isStringLiteral(c) || isWhiteSpace(c))) {
                // white space is allowed in literal values
                i++;
            }
            else if (c == '<' && data.charAt(i+1) == '/' && mode.peek().equals(Tag.VALUE)) {
                // end tag after string literal
                mode.pop();
                ValueTag valueTag = (ValueTag) parseTag(data.substring(startPos.pop(), i).trim());
                startPos.push(i);
                mode.push(Tag.END);
                i += 2;

                RegularXml parent = (RegularXml) domTree.peek();
                parent.setValue(valueTag.getName());
            }
            else i++;
        }

        if (!startPos.isEmpty() || !mode.isEmpty() || !domTree.isEmpty())
            throw new XmlFormatException("XML is not well-formed; it has orphan children/ elements.");
        else if (root == null)
            throw new XmlFormatException("A root node could not be created.");

        return root;
    }

    /**
     * Allowed chars in string literals
     *  [0-9A-Za-z+*.-_]
     */
    private static boolean isStringLiteral(char c) {
        return  isTagLiteral(c) ||
                (c == '+' || c == '*' || c == '.' || c == '-'); // separators and wildcards
    }

    private static boolean isTagLiteral(char c) {
        return  (c >= '0' && c <= '9') ||   // numbers
                (c >= 'A' && c <= 'Z') ||   // upper case
                (c >= 'a' && c <= 'z') ||   // lower case
                (c == '_');
    }

    private static boolean isWhiteSpace(char c) {
        return c == ' ';
    }
}
