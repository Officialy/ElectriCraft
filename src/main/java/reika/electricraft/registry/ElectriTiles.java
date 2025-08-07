/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import reika.dragonapi.DragonAPI;
import reika.dragonapi.modregistry.PowerTypes;
import reika.dragonapi.exception.RegistrationException;
import reika.dragonapi.instantiable.data.maps.BlockMap;
import reika.dragonapi.interfaces.registry.TileEnum;
import reika.electricraft.ElectriCraft;
import reika.electricraft.base.BlockEntityResistorBase;
import reika.electricraft.base.BlockEntityWireComponent;
import reika.electricraft.base.WiringTile;
import reika.electricraft.blockentities.BlockEntityBattery;
import reika.electricraft.blockentities.BlockEntityFuse;
import reika.electricraft.blockentities.BlockEntityGenerator;
import reika.electricraft.blockentities.BlockEntityMeter;
import reika.electricraft.blockentities.BlockEntityMotor;
import reika.electricraft.blockentities.BlockEntityPreciseResistor;
import reika.electricraft.blockentities.BlockEntityRelay;
import reika.electricraft.blockentities.BlockEntityResistor;
import reika.electricraft.blockentities.BlockEntityTransformer;
import reika.electricraft.blockentities.BlockEntityWire;
import reika.electricraft.blockentities.BlockEntityWirelessCharger;
import reika.electricraft.blockentities.modinterface.BlockEntityRFBattery;
import reika.electricraft.blockentities.modinterface.BlockEntityRFCable;
import reika.rotarycraft.auxiliary.interfaces.NBTMachine;

public enum ElectriTiles implements TileEnum {

    WIRE("electri.wire", ElectriBlocks.WIRE.get(), BlockEntityWire.class, "RenderWire"),
    GENERATOR("machine.electrigenerator", ElectriBlocks.GENERATOR.get(), BlockEntityGenerator.class, "RenderGenerator"),
    MOTOR("machine.electrimotor", ElectriBlocks.MOTOR.get(), BlockEntityMotor.class, "RenderMotor"),
    RESISTOR("machine.electriresistor", ElectriBlocks.RESISTOR.get(), BlockEntityResistor.class, "RenderResistor"),
    RELAY("machine.electrirelay", ElectriBlocks.RELAY.get(), BlockEntityRelay.class, "RenderRelay"),
    BATTERY("machine.electribattery", ElectriBlocks.BATTERY.get(), BlockEntityBattery.class),
    RF_CABLE("machine.rfcable", ElectriBlocks.RF_CABLE.get(), BlockEntityRFCable.class, "RenderCable"),
    METER("machine.wiremeter", ElectriBlocks.METER.get(), BlockEntityMeter.class, "RenderElectricMeter"),
    RFBATTERY("machine.rfbattery", ElectriBlocks.RFBATTERY.get(), BlockEntityRFBattery.class, "RenderModBattery"),
    TRANSFORMER("machine.transformer", ElectriBlocks.TRANSFORMER.get(), BlockEntityTransformer.class, "RenderTransformer"),
    //	EUSPLIT("machine.eusplit", 				ElectriBlocks.EUSPLIT, 		BlockEntityEUSplitter.class, 	0),
//	EUCABLE("machine.eucable", 				ElectriBlocks.EUCABLE, 		BlockEntityEUCable.class, 		0, "RenderCable"),
//	EUBATTERY("machine.eubattery", 			ElectriBlocks.EUBATTERY, 	BlockEntityEUBattery.class, 		0, "RenderModBattery"),
    FUSE("machine.fuse", ElectriBlocks.FUSE.get(), BlockEntityFuse.class, "RenderFuse"),
    WIRELESSPAD("machine.wirelesspad", ElectriBlocks.WIRELESS_CHARGER.get(), BlockEntityWirelessCharger.class),
    PRECISERESISTOR("machine.precresistor", ElectriBlocks.PRECISE_RESISTOR.get(), BlockEntityPreciseResistor.class, "RenderPreciseResistor");

    private final String name;
    private final Class<? extends BlockEntity> teClass;
    private final String render;
    private final Block blockInstance;
    private BlockEntity renderInstance;

