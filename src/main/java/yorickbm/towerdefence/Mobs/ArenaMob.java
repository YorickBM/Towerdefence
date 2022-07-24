package yorickbm.towerdefence.Mobs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import yorickbm.towerdefence.TowerDefence;
import yorickbm.towerdefence.arena.Arena;
import yorickbm.towerdefence.arena.Castle;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public abstract class ArenaMob {

    protected Entity entity;
    protected EntityType entityType;

    protected float tick_speed = 0.03f; //Every 20th of a second it moves 0.03 of a block
    protected float tick_attack = 3*20; //Every 3 seconds it attacks
    protected double attack_damage = 3; //Damage to castle on hit (Randomness applied)

    private List<Block> path;
    private Castle castle;

    private int step_index = 0;
    private int attack_index = 0;

    private BlockFace direction;
    private Location finalLocation;

    protected BukkitRunnable tickUpdater = new BukkitRunnable() {
        @Override
        public void run() {
            if(entity.isDead()) {
                this.cancel();
                return;
            }

            if(step_index >= path.size()) {

                moveEntityToFinalLocation();
                return;
            }

            Block corner = path.get(step_index);
            Location nextPoint = corner.getLocation().clone().add(0,0,0);
            Vector distanceVector = nextPoint.toVector().subtract(entity.getLocation().toVector());
            Vector moveVector = new Vector(0,distanceVector.getY(),0);

            boolean shouldMoveX = false, shouldMoveZ = false;
            switch(direction) {
                case NORTH,SOUTH -> shouldMoveZ=true;
                case EAST,WEST -> shouldMoveX=true;
            }

            if(distanceVector.getZ() < 0.0f && shouldMoveZ) {
                moveVector.setZ(distanceVector.getZ() < -tick_speed ? -tick_speed : distanceVector.getZ());
                entity.setRotation(180, 0);
            } else if(distanceVector.getZ() > 0.0f && shouldMoveZ) {
                moveVector.setZ(distanceVector.getZ() > tick_speed ? tick_speed : distanceVector.getZ());
                entity.setRotation(0, 0);
            }
            if(distanceVector.getX() < 0.0f && shouldMoveX) {
                moveVector.setX(distanceVector.getX() < -tick_speed ? -tick_speed : distanceVector.getX());
                entity.setRotation(90, 0);
            } else if(distanceVector.getX() > 0.0f && shouldMoveX) {
                moveVector.setX(distanceVector.getX() > tick_speed ? tick_speed : distanceVector.getX());
                entity.setRotation(-90, 0);
            }

            entity.setVelocity(moveVector);

            if(shouldMoveX && distanceVector.getX() < 0.01f && distanceVector.getX() > -0.01f)
                reachedBlock(corner.getLocation().subtract(0,1,0).getBlock());
            if(shouldMoveZ && distanceVector.getZ() < 0.01f && distanceVector.getZ() > -0.01f)
                reachedBlock(corner.getLocation().subtract(0,1,0).getBlock());
        }
    };

    private void moveEntityToFinalLocation() {
        if(finalLocation == null) {
            finalLocation = path.get(path.size()-1).getLocation().clone();

            switch(direction) {
                case NORTH,SOUTH -> finalLocation.add(ThreadLocalRandom.current().nextInt(-2, 2), 0, 0);
                case EAST,WEST -> finalLocation.add(0, 0, ThreadLocalRandom.current().nextInt(-2, 2));
            }

            switch(direction) {
                case NORTH -> finalLocation.add(0, 0, -ThreadLocalRandom.current().nextInt(3));
                case SOUTH -> finalLocation.add(0, 0, ThreadLocalRandom.current().nextInt(3));
                case EAST -> finalLocation.add(ThreadLocalRandom.current().nextInt(3), 0, 0);
                case WEST -> finalLocation.add(-ThreadLocalRandom.current().nextInt(3), 0, 0);
            }
        }

        Vector distanceVector = finalLocation.toVector().subtract(entity.getLocation().toVector());
        Vector moveVector = new Vector(0,distanceVector.getY(),0);

        if(distanceVector.getZ() < 0.0f) {
            moveVector.setZ(distanceVector.getZ() < -tick_speed ? -tick_speed : distanceVector.getZ());
        } else if(distanceVector.getZ() > 0.0f) {
            moveVector.setZ(distanceVector.getZ() > tick_speed ? tick_speed : distanceVector.getZ());
        }
        if(distanceVector.getX() < 0.0f) {
            moveVector.setX(distanceVector.getX() < -tick_speed ? -tick_speed : distanceVector.getX());
        } else if(distanceVector.getX() > 0.0f) {
            moveVector.setX(distanceVector.getX() > tick_speed ? tick_speed : distanceVector.getX());
        }

        entity.setVelocity(moveVector);

        //See if entity can attack!!!
        if(attack_index++ > tick_attack) {
            attack_index = 0;
            int missfire = ThreadLocalRandom.current().nextInt(0,5);
            if(missfire == 0) return; //Missfired attack

            if(entity.isDead()) return;
            LivingEntity le = ((LivingEntity)entity);
            le.swingMainHand();

            le.damage(ThreadLocalRandom.current().nextDouble(0.20, 0.85)); //Every attack kosts the mob between 0.20 and 0.85 health.
            if(castle != null) castle.applyDamage(ThreadLocalRandom.current().nextDouble(attack_damage*0.66, attack_damage));
        }
    }

    private void reachedBlock(Block blk) {
        step_index += 1;
        direction = ((int)blk.getData()) == 0 ? BlockFace.NORTH :
                ((int)blk.getData()) == 1 ? BlockFace.EAST :
                        ((int)blk.getData()) == 2 ? BlockFace.SOUTH :
                                ((int)blk.getData()) == 3 ? BlockFace.WEST : direction;
    }

    public ArenaMob() {}

    public ArenaMob setPath(List<Block> path) {
        this.path = path;
        return this;
    }

    public ArenaMob setCastle(Castle castle) {
        this.castle = castle;
        return this;
    }

    public ArenaMob spawn(Location location, BlockFace direction) {
        this.direction = direction;
        entity = location.getWorld().spawnEntity(location, entityType);
        tickUpdater.runTaskTimer(TowerDefence.getPlugin(), 8, 1);

        return this;
    }

    public void destroy() {
        if(!tickUpdater.isCancelled()) tickUpdater.cancel();

        if(entity.isDead()) return;
        LivingEntity le = ((LivingEntity)entity);
        le.damage(le.getHealth());
    }

    public <T> T Clone() {
        Object instance = null;
        try {
            instance = this.getClass().getConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return (T) instance;
    }
}
