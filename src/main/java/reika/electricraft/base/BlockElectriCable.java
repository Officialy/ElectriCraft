/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public abstract class BlockElectriCable extends Block implements EntityBlock {

    public BlockElectriCable(Properties properties) {
        super(properties.strength(0.5F, 15));
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

 /*   @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        double d = 0.25;
        ElectriCable te = (ElectriCable) getter.getBlockEntity(pos);
        if (te == null)
            return null;
        float minx = te.isConnectedOnSideAt((Level) getter, pos, Direction.WEST) ? 0 : 0.3F;
        float maxx = te.isConnectedOnSideAt((Level) getter, pos, Direction.EAST) ? 1 : 0.7F;
        float minz = te.isConnectedOnSideAt((Level) getter, pos, Direction.SOUTH) ? 0 : 0.3F;
        float maxz = te.isConnectedOnSideAt((Level) getter, pos, Direction.NORTH) ? 1 : 0.7F;
        float miny = te.isConnectedOnSideAt((Level) getter, pos, Direction.UP) ? 0 : 0.3F;
        float maxy = te.isConnectedOnSideAt((Level) getter, pos, Direction.DOWN) ? 1 : 0.7F;
        VoxelShape box = Block.box(pos.getX() + minx, pos.getY() + miny, pos.getZ() + minz, pos.getX() + maxx, pos.getY() + maxy, pos.getZ() + maxz);

        this.setBounds(box, pos.getX(), pos.getY(), pos.getZ());
        return box;
    }*/


    protected final void setBounds(VoxelShape box, int x, int y, int z) {
//todo		this.setBlockBounds((float)box.minX-x, (float)box.minY-y, (float)box.minZ-z, (float)box.maxX-x, (float)box.maxY-y, (float)box.maxZ-z);
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        ElectriCable te = (ElectriCable) level.getBlockEntity(pos);
        te.recomputeConnections((Level) level, pos); //todo bad cast possibly?
    }

    @Override
    public void onPlace(BlockState p_60566_, Level world, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        ElectriCable te = (ElectriCable) world.getBlockEntity(pos);
        te.addToAdjacentConnections(world, pos);
        te.recomputeConnections(world, pos);
    }

}
