package com.hzeng.crawl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.*;
import java.net.Proxy;

import static com.hzeng.crawl.IPPool.randomGetIPFromIPPool;
import static com.hzeng.crawl.IPPool.getIPProxy;

class CrawlerURL implements Serializable{

    private String uri;

    CrawlerURL() { }

    CrawlerURL(String url) {
        uri = url;
    }

    String getUri() {
        return uri;
    }

    void setUri(String url) {
        uri = url;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj instanceof CrawlerURL) {
            CrawlerURL crawlerUrl = (CrawlerURL) obj;
            return crawlerUrl.uri.equals(this.uri);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    Document contentOfURL(Proxy proxy) throws IOException {

        if (proxy == null)
            proxy = getIPProxy(randomGetIPFromIPPool());

        Document content = null;
        do {
            try {
                content = Jsoup.connect(uri).userAgent(Setting.getAgent()).proxy(proxy).get();
            } catch (Exception e) {
                e.printStackTrace();
                proxy = getIPProxy(randomGetIPFromIPPool());
            }
        } while (content == null);

        return content;
    }

    Connection.Response responseOfURL(Proxy proxy) throws IOException {

        if (proxy == null)
            proxy = getIPProxy(randomGetIPFromIPPool());

        Connection.Response content = null;
        do {
            try {
                content = Jsoup.connect(uri).userAgent(Setting.getAgent()).proxy(proxy).ignoreContentType(true).execute();
            } catch (Exception e) {
                e.printStackTrace();
                proxy = getIPProxy(randomGetIPFromIPPool());
            }
        } while (content == null);

        return content;
    }

    public void getSleep() throws InterruptedException {
        Thread.sleep(15 * 60 * 1000);
    }

    void generatorFromAPI(Storage storage, Proxy proxy) throws InterruptedException, IOException {}

    void saveData(Document doc) throws IOException {}
    void saveData(String doc) throws IOException {}

}
