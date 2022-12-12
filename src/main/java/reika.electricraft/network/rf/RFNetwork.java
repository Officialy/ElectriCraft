/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft.network.rf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.IEnergyStorage;
import reika.dragonapi.instantiable.data.immutable.WorldLocation;
import reika.electricraft.ElectriCraft;
import reika.electricraft.ElectriNetworkManager;
import reika.electricraft.NetworkObject;
import reika.electricraft.auxiliary.ElectriNetworkEvent.ElectriNetworkRepathEvent;
import reika.electricraft.auxiliary.ElectriNetworkEvent.ElectriNetworkTickEvent;
import reika.electricraft.blockentities.modinterface.BlockEntityRFCable;

public class RFNetwork implements NetworkObject {

    private final Collection<BlockEntityRFCable> cables = new ArrayList<>();
    private final HashMap<WorldLocation, EnergyInteraction> endpoints = new HashMap<>();
    private int energy = 0;
    private int networkLimit;
    private boolean disabled;
    private int tick;

    public void setIOLimit(int limit) {
        if (networkLimit != limit) {
            networkLimit = limit;
            for (BlockEntityRFCable cable : cables) {
                if (cable.getRFLimit() != networkLimit) {
                    cable.setRFLimit(networkLimit);
                }
            }
        }
        //ReikaJavaLibrary.pConsole("L:"+limit);
        //Thread.dumpStack();
    }

    public int getIOLimit() {
        return networkLimit;
    }

    public RFNetwork() {
        ElectriNetworkManager.instance.addNetwork(this);
    }

    public void tick(ElectriNetworkTickEvent evt) {
        tick++;
        if (tick % 1000 == 0)
            this.checkValidity();

        if (!disabled && !cables.isEmpty() && !endpoints.isEmpty()) {
            ArrayList<EnergyInteraction> collectibles = new ArrayList<>();
            ArrayList<EnergyInteraction> insertibles = new ArrayList<>();
            int maxCanPush = energy;
            //ReikaJavaLibrary.pConsole(this.getIOLimit(), Dist.DEDICATED_SERVER);
            Iterator<Entry<WorldLocation, EnergyInteraction>> it = endpoints.entrySet().iterator();
            while (it.hasNext()) {
                Entry<WorldLocation, EnergyInteraction> e = it.next();
                EnergyInteraction ei = e.getValue();
                if (ei.valid()) {
                    maxCanPush += ei.getTotalInsertible();
                    if (ei.isCollectible()) {
                        collectibles.add(ei);
                    }
                    if (ei.isInsertible()) {
                        insertibles.add(ei);
                    }
                } else {
                    it.remove();
                }
            }

            //ReikaJavaLibrary.pConsole(endpoints);

            //ReikaJavaLibrary.pConsole(this.getIOLimit(), Dist.DEDICATED_SERVER);
            maxCanPush = Math.min(this.getIOLimit(), maxCanPush);

            for (int i = 0; i < collectibles.size() && energy < maxCanPush; i++) {
                EnergyInteraction ei = collectibles.get(i);
                int space = maxCanPush - energy;
                energy += ei.collectEnergy(space);
            }

            for (int i = 0; i < insertibles.size() && energy > 0; i++) {
                EnergyInteraction ei = insertibles.get(i);
                int add = Math.min(energy, 1 + energy / insertibles.size());
                energy -= ei.addEnergy(add);
            }
        }
    }

    public void checkValidity() {
        cables.removeIf(BlockEntity::isRemoved);
		/*
		Iterator<Entry<WorldLocation, EnergyInteraction>> it2 = endpoints.entrySet().iterator();
		while (it2.hasNext()) {
			Entry<WorldLocation, EnergyInteraction> e = it2.next();
			EnergyInteraction ei = e.getValue();
			if (!ei.valid())
				it2.remove();
		}*/
        if (cables.isEmpty()/* || endpoints.isEmpty()*/)
            this.clear(false);
    }

    @Override
    public void repath(ElectriNetworkRepathEvent evt) {

    }

    public void addElement(BlockEntityRFCable te) {
        if (!cables.contains(te)) {
            cables.add(te);
            if (te.getRFLimit() > 0 && te.getRFLimit() != networkLimit) {
                this.setIOLimit(Math.min(te.getRFLimit(), this.getIOLimit()));
            }
        }
    }

    public void removeElement(BlockEntityRFCable te) {
        cables.remove(te);
        this.rebuild();
    }

    private void rebuild() {
        ElectriCraft.LOGGER.debug("Remapping RF network " + this);
        for (BlockEntityRFCable te : cables) {
            te.findAndJoinNetwork(te.getLevel(), te.getBlockPos());
        }
        this.clear(true);
    }

