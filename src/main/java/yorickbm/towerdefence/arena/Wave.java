package yorickbm.towerdefence.arena;

import yorickbm.towerdefence.API.Pair;
import yorickbm.towerdefence.Mobs.ArenaMob;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: YorickBM (https://www.spigotmc.org/members/yorick.111571/)
 */
public class Wave {

    private List<Pair<ArenaMob, Integer>> _waveMobs;

    public Wave() {
        _waveMobs = new ArrayList<>();
    }

    public void spawn(Arena arena) {
        _waveMobs.forEach(wave -> {
            arena.spawnMob(wave.getKey(), wave.getValue(), true, true);
        });
    }

    public void addMob(ArenaMob arenaMob, int amount) {
        _waveMobs.add(new Pair<>(arenaMob, amount));
    }
}
