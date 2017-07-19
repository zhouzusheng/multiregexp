# multiregexp
多模式正则匹配

对 c++ 实现的Lightgrep 的一个java 包装，接口重新设计， 风格更偏向java。
此外， c++  部分也对原来的 Lightgrep 做了少量修改，不需要依赖 icu， 并从re2 增加了 一些 unicode 属性名称。
修改代码将在另外项目中公布。

项目由来

在实际工作中， 我需要从数以10万计的正则中匹配出给定的文本，所以一直在寻找一种多模式正则匹配方案。
以前，有一个开源的firebird 项目，可以实现这个功能。目前这个项目闭源了，而且作者从code.google.com 中删除了之前的开源项目。
幸运的是，还有一个开源的Lightgrep，功能满足我的需要。这个项目已经有一些 java bind了，但是我不是很满意，决定再做一个。

特征

1. 同时支持单模式与多模式
2. 支持一次编译，多次使用，其中使用过程可以在另外的线程中
3. 支持编译后序列化为字节数组，进而可以序列化到文件。当然也支持序列化加载
4. 匹配支持字符串或者字节数组
5. 匹配位置自动转换为按字符计数而不是原始的字节计数

调用例子

      MultiPattern patterns = new MultiPattern());
      patterns.addPattern("敏感词", 0);
      patterns.addPattern("红牛", 0);
      patterns.addPattern("可乐雪碧", 0);
      patterns.addPattern("匹配", 0);
      
      Automation automation = patterns.toAutomation();
      patterns.close();
      
      Matcher matcher = automation.createMatcher();
      final GrepString r = new GrepString("今天，我们需要尝试一下敏感词的匹配，其中红牛和可乐雪碧都是我们要匹配的目标");
      matcher.match(r,  new HitCallback() {
          @Override
          public void match(HitItem item) {
            System.out.println(item.getId() + " " + r.subSequence(item.getStart(), item.getEnd()));
          }
      });
      r.close();
      matcher.close();
      
      automation.close();
      
			
			
有问题请加群  658587500
