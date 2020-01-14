package net.gudenau.minecraft.fcme;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.gudenau.minecraft.fcm.api.AsmTarget;
import net.gudenau.minecraft.fcm.api.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

@AsmTarget
public class Transformer implements IClassTransformer{
    @Override
    public boolean handlesClass(String name, String transformedName){
        return transformedName.equals("net.minecraft.entity.mob.CreeperEntity");
    }

    @Override
    public byte[] transformClass(String name, String transformedName, byte[] original){
        ClassReader reader = new ClassReader(original);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);

        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
        String HostileEntity = remapper.mapClassName(
            "intermediary",
            "net.minecraft.entity.mob.HostileEntity"
        ).replaceAll("\\.", "/");
        String tick = remapper.mapMethodName(
            "intermediary",
            "net.minecraft.class_1297",
            "method_5773",
            "()V"
        );

        for(MethodNode method : classNode.methods){
            if(method.name.equals(tick) && method.desc.equals("()V")){
                InsnList instructions = method.instructions;
                instructions.clear();
                instructions.add(new VarInsnNode(ALOAD, 0));
                instructions.add(new MethodInsnNode(
                    INVOKESPECIAL,
                    HostileEntity,
                    tick,
                    "()V",
                    false
                ));
                instructions.add(new InsnNode(RETURN));
                method.maxLocals = 1;
                method.maxStack = 1;

                List<LocalVariableNode> localVariables = method.localVariables;
                for(int i = localVariables.size() - 1; i >= 0 ; i--){
                    LocalVariableNode local = localVariables.get(i);
                    if(!local.name.equals("this")){
                        localVariables.remove(i);
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}