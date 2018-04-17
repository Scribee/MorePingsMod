package scribee.morePingsMod;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiMorePingsConfig extends GuiConfig {
	public GuiMorePingsConfig(GuiScreen parentScreen) {
	    super(parentScreen,
	    	new ConfigElement(ConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
	    	Reference.MODID,
	    	false,
	    	false,
	    	GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
	}
}
