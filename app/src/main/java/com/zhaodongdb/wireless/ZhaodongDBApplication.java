package com.zhaodongdb.wireless;

import android.app.Application;
import android.content.Context;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.jd.jrapp.push.IJRPush;
import com.jd.jrapp.push.PushManager;
import com.zhaodongdb.common.component.BaseApplication;
import com.zhaodongdb.common.config.AppConfig;
import com.zhaodongdb.common.utils.DeviceUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZhaodongDBApplication extends BaseApplication implements ReactApplication {

    private final ReactNativeHost mReactNativeHost =
            new ReactNativeHost(this) {
                @Override
                public boolean getUseDeveloperSupport() {
                    return BuildConfig.DEBUG;
                }

                @Override
                protected List<ReactPackage> getPackages() {
                    List<ReactPackage> packages = new ArrayList<>(Arrays.<ReactPackage>asList(
                            new MainReactPackage()
                    ));
//                    packages.add(new CustomToastPackage());
                    return packages;
                }

                @Override
                protected String getJSMainModuleName() {
                    return "index";
                }
            };

    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 设置环境
//        AppConfig.setEnv(AppConfig.EnvType.DEV);

        // 初始化JRPush
        PushManager.push = new IJRPush() {
            @Override
            public Application getApplication() {
                return getInstance();
            }

            @Override
            public String BASE_COMMON_SURL() {
                return null;
            }

            @Override
            public boolean isLogin() {
                return true;
            }

            @Override
            public String jdPin() {
                return "user1234567890";
            }

            @Override
            public String imEi() {
                return DeviceUtil.getDeviceID();
            }

            @Override
            public String appId() {
                return null;
            }

            @Override
            public String appSecret() {
                return null;
            }

            @Override
            public String appVersion() {
                return DeviceUtil.getAppVersion();
            }

            @Override
            public String storeSource() {
                return "unknown";
            }
        };
        PushManager.getInstance().setType(1);
        PushManager.getInstance().initAndGetToken("", "");

        SoLoader.init(this, /* native exopackage */ false);
        initializeFlipper(this); // Remove this line if you don't want Flipper enabled
    }

    /**
     * Loads Flipper in React Native templates.
     *
     * @param context
     */
    private static void initializeFlipper(Context context) {
        if (BuildConfig.DEBUG) {
            try {
        /*
         We use reflection here to pick up the class that initializes Flipper,
        since Flipper library is not available in release mode
        */
                Class<?> aClass = Class.forName("com.facebook.flipper.ReactNativeFlipper");
                aClass.getMethod("initializeFlipper", Context.class).invoke(null, context);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
