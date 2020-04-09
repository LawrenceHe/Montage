var zhaodongJsSdk = {
    /**
     * 判断是否为招东APP
     *
     * 输入样例：userAgent = Chrome/74.0.3729.185 Mobile Safari/537.36_ZhaodongDB_Android_1.0_cDevice=Android SDK built for x86_cSize=w1440*h2392_
     *
     * 返回样例：true
     */
    isZhaodong: function(){
        var userAgent = navigator.userAgent.toLowerCase();
        return /zhaodong/.test(userAgent);
    },

    /**
     * 获取APP版本号（仅招东APP内）
     *
     * 输入样例：userAgent = Chrome/74.0.3729.185 Mobile Safari/537.36_ZhaodongDB_Android_1.0_cDevice=Android SDK built for x86_cSize=w1440*h2392_
     *
     * 返回样例：1.0.0
     */
    getAppVersion: function(){
        if (this.isZhaodong()){
            var userAgent = navigator.userAgent;
            var key = '_ZhaodongDB_Android_';
            var appVersion = userAgent.substring(userAgent.indexOf(key) + key.length);
            appVersion = appVersion.substring(0, appVersion.indexOf('_')).trim();
            return appVersion;
        }
        return null;
    }

}