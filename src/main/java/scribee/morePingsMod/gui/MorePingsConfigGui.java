package scribee.morePingsMod.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import scribee.morePingsMod.config.ConfigHandler;
import scribee.morePingsMod.util.Reference;

public class MorePingsConfigGui extends GuiConfig {
	public MorePingsConfigGui(GuiScreen parentScreen) {
		super(parentScreen,
				getConfigElements(),
				Reference.MODID,
				false,
				false,
				GuiConfig.getAbridgedConfigPath(ConfigHandler.config.toString()));
	}
	
	private static List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> categories = new ArrayList<IConfigElement>();
		// probably not the best practice to use the forge example DummyCategoryElements, but I'm lazy and it works perfectly XD
		categories.add(new DummyCategoryElement(ConfigHandler.CATEGORY_GENERAL, "gui.config.general", GeneralSettings.class));
		categories.add(new DummyCategoryElement(ConfigHandler.CATEGORY_KEYWORDS, "gui.config.keywords", KeywordSettings.class));
		categories.add(new DummyCategoryElement(ConfigHandler.CATEGORY_PREFERENCES, "gui.config.preferences", Preferences.class));

		return categories;
	}
}
