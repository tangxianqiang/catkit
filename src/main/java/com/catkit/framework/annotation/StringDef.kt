package com.catkit.framework.annotation

/**
 * The kotlin version 1.0.3 is not support @StringDef.<br/>
 * It is always letting us to us const val to represent a constant. But
 * If we need more this kind of constants, we have to list many "const val".
 * so, we use Enum to avoid this situation(Enum is not good as "public
 * static final" in memory).However, using Annotation can let "const val"
 * more readable as Enum but use same memory.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class StringDef(vararg val value: String = [])
