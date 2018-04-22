package scribee.morePingsMod;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {

	public static Configuration config;
	
	public static boolean sendStatusMessages;
	public static boolean disableMod;
	public static String keywords;
	
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
	    keywords = config.getString("Keywords", 
	    		Configuration.CATEGORY_GENERAL,
	    		"keyword1,keyword2",
	    		"List of keywords");
	    sendStatusMessages = config.getBoolean("Mod status messages in chat",
	    		Configuration.CATEGORY_GENERAL,
	    		true,
	    		"Whether to send a message in chat when the mod is disabled or enabled");
	    
	    config.save();
	    
	    if (disableMod && !disabledLast) {
	    	if (Minecraft.getMinecraft().thePlayer != null) { // will be null if config is changed from title screen
	    		MorePingsMod.sendDisabledMessage("by config");
	    	}
    		
	    	disabledLast =  true;
	    }
	    else if (!disableMod && disabledLast) {
	    	if (Minecraft.getMinecraft().thePlayer != null) { // will be null if config is changed from title screen
	    		MorePingsMod.sendEnabledMessage("by config");
	    	}
	    	
	    	disabledLast = false;
	    }
	}
}
