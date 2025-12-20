// Создайте новый класс AtelierCommands.java:
package crazywoddman.atelier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AtelierCommands {
    private static final String CLEAR_FAILURE_KEY = "commands.clear_slot_modifiers.failed";
    private static final String RESET_FAILURE_KEY = "commands.reset_accessories.failed";
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands
            .literal("atelier")
            .requires(source -> source.hasPermission(3))
            .then(Commands
                .literal("accessories")
                .then(Commands
                    .literal("clearSlotModifiers")
                    .then(Commands
                        .argument("target", EntityArgument.entity())
                        .executes(AtelierCommands::clearSlotsAttributeModifiers)
                    )
                )
                .then(Commands
                    .literal("reset")
                    .then(Commands
                        .argument("target", EntityArgument.entity())
                        .executes(AtelierCommands::resetAccessories)
                    )
                )
            )
        );
    }
    
    private static int clearSlotsAttributeModifiers(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        try {
            Entity entity = EntityArgument.getEntity(context, "target");

            if ((entity instanceof LivingEntity living))
                return AccessoriesCapability
                    .getOptionally(living)
                    .map(inventory -> {
                        inventory.clearSlotModifiers();
                        source.sendSuccess(
                            () -> Component.translatable("commands.clear_slot_modifiers.success", living.getDisplayName()),
                            true
                        );

                        return 1;
                    })
                    .orElseGet(() -> {
                        source.sendFailure(Component.translatable(CLEAR_FAILURE_KEY, living.getDisplayName()));
                        return 0;
                    });
            
            source.sendFailure(Component.translatable(CLEAR_FAILURE_KEY, entity.getDisplayName()));
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