    private static final BlockMap<ElectriTiles> machineMappings = new BlockMap<>();

    public static final ElectriTiles[] teList = values();

    ElectriTiles(String n, Block block, Class<? extends BlockEntity> tile) {
        this(n, block, tile, null);
    }

    ElectriTiles(String n, Block block, Class<? extends BlockEntity> tile, String r) {
        teClass = tile;
        name = n;
        render = r;
        blockInstance = block;
    }

    public String getName() {
        return I18n.get(name);
    }

    @Override
    public BlockState getBlockState() {
        return getBlock().defaultBlockState();
    }

    public Class getTEClass() {
        return teClass;
    }

    public static ArrayList<ElectriTiles> getTilesOfBlock(Block b) {
        ArrayList li = new ArrayList<>();
        for (ElectriTiles electriTiles : teList) {
            if (electriTiles.blockInstance == b)
                li.add(electriTiles);
        }
        return li;
    }

/*	public static BlockEntity createTEFromIDAndMetadata(Block id) {
//	todo	if (id == ElectriBlocks.WIRE.get())
//			return new BlockEntityWire();
		ElectriTiles index = getMachineFromIDandMetadata(id);
		if (index == null) {
			ElectriCraft.LOGGER.error("ID "+id+" are not a valid machine identification pair!");
			return null;
		}
		Class TEClass = index.teClass;
		try {
			return (BlockEntity)TEClass.newInstance();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
			throw new RegistrationException(ElectriCraft.instance, "ID "+id+" afailed to instantiate its BlockEntity of "+TEClass);
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RegistrationException(ElectriCraft.instance, "ID "+id+" failed illegally accessed its BlockEntity of "+TEClass);
		}
	}*/

//	public static ElectriTiles getMachineFromIDandMetadata(Block id) {
//		return machineMappings.get(id);
//	}

    public boolean isAvailableInCreativeInventory() {
        if (this == RF_CABLE || this == RFBATTERY)
            return PowerTypes.RF.isLoaded();
//		if (this == EUCABLE || this == EUBATTERY)
//			return PowerTypes.EU.isLoaded();
        return true;
    }

    public boolean hasCustomItem() {
        return this == WIRE || this == BATTERY || this == RFBATTERY;// || this == EUBATTERY;
    }

    public boolean isWiring() {
        return this == WIRE || this == RF_CABLE;// || this == EUCABLE;
    }

    public boolean isResistor() {
        return BlockEntityResistorBase.class.isAssignableFrom(teClass);
    }

    public static ElectriTiles getTE(BlockGetter iba, BlockPos pos) {
        Block id = iba.getBlockState(pos).getBlock();
        if (id == ElectriBlocks.WIRE.get())
            return WIRE;
        return null;//todo getMachineFromIDandMetadata(id);
    }

    public ItemStack getCraftedProduct(BlockEntity te) {
        if (this == WIRE) {
            BlockEntityWire tw = (BlockEntityWire) te;
            ItemStack is = tw.insulated ? tw.getWireType().getCraftedInsulatedProduct() : tw.getWireType().getCraftedProduct();
			/*if (tw.getWireType() == WireType.SUPERCONDUCTOR) {
				is = ((ItemWirePlacer)is.getItem()).getFilledSuperconductor(tw.insulated);
			}*/
            return is;
        } else if (this == BATTERY) {
            return ((BlockEntityBattery) te).getBatteryType().getCraftedProduct();
        }
        return this.getCraftedProduct();
    }

    public ItemStack getCraftedProduct() {
		if (this == WIRE) {
//			return new ItemStack(ElectriItems.WIRE.get());
		}
		else
        if (this == BATTERY) {
            return new ItemStack(ElectriItems.BATTERY.get());
        } else if (this == RFBATTERY) {
            return new ItemStack(ElectriItems.RFBATTERY.get());
        }
//	todo	else if (this == EUBATTERY) {
//			return new ItemStack(ElectriItems.EUBATTERY.get());
//		}
//		else
//			return new ItemStack(ElectriItems.PLACER.get(), 1, this.ordinal());
        return ItemStack.EMPTY;
    }

