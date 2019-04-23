package com.catkit.framework

import com.catkit.framework.annotation.IntDef

/**
 * public final class, public final static args.
 *
 * Default crash file name.
 */
internal const val DEFAULT_ERROR_LOCAL_PATH_EXTRA = "crash"
/**
 * The level of update.
 * @see [com.catkit.framework.updater.Updater]
 */
const val UPDATE_MANUALLY = 0x00000002
const val UPDATE_FORCIBLY = 0x00000004
const val UPDATE_AUTOMATIC = 0x00000006

@IntDef(UPDATE_AUTOMATIC, UPDATE_MANUALLY, UPDATE_FORCIBLY)
@Retention(AnnotationRetention.SOURCE)
annotation class UpdateMode