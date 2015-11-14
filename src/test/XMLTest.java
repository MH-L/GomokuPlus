package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.imageio.stream.FileImageOutputStream;

import org.junit.Before;
import org.junit.Test;

import exceptions.XMLException;
import util.XMLHelper;
import util.XMLHelper.XMLElement;

public class XMLTest {
	private String contents;

	@Before
	public void initialize() throws URISyntaxException {
		File sampleFile = new File(getClass().getResource("/xml/sample.xml").toURI());
		try {
			byte[] allContentsBytes = Files.readAllBytes(sampleFile.toPath());
			contents = new String(allContentsBytes, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testXMLElementToString() {
		// First, a simple string.
		XMLElement sampleElement = new XMLElement("Testing", "For Test Purpose");
		String shouldEqual = "<Testing>For Test Purpose</Testing>";
		assertTrue(XMLHelper.elementToString(sampleElement, 0).equals(shouldEqual));

		// Then, a string with nested tags.
		String multilineString = String.format(
				"%s\n\t%s\n\t\t%s\n\t\t\t%s\n\t\t\t%s\n\t\t%s\n\t%s\n%s",
				"<Game>", "<Round>", "<Move>", "<x>6</x>",
				"<y>7</y>", "</Move>", "</Round>", "</Game>");
		XMLElement gameElement = new XMLElement("Game", null);
		XMLElement roundElement = new XMLElement("Round", null);
		XMLElement moveElement = new XMLElement("Move", null);
		XMLElement xElement = new XMLElement("x", "6");
		XMLElement yElement = new XMLElement("y", "7");
		gameElement.appendChild(roundElement);
		roundElement.appendChild(moveElement);
		moveElement.appendChild(xElement);
		moveElement.appendChild(yElement);
		assertTrue(XMLHelper.elementToString(gameElement, 0).equals(multilineString));
	}

	@Test
	public void testStringToXML() throws XMLException {
		System.out.println(contents);
		XMLElement ele = XMLHelper.strToXML(contents);
		assertEquals(ele.getName(), "Game");
	}

}
