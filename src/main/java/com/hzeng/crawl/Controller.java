package com.hzeng.crawl;

import com.hzeng.Config;

import java.io.IOException;

public class Controller {

    public static void main(String[] args) throws IOException {

        Storage storage = new Storage();

        //database

//recovery data

//        storage.setToCrawlUrls(StreamUtils.readObjectForList(Config.getUrlFile()));
 //       storage.setCrawledUrls(new HashSet<CrawlerURL>(Objects.requireNonNull(StreamUtils.readObjectForList(Config.getUrlVisit()))));
//IP Pool start

        Generator IPPoolTread = new Generator("IP Pool Tread", storage, new IPURL(Setting.getIpPoolUrl()));
        IPPoolTread.start();

        Generator DXYSeedThread = new Generator("DXY URL generator Thread", storage, new DxyCrawlerURL(Setting.getDxySeedUrl()));
        DXYSeedThread.start();

       // int threadNumber = Config.getThreadNumber();
       // for (int i = 0; i < threadNumber; i++) {
        //    Crawler crawler = new Crawler("Thread " + i, storage);
         //   crawler.start();
       // }
    }
}
