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

import reika.dragonapi.auxiliary.PacketTypes;

public enum ElectriPackets implements reika.dragonapi.interfaces.PacketId {

	RFCABLE(0, 1),
	TRANSFORMER(1, 2);

	private final int wireId;
	private static final java.util.Map<Integer, ElectriPackets> BY_ID = reika.dragonapi.interfaces.PacketId.index(values());
	@Override public int wireId() { return wireId; }

	public final int numInts;
	public final PacketTypes type;

	ElectriPackets(int id) { this(id, 0); }

	ElectriPackets(int id, int size) { this(id, size, PacketTypes.DATA); }

	ElectriPackets(int id, int size, PacketTypes t) {
		wireId = id;
		numInts = size;
		type = t;
	}

	public static final ElectriPackets getEnum(int id) {
		return reika.dragonapi.interfaces.PacketId.lookup(BY_ID, id);
	}

	public boolean hasData() {
		return numInts > 0;
	}

}
