import org.bukkit.Material;
import org.bukkit.entity.Entity;

import org.bukkit.entity.Player;
import yorickbm.towerdefence.API.Annotations.TowerLevel;
import yorickbm.towerdefence.towers.Schematic.*;
import yorickbm.towerdefence.towers.Tower;

import java.util.List;
import java.util.Random;

public class BombTower extends Tower {

    public BombTower() {
        super();

        super.icon = Material.TNT;
        super.Name = "Bomb Tower";
        super.Description = "Bomb all near by mobs! Dealing damage";
        super.Range = 5;

        super.loadSchematic(1, new TowerSchematic("{material&=&STONE_BRICKS%,% data&=&minecraft:stone_bricks%,% location&=&-1.0;0.0;-1.0}\n{material&=&CYAN_TERRACOTTA%,% data&=&minecraft:cyan_terracotta%,% location&=&-1.0;1.0;-1.0}\n{material&=&CYAN_TERRACOTTA%,% data&=&minecraft:cyan_terracotta%,% location&=&-1.0;2.0;-1.0}\n{material&=&INFESTED_CHISELED_STONE_BRICKS%,% data&=&minecraft:infested_chiseled_stone_bricks%,% location&=&-1.0;3.0;-1.0}\n{material&=&STONE_BRICK_SLAB%,% data&=&minecraft:stone_brick_slab[type=bottom,waterlogged=false]%,% location&=&-1.0;4.0;-1.0}\n{material&=&STONE_BRICK_STAIRS%,% data&=&minecraft:stone_brick_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]%,% location&=&-1.0;0.0;0.0}\n{material&=&STONE_BUTTON%,% data&=&minecraft:stone_button[face=wall,facing=west,powered=false]%,% location&=&-1.0;1.0;0.0}\n{material&=&STONE_BRICK_SLAB%,% data&=&minecraft:stone_brick_slab[type=top,waterlogged=false]%,% location&=&-1.0;2.0;0.0}\n{material&=&STONE_BRICK_STAIRS%,% data&=&minecraft:stone_brick_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]%,% location&=&-1.0;3.0;0.0}\n{material&=&AIR%,% data&=&minecraft:air%,% location&=&-1.0;4.0;0.0}\n{material&=&STONE_BRICKS%,% data&=&minecraft:stone_bricks%,% location&=&-1.0;0.0;1.0}\n{material&=&CYAN_TERRACOTTA%,% data&=&minecraft:cyan_terracotta%,% location&=&-1.0;1.0;1.0}\n{material&=&CYAN_TERRACOTTA%,% data&=&minecraft:cyan_terracotta%,% location&=&-1.0;2.0;1.0}\n{material&=&INFESTED_CHISELED_STONE_BRICKS%,% data&=&minecraft:infested_chiseled_stone_bricks%,% location&=&-1.0;3.0;1.0}\n{material&=&STONE_BRICK_SLAB%,% data&=&minecraft:stone_brick_slab[type=bottom,waterlogged=false]%,% location&=&-1.0;4.0;1.0}\n{material&=&STONE_BRICK_STAIRS%,% data&=&minecraft:stone_brick_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]%,% location&=&0.0;0.0;-1.0}\n{material&=&STONE_BUTTON%,% data&=&minecraft:stone_button[face=wall,facing=north,powered=false]%,% location&=&0.0;1.0;-1.0}\n{material&=&STONE_BRICK_SLAB%,% data&=&minecraft:stone_brick_slab[type=top,waterlogged=false]%,% location&=&0.0;2.0;-1.0}\n{material&=&STONE_BRICK_STAIRS%,% data&=&minecraft:stone_brick_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]%,% location&=&0.0;3.0;-1.0}\n{material&=&AIR%,% data&=&minecraft:air%,% location&=&0.0;4.0;-1.0}\n{material&=&STONE_BRICKS%,% data&=&minecraft:stone_bricks%,% location&=&0.0;0.0;0.0}\n{material&=&WHITE_TERRACOTTA%,% data&=&minecraft:white_terracotta%,% location&=&0.0;1.0;0.0}\n{material&=&WHITE_TERRACOTTA%,% data&=&minecraft:white_terracotta%,% location&=&0.0;2.0;0.0}\n{material&=&TNT%,% data&=&minecraft:tnt[unstable=false]%,% location&=&0.0;3.0;0.0}\n{material&=&AIR%,% data&=&minecraft:air%,% location&=&0.0;4.0;0.0}\n{material&=&STONE_BRICK_STAIRS%,% data&=&minecraft:stone_brick_stairs[facing=north,half=bottom,shape=straight,waterlogged=false]%,% location&=&0.0;0.0;1.0}\n{material&=&STONE_BUTTON%,% data&=&minecraft:stone_button[face=wall,facing=south,powered=false]%,% location&=&0.0;1.0;1.0}\n{material&=&STONE_BRICK_SLAB%,% data&=&minecraft:stone_brick_slab[type=top,waterlogged=false]%,% location&=&0.0;2.0;1.0}\n{material&=&STONE_BRICK_STAIRS%,% data&=&minecraft:stone_brick_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]%,% location&=&0.0;3.0;1.0}\n{material&=&AIR%,% data&=&minecraft:air%,% location&=&0.0;4.0;1.0}\n{material&=&STONE_BRICKS%,% data&=&minecraft:stone_bricks%,% location&=&1.0;0.0;-1.0}\n{material&=&CYAN_TERRACOTTA%,% data&=&minecraft:cyan_terracotta%,% location&=&1.0;1.0;-1.0}\n{material&=&CYAN_TERRACOTTA%,% data&=&minecraft:cyan_terracotta%,% location&=&1.0;2.0;-1.0}\n{material&=&INFESTED_CHISELED_STONE_BRICKS%,% data&=&minecraft:infested_chiseled_stone_bricks%,% location&=&1.0;3.0;-1.0}\n{material&=&STONE_BRICK_SLAB%,% data&=&minecraft:stone_brick_slab[type=bottom,waterlogged=false]%,% location&=&1.0;4.0;-1.0}\n{material&=&STONE_BRICK_STAIRS%,% data&=&minecraft:stone_brick_stairs[facing=west,half=bottom,shape=straight,waterlogged=false]%,% location&=&1.0;0.0;0.0}\n{material&=&STONE_BUTTON%,% data&=&minecraft:stone_button[face=wall,facing=east,powered=false]%,% location&=&1.0;1.0;0.0}\n{material&=&STONE_BRICK_SLAB%,% data&=&minecraft:stone_brick_slab[type=top,waterlogged=false]%,% location&=&1.0;2.0;0.0}\n{material&=&STONE_BRICK_STAIRS%,% data&=&minecraft:stone_brick_stairs[facing=east,half=bottom,shape=straight,waterlogged=false]%,% location&=&1.0;3.0;0.0}\n{material&=&AIR%,% data&=&minecraft:air%,% location&=&1.0;4.0;0.0}\n{material&=&STONE_BRICKS%,% data&=&minecraft:stone_bricks%,% location&=&1.0;0.0;1.0}\n{material&=&CYAN_TERRACOTTA%,% data&=&minecraft:cyan_terracotta%,% location&=&1.0;1.0;1.0}\n{material&=&CYAN_TERRACOTTA%,% data&=&minecraft:cyan_terracotta%,% location&=&1.0;2.0;1.0}\n{material&=&INFESTED_CHISELED_STONE_BRICKS%,% data&=&minecraft:infested_chiseled_stone_bricks%,% location&=&1.0;3.0;1.0}\n{material&=&STONE_BRICK_SLAB%,% data&=&minecraft:stone_brick_slab[type=bottom,waterlogged=false]%,% location&=&1.0;4.0;1.0}"));
    }

