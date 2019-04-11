package com.catkit.framework.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresPermission
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager
import com.catkit.framework.CatkitApplication
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.math.BigInteger
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * Be sure DeviceUtil is a singleton. It not needs thread safe and just
 * to get info of device. This singleton's memory loaded when fist use it.
 * so object declare is well done.
 * @author      XianQiang Tang
 */
object DeviceUtil {
    private const val DEFAULT_MAC = "02:00:00:00:00:02"
    private const val USELESS_ANDROID_ID = "9774d56d682e549c"
    /**
     * the cpu's arm
     */
    val supportABIs: Array<String>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Build.SUPPORTED_ABIS
        } else {
            @Suppress("DEPRECATION") arrayOf(Build.CPU_ABI)
        }

    /**
     * Get the phone's IMEI(the identify of phone card--sim card). It works for GSM/WCDMA/LTE.
     * so, it only works for Phone.
     *<p>permission[android.Manifest.permission.READ_PHONE_STATE]
     * is need, and some device return null or "". It maybe a string of "0000000000000" rather
     * than a effective 16 bit number.
     * </p>
     */
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getIMEI(): String? {
        var imei: String? = null
        val ctx: Context = CatkitApplication.appCtx
        ctx?.let {
            val phoneManager: TelephonyManager = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            imei = if (Build.VERSION.SDK_INT > 25) {
                phoneManager.imei
            } else {
                @Suppress("DEPRECATION") phoneManager.deviceId
            }
        }
        return imei
    }

    /**
     * The mac address is unique. But some phone will get default mac [DEFAULT_MAC]. eg: Android M.
     * <p>permission{@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}
     * permission{@code <uses-permission android:name="android.permission.INTERNET"/>}
     * is need, otherwise return null.
     * </p>
     *@return the mac address of phone
     */
    @RequiresPermission(allOf = [android.Manifest.permission.ACCESS_WIFI_STATE, android.Manifest.permission.INTERNET])
    fun getMacAddress(): String? {
        val macAddress: String?
        val macBuffer = StringBuffer()
        var netInterface: NetworkInterface?
        try {
            netInterface = NetworkInterface.getByName("eth1")
            if (netInterface == null) {
                netInterface = NetworkInterface.getByName("wlan0")
            }
            netInterface ?: return DEFAULT_MAC
            val address: ByteArray = netInterface.hardwareAddress
            address.forEach {
                macBuffer.append(String.format("%02X:", it))
            }
            if (macBuffer.isNotEmpty()) {
                macBuffer.deleteCharAt(macBuffer.length - 1)
            }
            macAddress = macBuffer.toString()
        } catch (e: SocketException) {
            return DEFAULT_MAC
        }
        return macAddress
    }

    /**
     * Android device named ANDROID_ID when first loaded.It maybe set to [USELESS_ANDROID_ID]
     * which is useless or the same between more than one phone.Or it maybe null.
     * @return ANDROID_ID, eg: 6ae48d23d1887323
     */
    @SuppressLint("HardwareIds")
    fun getAndroidId(): String {
        var androidId = String()
        val ctx: Context = CatkitApplication.appCtx
        ctx?.let {
            val id = Settings.Secure.getString(ctx.contentResolver, Settings.Secure.ANDROID_ID)
            androidId = if (id == null || id == USELESS_ANDROID_ID || id.length < 15) "" else id
        }
        return androidId
    }

    /**
     * The device's physical serial number. It needs permission of [android.Manifest.permission.READ_PHONE_STATE]
     * above Android O.
     * @return SERIAL, eg: 01b4549262d6a4a2
     */
    @RequiresPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getSerialNumber(): String? = if (Build.VERSION.SDK_INT > 25) android.os.Build.getSerial() else @Suppress("DEPRECATION") android.os.Build.SERIAL

    /**
     * The CPU serial number, error like "0000000000000000"
     */
    fun getCpuSerialNumber(): String? {
        var br: BufferedReader? = null
        try {
            br = BufferedReader(FileReader("/proc/cpuinfo"))
            var line: String?
            while (true) {
                line = br.readLine()
                if (line == null) {
                    break
                }

                if (line.startsWith("Serial")) {
                    line = line.substring(line.indexOf(":") + 1, line.length).trim()
                }

                if (!line.contains("0000000000")) {
                    return line
                }
            }
        } catch (e: Exception) {

        } finally {
            br?.close()
        }

        return ""
    }

    /**
     * Some permissions is need when getting device id. so, just by some easy way is ok.
     * If you want get more exactly, you can use IMEI, ANDROID_ID, serial number, MAC
     * address, and cpu serial number and so on. And make the string to MD5 is well.
     * <p>
     *     If ANDROID_ID and cpu serial number do not work, there is a random integer
     *     to used. so, to make the unique, we need put the string to SharedPreferences
     *     in local. It works well in app installed number counting.
     * </p>
     */
    fun getUniqueDeviceId(): String {
        var uniqueDeviceId: String
        val ctx = CatkitApplication.appCtx
        ctx ?: return ""

        val preferences = ctx.getSharedPreferences("device__prefs", Context.MODE_PRIVATE)
        val deviceId: String? = preferences.getString("device_id", null)
        if (!deviceId.isNullOrEmpty()) {
            return deviceId
        }

        do {
            uniqueDeviceId = getAndroidId()
            if (!uniqueDeviceId.isEmpty()) {
                uniqueDeviceId = "A$uniqueDeviceId"
                break
            }

            val cpuSerial = getCpuSerialNumber()
            if (cpuSerial.isNullOrEmpty()) {
                uniqueDeviceId = "C$cpuSerial"
                break
            }

            uniqueDeviceId = "R${BigInteger(64, Random()).toString(16)}"
        } while (false)

        preferences.edit().putString("device_id", uniqueDeviceId).apply()
        return uniqueDeviceId
    }

    /**
     * Get the device info and the Application info.
     * <p>
     *     Note: Be sure the api run after Application is running.
     * </p>
     * <p>
     *     Note: \r\n is the right way to format lines.
     * </p>
     */
    fun getDeviceInfo(): String {
        val deviceInfo = StringBuilder()
        //device info
        deviceInfo.append("OS            =  ").append(Build.DISPLAY).append("\r\n")
        deviceInfo.append("Manufacturer  =  ").append(Build.MANUFACTURER).append("\r\n")
        deviceInfo.append("Model         =  ").append(Build.MODEL).append("\r\n")
        deviceInfo.append("Brand         =  ").append(Build.BRAND).append("\r\n")
        deviceInfo.append("OSVersion     =  ").append(Build.VERSION.SDK_INT).append("\r\n")
        deviceInfo.append("DeviceId      =  ").append(getUniqueDeviceId()).append("\r\n")
        deviceInfo.append("IsRooted      =  ").append(isOSRooted()).append("\r\n")
        deviceInfo.append("ScreenSize    =  ").append(getScreenRealHeight()).append("x")
                .append(getScreenWidth()).append("\r\n")
        deviceInfo.append("CpuArms       =  ").append(supportABIs).append("\r\n")
        //app info
        deviceInfo.append("PackageName   =  ").append(AppUtil.getPackageName()).append("\r\n")
        deviceInfo.append("VersionCode   =  ").append(AppUtil.getVersionCode()).append("\r\n")
        deviceInfo.append("VersionName   =  ").append(AppUtil.getVersionName()).append("\r\n")
        deviceInfo.append("Signature     =  ").append(AppUtil.getSignature()).append("\r\n")
        deviceInfo.append("IsDebug       =  ").append(CatkitApplication.globalConfig.isDebug).append("\r\n")
        return deviceInfo.toString()
    }

    /**
     * @return true if the Android device is Rooted.
     */
    fun isOSRooted(): Boolean {
        val su = "su"
        val locations: Array<String> = arrayOf("/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
                "/system/sbin/", "/usr/bin/", "/vendor/bin/")
        locations.forEach {
            if (File(it + su).exists()) {
                return true
            }
        }
        return false
    }

    /**
     * Get the device real height, include NavigationBar height.
     * <p>
     *     Note: api getRealMetrics needs android level 17
     * </p>
     */
    @SuppressLint("NewApi")
    fun getScreenRealHeight(): Int {
        val windowManager: WindowManager = CatkitApplication.appCtx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    /**
     * Get device width.
     */
    fun getScreenWidth(): Int {
        val windowManager: WindowManager = CatkitApplication.appCtx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }
}
