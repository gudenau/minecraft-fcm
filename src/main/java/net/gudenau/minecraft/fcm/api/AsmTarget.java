package net.gudenau.minecraft.fcm.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * To allow for easily silencing Idea warnings without @SuppressWarnings
 * */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface AsmTarget{}
