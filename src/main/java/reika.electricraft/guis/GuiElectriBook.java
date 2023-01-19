///*******************************************************************************
// * @author Reika Kalseki
// *
// * Copyright 2017
// *
// * All rights reserved.
// * Distribution of the software in any form is only allowed with
// * explicit, prior permission from the owner.
// ******************************************************************************/
//package reika.electricraft.guis;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//import net.minecraft.world.item.crafting.Recipe;
//import org.lwjgl.opengl.GL11;
//
//import net.minecraft.client.renderer.tileentity.BlockEntityRendererDispatcher;
//import net.minecraft.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.item.crafting.CraftingManager;
//import net.minecraft.item.crafting.Recipe;
//import net.minecraft.world.level.block.entity.BlockEntity;
//
//
//import reika.dragonapi.libraries.io.ReikaTextureHelper;
//import reika.dragonapi.libraries.Java.ReikaJavaLibrary;
//import reika.dragonapi.libraries.rendering.ReikaGuiAPI;
//import reika.electricraft.auxiliary.ElectriDescriptions;
//import reika.electricraft.base.BlockEntityResistorBase;
//import reika.electricraft.base.BlockEntityResistorBase.ColorBand;
//import reika.electricraft.blocks.BlockChargePad;
//import reika.electricraft.registry.BatteryType;
//import reika.electricraft.registry.ElectriBook;
//import reika.electricraft.registry.ElectriTiles;
//import reika.electricraft.registry.WireType;
//import reika.electricraft.tileentities.BlockEntityWire;
//import reika.electricraft.tileentities.BlockEntityWirelessCharger.ChargerTiers;
//import reika.rotarycraft.auxiliary.HandbookAuxData;
//import reika.rotarycraft.auxiliary.interfaces.HandbookEntry;
//import reika.rotarycraft.gui.GuiHandbook;
//
//public class GuiElectriBook extends GuiHandbook {
//
//	private static final Random rand = new Random();
//
//	private ColorBand[] resistorBands = {ColorBand.BLACK, ColorBand.BLACK, ColorBand.BLACK};
//
//	private int guiTick;
//
//	public GuiElectriBook(Player p5ep, Level world, int s, int p) {
//		super(p5ep, world, s, p);
//	}
//
//	@Override
//	protected void reloadXMLData() {
//		ElectriDescriptions.reload();
//	}
//
//	@Override
//	protected void addTabButtons(int j, int k) {
//		ElectriBook.addRelevantButtons(j, k, screen, buttonList);
//	}
//
//	@Override
//	public int getMaxScreen() {
//		return ElectriBook.MODDESC.getScreen()+ElectriBook.MODDESC.getNumberChildren()/GuiHandbook.PAGES_PER_SCREEN;
//	}
//
//	@Override
//	public int getMaxSubpage() {
//		ElectriBook h = ElectriBook.getFromScreenAndPage(screen, page);
//		return h.isMachine() ? 1 : 0;
//	}
//
//	@Override
//	protected int getNewScreenByTOCButton(int id) {
//		switch(id) {
//			case 1:
//				return ElectriBook.INTRO.getScreen();
//			case 2:
//				return ElectriBook.CONVDESC.getScreen();
//			case 3:
//				return ElectriBook.TRANSDESC.getScreen();
//			case 4:
//				return ElectriBook.STORAGEDESC.getScreen();
//			case 5:
//				return ElectriBook.UTILDESC.getScreen();
//			case 6:
//				return ElectriBook.MODDESC.getScreen();
//		}
//		return 0;
//	}
//
//	@Override
//	protected boolean isOnTOC() {
//		return this.getEntry() == ElectriBook.TOC;
//	}
//
//	@Override
//	protected void drawAuxData(int posX, int posY) {
//		guiTick++;
//
//		ElectriBook h = (ElectriBook)this.getEntry();
//		if (h.isMachine()) {
//			List<ItemStack> out = h.getItems(subpage);
//			if (out == null || out.size() <= 0)
//				return;
//			if (h == ElectriBook.WIRES) {
//				out.clear();
//				WireType type = WireType.wireList[(int)(System.currentTimeMillis()/4000)%WireType.wireList.length];
//				ItemStack is = System.currentTimeMillis()%4000 >= 2000 ? type.getCraftedInsulatedProduct() : type.getCraftedProduct();
//				out.add(is);
//			}
//			List<Recipe> li = HandbookAuxData.getWorktable();
//			if (h == ElectriBook.EUBATT || h == ElectriBook.EUSPLITTER || h == ElectriBook.WIRELESSPAD)
//				li = CraftingManager.getInstance().getRecipeList();
//			ReikaGuiAPI.instance.drawCustomRecipes(ri, font, out, li, posX+72-18, posY+18, posX-1620, posY+32);
//		}
//		if (this.getGuiLayout() == PageType.CRAFTING) {
//			List<ItemStack> out = ReikaJavaLibrary.makeListFrom(h.getItem().get());
//			if (out == null || out.size() <= 0)
//				return;
//			ReikaGuiAPI.instance.drawCustomRecipes(ri, font, out, CraftingManager.getInstance().getRecipeList(), posX+72, posY+18, posX+162, posY+32);
//		}
//		/*
//		if (h == ElectriBook.MAGNET) {
//			ItemStack in = ElectriStacks.lodestone;
//			ItemStack out = ElectriItems.MAGNET.get();
//			int k = (int)((System.nanoTime()/2000000000)%ElectriItems.MAGNET.getNumberMetadatas());
//			if (k != 0) {
//				in = ElectriItems.MAGNET.getStackOfMetadata(k-1);
//				out = ElectriItems.MAGNET.getStackOfMetadata(k);
//			}
//			MachineRecipeRenderer.instance.drawCompressor(posX+66, posY+14, in, posX+120, posY+41, out);
//		}
//		if (h == ElectriBook.PELLET) {
//			ItemStack in = CraftingItems.GRAPHITE.getItem();
//			ItemStack in2 = CraftingItems.UDUST.getItem();
//			ItemStack out = ElectriItems.PELLET.get();
//			BlastCrafting r = RecipesBlastFurnace.getRecipes().getAllCraftingMaking(out).get(0);
//			MachineRecipeRenderer.instance.drawBlastFurnaceCrafting(posX+99, posY+18, posX+180, posY+32, r);
//			ReikaGuiAPI.instance.drawCenteredStringNoShadow(font, r.temperature+"C", posX+56, posY+66, 0);
//		}
//		 */
//	}
//
//	@Override
//	protected void doRenderMachine(double x, double y, HandbookEntry he) {
//		ElectriBook h = (ElectriBook)he;
//		ElectriTiles et = h.getMachine();
//		if (et != null) {
//			BlockEntity te = et.createTEInstanceForRender();
//			double sc = 48;
//			int r = (int)(System.nanoTime()/20000000)%360;
//			double a = 0;
//			double b = 0;
//			double c = 0;
//			if (et == ElectriTiles.WIRELESSPAD)
//				BlockChargePad.itemRender = ChargerTiers.tierList[(int)((System.nanoTime()/2000000000)%ChargerTiers.tierList.length)];
//			if (et.isWiring()) {
//				double dx = -x;
//				double dy = -y-21;
//				double dz = 0;
//				stack.translate(-dx, -dy, -dz);
//				GL11.glScaled(sc, -sc, sc);
//				stack.mulPose(renderq, 1, 0, 0);
//				stack.mulPose(r, 0, 1, 0);
//				stack.translate(a, b, c);
//				if (te instanceof BlockEntityWire) {
//					BlockEntityWire tw = (BlockEntityWire)te;
//					tw.setBlockMetadata((int)(System.currentTimeMillis()/4000)%WireType.wireList.length);
//					if (System.currentTimeMillis()%4000 >= 2000)
//						tw.insulated = true;
//					else
//						tw.insulated = false;
//				}
//				TileEntityRendererDispatcher.instance.renderTileEntityAt(te, -0.5, 0, -0.5, 0);
//				stack.translate(-a, -b, -c);
//				stack.mulPose(-r, 0, 1, 0);
//				stack.mulPose(-renderq, 1, 0, 0);
//				stack.translate(-dx, -dy, -dz);
//				GL11.glScaled(1D/sc, -1D/sc, 1D/sc);
//			}
//			else if (et.hasRender() && et.getBlock().getRenderType() != 0) {
//				double dx = -x;
//				double dy = -y-21;
//				double dz = 0;
//				stack.translate(-dx, -dy, -dz);
//				GL11.glScaled(sc, -sc, sc);
//				stack.mulPose(renderq, 1, 0, 0);
//				stack.mulPose(r, 0, 1, 0);
//				stack.translate(a, b, c);
//				if (te instanceof TileEntityResistorBase) {
//					TileEntityResistorBase tr = (TileEntityResistorBase)te;
//					if (guiTick%100 == 0 || resistorBands.length != tr.getColorBands().length) {
//						this.recalcResistorColors(tr);
//					}
//					for (int i = 0; i < resistorBands.length; i++)
//						tr.setColor(resistorBands[i], i+1);
//				}
//				TileEntityRendererDispatcher.instance.renderTileEntityAt(te, -0.5, 0, -0.5, 0);
//				if (te instanceof TileEntityResistorBase) {
//					TileEntityResistorBase tr = (TileEntityResistorBase)te;
//					for (int i = 0; i < resistorBands.length; i++)
//						tr.setColor(ColorBand.BLACK, i+1);
//				}
//				stack.translate(-a, -b, -c);
//				stack.mulPose(-r, 0, 1, 0);
//				stack.mulPose(-renderq, 1, 0, 0);
//				stack.translate(-dx, -dy, -dz);
//				GL11.glScaled(1D/sc, -1D/sc, 1D/sc);
//			}
//			else {
//				double dx = x;
//				double dy = y;
//				double dz = 0;
//				stack.translate(dx, dy, dz);
//				GL11.glScaled(sc, -sc, sc);
//				stack.mulPose(renderq, 1, 0, 0);
//				stack.mulPose(r, 0, 1, 0);
//				ReikaTextureHelper.bindTerrainTexture();
//				stack.translate(a, b, c);
//				int meta = et.getBlockMetadata();
//				if (et == ElectriTiles.BATTERY) {
//					meta = (int)(-1+System.currentTimeMillis()/2000)%BatteryType.batteryList.length;
//				}
//				rb.renderBlockAsItem(et.get(), meta, 1);
//				stack.translate(-a, -b, -c);
//				stack.mulPose(-r, 0, 1, 0);
//				stack.mulPose(-renderq, 1, 0, 0);
//				GL11.glScaled(1D/sc, -1D/sc, 1D/sc);
//				stack.translate(-dx, -dy, -dz);
//			}
//			BlockChargePad.itemRender = null;
//		}
//	}
//
//	private void recalcResistorColors(TileEntityResistorBase tr) {
//		ColorBand[] raw = tr.getColorBands();
//		resistorBands = new ColorBand[raw.length];
//		for (int i = 0; i < resistorBands.length; i++)
//			resistorBands[i] = ColorBand.bandList[rand.nextInt(ColorBand.bandList.length)];
//		resistorBands[resistorBands.length-1] = ColorBand.bandList[rand.nextInt(ColorBand.GRAY.ordinal())];
//	}
//
//	@Override
//	protected void drawAuxGraphics(int posX, int posY, float ptick) {
//		ElectriBook h = (ElectriBook)this.getEntry();
//		ReikaGuiAPI api = ReikaGuiAPI.instance;
//
//	}
//
//	@Override
//	protected HandbookEntry getEntry() {
//		return ElectriBook.getFromScreenAndPage(screen, page);
//	}
//
//	@Override
//	public boolean isLimitedView() {
//		return false;
//	}
//
//	@Override
//	protected PageType getGuiLayout() {
//		ElectriBook h = (ElectriBook)this.getEntry();
//		if (this.isOnTOC())
//			return PageType.TOC;
//		if (h.isParent())
//			return PageType.PLAIN;
//		if (subpage == 1)
//			return PageType.PLAIN;
//		if (h.isMachine())
//			return PageType.MACHINERENDER;
//		return PageType.PLAIN;
//	}
//
//	@Override
//	protected void bindTexture() {
//		ElectriBook h = (ElectriBook)this.getEntry();
//		/*
//		if (h == ElectriBook.FUSIONINFO) {
//			ReikaTextureHelper.bindTexture(ElectriCraft.class, "Textures/GUI/Handbook/fusion.png");
//		}
//		else if (h == ElectriBook.FISSIONINFO) {
//			ReikaTextureHelper.bindTexture(ElectriCraft.class, "Textures/GUI/Handbook/fission.png");
//		}
//		else*/
//		super.bindTexture();
//	}
//
//	@Override
//	public List<HandbookEntry> getAllTabsOnScreen() {
//		List<ElectriBook> li = ElectriBook.getEntriesForScreen(screen);
//		return new ArrayList(li);
//	}
//
//}
