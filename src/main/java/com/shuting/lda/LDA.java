package com.shuting.lda;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.io.Files;
import com.shuting.analysis.Analysis;
import com.shuting.analysisWay.DefinedAnalysis;
import com.shuting.ldaConfig.ConfigInf;
import com.shuting.ldaGibbs.LDAGibbsModel;
 
public class LDA {

	//训练模型类	
	private LDAModel ldaAModel = null;

	//集成分词
	private Analysis analysis = null;

	
	public LDA() {
		
		this.analysis = DefinedAnalysis.DEFAUlT;
		this.ldaAModel = new LDAGibbsModel(ConfigInf.topicNum, ConfigInf.alpha, ConfigInf.beta, ConfigInf.iteration, ConfigInf.saveStep, ConfigInf.beginSaveIters);
	}

	/**@param analysis 分词器   @param ldaModel 模型**/	  
	public LDA(Analysis analysis, LDAModel ldaModel) {
		this.analysis = analysis;
		this.ldaAModel = ldaModel;
	}

	//用户自定义分词器的设置
	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}

	//LDA 根据文件训练.一个文件相当于一个文档,ps:文件不好太大
	public void addDoc(File file, String charset) throws IOException {
		addDoc(file.getName(),Files.newReader(file, Charset.forName(charset)));
	}

	//LDA 根据文本训练,一个文本相当于一个文档	
	public void addDoc(String name ,String content) {
		addDoc(name,new StringReader(content));
	}

	//LDA 根据文本训练,一个流相当于一个文档	
	public void addDoc(String name ,Reader reader) {
		List<String> words = null;
		try {
			words = analysis.getWords(reader);
			ldaAModel.addDoc(name,words);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	public void trainAndSave(String modelPath, String charset) throws IOException {
		ldaAModel.trainAndSave(modelPath, charset);
	}

}
