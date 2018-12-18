package com.hzeng;

import org.jsoup.nodes.Document;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.Proxy;
import java.net.URL;
import static com.hzeng.IPPool.getIPFromIPPool;
import static com.hzeng.IPPool.getIPProxy;

class CrawlerURL implements Serializable{

    private String uri;

    CrawlerURL() { }

    CrawlerURL(String url) {
        uri = url;
    }

    String getUri() {
        return uri;
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

    private String connectToURL() throws IOException {

        StringBuilder bs = new StringBuilder();

        Proxy proxy = getIPProxy(getIPFromIPPool());

        URL url = new URL(uri);

        HttpsURLConnection url_connection = (HttpsURLConnection)url.openConnection(proxy);

        url_connection.connect();

        InputStream is = url_connection.getInputStream();

        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));

        String line;

        while((line = buffer.readLine()) != null) { bs.append(line); }

        return bs.toString();
    }

    public void getSleep() throws InterruptedException {
        Thread.sleep(15 * 60 * 1000);
    }

    String getResponseOfURL() throws InterruptedException {

        String response;
        do {
            response = null;
            try {
                response = connectToURL();
            } catch (IOException e) {
                e.printStackTrace();
                getSleep();
            }

        } while (response == null);

        return response;
    }

    void generatorFromAPI(Storage storage) throws InterruptedException {}

    void saveData(Document doc) throws IOException {}
    void saveData(String doc) throws IOException {}

    static OutputStream saveToFile(String file_name) throws IOException {

        File file = new File(file_name);
        boolean result = false;

        if (!file.exists()) {

            if (!file.getParentFile().exists())
                while (! result)
                    result = file.getParentFile().mkdirs();
            result = false;

            while (!result)
                result = file.createNewFile();
        }

        return new FileOutputStream(file);
    }
}
