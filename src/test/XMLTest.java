package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import util.XMLHelper;
import util.XMLHelper.XMLElement;

public class XMLTest {
	private XMLHelper helper;

	@Before
	public void initialize() {
		helper = new XMLHelper();
	}

	@Test
	public void testXMLElementToString() {
		// First, a simple string.
		XMLElement sampleElement = helper.new XMLElement("Testing", "For Test Purpose");
		String shouldEqual = "<Testing>For Test Purpose</Testing>";
		assertTrue(XMLHelper.elementToString(sampleElement, 0).equals(shouldEqual));

		// Then, a string with nested tags.
		String multilineString = String.format(
				"%s\n\t%s\n\t\t%s\n\t\t\t%s\n\t\t\t%s\n\t\t%s\n\t%s\n%s",
				"<Game>", "<Round>", "<Move>", "<x>6</x>",
				"<y>7</y>", "</Move>", "</Round>", "</Game>");
		XMLElement gameElement = helper.new XMLElement("Game", null);
		XMLElement roundElement = helper.new XMLElement("Round", null);
		XMLElement moveElement = helper.new XMLElement("Move", null);
		XMLElement xElement = helper.new XMLElement("x", "6");
		XMLElement yElement = helper.new XMLElement("y", "7");
		gameElement.appendChild(roundElement);
		roundElement.appendChild(moveElement);
		moveElement.appendChild(xElement);
		moveElement.appendChild(yElement);
		assertTrue(XMLHelper.elementToString(gameElement, 0).equals(multilineString));
	}

	@Test
	public void testStringToXML() {
		// Method not implemented yet.
	}

}
