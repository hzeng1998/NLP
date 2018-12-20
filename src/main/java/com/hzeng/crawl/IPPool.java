package com.hzeng.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

enum IPProxy {
    IP, PORT
}

class IPURL extends CrawlerURL implements Serializable {

    IPURL(String uri) {
        super(uri);
    }

    void generatorFromAPI(Storage storage, Proxy proxy) throws InterruptedException, IOException {

        Document doc = null;

        while (true) {

            int page = 1;

            //IPPool.validAllIPProxy();

            for (String key : RedisAPI.keys("IP*")) {
                RedisAPI.del(key);
            }

            while (RedisAPI.keys("IP*").size() <= 20) {

                setUri(getUri() + page);

                while (doc == null) {

                    try {
                        doc = Jsoup.connect(getUri()).userAgent(Setting.getAgent()).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                doc.getElementById("ip_list")
                        .getElementsByTag("tr")
                        .forEach((element -> {
                            Elements IP_elements = element.getElementsByTag("td");
                            if (IP_elements.size() != 0) {
                                String ip = IP_elements.first().nextElementSibling().text(),
                                        port = IP_elements.first().nextElementSibling().nextElementSibling().text();

                                EnumMap<IPProxy, Object> new_IP = new EnumMap<IPProxy, Object>(IPProxy.class);
                                new_IP.put(IPProxy.IP, ip);
                                new_IP.put(IPProxy.PORT, port);

                                if (!RedisAPI.exists("IP" + ip + port)) {

                                    try {
                                        if (IPPool.validateIP(new_IP)) {
                                            System.out.println(ip + ":" + port);
                                            IPPool.putIPIntoIPPool(ip, port);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }));

                page++;
            }
            getSleep();
        }
    }
}

public class IPPool {

    static boolean validateIP(EnumMap<IPProxy, Object> IP) throws IOException {

        String ip = (String) IP.get(IPProxy.IP);
        int port = Integer.valueOf((String) IP.get(IPProxy.PORT));

        Proxy proxy = getIPProxy(IP);

        try {
            Jsoup.connect(Setting.getIpValidateUrl()).proxy(proxy).ignoreContentType(true).get();
        } catch (Exception e) {
            System.out.println(ip + ":" + port + "cannot use.");
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

    static EnumMap<IPProxy, Object> randomGetIPFromIPPool() throws IOException {

        EnumMap<IPProxy, Object> retMap = new EnumMap<IPProxy, Object>(IPProxy.class);

        Set<String> keys = RedisAPI.keys("IP*");

        if (keys.isEmpty()) {
            retMap.put(IPProxy.IP, "127.0.0.1");
            retMap.put(IPProxy.PORT, "1080");
            return retMap;
        }

        String key;
        do {
            key = getRandomElement(keys);
        } while (!validateIP(retMap = getIPFromIPPool(key)));

        return retMap;
    }

    static void putIPIntoIPPool(String ip, String port) {

        RedisAPI.lpush("IP" + ip + port, ip);
        RedisAPI.lpush("IP" + ip + port, port);
        System.out.println(MessageFormat.format("Add IP: {0} Port: {1} into IP Pool", ip, port));
    }

    static Proxy getIPProxy(EnumMap<IPProxy, Object> IP) {

        String ip = (String) IP.get(IPProxy.IP);
        int port = Integer.valueOf((String) IP.get(IPProxy.PORT));

        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
    }

    static EnumMap<IPProxy, Object> getIPFromIPPool(String key) {

        EnumMap<IPProxy, Object> retMap = new EnumMap<IPProxy, Object>(IPProxy.class);
        List<String> values = RedisAPI.lrange(key, 0, -1);

        if (values.size() == 0) {
            retMap.put(IPProxy.IP, "127.0.0.1");
            retMap.put(IPProxy.PORT, "1080");
        }
        else {
            retMap.put(IPProxy.IP, values.get(1));
            retMap.put(IPProxy.PORT, values.get(0));
        }

        return retMap;
    }

    static void validAllIPProxy() throws IOException {

        for (String key : RedisAPI.keys("IP*")) {

            if (!validateIP(getIPFromIPPool(key))) {
                RedisAPI.del(key);
            }
        }

    }
}
