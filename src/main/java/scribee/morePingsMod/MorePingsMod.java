package scribee.morePingsMod;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import scribee.morePingsMod.command.MorePingsCommand;
import scribee.morePingsMod.config.ConfigHandler;
import scribee.morePingsMod.util.FormattingUtil;
import scribee.morePingsMod.util.MessageUtil;
import scribee.morePingsMod.util.Reference;
import scribee.morePingsMod.util.ScheduledCode;

@Mod(modid = Reference.MODID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY, clientSideOnly = true)
public class MorePingsMod {
	
	public static boolean scheduled = false; // used to make sure the disable/enable message sends only the first time WorldEvent.Load is fired
	
	private static boolean onHypixel = false;
	private static String lastIP = "none";
	private static String mmName = "";
	
	private static List<String> keywordList = new ArrayList<String>();
	
	private static Pattern mmNamePattern = Pattern.compile("as " + String.valueOf('\u00a7') + "r" + String.valueOf('\u00a7') + "a(\\w{3,10}) ");
	
	private static ResourceLocation location = new ResourceLocation("mp:ding");
	private static ISound ding = PositionedSoundRecord.create(location);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.init(event);
		ConfigHandler.syncConfig();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new MorePingsCommand());

		updateKeywords();
	}
	
	 @SubscribeEvent
	 public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		 if (event.modID.equals(Reference.MODID)) {
			 ConfigHandler.syncConfig(); 
			 updateKeywords();
		 }
	 }

	 @SubscribeEvent
	 public void onChatEvent(ClientChatReceivedEvent event) {
		 if (!ConfigHandler.disableMod && onHypixel) {
			 String message = event.message.getFormattedText();

			 if (message.contains(":")) {
				 Pattern keywordPattern;

				 for (String keyword : keywordList) {
					 if (ConfigHandler.caseSensitive) {
						 keywordPattern = Pattern.compile(".+?" + String.valueOf('\u00a7') + "(7|f): .*(" + String.valueOf('\u00a7') + "7|" + String.valueOf('\u00a7') + "r|\\b)(" + keyword + "\\b)");
					 }
					 else {
						 keywordPattern = Pattern.compile(".+?" + String.valueOf('\u00a7') + "(7|f): .*(" + String.valueOf('\u00a7') + "7|" + String.valueOf('\u00a7') + "r|\\b)(" + keyword + "\\b)", Pattern.CASE_INSENSITIVE);
					 }
						 
					 Matcher keywordMatcher = keywordPattern.matcher(message);

					 /**
					  * Check if any keywords appear in the message.
					  * Also make sure that the message isn't in a pm (from someone)/party chat/guild chat, unless its enabled by the config.
					  * Messages to people are always ignored.
					  */
					 if ((!message.substring(2, 7).equalsIgnoreCase("from ") || (ConfigHandler.privateChat && (message.substring(2, 7).equalsIgnoreCase("from ")))) && (!message.substring(2, 8).equalsIgnoreCase("party>") || (ConfigHandler.partyChat && (message.substring(2, 8).equalsIgnoreCase("party>")))) && (!message.substring(2, 8).equalsIgnoreCase("guild>") || (ConfigHandler.guildChat && (message.substring(2, 8).equalsIgnoreCase("guild>")))) && !message.substring(2, 5).equalsIgnoreCase("to ") && keywordMatcher.find()) {						 
						 int keywordInd = keywordMatcher.start(3);
						 // check if player is a non, using the color code just before the colon
						 if (keywordMatcher.group(1).equals("7")) {
							 event.message = new ChatComponentText(message.substring(0, keywordInd) +
									 FormattingUtil.getFormattedKeyword(keyword, true) + 
									 message.substring(keywordInd + keyword.length()));

							 if (ConfigHandler.playDing)
								 Minecraft.getMinecraft().getSoundHandler().playSound(ding);
						 }
						 // check if player is a donator
						 else if (keywordMatcher.group(1).equals("f")) { 
							 event.message = new ChatComponentText(message.substring(0, keywordInd) +
									 FormattingUtil.getFormattedKeyword(keyword, false) + 
									 message.substring(keywordInd + keyword.length()));

							 if (ConfigHandler.playDing)
								 Minecraft.getMinecraft().getSoundHandler().playSound(ding);
						 }
						 break;
					 }
				 }
			 }
			 // check for nick set message to add nick to keywords
			 else if (message.length() > 27 && message.contains("You are now nicked as ")) {
				 ConfigHandler.config.get(ConfigHandler.CATEGORY_HIDDEN, "Current nick", "", "Automatically stores the name that the player is currently nicked as").set(message.substring(26, message.length() - 3));
				 ConfigHandler.syncConfig();
				 updateKeywords();
				 
				 MessageUtil.sendModMessage(EnumChatFormatting.GREEN + "Nick added to keywords", "nicked as " + FormattingUtil.getFormattedKeyword(ConfigHandler.nick, false), 2, true);
			 }
			 // check for nick reset message to remove nick from keywords
			 else if (message.length() == 31 && message.contains("Your nick has been reset!")) {
				 ConfigHandler.config.get(ConfigHandler.CATEGORY_HIDDEN, "Current nick", "", "Automatically stores the name that the player is currently nicked as").set("");
				 ConfigHandler.syncConfig();
				 updateKeywords();

				 MessageUtil.sendModMessage(EnumChatFormatting.GREEN + "Nick removed from keywords", "reset nick", 2, true);
			 }
			 else {
				 Matcher mmNameMatcher = mmNamePattern.matcher(message);
				 // check for murder mystery nickname message to add it to keywords
				 if (message.length() > 55 && mmNameMatcher.find()) {
					 mmName = mmNameMatcher.group(1);
					 updateKeywords();
					 
					 MessageUtil.sendModMessage(EnumChatFormatting.GREEN + "Nickname for this game added to keywords", "nicked as " + FormattingUtil.getFormattedKeyword(mmName, false), 2, true);
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
    	// assume that if the player joins a new world, they aren't in the same mm game anymore
    	if (!mmName.equals("")) {
    		mmName = "";
    		updateKeywords();
    		MessageUtil.sendModMessage(EnumChatFormatting.GREEN + "Nickname from last game removed from keywords", "", 2, true);
    	}
    	
    	if (!ConfigHandler.disableMod) {
    		checkServer();
    	}

    	// send welcome message if this is the first time using the mod
    	if (ConfigHandler.firstJoin) {
    		ConfigHandler.config.get(ConfigHandler.CATEGORY_HIDDEN, "Just started using mod", true, "Used to send an info message only the first time they join a world with the mod").set(false);
    		ConfigHandler.syncConfig();
    		
    		try {
    			IChatComponent welcome = new ChatComponentText(" \n" + EnumChatFormatting.BLUE + "-----" + EnumChatFormatting.AQUA + "Thanks for downloading the More Pings Mod!" + EnumChatFormatting.BLUE + "-----" + EnumChatFormatting.WHITE + "\n \nYou can change all mod settings with /morepings, or in the forge configuration GUI :D\n \n" + EnumChatFormatting.BLUE + "-----------------------------------------------\n ");
    			new ScheduledCode(() -> Minecraft.getMinecraft().thePlayer.addChatMessage(welcome), 60);
    		}
    		catch (NullPointerException e) {
        		System.out.println("Player left while message pending");
        		// reset to true to re-attempt to send the message on next join
        		ConfigHandler.config.get(ConfigHandler.CATEGORY_HIDDEN, "Just started using mod", true, "Used to send an info message only the first time they join a world with the mod").set(true);
    		}
    	}
    }
    
    /**
     * Populates keywordList using current player nick and murder mystery nickname (if they exist and are set to be used by 
     * the config), and the keywords specified in the config file
     */
    public static void updateKeywords() {
    	if (!keywordList.isEmpty()) {
    		keywordList.clear();
    	}
    	
    	for (String keyword : ConfigHandler.keywords) {
    		keywordList.add(ConfigHandler.caseSensitive ? keyword : keyword.toLowerCase());
    	}
    	
    	if (ConfigHandler.useNickAsKeyword && !ConfigHandler.nick.equals("")) {
    		keywordList.add(ConfigHandler.caseSensitive ? ConfigHandler.nick : ConfigHandler.nick.toLowerCase());
    	}
    	if (ConfigHandler.useMMNameAsKeyword && !mmName.equals("")) {
    		keywordList.add(ConfigHandler.caseSensitive ? mmName : mmName.toLowerCase());
    	}
    }
    
    /**
     * Checks whether the player is on hypixel, and sends different status messages in chat based on whether they are.
     */
    public static void checkServer() {
    	if (!Minecraft.getMinecraft().isSingleplayer()) {
			if (FMLClientHandler.instance().getClient().getCurrentServerData().serverIP.contains(".hypixel.net"))
				onHypixel = true;

			// if on hypixel, messages enabled, and didn't last join hypixel
			if (!scheduled && ConfigHandler.sendStatusMessages && !lastIP.contains(".hypixel.net") && onHypixel) {
				MessageUtil.sendEnabledMessage("on hypixel");
				
				scheduled = true;
			}
			// if not on hypixel, messages enabled, and not the same server as last joined
			else if (!scheduled && ConfigHandler.sendStatusMessages && !lastIP.equals(FMLClientHandler.instance().getClient().getCurrentServerData().serverIP)) {
				MessageUtil.sendDisabledMessage("not on hypixel");
				
				scheduled = true;
			}
			
			lastIP = FMLClientHandler.instance().getClient().getCurrentServerData().serverIP;
		}
    	// if in singleplayer mode, messages enabled, and wasn't last in singleplayer
		else if (!scheduled && ConfigHandler.sendStatusMessages && !lastIP.equals("singleplayer")) {
			MessageUtil.sendDisabledMessage("singleplayer mode");
			
			scheduled = true;
			lastIP = "singleplayer";
		}
    }
}
