package com.shuting.analysisWay;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.shuting.analysis.Analysis;
import com.shuting.fileTool.JFile;


public class DefinedAnalysis implements Analysis {
	
	public static ArrayList<String> stopWords = null;
	public static final Analysis DEFAUlT = new DefinedAnalysis(true);
  
	/**是否需要过滤停用词**/
	public DefinedAnalysis(boolean isFilter) {	
		  
		if (isFilter) {			
			stopWords = JFile.readFileToList(new File("stopWords.txt"));
		}
		else{
			stopWords=new ArrayList<String>();
		}
	}		
	
	public List<String> getWords(Reader reader) throws IOException {

		List<String> wordSet = new ArrayList<String>();
		BufferedReader br = null ;
		try {			
			br = new BufferedReader(reader);
			String tempStr = null;
			
			while ((tempStr = br.readLine()) != null) {

				for (String word : tempStr.split(" ")) {
					
					if (!filter(word)) {
						wordSet.add(word);
					}
				}
			}
			
			return wordSet;
			
		} finally {
			if(br!=null){
				br.close() ;
			}				
		}
	}

	public boolean filter(String word) {
		
		return stopWords.contains(word);
	}
}
