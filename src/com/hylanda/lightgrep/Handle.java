package com.hylanda.lightgrep;

/**
 * 对C 句柄的封装
 * @author zhouzusheng
 *
 */
public abstract class Handle {
	protected long pointer;
	public boolean isClosed() {
		return pointer == 0;
	}
}
