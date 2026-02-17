package crazywoddman.atelier.api;

import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import crazywoddman.atelier.Atelier;
import crazywoddman.atelier.network.NetworkHandler;
import crazywoddman.atelier.network.SyncModulesDataPacket;
import io.wispforest.accessories.api.slot.SlotReference;

public class ModulesDataProvider implements ICapabilityProvider {
    public static Capability<ModulesData> MODULES_DATA = CapabilityManager.get(new CapabilityToken<>(){});
    public static ModulesData get(LivingEntity entity) {
        return entity
            .getCapability(ModulesDataProvider.MODULES_DATA)
            .orElseThrow(() -> new IllegalStateException(entity.getName().getString() + " does not have modules capability"));
    }
    
    private final LivingEntity entity;
    private ModulesData modulesData = null;
    private final LazyOptional<ModulesData> optional = LazyOptional.of(this::getModulesData);
    
    public ModulesDataProvider(LivingEntity entity) {
        this.entity = entity;
    }
    
    private ModulesData getModulesData() {
        if (modulesData == null)
            modulesData = new ModulesData(entity);
        return modulesData;
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        return capability == MODULES_DATA ? optional.cast() : LazyOptional.empty();
    }

    public static class ModulesData implements INBTSerializable<CompoundTag> {
        private final LivingEntity entity;
        private boolean dirty;
        
        public record ParentReference(SlotReference accessoriesSlot, EquipmentSlot equipmentSlot) {
            public ParentReference(SlotReference accessoriesSlot) {
                this(accessoriesSlot, null);
            }

            public ParentReference(EquipmentSlot equipmentSlot) {
                this(null, equipmentSlot);
            }

            public UUID createUUID() {
                return UUIDUtil.createOfflinePlayerUUID(this.accessoriesSlot == null ? this.equipmentSlot.name() : (this.accessoriesSlot.slotName() + "_" + this.accessoriesSlot.slot()));
            }
        }
        public Map<String, List<ParentReference>> modules;

        private ModulesData(LivingEntity entity) {
            this.entity = entity;
            clear();
        }

        private void markDirty() {
            if (!dirty && !entity.level().isClientSide) {
                dirty = true;
                Atelier.Queue.addToQueue(entity, () -> {
                    NetworkHandler.CHANNEL.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                        new SyncModulesDataPacket(entity.getId(), serializeNBT())
                    );
                    dirty = false;
                });
            }
        }

        public List<ParentReference> get(String slot) {
            if (!modules.containsKey(slot)) {
                modules.put(slot, new ArrayList<>() { 
                    @Override
                    public boolean add(ParentReference parent) {
                        markDirty();
                        return super.add(parent);
                    }
                    @Override
                    public boolean remove(Object o) {
                        boolean result = super.remove(o);
                        
                        if (result)
                            markDirty();
                        
                        return result;
                    }
                });
                markDirty();
            }

            return modules.get(slot);
        }
        
        public void clear() {
            modules = new HashMap<>();
            markDirty();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            
            for (Map.Entry<String, List<ParentReference>> entry : modules.entrySet()) {
                ListTag slot = new ListTag();

                for (ParentReference parent : entry.getValue()) {
                    CompoundTag parentTag = new CompoundTag();

                    if (parent.accessoriesSlot == null)
                        parentTag.putString("slot", parent.equipmentSlot.getName());
                    else {
                        parentTag.putString("slot", parent.accessoriesSlot.slotName());
                        parentTag.putInt("index", parent.accessoriesSlot.slot());
                    }

                    slot.add(parentTag);
                }

                tag.put(entry.getKey(), slot);
            }

            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            clear();;

            for (String key : tag.getAllKeys()) {
                List<ParentReference> parents = new ArrayList<>();
                ListTag list = tag.getList(key, Tag.TAG_COMPOUND);
                
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag parent = list.getCompound(i);
                    
                    if (parent.contains("index")) {
                        String slot = parent.getString("slot");
                        int index = parent.getInt("index");
                        parents.add(new ParentReference(SlotReference.of(entity, slot, index)));
                    } else {
                        EquipmentSlot slot = EquipmentSlot.byName(parent.getString("slot"));
                        parents.add(new ParentReference(slot));
                    }
                }
                
                modules.put(key, parents);
            }
        }
    }
}