package com.shuting.ldaConfig;

import java.io.File;
import java.util.ArrayList;

import com.shuting.fileTool.JFile;

//加载配置文件
public class LoadConfigFile {	
	
	private String filePath="";
	
	public LoadConfigFile(String filePath){
		
		this.filePath=filePath;		
	}
	  
	public Boolean readConfigInf(){	
		
		ArrayList<String> configSet=JFile.readFileToList(new File(filePath));
		if(configSet.size()!=3){
			
			System.out.println("配置文件格式有误！！！");
			return false;
		}		
		
		ConfigInf.corpusPath=configSet.get(0);
		String[] strSet=configSet.get(1).split("#");
		ConfigInf.topicNum=Integer.parseInt(strSet[0]);
		ConfigInf.topicTopWord=Integer.parseInt(strSet[1]);
		ConfigInf.alpha=Double.valueOf(strSet[2]);
		ConfigInf.beta=Double.valueOf(strSet[3]);
		ConfigInf.iteration=Integer.parseInt(strSet[4]);
		if(strSet.length==7){
			
			ConfigInf.beginSaveIters=Integer.parseInt(strSet[5]);
			ConfigInf.saveStep=Integer.parseInt(strSet[6]);			
		}		
		if(strSet.length>7){			
			System.out.println("配置文件格式有误！！！");
			return false;
		}
		ConfigInf.resultPath=configSet.get(2);		
			
		return true;
	}
	
}
