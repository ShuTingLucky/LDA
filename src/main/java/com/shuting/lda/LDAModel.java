package com.shuting.lda;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
  
import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.MinMaxPriorityQueue;
import com.google.common.io.Files;
import com.google.common.primitives.Doubles;
import com.shuting.ldaConfig.ConfigInf;
import com.shuting.ldaPojo.Doc;
import com.shuting.ldaPojo.Topic;
import com.shuting.ldaPojo.Vector;
import com.shuting.ldaResultAnalysis.TopicAveCorre;

public abstract class LDAModel {

	// 主题数
	protected int topicNum;
	// 狄利克雷函数的两个参数
	protected double alpha = 0.1;
	protected double beta = 0.1;
	// 迭代次数
	protected int iteration = 100;
	// 多少次迭代保存一次,默认不保存
	protected int saveStep = Integer.MAX_VALUE;
	// 开始保存的迭代次数,默认不保存
	protected int beginSaveIters = Integer.MAX_VALUE;
	// v语料库的词类型数目(区别Topic类中的vCount) m语料库中的文档数
	protected int vCount, dCount;

	// 文档-主题矩阵
	protected List<Doc> docs = Lists.newArrayList();
	// 主题-词矩阵
	protected Topic[] topics = null;
	// 词和id双向map
	protected BiMap<String, Integer> vectorMap = HashBiMap.create();

	/**
	 * @param alpha
	 * @param beta
	 * @param iteration
	 *            迭代次数
	 * @param saveStep
	 *            每多少步保存一次
	 * @param beginSaveIters
	 *            开始保存的迭代次数
	 */
	public LDAModel(int topicNum, double alpha, double beta, int iteration, int saveStep, int beginSaveIters) {
		super();
		this.topicNum = topicNum;
		this.alpha = alpha;
		this.beta = beta;
		this.iteration = iteration;
		this.saveStep = saveStep;
		this.beginSaveIters = beginSaveIters;
	}

	// 添加文档，初始化文档-主题矩阵
	public void addDoc(String name, List<String> wordSet) {

		dCount++;

		Doc doc = new Doc(name, topicNum);
		Integer id = null;
		int topicId = 0;
		for (String word : wordSet) {

			// 构造id-topicId之间的映射
			id = vectorMap.get(word);
			if (id == null) {
				id = vCount;
				vectorMap.put(word, vCount);
				vCount++;
			}
			// random topic 门洛奇
			topicId = (int) (Math.random() * topicNum);
			// 文档增加向量
			doc.addVector(new Vector(id, topicId));
		}

		docs.add(doc);
	}

	// 初始化主题-词矩阵
	private void fullTopicVector() {

		topics = new Topic[topicNum];
		for (int i = 0; i < topics.length; i++) {
			topics[i] = new Topic(vCount);
		}

		for (Doc doc : docs) {
			for (Vector vector : doc.vectors) {
				topics[vector.topicId].addVector(vector);
			}
		}
	}

	/** 开始训练 **/
	public void trainAndSave(String modelPath, String charset) throws IOException {

		fullTopicVector();
		// System.out.println("insert model ok! ");

		// 迭代收敛
		for (int i = 0; i < iteration; i++) {

			if ((i >= beginSaveIters) && (((i - beginSaveIters) % saveStep) == 0)) {

				saveModel(i + "", modelPath, charset);
			}
			// System.out.println("Iteration:\t" + i);
			for (Doc doc : docs) {
				for (Vector vector : doc.vectors) {
					sampleTopic(doc, vector);
				}
			}
		}

		System.out.println("explan model ok!");
		saveModel("result", modelPath, charset);
		System.out.println("save Model ok!");
	}

	/** 保存模型 **/
	private void saveModel(String iters, String modelPath, String charset) throws IOException {

		// 主题-词矩阵
		double[][] phi = new double[topicNum][vCount];
		// 文档-主题矩阵
		double[][] theta = new double[dCount][topicNum];

		updateEstimatedParameters(phi, theta);

		saveModel(iters, phi, theta, modelPath, charset);
	}

	private static final String LINES = "\n";
	private static final char[] LINEC = "\n".toCharArray();

