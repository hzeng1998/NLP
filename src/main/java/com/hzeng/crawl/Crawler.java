package com.hzeng.crawl;

import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.Proxy;

public class Crawler implements Runnable {

    Storage storage;

    private Thread t;
    private String threadName;

    Proxy proxy;

    Crawler(String name, Storage storage) {
        threadName = name;
        this.storage = storage;
    }

    private void fetchPage() throws InterruptedException, IOException {

        Document doc;
        CrawlerURL current_Crawler_url;

        while (true) {

            current_Crawler_url = storage.popToCrawl();
            doc = current_Crawler_url.contentOfURL(proxy);
            current_Crawler_url.saveData(doc);
            current_Crawler_url.getSleep();
        }
    }

    @Override
    public void run() {
        try {
            fetchPage();
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
