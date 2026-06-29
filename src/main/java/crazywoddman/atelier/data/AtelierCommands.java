package crazywoddman.atelier.data;

import org.slf4j.Logger;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;

import crazywoddman.atelier.Atelier;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import io.wispforest.accessories.impl.ExpandedSimpleContainer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

// TODO: implement for Curios
public class AtelierCommands {
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(
            Commands
            .literal(Atelier.MODID)
            .requires(source -> source.hasPermission(2))
            .then(Commands
                .literal("accessories")
                .then(Commands
                    .argument("target", EntityArgument.entity())
                    .then(Commands
                        .literal("reset")
                        .executes(AtelierCommands::resetAccessories)
                    )
                    .then(Commands
                        .literal("set")
                        .then(Commands
                            .argument("slot", StringArgumentType.word())
                            .then(Commands
                                .argument("index", IntegerArgumentType.integer(0))
                                .then(Commands
                                    .argument("item", ItemArgument.item(context))
                                    .executes(AtelierCommands::setAccessory)
                                )
                            )
                        )
                    )
                    .then(Commands
                        .literal("copy")
                        .then(Commands
                            .literal("from")
                            .then(Commands
                                .argument("target2", EntityArgument.entity())
                                .executes(AtelierCommands::copyAccessories)
                            )
                        )
                    )
                )
            )
        );
    }

    private static int copyAccessories(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (resetAccessories(context) == 0)
            return 0;

        CommandSourceStack source = context.getSource();
        Entity to = EntityArgument.getEntity(context, "target");
        Entity from = EntityArgument.getEntity(context, "target2");

        if (to instanceof LivingEntity toLiving) {
            if (from instanceof LivingEntity fromLiving) {
                try {
                    AccessoriesCapability toCap = AccessoriesCapability.get(toLiving);
                    AccessoriesCapability fromCap = AccessoriesCapability.get(fromLiving);
                    fromCap.getAllEquipped().forEach(equipped -> {
                        AccessoriesContainer container = toCap.getContainer(new SlotTypeReference(equipped.reference().slotName()));
                        int slot = equipped.reference().slot();

                        if (container != null && container.getSize() > slot) {
                            container.getAccessories().setItem(slot, equipped.stack().copy());
                        }
                    });
                    source.sendSuccess(
                        () -> Component.literal("Successfully copied Accessories inventory from %s to %s ".formatted(
                            fromLiving.getDisplayName().getString(),
                            toLiving.getDisplayName().getString()
                        )),
                        true
                    );
                } catch (Exception e) {
                    LOGGER.error("Failed to reset Accessories inventory", e);
                }
                return failure(source);
            }
            return notaLiving(source, from);
        }
        return notaLiving(source, to);
    }

    private static int setAccessory(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Entity entity = EntityArgument.getEntity(context, "target");
        
        if (entity instanceof LivingEntity living) {
            ItemStack stack = ItemArgument.getItem(context, "item").createItemStack(1, false);

            return AccessoriesCapability.getOptionally(living).map(capability -> {
                String slot = StringArgumentType.getString(context, "slot");
                AccessoriesContainer container = capability.getContainer(new SlotTypeReference(slot));
                
                if (container == null) {
                    source.sendFailure(Component.literal("Slot [" + slot + "] not found"));
                    return 0;
                }
                
                ExpandedSimpleContainer accessories = container.getAccessories();
                int index = IntegerArgumentType.getInteger(context, "index");
                
                if (index >= accessories.getContainerSize()) {
                    source.sendFailure(Component.literal("Index [%d] out of bounds: [%d]".formatted(index, (accessories.getContainerSize() - 1))));
                    return 0;
                }
                
                accessories.setItem(index, stack);
                
                source.sendSuccess(
                    () -> Component.literal("Set %s in slot %s[%d] for %s".formatted(
                        stack.getDisplayName().getString(),
                        slot, index,
                        living.getDisplayName().getString()
                    )),
                    true
                );
                
                return 1;
            }).orElseGet(() -> {
                return noCap(source, entity);
            });
        }
        
        return notaLiving(source, entity);
    }
    
    private static int resetAccessories(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        try {
            Entity entity = EntityArgument.getEntity(context, "target");

            if ((entity instanceof LivingEntity living)) {
                return AccessoriesCapability
                    .getOptionally(living)
                    .map(inventory -> {
                        inventory.reset(false);
                        source.sendSuccess(
                            () -> Component.literal("Successfully reset Accessories inventory for " + living.getDisplayName().getString()),
                            true
                        );

                        return 1;
                    })
                    .orElseGet(() -> {
                        return failure(source);
                    });
            }

            return notaLiving(source, entity);
        } catch (Exception e) {
            LOGGER.error("Failed to reset Accessories inventory", e);
        }
        return failure(source);
    }

    private static int failure(CommandSourceStack source) {
        source.sendFailure(Component.translatable("command.failed"));
        return 0;
    }

    private static int notaLiving(CommandSourceStack source, Entity entity) {
        source.sendFailure(Component.literal("Target is not a living entity: " + entity == null ? null : entity.getDisplayName().getString()));
        return 0;
    }

    private static int noCap(CommandSourceStack source, Entity entity) {
        source.sendFailure(Component.literal(entity == null ? null : entity.getDisplayName().getString() + " has no accessories capability"));
        return 0;
    }
}