package com.hzeng;

import java.io.File;
import java.io.IOException;

public class Generator implements Runnable {

    Storage storage;

    CrawlerURL seed;

    public Generator(String name, Storage storage, CrawlerURL seed) {
        this.storage = storage;
        threadName = name;
        this.seed = seed;
    }

    private Thread t;
    private String threadName;

    private void generateURL() throws InterruptedException, IOException {

        seed.generatorFromAPI(storage);
        StreamUtils.<CrawlerURL>writeObject(storage.getToCrawlUrls(), new File(""));
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
