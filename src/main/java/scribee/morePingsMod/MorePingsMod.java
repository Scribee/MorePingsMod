package scribee.morePingsMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Mod(modid = Reference.MODID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY)
public class MorePingsMod {
	
	// used to make sure the disable/enable message sends only the first time WorldEvent.Load is fired
	private static boolean scheduled = false;
	private static boolean onHypixel = false;
	private static String lastIP = "none";
	
	private static List<String> keywordList = new ArrayList<String>();
	
	private static ResourceLocation location = new ResourceLocation("mp:ding");
	private static PositionedSoundRecord ding = PositionedSoundRecord.create(location);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// used when testing to reset config each time
		//event.getSuggestedConfigurationFile().delete();
		
		ConfigHandler.init(event);
		ConfigHandler.syncConfig();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		
		updateKeywords();
	}
	
	 @SubscribeEvent
	 public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		 if (event.modID.equals(Reference.MODID)) {
			 ConfigHandler.syncConfig();
			 
			 updateKeywords();
		 }
	 }

	 // TODO switch over to using regex
	 @SubscribeEvent(priority = EventPriority.HIGH) // high priority improves compatibility with some other chat mods (since the change to how the formatted message is sent)
	 public void onChatEvent(ClientChatReceivedEvent event) {
		 // used to allow testing on singleplayer
		 //onHypixel = true;
		 if (!ConfigHandler.disableMod && onHypixel) {

			 String message = event.message.getFormattedText(); // used for keeping the formatting for the final message
			 String text = event.message.getUnformattedText().toLowerCase(); // used for checking actual message content

			 int startInd = message.indexOf(": "); // index of the beginning of the message content in the formatted string

			 // if there is a colon in the message
			 if (startInd != -1) {
				 for (String keyword : keywordList) { 
					 String messageToCheck = ConfigHandler.caseSensitive ? message.substring(message.indexOf(": ")) : message.substring(message.indexOf(": ")).toLowerCase();
					 /** 
					  * Check if any keywords appear in the content part of the message (don't want to be pinged every time Di-scri-minate chats).
					  * Also make sure that the message isn't in a pm(from someone)/party chat/guild chat unless its enabled by the config.
					  * Messages to people are always ignored.
					  */
					 if (messageToCheck.contains(keyword) && (!text.substring(0, 5).equals("from ") || (ConfigHandler.privateChat && (text.substring(0, 5).equals("from ")))) && (!text.substring(0, 6).equals("party>") || (ConfigHandler.partyChat && (text.substring(0, 6).equals("party>")))) && (!text.substring(0, 6).equals("guild>") || (ConfigHandler.guildChat && (text.substring(0, 6).equals("guild>")))) && !text.substring(0, 3).equals("to ")) {						 
						 int keywordInd = message.toLowerCase().substring(message.indexOf(": ")).indexOf(keyword) + message.substring(0, message.indexOf(": ")).length();
						 // check if player is a non using the color code just before the colon
						 if (message.substring(startInd - 1, startInd).equals("7")) { 
							 event.message = new ChatComponentText(message.substring(0, keywordInd) +
									 getFormattedKeyword(keyword, true) + 
									 message.substring(keywordInd + keyword.length()));

							 if (ConfigHandler.playDing)
								 Minecraft.getMinecraft().getSoundHandler().playSound(ding);
						 }
						 // check if player is a donator
						 else if (message.substring(startInd - 1, startInd).equals("f")) { 
							 event.message = new ChatComponentText(message.substring(0, keywordInd) +
									 getFormattedKeyword(keyword, false) + 
									 message.substring(keywordInd + keyword.length()));

							 if (ConfigHandler.playDing)
								 Minecraft.getMinecraft().getSoundHandler().playSound(ding);
						 }
						 break;
					 }
				 }
			 }
			 else if (text.length() > 24) {
				 // check for nick set message to add nick as a keyword
				 if (message.contains("You are now nicked as ")) {
					 ConfigHandler.config.get(ConfigHandler.CATEGORY_HIDDEN, "Current nick", "", "Automatically stores the name that the player is currently nicked as").set(text.substring(22, text.length() - 1));
					 ConfigHandler.syncConfig();
					 updateKeywords();
					 
					 Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
							 EnumChatFormatting.WHITE + "[More Pings] " + 
									 EnumChatFormatting.GREEN + "Nick added to keywords " + 
									 EnumChatFormatting.WHITE + "(nicked as " + 
									 EnumChatFormatting.ITALIC + ConfigHandler.nick + 
									 EnumChatFormatting.RESET + ")")
							 );
				 }
				 else if (message.contains("Your nick has been reset!")) {
					 ConfigHandler.config.get(ConfigHandler.CATEGORY_HIDDEN, "Current nick", "", "Automatically stores the name that the player is currently nicked as").set("");
					 ConfigHandler.syncConfig();
					 updateKeywords();
					 
					 Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(
							 EnumChatFormatting.WHITE + "[More Pings] " + 
									 EnumChatFormatting.RED + "Nick removed from keywords " + 
									 EnumChatFormatting.WHITE + "(unnicked)")
							 );
				 }
			 }
		 }
	 }

    @SubscribeEvent
    public void onPlayerLeaveEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
       onHypixel = false;
    }
    
    @SubscribeEvent
    public void onWorldJoinEvent(WorldEvent.Load event) {
    	if (!ConfigHandler.disableMod)
    		checkServer();
    }
    
    // TODO make a single, more generalized, message sender
    // include catch for npe if player left during delay
    
    /**
     * Sends a message to the player stating the mod is disabled
     * @param reason Text to put in parentheses after message
     */
    public static void sendDisabledMessage(String reason) {
    	IChatComponent message = new ChatComponentText(reason.equals("") ?
    			EnumChatFormatting.WHITE + "[More Pings] " + 
    			EnumChatFormatting.RED + "Mod Disabled " : EnumChatFormatting.WHITE + "[More Pings] " + 
    			EnumChatFormatting.RED + "Mod Disabled " + 
    			EnumChatFormatting.WHITE + "(" + reason + ")"
    		);
    	Minecraft.getMinecraft().thePlayer.addChatMessage(message);
    	
    	scheduled = false;
    }
    
    /**
     * Sends a message to the player stating the mod is enabled
     * @param reason Text to put in parentheses after message
     */
    public static void sendEnabledMessage(String reason) {
    	IChatComponent message = new ChatComponentText(reason.equals("") ?
    			EnumChatFormatting.WHITE + "[More Pings] " + 
    			EnumChatFormatting.GREEN + "Re-enabled" : EnumChatFormatting.WHITE + "[More Pings] " + 
    			EnumChatFormatting.GREEN + "Re-enabled " + 
    			EnumChatFormatting.WHITE + "(" + reason + ")" 
    		);
    	Minecraft.getMinecraft().thePlayer.addChatMessage(message);
    	
    	scheduled = false;
    }
    
    /**
     * Populates keywordList using current player nick (if exists) and keywords specified in the config file
     */
    public static void updateKeywords() {
    	if (!keywordList.isEmpty())
    		keywordList.clear();
    	
    	for (String keyword : ConfigHandler.keywords) {
    		keywordList.add(ConfigHandler.caseSensitive ? keyword : keyword.toLowerCase());
    	}
    	
    	if (ConfigHandler.useNickAsKeyword && !ConfigHandler.nick.equals(""))
    		keywordList.add(ConfigHandler.caseSensitive ? ConfigHandler.nick : ConfigHandler.nick.toLowerCase());
    	
    	Collections.sort(keywordList, new LengthComparator());
    }
    
    /**
     * Checks whether the player is on hypixel, and sends mod status messages in chat
     * based on whether they are.
     */
    public static void checkServer() {
    	if (!Minecraft.getMinecraft().isSingleplayer()) {
			if (FMLClientHandler.instance().getClient().getCurrentServerData().serverIP.contains(".hypixel.net"))
				onHypixel = true;

			// on hypixel, messages enabled, and didn't last join hypixel
			if (!scheduled && onHypixel && ConfigHandler.sendStatusMessages && !lastIP.contains(".hypixel.net")) {
				new ScheduledCode(() -> sendEnabledMessage("on hypixel"), 100);
				
				scheduled = true;
			}
			// not on hypixel, messages enabled, and not the same server as last joined
			else if (!scheduled && ConfigHandler.sendStatusMessages && !lastIP.equals(FMLClientHandler.instance().getClient().getCurrentServerData().serverIP)) {
				new ScheduledCode(() -> sendDisabledMessage("not on hypixel"), 100);
				
				scheduled = true;
			}
			
			lastIP = FMLClientHandler.instance().getClient().getCurrentServerData().serverIP;
		}
		else if (ConfigHandler.sendStatusMessages && !scheduled) {
			new ScheduledCode(() -> sendDisabledMessage("singleplayer mode"), 100);
			
			scheduled = true;
		}
    }
    
    /**
     * Used to highlight keywords however the user has selected in the config file
     * @param keyword Keyword to add styling and color to
     * @param isNon If the player is a non
     * @return keyword with color and styling
     */
    public static String getFormattedKeyword(String keyword, boolean isNon) {
    	String ping = keyword + EnumChatFormatting.RESET;
    	
    	ping += isNon ? EnumChatFormatting.GRAY : EnumChatFormatting.WHITE;

    	ping = ConfigHandler.pingStyle.equals("None") ? ping : ConfigHandler.pingStyle.substring(0, 2) + ping;
    	ping = ConfigHandler.pingColor.equals("None") ? ping : ConfigHandler.pingColor.substring(0, 2) + ping;

    	return ping;
    }
}
