package com.hzeng;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

import static com.hzeng.Config.getIpValidateUrl;

enum IPProxy {
    IP, PORT
}

class IPURL extends CrawlerURL implements Serializable {

    IPURL(String uri) {
        super(uri);
    }

    void generatorFromAPI(Storage storage) throws InterruptedException {

        Document doc = Jsoup.parse(getResponseOfURL());

        while (true) {

            doc.getElementById("ip_list")
                    .getElementsByTag("tr")
                    .forEach((element -> {
                        Elements IP_elements = element.getElementsByTag("td");
                        String ip = IP_elements.first().nextElementSibling().nextElementSibling().text(),
                                port = IP_elements.first().nextElementSibling().nextElementSibling().nextElementSibling().text();
                        IPPool.putIPIntoIPPool(ip, port);
                    }));

            getSleep();
        }
    }
}

public class IPPool {

    private static int number;

    static int getNumber() {
        return number;
    }

    static boolean validateIP(EnumMap<IPProxy, Object> IP) throws IOException {

        String ip = (String) IP.get(IPProxy.IP);
        int port = Integer.valueOf((String) IP.get(IPProxy.PORT));

        Proxy proxy = getIPProxy(IP);

        URL crawlerUrl = new URL(getIpValidateUrl());
        HttpsURLConnection url_connection = (HttpsURLConnection) crawlerUrl.openConnection(proxy);

        if (url_connection.getResponseCode() >= 400) {
            System.out.println(MessageFormat.format("IP: {0} Port: {1} cannot use", ip, port));
            return false;
        }

        return true;
    }

    private static int getRandomInt(int max_len) {
        return (int) (Math.random() * max_len);
    }

    private static <E> E getRandomElement(Set<E> set) {

        int rn = getRandomInt(set.size());
        int count = 0;
        for (E e : set) {
            if (count == rn) {
                return e;
            }
            count++;
        }
        return null;
    }

    static EnumMap<IPProxy, Object> getIPFromIPPool() throws IOException {

        EnumMap<IPProxy, Object> retMap = new EnumMap<IPProxy, Object>(IPProxy.class);

        Set<String> keys = RedisAPI.keys("IP*");

        do {
            String key = getRandomElement(keys);
            List<String> values = RedisAPI.lrange(key, 0, -1);
            retMap.put(IPProxy.IP, values.get(0));
            retMap.put(IPProxy.PORT, values.get(1));
        } while (!validateIP(retMap));

        return retMap;
    }

    static void putIPIntoIPPool(String ip, String port) {

        RedisAPI.lpush("IP" + number, ip);
        RedisAPI.lpush("IP" + number, port);
        System.out.println(MessageFormat.format("Add IP: {0} Port: {1} into IP Pool", ip, port));
        number++;
    }

    static Proxy getIPProxy(EnumMap<IPProxy, Object> IP) {

        String ip = (String) IP.get(IPProxy.IP);
        int port = Integer.valueOf((String) IP.get(IPProxy.PORT));

        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
    }
}
