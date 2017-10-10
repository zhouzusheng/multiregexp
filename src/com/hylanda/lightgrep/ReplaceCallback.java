package com.hylanda.lightgrep;

public interface ReplaceCallback {
	void action(HitItem item, StringBuilder buffer);
}
