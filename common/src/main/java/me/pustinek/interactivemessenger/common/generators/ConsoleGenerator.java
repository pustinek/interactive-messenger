package me.pustinek.interactivemessenger.common.generators;

import me.pustinek.interactivemessenger.common.message.InteractiveMessage;
import me.pustinek.interactivemessenger.common.message.InteractiveMessagePart;
import me.pustinek.interactivemessenger.common.message.TextMessagePart;
import me.pustinek.interactivemessenger.common.message.enums.Color;
import me.pustinek.interactivemessenger.common.message.enums.Format;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;

public class ConsoleGenerator {
	private static final Pattern REPLACE_ALL_RGB_PATTERN = Pattern.compile("(&)?&#([0-9a-fA-F]{6})");

	/**
	 * @throws NumberFormatException If the provided hex color code is invalid or if version is lower than 1.16.
	 */
	public static String parseHexColor(String hexColor) throws NumberFormatException {

		if (hexColor.startsWith("#")) {
			hexColor = hexColor.substring(1); //fuck you im reassigning this.
		}
		if (hexColor.length() != 6) {
			throw new NumberFormatException("Invalid hex length");
		}
		java.awt.Color.decode("#" + hexColor);
		StringBuilder assembledColorCode = new StringBuilder();
		assembledColorCode.append("\u00a7x");
		for (char curChar : hexColor.toCharArray()) {
			assembledColorCode.append("\u00a7").append(curChar);
		}
		return assembledColorCode.toString();
	}


	/**
	 * Map Color to the native formatting codes used in chat (https://minecraft.gamepedia.com/Formatting_codes)
	 */
	private static final EnumMap<Color, Character> colorCode = new EnumMap<Color, Character>(Color.class) {{
		put(Color.BLACK, '0');
		put(Color.DARK_BLUE, '1');
		put(Color.DARK_GREEN, '2');
		put(Color.DARK_AQUA, '3');
		put(Color.DARK_RED, '4');
		put(Color.DARK_PURPLE, '5');
		put(Color.GOLD, '6');
		put(Color.GRAY, '7');
		put(Color.DARK_GRAY, '8');
		put(Color.BLUE, '9');
		put(Color.GREEN, 'a');
		put(Color.AQUA, 'b');
		put(Color.RED, 'c');
		put(Color.LIGHT_PURPLE, 'd');
		put(Color.YELLOW, 'e');
		put(Color.WHITE, 'f');
	}};

	/**
	 * Map Format to the native formatting codes used in chat (https://minecraft.gamepedia.com/Formatting_codes)
	 */
	private static final EnumMap<Format, Character> formatCode = new EnumMap<Format, Character>(Format.class) {{
		put(Format.BOLD, 'l');
		put(Format.ITALIC, 'o');
		put(Format.UNDERLINE, 'n');
		put(Format.STRIKETHROUGH, 's');
		put(Format.OBFUSCATE, 'k');
		put(Format.RESET, 'r');
	}};

	private static final char COLOR_CHAR = '§';

	/**
	 * Parses the given message to a String containing control characters
	 * for formatting that can be used for console outputs, but also for normal player
	 * messages.
	 * <p>
	 * The returned message will only contain colors, bold, italic, underlining and 'magic'
	 * characters. Hovers and other advanced tellraw enums will be skipped.
	 * @param message The parsed InteractiveMessage
	 * @return Plain message that can be send
	 */
	public static String generate(InteractiveMessage message) {
		StringBuilder result = new StringBuilder();
		Color activeColor = Color.WHITE;

		Set<Format> activeFormatting = EnumSet.noneOf(Format.class);
		for(InteractiveMessagePart interactivePart : message) {
			for(TextMessagePart textPart : interactivePart) {
				// Use reset if there is formatting active we need to get rid of
				if(!textPart.getFormatting().containsAll(activeFormatting)) {
					result.append(COLOR_CHAR).append(formatCode.get(Format.RESET));
					activeColor = Color.WHITE;
					activeFormatting.clear();
				}

				// Color
				if(activeColor != textPart.getColor()) {

					if(textPart.getColor() == Color.HEX){
						result.append(parseHexColor(textPart.getHexColor()));
					}else{
						result.append(COLOR_CHAR).append(colorCode.get(textPart.getColor()));
					}


					activeColor = textPart.getColor();
				}

				// Formatting
				Set<Format> formattingToAdd = EnumSet.noneOf(Format.class);
				formattingToAdd.addAll(textPart.getFormatting());
				formattingToAdd.removeAll(activeFormatting);
				for(Format format : formattingToAdd) {
					result.append(COLOR_CHAR).append(formatCode.get(format));
					activeFormatting.add(format);
				}

				// Text
				result.append(textPart.getText());
			}

            // Add newlines
			if(interactivePart.hasNewline()) {
				result.append("\n");
            }
        }
		return result.toString();
	}

}
