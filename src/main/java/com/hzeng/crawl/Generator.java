package com.hzeng.crawl;

import com.hzeng.Config;
import com.hzeng.StreamUtils;
import org.omg.CORBA.SetOverrideType;

import java.io.IOException;
import java.net.Proxy;
import java.util.Set;

public class Generator implements Runnable {

    Storage storage;

    CrawlerURL seed;

    Proxy proxy;

    public Generator(String name, Storage storage, CrawlerURL seed) {
        this.storage = storage;
        threadName = name;
        this.seed = seed;
    }

    private Thread t;
    private String threadName;

    private void generateURL() throws InterruptedException, IOException {

        seed.generatorFromAPI(storage, proxy);
        StreamUtils.<CrawlerURL>writeObject(storage.getToCrawlUrls(), Setting.getUrlFile());
    }

    @Override
    public void run() {
        try {
            generateURL();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void start() {
        System.out.println("Starting " + threadName);
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
