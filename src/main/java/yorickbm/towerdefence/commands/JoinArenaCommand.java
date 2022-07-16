package yorickbm.towerdefence.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yorickbm.towerdefence.Core;
import yorickbm.towerdefence.arena.Arena;

import java.util.Random;

public class JoinArenaCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("Only a player can execute this commando!");
            return false;
        }

        if(args.length < 1) {
            sender.sendMessage("Please enter the number of the arena you want to join!");
            return false;
        }

        int arena = 0;
        if(args[0].equals("random")) arena = new Random().nextInt(Core.getInstance().getArenas().size());
        else arena = Integer.parseInt(args[0]);

        if(arena > Core.getInstance().getArenas().size()) {
            sender.sendMessage("The number you entered is not a valid arena!");
            return false;
        }

        Arena arenaClass = Core.getInstance().getArenas().get(arena-1);
        arenaClass.teleportLobby((Player)sender);
        arenaClass.addPlayer(((Player)sender).getUniqueId());

        return true;
    }

}
