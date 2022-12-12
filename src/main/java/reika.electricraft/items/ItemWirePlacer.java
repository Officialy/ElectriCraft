///*******************************************************************************
// * @author Reika Kalseki
// *
// * Copyright 2017
// *
// * All rights reserved.
// * Distribution of the software in any form is only allowed with
// * explicit, prior permission from the owner.
// ******************************************************************************/
//package reika.electricraft.items;
//
//import java.util.List;
//
//import net.minecraft.ChatFormatting;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.InteractionResultHolder;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.CreativeModeTab;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.context.UseOnContext;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.material.Fluid;
//import org.lwjgl.input.Keyboard;
//
//import net.minecraft.block.material.Material;
//import net.minecraft.client.renderer.texture.IIconRegister;
//import net.minecraft.creativetab.CreativeTabs;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.entity.player.Player;
//import net.minecraft.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.util.AABB;
//import net.minecraft.util.EnumChatFormatting;
//
//import net.minecraftforge.fluids.Fluid;
//import net.minecraftforge.fluids.FluidRegistry;
//
//import reika.dragonapi.libraries.level.ReikaWorldHelper;
//import reika.electricraft.ElectriCraft;
//import reika.electricraft.registry.ElectriItems;
//import reika.electricraft.registry.ElectriTiles;
//import reika.electricraft.registry.WireType;
//import reika.electricraft.tileentities.BlockEntityWire;
//import reika.rotarycraft.api.interfaces.Fillable;
//
//import cpw.mods.fml.relauncher.Side;
//import cpw.mods.fml.relauncher.SideOnly;
//import reika.rotarycraft.registry.RotaryFluids;
//
//public class ItemWirePlacer extends Item implements Fillable {
//
//	public ItemWirePlacer(int tex) {
//		super();
//		this.setHasSubtypes(true);
//		this.setMaxDamage(0);
//		maxStackSize = 64;
//		this.setCreativeTab(ElectriCraft.tabElectri);
//	}
//
//	@Override
//	public InteractionResult useOn(UseOnContext context) {
//
//		if (!ReikaWorldHelper.softBlocks(context.getLevel(), context.getClickedPos()) && ReikaWorldHelper.getMaterial(context.getLevel(), context.getClickedPos()) != Material.water && ReikaWorldHelper.getMaterial(context.getLevel(), context.getClickedPos()) != Material.lava) {
//			if (side == 0)
//				--y;
//			if (side == 1)
//				++y;
//			if (side == 2)
//				--z;
//			if (side == 3)
//				++z;
//			if (side == 4)
//				--x;
//			if (side == 5)
//				++x;
//			if (!ReikaWorldHelper.softBlocks(context.getLevel(), context.getClickedPos()) && ReikaWorldHelper.getMaterial(context.getLevel(), context.getClickedPos()) != Material.water && ReikaWorldHelper.getMaterial(context.getLevel(), context.getClickedPos()) != Material.lava)
//				return false;
//		}
//		if (!this.checkValidBounds(is, ep, context.getLevel(), context.getClickedPos()))
//			return false;
//		AABB box = AABB.getBoundingBox(context.getClickedPos(), x+1, y+1, z+1);
//		List inblock = context.getLevel().getEntitiesWithinAABB(LivingEntity.class, box);
//		if (inblock.size() > 0)
//			return false;
//		if (!ep.canPlayerEdit(context.getClickedPos(), 0, is))
//			return false;
//		if (!this.canBePlaced(is))
//			return false;
//		else
//		{
//			if (!ep..isCreative())
//				--is.getCount();
//			int meta = is.getItemDamage()%WireType.INS_OFFSET;
//			context.getLevel().setBlock(context.getClickedPos(), ElectriTiles.WIRE.getBlock(), meta, 3);
//		}
//		context.getLevel().playSoundEffect(x+0.5, y+0.5, z+0.5, ElectriTiles.WIRE.getPlaceSound(), 1F, 1.5F);
//		BlockEntityWire te = (BlockEntityWire)context.getLevel().getBlockEntity(new BlockPos(context.getClickedPos()));
//		te.setPlacer(ep);
//		te.insulated = is.getItemDamage() >= WireType.INS_OFFSET;
//		return true;
//	}
//
//	protected boolean checkValidBounds(ItemStack is, Player ep, Level world, int x, int y, int z) {
//		return y > 0 && y < world.getHeight()-1;
//	}
//
//	private boolean canBePlaced(ItemStack is) {
//		if (is.getItemDamage()%WireType.INS_OFFSET == WireType.SUPERCONDUCTOR.ordinal()) {
//			return is.getTag() != null && is.getTag().getBoolean("fluid");
//		}
//		return true;
//	}
//
//	@Override
//	public void getSubItems(Item par1, CreativeModeTab par2CreativeTabs, List par3List) {
//		for (int i = 0; i < 32; i++) {
//			ItemStack item = new ItemStack(par1, 1, i);
//			if (ElectriItems.WIRE.isAvailableInCreative(item)) {
//				par3List.add(item);
//				if (i%WireType.INS_OFFSET == WireType.SUPERCONDUCTOR.ordinal()) {
//					ItemStack item2 = item.copy();
//					item2.getOrCreateTag().putBoolean("fluid", true);
//					item2.getOrCreateTag().putInt("lvl", this.getCapacity(item2));
//					par3List.add(item2);
//				}
//			}
//		}
//	}
//
//	public ItemStack getFilledSuperconductor(boolean insulated) {
//		ItemStack item2 = insulated ? WireType.SUPERCONDUCTOR.getCraftedInsulatedProduct() : WireType.SUPERCONDUCTOR.getCraftedProduct();
//		item2.getOrCreateTag().putBoolean("fluid", true);
//		item2.getOrCreateTag().putInt("lvl", this.getCapacity(item2));
//		return item2;
//	}
//
//	@Override
//	public void addInformation(ItemStack is, Player ep, List li, boolean par4) {
//		WireType type = WireType.getTypeFromWireDamage(is.getItemDamage());
//		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
//			li.add(String.format("Voltage Loss: %dV/m", type.resistance));
//			li.add(String.format("Max Current: %dA", type.maxCurrent));
//		}
//		else {
//			li.add(ChatFormatting.GREEN+"Hold shift for wire data");
//		}
//		if (type == WireType.SUPERCONDUCTOR) {
//			if (is.getTag() != null && is.getTag().getBoolean("fluid")) {
//				li.add("Filled with Coolant");
//			}
//			else
//				li.add("Cannot be placed until filled with coolant");
//		}
//	}
//
//	@Override
//	public int getMetadata(int meta) {
//		return meta;
//	}
//
//	@Override
//	public final String getUnlocalizedName(ItemStack is)
//	{
//		int d = is.getItemDamage();
//		return super.getUnlocalizedName() + "." + String.valueOf(d);
//	}
//
//	@Override
//	public boolean isValidFluid(Fluid f, ItemStack is) {
//		return this.canFill(is) ? this.isColdFluid(f) : false;
//	}
//
//	private boolean isColdFluid(Fluid f) {
//		if (f.equals(RotaryFluids.LIQUID_NITROGEN.get()))
//			return true;
////		if (f.equals(FluidRegistry.getFluid("cryotheum")))
////			return true;
//		return false;
//	}
//
//	@Override
//	public int getCapacity(ItemStack is) {
//		return 25;
//	}
//
//	@Override
//	public int getCurrentFillLevel(ItemStack is) {
//		return is.getTag() != null ? is.getTag().getInt("lvl") : 0;
//	}
//
//	@Override
//	public int addFluid(ItemStack is, Fluid f, int amt) {
//		if (this.canFill(is)) {
//			int liq = this.getCurrentFillLevel(is);
//			int added = Math.min(amt, this.getCapacity(is)-liq);
//			is.getOrCreateTag().putInt("lvl", added+liq);
//			if (this.isFull(is))
//				is.getOrCreateTag().putBoolean("fluid", true);
//			return added;
//		}
//		return 0;
//	}
//
//	@Override
//	public boolean isFull(ItemStack is) {
//		return is.getTag() != null && this.getCurrentFillLevel(is) >= this.getCapacity(is);
//	}
//
//	private boolean canFill(ItemStack is) {
//		if (is.getItemDamage()%WireType.INS_OFFSET == WireType.SUPERCONDUCTOR.ordinal()) {
//			if (!this.isFull(is)) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Override
//	public Fluid getCurrentFluid(ItemStack is) {
//		return this.getCurrentFillLevel(is) > 0 ? RotaryFluids.LIQUID_NITROGEN.get() : null;
//	}
//
//	@Override
//	public String getItemStackDisplayName(ItemStack is) {
//		ElectriItems ir = ElectriItems.getEntry(is);
//		return ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
//	}
//
//}
