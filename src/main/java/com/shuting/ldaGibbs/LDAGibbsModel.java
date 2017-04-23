package com.shuting.ldaGibbs;

import com.shuting.lda.LDAModel;
import com.shuting.ldaPojo.Doc;
import com.shuting.ldaPojo.Topic;
import com.shuting.ldaPojo.Vector;

public class LDAGibbsModel extends LDAModel {

	/**构造方法**/
	public LDAGibbsModel(int topicNum, double alpha, double beta, int iteration, int saveStep, int beginSaveIters) {
		super(topicNum, alpha, beta, iteration, saveStep, beginSaveIters);
	}

	//实现父类LDAModel的抽象方法：计算 p(z_i = k|z_-i, w)
	protected void sampleTopic(Doc doc, Vector vector) {

		int oldTopic = vector.topicId;
		doc.removeVector(vector);
		topics[oldTopic].removeVector(vector);

		double[] p = new double[topicNum];

		//Gibbs采样！！！
		for (int k = 0; k < topicNum; k++) {
			
			p[k] = (topics[k].vectorIdArray[vector.id] + beta) / (topics[k].vCount + vCount * beta) * (doc.topicArray[k] + alpha)
					/ (doc.topicArray.length - 1 + topicNum * alpha);
		}

		//轮盘赌最后累计使得p[k]是前面所有topic可能性的和
		for (int k = 1; k < topicNum; k++) {
			p[k] += p[k - 1];
		}
		
		double u = Math.random() * p[topicNum - 1];

		int newTopic;
		for (newTopic = 0; newTopic < topicNum; newTopic++) {
			if (u < p[newTopic]) {
				break;
			}
		}

		vector.topicId = newTopic;
		doc.updateVector(vector);
		topics[newTopic].addVector(vector);
	}
	
	
	protected void updateEstimatedParameters(double[][] phi, double[][] theta) {

		//填充phi文件
		Topic topic = null;
		for (int k = 0; k < topicNum; k++) {
			topic = topics[k];
			for (int v = 0; v < vCount; v++) {
				phi[k][v] = (topic.vectorIdArray[v] + beta) / (topic.vCount + vCount * beta);
			}
		}
		  
		//填充theta文件
		Doc doc = null;
		for (int d = 0; d < dCount; d++) {
			doc = docs.get(d);
			for (int k = 0; k < topicNum; k++) {
				theta[d][k] = (doc.topicArray[k] + alpha) / (doc.vectors.size() + topicNum * alpha);
			
			}
		}
	}
	
}
