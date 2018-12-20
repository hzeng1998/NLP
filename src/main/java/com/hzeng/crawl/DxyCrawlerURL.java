package com.hzeng.crawl;

import com.hzeng.Config;
import com.hzeng.StreamUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Proxy;
import java.text.MessageFormat;
import java.util.UUID;

class DxyCrawlerURL extends CrawlerURL {

    private DxyCrawlerURL(int id) {
        super("https://dxy.com/disease/" + id);
    }

    public DxyCrawlerURL(String uri) { super(uri); }

    void generatorFromAPI(Storage storage, Proxy proxy) throws InterruptedException, IOException {

        Document doc = contentOfURL(proxy);

        Elements nav_bar_item = doc.select("li.section-navbar-item > a");

        for (Element e : nav_bar_item) {
            String group_name = e.attr("href");
            group_name = group_name.substring(25, group_name.length() - 1);

            int total_pages = 1;

            for (int index = 1; index <= total_pages; index++) {

                getSleep();

                String json_string =
                        new CrawlerURL("https://dxy.com/view/i/disease/list?section_group_name=" + group_name + "&page_index=" + index)
                                .responseOfURL(proxy).body();

                JSONObject index_json = new JSONObject(json_string);

                index_json = (JSONObject) index_json.get("data");

                if (index == 1) {
                    total_pages = index_json.getInt("total_pages");
                }

                JSONArray item_array = (JSONArray) index_json.get("items");

                for (int j = 1; j < item_array.length(); j++) {
                    JSONObject item = (JSONObject) item_array.get(j);
                    storage.addToCrawl(new DxyCrawlerURL(item.getInt("id")));
                }
            }
        }
    }

    public void getSleep() throws InterruptedException {
        Thread.sleep(200);
    }

    void saveData(Document doc) throws IOException {
        JSONArray jsonArray = new JSONArray();
        int count = 0;

        Element diseaseCard = doc.select("div.disease-card-info").first();
        String diseaseInfoTitle = diseaseCard.child(0).text();
        String diseaseInfoDescribe = diseaseCard.child(1).text();

        jsonArray.put(count++, new JSONObject()
                .put("question", diseaseInfoTitle)
                .put("answer", diseaseInfoDescribe)
                .put("questionField", diseaseInfoTitle)
                .put("origin", getUri()));

        Elements diseaseDetail = doc.select("div.disease-detail-card");

        for (Element element : diseaseDetail) {
            String questionField = diseaseInfoTitle + "-" + element.select("h3").first().text();

            element = element.select("div.disease-detail-card-deatil").first();

            JSONObject question = null;
            StringBuilder value = null;

            Elements questions = element.children();

            for (Element child : questions) {
                if (child.tagName().equals("h2")) {
                    if (value != null) {
                        question.put("answer", value.toString())
                                .put("questionField", questionField)
                                .put("origin", getUri());
                        jsonArray.put(count++, question);
                    }
                    value = new StringBuilder();
                    question = new JSONObject();
                    question.put("question", child.text());
                }
                if (child.tagName().equals("p")) {
                    if (value == null) {
                        value = new StringBuilder();
                        question = new JSONObject();
                        question.put("question", questionField);
                    }

                    value.append(child.text()).append("\n");
                }
                if (child.tagName().equals("ol")) {
                    for (Element li : child.select("li")) {
                        assert value != null;
                        value.append("\t").append(li.text()).append("\n");
                    }
                }
                if (child.tagName().equals("ul")) {
                    for (Element li : child.select("li")) {
                        assert value != null;
                        value.append("\t").append(li.text()).append("\n");
                    }
                }
            }
            if (value != null) {
                question.put("answer", value.toString())
                        .put("questionField", questionField)
                        .put("origin", getUri());
                jsonArray.put(count++, question);
            }
        }

        String document_path = Config.getDataStorePath() + "\\" + UUID.randomUUID().toString();

        OutputStream writer = StreamUtils.saveToFile(document_path);
        writer.write(jsonArray.toString().getBytes());
        writer.flush();
        writer.close();

        System.out.println(MessageFormat.format("Saved {0}", document_path));
    }
}
