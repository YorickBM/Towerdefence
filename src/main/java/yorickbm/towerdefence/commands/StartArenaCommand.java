package yorickbm.towerdefence.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yorickbm.towerdefence.API.Exceptions.PlayerNotInArenaException;
import yorickbm.towerdefence.TowerDefence;
import yorickbm.towerdefence.arena.Arena;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class StartArenaCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        int arena = 0;

        if(sender instanceof Player) {
            //TODO check permission

            if(!TowerDefence.getApi().isPlayerInArena((Player)sender) && args.length < 1) {
                sender.sendMessage("Please enter the number of the arena you want to start!");
                return false;
            } else if(args.length < 1) {
                try {
                    arena = TowerDefence.getApi().getArenaForPlayer((Player)sender).getID();
                } catch (PlayerNotInArenaException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                arena = Integer.parseInt(args[0]);
            }
        } else {
            if(args.length < 1) {
                sender.sendMessage("Please give us an arena to start!");
                return false;
            }
            arena = Integer.parseInt(args[0]);
        }

        Arena arenaClass = TowerDefence.getApi().getArena(arena);

        if(arenaClass == null) {
            sender.sendMessage("Could not find the arena you would like to start!");
            return true;
        }

        arenaClass.prepare();
        sender.sendMessage("Arena has been started!");

        return true;
    }

}
