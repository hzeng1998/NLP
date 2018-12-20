package com.hzeng.index;

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
import java.util.ArrayList;

public class DocSearch {

    Analyzer analyzer;
    IndexSearcher indexSearcher;

    public DocSearch() throws IOException {

        Directory directory = FSDirectory.open(new File(Setting.getIndexStorePath()));
        IndexReader indexReader = DirectoryReader.open(directory);

        indexSearcher = new IndexSearcher(indexReader);

        analyzer = new IKAnalyzer();
    }

    public ArrayList<String> search(String args) throws IOException {

        QueryParser parser = new QueryParser("search", analyzer);
        Query query = null;

        ArrayList<String> match_doc = new ArrayList<>();

        try {
            query = parser.parse(args);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TopDocs topDocs = indexSearcher.search(query, com.hzeng.expan.Setting.getMatchDocs());

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;
            Document document = indexSearcher.doc(docID);

            match_doc.add(document.get("question") + document.get("answer"));
        }

        return match_doc;
    }
}
