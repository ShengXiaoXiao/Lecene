import org.ansj.library.DicLibrary;
import org.ansj.library.SynonymsLibrary;
import org.nlpcn.commons.lang.tire.SmartGetWord;
import org.nlpcn.commons.lang.tire.domain.SmartForest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Output {

    //静态加载类似“北大和北京大学”之类的同义词典
    static {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("./src/main/resources/ShortWord.txt"));
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] columns = line.split("\\s+");
                DicLibrary.insert(DicLibrary.DEFAULT,columns[0],"nz",1000);
                SynonymsLibrary.insert(SynonymsLibrary.DEFAULT, new String[] { columns[0], columns[1] });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static SmartForest<Integer> forest = new SmartForest<Integer>();  //建立根节点

    /**
     * 词典的构造.一行一个词后面是参数.可以从文件读取.可以是read流.
     */
    public static void setTrieTree(){
        //读取字典中的近义句式和相应的参数，建立branches
        try {
            Scanner scanner = new Scanner(new File("./src/main/resources/synonym.txt"));
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] columns = line.split("\\s+");
                forest.add(columns[0],Integer.parseInt(columns[1]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Boolean test(String content1, String content2){

            //第一步，找同义句式，并去掉同义句式
            SmartGetWord<Integer> udg1 = forest.getWord(content1);
            SmartGetWord<Integer> udg2 = forest.getWord(content2);
            String words;
            int p1 = -1, p2 = -1;
            List<String> treeWords = new ArrayList<String>();

            while ((words = udg1.getAllWords()) != null) {
                p1 = udg1.getParam();
                System.out.println(p1);
                treeWords.add(words);
            }

            while ((words = udg2.getAllWords()) != null) {
                p2 = udg2.getParam();
                System.out.println(p2);
                treeWords.add(words);
            }
            //LY
            for (int i = 0; i <treeWords.size(); i++){
                System.out.println(treeWords.get(i));
            }

            Collections.sort(treeWords);  //长词组在前，优先去掉
            for (int i = treeWords.size() - 1; i >= 0; i--) {
                content1 = content1.replace(treeWords.get(i), "");
                content2 = content2.replace(treeWords.get(i), "");
            }

            //判断两句话是否含同义句式，若不含直接返回false
            if (p1 != p2) {
                return false;
            }

            //第二步，把不能识别的词先处理下
            List<String> unhandleDic = null;

        try {
            unhandleDic = Files.readAllLines(Paths.get("./src/main/resources/unhandledWords.txt"));

            int flag1=0;
            int flag2=0;
            for (int i = 0; i < unhandleDic.size(); i++) {
                if (content1.contains(unhandleDic.get(i))) {
                    flag1+=1;
                    if(content2.contains(unhandleDic.get(i))){
                        flag2+=1;
                    }
                }
            }

            if (flag1!=flag2) {
                return false;  //一旦一个包含无法切分词，另一个不包含，直接返回false
            }

            //第三步，存在同义句式，但不存在无法识别的名词，用ansj识别是否是相同的名词
            if (flag1==0) {
                if(!ansj.judgeWord(content1,content2)){
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

}

