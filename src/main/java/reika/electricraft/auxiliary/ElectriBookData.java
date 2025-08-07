///*******************************************************************************
// * @author Reika Kalseki
// *
// * Copyright 2017
// *
// * All rights reserved.
// * Distribution of the software in any form is only allowed with
// * explicit, prior permission from the owner.
// ******************************************************************************/
//package reika.electricraft.auxiliary;
//
//import reika.dragonapi.instantiable.data.maps.ArrayMap;
//import reika.electricraft.registry.ElectriBook;
//
//public class ElectriBookData {
//
//	private static final ArrayMap<ElectriBook> tabMappings = new ArrayMap<>(2);
//
//	private static void mapHandbook() {
//		for (int i = 0; i < ElectriBook.tabList.length; i++) {
//			ElectriBook h = ElectriBook.tabList[i];
//			tabMappings.putV(h, h.getScreen(), h.getPage());
//		}
//	}
//
//	public static ElectriBook getMapping(int screen, int page) {
//		return tabMappings.getV(screen, page);
//	}
//
//	static {
//		mapHandbook();
//	}
//}
