package com.shuting.ldaResultAnalysis;

import com.shuting.ldaConfig.ConfigInf;

public class TopicAveCorre {	
	
	double[][] topicWordMap;
	
	public TopicAveCorre(double[][] topicWordMap) {

		this.topicWordMap=topicWordMap;	
	}
	
	public double[] getVecModulue(int topicNum,int wordNum){
		
		double[] modules=new double[topicNum];
		for(int i=0;i<topicNum;i++){
			
			double valueSum=0.0;
			for(int j=0;j<wordNum;j++){
				
				valueSum+=Math.pow(topicWordMap[i][j], 2);
			}
			modules[i]=Math.sqrt(valueSum);			
		}
		
		return modules;
		
	}
	
	public double getAveCorre(){
		
		int topicNum=ConfigInf.topicNum;
		int wordNum=topicWordMap[0].length;		
		double[] modules=getVecModulue(topicNum,wordNum);
		double correSum=0.0;
		
		for(int i=0;i<topicNum;i++){			
			for(int j=i+1;j<topicNum;j++){
				
				double subSum=0.0;
				for(int k=0;k<wordNum;k++){
					
					subSum+=topicWordMap[i][k]*topicWordMap[j][k];				
				}				
				double multiply=modules[i]*modules[j];
				correSum+=subSum/multiply;				
			}		
		}
		
		int num=(topicNum*(topicNum-1))/2;
		double avgCorre=correSum/num;
		
		return avgCorre;		
	}
}
