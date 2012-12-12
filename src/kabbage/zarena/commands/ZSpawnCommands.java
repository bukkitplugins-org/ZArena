package kabbage.zarena.commands;

import kabbage.zarena.commands.utils.ArgumentCountException;
import kabbage.zarena.commands.utils.ECommand;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ZSpawnCommands implements CommandExecutor {
	public ZSpawnCommands()
	{
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		ECommand command = new ECommand(commandLabel, args);
		CommandHandler handler = new CommandHandler(sender, command);

		String helpMessage = "";
		boolean softFailure = false; //If true, the string helpMessage is sent to sender. return true.
		boolean hardFailure = false; //If true, return false. (sender gets sent the usage)
		try
		{
			switch(command.get(1).toLowerCase())
			{
			case "set":
				helpMessage = "/zspawn set <spawner-name>";
				handler.setZSpawn(command.get(2));
				break;
			case "remove":
				helpMessage = "/zspawn remove <spawner-name | %nearest%>";
				handler.removeZSpawn(command.get(2));
				break;
			case "jump":
				helpMessage = "/zspawn jump <spawner-name | %nearest%>";
				handler.jumpToZSpawn(command.get(2));
				break;
			case "list":
				handler.listZSpawns();
				break;
			case "markboss":
				helpMessage = "/zspawn markboss <spawner-name>";
				handler.markBossSpawn(command.get(2));
				break;
			default:
				hardFailure = true;
			}
		} catch (ArgumentCountException ex) //If the sender does not use an adequate amount of arguments
		{
			if (ex.getErrorIndex() == 1)
				hardFailure = true;
			else
				softFailure = true;
		} catch(ClassCastException e) //If the command tries to get a Player from the sender, but the sender is the console
		{
			helpMessage = "You must be a Player to execute this command.";
			softFailure = true;
		}
		if(hardFailure)
			return false;
		else if(softFailure)
		{
			sender.sendMessage(ChatColor.RED + helpMessage);
			return true;
		}
		return true;
	}
}
