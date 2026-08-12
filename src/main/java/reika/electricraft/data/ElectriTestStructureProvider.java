package reika.electricraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import net.minecraft.SharedConstants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.Identifier;
import reika.electricraft.ElectriCraft;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/** Generates the stone-floored arena used by ElectriCraft's headless server GameTests. */
public final class ElectriTestStructureProvider implements DataProvider {

    public static final Identifier ARENA = Identifier.fromNamespaceAndPath(ElectriCraft.MODID, "test_arena");
    private static final int SIZE_XZ = 11;
    private static final int SIZE_Y = 6;

    private final PackOutput output;

    public ElectriTestStructureProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        CompoundTag root = new CompoundTag();
        root.putInt("DataVersion", SharedConstants.getCurrentVersion().dataVersion().version());

        ListTag size = new ListTag();
        size.add(IntTag.valueOf(SIZE_XZ));
        size.add(IntTag.valueOf(SIZE_Y));
        size.add(IntTag.valueOf(SIZE_XZ));
        root.put("size", size);
        root.put("entities", new ListTag());

        ListTag palette = new ListTag();
        CompoundTag stone = new CompoundTag();
        stone.putString("Name", "minecraft:stone");
        palette.add(stone);
        root.put("palette", palette);

        ListTag blocks = new ListTag();
        for (int x = 0; x < SIZE_XZ; x++) {
            for (int z = 0; z < SIZE_XZ; z++) {
                CompoundTag block = new CompoundTag();
                ListTag pos = new ListTag();
                pos.add(IntTag.valueOf(x));
                pos.add(IntTag.valueOf(0));
                pos.add(IntTag.valueOf(z));
                block.put("pos", pos);
                block.putInt("state", 0);
                blocks.add(block);
            }
        }
        root.put("blocks", blocks);

        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(ARENA.getNamespace()).resolve("structure").resolve(ARENA.getPath() + ".nbt");
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            HashingOutputStream hashing = new HashingOutputStream(Hashing.sha1(), bytes);
            NbtIo.writeCompressed(root, hashing);
            cache.writeIfNeeded(path, bytes.toByteArray(), hashing.hash());
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName() {
        return "ElectriCraft Test Arena Structure";
    }
}
