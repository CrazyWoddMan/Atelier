package crazywoddman.atelier.blocks;

import java.util.EnumMap;
import java.util.Map;

import crazywoddman.atelier.gui.SewingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class SewingTable extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty SPOOL = BooleanProperty.create("spool");
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    public enum Part implements StringRepresentable {
        LEFT("left"),
        RIGHT("right"),
        MACHINE("machine");

        private final String name;

        Part(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private static final Map<Direction, VoxelShape> LEFT_SHAPES = shapeMap(Shapes.join(Shapes.block(), box(0, 0, 0, 14, 14, 14), BooleanOp.ONLY_FIRST));
    private static final Map<Direction, VoxelShape> MACHINE_SHAPES = shapeMap(box(5, 0, 6, 15, 7, 10));

    public SewingTable(Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(PART, Part.LEFT)
            .setValue(SPOOL, false)
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(PART)) {
            case LEFT -> LEFT_SHAPES.get(state.getValue(FACING));
            case RIGHT -> super.getShape(state, world, pos, context);
            case MACHINE -> MACHINE_SHAPES.get(state.getValue(FACING));
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, PART, SPOOL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (
            level.getBlockState(pos.relative(facing.getCounterClockWise())).canBeReplaced(context)
            && level.getBlockState(pos.above()).canBeReplaced(context)
            && level.getWorldBorder().isWithinBounds(pos)
        )
            return super.getStateForPlacement(context).setValue(FACING, facing).setValue(PART, Part.LEFT);

        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(level, pos, state, entity, stack);

        Direction facing = state.getValue(FACING);
        level.setBlock(
            pos.relative(facing.getCounterClockWise()),
            defaultBlockState().setValue(FACING, facing).setValue(PART, Part.RIGHT),
            UPDATE_ALL
        );
        level.setBlock(
            pos.above(),
            defaultBlockState().setValue(FACING, facing).setValue(PART, Part.MACHINE),
            UPDATE_ALL
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof SewingTableBlockEntity sewingTable)
            Containers.dropItemStack(
                    level,
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    sewingTable.getSpoolStack()
                );

            BlockPos[] poses = switch (state.getValue(PART)) {
                case LEFT -> new BlockPos[]{pos.above(), pos.relative(state.getValue(FACING).getCounterClockWise())};
                case RIGHT -> new BlockPos[]{pos.relative(state.getValue(FACING).getClockWise())};
                case MACHINE -> new BlockPos[]{pos.below()};
            };

            for (BlockPos side : poses)
                if (level.getBlockState(side).getBlock() == this)
                    level.destroyBlock(side, false);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            NetworkHooks.openScreen((ServerPlayer) player, 
                new SimpleMenuProvider(
                    (id, inventory, p) -> new SewingTableMenu(id, inventory, pos),
                    Component.empty()
                ),
                pos
            );
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == Part.LEFT ? new SewingTableBlockEntity(pos, state) : null;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    public static VoxelShape rotateShape(VoxelShape northShape, Direction facing) {
        VoxelShape[] buffer = new VoxelShape[]{northShape, Shapes.empty()};
        int times = (facing.get2DDataValue() + 2) % 4;
        
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    private static Map<Direction, VoxelShape> shapeMap(VoxelShape northShape) {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);

        map.put(Direction.NORTH, northShape);

        for (Direction facing : new Direction[]{Direction.EAST, Direction.SOUTH, Direction.WEST})
            map.put(facing, rotateShape(northShape, facing));

        return map;
    }
}
