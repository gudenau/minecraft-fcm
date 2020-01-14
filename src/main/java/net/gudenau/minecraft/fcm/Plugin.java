package net.gudenau.minecraft.fcm;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.gudenau.minecraft.fcm.api.IClassTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.FabricMixinTransformerProxy;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static org.objectweb.asm.Opcodes.*;

public class Plugin implements IMixinConfigPlugin{
    static{
        try{
            ClassLoader classLoader = Plugin.class.getClassLoader();

            // Get Class<KnotClassLoader>
            Class<?> KnotClassLoader = classLoader.getClass();
            if(!KnotClassLoader.getName().equals("net.fabricmc.loader.launch.knot.KnotClassLoader")){
                throw new RuntimeException("ClassLoader was not an instance of KnotClassLoader");
            }

            // Get Class<KnotClassDelegate>
            Field KnotClassLoader$delegate = KnotClassLoader.getDeclaredField("delegate");
            Class<?> KnotClassDelegate = KnotClassLoader$delegate.getType();
            if(!KnotClassDelegate.getName().equals("net.fabricmc.loader.launch.knot.KnotClassDelegate")){
                throw new RuntimeException("KnotClassLoader.delegate is not an instance of KnotClassDelegate");
            }

            // Get KnotClassDelegate instance
            KnotClassLoader$delegate.setAccessible(true);
            Object delegate = KnotClassLoader$delegate.get(classLoader);

            // Get mixinTransformer
            Field KnotClassDelegate$mixinTransformer = KnotClassDelegate.getDeclaredField("mixinTransformer");
            KnotClassDelegate$mixinTransformer.setAccessible(true);
            FabricMixinTransformerProxy mixinTransformer = (FabricMixinTransformerProxy)KnotClassDelegate$mixinTransformer.get(delegate);

            // Get Class<MixinTransformer>
            Field FabricMixinTransformerProxy$transformer = FabricMixinTransformerProxy.class.getDeclaredField("transformer");
            Class<?> MixinTransformer = FabricMixinTransformerProxy$transformer.getType();
            if(!MixinTransformer.getName().equals("org.spongepowered.asm.mixin.transformer.MixinTransformer")){
                throw new RuntimeException("FabricMixinTransformerProxy.transformer is not an instance of MixinTransformer");
            }

            // Get transformer
            FabricMixinTransformerProxy$transformer.setAccessible(true);
            Object transformer = FabricMixinTransformerProxy$transformer.get(mixinTransformer);

            // Generate proxy
            Unsafe unsafe = getUnsafe();
            Class<?> MixinTransformerProxy = unsafe.defineAnonymousClass(MixinTransformer, generateProxyClass(), null);

            // Create proxy
            Object proxy = unsafe.allocateInstance(MixinTransformerProxy);
            Field MixinTransformerProxy$parent = MixinTransformerProxy.getDeclaredField("parent");
            long MixinTransformerProxy$parent$cookie = unsafe.objectFieldOffset(MixinTransformerProxy$parent);
            unsafe.putObject(proxy, MixinTransformerProxy$parent$cookie, transformer);

            // Set proxy
            FabricMixinTransformerProxy$transformer.set(mixinTransformer, proxy);
        }catch(IllegalArgumentException | ReflectiveOperationException t){
            throw new RuntimeException("Something went wrong setting up the transformer", t);
        }
    }

