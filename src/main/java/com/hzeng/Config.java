package com.hzeng;

import java.net.URL;

class Config {

    private static final String INDEX_STORE_PATH = "./index";
    private static final String DATA_STORE_PATH = "./data";
    private static final int THREAD_NUMBER = 1;
    private final static String URL_FILE = ".\\url";
    private final static String URL_VISIT = ".\\url_visit";

    private static final String IP_VALIDATE_URL = "https://www.baidu.com";
    private static final String IP_POOL_URL = "https://www.xicidaili.com/wn/";

    private static final String DXY_SEED_URL = "https://dxy.com/diseases";

    static String getIndexStorePath() {
        return INDEX_STORE_PATH;
    }

    static String getDataStorePath() {
        return DATA_STORE_PATH;
    }

    static int getThreadNumber() {
        return THREAD_NUMBER;
    }

    static String getIpPoolUrl() {
        return IP_POOL_URL;
    }

    private static final String REDIS_HOST = "127.0.0.1";
    private static final int REDIS_PORT = 6379;

    static String getRedisHost() {
        return REDIS_HOST;
    }

    static int getRedisPort() {
        return REDIS_PORT;
    }

    static String getIpValidateUrl() {
        return IP_VALIDATE_URL;
    }

    static String getUrlFile() {
        return URL_FILE;
    }

    static String getDxySeedUrl() {
        return DXY_SEED_URL;
    }

    static String getUrlVisit() {
        return URL_VISIT;
    }
}
