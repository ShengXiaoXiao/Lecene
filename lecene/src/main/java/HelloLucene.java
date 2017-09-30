import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.Analyzer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class HelloLucene {
    public static final String INDEX_PATH = "resources/patternIndex";
    private Directory dir = null;
    private IndexReader reader;
    private IndexSearcher searcher;

    private static class SingletionHolder{
        private static final HelloLucene INSTANCE =new HelloLucene();
    }

    private HelloLucene(){
        init();
    }
    public static final HelloLucene getInstance(){return SingletionHolder.INSTANCE;}

    private void  init(){
        try {
            this.dir = FSDirectory.open(Paths.get(INDEX_PATH));
            if (dirEmpty()) {  //索引文件不能重复建立！
                System.out.println("索引构建开始");
                indexer();
                System.out.println("索引构建完毕");
            }
            this.reader = DirectoryReader.open(this.dir);
            this.searcher = new IndexSearcher(this.reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean dirEmpty() {
        File dir = new File(HelloLucene.INDEX_PATH);
        if (dir.listFiles().length == 0) {
            return true;
        }
        return false;
    }
    //构造索引
    public void  indexer() throws  Exception{

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config =  new IndexWriterConfig(analyzer);
        IndexWriter writer = new IndexWriter(this.dir,config);
        indexPair(writer,"我","123");
        indexPair(writer,"你","345");
        indexPair(writer,"他","789");
        writer.close();
    }

    private void indexPair(IndexWriter writer, String pattern,String number) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("pattern",pattern, Field.Store.YES));
        doc.add(new StringField("number",number, Field.Store.YES));
        writer.addDocument(doc);
    }

    public List<String> search(String text, int num)throws Exception{
        System.out.println("搜索:" + text);
        List<String> list= new ArrayList<>();
        Analyzer analyzer = new StandardAnalyzer();//标准分词器，会自动去掉空格啊，is a the等单词
        QueryParser parser = new QueryParser("pattern", analyzer); //查询解析器
        Query query = parser.parse(text);//通过解析要查询的String，获取查询对象
        long startTime = System.currentTimeMillis();
        TopDocs docs =this.searcher.search(query,num);
        for(ScoreDoc scoreDoc : docs.scoreDocs) { //取出每条查询结果
            Document doc = this.searcher.doc(scoreDoc.doc); //scoreDoc.doc相当于docID,根据这个docID来获取文档
            list.add(doc.get("pattern"));
        }
        reader.close();
        return list;
    }


    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try {
            List<String> list = HelloLucene.getInstance().search("我们",3);
            for (String str :list){
                System.out.println(str);
            }
        }catch (ParseException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long time =endTime - startTime;
        System.out.println("消耗时间" + time);
    }
}
