package cn.wj.lucene;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
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
 * ����ά��
 * ���  ���ų���
 * ɾ��
 * �޸�
 * ��ѯ  ���ų��� ��׼��ѯ 
 * @author lx
 *
 */
public class LuceneManager {

	//
	public IndexWriter getIndexWriter() throws Exception{
		// ��һ��������һ��java���̣�������jar����
		// �ڶ���������һ��indexwriter����
		Directory directory = FSDirectory.open(new File("F:\\temp\\index"));
		// Directory directory = new RAMDirectory();//�����������ڴ��� ���ڴ������⣩
		Analyzer analyzer = new StandardAnalyzer();// �ٷ��Ƽ�
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		return new IndexWriter(directory, config);
	}
	//ȫɾ��
	@Test
	public void testAllDelete() throws Exception {
		IndexWriter indexWriter = getIndexWriter();
		indexWriter.deleteAll();
		indexWriter.close();
	}
	//��������ɾ��
	@Test
	public void testDelete() throws Exception {
		IndexWriter indexWriter = getIndexWriter();
		Query query = new TermQuery(new Term("fileName","apache"));
		indexWriter.deleteDocuments(query);
		indexWriter.close();
	}
	//�޸�
	@Test
	public void testUpdate() throws Exception {
		IndexWriter indexWriter = getIndexWriter();
		Document doc = new Document();
		doc.add(new TextField("fileN", "�����ļ���",Store.YES));
		doc.add(new TextField("fileC", "�����ļ�����",Store.YES));
		indexWriter.updateDocument(new Term("fileName","lucene"), doc, new IKAnalyzer());
		indexWriter.close();
	}
	//IndexReader  IndexSearcher
	public IndexSearcher getIndexSearcher() throws Exception{
		// ��һ��������һ��Directory����Ҳ�����������ŵ�λ�á�
		Directory directory = FSDirectory.open(new File("F:\\temp\\index"));
		// �ڶ���������һ��indexReader������Ҫָ��Directory����
		IndexReader indexReader = DirectoryReader.open(directory);
		// ������������һ��indexsearcher������Ҫָ��IndexReader����
		return new IndexSearcher(indexReader);
	}
	//ִ�в�ѯ�Ľ��
	public void printResult(IndexSearcher indexSearcher,Query query)throws Exception{
		// ���岽��ִ�в�ѯ��
		TopDocs topDocs = indexSearcher.search(query, 10);
		// �����������ز�ѯ�����������ѯ����������
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
	}
	//��ѯ����
	@Test
	public void testMatchAllDocsQuery() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		Query query = new MatchAllDocsQuery();
		System.out.println(query);
		printResult(indexSearcher, query);
		//�ر���Դ
		indexSearcher.getIndexReader().close();
	}
	//������ֵ��Χ��ѯ
	@Test
	public void testNumericRangeQuery() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		
		Query query = NumericRangeQuery.newLongRange("fileSize", 47L, 200L, false, true);
		System.out.println(query);
		printResult(indexSearcher, query);
		//�ر���Դ
		indexSearcher.getIndexReader().close();
	}
	//������ϲ�ѯ����
	@Test
	public void testBooleanQuery() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		
		BooleanQuery booleanQuery = new BooleanQuery();
		
		Query query1 = new TermQuery(new Term("fileName","apache"));
		Query query2 = new TermQuery(new Term("fileName","lucene"));
		//  select * from user where id =1 or name = 'safdsa'
		booleanQuery.add(query1, Occur.MUST);
		booleanQuery.add(query2, Occur.SHOULD);
		System.out.println(booleanQuery);
		printResult(indexSearcher, booleanQuery);
		//�ر���Դ
		indexSearcher.getIndexReader().close();
	}
	//�������͵Ķ����ѯ
	@Test
	public void testQueryParser() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		//����1�� Ĭ�ϲ�ѯ����  
		//����2�����õķ�����
		QueryParser queryParser = new QueryParser("fileName",new IKAnalyzer());
		// *:*   ��ֵ
		Query query = queryParser.parse("fileName:lucene is apache OR fileContent:lucene is apache");
		
		printResult(indexSearcher, query);
		//�ر���Դ
		indexSearcher.getIndexReader().close();
	}
	//���������Ķ����ѯ   ���Ĭ����
	@Test
	public void testMultiFieldQueryParser() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		
		String[] fields = {"fileName","fileContent"};
		//����1�� Ĭ�ϲ�ѯ����  
		//����2�����õķ�����
		MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields,new IKAnalyzer());
		// *:*   ��ֵ
		Query query = queryParser.parse("lucene is apache");
		
		printResult(indexSearcher, query);
		//�ر���Դ
		indexSearcher.getIndexReader().close();
	}
	
	
}
