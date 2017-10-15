package com.hylanda.lightgrep;

public interface ReplaceCallback {
	boolean validate(HitItem item);
	void action(HitItem item, StringBuilder buffer);
}
