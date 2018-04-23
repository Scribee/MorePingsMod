package scribee.morePingsMod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {

	public static Configuration config;
	
	public static boolean sendStatusMessages;
	public static boolean disableMod;
	public static boolean playDing;
	public static boolean partyChat;
	public static boolean guildChat;
	public static boolean privateChat;
	public static String[] keywords;
	
	private static boolean disabledLast = false;
	
	public static void init(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		
		syncConfig();
	}
	
	public static void syncConfig() {
	    disableMod = config.getBoolean("Disable mod",
	    		Configuration.CATEGORY_GENERAL,
	    		false,
	    		"Whether the mod is disabled or not");
	    keywords = config.getStringList("Keywords", 
	    		Configuration.CATEGORY_GENERAL,
	    		new String[] { "" },
	    		"List of keywords");
	    sendStatusMessages = config.getBoolean("Send mod status in chat",
	    		Configuration.CATEGORY_GENERAL,
	    		true,
	    		"Whether to send a message in chat when the mod is disabled or enabled");
	    playDing = config.getBoolean("Play ding sound for keywords", 
	    		Configuration.CATEGORY_GENERAL, 
	    		true, 
	    		"Whether to play a ding sound when a keyword is found");
	    
	    partyChat = config.getBoolean("Ping for party chat", 
	    		Configuration.CATEGORY_GENERAL, 
	    		true, 
	    		"Whether to ping for keywords in party chat");
	    guildChat = config.getBoolean("Ping for guild chat", 
	    		Configuration.CATEGORY_GENERAL, 
	    		true, 
	    		"Whether to ping for keywords in guild chat");
	    privateChat = config.getBoolean("Ping for private messages", 
	    		Configuration.CATEGORY_GENERAL, 
	    		false, 
	    		"Whether to ping for keywords in pms");
	    
	    config.save();
	    
	    if (disableMod && !disabledLast) {
	    	if (Minecraft.getMinecraft().thePlayer != null && ConfigHandler.sendStatusMessages) { // will be null if config is changed from title screen
	    		MorePingsMod.sendDisabledMessage("by config");
	    	}
    		
	    	disabledLast =  true;
	    }
	    else if (!disableMod && disabledLast) {
	    	if (Minecraft.getMinecraft().thePlayer != null && ConfigHandler.sendStatusMessages) { // will be null if config is changed from title screen
	    		MorePingsMod.sendEnabledMessage("by config");
	    		MorePingsMod.checkServer(); // if re-enabled after joining a server, onHypixel hasn't been updated, so update it now
	    	}
	    	
	    	disabledLast = false;
	    }
	}
}
