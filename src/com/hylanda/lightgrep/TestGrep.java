package com.hylanda.lightgrep;

public class TestGrep {
	public static void main(String[] args) throws Exception {
		new TestGrep().run();
		//new TestGrep().run_file();
	}
	
	private void run() throws Exception {
		try(MultiPattern patterns = new MultiPattern()) {
			patterns.addPattern("敏感词", 0);
			patterns.addPattern("红牛", 0);
			patterns.addPattern("可乐雪碧", 0);
			patterns.addPattern("匹配", 0);
			try(Automation automation = patterns.toAutomation()) {
				patterns.close();
				try(Matcher matcher = automation.createMatcher()){
					final GrepString r = new GrepString("今天，我们需要尝试一下敏感词的匹配，其中红牛和可乐雪碧都是我们要匹配的目标");
					
					long st = System.currentTimeMillis();
					int count = 1;
					for(int i = 0; i < count; i++) {
						matcher.match(r,  new HitCallback() {
							
							@Override
							public void match(HitItem item) {
								System.out.println(item.getId() + " " + r.subSequence(item.getStart(), item.getEnd()));
							}
						});
					}
					long end = System.currentTimeMillis();
					System.out.println("t=" + (end - st) / (count * 1.0f));
					r.close();
				}
			}
		}
		
	}
	
}
