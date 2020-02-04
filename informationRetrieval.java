package com.ovgu.dke.ir.lucene;

import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 * @author Anshu Daur
 * @author Akanksha Saxena
 * @author Shivalika Suman
 * @author Chinmaya Hegde
 */
public class informationRetrieval {
	/**
	 * Main function
	 * 
	 * @param args Arguments for command line
	 */

	@SuppressWarnings("static-access")
	/**
	 * 
	 * @param args Arguments for command line
	 */
	public static void main(String args[]) {
		try {
			/* Inputs from Command Line */
			String path_to_document_folder = args[0];
			String path_to_index_folder = args[1];
			String ranking_Model = args[2];
			String Query = args[3];

			/* Validate documents */
			LuceneBuildIndex build_Index = new LuceneBuildIndex();
			build_Index.init(path_to_index_folder, path_to_document_folder);
			LuceneSearchIndex call_Search = new LuceneSearchIndex();
			/**
			 * Code to Create Lucene Searcher. index_Searcher searches over an IndexReader
			 */
			IndexSearcher index_Searcher;
			index_Searcher = call_Search.create_Searcher(path_to_index_folder, ranking_Model);
			/* Search contents created by index_Searcher using Query */
			TopDocs found_Docs = call_Search.searchIndexDoc(Query, index_Searcher);
			/* Code to print Total number of documents found */
			System.out.println("Total Documents :: " + found_Docs.totalHits);
			int doc_Rank = 1;
			/* Code to print Path of Documents having Search Query */
			for (ScoreDoc sD : found_Docs.scoreDocs) {
				Document doc = index_Searcher.doc(sD.doc);
				System.out.println("Path : " + doc.get("path") + ", Score : " + sD.score + ",Rank : " + doc_Rank);
				doc_Rank++;
			}
		} catch (IOException e) {
			/* TODO Auto-generated catch block */
			e.printStackTrace();
		} catch (Exception e) {
			/* TODO Auto-generated catch block */
			System.out.println("Invalid input!!!!");
			e.printStackTrace();

		}
	}

}