    private static byte[] generateProxyClass(){
        ClassWriter classWriter = new ClassWriter(0);

        classWriter.visit(
            V1_8,
            ACC_PUBLIC,
            "org/spongepowered/asm/mixin/transformer/MixinTransformerProxy",
            null,
            "org/spongepowered/asm/mixin/transformer/MixinTransformer",
            null
        );

        classWriter.visitField(
            ACC_PRIVATE | ACC_FINAL,
            "parent",
            "Lorg/spongepowered/asm/mixin/transformer/MixinTransformer;",
            null,
            null
        );

        MethodVisitor method = classWriter.visitMethod(
            ACC_PUBLIC,
            "<init>",
            "(Lorg/spongepowered/asm/mixin/transformer/MixinTransformer;)V",
            null,
            null
        );
        method.visitCode();
        try{
            Label start = new Label();
            method.visitLabel(start);
            method.visitVarInsn(ALOAD, 0);
            method.visitMethodInsn(
                INVOKESPECIAL,
                "org/spongepowered/asm/mixin/transformer/MixinTransformer",
                "<init>",
                "()V",
                false
            );
            method.visitVarInsn(ALOAD, 0);
            method.visitVarInsn(ALOAD, 1);
            method.visitFieldInsn(
                PUTFIELD,
                "org/spongepowered/asm/mixin/transformer/MixinTransformerProxy",
                "parent",
                "Lorg/spongepowered/asm/mixin/transformer/MixinTransformer;"
            );
            method.visitInsn(RETURN);
            Label end = new Label();
            method.visitLabel(end);

            method.visitLocalVariable(
                "this",
                "Lorg/spongepowered/asm/mixin/transformer/MixinTransformerProxy;",
                null,
                start,
                end,
                0
            );

            method.visitLocalVariable(
                "parent",
                "Lorg/spongepowered/asm/mixin/transformer/MixinTransformer;",
                null,
                start,
                end,
                1
            );

            method.visitMaxs(2, 2);
        }finally{
            method.visitEnd();
        }

        method = classWriter.visitMethod(
            ACC_PUBLIC,
            "transformClassBytes",
            "(Ljava/lang/String;Ljava/lang/String;[B)[B",
            null,
            null
        );
        method.visitCode();
        try{
            // access flags 0x1
            Label start = new Label();
            method.visitLabel(start);
            method.visitVarInsn(ALOAD, 0);
            method.visitFieldInsn(
                GETFIELD,
                "org/spongepowered/asm/mixin/transformer/MixinTransformerProxy",
                "parent",
                "Lorg/spongepowered/asm/mixin/transformer/MixinTransformer;"
            );
            method.visitVarInsn(ALOAD, 1);
            method.visitVarInsn(ALOAD, 2);
            method.visitVarInsn(ALOAD, 3);
            method.visitMethodInsn(
                INVOKEVIRTUAL,
                "org/spongepowered/asm/mixin/transformer/MixinTransformer",
                "transformClassBytes",
                "(Ljava/lang/String;Ljava/lang/String;[B)[B",
            false
            );
            method.visitVarInsn(ASTORE, 4);
            Label middle = new Label();
            method.visitLabel(middle);
            method.visitVarInsn(ALOAD, 1);
            method.visitVarInsn(ALOAD, 2);
            method.visitVarInsn(ALOAD, 4);
            method.visitMethodInsn(
                INVOKESTATIC,
                "net/gudenau/minecraft/fcm/Transformer",
                "transform",
                "(Ljava/lang/String;Ljava/lang/String;[B)[B",
                false
            );
            method.visitInsn(ARETURN);
            Label end = new Label();
            method.visitLabel(end);

            method.visitLocalVariable(
                "this",
                "Lorg/spongepowered/asm/mixin/transformer/MixinTransformerProxy;",
                null,
                start,
                end,
                0
            );
            method.visitLocalVariable(
                "name",
                "Ljava/lang/String;",
                null,
                start,
                end,
                1
            );
            method.visitLocalVariable(
                "transformedName",
                "Ljava/lang/String;",
                null,
                start,
                end,
                2
            );
            method.visitLocalVariable(
                "basicClass",
                "[B",
                null,
                start,
                end,
                3
            );
            method.visitLocalVariable(
                "parentClass",
                "[B",
                null,
                middle,
                end,
                4
            );
            method.visitMaxs(4, 5);
        }finally{
            method.visitEnd();
        }

        return classWriter.toByteArray();
    }

    private static Unsafe getUnsafe(){
        try{
            Field Unsafe$theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            Unsafe$theUnsafe.setAccessible(true);
            return (Unsafe)Unsafe$theUnsafe.get(null);
        }catch(ReflectiveOperationException e){
            throw new RuntimeException("Failed to get Unsafe instance", e);
        }
    }

    @Override
    public void onLoad(String mixinPackage){}

    @Override
    public String getRefMapperConfig(){
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName){
        return false;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets){}

    @Override
    public List<String> getMixins(){
        for(ModContainer mod : FabricLoader.getInstance().getAllMods()){
            ModMetadata metadata = mod.getMetadata();
            if(metadata.containsCustomValue("gud_fcm")){
                for(CustomValue value : metadata.getCustomValue("gud_fcm").getAsArray()){
                    try{
                        Constructor<? extends IClassTransformer> transformer = Class.forName(value.getAsString())
                            .asSubclass(IClassTransformer.class)
                            .getDeclaredConstructor();
                        transformer.setAccessible(true);
                        Transformer.registerTransformer(transformer.newInstance());
                    }catch(ReflectiveOperationException e){
                        throw new RuntimeException(String.format(
                            "Failed to load transformer %s from %s",
                            value.getAsString(),
                            metadata.getId()
                        ), e);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo){}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo){}
}
