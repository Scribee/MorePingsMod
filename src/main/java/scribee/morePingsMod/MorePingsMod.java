package scribee.morePingsMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = Reference.MODID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY)
public class MorePingsMod {
	
	private String[] keywordList;
	private ResourceLocation loc = new ResourceLocation("mp:ding");
	private PositionedSoundRecord ding = PositionedSoundRecord.create(loc);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigHandler.init(event);
		ConfigHandler.syncConfig();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	 @SubscribeEvent
	 public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		 if (eventArgs.modID.equals(Reference.MODID)) {
			 ConfigHandler.syncConfig();
			 if (!ConfigHandler.keywords.equals("keyword1,keyword2")) { //make sure its not the default value, dont want to be pinged for "keyword1" in case it shows up
				 keywordList = new String[ConfigHandler.keywords.split(",").length];
				 for (int i = 0; i < ConfigHandler.keywords.split(",").length; i++) {
					 keywordList[i] = ConfigHandler.keywords.split(",")[i];
				 }
			 }
		 }
	 }

	@SubscribeEvent
	public void onChatEvent(ClientChatReceivedEvent event) {
		if (ConfigHandler.pingsEnabled) {
			String message = event.message.getFormattedText(); //used for keeping the formatting for the final message
			String text = event.message.getUnformattedText().toLowerCase(); //used for checking actual message content
			int startInd = message.indexOf(": "); //index of the beginning of the message content in the formatted string, this should always be the first colon after the player's name
			//make sure there is a colon in the message
			if (startInd != -1) {
				for (int i = 0; i < keywordList.length; i++) { //loop through keywords and check if any appear in the content part of the message (don't want to be pinged every time Di*scri*minate chats),
					//also make sure that the message isn't a pm
					if (text.substring(text.indexOf(": "), text.length()).contains(keywordList[i].toString()) && !(text.substring(0, 2).equals("to") || text.substring(0, 4).equals("from"))) {
						int keywordInd = message.toLowerCase().indexOf(keywordList[i].toString());
					
						//check if player is a non, as the color code 7 is used before the colon to make the chat gray (reset rest of message to gray)
						if (message.substring(startInd - 1, startInd).equals("7")) { 
							Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message.substring(0, keywordInd) + EnumChatFormatting.YELLOW + message.substring(keywordInd, keywordInd + keywordList[i].toString().length()) + EnumChatFormatting.GRAY + message.substring(keywordInd + keywordList[i].toString().length(), message.length()))); //sends new, formatted message
							event.setCanceled(true); //keeps original message from sending
							Minecraft.getMinecraft().getSoundHandler().playSound(ding);
						}
						//check if player is a donator, as the color code f is used before the colon to make the chat white (reset rest of message to white)
						else if (message.substring(startInd - 1, startInd).equals("f")) { 
							Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message.substring(0, keywordInd) + EnumChatFormatting.YELLOW + message.substring(keywordInd, keywordInd + keywordList[i].toString().length()) + EnumChatFormatting.WHITE + message.substring(keywordInd + keywordList[i].toString().length(), message.length()))); //sends new, formatted message
							event.setCanceled(true); //keeps original message from sending
							Minecraft.getMinecraft().getSoundHandler().playSound(ding);
						}
					break; //no need to keep searching for keywords in this message
					}
				}
			}
		}
		else {
			System.out.println("disabled by config");
		}
	}
}
