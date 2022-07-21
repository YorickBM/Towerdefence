package yorickbm.towerdefence.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yorickbm.towerdefence.API.RandomString;
import yorickbm.towerdefence.TowerDefence;
import yorickbm.towerdefence.towers.Schematic.TowerSchematic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class CreateTowerCommand implements CommandExecutor {

    public static RandomString identifierGen = new RandomString();

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if(sender instanceof Player) {
            //TODO check permission
        } else { sender.sendMessage("Only a player max execute this command!"); return false; } //Only players can create towers

        if(args.length < 6) {
            sender.sendMessage("Please enter both corner locations of tower");
            return false;
        }

        Location A = new Location(((Player) sender).getWorld(), Float.parseFloat(args[0]), Float.parseFloat(args[1]), Float.parseFloat(args[2]));
        Location B = new Location(((Player) sender).getWorld(), Float.parseFloat(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));

        TowerSchematic schematic = new TowerSchematic(A,B);

        String clazz = "super.loadSchematic(1, new TowerSchematic(\"" + schematic.toString().replace("\n", "\\n") + "\"));";
        clazz.replace("}\n\"));", "}\"));");

        File file = null;
        try {
            file = new File(TowerDefence.getInstance().getDataFolder() + "/towers/");
            file.mkdirs();
            file = new File(file.getPath() + "/" + identifierGen.nextString() + ".java");
            if(!file.createNewFile()) {
                sender.sendMessage("Could not create your tower file!");
                return false;
            }

            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(clazz);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage("Created your tower: " + file.getName() + " at " + file.getParent());
        return true;
    }



}
