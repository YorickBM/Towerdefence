package yorickbm.towerdefence.towers.Schematic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import yorickbm.towerdefence.API.TDLocation;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class SchematicBlock {

    final Material material;
    final BlockData data;
    final TDLocation location;

    public SchematicBlock(Location origin, Block block) {
        material = block.getType();
        data = block.getBlockData();
        location = new TDLocation(block.getLocation().clone().subtract(origin));
    }

    public SchematicBlock(String block) {
        String[] variables = block.substring(1, block.length()-1).split("%,%");

        material = Material.valueOf(variables[0].split("&=&")[1]);
        data = Bukkit.getServer().createBlockData(variables[1].split("&=&")[1]);
        location = new TDLocation().fromString(variables[2].split("&=&")[1]);
    }

    @Override
    public String toString() {
        return "{" +
                "material&=&" + material +
                "%,% data&=&" + data.getAsString() +
                "%,% location&=&" + location +
                '}';
    }
}
