package com.catkit.framework.config

import android.content.Context
import android.content.SharedPreferences

/**
 * The SharedPreferences. There will be many kinds of SharedPreferences in
 * you app by distinguished name from others.
 * <p>
 *     Note: The app's SharedPreferences is Singleton(There are synchronized and if-null called
 *     when get SharedPreferencesImpl by name). so you do not need set Object
 *     or lazy instance but you want to do it.
 * </p>
 *
 * <p>
 *     Note: SharedPreferencesImpl's instance is safe under mix thread because of get method
 *     is a synchronized({@code synchronized(mLock)}). Besides, do not cache your SharedPreferences,
 *     it has been done by apply and commit.
 * </p>
 * <p>
 *     Position:data/shared_prefs/name.xml
 * </p>
 * <p>
 *     Defects： Different app's SharedPreferencesImpl({@code ArrayMap<String, ArrayMap<String, SharedPreferencesImpl>>})
 *     is distinguished by package name. And It is distinguished by name string in you own app. so, it is
 *     a ArrayMap, be careful of your memory if you set many BaseLocalConfig by name[BaseLocalConfig.name].
 * </p>
 * <p>
 *     Note: Do not put much data to SharedPreferences because of static
 *     ArrayMap({@code ArrayMap<String, ArrayMap<String, SharedPreferencesImpl>>}) and HashMap to save data.
 * </p>
 * <p>
 *     Note: Do not use it in mix progress because of static ArrayMap and it's instance. You
 *     can use {@code MODE_MULTI_PROCESS} with {@code MODE_WORLD_READABLE} and {@code MODE_WORLD_WRITEABLE}
 *     below Android N. Use ContentProvider will be good.
 * </p>
 */
abstract class BaseLocalConfig(ctx: Context, protected val name: String) : BaseConfig() {
    private val config: SharedPreferences = ctx.getSharedPreferences(name, Context.MODE_PRIVATE)

    /**
     * Different from clear[clear], it is used by cleaning some data you want.
     */
    abstract fun clean()

    open fun getInt(key: String, defValue: Int = 0) = config.getInt(key, defValue)

    open fun getLong(key: String, defValue: Long = 0L) = config.getLong(key, defValue)

    open fun getFloat(key: String, defValue: Float = 0.0F) = config.getFloat(key, defValue)

    open fun getBoolean(key: String, defValue: Boolean = false) = config.getBoolean(key, defValue)

    open fun getString(key: String, defValue: String? = null): String? = config.getString(key, defValue)

    open fun getStringSet(key: String, values: Set<String>? = null): Set<String>? = config.getStringSet(key,
            values)


    open fun setInt(key: String, value: Int) = config.edit().putInt(key, value).apply()

    open fun setLong(key: String, value: Long) = config.edit().putLong(key, value).apply()

    open fun setFloat(key: String, value: Float) = config.edit().putFloat(key, value).apply()

    open fun setBoolean(key: String, value: Boolean) = config.edit().putBoolean(key, value).apply()

    open fun setString(key: String, value: String?) = config.edit().putString(key, value).apply()

    open fun setStringSet(key: String, value: Set<String>?) = config.edit().putStringSet(key, value).apply()


    open fun clear() = config.edit().clear().apply()

    open fun size() = config.all.size

    /**
     * Do something before apply(block is action)
     * <p>
     *     We do not need the result(Boolean) to do something next, so use apply(). The commit method
     *     has a return data, however apply is not. The commit will block ui thread, apply is not, it
     *     is run in thread pool and it is asynchronous, besides apply will not report error and is
     *     write on cache first before write to disk.
     * </p>
     */
    open fun apply(action: () -> Unit) = config.edit().apply { action() }.apply()

}