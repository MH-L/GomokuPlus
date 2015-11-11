package style;

import java.awt.GraphicsEnvironment;

public class FontStyle {
	public static boolean isFontAvailable(String fontName) {
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] fonts = g.getAvailableFontFamilyNames();
		for (String font : fonts) {
			if (font.equals(fontName)) {
				return true;
			}
		}

		return false;
	}
}
