package scribee.morePingsMod;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigHandler {

	public static Configuration config;
	
	public static boolean pingsEnabled;
	public static String keywords;
	
	public static void init(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		syncConfig();
	}
	
	public static void syncConfig() {
		System.out.println("syncing config");
	    pingsEnabled = config.getBoolean("pingsEnabled", Configuration.CATEGORY_GENERAL, true, "Whether pings are enabled");
	    keywords = config.getString("keywords", Configuration.CATEGORY_GENERAL, "keyword1,keyword2", "List of keywords");
	    config.save();
	}
}
