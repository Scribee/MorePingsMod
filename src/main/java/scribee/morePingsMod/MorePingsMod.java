package scribee.morePingsMod;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class MorePingsMod {

	public ArrayList keywords = new ArrayList();
	public ResourceLocation loc = new ResourceLocation("mp:ding");
	public PositionedSoundRecord ding = PositionedSoundRecord.create(loc);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		keywords.add("scribbee");
		keywords.add("scribbe");
		keywords.add("sccribee");
		keywords.add("scrivee");
		keywords.add("scibee");
		keywords.add("scribe");
		keywords.add("scrib");
		keywords.add("scri");
		keywords.add("scrribee");
		keywords.add("halper");
		keywords.add(" helper");
		keywords.add("helpr");
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}

	@SubscribeEvent
	public void onChatEvent(ClientChatReceivedEvent event) {
		String message = event.message.getFormattedText(); //used for keeping the formatting for the final message
		String text = event.message.getUnformattedText().toLowerCase(); //used for checking actual message content
		int startInd = message.indexOf(": "); //index of the beginning of the message content in the formatted string, this should always be the first colon after the player's name
		//make sure there is a colon in the message
		if (startInd >= -1) {
			//make the content of the formatted message lowercase as a 'fix' for a bug with splitting the message
			message = message.substring(0, startInd) + message.substring(startInd, message.length()).toLowerCase();
			for (int i = 0; i < keywords.size(); i++) { //loop through keywords and check if any appear in the content part of the
				//message (don't want to be pinged every time Di*scri*minate chats), also make sure that the message isn't a pm
				if (text.substring(text.indexOf(": "), text.length()).contains(keywords.get(i).toString()) && !(text.substring(0, 2).equals("to") || text.substring(0, 4).equals("from"))) {
					String[] splitMessage = message.split(keywords.get(i).toString()); //removes keyword from message and separates into 2 strings
					//splitMessage.length will only be 1 if there was nothing to the right of the keyword, and
					//if the name is at the end of the message, we don't need to concatenate splitMessage[1] or the original message color
					if (splitMessage.length == 1) {
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(splitMessage[0] + EnumChatFormatting.YELLOW + keywords.get(i).toString())); //sends new, formatted message
						event.setCanceled(true); //keeps original message from sending
						Minecraft.getMinecraft().getSoundHandler().playSound(ding);
					}
					//check if player is a non, as the color code 7 is used before the colon to make the chat gray (reset rest of message to gray)
					else if (message.substring(startInd - 1, startInd).equals("7")) { 
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(splitMessage[0] + EnumChatFormatting.YELLOW + keywords.get(i).toString() + EnumChatFormatting.GRAY + splitMessage[1])); //sends new, formatted message
						event.setCanceled(true); //keeps original message from sending
						Minecraft.getMinecraft().getSoundHandler().playSound(ding);
					}
					//check if player is a donator, as the color code f is used before the colon to make the chat white (reset rest of message to white)
					else if (message.substring(startInd - 1, startInd).equals("f")) { 
						Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(splitMessage[0] + EnumChatFormatting.YELLOW + keywords.get(i).toString() + EnumChatFormatting.WHITE + splitMessage[1])); //sends new, formatted message
						event.setCanceled(true); //keeps original message from sending
						Minecraft.getMinecraft().getSoundHandler().playSound(ding);
					}
				break; //no need to keep searching for keywords in this message
				}
			}
		}
	}
}
