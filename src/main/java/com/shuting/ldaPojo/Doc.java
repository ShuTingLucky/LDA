package com.shuting.ldaPojo;

import java.util.List;

import com.google.common.collect.Lists;

public class Doc {
	
	//文档名
	private String docName;
	//文档词矩阵(id-topicID)
	public List<Vector> vectors;
	//文档topic矩阵
	public int[] topicArray = null;
	  

	public Doc(String docName, int topicNum) {
		
		this.docName = docName;
		topicArray = new int[topicNum];
		vectors = Lists.newArrayList();
	}

	public void addVector(Vector vector) {	
		vectors.add(vector);
		topicArray[vector.topicId]++;
	}

	public void removeVector(Vector vector) {		
		topicArray[vector.topicId]--;
	}

	public void updateVector(Vector vector) {		
		topicArray[vector.topicId]++;
	}

	public String getDocName() {
		return docName;
	}
	
}
