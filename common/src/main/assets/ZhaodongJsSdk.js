var zhaodongJsSdk = {
    /**
    }
     * 判断是否为招东APP
     *
     * 输入样例：userAgent = Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_4 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13G35,(zhaodong 5.5.0/47 v2)
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
     * 输入样例：userAgent = Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_4 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13G35,(zhaodong 5.5.0/47 v2)
     *
     * 返回样例：5.5.0
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