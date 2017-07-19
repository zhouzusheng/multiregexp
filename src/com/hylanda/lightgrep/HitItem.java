package com.hylanda.lightgrep;

/**
 * 查询命中结果
 * @author zhouzusheng
 *
 */
public class HitItem {
	private int start; //命中的开始位置， 用包装类时已经转换为 字符计数，直接用jni 时是字节计数
	private int end;//命中的结束位置（不包括）， 用包装类时已经转换为 字符计数，直接用jni 时是字节计数
	private int id; //模式的id， 从0 开始编号
	
	public HitItem(){
		
	}
	
	public HitItem(int start, int end, int id) {
		super();
		this.start = start;
		this.end = end;
		this.id = id;
	}
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
}
