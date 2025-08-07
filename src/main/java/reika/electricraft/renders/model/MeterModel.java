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

public class MeterModel extends Model
{
    
    public static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "textures/metertex.png");
    
    private final ModelPart shape1;
    private final ModelPart shape2;
    private final ModelPart shape2a;
    private final ModelPart shape3;
    private final ModelPart shape3a;
    private final ModelPart shape4;
    private final ModelPart root;
    
    public MeterModel(ModelPart modelPart) {
        super(RenderType::entityCutout);
        this.root = modelPart;
        
        this.shape1 = modelPart.getChild("shape1");
        this.shape2 = modelPart.getChild("shape2");
        this.shape2a = modelPart.getChild("shape2a");
        this.shape3 = modelPart.getChild("shape3");
        this.shape3a = modelPart.getChild("shape3a");
        this.shape4 = modelPart.getChild("shape4");
    }

    // Grab the parts in the constructor if you need them
    
    public static LayerDefinition createLayer() {
        MeshDefinition definition = new MeshDefinition();
        PartDefinition root = definition.getRoot();
        
        root.addOrReplaceChild("shape1",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 0)
                .addBox(0, 0, 0, 16, 15, 16),
            PartPose.offsetAndRotation(-8, 9, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape2",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 50)
                .addBox(0, 0, 0, 2, 1, 16),
            PartPose.offsetAndRotation(6, 8, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape2a",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 32)
                .addBox(0, 0, 0, 2, 1, 16),
            PartPose.offsetAndRotation(-8, 8, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape3",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 68)
                .addBox(0, 0, 0, 12, 1, 2),
            PartPose.offsetAndRotation(-6, 8, 6, 0, 0, 0));
        
        root.addOrReplaceChild("shape3a",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 68)
                .addBox(0, 0, 0, 12, 1, 2),
            PartPose.offsetAndRotation(-6, 8, -8, 0, 0, 0));
        
        root.addOrReplaceChild("shape4",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 72)
                .addBox(0, 0, 0, 15, 1, 15),
            PartPose.offsetAndRotation(-7.5F, 8.5F, -7.5F, 0, 0, 0));
        
        return LayerDefinition.create(definition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        root.render(stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
