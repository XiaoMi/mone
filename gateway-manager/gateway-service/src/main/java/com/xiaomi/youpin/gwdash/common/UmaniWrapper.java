package com.xiaomi.youpin.gwdash.common;

import com.xiaomi.mone.umami.Umami;

public class UmaniWrapper {

        private volatile static UmaniWrapper instance;

        public void sentUsername(String username) {
                Umami.sendEvent("gateway", username);
        }

        private UmaniWrapper(String url, String website) {
                Umami.setUrl(url);
                Umami.setWebsite(website);
                Umami.setReferer("https://127.0.0.1/");
                Umami.setOrigin("https://127.0.0.1");
        }

        public static UmaniWrapper getUmani(String url, String website) {
                if (instance == null) {
                        synchronized (UmaniWrapper.class) {
                                if (instance == null) {
                                        instance = new UmaniWrapper(url, website);
                                }
                        }
                }
                return instance;
        }
}
