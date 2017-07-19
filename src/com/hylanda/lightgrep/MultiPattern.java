package com.hylanda.lightgrep;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 构造多模式
 * @author zhouzusheng
 *
 */
public class MultiPattern  implements  AutoCloseable{

	LinkedList<Long> patterns = new LinkedList<Long>();
	private int totalLength = 0;
	
	@Override
	public void close() throws Exception {
		Long pat;
		while((pat = patterns.poll())!= null) {
			NativeLibrary.releasePattern(pat);
		}
		totalLength = 0;
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	public boolean isClosed() {
        return patterns.isEmpty();
    }
	
	/**
	 * 添加正则
	 * @param input
	 * @param flags 1表示不区分大小写， 0 表示默认， 区分大小写
	 * @return
	 * @throws RegExprException
	 */
	public boolean addPattern(String input,  int flags) throws RegExprException {
		long v = NativeLibrary.parsePattern(input, flags);
		if(v != 0) {
			totalLength += input.length() * UTF8CharOffset.AVG_BYTE_PER_CHAR;
			patterns.add(v);
			return true;
		}
		return false;
	}
	
	public int getTotalLength() {
		return totalLength;
	}

	public Collection<Long> getPatterns() {
		return patterns;
	}
	
	/**
	 * 生成自动机
	 * @return
	 */
	public Automation toAutomation() {
		if(patterns.isEmpty())
			return null;
		return new Automation(this);
	}
	
}
