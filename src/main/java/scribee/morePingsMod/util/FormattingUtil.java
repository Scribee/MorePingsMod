package scribee.morePingsMod.util;

import net.minecraft.util.EnumChatFormatting;
import scribee.morePingsMod.config.ConfigHandler;

public class FormattingUtil {
	
	private static String[] rainbowColors = new String[] {
			EnumChatFormatting.RED + "Red",
			EnumChatFormatting.GOLD + "Gold",
			EnumChatFormatting.YELLOW + "Yellow",
			EnumChatFormatting.GREEN + "Green",
			EnumChatFormatting.AQUA + "Aqua",
			EnumChatFormatting.BLUE + "Blue",
			EnumChatFormatting.LIGHT_PURPLE + "Pink"
	};
	
	/**
     * Used to format keywords with color and style specified in the config file
     * 
     * @param keyword Keyword to add styling and color to
     * @param isNon If the player is a non
     * @return keyword with color and styling
     */
    public static String getFormattedKeyword(String ping, boolean isNon) {
    	if (EnumChatFormatting.getTextWithoutFormattingCodes(ConfigHandler.pingColor).equals("Rainbow")) {
    		ping = formatRainbow(ping);
    	}
    	else {
    		ping = ConfigHandler.pingStyle.equals("None") ? ping : ConfigHandler.pingStyle.substring(0, 2) + ping;
    		ping = ConfigHandler.pingColor.equals("None") ? ping : ConfigHandler.pingColor.substring(0, 2) + ping;
    	}
    	
    	ping += EnumChatFormatting.RESET;
    	ping += isNon ? EnumChatFormatting.GRAY : EnumChatFormatting.WHITE; // set the rest of the message to match the color it should be

    	return ping;
    }
    
    /**
     * Colors each character in a string a different color of the rainbow
     * 
     * @param keyword String to be colored
     * @return keyword with rainbow coloring
     */
    public static String formatRainbow(String keyword) {
		String newKeyword = "";
		// Making it not actually random allows pingColor to save properly. As it was before, pingColor defaulted back to None every time Minecraft was reopened.
		int pos = keyword.length() % rainbowColors.length;
		
		for (int i = 0; i < keyword.length(); i++) {
			newKeyword += rainbowColors[pos % rainbowColors.length].substring(0, 2);
			pos++;
			newKeyword += keyword.substring(i, i + 1);
		}
		
		return newKeyword;
	}
}
