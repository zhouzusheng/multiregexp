package com.hylanda.lightgrep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegExpSet implements AutoCloseable {
	
	public static class RegExpItem{
		int		id;
		boolean enable;
		String  name;
		Pattern when;
		String  then;
		
	}
	
	Automation   automation;
	Map<String,  Integer> nameToId;
	Map<Integer, RegExpItem> patterns;
	
	@Override
	public void close() throws Exception {
		if(automation != null) {
			automation.close();
			automation = null;
		}
		if(nameToId != null){
			nameToId.clear();
			nameToId = null;
		}
		if(patterns != null) {
			patterns.clear();
			patterns = null;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	public boolean loadPatterns(File path) {
		MultiPattern patterns = new MultiPattern();
		nameToId = new HashMap<String, Integer>();
		this.patterns = new HashMap<Integer, RegExpItem>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"))){
			String line;
			int order = 0;
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(line.isEmpty() || line.startsWith("#"))
					continue;
				if(line.startsWith("name:")) {
					String name = line.substring(5).trim();
					if(name.startsWith("disable")) //名称以 disable 开头的  忽略
						continue;
					
					line = reader.readLine();
					String when = parseRegxpString(line, "when:");
					if(when == null) {
						break; //格式不对
					}
					
					int flags = 0;
					if(line.trim().endsWith("\"/nocase")){
						flags = 1;
					}
					line = reader.readLine();
					String then = parseRegxpString(line, "then:");
					if(then == null) {
						break; //格式不对
					}
					if(patterns.addPattern(when, flags)){
						nameToId.put(name, order);
						RegExpItem item = new RegExpItem();
						item.id = order;
						item.enable = true;
						item.name = name;
						//优化，包含$ 的才需要再次编译正则
						if(then.contains("$"))
							item.when = Pattern.compile(when, flags == 1? Pattern.CASE_INSENSITIVE:0);
						item.then = then;
						this.patterns.put(order, item);
						order++;
					} else {
						System.err.println(when + " error");
					}
				}
			}
			automation = patterns.toAutomation();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			try {
				close();
			} catch (Exception e1) {
			}
			return false;
		} finally {
			try {
				patterns.close();
			} catch (Exception e) {
			}
		}
		return true;
	}
	
	public void enableItem(String name, boolean enable) {
		Integer Id = nameToId.get(name);
		if(Id != null) {
			patterns.get(Id).enable = enable;
		}
	}
	
	public String replaceAll(String input) {
		if(automation == null)
			return input;
		Matcher matcher = new Matcher(automation);
		final GrepString text = new GrepString(input);
		try {
			return matcher.replace(text, new ReplaceCallback(){
				@Override
				public void action(HitItem item, StringBuilder buffer) {
					doReplace(text, item, buffer);
				}

				@Override
				public boolean validate(HitItem item) {
					return doValidate(item);
				}
			});
		} finally {
			try {
				text.close();
				matcher.close();
			} catch (Exception e) {
			}
		}
	}
	protected boolean doValidate(HitItem item){
		RegExpItem regexp = patterns.get(item.getId());
		return regexp == null || regexp.enable;
	}
	protected void doReplace(GrepString text, HitItem item, StringBuilder buffer) {
		RegExpItem regexp = patterns.get(item.getId());
		if(regexp == null || !regexp.enable) {
			//原样保留
			buffer.append(text, item.getStart(), item.getEnd());
		} else {
			//执行替换，只有包含 $ 的才需要再次执行正则
			if(regexp.when != null) {
				CharSequence sub = text.subSequence(item.getStart(), item.getEnd());
				java.util.regex.Matcher m = regexp.when.matcher(sub);
				String result = m.replaceAll(regexp.then);
				buffer.append(result);
			} else {
				buffer.append(regexp.then);
			}
		}
	}
	
	private String parseRegxpString(String line, String pre){
		if(line == null)
			return null;
		line = line.trim();
		if(line.isEmpty() ||!line.startsWith(pre))
			return null;
		
		line = line.substring(pre.length());
		if(line.startsWith("\"")) {
			int pos = line.lastIndexOf('"');
			if(pos == 0) {
				line = "";
			} else
				line = line.substring(1, pos);
		}
		return line;
	}
	
	public static void main(String[] args) throws Exception {
		RegExpSet set = new RegExpSet();
		set.loadPatterns(new File("test.txt"));
		String bbb = set.replaceAll("zhouzusheng is good");
		System.out.println(bbb);
		set.close();
		
	}
}
