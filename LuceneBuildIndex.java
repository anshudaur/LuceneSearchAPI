package com.ovgu.dke.ir.lucene;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 * @author Anshu Daur
 * @author Akanksha Saxena
 * @author Shivalika Suman
 * @author Chinmaya Hegde
 */
public class LuceneBuildIndex {
	/**
	 * The class is used to accept the required paths for documents and path to
	 * index folder. Iterate over all the sub folders and pre-process the documents
	 * for stop words and stemming. Finally to Create a index file.
	 * 
	 * @param path_to_index_folder
	 * @param path_to_document_folder
	 */

	public static void init(String path_to_index_folder, String path_to_document_folder) {

		/* path_to_document_folder contains the path of Documents folder */
		String docsPath = path_to_document_folder;
		/* path_to_index_folder contains the path of index folder */
		String indexPath = path_to_index_folder;
		/* Final Documents folder Path Variable */
		final Path docDir = Paths.get(docsPath);
		try {
			/**
			 * Creating an org.apache.lucene.store.Directory instance FSDirectory in Lucene
			 * takes the path argument to store the indexes in the file system
			 */
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			/* Created an Analyzer instance to analyze token streams to analyze the text */
			/* Created a customized subclass to implement porter stemmer algorithm on the token stream*/
			Analyzer analyzer = new Analyzer() {
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
            };
			/*
			 * Code for Index Writer Configuration - If Index file is present - then append
			 * else Create
			 */
			IndexWriterConfig index_Writer_Config = new IndexWriterConfig(analyzer);
			index_Writer_Config.setOpenMode(OpenMode.CREATE_OR_APPEND);
			/*
			 * index_Writer instance of type IndexWriter - to write new index file to the
			 * directory
			 */
			IndexWriter index_Writer = new IndexWriter(dir, index_Writer_Config);
			/*
			 * indexParsedDocs will iterate over all files and directories in the document
			 * Directory
			 */
			indexParsedDocs(index_Writer, docDir);
			index_Writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param index_Writer To create and maintain an index.
	 * @param path         This is the path to Final doc directory.
	 * @throws IOException
	 */
	static void indexParsedDocs(final IndexWriter index_Writer, Path path) throws IOException {
		/* indexParsedDocs goes through the directory path and create indexes */
		if (Files.isDirectory(path)) {
			/* Code to iterate through the directory */
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path fileName, BasicFileAttributes attributes) throws IOException {
					try {
						/* Code to index the selected file */
						indexFile(index_Writer, fileName, attributes.lastModifiedTime().toMillis());
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			/* Code to index the file */
			indexFile(index_Writer, path, Files.getLastModifiedTime(path).toMillis());
		}
	}

	/**
	 * 
	 * @param index_Writer To create and maintain an index for files
	 * @param file
	 * @param lastModified
	 * @throws IOException
	 */
	static void indexFile(IndexWriter index_Writer, Path file, long lastModified) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {
			/* This code indexes the file passed as argument */
			String file_Path = file.toString();
			if (file_Path.contains(".html") || file_Path.contains(".htm") || file_Path.contains(".txt")) {
				Document doc = new Document();
				doc.add(new StringField("path", file.toString(), Field.Store.YES));
				doc.add(new LongPoint("modified", lastModified));
				doc.add(new TextField("contents", new String(Files.readAllBytes(file)), Store.YES));
				//System.out.println("file :: " + file.toString());
				index_Writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}
}
