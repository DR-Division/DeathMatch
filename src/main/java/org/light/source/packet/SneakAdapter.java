package org.light.source.packet;

import com.comphenix.packetwrapper.WrapperPlayServerEntityMetadata;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.light.source.Game.GameManager;

import java.util.List;

public class SneakAdapter extends PacketAdapter{

    public SneakAdapter(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.ENTITY_METADATA) {
            Player p = event.getPlayer();
            if (GameManager.getInstance().contains(p.getUniqueId())) {
                WrapperPlayServerEntityMetadata metadata = new WrapperPlayServerEntityMetadata(event.getPacket().deepClone());
                Entity entity = metadata.getEntity(event);
                if (entity != p && entity instanceof LivingEntity) {
                    List<WrappedWatchableObject> metas = metadata.getMetadata();
                    Object object = metas.get(0).getValue();
                    if (object instanceof Byte && ((byte)object | 0x02) == 0x02) {
                        metadata.getMetadata().get(0).setValue((byte) ((byte) metas.get(0).getValue() & ~0x02));
                        event.setPacket(metadata.getHandle());
                    }
                }
            }
        }

    }
}
