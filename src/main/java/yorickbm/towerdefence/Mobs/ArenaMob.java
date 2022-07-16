package yorickbm.towerdefence.Mobs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import yorickbm.towerdefence.arena.Arena;

import java.util.List;

public abstract class ArenaMob {

    protected LivingEntity entity;
    protected Arena arena;
    protected float tick_speed = 0.03f;
    protected List<Block> path;
    private int step_index = 0;

    protected BukkitRunnable tickUpdater = new BukkitRunnable() {
        @Override
        public void run() {
            if(step_index >= path.size()) {
                tickUpdater.cancel(); //Stop our move updater!

                //TODO move between 1/3 blocks forward
                //TODO move to the side 1/5 blocks

                return;
            }

            Block corner = path.get(step_index);
            Location nextPoint = corner.getLocation().clone();
            Vector distanceVector = nextPoint.toVector().subtract(entity.getLocation().toVector());
            Vector moveVector = new Vector(0,0,0);

            if(distanceVector.getZ() < 0.0f) {
                moveVector = new Vector(0, distanceVector.getY(), distanceVector.getZ() < -tick_speed ? -tick_speed : distanceVector.getZ());
                entity.setRotation(180, 0);
            } else if(distanceVector.getZ() > 0.0f) {
                moveVector = new Vector(0, distanceVector.getY(), distanceVector.getZ() > tick_speed ? tick_speed : distanceVector.getZ());
                entity.setRotation(0, 0);
            }
            if(distanceVector.getX() < 0.0f) {
                moveVector = new Vector(distanceVector.getX() < -tick_speed ? -tick_speed : distanceVector.getX(), distanceVector.getY(), 0);
                entity.setRotation(90, 0);
            } else if(distanceVector.getX() > 0.0f) {
                moveVector = new Vector(distanceVector.getX() > tick_speed ? tick_speed : distanceVector.getX(), distanceVector.getY(), 0);
                entity.setRotation(-90, 0);
            }

            entity.setVelocity(moveVector);

            if(distanceVector.getZ() < 0.01f && distanceVector.getZ() > -0.01f
            && distanceVector.getX() < 0.01f && distanceVector.getX() > -0.01f) {
                step_index += 1;
            }
        }
    };

    public abstract void spawn(Location location);

    public void destroy() {
        entity.damage(entity.getHealth());
    }
}
