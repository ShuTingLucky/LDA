package com.shuting.analysis;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**分词的接口**/
public interface Analysis {
  
	/**传入正文获得分词结果**/
	public List<String> getWords(Reader reader) throws IOException;

	/**停用词过滤
	 * @param word 单个的词
	 * @return boolean**/
	boolean filter(String word);
}
