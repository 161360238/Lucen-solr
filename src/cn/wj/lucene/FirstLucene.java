package cn.wj.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * lucene入门 创建索引 查询索引
 * 
 * @author 王杰
 * 
 */
public class FirstLucene {

	// 创建索引
	@Test
	public void creatIndex() throws IOException {
		// 创建一个indexwriter对象

		// 指定索引库的存放位置Directory对象
		Directory directory = FSDirectory.open(new File("F:\\temp\\index"));
		// 指定一个分析器，对文档内容进行分析。
		Analyzer analyzer = new IKAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,
				analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);

		// 创建filed对象，将filed对象添加到document对象中
		File f = new File("F:\\temp\\source");// 指定源文件的位置
		File[] listFile = f.listFiles();

		for (File file : listFile) {
			// 创建documen对象
			Document document = new Document();
			// 指定需要的域
			String file_name = file.getName();
			Field fileNameField = new TextField("fileName", file_name,
					Store.YES);
			// 文件大小
			long file_size = FileUtils.sizeOf(file);
			Field fileSizeField = new LongField("fileSize", file_size,
					Store.YES);
			// 文件路径
			String file_path = file.getPath();
			Field filePathField = new StoredField("filePath", file_path);
			// 文件内容
			String file_content = FileUtils.readFileToString(file);
			Field fileContentField = new TextField("fileContent", file_content,
					Store.NO);
			// 把域添加到文件对象
			document.add(fileNameField);
			document.add(fileSizeField);
			document.add(filePathField);
			document.add(fileContentField);
			// 第四步：使用indexwriter对象将document对象写入索引库，此过程进行索引创建。并将索引和document对象写入索引库。
			indexWriter.addDocument(document);
		}
		// 关闭IndexWriter
		indexWriter.close();
	}

	// 搜索索引，精准查询
	@Test
	public void searchindex() throws IOException {
		// 1,创建一个Directory对象，也就是索引库存放的位置
		Directory directory = FSDirectory.open(new File("F:\\temp\\index")); // 磁盘
		// 2,创建一个indexReader对象，需要指定Directory对象
		IndexReader indexReader = DirectoryReader.open(directory);
		// 3,创建一个indexSearce对象，需要指定IndexReader对象
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		// 4,创建一个termQuery对象，指定查询的域和查询的关键词
		Query query = new TermQuery(new Term("fileName", "html"));
		// 5,执行查询
		TopDocs topDocs = indexSearcher.search(query, 10);
		// 6,返回查询结果，遍历查询结果并输出
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int doc = scoreDoc.doc;
			Document document = indexSearcher.doc(doc);
			// 文件名称
			String fileName = document.get("fileName");
			System.out.println(fileName);
			// 文件内容
			String fileContent = document.get("fileContent");
			System.out.println(fileContent);
			// 文件大小
			String fileSize = document.get("fileSize");
			System.out.println(fileSize);
			// 文件路径
			String filePath = document.get("filePath");
			System.out.println(filePath);
			System.out.println("------------");
		}
		//7,关闭IndexReader对象
		indexReader.close();
	}

}