	private void saveModel(String iters, double[][] phi, double[][] theta, String modelPath, String charsetName)
			throws IOException {

		String modelName = "lda_" + iters;
		File modelDir = new File(modelPath);
		// 创建路径
		if (!modelDir.isDirectory()) {
			modelDir.mkdirs();
		}

		Charset charset = Charset.forName(charsetName);

		// 配置信息
		StringBuilder strBuf = new StringBuilder();
		strBuf.append("alpha = " + alpha + LINES);
		strBuf.append("beta = " + beta + LINES);
		strBuf.append("topicNum = " + topicNum + LINES);
		strBuf.append("docNum = " + dCount + LINES);
		strBuf.append("termNum = " + vCount + LINES);
		strBuf.append("iterations = " + iteration + LINES);
		strBuf.append("saveStep = " + saveStep + LINES);
		strBuf.append("beginSaveIters = " + beginSaveIters);

		Files.write(strBuf, new File(modelDir, modelName + ".params"), charset);

		// 主题-词矩阵：lda.phi K*V
		BufferedWriter writer = Files.newWriter(new File(modelDir, modelName + ".phi"), charset);
		for (int i = 0; i < topicNum; i++) {
			
			writer.write(Joiner.on("\t").join(Doubles.asList(phi[i])));
			writer.write(LINEC);
		}
		writer.flush();
		writer.close();

		// 文档-主题矩阵：lda.theta M*K
		writer = Files.newWriter(new File(modelDir, modelName + ".theta"), charset);
		for (int i = 0; i < dCount; i++) {
			writer.write(docs.get(i).getDocName() + "\t");
			writer.write(Joiner.on("\t").join(Doubles.asList(theta[i])));
			writer.write(LINEC);
		}
		writer.flush();
		writer.close();

		// lda.tassign
		writer = Files.newWriter(new File(modelDir, modelName + ".tassign"), charset);
		Doc doc = null;
		Vector vector = null;
		for (int m = 0; m < dCount; m++) {
			doc = docs.get(m);
			writer.write(doc.getDocName() + "\t");
			for (int n = 0; n < doc.vectors.size(); n++) {
				vector = doc.vectors.get(n);
				writer.write(vector.id + ":" + vector.topicId + "\t");
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();

		//lda.wordmap
		writer = Files.newWriter(new File(modelDir, modelName + ".wordmap"), charset);
		for(Map.Entry<String, Integer> iter : vectorMap.entrySet()){
			
			String word=iter.getKey();
			int wordID=iter.getValue();
			writer.write(wordID + "\t" + word+"\n");			
		}
		writer.flush();
		writer.close();

		// lda.twords phi[][] K*V
		writer = Files.newWriter(new File(modelDir, modelName + ".twords"), charset);
		TopicAveCorre analysisResult=new TopicAveCorre(phi);
		double avgCorre=analysisResult.getAveCorre();
		writer.write("平均相似度：\t"+avgCorre+"\n");
		// 输出每个topic下概率分布的前20g个词
		int topNum = ConfigInf.topicTopWord;
		// 存储每个topic下各个词的概率分布
		double[] scores = null;
		VecotrEntry pollFirst = null;
		for (int i = 0; i < topicNum; i++) {
			writer.write("topic " + i + "\t:\n");
			MinMaxPriorityQueue<VecotrEntry> mmp = MinMaxPriorityQueue.create();
			scores = phi[i];
			for (int j = 0; j < vCount; j++) {
				mmp.add(new VecotrEntry(j, scores[j]));
			}

			for (int j = 0; j < topNum; j++) {
				if (mmp.isEmpty()) {
					break;
				}
				pollFirst = mmp.pollFirst();
				writer.write("\t" + vectorMap.inverse().get(pollFirst.id) + " " + pollFirst.score + "\n");
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
	}

	/**
	 * public static void main(String[] args) {
	 * 
	 * MinMaxPriorityQueue<VecotrEntry> mmp = MinMaxPriorityQueue.create();
	 * 
	 * mmp.add(new VecotrEntry(1, 1.0)); mmp.add(new VecotrEntry(3, 3.0));
	 * mmp.add(new VecotrEntry(2, 2.0));
	 * 
	 * for (int i = 0; i < 2; i++) { System.out.println(mmp.pollFirst().id); } }
	 **/

	/** 排序类 **/
	static class VecotrEntry implements Comparable<VecotrEntry> {

		int id;
		double score;

		public VecotrEntry(int id, double score) {
			this.id = id;
			this.score = score;
		}

		public int compareTo(VecotrEntry o) {

			if (this.score > o.score) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	/** 计算 p(z_i = k|z_-i, w) 抽样 **/
	protected abstract void sampleTopic(Doc doc, Vector vector);

	/** 更新估计参数 **/
	protected abstract void updateEstimatedParameters(double[][] phi, double[][] theta);

}
