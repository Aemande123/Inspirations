package knightminer.inspirations.library.recipe.cauldron;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;

import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Cauldron recipe to transform a potion into another potion. Used primarily with vanilla potion recipes from the brewing registry.
 */
@ParametersAreNonnullByDefault
public class CauldronBrewingRecipe implements ISimpleCauldronRecipe {

	private Ingredient reagent;
	private CauldronState input;
	private CauldronState output;

	/**
	 * @param input    Input potion type
	 * @param reagent  Ingredient for transformation
	 * @param output   Resulting potion type
	 */
	public CauldronBrewingRecipe(PotionType input, Ingredient reagent, PotionType output) {
		this.input = CauldronState.potion(input);
		this.reagent = reagent;
		this.output = CauldronState.potion(output);
	}

	@Override
	public boolean matches(ItemStack stack, boolean boiling, int level, CauldronState state) {
		// must have at least one level and be boiling. If 3 or more stack count must be bigger than 1
		return level > 0 && (level < 3 || stack.getCount() > 1) && boiling && state.matches(input) && reagent.apply(stack);
	}

	@Override
	public ItemStack transformInput(ItemStack stack, boolean boiling, int level, CauldronState state) {
		stack.shrink(level > 2 ? 2 : 1);
		return stack;
	}

	@Override
	public CauldronState getState(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return output;
	}

	@Override
	public List<ItemStack> getInput() {
		return ImmutableList.copyOf(reagent.getMatchingStacks());
	}

	@Override
	public int getInputLevel() {
		return 2;
	}

	@Override
	public Object getInputState() {
		PotionType potion = input.getPotion();
		return potion == PotionTypes.WATER ? FluidRegistry.WATER : potion;
	}

	@Override
	public Object getState() {
		PotionType potion = output.getPotion();
		return potion == PotionTypes.WATER ? FluidRegistry.WATER : potion;
	}

	@Override
	public boolean isBoiling() {
		return true;
	}

	@Override
	public SoundEvent getSound(ItemStack stack, boolean boiling, int level, CauldronState state) {
		return SoundEvents.BLOCK_BREWING_STAND_BREW;
	}

	@Override
	public String toString() {
		return String.format("CauldronBrewingRecipe: %s from %s",
				output.getPotion().getRegistryName(),
				input.getPotion().getRegistryName());
	}
}
