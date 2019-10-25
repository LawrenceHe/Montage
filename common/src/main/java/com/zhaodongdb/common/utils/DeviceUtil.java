package com.zhaodongdb.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebSettings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by dev on 2017/5/25.
 */

public class DeviceUtil {

    private final static int kSystemRootStateUnknow = -1;
    private final static int kSystemRootStateDisable = 0;
    private final static int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;
    private static Boolean isEmulatorDevice;
    private static int windowWidth;// 屏幕宽
    private static int windowHeight;// 屏幕高

    private static String mobileUUID = "";
    private static String macAddress = "";
    private static String androidID = "";

    private static Boolean isPad = false;

    public static String getMacAddress() {
        if (!StringUtil.emptyOrNull(macAddress)) {
            return macAddress;
        }
        String macAddr = "";
        WifiManager wifiManager = (WifiManager) FoundationContextHolder.context.getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null) {
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info != null && info.getMacAddress() != null) {
                macAddr = info.getMacAddress().replace(":", "");
            }
        }

        //解决Android 6.0以上无法获取Mac地址的问题
        if (StringUtil.emptyOrNull(macAddr) || macAddr.equalsIgnoreCase("020000000000")) {
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iF = interfaces.nextElement();

                    byte[] addr = iF.getHardwareAddress();
                    if (addr == null || addr.length == 0) {
                        continue;
                    }

                    StringBuilder buf = new StringBuilder();
                    for (byte b : addr) {
                        buf.append(String.format("%02X:", b));
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    String mac = buf.toString();
                    if (iF.getName().startsWith("wlan0")) {
                        macAddr = mac.replace(":", "");
                        break;
                    }
                    if (iF.getName().startsWith("eth0")) {
                        macAddr = mac.replace(":", "");
                        break;
                    }
                }
            } catch (SocketException e) {

            }
        }
        if (!StringUtil.emptyOrNull(macAddr) && macAddr.contains("000000000000")) {
            macAddr = getMac();
            if (!StringUtil.emptyOrNull(macAddr)) {
                macAddr.replace(":", "");
            }
        }

        macAddr = StringUtil.getUnNullString(macAddr);
        if (StringUtil.emptyOrNull(macAddr) || macAddr.contains("000000") || macAddr.equalsIgnoreCase("020000000000")) {
            macAddr = Settings.Secure.getString(FoundationContextHolder.getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        macAddress = macAddr.toUpperCase();
        return macAddress;
    }

    public static String getMac() {
        String macSerial = "";
        String str = "";
        LineNumberReader input = null;
        Process ex = null;
        try {
            ex = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(ex.getInputStream());
            input = new LineNumberReader(ir);
            while (null != str) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (Throwable var5) {
            var5.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (ex != null) {
                ex.destroy();
            }
        }
        return macSerial;
    }

    /**
     * get imei
     *
     * @return
     */
    public static String getTelePhoneIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) FoundationContextHolder.context.getSystemService(TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            try {
                return StringUtil.getUnNullString(telephonyManager.getDeviceId());
            } catch (Exception e) {

            }
        }
        return "";
    }

    /**
     * get imsi
     *
     * @return
     */
    public static String getTelePhoneIMSI() {
        TelephonyManager telephony = (TelephonyManager) FoundationContextHolder.context.getSystemService(TELEPHONY_SERVICE);
        if (telephony != null) {
            try {
                return StringUtil.getUnNullString(telephony.getSubscriberId());
            } catch (SecurityException e) {

            }
        }
        return "";
    }

    public static String getAndroidID() {
        if (!StringUtil.emptyOrNull(androidID)) {
            return androidID;
        }

        try {
            String androidId = Settings.Secure.getString(FoundationContextHolder.context.getContentResolver(), Settings.Secure.ANDROID_ID);
            androidID = androidId;
            return androidId;
        } catch (Exception e) {

        }
        return "";
    }

    public static String getSerialNum() {
        return Build.SERIAL;
    }

    public static String getRomVersion() {
        String miuiVersion = checkAndGetMIUIVersion();
        if (!StringUtil.emptyOrNull(miuiVersion)) {
            return miuiVersion;
        }

        String emuiVersion = checkAndGetEmuiVesion();
        if (!StringUtil.emptyOrNull(emuiVersion)) {
            return emuiVersion;
        }

        return Build.VERSION.INCREMENTAL;
    }

    public static String checkAndGetMIUIVersion() {
        if (!StringUtil.emptyOrNull(getSystemProperty("ro.miui.ui.version.name"))) {
            return "MIUI_" + Build.VERSION.INCREMENTAL;
        }
        return null;
    }

    public static String checkAndGetEmuiVesion() {
        String emuiVersion = getSystemProperty("ro.build.version.emui");
        if (!StringUtil.emptyOrNull(emuiVersion)) {
            return emuiVersion;
        }
        return null;
    }


    public static boolean isRoot() {
        if (systemRootState == kSystemRootStateEnable) {
            return true;
        } else if (systemRootState == kSystemRootStateDisable) {
            return false;
        }

        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {

        }
        systemRootState = kSystemRootStateDisable;
        return false;
    }

    /**
     * 设置当前页面屏幕亮度
     *
     * @param activity
     * @param brightNess 亮度值
     */
    public static void setScreenBrightness(Activity activity, float brightNess) {
        if (activity != null) {
            WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
            layoutParams.screenBrightness = brightNess;
            activity.getWindow().setAttributes(layoutParams);
        }
    }

    /**
     * 获取当前页面屏幕亮度
     *
     * @param activity
     */
    public static float getScreenBrightness(Activity activity) {
        float value = 0;
        if (activity != null) {
            WindowManager.LayoutParams layoutParams = activity.getWindow().getAttributes();
            value = layoutParams.screenBrightness;
        }
        //value<0 时重新获取一次系统屏幕亮度
        if (value <= 0) {
            ContentResolver cr = activity.getContentResolver();
            try {
                value = (Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS)) / 255.0f;
            } catch (Settings.SettingNotFoundException e) {
                value = 0.6f;
            }
        }
        return value;
    }

    /**
     * 获取当前app的运行内存
     *
     * @param context
     * @return
     */
    public static double getRunningMemory(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获得系统里正在运行的所有进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessesList = mActivityManager.getRunningAppProcesses();
        double mDirty = 0;
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {
            // 进程名
            String processName = runningAppProcessInfo.processName;
            if (processName.equalsIgnoreCase("ctrip.android.view")) {
                // 进程ID号
                int pid = runningAppProcessInfo.pid;
                // 用户ID
                int uid = runningAppProcessInfo.uid;
                // 占用的内存
                int[] pids = new int[]{pid};
                Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(pids);
                int memorySize = memoryInfo[0].dalvikPrivateDirty;
                double memorySizeFl = (memorySize / 1024.0);
                int heapSize = memoryInfo[0].dalvikPss;
                double heapSizeDo = heapSize / 1024.0;
                mDirty = memoryInfo[0].otherPrivateDirty / 1024.0;
                break;
            }
        }
        return mDirty;
    }

    public static String getAvailMemory(Context context) {
        ArrayList<String> infos = new ArrayList<String>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        // mi.availMem; 当前系统的可用内存
        // 将获取的内存大小规格化
        infos.add(Formatter.formatFileSize(context, mi.availMem));
        if (Build.VERSION.SDK_INT >= 16) {
            infos.add(Formatter.formatFileSize(context, mi.totalMem));
        }
        infos.add(String.valueOf(mi.lowMemory));
        return infos.toString();
    }

    public static String getDeviceModel() {
        return Build.MODEL == null ? "" : Build.MODEL;
    }

    public static String getDeviceBrand() {
        return Build.BRAND == null ? "" : Build.BRAND;
    }

    /**
     * 检测是否是nubia手机
     *
     * @return true 是nubia手机，false否
     */
    public static boolean isNubia() {
        return "nubia".equalsIgnoreCase(getDeviceBrand());
    }

    /**
     * 获取设备 SDK版本号
     */
    public static int getSDKVersionInt() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * 获取动画设置
     */
    public static boolean getAnimationSetting(Context context) {
        ContentResolver cv = context.getContentResolver();
        String animation = Settings.System.getString(cv, Settings.System.TRANSITION_ANIMATION_SCALE);
        return StringUtil.toDouble(animation) > 0;
    }

    /**
     * 获取屏幕的宽高
     *
     * @param dm 设备显示对象描述
     * @return int数组, int[0] - width, int[1] - height
     */
    public static int[] getScreenSize(DisplayMetrics dm) {
        int[] result = new int[2];
        result[0] = dm.widthPixels;
        result[1] = dm.heightPixels;
        return result;
    }

    /**
     * Dip转换为实际屏幕的像素值
     *
     * @param dm  设备显示对象描述
     * @param dip dip值
     * @return 匹配当前屏幕的像素值
     */
    public static int getPixelFromDip(DisplayMetrics dm, float dip) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, dm) + 0.5f);
    }

    public static int getPixelFromDip(float dip) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, FoundationContextHolder.context.getResources().getDisplayMetrics()) + 0.5f);
    }

    /**
     * 判断是否arm架构cpu
     *
     * @return arm返回true，否则false
     */
    public static boolean isARMCPU() {
        String cpu = Build.CPU_ABI;
        return cpu != null && cpu.toLowerCase().contains("arm");
    }

    public static String getSystemProperty(String propName) {
        String line = null;
        BufferedReader input = null;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
            return line;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (p != null) {
                p.destroy();
            }
        }

        return line;
    }

    public static float getDesity(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }

    public static boolean isAppInstalled(Context context, String pkgName) {
        if (context == null) {
            return false;
        }

        try {
            context.getPackageManager().getPackageInfo(pkgName, PackageManager.PERMISSION_GRANTED);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * (检查终端是否支持指定的intent)
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        if (context == null || intent == null) {
            return false;
        }

        PackageManager pkgManager = context.getPackageManager();
        if (pkgManager == null) {
            return false;
        }
        List<ResolveInfo> list = pkgManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static boolean isEmulator() {
        if (isEmulatorDevice == null) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) FoundationContextHolder.context.getSystemService(TELEPHONY_SERVICE);
                String imei = telephonyManager.getDeviceId();
                if ((imei == null || imei.equals("000000000000000")) && StringUtil.emptyOrNull(getTelePhoneIMSI())) {
                    isEmulatorDevice = true;
                    return isEmulatorDevice;
                }
                isEmulatorDevice = Build.MODEL.equals("sdk") || Build.MODEL.equals("google_sdk") || Build.BRAND.equals("generic")
                        || Build.MANUFACTURER.contains("Genymotion") || Build.PRODUCT.contains("vbox");
                return isEmulatorDevice;
            } catch (Exception ioe) {
            }
            isEmulatorDevice = false;
        }
        return isEmulatorDevice;
    }

    private static final int BLUETOOTH_OFF = 0;

    public static boolean isBluetoothPersistedStateOn() {
        try {
            if (Build.VERSION.SDK_INT < 17) {
                return Settings.Secure.getInt(FoundationContextHolder.context.getContentResolver(),
                        Settings.Global.BLUETOOTH_ON, 0) != BLUETOOTH_OFF;
            } else {
                return false;
            }
        } catch (Exception e) {

        }
        return false;
    }


    /**
     * 获取当前设备号
     *
     * @return
     */
    public static String getMobileUUID() {
        if (!StringUtil.emptyOrNull(mobileUUID)) {
            return mobileUUID;
        }

        String uuid = "";
        try {
            // 先获取mac
            WifiManager wifi = (WifiManager) FoundationContextHolder.context.getSystemService(Context.WIFI_SERVICE);
            /* 获取mac地址 */
            if (wifi != null) {
                WifiInfo info = wifi.getConnectionInfo();
                if (info != null && info.getMacAddress() != null) {
                    uuid = info.getMacAddress().replace(":", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // 再加上imei
            TelephonyManager telephonyManager = (TelephonyManager) FoundationContextHolder.context.getSystemService(TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
            uuid = uuid + imei;
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (uuid != null && uuid.length() > 64) {
            uuid = uuid.substring(0, 64);
        }
        mobileUUID = uuid;
        return uuid;
    }


    /**
     * 功能描述:Do not keep activities 是否打开
     */
    public static boolean isAlwaysBedestroy() {
        int i = Settings.System.getInt(FoundationContextHolder.context.getContentResolver(), Settings.System.ALWAYS_FINISH_ACTIVITIES, 0);
        return i == 1;
    }


    /**
     * 功能描述:TODO(是否 勾选 不保留活动)
     */
    public static boolean isDontKeepActivities(Application sAppInstance) {
        int setting = Settings.System.getInt(sAppInstance.getContentResolver(), Settings.System.ALWAYS_FINISH_ACTIVITIES, 0);
        return setting == 1;
    }


    public static int getWindowWidth() {
        return windowWidth;
    }

    public static void setWindowWidth(int windowWidth) {
        DeviceUtil.windowWidth = windowWidth;
    }

    public static int getWindowHeight() {
        return windowHeight;
    }

    public static void setWindowHeight(int windowHeight) {
        DeviceUtil.windowHeight = windowHeight;
    }

    public static boolean isSDCardAvailaleSize() {
        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File path = Environment.getExternalStorageDirectory();// 取得sdcard文件路径
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                return availableBlocks * blockSize > 30 * 1024 * 1024;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

    }

    /**
     * 获取整个产品的名称
     */
    public static String getProductName() {
        String product = Build.PRODUCT;

        if (product == null) {
            return "";
        } else {
            return product;
        }
    }

    /**
     * 检测当前是否arm架构
     *
     * @param context context
     * @return boolean
     */
    public static boolean isARMCPU(Context context) {
        return !isX86CPU(context);
    }

    public static boolean isX86CPU(Context context) {
        boolean isX86 = false;
        String osArch = System.getProperty("os.arch");
        String abi1 = DeviceUtil.get(context, "ro.product.cpu.abi");
        String vendor = DeviceUtil.get(context, "ro.cpu.vendor");
        if ((!TextUtils.isEmpty(abi1) && abi1.toLowerCase().startsWith("x86"))
                || "intel".equalsIgnoreCase(vendor)
                || "i686".equalsIgnoreCase(osArch)) {
            isX86 = true;
        }
        return isX86;
    }

    /**
     * 根据给定Key获取值.
     *
     * @return 如果不存在该key则返回空字符串
     */
    public static String get(Context context, String key) {
        String ret = "";
        try {
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = cl.loadClass("android.os.SystemProperties");

            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;
            Method get = SystemProperties.getMethod("get", paramTypes);

            Object[] params = new Object[1];
            params[0] = new String(key);
            ret = (String) get.invoke(SystemProperties, params);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    private static int mStatusBarHeight = 0;

    //获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        if (mStatusBarHeight != 0) {
            return mStatusBarHeight;
        }
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            mStatusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return mStatusBarHeight;
    }


    //获取IP地址
    public static String getIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取屏幕宽度
    public static int getScreenWidth() {
        Display display = ((WindowManager) FoundationContextHolder.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    //获取屏幕高度
    public static int getScreenHeight() {
        Display display = ((WindowManager) FoundationContextHolder.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public static long getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize, totalBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
        } else {
            blockSize = stat.getBlockSize();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            totalBlocks = stat.getBlockCountLong();
        } else {
            totalBlocks = stat.getBlockCount();
        }
        return blockSize * totalBlocks;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static long getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize, availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
        } else {
            blockSize = stat.getBlockSize();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            availableBlocks = stat.getAvailableBlocks();
        }
        return blockSize * availableBlocks;
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    public static long getDiskTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize, totalBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
        } else {
            blockSize = stat.getBlockSize();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            totalBlocks = stat.getBlockCountLong();
        } else {
            totalBlocks = stat.getBlockCount();
        }
        return blockSize * totalBlocks;
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public static long getDiskAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize, availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
        } else {
            blockSize = stat.getBlockSize();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            availableBlocks = stat.getAvailableBlocks();
        }
        return blockSize * availableBlocks;
    }

    /**
     * 获取系统总内存
     *
     * @return 总内存大单位为B。
     */
    public static long getTotalMemorySize() {
        String dir = "/proc/meminfo";
        FileReader fr = null;
        try {
            fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            fr.close();
            br.close();
            return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
        } catch (IOException e) {
            e.printStackTrace();

            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return 0;
    }


    /**
     * 获取当前可用内存，返回数据以字节为单位。
     *
     * @return 当前可用内存单位为B。
     */
    public static long getAvailableMemory() {
        ActivityManager am = (ActivityManager) FoundationContextHolder.context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    /**
     * 获取UserAgent
     *
     * @return
     */
    public static String getUserAgent() {
        String userAgent = "";
        StringBuffer sb = new StringBuffer();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    userAgent = WebSettings.getDefaultUserAgent(FoundationContextHolder.context);
                } catch (Exception e) {
                    userAgent = System.getProperty("http.agent");
                }
            } else {
                userAgent = System.getProperty("http.agent");
            }

            if (userAgent != null) {
                for (int i = 0, length = userAgent.length(); i < length; i++) {
                    char c = userAgent.charAt(i);
                    if (c <= '\u001f' || c >= '\u007f') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }

                if (sb.length() > 0) {
                    sb.append("_CtripAPP_Android_").append(getAppVersion());
                }

                if (sb.length() > 0) {
                    String model = getDeviceModel();
                    sb.append("_cDevice=").append(TextUtils.isEmpty(model) ? "NULL" : model);
                    sb.append("_cSize=").append("w" + getWindowWidth() + "*h" + getWindowHeight());
                    sb.append("_");
                }
            }

        } catch (Exception ex) {

        }
        return sb.toString();
    }

    public static String getAppVersion() {
        String version = "";
        final String versionNameForHuaweiCtch1 = "ctch1";
        if (FoundationContextHolder.context != null) {
            PackageManager packageManager = FoundationContextHolder.context.getPackageManager();
            try {
                PackageInfo info = packageManager.getPackageInfo(FoundationContextHolder.context.getPackageName(), 0);
                version = info.versionName.endsWith(versionNameForHuaweiCtch1) ? info.versionName
                        .replace(versionNameForHuaweiCtch1, "") : info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return version;
    }

    /**
     * 判断是否有无SIM卡
     *
     * @return 是否有无SIM卡
     */
    public static boolean isNoHaveSIM() {
        TelephonyManager telephonyManager = (TelephonyManager) FoundationContextHolder.context.getSystemService(TELEPHONY_SERVICE);
        return (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_ABSENT);
    }

    /**
     * 功能描述:判断设备是否是TabletPC
     */
    public static boolean isTablet() {
        if (isPad == null) {
            WindowManager windowManager = (WindowManager) FoundationContextHolder.context.getSystemService(
                    Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();
            display.getMetrics(displayMetrics);

            double x = Math.pow(displayMetrics.widthPixels / displayMetrics.xdpi, 2);
            double y = Math.pow(displayMetrics.heightPixels / displayMetrics.ydpi, 2);
            double screenInches = Math.sqrt(x + y);

            isPad = screenInches >= 7.5; //屏幕对角线大于7.5inch的为平板
        }
        return isPad;
    }

    /**
     * 判断Wi-Fi是否打开
     *
     * @param context
     * @return
     */
    public static boolean isWifiEnable(Context context) {
        if (context == null) {
            return false;
        }
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wm != null && wm.isWifiEnabled();
    }

    /**
     * 判断 总是允许扫描Wi-Fi 是否开启
     *
     * @param context
     * @return
     */
    public static boolean isWifiScanAlwaysAvailable(Context context) {
        if (context == null) {
            return false;
        }
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                return wm != null && wm.isScanAlwaysAvailable();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public static Map<String, String> getSimInfo() {
        Map<String, String> simInfos = new HashMap<>();
        try {
            TelephonyManager manager = (TelephonyManager) FoundationContextHolder.context.getSystemService(TELEPHONY_SERVICE);
            Class clazz = manager.getClass();
            Method getImei = clazz.getDeclaredMethod("getImei", int.class);//(int slotId)
            if (Build.VERSION.SDK_INT > 20) {
                Object imei1 = getImei.invoke(manager, 0);
                if (imei1 != null) {
                    simInfos.put("imei1", imei1.toString());
                }
                Object imei2 = getImei.invoke(manager, 1);
                if (imei2 != null) {
                    simInfos.put("imei2", imei2.toString());
                }
                if (Build.VERSION.SDK_INT > 25) {
                    Method getMeid = clazz.getDeclaredMethod("getMeid");//(int slotId)
                    Object meid = getMeid.invoke(manager);
                    if (meid != null) {
                        simInfos.put("meid", meid.toString());
                    }
                } else {
                    if (TelephonyManager.PHONE_TYPE_CDMA == manager.getPhoneType()) {
                        simInfos.put("meid", manager.getDeviceId());
                    }
                }
            } else {
                if (TelephonyManager.PHONE_TYPE_CDMA == manager.getPhoneType()) {
                    simInfos.put("meid", manager.getDeviceId());
                } else {
                    simInfos.put("imei1", manager.getDeviceId());
                }
            }
        } catch (Throwable e) {
        }
        return simInfos;
    }

    public static boolean isApkDebugable() {
        try {
            ApplicationInfo info = FoundationContextHolder.getContext().getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) FoundationContextHolder.getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (mActivityManager
                .getRunningAppProcesses() == null) {
            return "";
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }

    public static boolean isAppOnForeground() {
        List<ActivityManager.RunningAppProcessInfo> appProcesses = ((ActivityManager) FoundationContextHolder.getContext()
                .getSystemService(Context.ACTIVITY_SERVICE))
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(FoundationContextHolder.context.getPackageName())
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    public static String getDeviceID() {
        if (DeviceUtil.getSDKVersionInt() >= Build.VERSION_CODES.M) {
            return DeviceUtil.getMacAddress() + Build.SERIAL;
        } else {
            return DeviceUtil.getMacAddress() + DeviceUtil.getTelePhoneIMEI();
        }
    }
}
