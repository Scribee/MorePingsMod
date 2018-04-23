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
		// used when testing
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

	 @SubscribeEvent(priority = EventPriority.LOW)
	 public void onChatEvent(ClientChatReceivedEvent event) {
		 if (!ConfigHandler.disableMod && onHypixel) {
			 
			 String message = event.message.getFormattedText(); // used for keeping the formatting for the final message
			 String text = event.message.getUnformattedText().toLowerCase(); // used for checking actual message content
			 
			 int startInd = message.indexOf(": "); // index of the beginning of the message content in the formatted string

			 if (startInd != -1) {
				 for (String keyword : keywordList) { 
					 // check if any keywords appear in the content part of the message (don't want to be pinged every time Di-scri-minate chats)
					 // also make sure that the message isn't a pm
					 if (text.substring(text.indexOf(": "), text.length()).contains(keyword.toString()) && !(text.substring(0, 2).equals("to") || text.substring(0, 4).equals("from"))) {						 
						 int keywordInd = message.toLowerCase().indexOf(keyword.toString());
						 // check if player is a non using color code
						 if (message.substring(startInd - 1, startInd).equals("7")) { 
							 Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message.substring(0, keywordInd) +
									 EnumChatFormatting.YELLOW + message.substring(keywordInd, keywordInd + keyword.toString().length()) +
									 EnumChatFormatting.GRAY + message.substring(keywordInd + keyword.toString().length(), message.length())));
							 event.setCanceled(true);
							 
							 if (ConfigHandler.playDing) {
								 Minecraft.getMinecraft().getSoundHandler().playSound(ding);
							 }
						 }
						 // check if player is a donator
						 else if (message.substring(startInd - 1, startInd).equals("f")) { 
							 Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message.substring(0, keywordInd) +
									 EnumChatFormatting.YELLOW + message.substring(keywordInd, keywordInd + keyword.toString().length()) +
									 EnumChatFormatting.WHITE + message.substring(keywordInd + keyword.toString().length(), message.length())));
							 event.setCanceled(true);
							 
							 if (ConfigHandler.playDing) {
								 Minecraft.getMinecraft().getSoundHandler().playSound(ding);
							 }
						 }
						 break;
					 }
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
    	if (!ConfigHandler.disableMod) {
    		checkServer();
    	}
    }
    
    public static void sendDisabledMessage(String reason) {
    	IChatComponent message = new ChatComponentText(
    			EnumChatFormatting.WHITE + "[More Pings] " + 
    			EnumChatFormatting.RED + "Mod Disabled " + 
    			EnumChatFormatting.WHITE + "(" + reason + ")"
    		);
    	Minecraft.getMinecraft().thePlayer.addChatMessage(message);
    	
    	scheduled = false;
    }
    
    public static void sendEnabledMessage(String reason) {
    	IChatComponent message = new ChatComponentText(
    			EnumChatFormatting.WHITE + "[More Pings] " + 
    			EnumChatFormatting.GREEN + "Re-enabled " + 
    			EnumChatFormatting.WHITE + "(" + reason + ")"
    		);
    	Minecraft.getMinecraft().thePlayer.addChatMessage(message);
    	
    	scheduled = false;
    }
    
    public static void updateKeywords() {
    	if (!keywordList.isEmpty()) {
    		keywordList.clear();
    	}
    	for (String keyword : ConfigHandler.keywords) {
    		keywordList.add(keyword);
    	}
    	System.out.println("Unsorted: " + keywordList);
    	Collections.sort(keywordList, new LengthComparator());
    	System.out.println("Sorted: " + keywordList);
    }
    
    public static void checkServer() {
    	if (!Minecraft.getMinecraft().isSingleplayer()) {
			if (FMLClientHandler.instance().getClient().getCurrentServerData().serverIP.contains(".hypixel.net")) {
				onHypixel = true;
			}

			// on hypixel, messages enabled, and didn't last join hypixel
			if (!scheduled && onHypixel && ConfigHandler.sendStatusMessages && !lastIP.contains(".hypixel.net")) {
				new ScheduledCode(() -> sendEnabledMessage("on hypixel"), 120);
				scheduled = true;
			}
			// not on hypixel, messages enabled, and not the same server as last joined
			else if (!scheduled && ConfigHandler.sendStatusMessages && !lastIP.equals(FMLClientHandler.instance().getClient().getCurrentServerData().serverIP)) {
				new ScheduledCode(() -> sendDisabledMessage("not on hypixel"), 120);
				scheduled = true;
			}
			
			lastIP = FMLClientHandler.instance().getClient().getCurrentServerData().serverIP;
		}
		else if (ConfigHandler.sendStatusMessages && !scheduled) {
			new ScheduledCode(() -> sendDisabledMessage("singleplayer mode"), 120);
			
			scheduled = true;
		}
    }
}