    public BlockEntity createTEInstanceForRender() {
        if (renderInstance == null) {
            try {
                renderInstance = teClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                throw new RegistrationException(ElectriCraft.instance, "Could not create TE instance to render " + this);
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return renderInstance;
    }

    public boolean hasRender() {
        return render != null;
    }

    public String getRenderer() {
        if (!this.hasRender())
            throw new RuntimeException("Machine " + name + " has no render to call!");
        return "reika.electricraft.Renders." + render;
    }

    public Block getBlock() {
        return this.get();
    }

    public Block get() {
        return blockInstance;
    }

    public boolean renderInPass1() {
        return this == ElectriTiles.TRANSFORMER;
    }

    public boolean isDummiedOut() {
//		if (DragonAPI.isReikasComputer())
//			return false;
//		if (this == EUSPLIT && !PowerTypes.EU.isLoaded())
//			return true;
        return false;
    }

/*	public void addRecipe(Recipe ir) {
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(ir, RecipeLevel.CORE);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ir);
			}
		}
	}

	public void addRecipe(ItemStack is, Object... obj) {
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(is, RecipeLevel.CORE, obj);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(is, obj);
			}
		}
	}

	public void addCrafting(Object... obj) {
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(this.getCraftedProduct(), RecipeLevel.CORE, obj);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(this.getCraftedProduct(), obj);
			}
		}
	}

	public void addSizedOreCrafting(int size, Object... obj) {
		ItemStack is = this.getCraftedProduct();
		ShapedRecipe ir = new ShapedRecipe(ReikaItemHelper.getSizedItemStack(is, size), obj);
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(ir, RecipeLevel.CORE);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ir);
			}
		}
	}

	public void addSizedOreNBTCrafting(int size, CompoundTag tag, Object... obj) {
		ItemStack is = this.getCraftedProduct();
		is.put(tag.copy());
		ShapedRecipe ir = new ShapedRecipe(ReikaItemHelper.getSizedItemStack(is, size), obj);
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(ir, RecipeLevel.CORE);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ir);
			}
		}
	}

	public void addOreCrafting(Object... obj) {
		ItemStack is = this.getCraftedProduct();
		ShapedRecipe ir = new ShapedRecipe(is, obj);
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(ir, RecipeLevel.CORE);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ir);
			}
		}
	}

	public void addSizedCrafting(int num, Object... obj) {
		if (!this.isDummiedOut()) {
			WorktableRecipes.getInstance().addRecipe(ReikaItemHelper.getSizedItemStack(this.getCraftedProduct(), num), RecipeLevel.CORE, obj);
			if (ConfigRegistry.TABLEMACHINES.getState()) {
				GameRegistry.addRecipe(ReikaItemHelper.getSizedItemStack(this.getCraftedProduct(), num), obj);
			}
		}
	}*/

    public static void loadMappings() {
        for (int i = 0; i < ElectriTiles.teList.length; i++) {
            ElectriTiles r = ElectriTiles.teList[i];
            Block id = r.getBlock();
            machineMappings.put(id, r);
        }
    }

    public String getPlaceSound() {
        return switch (this) {
            case WIRE, RF_CABLE -> "step.cloth";
            default -> "step.stone";
        };
    }

    public boolean isSpecialWiringPiece() {
        return BlockEntityWireComponent.class.isAssignableFrom(teClass);
    }

    public boolean isWiringPiece() {
        return WiringTile.class.isAssignableFrom(teClass);
    }

    public static ElectriTiles getMachine(ItemStack item) {
//		if (item.getItem() == ElectriItems.WIRE.get())
//			return WIRE;
        if (item.getItem() == ElectriItems.BATTERY.get())
            return BATTERY;
        if (item.getItem() == ElectriItems.RFBATTERY.get())
            return RFBATTERY;
//		if (item.getItem() == ElectriItems.EUBATTERY.get())
//			return EUBATTERY;
        return teList[1];//todo item.getItemDamage()];
    }

    public boolean hasNBTVariants() {
        return NBTMachine.class.isAssignableFrom(teClass);
    }

    public boolean is6Sided() {
        return this == WIRELESSPAD;
    }
}
