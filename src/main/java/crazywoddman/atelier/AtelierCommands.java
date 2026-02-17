package crazywoddman.atelier;

import java.util.List;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import crazywoddman.atelier.api.ModulesDataProvider;
import crazywoddman.atelier.api.ModulesDataProvider.ModulesData.ParentReference;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class AtelierCommands {
    private static final String RESET_FAILURE_KEY = "commands.reset_accessories.failed";
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(
            Commands
            .literal("atelier")
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
                )
            )
            .then(Commands
                .literal("capability")
                .then(Commands
                    .argument("target", EntityArgument.entity())
                    .executes(AtelierCommands::modulesData)
                )
            )
        );
    }

    private static int setAccessory(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        
        if (EntityArgument.getEntity(context, "target") instanceof LivingEntity entity) {
            ItemStack stack = ItemArgument.getItem(context, "item").createItemStack(1, false);

            return AccessoriesCapability.getOptionally(entity).map(capability -> {
                String slot = StringArgumentType.getString(context, "slot");
                AccessoriesContainer container = capability.getContainer(new SlotTypeReference(slot));
                
                if (container == null) {
                    source.sendFailure(Component.literal("Slot [" + slot + "] not found"));
                    return 0;
                }
                
                ExpandedSimpleContainer accessories = container.getAccessories();
                int index = IntegerArgumentType.getInteger(context, "index");
                
                if (index >= accessories.getContainerSize()) {
                    source.sendFailure(Component.literal("Index [" + index + "] out of bounds: [" + (accessories.getContainerSize() - 1) + "]"));
                    return 0;
                }
                
                accessories.setItem(index, stack);
                
                source.sendSuccess(
                    () -> Component.literal("Set " + stack.getDisplayName().getString() + 
                        " in slot " + slot + "[" + index + 
                        "] for " + entity.getDisplayName().getString()),
                    true
                );
                
                return 1;
            }).orElseGet(() -> {
                source.sendFailure(Component.literal(entity.getDisplayName().getString() + " has no accessories capability"));
                return 0;
            });
        }
        
        source.sendFailure(Component.literal("Target is not a living entity"));
        return 0;
    }

    private static int modulesData(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        try {
            if (EntityArgument.getEntity(context, "target") instanceof LivingEntity entity) {
                Map<String, List<ParentReference>> data = ModulesDataProvider.get(entity).modules;
                MutableComponent component = Component.empty();
                for (String slot : data.keySet())
                    component.append(slot + ": " + data.get(slot).stream().map(value -> value.accessoriesSlot() == null ? value.equipmentSlot().toString() : (value.accessoriesSlot().slotName() + "[" + value.accessoriesSlot().slot() + "]")).toList().toString() + ", ");
                source.sendSuccess(() -> component, true);
            }
        } catch (Exception e) {
            source.sendFailure(Component.translatable("command.failed"));
        }

        return 0;
    }
    
    private static int resetAccessories(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        try {
            Entity entity = EntityArgument.getEntity(context, "target");

            if ((entity instanceof LivingEntity living))
                return AccessoriesCapability
                    .getOptionally(living)
                    .map(inventory -> {
                        inventory.reset(false);
                        source.sendSuccess(
                            () -> Component.translatable("commands.reset_accessories.success", living.getDisplayName()),
                            true
                        );

                        return 1;
                    })
                    .orElseGet(() -> {
                        source.sendFailure(Component.translatable(RESET_FAILURE_KEY, living.getDisplayName()));
                        return 0;
                    });
            
            source.sendFailure(Component.translatable(RESET_FAILURE_KEY, entity.getDisplayName()));
        } catch (Exception e) {
            source.sendFailure(Component.translatable("command.failed"));
        }

        return 0;
    }
}