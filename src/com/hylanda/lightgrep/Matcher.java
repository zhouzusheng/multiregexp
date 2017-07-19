package com.hylanda.lightgrep;

/**
 * 从 Automation 得到的 查询对象， 这个对象不是线程安全的，不可以跨线程使用
 * @author zhouzusheng
 *
 */
public class Matcher extends Handle implements  AutoCloseable{

	/**
	 * 保持一份对 GrepAutomation 的引用， 避免 automation被回收掉导致死机
	 */
	Automation automation;
	
	public Matcher(Automation automation) {
		this.automation = automation;
		this.pointer = NativeLibrary.createMatcher(automation.getDfa());
	}
	
	@Override
	public void close() throws Exception {
		if(pointer != 0) {
			NativeLibrary.releaseMatcher(pointer);
			pointer = 0;
		}
		automation = null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	/**
	 * 执行匹配，匹配到的每一个结果回调用一次callback， HitItem 中的位置信息已经转换为字符计数
	 * @param text
	 * @param callback
	 */
	public void match(final GrepString text, final HitCallback callback) {
		match(text, 0, text.length(), callback);
	};

	/**
	 * 执行匹配， 可以只匹配部分数据，匹配到的每一个结果调用一次callback， HitItem 中的位置信息已经转换为字符计数
	 * @param text
	 * @param pos
	 * @param end
	 * @param callback
	 */
	public void match(final GrepString text, int pos, int end, final HitCallback callback) {
		if(end < 0)
			end = text.length();
		if(automation.isClosed() || pointer == 0)
			throw new IllegalStateException("自动机已经释放，无法搜索");
		int off = text.bytePos(pos);
		int last = text.bytePos(end);
		NativeLibrary.matchText(pointer, text.pointer(), off, last, new HitCallback(){

			@Override
			public void match(HitItem item) {
				item.setStart(text.charPos(item.getStart()));
				item.setEnd(text.charPos(item.getEnd()));
				callback.match(item);
			}
		});
	}
	
	/**
	 * 执行开始匹配而不是全匹配，匹配到的每一个结果调用一次callback， HitItem 中的位置信息已经转换为字符计数
	 * @param text
	 * @param callback
	 */
	public void startWithText(final GrepString text,  final HitCallback callback) {
		startWithText(text, 0, text.length(), callback);
	}
	
	/**
	 * 执行开始匹配而不是全匹配，匹配到的每一个结果回调用一次callback， HitItem 中的位置信息已经转换为字符计数
	 * @param text
	 * @param pos
	 * @param end
	 * @param callback
	 */
	public void startWithText(final GrepString text, int pos, int end, final HitCallback callback) {
		if(end < 0)
			end = text.length();
		
		if(automation.isClosed() || pointer == 0)
			throw new IllegalStateException("自动机已经释放，无法搜索");
		NativeLibrary.startWithText(pointer, text.pointer(), text.bytePos(pos), text.bytePos(end), new HitCallback(){

			@Override
			public void match(HitItem item) {
				item.setStart(text.charPos(item.getStart()));
				item.setEnd(text.charPos(item.getEnd()));
				callback.match(item);
			}
		});
	}
	
	/**
	 * 直接暴露 utf8的字节匹配，匹配到的结果位置信息是按字节计数的
	 * @param buffer
	 * @param pos
	 * @param end
	 * @param callback
	 */
	public void matchBuffer(byte[] buffer, int pos, int end, final HitCallback callback) {
		if(automation.isClosed() || pointer == 0)
			throw new IllegalStateException("自动机已经释放，无法搜索");
		NativeLibrary.matchBuffer(pointer, buffer, pos, end, callback);
	}
	
	/**
	 *  直接暴露 utf8的字节开始匹配而不是全匹配，匹配到的结果位置信息是按字节计数的
	 * @param buffer
	 * @param pos
	 * @param end
	 * @param callback
	 */
	public void startWithBuffer(byte[] buffer, int pos, int end, final HitCallback callback) {
		if(automation.isClosed() || pointer == 0)
			throw new IllegalStateException("自动机已经释放，无法搜索");
		NativeLibrary.startWithBuffer(pointer, buffer, pos, end, callback);
	}
}
