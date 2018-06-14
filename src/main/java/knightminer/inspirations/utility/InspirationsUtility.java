package knightminer.inspirations.utility;

import com.google.common.eventbus.Subscribe;

import knightminer.inspirations.common.CommonProxy;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.common.PulseBase;
import knightminer.inspirations.utility.block.BlockBricksButton;
import knightminer.inspirations.utility.block.BlockCarpetedPressurePlate;
import knightminer.inspirations.utility.block.BlockCarpetedPressurePlate.BlockCarpetedPressurePlate2;
import knightminer.inspirations.utility.block.BlockCarpetedTrapdoor;
import knightminer.inspirations.utility.block.BlockRedstoneBarrel;
import knightminer.inspirations.utility.block.BlockRedstoneCharge;
import knightminer.inspirations.utility.block.BlockRedstoneTorchLever;
import knightminer.inspirations.utility.block.BlockTorchLever;
import knightminer.inspirations.utility.item.ItemRedstoneCharger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Bootstrap;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import slimeknights.mantle.pulsar.pulse.Pulse;

@Pulse(id = InspirationsUtility.pulseID, description = "Adds various utilities")
public class InspirationsUtility extends PulseBase {
	public static final String pulseID = "InspirationsUtility";

	@SidedProxy(clientSide = "knightminer.inspirations.utility.UtilityClientProxy", serverSide = "knightminer.inspirations.common.CommonProxy")
	public static CommonProxy proxy;

	// blocks
	public static Block redstoneCharge;
	public static Block torchLever;
	public static Block redstoneBarrel;
	public static BlockBricksButton bricksButton;
	public static Block redstoneTorchLever;
	public static Block redstoneTorchLeverPowered;
	public static Block[] carpetedTrapdoors;
	public static Block carpetedPressurePlate1;
	public static Block carpetedPressurePlate2;

	// items
	public static Item redstoneCharger;

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
	}

	@SubscribeEvent
	public void registerBlocks(Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();

		if(Config.enableTorchLever) {
			torchLever = registerBlock(r, new BlockTorchLever(), "torch_lever");
		}

		if(Config.enableRedstoneCharge) {
			redstoneCharge = registerBlock(r, new BlockRedstoneCharge(), "redstone_charge");
		}

		if(Config.enableBricksButton) {
			bricksButton = registerBlock(r, new BlockBricksButton(), "bricks_button");
		}
		if(Config.enableRedstoneBarrel) {
			redstoneBarrel = registerBlock(r, new BlockRedstoneBarrel(), "redstone_barrel");
		}
		if(Config.enableRedstoneTorchLever) {
			redstoneTorchLever = registerBlock(r, new BlockRedstoneTorchLever(false), "redstone_torch_lever");
			redstoneTorchLeverPowered = registerBlock(r, new BlockRedstoneTorchLever(true), "redstone_torch_lever_powered");
		}
		if(Config.enableCarpetedTrapdoor) {
			carpetedTrapdoors = new Block[16];
			for(EnumDyeColor color : EnumDyeColor.values()) {
				carpetedTrapdoors[color.getMetadata()] = registerBlock(r, new BlockCarpetedTrapdoor(), "carpeted_trapdoor_" + color.getName());
			}
		}
		if(Config.enableCarpetedPressurePlate) {
			carpetedPressurePlate1 = registerBlock(r, new BlockCarpetedPressurePlate(false), "carpeted_pressure_plate_1");
			carpetedPressurePlate2 = registerBlock(r, new BlockCarpetedPressurePlate2(), "carpeted_pressure_plate_2");
		}
	}

	@SubscribeEvent
	public void registerItems(Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();

		if(Config.enableRedstoneCharge) {
			redstoneCharger = registerItem(r, new ItemRedstoneCharger(), "redstone_charger");
		}

		// itemblocks
		if(torchLever != null) {
			registerItemBlock(r, torchLever);
		}
		if(bricksButton != null) {
			registerEnumItemBlock(r, bricksButton);
		}
		if(redstoneBarrel != null) {
			registerItemBlock(r, redstoneBarrel);
		}
		if(redstoneTorchLever != null) {
			registerItemBlock(r, redstoneTorchLever);
		}
		if(carpetedTrapdoors != null) {
			for(Block trapdoor : carpetedTrapdoors) {
				registerItemBlock(r, trapdoor);
			}
		}
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		proxy.init();

		registerDispenserBehavior();
	}

	@Subscribe
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		MinecraftForge.EVENT_BUS.register(UtilityEvents.class);
	}

	private void registerDispenserBehavior() {
		registerDispenserBehavior(redstoneCharger, new Bootstrap.BehaviorDispenseOptional() {
			@Override
			protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
				this.successful = true;
				World world = source.getWorld();
				EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
				BlockPos pos = source.getBlockPos().offset(facing);

				if (redstoneCharge.canPlaceBlockAt(world, pos)) {
					world.setBlockState(pos, redstoneCharge.getDefaultState().withProperty(BlockRedstoneCharge.FACING, facing));

					if (stack.attemptDamageItem(1, world.rand, null)) {
						stack.setCount(0);
					}
				} else {
					this.successful = false;
				}

				return stack;
			}
		});
	}
}
