package yorickbm.towerdefence.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import yorickbm.towerdefence.API.RandomString;
import yorickbm.towerdefence.Core;
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

        String clazz = "import org.bukkit.Material;\n" +
                "import yorickbm.towerdefence.API.Annotations.TowerLevel;\n" +
                "import yorickbm.towerdefence.Core;\n" +
                "\n" +
                "import java.util.logging.Level;\n" +
                "\n" +
                "/**\n" +
                " * Last modified by: YorickBM on 27-06-2022\n" +
                " */\n" +
                "public class GenericTower extends Tower {\n" +
                "\n" +
                "    public GenericTower() {\n" +
                "        super();\n" +
                "\n" +
                "        super.icon = Material.CHEST;\n" +
                "        super.Name = \"Generic Tower\";\n" +
                "        super.Description = \"This tower just looks pretty :D\";\n" +
                "        super.Range = 1;\n" +
                "\n" +
                "        super.schematic = new TowerSchematic(\"" + schematic.toString().replace("\n", "\\n") + "\")\n" +
                "    }\n" +
                "\n" +
                "    @TowerLevel(level = 1)\n" +
                "    public void trigger_lvl1() {\n" +
                "        Core.getInstance().getLogger().log(Level.CONFIG, String.format(\"Trigger for lvl 1 of tower %s\", super.Name));\n" +
                "    }\n" +
                "\n" +
                "    @TowerLevel(level = 2)\n" +
                "    public void trigger_lvl2() {\n" +
                "        Core.getInstance().getLogger().log(Level.CONFIG, String.format(\"Trigger for lvl 2 of tower %s\", super.Name));\n" +
                "    }\n" +
                "\n" +
                "}\n";

        File file = null;
        try {
            file = new File(Core.getInstance().getDataFolder() + "/towers/");
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
