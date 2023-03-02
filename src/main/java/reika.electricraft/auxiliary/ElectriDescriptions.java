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
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.resources.language.LanguageInfo;
//import net.minecraft.server.packs.resources.ResourceManager;
//import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.fml.loading.FMLLoader;
//import reika.dragonapi.instantiable.io.XMLInterface;
//import reika.dragonapi.libraries.java.ReikaObfuscationHelper;
//import reika.electricraft.ElectriCraft;
//import reika.electricraft.network.WireNetwork;
//import reika.electricraft.registry.BatteryType;
//import reika.electricraft.registry.ElectriTiles;
//import reika.electricraft.registry.WireType;
//import reika.electricraft.blockentities.BlockEntityTransformer;
//import reika.electricraft.blockentities.BlockEntityWirelessCharger;
//import reika.electricraft.blockentities.modinterface.BlockEntityRFBattery;
//
//public final class ElectriDescriptions {
//
//	private static String PARENT = getParent(true);
//	public static final String DESC_SUFFIX = ":desc";
//	public static final String NOTE_SUFFIX = ":note";
//
//	private static HashMap<ElectriBook, String> data = new HashMap<ElectriBook, String>();
//	private static HashMap<ElectriBook, String> notes = new HashMap<ElectriBook, String>();
//
//	private static HashMap<ElectriTiles, Object[]> machineData = new HashMap<ElectriTiles, Object[]>();
//	private static HashMap<ElectriTiles, Object[]> machineNotes = new HashMap<ElectriTiles, Object[]>();
//	private static HashMap<ElectriBook, Object[]> miscData = new HashMap<ElectriBook, Object[]>();
//
//	private static ArrayList<ElectriBook> categories = new ArrayList<ElectriBook>();
//
//	private static final XMLInterface parents = loadData("categories");
//	private static final XMLInterface machines = loadData("machines");
//	private static final XMLInterface infos = loadData("info");
//
//	private static XMLInterface loadData(String name) {
//		XMLInterface xml = new XMLInterface(ElectriCraft.class, PARENT+name+".xml", !ReikaObfuscationHelper.isDeObfEnvironment());
//		xml.setFallback(getParent(false)+name+".xml");
//		xml.init();
//		return xml;
//	}
//
//	private static String getParent(boolean locale) {
//		return locale && FMLLoader.getDist() == Dist.CLIENT ? getLocalizedParent() : "resources/";
//	}
//
//
//	private static String getLocalizedParent() {
//		LanguageInfo language = Minecraft.getInstance().getLanguageManager().getSelected();
//		String lang = language.getCode();
//		if (hasLocalizedFor(language) && !"en_US".equals(lang))
//			return "resources/"+lang+"/";
//		return "resources/";
//	}
//
//	private static boolean hasLocalizedFor(LanguageInfo language) {
//		String lang = language.getCode();
//		try (InputStream o = ElectriCraft.class.getResourceAsStream("resources/"+lang+"/categories.xml")) {
//			return o != null;
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	public static String getTOC() {
//		List<ElectriBook> toctabs = ElectriBook.getTOCTabs();
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < toctabs.size(); i++) {
//			ElectriBook h = toctabs.get(i);
//			sb.append("Page ");
//			sb.append(h.getScreen());
//			sb.append(" - ");
//			sb.append(h.getTitle());
//			if (i < toctabs.size()-1)
//				sb.append("\n");
//		}
//		return sb.toString();
//	}
//
//	private static void addData(ElectriTiles m, Object... data) {
//		machineData.put(m, data);
//	}
//
//	private static void addData(ElectriBook h, Object... data) {
//		miscData.put(h, data);
//	}
//
//	private static void addData(ElectriBook h, int[] data) {
//		Object[] o = new Object[data.length];
//		for (int i = 0; i < o.length; i++)
//			o[i] = data[i];
//		miscData.put(h, o);
//	}
//
//	private static void addNotes(ElectriTiles m, Object... data) {
//		machineNotes.put(m, data);
//	}
//
//	public static void reload() {
//		PARENT = getParent(true);
//
//		data.clear();
//		loadNumericalData();
//
//		machines.reread();
//		infos.reread();
//
//		parents.reread();
//
//		loadData();
//	}
//
//	private static void addEntry(ElectriBook h, String sg) {
//		data.put(h, sg);
//	}
//
//	public static void loadData() {
//		List<ElectriBook> parenttabs = ElectriBook.getCategoryTabs();
//
//		List<ElectriBook> machinetabs = ElectriBook.getMachineTabs();
//		ElectriBook[] infotabs = ElectriBook.getInfoTabs();
//
//		for (int i = 0; i < parenttabs.size(); i++) {
//			ElectriBook h = parenttabs.get(i);
//			String desc = parents.getValueAtNode("categories:"+h.name().toLowerCase(Locale.ENGLISH));
//			addEntry(h, desc);
//		}
//
//		for (int i = 0; i < machinetabs.size(); i++) {
//			ElectriBook h = machinetabs.get(i);
//			ElectriTiles m = h.getMachine();
//			String desc = machines.getValueAtNode("machines:"+m.name().toLowerCase(Locale.ENGLISH)+DESC_SUFFIX);
//			String aux = machines.getValueAtNode("machines:"+m.name().toLowerCase(Locale.ENGLISH)+NOTE_SUFFIX);
//			desc = String.format(desc, machineData.get(m));
//			aux = String.format(aux, machineNotes.get(m));
//
//			if (XMLInterface.NULL_VALUE.equals(desc))
//				desc = "There is no handbook data for this machine yet.";
//
//			if (m.isDummiedOut()) {
//				desc += "\nThis machine is currently unavailable.";
//				aux += "\nNote: Dummied Out";
//			}
//
//			addEntry(h, desc);
//			notes.put(h, aux);
//		}
//
//		for (int i = 0; i < infotabs.length; i++) {
//			ElectriBook h = infotabs[i];
//			String desc = infos.getValueAtNode("info:"+h.name().toLowerCase(Locale.ENGLISH));
//			desc = String.format(desc, miscData.get(h));
//			addEntry(h, desc);
//		}
//	}
//
//
//	public static String getData(ElectriBook h) {
//		if (!data.containsKey(h))
//			return "";
//		return data.get(h);
//	}
//
//	public static String getNotes(ElectriBook h) {
//		if (!notes.containsKey(h))
//			return "";
//		return notes.get(h);
//	}
//
//	static {
//		loadNumericalData();
//		MinecraftForge.EVENT_BUS.register(new ReloadListener());
//	}
//
//	public static class ReloadListener implements ResourceManagerReloadListener {
//
//		@Override
//		public void onResourceManagerReload(ResourceManager manager) {
//			ElectriDescriptions.reload();
//		}
//	}
//
//	private static void loadNumericalData() {
//		addData(ElectriBook.LIMITS, WireType.getLimitsForDisplay());
//		addNotes(ElectriTiles.GENERATOR, WireNetwork.TORQUE_PER_AMP, WireNetwork.TORQUE_PER_AMP);
//		addData(ElectriTiles.TRANSFORMER, BlockEntityTransformer.MAXTEMP, BlockEntityTransformer.MAXCURRENT);
//		addData(ElectriTiles.RFBATTERY, BlockEntityRFBattery.CAPACITY);
////		addData(ElectriTiles.EUBATTERY, BlockEntityEUBattery.CAPACITY);
//		addNotes(ElectriTiles.BATTERY, BatteryType.getDataForDisplay());
//		addNotes(ElectriTiles.WIRELESSPAD, BlockEntityWirelessCharger.ChargerTiers.getDataForDisplay());
//	}
//}
