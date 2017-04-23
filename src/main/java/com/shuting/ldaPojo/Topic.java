package com.shuting.ldaPojo;


public class Topic {
	
	public int vCount ;
	
	public int[] vectorIdArray = null ;
	
	public Topic(int vClassCount){
		
		vectorIdArray = new int[vClassCount] ;
	}
  
	
	public void removeVector(Vector vector){
		this.vCount-- ;
		vectorIdArray[vector.id]-- ;
	}

	public void addVector(Vector vector) {
		// TODO Auto-generated method stub
		this.vCount++ ;
		vectorIdArray[vector.id]++ ;
	}
}