    @TowerLevel(level = 1, costs = 42)
    public void trigger_lvl1(List<Entity> entities) {

        Random rand = new Random();
        Entity victem = entities.get(0);

        victem = entities.get(rand.nextInt(entities.size()));
        victem.getWorld().createExplosion(victem.getLocation().getX(), victem.getLocation().getY(), victem.getLocation().getZ(),
                3, false, false);

        super.setCooldown(12);

    }

    @TowerLevel(level = 2, costs = 67)
    public void trigger_lvl2(List<Entity> entities) {
        Random rand = new Random();
        Entity victem = entities.get(0);

        victem = entities.get(rand.nextInt(entities.size()));
        victem.getWorld().createExplosion(victem.getLocation().getX(), victem.getLocation().getY(), victem.getLocation().getZ(),
                5, false, false);

        super.setCooldown(12);
    }

    @TowerLevel(level = 3, costs = 96)
    public void trigger_lvl3(List<Entity> entities) {
        Random rand = new Random();
        Entity victem = entities.get(0);

        victem = entities.get(rand.nextInt(entities.size()));
        victem.getWorld().createExplosion(victem.getLocation().getX(), victem.getLocation().getY(), victem.getLocation().getZ(),
                5, false, false);

        victem = entities.get(rand.nextInt(entities.size()));
        victem.getWorld().createExplosion(victem.getLocation().getX(), victem.getLocation().getY(), victem.getLocation().getZ(),
                5, false, false);

        super.setCooldown(8);
    }

    @TowerLevel(level = 4, costs= 110)
    public void trigger_lvl4(List<Entity> entities) {
        Random rand = new Random();
        Entity victem = entities.get(0);

        victem = entities.get(rand.nextInt(entities.size()));
        victem.getWorld().createExplosion(victem.getLocation().getX(), victem.getLocation().getY(), victem.getLocation().getZ(),
                5, false, false);

        victem = entities.get(rand.nextInt(entities.size()));
        victem.getWorld().createExplosion(victem.getLocation().getX(), victem.getLocation().getY(), victem.getLocation().getZ(),
                5, false, false);

        super.setCooldown(4);
    }

}
