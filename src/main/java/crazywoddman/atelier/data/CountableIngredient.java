package crazywoddman.atelier.data;

import com.google.gson.JsonElement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class CountableIngredient {
    public final Ingredient ingredient;
    public final int count;

    public CountableIngredient(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    public boolean test(ItemStack stack) {
        return this.ingredient.test(stack) && stack.getCount() >= this.count;
    }

    public ItemStack[] getItems() {
        ItemStack[] stacks = this.ingredient.getItems();

        for (ItemStack stack : stacks)
            stack.setCount(count);

        return stacks;
    }

    public void toNetwork(FriendlyByteBuf buffer) {
        this.ingredient.toNetwork(buffer);
        buffer.writeByte(count);
    }

    public static CountableIngredient fromNetwork(FriendlyByteBuf buffer) {
        return new CountableIngredient(Ingredient.fromNetwork(buffer), buffer.readByte());
    }

    public static CountableIngredient fromJson(JsonElement element) {
        return new CountableIngredient(
            Ingredient.fromJson(element),
            GsonHelper.getAsInt(element.getAsJsonObject(), "count", 1)
        );
    }
}