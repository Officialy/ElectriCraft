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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import reika.dragonapi.interfaces.registry.OreEnum;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;
import reika.dragonapi.libraries.java.ReikaStringParser;
import reika.dragonapi.libraries.registry.ReikaItemHelper;
import reika.electricraft.ElectriCraft;

public enum ElectriOres implements OreEnum {

    COPPER(32, 64, 6, 8, Level.OVERWORLD, 0.5F, 1, "ore.copper"),
    TIN(48, 72, 8, 8, Level.OVERWORLD, 0.2F, 1, "ore.tin"),
    SILVER(0, 32, 6, 4, Level.OVERWORLD, 0.8F, 2, "ore.silver"),
    NICKEL(16, 48, 8, 6, Level.OVERWORLD, 0.3F, 1, "ore.nickel"),
    ALUMINUM(60, 128, 8, 10, Level.OVERWORLD, 0.4F, 1, "ore.aluminum"),
    PLATINUM(0, 16, 4, 2, Level.OVERWORLD, 1F, 2, "ore.platinum");

    public final int minY;
    public final int maxY;
    public final int veinSize;
    public final int perChunk;
    public final ResourceKey<Level> dimensionID;
    public final String oreName;
    public final int harvestLevel;
    public final float xpDropped;

    public static final ElectriOres[] oreList = values();

    private static final EnumMap<ElectriOres, Boolean> equivalents = new EnumMap<>(ElectriOres.class);

    ElectriOres(int min, int max, int size, int count, ResourceKey<Level> dim, float xp, int level, String name) {
        minY = min;
        maxY = max;
        veinSize = size;
        perChunk = count;
        dimensionID = dim;
        oreName = I18n.get(name);
        harvestLevel = level;
        xpDropped = xp;
    }

    @Override
    public String toString() {
        return this.name() + " " + perChunk + "x" + veinSize + " between " + minY + " and " + maxY;
    }

    public static ElectriOres getOre(BlockGetter iba, BlockPos pos) {
        Block id = iba.getBlockState(pos).getBlock();

        if (id != ElectriBlocks.ORE.get())
            return null;
        return oreList[1]; //todo int was meta
    }

    public static ElectriOres getOre(Block id) {
        if (id != ElectriBlocks.ORE.get())
            return null;
        return 2 < oreList.length ? oreList[2] : null; //todo number was meta
    }

    public String getTextureName() {
        return "ElectriCraft:ore" + ReikaStringParser.capFirstChar(this.name());
    }

    public String getDictionaryName() {
        return "ore" + ReikaStringParser.capFirstChar(this.name());
    }

    public String getProductDictionaryName() {
        return "ingot" + ReikaStringParser.capFirstChar(this.name());
    }

    public Block getBlock() {
        return ElectriBlocks.ORE.get();
    }

    @Override
    public BlockEntity getBlockEntity(Level world, BlockPos pos) {
        return null;
    }

    @Override
    public boolean canGenAt(Level world, BlockPos pos) {
        return false;
    }

    public ItemStack getOreBlock() {
        return new ItemStack(this.getBlock(), 1);
    }

    public ItemStack getProduct() {
        return ElectriItems.INGOTS.get().getDefaultInstance();//getStackOfMetadata(this.ordinal());
    }

    public List<ItemStack> getOreDrop() {
        return ReikaJavaLibrary.makeListFrom(new ItemStack(ElectriBlocks.ORE.get(), 1));
    }

    public String getProductName() {
        return I18n.get("Items." + this.name().toLowerCase(Locale.ENGLISH));
    }

    public Block getReplaceableBlock() {
        return switch (dimensionID.registry().getPath()) {
            case "0" -> Blocks.STONE;
            case "the_end" -> Blocks.END_STONE;
            case "the_nether" -> Blocks.NETHERRACK;
//            case 7 -> Blocks.STONE;
            default -> Blocks.STONE;
        };
    }

    public boolean isValidDimension(ResourceKey<Level> id) {
        if (id == dimensionID)
            return true;
//        if (id == ReikaTwilightHelper.getDimensionID() && dimensionID == 0)
//            return true;
        return dimensionID == Level.OVERWORLD && id != Level.NETHER && id != Level.END;
    }

    public boolean isValidBiome(Biome biome) {
        return true;
    }

    public boolean canGenerateInChunk(Level world, int chunkX, int chunkZ) {
        ResourceKey<Level> id = world.dimension();
        if (!this.shouldGen())
            return false;
        if (!this.isValidDimension(id))
            return false;
        return this.isValidBiome(world.getBiome(new BlockPos(chunkX << 4,0, chunkZ << 4)).value());// || id == ReikaTwilightHelper.getDimensionID();
    }

    private boolean shouldGen() {
        return true;
    }

    public boolean canGenAt(Level world, int x, int y, int z) {
        return this.isOreEnabled();
    }

    public boolean isOreEnabled() {
//       todo if (ElectriCraft.config.isOreGenEnabled(this))
//            return true;
//	todo	if (ModList.CONDENSEDORES.isLoaded() && CondensedOreAPI.instance.doesBlockGenerate(this.getBlock(), this.getBlockMetadata()))
//			return false;
        return !this.hasEquivalents();
    }

    public boolean hasEquivalents() {
        Boolean b = equivalents.get(this);
        if (b == null) {
            ArrayList<ItemStack> li = null;//todo OreDictionary.getOres(this.getDictionaryName());
            boolean flag = false;
            for (int i = 0; i < li.size() && !flag; i++) {
                ItemStack is = li.get(i);
                if (!ReikaItemHelper.matchStacks(is, this.getOreBlock())) {
                    b = true;
                    flag = true;
                }
            }
            b = flag;
            equivalents.put(this, b);
        }
        return b.booleanValue();
    }

    public boolean dropsSelf() {
        List<ItemStack> li = this.getOreDrop();
        return li.size() == 1 && ReikaItemHelper.matchStackWithBlock(li.get(0), ElectriBlocks.ORE.get().defaultBlockState());
    }

    @Override
    public int getHarvestLevel() {
        return harvestLevel;
    }

    @Override
    public String getHarvestTool() {
        return "pickaxe";
    }

    @Override
    public float getXPDropped(Level world, BlockPos pos) {
        return xpDropped;
    }

    @Override
    public boolean dropsSelf(Level world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean enforceHarvestLevel() {
        return false;
    }

    @Override
    public int getRandomGeneratedYCoord(Level world, int posX, int posZ, Random random) {
        return minY + random.nextInt(maxY - minY + 1);
    }
}
