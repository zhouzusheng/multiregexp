package com.hylanda.lightgrep;

/**
 * 查询命中结果的回调类
 * @author zhouzusheng
 *
 */
public interface HitCallback {
	void match(HitItem item);
}
