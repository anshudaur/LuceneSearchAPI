package com.ovgu.dke.ir.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @author Anshu Daur
 * @author Akanksha Saxena
 * @author Shivalika Suman
 * @author Chinmaya Hegde
 */
public class LuceneSearchIndex {
	/**
	 * 
	 * @param textToFind This is the query input from the user
	 * @param searcher   This is to select between two ranking models
	 * @return hits This is the number of documents containing the query
	 * @throws Exception This Generates parse exception
	 */
	/* directory containing the lucene indexes */
	static TopDocs searchIndexDoc(String textToFind, IndexSearcher searcher) throws Exception {
		/* Create search query */
		QueryParser query_Parser = new QueryParser("contents", new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents( String fieldName ) {
            	/* Created an Analyzer instance to analyze token streams to analyze the text */
    			/* Created a customized subclass to implement porter stemmer algorithm on the token stream*/
                TokenStreamComponents token_Stream = new TokenStreamComponents( new StandardTokenizer() );
                /* Convert tokens to lowercase and apply PorterStemmer*/
                token_Stream = new TokenStreamComponents( token_Stream.getTokenizer(), new LowerCaseFilter( token_Stream.getTokenStream() ) );
                token_Stream = new TokenStreamComponents( token_Stream.getTokenizer(), new PorterStemFilter( token_Stream.getTokenStream() ) );
                return token_Stream;
            }
        });
		Query query = query_Parser.parse(textToFind);

		/* Search the Index */
		TopDocs hits = searcher.search(query, 10);
		return hits;
	}

	/**
	 * 
	 * @param path_to_document_folder This is the path to corpus
	 * @param ranking_Model           Vector space model or Okapi Model
	 * @return index_Searcher
	 * @throws IOException
	 */
	IndexSearcher create_Searcher(String path_to_document_folder, String ranking_Model) throws IOException {
		Directory dir = FSDirectory.open(Paths.get(path_to_document_folder));
		/* Code to an interface for accessing the index created by Lucene */
		IndexReader index_Reader = DirectoryReader.open(dir);
		/* Code for Index searcher */
		IndexSearcher index_Searcher = new IndexSearcher(index_Reader);
		/* Code for computing rank for Okapi Ranking Model */
		if (ranking_Model.contentEquals("OK")) {
			index_Searcher.setSimilarity(new BM25Similarity());
		} else {
			/* Code for computing rank for Vector Space Ranking model */
			TFIDFSimilarity similarity = new ClassicSimilarity();
			index_Searcher.setSimilarity(similarity);
		}
		return index_Searcher;
	}
}
