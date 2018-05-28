package scribee.morePingsMod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import scribee.morePingsMod.MorePingsMod;

public class MessageUtil {	
	/**
     * Used to send all mod status messages in chat
     * 
     * @param content Main content of the message
     * @param extraInfo Any information to be included in parentheses after content
     * @param delay Number of ticks to wait before sending message (set to 0 to send the message immediately)
     * @param addPrefix Whether or not to prefix the message with "[More Pings] "
     */
    public static void sendModMessage(String content, String extraInfo, int delay, boolean addPrefix) {
    	IChatComponent message = addPrefix ? new ChatComponentText(EnumChatFormatting.WHITE + "[More Pings] ") : new ChatComponentText("");
    	message.appendText(content);
    	
    	if (!extraInfo.equals(""))
    		message.appendText(" (" + extraInfo + EnumChatFormatting.WHITE + ")");

    	try {
    		if (delay > 0) {
    			new ScheduledCode(() -> sendModMessage(message.getFormattedText(), "", 0, false), delay);
    		}
    		else if (delay == 0) {
    			Minecraft.getMinecraft().thePlayer.addChatMessage(message);
    		}
    	}
    	catch (NullPointerException e) {
    		System.out.println("Player left while message pending");
    	}
    }

    /**
     * Sends a message to the player stating the mod is disabled
     * 
     * @param reason Text to put in parentheses after message
     */
    public static void sendDisabledMessage(String reason) {
    	String message = EnumChatFormatting.RED + "Mod Disabled" + EnumChatFormatting.WHITE;
    	
    	sendModMessage(message, reason, 100, true);
    	
    	if (MorePingsMod.scheduled) {
    		MorePingsMod.scheduled = false;
    	}
    }
    
    /**
     * Sends a message to the player stating the mod is enabled
     * 
     * @param reason Text to put in parentheses after message
     */
    public static void sendEnabledMessage(String reason) {
    	String message = EnumChatFormatting.GREEN + "Re-enabled" + EnumChatFormatting.WHITE;
    	
    	sendModMessage(message, reason, 100, true);
    	
    	if (MorePingsMod.scheduled) {
    		MorePingsMod.scheduled = false;
    	}
    }
}
