package scribee.morePingsMod;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {

	public static Configuration config;
	
	public static boolean useNickAsKeyword;
	public static boolean caseSensitive;
	public static boolean sendStatusMessages;
	public static boolean disableMod;
	public static boolean playDing;
	public static boolean partyChat;
	public static boolean guildChat;
	public static boolean privateChat;
	public static String[] keywords;
	public static String pingColor;
	public static String pingStyle;
	public static String nick;
	
	private static String[] validColors;
	private static String[] validStyles;

	private static boolean disabledLast = false;

	public static void init(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		validColors = new String[] {
				"None",
				EnumChatFormatting.BLACK + "Black", 
				EnumChatFormatting.DARK_BLUE + "Dark Blue", 
				EnumChatFormatting.DARK_GREEN + "Dark Green", 
				EnumChatFormatting.DARK_AQUA + "Dark Aqua", 
				EnumChatFormatting.DARK_RED + "Dark Red", 
				EnumChatFormatting.DARK_PURPLE + "Dark Purple", 
				EnumChatFormatting.GOLD + "Gold", 
				EnumChatFormatting.GRAY + "Gray", 
				EnumChatFormatting.DARK_GRAY + "Dark Gray", 
				EnumChatFormatting.BLUE + "Blue", 
				EnumChatFormatting.GREEN + "Green", 
				EnumChatFormatting.AQUA + "Aqua", 
				EnumChatFormatting.RED + "Red", 
				EnumChatFormatting.LIGHT_PURPLE + "Light Purple",
				EnumChatFormatting.YELLOW + "Yellow", 
				EnumChatFormatting.WHITE + "White"
		};
		validStyles = new String[] {
				"None",
				EnumChatFormatting.BOLD + "Bold",
				EnumChatFormatting.UNDERLINE + "Underline", 
				EnumChatFormatting.ITALIC + "Italic"
		};
		
		syncConfig();
	}
	
	public static void syncConfig() {
	    disableMod = config.getBoolean("Disable mod",
	    		Configuration.CATEGORY_GENERAL,
	    		false,
	    		"Whether the mod is disabled or not");
	    caseSensitive = config.getBoolean("Make keywords case sensitive",
	    		Configuration.CATEGORY_GENERAL,
	    		false,
	    		"Whether keywords should be pinged for only if they match the exact casing of the set keyword");
	    keywords = config.getStringList("Keywords", 
	    		Configuration.CATEGORY_GENERAL,
	    		new String[] { "" },
	    		"List of keywords");
	    useNickAsKeyword = config.getBoolean("Add nick to keywords",
	    		Configuration.CATEGORY_GENERAL,
	    		true,
	    		"If true, when you're nicked your nickname will be used as a keyword");
	    nick = config.getString("Current nick",
	    		"Hidden", // won't be displayed in the config gui
	    		"",
	    		"Automatically stores the name that the player is currently nicked as"
	    		);
	    
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
	    
	    pingColor = config.getString("Ping color", 
	    		Configuration.CATEGORY_GENERAL, 
	    		"Yellow", 
	    		"What color to make keywords", 
	    		validColors);
	    pingStyle = config.getString("Ping styling", 
	    		Configuration.CATEGORY_GENERAL, 
	    		"None", 
	    		"Styling to apply to keywords", 
	    		validStyles);
	    
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