package scribee.morePingsMod.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import scribee.morePingsMod.ConfigHandler;
import scribee.morePingsMod.Reference;

public class MorePingsConfigGui extends GuiConfig {
	public MorePingsConfigGui(GuiScreen parentScreen) {
		super(parentScreen,
				new ConfigElement(ConfigHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
				Reference.MODID,
				false,
				false,
				GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
	}
}
