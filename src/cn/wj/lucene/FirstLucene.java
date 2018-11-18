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
 * lucene���� �������� ��ѯ����
 * 
 * @author ����
 * 
 */
public class FirstLucene {

	// ��������
	@Test
	public void creatIndex() throws IOException {
		// ����һ��indexwriter����

		// ָ��������Ĵ��λ��Directory����
		Directory directory = FSDirectory.open(new File("F:\\temp\\index"));
		// ָ��һ�������������ĵ����ݽ��з�����
		Analyzer analyzer = new IKAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,
				analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config);

		// ����filed���󣬽�filed������ӵ�document������
		File f = new File("F:\\temp\\source");// ָ��Դ�ļ���λ��
		File[] listFile = f.listFiles();

		for (File file : listFile) {
			// ����documen����
			Document document = new Document();
			// ָ����Ҫ����
			String file_name = file.getName();
			Field fileNameField = new TextField("fileName", file_name,
					Store.YES);
			// �ļ���С
			long file_size = FileUtils.sizeOf(file);
			Field fileSizeField = new LongField("fileSize", file_size,
					Store.YES);
			// �ļ�·��
			String file_path = file.getPath();
			Field filePathField = new StoredField("filePath", file_path);
			// �ļ�����
			String file_content = FileUtils.readFileToString(file);
			Field fileContentField = new TextField("fileContent", file_content,
					Store.NO);
			// ������ӵ��ļ�����
			document.add(fileNameField);
			document.add(fileSizeField);
			document.add(filePathField);
			document.add(fileContentField);
			// ���Ĳ���ʹ��indexwriter����document����д�������⣬�˹��̽�����������������������document����д�������⡣
			indexWriter.addDocument(document);
		}
		// �ر�IndexWriter
		indexWriter.close();
	}

	// ������������׼��ѯ
	@Test
	public void searchindex() throws IOException {
		// 1,����һ��Directory����Ҳ�����������ŵ�λ��
		Directory directory = FSDirectory.open(new File("F:\\temp\\index")); // ����
		// 2,����һ��indexReader������Ҫָ��Directory����
		IndexReader indexReader = DirectoryReader.open(directory);
		// 3,����һ��indexSearce������Ҫָ��IndexReader����
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		// 4,����һ��termQuery����ָ����ѯ����Ͳ�ѯ�Ĺؼ���
		Query query = new TermQuery(new Term("fileName", "html"));
		// 5,ִ�в�ѯ
		TopDocs topDocs = indexSearcher.search(query, 10);
		// 6,���ز�ѯ�����������ѯ��������
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int doc = scoreDoc.doc;
			Document document = indexSearcher.doc(doc);
			// �ļ�����
			String fileName = document.get("fileName");
			System.out.println(fileName);
			// �ļ�����
			String fileContent = document.get("fileContent");
			System.out.println(fileContent);
			// �ļ���С
			String fileSize = document.get("fileSize");
			System.out.println(fileSize);
			// �ļ�·��
			String filePath = document.get("filePath");
			System.out.println(filePath);
			System.out.println("------------");
		}
		//7,�ر�IndexReader����
		indexReader.close();
	}

}
