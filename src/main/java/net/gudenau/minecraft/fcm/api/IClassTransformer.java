package net.gudenau.minecraft.fcm.api;

public interface IClassTransformer{
    boolean handlesClass(String name, String transformedName);
    byte[] transformClass(String name, String transformedName, byte[] original);
}
