package util;

import java.util.ArrayList;
import java.util.List;

public class XMLHelper {
	private class XMLElement {
		private String name;
		private List<XMLElement> childElements;
		private String content;
		private XMLElement(String name, String content) {
			this.name = name;
			this.content = content;
			childElements = new ArrayList<XMLElement>();
		}

		private void appendChild(XMLElement child) {
			childElements.add(child);
		}
	}

	public static String createStartTag(String tagName) {
		return "<" + tagName + ">";
	}

	public static String createEndTag(String tagName) {
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
		result += createEndTag(ele.name);
		return result;
	}
}