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
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
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

    /**
     * Registers an FE endpoint at the given position; {@code face} is the face of that block the
     * cable touches. The handler is resolved lazily through the block energy capability each
     * interaction (1.7.10 held the RF-API tile directly; modern FE machines only expose
     * capabilities, so instanceof checks would connect to nothing).
     */
    public void addConnection(Level world, BlockPos pos, Direction face) {
        if (world.getBlockEntity(pos) instanceof BlockEntityRFCable)
            return;
        WorldLocation loc = new WorldLocation(world, pos);
        EnergyInteraction has = endpoints.get(loc);
        if (has == null) {
            endpoints.put(loc, new EnergyInteraction(loc, face));
        } else {
            has.addSide(face);
        }
    }

    public void merge(RFNetwork n) {
        if (n != this) {
            ArrayList<BlockEntityRFCable> li = new ArrayList<>(n.cables);
            for (EnergyInteraction ei : n.endpoints.values()) {
                EnergyInteraction has = endpoints.get(ei.location);
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

    //Journals the shared buffer so inserts made inside another mod's aborted transaction revert.
    private final SnapshotJournal<Integer> energyJournal = new SnapshotJournal<>() {
        @Override
        protected Integer createSnapshot() {
            return energy;
        }

        @Override
        protected void revertToSnapshot(Integer snapshot) {
            energy = snapshot;
        }
    };

    public int insertEnergy(int maxAdd, TransactionContext tx) {
        if (energy >= this.getIOLimit())
            return 0;
        maxAdd = Math.min(this.getIOLimit(), maxAdd);
        if (maxAdd > 0) {
            energyJournal.updateSnapshots(tx);
            energy += maxAdd;
        }
        return maxAdd;
    }

    public int extractEnergy(int maxDrain, TransactionContext tx) {
        maxDrain = Math.min(maxDrain, this.getIOLimit());
        int drain = Math.min(maxDrain, energy);
        if (drain > 0) {
            energyJournal.updateSnapshots(tx);
            energy -= drain;
        }
        return drain;
    }

    public int getBufferedEnergy() {
        return energy;
    }

    private static class EnergyInteraction {

        private final WorldLocation location;
        private final ArrayList<Direction> sides = new ArrayList<>();

        private EnergyInteraction(WorldLocation loc, Direction... dirs) {
            location = loc;
            for (Direction dir : dirs) {
                this.addSide(dir);
            }
        }

        public boolean isInsertible() {
            return this.getTotalInsertible() > 0;
        }

        public boolean isCollectible() {
            return this.getTotalCollectible() > 0;
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

        /** The FE handler exposed on the given face, or null. */
        private EnergyHandler getCap(Direction side) {
            Level world = location.getWorld();
            return world == null ? null : world.getCapability(Capabilities.Energy.BLOCK, location.pos, side);
        }

        public int collectEnergy(int max) {
            int total = 0;
            try (Transaction tx = Transaction.openRoot()) {
                for (Direction side : sides) {
                    if (total >= max)
                        break;
                    EnergyHandler cap = this.getCap(side);
                    if (cap != null) {
                        total += cap.extract(max - total, tx);
                    }
                }
                tx.commit();
            }
            return total;
        }

        public int addEnergy(int max) {
            int total = 0;
            try (Transaction tx = Transaction.openRoot()) {
                for (Direction side : sides) {
                    if (total >= max)
                        break;
                    EnergyHandler cap = this.getCap(side);
                    if (cap != null) {
                        total += cap.insert(max - total, tx);
                    }
                }
                tx.commit();
            }
            return total;
        }

        public int getTotalCollectible() {
            int total = 0;
            try (Transaction tx = Transaction.openRoot()) { //never committed: pure simulation
                for (Direction side : sides) {
                    EnergyHandler cap = this.getCap(side);
                    if (cap != null) {
                        total += cap.extract(Integer.MAX_VALUE, tx);
                    }
                }
            }
            return total;
        }

        public int getTotalInsertible() {
            int total = 0;
            try (Transaction tx = Transaction.openRoot()) { //never committed: pure simulation
                for (Direction side : sides) {
                    EnergyHandler cap = this.getCap(side);
                    if (cap != null) {
                        total += cap.insert(Integer.MAX_VALUE, tx);
                    }
                }
            }
            return total;
        }

        @Override
        public String toString() {
            return location.toString();
        }

        public boolean valid() {
            if (location.getWorld() == null)
                return false;
            for (Direction side : sides) {
                if (this.getCap(side) != null)
                    return true;
            }
            return false;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof EnergyInteraction ei) {
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
