package com.hzeng;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class Crawler implements Runnable {

    Storage storage;

    private Thread t;
    private String threadName;

    Crawler(String name, Storage storage) {
        threadName = name;
        this.storage = storage;
    }

    private void fetchPage() throws InterruptedException, IOException {

        Document doc;
        CrawlerURL current_Crawler_url;

        while (true) {

            current_Crawler_url = storage.popToCrawl();
            doc = Jsoup.parse(current_Crawler_url.getResponseOfURL());
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
