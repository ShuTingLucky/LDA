package com.shuting.ldaTest;

import java.io.File;
import java.io.IOException;

import com.shuting.lda.LDA;
import com.shuting.ldaConfig.ConfigInf;
import com.shuting.ldaConfig.LoadConfigFile;

public class LDATest {	
	
	/**
    public static void spilt() {
    	
        ArrayList<String> lines = JFile.readAllFile("D:/abstract");

        int count = 0;
        for (String line : lines) {
        	
        	String title=line.split("<=>")[0];
        	String dataStr=line.split("<=>")[0];
            JFile.appendToFile("D:/index.txt", count + "<=>" + title);
            JFile.appendToFile("D:/sourceData/" + count + ".txt", dataStr);
            count++;
        }
    }**/
    
    public static void training(String filePath) throws IOException {
    	
        File[] files = new File(filePath).listFiles();

        System.out.println("语料库中文档的数量："+files.length);
        LDA lda = new LDA();
        for (File file : files) {
       
            lda.addDoc(file, "utf-8");
        }

        lda.trainAndSave(ConfigInf.resultPath, "utf-8");
    }
    
    public static void main(String[] args) throws IOException {    	
    
    	if(args.length!=1){
    		
    		System.out.println("抱歉，参数个数不正确！！！");
    		System.exit(0);
    	}
    	
    	LoadConfigFile config=new LoadConfigFile(args[0]);
    	if(config.readConfigInf()){
    		
    		training(ConfigInf.corpusPath);    		
    	}
        
    }
    
}
