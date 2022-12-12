package reika.electricraft.renders.model;
    
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;

import static reika.electricraft.ElectriCraft.MODID;

public class RelayModel extends Model
{
    
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/relay.png");
    
    private final ModelPart shape1;
    private final ModelPart shape1a;
    private final ModelPart shape2;
    private final ModelPart shape3;
    private final ModelPart shape3a;
    private final ModelPart shape4;
    private final ModelPart shape4a;
    private final ModelPart shape4b;
    private final ModelPart shape4c;
    private final ModelPart shape4d;
    private final ModelPart shape4e;
    private final ModelPart shape4f;
    private final ModelPart shape4h;
    private final ModelPart shape4i;
    private final ModelPart shape4j;
    private final ModelPart root;
    
    public RelayModel(ModelPart modelPart) {
        super(RenderType::entityCutout);
        this.root = modelPart;
        
        this.shape1 = modelPart.getChild("shape1");
        this.shape1a = modelPart.getChild("shape1a");
        this.shape2 = modelPart.getChild("shape2");
        this.shape3 = modelPart.getChild("shape3");
        this.shape3a = modelPart.getChild("shape3a");
        this.shape4 = modelPart.getChild("shape4");
        this.shape4a = modelPart.getChild("shape4a");
        this.shape4b = modelPart.getChild("shape4b");
        this.shape4c = modelPart.getChild("shape4c");
        this.shape4d = modelPart.getChild("shape4d");
        this.shape4e = modelPart.getChild("shape4e");
        this.shape4f = modelPart.getChild("shape4f");
        this.shape4h = modelPart.getChild("shape4h");
        this.shape4i = modelPart.getChild("shape4i");
        this.shape4j = modelPart.getChild("shape4j");
    }

    // Grab the parts in the constructor if you need them
    
    public static LayerDefinition createLayer() {
        MeshDefinition definition = new MeshDefinition();
        PartDefinition root = definition.getRoot();
        
        root.addOrReplaceChild("shape1",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(63, 0)
                .addBox(0, 0, 0, 12, 6, 14),
            PartPose.offsetAndRotation(-6, 16, -7, 0, 0, 0));
        
        root.addOrReplaceChild("shape1a",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 0)
                .addBox(0, 0, 0, 12, 2, 16),
            PartPose.offsetAndRotation(-6, 22, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape2",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 20)
                .addBox(0, 0, 0, 7, 4, 16),
            PartPose.offsetAndRotation(-3.5F, 12, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape3",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(42, 42)
                .addBox(0, -2, 0, 5, 2, 15),
            PartPose.offsetAndRotation(-3.5F, 12, -7.5F, 0, 0, 2.094395F));
        
        root.addOrReplaceChild("shape3a",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 42)
                .addBox(0, 0, 0, 5, 2, 15),
            PartPose.offsetAndRotation(3.5F, 12, -7.5F, 0, 0, 1.047198F));
        
        root.addOrReplaceChild("shape4",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(53, 29)
                .addBox(0, 0, 0, 1, 3, 1),
            PartPose.offsetAndRotation(4, 16, 7, 0, 0, 0));
        
        root.addOrReplaceChild("shape4a",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(52, 21)
                .addBox(0, 0, 0, 8, 6, 1),
            PartPose.offsetAndRotation(-4, 16, 7, 0, 0, 0));
        
        root.addOrReplaceChild("shape4b",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(47, 20)
                .addBox(0, 0, 0, 1, 6, 1),
            PartPose.offsetAndRotation(5, 16, 7, 0, 0, 0));
        
        root.addOrReplaceChild("shape4c",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(47, 28)
                .addBox(0, 0, 0, 1, 6, 1),
            PartPose.offsetAndRotation(-6, 16, 7, 0, 0, 0));
        
        root.addOrReplaceChild("shape4d",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(53, 29)
                .addBox(0, 0, 0, 1, 3, 1),
            PartPose.offsetAndRotation(-5, 16, 7, 0, 0, 0));
        
        root.addOrReplaceChild("shape4e",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(47, 20)
                .addBox(0, 0, 0, 1, 6, 1),
            PartPose.offsetAndRotation(5, 16, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape4f",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(52, 21)
                .addBox(0, 0, 0, 8, 6, 1),
            PartPose.offsetAndRotation(-4, 16, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape4h",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(47, 28)
                .addBox(0, 0, 0, 1, 6, 1),
            PartPose.offsetAndRotation(-6, 16, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape4i",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(53, 29)
                .addBox(0, 0, 0, 1, 3, 1),
            PartPose.offsetAndRotation(-5, 16, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape4j",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(53, 29)
                .addBox(0, 0, 0, 1, 3, 1),
            PartPose.offsetAndRotation(4, 16, -8, 0, 0, 0));
        
        return LayerDefinition.create(definition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    
        root.render(stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
