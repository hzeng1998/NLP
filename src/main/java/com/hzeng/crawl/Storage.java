package com.hzeng.crawl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Storage {

    private List<CrawlerURL> toCrawlUrls;
    private Set<CrawlerURL> crawledUrls;

    public Storage() {
        toCrawlUrls = new ArrayList<CrawlerURL>();
    }

    public List<CrawlerURL> getToCrawlUrls() {
        return toCrawlUrls;
    }

    public Set<CrawlerURL> getCrawledUrls() {
        return crawledUrls;
    }

    public void setCrawledUrls(Set<CrawlerURL> crawledUrls) {
        this.crawledUrls = crawledUrls;
    }

    synchronized public void setToCrawlUrls(List<CrawlerURL> toCrawlUrls) {
        this.toCrawlUrls = toCrawlUrls;
    }

    synchronized public void addToCrawl(CrawlerURL crawlerURL) {
        toCrawlUrls.add(crawlerURL);
        System.out.println(MessageFormat.format("{0} is going to be crawled", crawlerURL.getUri()));
        notify();
    }

    synchronized public void addCrawled(CrawlerURL crawlerURL) {
        crawledUrls.add(crawlerURL);
    }

    synchronized public CrawlerURL popToCrawl() {
        if (toCrawlUrls.size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        CrawlerURL crawlerURL = toCrawlUrls.get(0);
        toCrawlUrls.remove(crawlerURL);
        addCrawled(crawlerURL);

        return crawlerURL;
    }
}