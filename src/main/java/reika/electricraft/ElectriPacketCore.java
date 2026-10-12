/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.electricraft;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import reika.dragonapi.auxiliary.PacketTypes;
import reika.dragonapi.interfaces.PacketHandler;
import reika.dragonapi.libraries.io.ReikaChatHelper;
import reika.dragonapi.libraries.io.ReikaPacketHelper;
import reika.electricraft.registry.ElectriPackets;
import reika.electricraft.blockentities.BlockEntityTransformer;
import reika.electricraft.blockentities.modinterface.BlockEntityRFCable;


public class ElectriPacketCore implements PacketHandler {

	public void handleData(ReikaPacketHelper.PacketObj packet, Level world, Player ep) {
		if (!world.isClientSide() && packet.getType().isClientboundOnly()) return;
		DataInputStream inputStream = packet.getDataIn();
		int control = Integer.MIN_VALUE;
		int len;
		ElectriPackets pack = null;
		int[] data = new int[0];
		long longdata = 0;
		float floatdata = 0;
		int x = 0;
		int y = 0;
		int z = 0;
		double dx = 0;
		double dy = 0;
		double dz = 0;
		boolean readinglong = false;
		String stringdata = null;
		UUID id = null;
		//System.out.print(packet.length);
		try {
			//ReikaJavaLibrary.pConsole(inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt());
			PacketTypes packetType = packet.getType();
			switch(packetType) {
				case FULLSOUND:
					break;
				case SOUND:
					return;
				case STRING:
					stringdata = packet.readString();
					control = inputStream.readInt();
					//pack = ReactorPackets.getEnum(control);
					break;
				case DATA:
					control = inputStream.readInt();
					pack = ElectriPackets.getEnum(control);
					len = pack.numInts;
					data = new int[len];
					readinglong = false;//pack.isLongPacket();
					//if (!readinglong) {
					for (int i = 0; i < len; i++)
						data[i] = inputStream.readInt();
					//}
					//else
					//	longdata = inputStream.readLong();
					break;
				case POS:
					control = inputStream.readInt();
					pack = ElectriPackets.getEnum(control);
					dx = inputStream.readDouble();
					dy = inputStream.readDouble();
					dz = inputStream.readDouble();
					len = pack.numInts;
					if (pack.hasData()) {
						data = new int[len];
						for (int i = 0; i < len; i++)
							data[i] = inputStream.readInt();
					}
					break;
				case UPDATE:
					control = inputStream.readInt();
					//pack = ReactorPackets.getEnum(control);
					break;
				case FLOAT:
					control = inputStream.readInt();
					//pack = ReactorPackets.getEnum(control);
					floatdata = inputStream.readFloat();
					break;
				case SYNC:
					String name = packet.readString();
					x = inputStream.readInt();
					y = inputStream.readInt();
					z = inputStream.readInt();
					ReikaPacketHelper.updateBlockEntityData(world, x, y, z, name, inputStream);
					return;
				case TANK:
					ReikaPacketHelper.receiveTankSyncPacket(world, inputStream);
					return;
				case RAW:
					control = inputStream.readInt();
					len = 1;
					data = new int[len];
					for (int i = 0; i < len; i++)
						data[i] = inputStream.readInt();
					break;
				case PREFIXED:
					control = inputStream.readInt();
					len = reika.dragonapi.libraries.io.PacketValidation.readIntCount(inputStream);
					data = new int[len];
					for (int i = 0; i < len; i++)
						data[i] = inputStream.readInt();
					break;
				case NBT:
					break;
				case STRINGINT:
				case STRINGINTLOC:
					stringdata = packet.readString();
					control = inputStream.readInt();
					data = new int[1];
					for (int i = 0; i < data.length; i++)
						data[i] = inputStream.readInt();
					break;
				case UUID:
					control = inputStream.readInt();
					pack = ElectriPackets.getEnum(control);
					long l1 = inputStream.readLong(); //most
					long l2 = inputStream.readLong(); //least
					id = new UUID(l1, l2);
					break;
			}
			if (packetType.hasCoordinates()) {
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
			}
		}
		catch (IOException | IllegalArgumentException e) {
			e.printStackTrace();
			return;
		}
        if (pack == null) return;
        BlockPos target = new BlockPos(x, y, z);
        if (!world.hasChunkAt(target)) return;
        BlockEntity te = world.getBlockEntity(target);
        if (!reika.dragonapi.libraries.io.PacketValidation.hasMenu(ep, world, target, te)) return;
        if (pack == ElectriPackets.RFCABLE && (data.length != 1 || data[0] < 0)) return;
        if (pack == ElectriPackets.TRANSFORMER && (data.length != 2 || data[0] <= 0 || data[1] <= 0 || data[0] > 9999 || data[1] > 9999)) return;
		try {
			switch (pack) {
				case RFCABLE:
					((BlockEntityRFCable)te).setRFLimit(data[0]);
					break;
				case TRANSFORMER:
					BlockEntityTransformer tf = (BlockEntityTransformer)te;
					if (tf != null) {
						tf.setRatio(data[0], data[1]);
					}
					break;
			}
		}
		catch (Exception e) {
			ElectriCraft.LOGGER.error("Machine/item was deleted before its packet '"+pack+"' could be received!");
			ReikaChatHelper.writeString("Machine/item was deleted before its packet '"+pack+"' could be received!");
			e.printStackTrace();
		}
	}
}
