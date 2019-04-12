package com.catkit.framework.annotation

/**
 * The kotlin version 1.0.3 is not support @IntDef.
 * <p> sample:
 *  const val SPRING = 1
 *  const val SUMMER = 2
 *  const val AUTUMN = 3
 *  const val WINTER = 4
 *
 *  @IntDef(SPRING, SUMMER, AUTUMN, WINTER)
 *  @Retention(AnnotationRetention.SOURCE)
 *  annotation class Season{}
 * </p>
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class IntDef(vararg val value: Int = [], val flag: Boolean = false)
