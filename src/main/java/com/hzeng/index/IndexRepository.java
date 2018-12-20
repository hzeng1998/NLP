package com.hzeng.index;

import com.hzeng.Config;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Objects;

public class IndexRepository {

    public static void main(String[] args) throws IOException {

        Directory directory = FSDirectory.open(new File(Setting.getIndexStorePath()));
        File files = new File(Config.getDataStorePath());

        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);

        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

        for (File file : Objects.requireNonNull(files.listFiles())) {

            String fileContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            JSONArray jsonArray = new JSONArray(fileContent);

            for (int index = 0; index < jsonArray.length(); index++) {
                Document document = new Document();
                JSONObject jsonObject = jsonArray.getJSONObject(index);
                //for coding error!
                String questionField = jsonObject.has("questionFiled") ?
                        jsonObject.getString("questionFiled") : jsonObject.getString("questionField");

                document.add(new StringField("question", jsonObject.getString("question"), Field.Store.YES));
                document.add(new StringField("answer", jsonObject.getString("answer"), Field.Store.YES));
                document.add(new TextField("search", jsonObject.getString("question")
                        + jsonObject.getString("answer")
                        + questionField
                        + jsonObject.getString("origin"), Field.Store.NO));
                document.add(new StringField("questionField", questionField, Field.Store.YES));
                document.add(new StringField("origin", jsonObject.getString("origin"), Field.Store.YES));
                indexWriter.addDocument(document);
            }

            System.out.println(MessageFormat.format("Build index for {0}", file.getName()));
        }
        indexWriter.close();
    }

}
