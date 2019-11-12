package com.zhaodongdb.common.config;

public class AppConfig {

    public enum EnvType {
        DEV, SIT, UAT, PRD
    }

    public static AppConfig.EnvType getEnv() {
        EnvType envType = EnvType.PRD;
        return envType;
    }
}
