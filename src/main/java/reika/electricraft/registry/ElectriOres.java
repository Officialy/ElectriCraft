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

import net.minecraft.network.chat.Component;
import reika.dragonapi.libraries.level.LegacyOreVeins;
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

    ElectriOres(int min, int max, int size, int count, ResourceKey<Level> dim, float xp, int level, String name) {
        minY = min;
        maxY = max;
        veinSize = size;
        perChunk = count;
        dimensionID = dim;
        oreName = Component.translatable(name).getString();
        harvestLevel = level;
        xpDropped = xp;
    }

    @Override
    public String toString() {
        return this.name() + " " + perChunk + "x" + veinSize + " between " + minY + " and " + maxY;
    }

    public static ElectriOres getOre(BlockGetter world, BlockPos pos) {
        return getOre(world.getBlockState(pos).getBlock());
    }

    public static ElectriOres getOre(Block block) {
        for (ElectriOres ore : oreList)
            if (block == ore.getBlock() || block == ore.getDeepslateBlock()) return ore;
        return null;
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
        return switch (this) { //was ELECTRIORE metadata in 1.7.10; copper is vanilla now
            case COPPER -> net.minecraft.world.level.block.Blocks.COPPER_ORE;
            case TIN -> ElectriBlocks.TIN_ORE.get();
            case SILVER -> ElectriBlocks.SILVER_ORE.get();
            case NICKEL -> ElectriBlocks.NICKEL_ORE.get();
            case ALUMINUM -> ElectriBlocks.ALUMINUM_ORE.get();
            case PLATINUM -> ElectriBlocks.PLATINUM_ORE.get();
        };
    }

    /** Deepslate variant for ores whose range dips below y=0; null when none exists. */
    public Block getDeepslateBlock() {
        return switch (this) {
            case COPPER -> net.minecraft.world.level.block.Blocks.DEEPSLATE_COPPER_ORE;
            case SILVER -> ElectriBlocks.DEEPSLATE_SILVER_ORE.get();
            case NICKEL -> ElectriBlocks.DEEPSLATE_NICKEL_ORE.get();
            case PLATINUM -> ElectriBlocks.DEEPSLATE_PLATINUM_ORE.get();
            default -> null;
        };
    }

    @Override
    public BlockEntity getBlockEntity(Level world, BlockPos pos) {
        return null;
    }

    @Override
    public boolean canGenAt(Level world, BlockPos pos) {
        return isOreEnabled() && isValidDimension(world.dimension());
    }

    public ItemStack getOreBlock() {
        return new ItemStack(this.getBlock(), 1);
    }

    public ItemStack getProduct() {
        return new ItemStack(this.getProductItem());
    }

    /** Registry-only lookup also works during 26.3 datagen, before item components are bound. */
    public net.minecraft.world.item.Item getProductItem() {
        return switch (this) { //was one metadata item in 1.7.10; copper is vanilla now
            case COPPER -> net.minecraft.world.item.Items.COPPER_INGOT;
            case TIN -> ElectriItems.TIN_INGOT.get();
            case SILVER -> ElectriItems.SILVER_INGOT.get();
            case NICKEL -> ElectriItems.NICKEL_INGOT.get();
            case ALUMINUM -> ElectriItems.ALUMINUM_INGOT.get();
            case PLATINUM -> ElectriItems.PLATINUM_INGOT.get();
        };
    }

    public List<ItemStack> getOreDrop() {
        return List.of(new ItemStack(this == COPPER ? net.minecraft.world.item.Items.RAW_COPPER : getBlock().asItem()));
    }

    public String getProductName() {
        return Component.translatable("Items." + this.name().toLowerCase(Locale.ENGLISH)).getString();
    }

    public Block getReplaceableBlock() { return Blocks.STONE; }

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

    private boolean shouldGen() { return this != COPPER && isOreEnabled(); }

    public boolean canGenAt(Level world, int x, int y, int z) { return canGenAt(world,new BlockPos(x,y,z)); }

    /** A true option forces generation; false falls back to the original equivalent-ore rule. */
    public boolean isOreEnabled() {
        return this != COPPER && (ElectriCraft.config.isOreGenEnabled(this) || !hasEquivalents());
    }

    public boolean hasEquivalents() {
        return LegacyOreVeins.hasEquivalent(getBlock(),name().toLowerCase(Locale.ROOT),getDeepslateBlock());
    }

    public boolean dropsSelf() { return this != COPPER; }

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
        return dropsSelf();
    }

    @Override
    public boolean enforceHarvestLevel() {
        return true;
    }

    @Override
    public int getRandomGeneratedYCoord(Level world, int posX, int posZ, Random random) {
        return minY + random.nextInt(maxY - minY + 1);
    }
}
