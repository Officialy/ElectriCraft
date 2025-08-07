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
//import java.util.ArrayList;
//import java.util.List;
//
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//
//
//import net.minecraft.world.phys.AABB;
//import reika.dragonapi.libraries.ReikaDirectionHelper;
//import reika.dragonapi.libraries.ReikaEntityHelper;
//import reika.dragonapi.libraries.level.ReikaWorldHelper;
//import reika.electricraft.ElectriCraft;
//import reika.electricraft.auxiliary.interfaces.ConversionTile;
//import reika.electricraft.base.ElectriBlockEntity;
//import reika.electricraft.base.BlockEntityWireComponent;
//import reika.electricraft.registry.ElectriItems;
//import reika.electricraft.registry.ElectriTiles;
//import reika.electricraft.tileentities.BlockEntityTransformer;
//import reika.rotarycraft.auxiliary.RotaryAux;
//import reika.rotarycraft.auxiliary.interfaces.NBTMachine;
//
//public class ItemElectriPlacer extends Item {
//
//	public ItemElectriPlacer(int tex) {
//		super();
//		this.setHasSubtypes(true);
//		this.setMaxDamage(0);
//		maxStackSize = 64;
//		this.setCreativeTab(ElectriCraft.tabElectri);
//	}
//
//	@Override
//	public boolean onItemUse(ItemStack is, Player ep, Level world, int x, int y, int z, int side, float par8, float par9, float par10) {
//		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava) {
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
//			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava)
//				return false;
//		}
//		if (!this.checkValidBounds(is, ep, world, x, y, z))
//			return false;
//		AABB box = AABB.getBoundingBox(x, y, z, x+1, y+1, z+1);
//		List inblock = world.getEntitiesWithinAABB(LivingEntity.class, box);
//		if (inblock.size() > 0)
//			return false;
//		ElectriTiles m = ElectriTiles.TEList[is.getItemDamage()];
//		if (m == ElectriTiles.MOTOR && (ElectriTiles.getTE(world, x, y+1, z) == m || ElectriTiles.getTE(world, x, y-1, z) == m))
//			return false;
//		if (!ep.canPlayerEdit(x, y, z, 0, is))
//			return false;
//		else {
//			if (!ep..isCreative())
//				--is.getCount();
//			world.setBlock(x, y, z, m.getBlock(), this.getMeta(m, is), 3);
//		}
//		world.playSoundEffect(x+0.5, y+0.5, z+0.5, m.getPlaceSound(), 1F, 1.5F);
//		ElectriBlockEntity te = (ElectriBlockEntity)world.getBlockEntity(new BlockPos(x, y, z));
//		te.setPlacer(ep);
//		te.setBlockMetadata(m.is6Sided() ? RotaryAux.get6SidedMetadataFromPlayerLook(ep) : RotaryAux.get4SidedMetadataFromPlayerLook(ep));
//		te.isFlipped = RotaryAux.shouldSetFlipped(world, x, y, z);
//		if (te instanceof ShaftMachine) {
//			ShaftMachine sm = (ShaftMachine)te;
//			sm.setIORenderAlpha(512);
//		}
//		if (te instanceof NBTMachine) {
//			((NBTMachine)te).setDataFromItemStackTag(is.stackTagCompound);
//		}
//		if (te instanceof BlockEntityWireComponent) {
//			Direction dir = ReikaEntityHelper.getDirectionFromEntityLook(ep, false);
//			//ReikaJavaLibrary.pConsole(dir+":"+te, Dist.DEDICATED_SERVER);
//			((BlockEntityWireComponent)te).setFacing(dir);
//		}
//		if (te instanceof BlockEntityTransformer) {
//			Direction dir = ReikaDirectionHelper.getRightBy90(ReikaEntityHelper.getDirectionFromEntityLook(ep, false));
//			//ReikaJavaLibrary.pConsole(dir+":"+te, Dist.DEDICATED_SERVER);
//			((TileEntityTransformer)te).setFacing(dir);
//		}
//		if (te instanceof ConversionTile) {
//			Direction dir = ReikaEntityHelper.getDirectionFromEntityLook(ep, false);
//			((ConversionTile)te).setFacing(dir);
//		}
//
//		return true;
//	}
//
//	private int getMeta(ElectriTiles m, ItemStack is) {
//		if (m == ElectriTiles.WIRELESSPAD) {
//			int tier = is.stackTagCompound != null ? is.stackTagCompound.getInt("tier") : 0;
//			return tier;
//		}
//		return m.getBlockMetadata();
//	}
//
//	@Override
//	@SideOnly(Dist.CLIENT)
//	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
//		//ReikaJavaLibrary.pConsole("L="+ElectriTiles.TEList.length);
//		for (int i = 0; i < ElectriTiles.TEList.length; i++) {
//			//ReikaJavaLibrary.pConsole("i="+i);
//			ElectriTiles t = ElectriTiles.TEList[i];
//			if (!t.hasCustomItem() && t.isAvailableInCreativeInventory()) {
//				BlockEntity te = t.createTEInstanceForRender();
//				ItemStack item = t.getCraftedProduct();
//				if (t == ElectriTiles.WIRELESSPAD) {
//					for (int k = 0; k < TileEntityWirelessCharger.ChargerTiers.tierList.length; k++) {
//						ItemStack is = item.copy();
//						is.stackTagCompound = new CompoundTag();
//						is.stackTagCompound.putInt("tier", k);
//						par3List.add(is);
//					}
//					continue;
//				}
//				else if (t.hasNBTVariants()) {
//					ArrayList<CompoundTag> li = ((NBTMachine)te).getCreativeModeVariants();
//					for (int k = 0; k < li.size(); k++) {
//						CompoundTag NBT = li.get(k);
//						ItemStack is = item.copy();
//						is.stackTagCompound = NBT;
//						par3List.add(is);
//					}
//				}
//				par3List.add(item);
//			}
//		}
//	}
//
//	@SideOnly(Dist.CLIENT)
//	@Override
//	public void addInformation(ItemStack is, Player ep, List li, boolean verbose) {
//		int i = is.getItemDamage();
//		ElectriTiles m = ElectriTiles.TEList[i];
//		BlockEntity te = m.createTEInstanceForRender();
//		if (m.hasNBTVariants() && is.stackTagCompound != null) {
//			li.addAll(((NBTMachine)te).getDisplayTags(is.stackTagCompound));
//		}
//		if (m == ElectriTiles.WIRELESSPAD && is.stackTagCompound != null) {
//			li.add(TileEntityWirelessCharger.ChargerTiers.tierList[is.stackTagCompound.getInt("tier")].getLocalizedName());
//		}
//	}
//
//	protected boolean checkValidBounds(ItemStack is, Player ep, Level world, int x, int y, int z) {
//		return y > 0 && y < world.provider.getHeight()-1;
//	}
//
//	@Override
//	public int getMetadata(int meta) {
//		return meta;
//	}
//
//	@Override
//	public String getItemStackDisplayName(ItemStack is) {
//		ElectriItems ir = ElectriItems.getEntry(is);
//		return ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
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
//	public final void registerIcons(IIconRegister ico) {}
//
//}
