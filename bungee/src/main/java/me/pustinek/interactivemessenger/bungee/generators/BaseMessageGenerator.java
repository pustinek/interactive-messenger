package me.pustinek.interactivemessenger.bungee.generators;

import me.pustinek.interactivemessenger.common.message.InteractiveMessage;
import me.pustinek.interactivemessenger.common.message.InteractiveMessagePart;
import me.pustinek.interactivemessenger.common.message.TextMessagePart;
import me.pustinek.interactivemessenger.common.message.enums.Click;
import me.pustinek.interactivemessenger.common.message.enums.Color;
import me.pustinek.interactivemessenger.common.message.enums.Format;
import me.pustinek.interactivemessenger.common.message.enums.Hover;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

public class BaseMessageGenerator {

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



    private static final EnumMap<Click, ClickEvent.Action> clickKey = new EnumMap<Click, ClickEvent.Action>(Click.class) {{
        put(Click.COMMAND, ClickEvent.Action.RUN_COMMAND);
        put(Click.LINK, ClickEvent.Action.OPEN_URL);
        put(Click.SUGGEST, ClickEvent.Action.SUGGEST_COMMAND);
    }};


    private static final EnumMap<Hover, HoverEvent.Action> hoverKey = new EnumMap<Hover, HoverEvent.Action>(Hover.class) {{
        put(Hover.HOVER, HoverEvent.Action.SHOW_TEXT);
    }};



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
    public static BaseComponent generate(InteractiveMessage message) {

        BaseComponent builder = new TextComponent("");

        Color activeColor = Color.WHITE;
        Set<Format> activeFormatting = EnumSet.noneOf(Format.class);
        for(InteractiveMessagePart interactivePart : message) {
            TextComponent textComponent = new TextComponent();

            for(TextMessagePart textPart : interactivePart) {
                // Use reset if there is formatting active we need to get rid of
                if(!textPart.getFormatting().containsAll(activeFormatting)) {
                    textComponent.setColor(ChatColor.RESET);
                    activeColor = Color.WHITE;
                    activeFormatting.clear();
                }
                // Color
                if(activeColor != textPart.getColor()) {
                    if(textPart.getColor() == Color.HEX){
                        textComponent.setColor(ChatColor.of(textPart.getHexColor()));
                    }else{
                        Character character = colorCode.get(textPart.getColor());
                        textComponent.setColor(ChatColor.of("&" + character));
                    }

                    activeColor = textPart.getColor();
                }

                // Formatting
                Set<Format> formattingToAdd = EnumSet.noneOf(Format.class);
                formattingToAdd.addAll(textPart.getFormatting());
                formattingToAdd.removeAll(activeFormatting);
                for(Format format : formattingToAdd) {
                    switch (format){
                        case BOLD:
                            textComponent.setBold(true);
                            break;
                            case UNDERLINE:
                            textComponent.setUnderlined(true);
                                break;
                                case OBFUSCATE:
                            textComponent.setObfuscated(true);
                            break;
                            case ITALIC:
                            textComponent.setItalic(true);
                            break;
                            case STRIKETHROUGH:
                            textComponent.setStrikethrough(true);
                            break;
                    }
                }
            }
            //TODO: create interactive part
            if(interactivePart.getOnClick() != null){

            }

            if(interactivePart.getOnHover() != null){

            }

            // Add newlines
            if(interactivePart.hasNewline()) {
                textComponent.addExtra("\n");
            }

            builder.addExtra(textComponent);
        }


        return builder;
    }


}
