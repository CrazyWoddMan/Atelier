package crazywoddman.atelier.api;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;

public final class SimpleSlot implements Comparable<SimpleSlot> {
    public static final byte NO_INDEX = -1;
    public final String name;
    public final byte index;

    private SimpleSlot(String name, byte index) {
        this.name = name;
        this.index = index;
    }

    public static SimpleSlot of(String name, byte index) {
        return (name == null && index == NO_INDEX) ? null : new SimpleSlot(name, index);
    }

    public static SimpleSlot of(String name, int index) {
        return of(name, (byte)index);
    }

    public static SimpleSlot of(EquipmentSlot slot) {
        return new SimpleSlot(slot.getName(), (byte)-slot.ordinal());
    }

    @Override
    public String toString() {
        return this.index < NO_INDEX ? this.name : "%s[%d]".formatted(this.name, this.index);
    }

    @Override
    public int compareTo(SimpleSlot other) {
        int result = this.name.compareTo(other.name);
        return result == 0 ? Integer.compare(Math.abs(this.index), Math.abs(other.index)) : result;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof SimpleSlot other && this.index == other.index && Objects.equals(this.name, other.name));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.index);
    }

    public EquipmentSlot asEquipmentSlot() {
        return EquipmentSlot.values()[-this.index];
    }

    public Optional<SlotAccess> getAccess(Player player) {
        return this.index < NO_INDEX
        ? Optional.of(SlotAccess.forEquipmentSlot(player, asEquipmentSlot()))
        : CompatHelper.getSlotAccess(player, this);
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeByte(this.index);

        if (this.index > NO_INDEX)
            buf.writeUtf(this.name);
    }

    public static SimpleSlot fromNetwork(FriendlyByteBuf buf) {
        byte index = buf.readByte();

        return index < NO_INDEX
        ? of(EquipmentSlot.values()[-index])
        : of(buf.readUtf(), index);
    }
}