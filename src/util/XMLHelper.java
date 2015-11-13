package util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import exceptions.XMLException;

public class XMLHelper {
	public static class XMLElement {
		private String name;
		private List<XMLElement> childElements;
		private XMLElement parentElement;
		private String content;
		public XMLElement(String name, String content) {
			this.name = name;
			this.content = content;
			childElements = new ArrayList<XMLElement>();
			parentElement = null;
		}

		public void appendChild(XMLElement child) {
			childElements.add(child);
			child.parentElement = this;
		}

		public List<XMLElement> getChild(String name) {
			List<XMLElement> retVal = new ArrayList<XMLElement>();
			for (XMLElement child : childElements) {
				if (child.name.equals(name)) {
					retVal.add(child);
				}
			}
			return retVal;
		}

		public XMLElement getParent() {
			return parentElement;
		}

		public void setContent(String c) {
			this.content = c;
		}
	}

	public XMLHelper() {}

	private static String createStartTag(String tagName) {
		return "<" + tagName + ">";
	}

	private static String createEndTag(String tagName) {
		return "</" + tagName + ">";
	}

	public static String elementToString(XMLElement ele, int baseIndent) {
		String result = "";
		for (int i = 0; i < baseIndent; i++) {
			result += "\t";
		}
		result += createStartTag(ele.name);
		if (ele.childElements.isEmpty()) {
			result += ele.content;
			result += createEndTag(ele.name);
			return result;
		}
		for (int i = 0; i < ele.childElements.size(); i++) {
			result += "\n";
			result += elementToString(ele.childElements.get(i), baseIndent + 1);
		}
		result += "\n";
		for (int i = 0; i < baseIndent; i++) {
			result += "\t";
		}
		result += createEndTag(ele.name);
		return result;
	}

	public static XMLElement strToXML(String str) throws XMLException {
		Document retVal = loadXMLFromString(str);
		Element gameElement = retVal.getDocumentElement();
		return createXMLElementFromElement(gameElement);
	}

	private static XMLElement createXMLElementFromElement(Element e) {
		NodeList gameNodes = e.getChildNodes();
		ArrayList<XMLElement> retVal = createElements(gameNodes);
		XMLElement gameEle = new XMLElement(e.getNodeName(), null);
		for (int i = 0; i < retVal.size(); i++) {
			gameEle.appendChild(retVal.get(i));
		}

		return gameEle;
	}

	private static ArrayList<XMLElement> createElements(NodeList nodes) {
		if (nodes.getLength() == 0)
			return new ArrayList<XMLElement>();
		ArrayList<XMLElement> retVal = new ArrayList<XMLElement>();
		for (int i = 0; i < nodes.getLength(); i++) {
			retVal.add(createXMLElementFromElement((Element) nodes.item(i)));
		}

		return retVal;
	}

	private static Document loadXMLFromString(String xml) throws XMLException {
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new XMLException("XML document mal-formed.");
		}
	    InputSource is = new InputSource(new StringReader(xml));
	    try {
			return builder.parse(is);
		} catch (SAXException | IOException e) {
			throw new XMLException("XML document mal-formed.");
		}
	}

	private static boolean whitespaceOnly(String s) {
		for (int i = 0; i < s.length(); i++) {
			char cur = s.charAt(i);
			if (cur != ' ' && cur != '\n' && cur != '\r') {
				return false;
			}
		}
		return true;
	}
}
