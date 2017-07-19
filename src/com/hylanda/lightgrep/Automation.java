package com.hylanda.lightgrep;

import java.io.DataOutput;
import java.io.IOException;

/**
 * 代表很多正则模式编译得到的状态机
 * 这个状态机可以序列化与反序列化
 * 这个对象适合一次创建，创建完毕不同线程可以同时使用
 * @author zhouzusheng
 *
 */
public class Automation extends Handle implements  AutoCloseable{
	
	public Automation() {
		pointer = 0;
	}
	
	/**
	 * 从一个模式得到的状态机
	 * @param pattern
	 */
	public Automation(Pattern pattern) {
		int  v =  (int)(pattern.GetInput().length() * UTF8CharOffset.AVG_BYTE_PER_CHAR);
		long vmStates = NativeLibrary.createVsmStates(v, 1);
		NativeLibrary.addPattern(vmStates,  pattern.GetPattern());
		pointer = NativeLibrary.createAutomation(vmStates);
		NativeLibrary.releaseVsmStates(vmStates);
	}
	
	/**
	 * 从多个模式得到的状态机
	 * @param pattern
	 */
	public Automation(MultiPattern pattern) {
		int  v =  (int)(pattern.getTotalLength() * UTF8CharOffset.AVG_BYTE_PER_CHAR);
		long vmStates = NativeLibrary.createVsmStates(v, pattern.getPatterns().size());
		for(long pat : pattern.getPatterns()) {
			NativeLibrary.addPattern(vmStates, pat);
		}
		pointer = NativeLibrary.createAutomation(vmStates);
		NativeLibrary.releaseVsmStates(vmStates);
		
	}
	
	/**
	 * 从一个模式得到状态机，这样实际是单模式匹配
	 * @param input
	 * @param flags  1表示不区分大小写， 0 表示默认， 区分大小写
	 * @throws Exception
	 */
	public Automation(String input, int flags) throws Exception {
		try(Pattern pattern = Pattern.compile(input, flags)) {
			int  v =  (int)(input.length() * UTF8CharOffset.AVG_BYTE_PER_CHAR);
			long vmStates = NativeLibrary.createVsmStates(v, 1);
			NativeLibrary.addPattern(vmStates,  pattern.GetPattern());
			pointer = NativeLibrary.createAutomation(vmStates);
			NativeLibrary.releaseVsmStates(vmStates);
		}
	}

	@Override
	public void close() throws Exception {
		if(pointer != 0) {
			long t = pointer;
			pointer = 0;
			NativeLibrary.releaseAutomation(t);
		}
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	public long getDfa() {
		return pointer;
	}
	
	/**
	 * 获取buffer size， 通常用来保存到文件
	 * 
	 * @return
	 */
	public int getSize() {
		return NativeLibrary.getAutomationSize(pointer);
	}
	
	/**
	 * 获取buffer， 通常用来保存到文件
	 * @return
	 */
	public byte[] getAutomationBuffer() {
		return NativeLibrary.getAutomationBuffer(pointer);
	}
	
	/**
	 * 反序列化， 调用这个函数后，状态机将从buffer 加载
	 * 需要注意的是， buffer 应该是之前的状态机getAutomationBuffer 得到的（或者得到后存放到文件中的）
	 * @param buffer
	 * @param off
	 * @param len
	 * @return
	 */
	public boolean applyAutomationBuffer(byte[] buffer, int off, int len) {
		if(pointer != 0) {
			NativeLibrary.releaseAutomation(pointer);
			pointer = 0;
		}
		pointer = NativeLibrary.readAutomation(buffer, off, len);
		return pointer != 0;
	}
	
	/**
	 * 创建一次匹配器，创建后Matcher 不要跨越线程使用
	 * @return
	 */
	public Matcher createMatcher() {
		if(pointer == 0) {
			return null;
		}
		return new Matcher(this);
	}

	public void write(DataOutput out) throws IOException {
		out.write(getAutomationBuffer());
	}
	
}