    public void addConnection(IEnergyStorage ih, Direction dir) {
        if (ih instanceof BlockEntityRFCable)
            return;
        EnergyInteraction has = this.getInteractionFor(ih);
        if (has == null) {
            endpoints.put(new WorldLocation((BlockEntity) ih), new EnergyInteraction(ih, dir));
        } else {
            has.addSide(dir);
        }
    }

    public void merge(RFNetwork n) {
        if (n != this) {
            ArrayList<BlockEntityRFCable> li = new ArrayList<>(n.cables);
            for (EnergyInteraction ei : n.endpoints.values()) {
                EnergyInteraction has = this.getInteractionFor(ei.getTile());
                if (has == null) {
                    endpoints.put(ei.location, ei);
                } else {
                    has.merge(ei);
                }
            }
            n.clear(false);
            for (BlockEntityRFCable wire : li) {
                wire.setNetwork(this);
            }
            if (n.getIOLimit() != 0 && n.networkLimit != networkLimit)
                this.setIOLimit(Math.min(n.getIOLimit(), this.getIOLimit()));
        }
        this.updateWires();
    }

    private EnergyInteraction getInteractionFor(IEnergyStorage tile) {
        return endpoints.get(new WorldLocation((BlockEntity) tile));
    }

    private void updateWires() {

    }

    private void clear(boolean clearTiles) {
        if (clearTiles) {
            for (BlockEntityRFCable cable : cables) {
                cable.resetNetwork();
            }
        }

        cables.clear();
        endpoints.clear();
        energy = 0;
        disabled = true;

        ElectriNetworkManager.instance.scheduleNetworkDiscard(this);
    }

    @Override
    public String toString() {
        return cables.size() + ": " + endpoints;
    }

    public int drainEnergy(int maxReceive, boolean simulate) {
        maxReceive = Math.min(maxReceive, this.getIOLimit());
        int drain = Math.min(maxReceive, energy);
        if (!simulate)
            energy -= drain;
        return drain;
    }

    public int addEnergy(int maxAdd, boolean simulate) {
        //ReikaJavaLibrary.pConsole(this.getIOLimit()+"/"+energy, Dist.DEDICATED_SERVER);
        if (energy >= this.getIOLimit())
            return 0;
        maxAdd = Math.min(this.getIOLimit(), maxAdd);
        if (!simulate)
            energy += maxAdd;
        return maxAdd;
    }

    private static class EnergyInteraction {

        private final WorldLocation location;
        private final ArrayList<Direction> sides = new ArrayList<>();
        private final boolean canExtract;
        private final boolean canReceive;

        private EnergyInteraction(IEnergyStorage ih, Direction... dirs) {
            location = new WorldLocation((BlockEntity) ih);
            for (Direction dir : dirs) {
                this.addSide(dir);
            }
            canExtract = ih.canExtract();
            canReceive = ih.canReceive();
        }

        public boolean isInsertible() {
            return this.getTotalInsertible() > 0;
        }

        public boolean isCollectible() {
            return this.getTotalCollectible() > 0;
        }

        public boolean contains(IEnergyStorage tile) {
            return tile == this.getTile();
        }

        public void addSide(Direction dir) {
            if (!sides.contains(dir))
                sides.add(dir);
        }

        public void merge(EnergyInteraction ei) {
            for (int i = 0; i < ei.sides.size(); i++) {
                this.addSide(ei.sides.get(i));
            }
        }
        public int collectEnergy(int max) {
            if (!canExtract)
                return 0;
            int total = 0;
            IEnergyStorage tile = this.getTile();
            if (tile.canReceive()) {
                int collect = max - total;
                total += tile.extractEnergy(collect, false);
            }

            return total;
        }

        public int addEnergy(int max) {
            if (!canReceive)
                return 0;
            int total = 0;
            IEnergyStorage tile = this.getTile();
            if (tile.canReceive()) {
                int add = max - total;
                total += (tile).receiveEnergy(add, false);
            }

            return total;
        }

        public int getTotalCollectible() {
            if (!canExtract)
                return 0;
            int total = 0;
            IEnergyStorage tile = this.getTile();
            if (tile.canReceive()) {
                total += (tile).extractEnergy(Integer.MAX_VALUE, true);

            }
            return total;
        }

        public int getTotalInsertible() {
            if (!canReceive)
                return 0;
            int total = 0;
            IEnergyStorage tile = this.getTile();
            if (tile.canReceive()) {
                total += (tile).receiveEnergy(Integer.MAX_VALUE, true);
            }

            return total;
        }

        @Override
        public String toString() {
            return this.getTile().toString();
        }

        public boolean valid() {
            return this.getTile() != null;
        }

        private IEnergyStorage getTile() {
            BlockEntity te = location.getBlockEntity();
            return te instanceof IEnergyStorage ? (IEnergyStorage) te : null;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof EnergyInteraction) {
                EnergyInteraction ei = (EnergyInteraction) o;
                return ei.location.equals(location);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return location.hashCode();
        }
    }

}
