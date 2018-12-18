package com.hzeng;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;

public class Controller {

    public static void main(String[] args) throws IOException {

        Storage storage = new Storage();
//recovery data

        storage.setToCrawlUrls(StreamUtils.readObjectForList(new File(Config.getUrlFile())));
        storage.setCrawledUrls(new HashSet<CrawlerURL>(Objects.requireNonNull(StreamUtils.readObjectForList(new File(Config.getUrlVisit())))));
//IP Pool start

        Generator IPPoolTread = new Generator("IP Pool Tread", storage, new IPURL(Config.getIpPoolUrl()));
        IPPoolTread.start();

        Generator DXYSeedThread = new Generator("DXY URL generator Thread", storage, new DxyCrawlerURL(Config.getDxySeedUrl()));
        DXYSeedThread.start();

        int threadNumber = Config.getThreadNumber();
        for (int i = 0; i < threadNumber; i++) {
            Crawler crawler = new Crawler("Thread " + i, storage);
            crawler.start();
        }
    }
}
