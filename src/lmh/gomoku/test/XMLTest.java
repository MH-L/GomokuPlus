package lmh.gomoku.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;

import lmh.gomoku.exception.XMLException;
import lmh.gomoku.model.IMove;
import lmh.gomoku.util.RecordCreator;
import lmh.gomoku.util.XMLHelper;
import lmh.gomoku.util.XMLHelper.XMLElement;

import org.junit.Before;
import org.junit.Test;

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
		assertTrue(XMLHelper.elementToString(sampleElement).equals(shouldEqual));

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
		assertTrue(XMLHelper.elementToString(gameElement).equals(multilineString));
	}

	@Test
	public void testStringToXML() throws XMLException {
		XMLElement ele = XMLHelper.strToXML(contents);
		assertEquals(ele.getName(), "Game");
		assertEquals(1, ele.getChild("Result").size());
		assertEquals(1, ele.getChild("Moves").size());
		assertEquals(1, ele.getChild("Withdrawals").size());
		assertEquals(10, ele.getChild("Moves").get(0).getChild("Round").size());
		assertEquals(3, ele.getAllChildren().size());
		XMLElement moveElement = ele.getChild("Moves").get(0).
				getAllChildren().get(0).getAllChildren().get(0);
		assertEquals(7, Integer.parseInt(moveElement.getChild("X").get(0).getContent()));
		assertEquals(6, Integer.parseInt(moveElement.getChild("Y").get(0).getContent()));
	}

	@Test
	public void testGenerateRecordFromXML() throws XMLException {
		XMLElement elem = XMLHelper.strToXML(contents);
		ArrayList<IMove> moves = RecordCreator.generateMovesFromXML(elem);
		assertEquals(moves.size(), 19);
	}

}
