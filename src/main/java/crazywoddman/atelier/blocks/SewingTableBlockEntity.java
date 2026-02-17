package crazywoddman.atelier.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class SewingTableBlockEntity extends BlockEntity {
    
    private final ItemStackHandler spoolInventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            BlockPos above = worldPosition.above();
            BlockState state = level.getBlockState(above);

            if (level != null && !level.isClientSide && state.getBlock() instanceof SewingTable)
                level.setBlock(above, state.setValue(SewingTable.SPOOL, !getSpoolStack().isEmpty()), Block.UPDATE_ALL);
            
            setChanged();
        }
    };
    
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> spoolInventory);

    public SewingTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(AtelierBlockEntities.SEWING_TABLE.get(), pos, blockState);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        spoolInventory.deserializeNBT(nbt.getCompound("Spool"));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("Spool", spoolInventory.serializeNBT());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
            return handler.cast();
        
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
    }

    public ItemStackHandler getSpoolInventory() {
        return spoolInventory;
    }
    
    public ItemStack getSpoolStack() {
        return spoolInventory.getStackInSlot(0);
    }
    
    public void setSpoolStack(ItemStack stack) {
        spoolInventory.setStackInSlot(0, stack);
    }
}