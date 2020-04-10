package com.zhaodongdb.common.config;

public class AppConfig {

    public enum EnvType {
        DEV, SIT, UAT, PRD
    }

    private static EnvType envType = EnvType.PRD;

    public static void setEnv(AppConfig.EnvType type) {
        envType = type;
    }

    public static AppConfig.EnvType getEnv() {
        return envType;
    }
}
