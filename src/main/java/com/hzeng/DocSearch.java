package com.hzeng;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class DocSearch {
    public static void main(String[] args) throws IOException {

        Directory directory = FSDirectory.open(new File(Config.getIndexStorePath()));
        IndexReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Analyzer analyzer = new IKAnalyzer();

        Scanner in = new Scanner(System.in);

        while (in.hasNext()) {
            String queryString = in.next();
            QueryParser parser = new QueryParser("search", analyzer);
            Query query = null;
            try {
                query = parser.parse(queryString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            TopDocs topDocs = indexSearcher.search(query, 5);

            System.out.println("Search answer:" + topDocs.totalHits);

            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                int docID = scoreDoc.doc;
                Document document = indexSearcher.doc(docID);

                System.out.println(document.getField("question").stringValue());
                System.out.println(document.getField("answer").stringValue());
                System.out.println(document.getField("questionField").stringValue());
                System.out.println(document.getField("origin").stringValue());
                System.out.println();
                System.out.println("========================================");
            }
        }

        indexReader.close();
    }
}
