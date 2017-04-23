package com.shuting.ldaTest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.shuting.fileTool.JFile;

/**
 * 计算LDA之后，给每个文章添加标签
 * Created by 睿 on 2017/1/21.
 */

public class LDAClustering {
	
    private String doc_Topic ;
    private String docID_Title ;

    public LDAClustering(){
    	
    	doc_Topic = "D:\\源码阅读\\lda\\result\\result\\cluster4\\lda_result.theta";
        docID_Title = "D:/index.txt";
    }
    
    public LDAClustering(String doc_Topic, String docID_Title) {
        
    	this.doc_Topic = doc_Topic;
        this.docID_Title = docID_Title;
    }    
    
    public void clustering() {

        ArrayList<String> docSet = JFile.readFileToList(new File(docID_Title), "utf-8");
        
        Map<String, String> map = getDocumentAndTopic(doc_Topic, 76);
        System.out.println(map.size());

        for (String doc : docSet) {
        	
            String docID = doc.split("<=>")[0];
            String title = doc.split("<=>")[1];

            if (map.containsKey(docID)) {

                JFile.appendToFile("finalResult.txt", map.get(docID) + "," + title);
            }
        }
    }

    public static Map<String, String> getDocumentAndTopic(String filePath, int number) {
    	
        ArrayList<String> lines = JFile.readFileToList(new File(filePath), "utf-8");

        Map<String, String> list = new HashMap<String, String>();
        for (String line : lines) {
        	
            String[] spilt = line.split("\t");
            if (spilt.length == number) {
            	
                String indexID = spilt[0].substring(0, spilt[0].indexOf("."));

                double max = Double.valueOf(spilt[1]);
                int count = 0;
                for (int i = 2;i < spilt.length;i++) {
                    double temp = Double.valueOf(spilt[i]);

                    if (temp > max) {
                        max = temp;
                        count = (i - 1);
                    }
                }
                list.put(indexID, String.valueOf(count));
            }
        }

        return list;
    }

    
    
    public static void main(String[] args) {
        /*if (args.length != 2) {
            System.out.println("Error!!!");
            System.exit(1);
        }*/

        //分类
//        LDAClustering ldaClustering = new LDAClustering(args[0], args[1]);
//        LDAClustering ldaClustering = new LDAClustering();
//
//        ldaClustering.clustering();

        ArrayList<String> DIAN = new ArrayList<String>();
        DIAN.add("66");DIAN.add("62");DIAN.add("67");DIAN.add("15");DIAN.add("16");DIAN.add("68");
        DIAN.add("69");DIAN.add("71");DIAN.add("72");DIAN.add("37");DIAN.add("74");DIAN.add("55");DIAN.add("19");


        DIAN.add("59");DIAN.add("52");//DIAN.add("42");DIAN.add("31");
//        String[] dianli = new String[]{"29", "65", "49", "54", "2", "5", "17", "18", "32", "33", "36", "24"};


        ArrayList<String> list = JFile.readFileToList(new File("finalResult.txt"), "utf-8");

        System.out.println(list.size());

        for (String line : list) {
            String id = line.split(",")[0];
//            System.out.println(id);

            String res = line.substring(line.indexOf(","));
            if (DIAN.contains(id)) {
                JFile.appendToFile("resultresult.txt", "1000" + res);
            }
        }

    }

    
    
    
}
