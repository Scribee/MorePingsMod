package scribee.morePingsMod.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import scribee.morePingsMod.gui.MorePingsConfigGui;
import scribee.morePingsMod.util.ScheduledCode;

public class MorePingsCommand extends CommandBase {

	@Override
	public String getCommandName() {
		return "morepings";
	}

	@Override
    public List<String> getCommandAliases() {
        List<String> aliases = new ArrayList<String>();
        aliases.add("mp");
        aliases.add("morepingsmod");
        
        return aliases;
    }
	
	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "Usage: /" + getCommandName();
	}
	
	@Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0)
			new ScheduledCode(() -> Minecraft.getMinecraft().displayGuiScreen(new MorePingsConfigGui(Minecraft.getMinecraft().currentScreen)), 1);
	}

}
