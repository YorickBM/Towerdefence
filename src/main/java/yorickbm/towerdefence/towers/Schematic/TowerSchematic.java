package yorickbm.towerdefence.towers.Schematic;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class TowerSchematic {

    final List<SchematicBlock> _blocks;
    final List<SchematicBlock> _decoration;

    public TowerSchematic(Location A, Location B) {
        List<SchematicBlock> result = findLocations(A, B);
        _decoration = result.stream().filter(sb -> !sb.material.isSolid()).collect(Collectors.toList());

        result.removeAll(_decoration);
        _blocks = result;
    }

    public TowerSchematic(String data) {
        List<SchematicBlock> result  = new ArrayList<>();

        if(data.length() >= 5)
            for(String block : data.split("\n")) result.add(new SchematicBlock( block));

        _decoration = result.stream().filter(sb -> !sb.material.isSolid()).collect(Collectors.toList());

        result.removeAll(_decoration);
        _blocks = result;
    }

    public void build(Location location) {
        _blocks.forEach(sb -> {
            Block block = location.clone().add(sb.location.getX(), sb.location.getY(), sb.location.getZ()).getBlock();
            block.setType(sb.material);
            block.setBlockData(sb.data);
        });
        _decoration.forEach(sb -> {
            Block block = location.clone().add(sb.location.getX(), sb.location.getY(), sb.location.getZ()).getBlock();
            block.setType(sb.material);
            block.setBlockData(sb.data);
        });
    }

    public void destroy(Location location) {

        _decoration.forEach(sb -> {
            location.clone().add(sb.location.getX(), sb.location.getY(), sb.location.getZ()).getBlock().setType(Material.AIR);
        });
        _blocks.forEach(sb -> {
            location.clone().add(sb.location.getX(), sb.location.getY(), sb.location.getZ()).getBlock().setType(Material.AIR);
        });

    }

    //Source: https://gist.github.com/anonymous/c2141491bc3917f7ad088cc4a9b400b1
    //Modified by: YorickBM
    private List<SchematicBlock> findLocations(Location start, Location end) {
        final List<SchematicBlock> data = new ArrayList<>();

        final int topBlockX = (start.getBlockX() < end.getBlockX() ? end.getBlockX() : start.getBlockX());
        final int bottomBlockX = (start.getBlockX() > end.getBlockX() ? end.getBlockX() : start.getBlockX());

        final int topBlockY = (start.getBlockY() < end.getBlockY() ? end.getBlockY() : start.getBlockY());
        final int bottomBlockY = (start.getBlockY() > end.getBlockY() ? end.getBlockY() : start.getBlockY());

        final int topBlockZ = (start.getBlockZ() < end.getBlockZ() ? end.getBlockZ() : start.getBlockZ());
        final int bottomBlockZ = (start.getBlockZ() > end.getBlockZ() ? end.getBlockZ() : start.getBlockZ());

        final int centerZ = (topBlockZ+bottomBlockZ)/2;
        final int centerX = (topBlockX+bottomBlockX)/2;

        Location origin = new Location(start.getWorld(), centerX, bottomBlockY, centerZ);

        for(int x = centerX-1; x <= centerX+1; x++) {
            for(int z = centerZ-1; z <= centerZ+1; z++) {
                for(int y = bottomBlockY; y <= topBlockY; y++) {
                    SchematicBlock schematicBlock = new SchematicBlock(origin, start.getWorld().getBlockAt(x, y, z));
                    data.add(schematicBlock);
                }
            }
        }

        return data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for(SchematicBlock block : _blocks) builder.append(block.toString() + "\n");
        for(SchematicBlock block : _decoration) builder.append(block.toString() + "\n");

        return builder.toString().substring(0, builder.toString().lastIndexOf("\n"));
    }

}
