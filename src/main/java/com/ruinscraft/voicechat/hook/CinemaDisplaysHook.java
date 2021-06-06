package com.ruinscraft.voicechat.hook;

import com.ruinscraft.cinemadisplays.CinemaDisplaysPlugin;
import com.ruinscraft.voicechat.VCPlugin;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class CinemaDisplaysHook {

    private VCPlugin vcPlugin;
    private CinemaDisplaysPlugin cinemaDisplaysPlugin;

    public CinemaDisplaysHook(VCPlugin vcPlugin, CinemaDisplaysPlugin cinemaDisplaysPlugin) {
        this.vcPlugin = vcPlugin;
        this.cinemaDisplaysPlugin = cinemaDisplaysPlugin;
    }

    public boolean isInTheater(Player player) {
        return cinemaDisplaysPlugin.getTheaterManager().getCurrentTheater(player) != null;
    }

    public Set<Player> getPlayersInSameTheater(Player player) {
        return cinemaDisplaysPlugin.getTheaterManager().getCurrentTheater(player).getViewers();
    }

    public Set<Player> getPlayersNotInTheater() {
        return vcPlugin.getServer().getOnlinePlayers().stream()
                .filter(p -> cinemaDisplaysPlugin.getTheaterManager().getCurrentTheater(p) == null)
                .collect(Collectors.toSet());
    }

}
