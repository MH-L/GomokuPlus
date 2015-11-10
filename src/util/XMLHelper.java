package util;

import java.util.ArrayList;
import java.util.List;

public class XMLHelper {
	public class XMLElement {
		private String name;
		private List<XMLElement> childElements;
		private String content;
		public XMLElement(String name, String content) {
			this.name = name;
			this.content = content;
			childElements = new ArrayList<XMLElement>();
		}

		public void appendChild(XMLElement child) {
			childElements.add(child);
		}

		public XMLElement getChild(String name) {
			for (XMLElement child : childElements) {
				if (child.name.equals(name)) {
					return child;
				}
			}
			return null;
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

	public static XMLElement strToXML(String str) {
		char[] array = str.toCharArray();
		for (int i = 0; i < array.length; i++) {

		}
		return null;
	}
}
