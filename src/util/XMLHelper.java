package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import exceptions.XMLException;

public class XMLHelper {
	public class XMLElement {
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
		Stack<String> openedTags = new Stack<String>();
		char[] array = str.toCharArray();
		char prev = ' ';
		String nameBuffer = "";
		String contentBuffer = "";
		boolean nameStarted = false;
		boolean contentStarted = false;
		boolean isOpening = false;
		XMLElement retVal = null;
		XMLElement curElement = null;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == '<') {
				// start of tag (no matter whether or not it is an opening tag)
				if (nameStarted == true) {
					// Cannot have "<" inside an XML tag.
					throw new XMLException("Invalid XML tag.");
				} else if (contentBuffer.length() > 0) {
					// as the exception suggests, if there is already some contents,
					// then the nested tag is not valid.
					if (i == array.length || array[i+1] != '/') {
						throw new XMLException("Contents inside XML element with nested XML elements.");
					}
				}
				nameStarted = true;
				contentStarted = false;
				isOpening = true;
			} else if (array[i] == '>') {
				if (!nameStarted) {
					throw new XMLException("Closing tag inside content.");
				}
				nameStarted = false;
				if (isOpening) {
					openedTags.add(nameBuffer);
					nameBuffer = "";
					contentStarted = true;
				} else {
					String tag = openedTags.pop();
					if (!nameBuffer.equals(tag)) {
						throw new XMLException("No closing tags match some opened tags.");
					}
					nameBuffer = "";
					contentStarted = false;
				}
			} else if (array[i] == '/') {
				if (prev == '<') {
					isOpening = false;
				} else if (nameStarted) {
					throw new XMLException("\"/\" Symbol in XML tags.");
				} else if (contentStarted) {
					contentBuffer += '/';
				} else {
					throw new XMLException("Content outside tag");
				}
			} else if (array[i] == ' ' ||
					array[i] == '\n' || array[i] == '\r') {
				if (nameStarted) {
					throw new XMLException("Space characters inside name tag.");
				}
			} else {
				if (nameStarted) {
					nameBuffer += array[i];
				} else if (contentStarted) {
					contentBuffer += array[i];
				} else {
					throw new XMLException("Content outside tag");
				}
			}
			prev = array[i];
		}

		if (!openedTags.empty()) {
			throw new XMLException("Malformed XML document.");
		}
		return retVal;
	}
}
