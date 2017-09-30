import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.library.SynonymsLibrary;
import org.ansj.recognition.impl.SynonymsRecgnition;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ansj {

    //获得两个语句中的名词
    public static String getWord(String content) {
        //只关注这些词性的词  关键词只关注这些词
        Set<String> expectedNature = new HashSet<String>() {{
            add("nr");//人名
            add("ns");//地名
            add("nsf");//音译地名
            add("nt");//机构团体名
            add("nz");//其他专名
            add("t");//时间词
            add("s");//处所词
            add("f");//方位词
            add("n");//名词
            add("ntu");//大学
            add("j");// 省略词
        }};

        String word="";
        Result result = ToAnalysis.parse(content); //分词结果的一个封装，主要是一个List<Term>的terms
        List<Term> terms = result.getTerms(); //拿到terms
        //System.out.println(result.getTerms());

        for (int i = 0; i < terms.size(); i++) {
            String wordStr = terms.get(i).getName(); //拿到词
            String natureStr = terms.get(i).getNatureStr(); //拿到词性
            if (expectedNature.contains(natureStr)) {
                word=wordStr;
            }
        }

        return word;
    }

    //判断是否是同义
    public static Boolean judgeWord(String content1,String content2){

        String word1=getWord(content1);
        String word2=getWord(content2);

        if(word1.equals(word2)){
            return true;
        }

        SynonymsRecgnition synonymsRecgnition = new SynonymsRecgnition();
        for (Term term : ToAnalysis.parse(word1).recognition(synonymsRecgnition)) {
            if (term.getSynonyms()!=null && term.getSynonyms().contains(word2)) {
                    return true;
            }
        }

        return false;
    }

}
