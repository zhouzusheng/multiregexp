package com.hylanda.lightgrep;

/**
 * 构造单模式，很少使用，为了对称性，也提供这个对象。
 * 相比于java的 正则模式，这个就差了很多，不能提供子分组是其致命缺点
 * @author zhouzusheng
 *
 */
public class Pattern extends Handle implements  AutoCloseable{
	
	private String input;
	
	protected long GetPattern() {
		return pointer;
	}
	
	protected  String GetInput() {
		return input;
	}
	protected Pattern(String input, long pattern) {
		this.input = input;
		this.pointer = pattern; 
	}
	
	public static Pattern compile(String input,  int flags) throws RegExprException {
		return new Pattern(input, NativeLibrary.parsePattern(input, flags));
	}
	
	@Override
	public void close() throws Exception {
		if(pointer != 0) {
			long t = pointer;
			pointer = 0;
			NativeLibrary.releasePattern(t);
		}
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	
	
	public Automation toAutomation() {
		if(pointer == 0)
			return null;
		return new Automation(this);
	}
}
