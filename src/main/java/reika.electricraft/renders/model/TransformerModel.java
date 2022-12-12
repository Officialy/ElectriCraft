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

public class TransformerModel extends Model
{
    
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(MODID, "textures/transformertex.png");
    
    private final ModelPart shape1;
    private final ModelPart shape1a;
    private final ModelPart shape2;
    private final ModelPart shape2a;
    private final ModelPart shape3;
    private final ModelPart shape3a;
    private final ModelPart root;
    
    public TransformerModel(ModelPart modelPart) {
        super(RenderType::entityCutout);
        this.root = modelPart;
        
        this.shape1 = modelPart.getChild("shape1");
        this.shape1a = modelPart.getChild("shape1a");
        this.shape2 = modelPart.getChild("shape2");
        this.shape2a = modelPart.getChild("shape2a");
        this.shape3 = modelPart.getChild("shape3");
        this.shape3a = modelPart.getChild("shape3a");
    }

    // Grab the parts in the constructor if you need them
    
    public static LayerDefinition createLayer() {
        MeshDefinition definition = new MeshDefinition();
        PartDefinition root = definition.getRoot();
        
        root.addOrReplaceChild("shape1",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 0)
                .addBox(0, 0, 0, 4, 16, 4),
            PartPose.offsetAndRotation(2, 8, -2, 0, 0, 0));
        
        root.addOrReplaceChild("shape1a",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(17, 0)
                .addBox(0, 0, 0, 4, 16, 4),
            PartPose.offsetAndRotation(-6, 8, -2, 0, 0, 0));
        
        root.addOrReplaceChild("shape2",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(17, 21)
                .addBox(0, 0, 0, 4, 4, 4),
            PartPose.offsetAndRotation(-2, 20, -2, 0, 0, 0));
        
        root.addOrReplaceChild("shape2a",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(0, 21)
                .addBox(0, 0, 0, 4, 4, 4),
            PartPose.offsetAndRotation(-2, 8, -2, 0, 0, 0));
        
        root.addOrReplaceChild("shape3",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(34, 8)
                .addBox(0, 0, 0, 6, 1, 6),
            PartPose.offsetAndRotation(1, 12.5F, -3, 0, 0, 0));
        
        root.addOrReplaceChild("shape3a",
            CubeListBuilder.create()
                .mirror(true)
                .texOffs(34, 0)
                .addBox(0, 0, 0, 6, 1, 6),
            PartPose.offsetAndRotation(-7, 12.5F, -3, 0, 0, 0));
        
        return LayerDefinition.create(definition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    
        root.render(stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
