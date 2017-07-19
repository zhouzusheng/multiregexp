package com.hylanda.lightgrep;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
/**
 * lightgrep 的底层 jni 封装，使用者不要直接调用
 * @author zhouzusheng
 *
 */

class NativeLibrary {
	
	 public static final String OSName = System.getProperty("os.name").toLowerCase();
	 public static final String ArchName = System.getProperty("os.arch");
	 public static final boolean IsWindows = OSName.indexOf("windows") > -1;
	 public static final boolean IsLinux = OSName.indexOf("linux") > -1;
	 public static final boolean IsMacOs = !IsWindows && !IsLinux;
	 public static final String sharedExt = getSharedExtName(); 
	 

	/**
	 * parse正则成一个内部的对象
	 * @param input 正则表达式
	 * 		除普通的正则外，支持下面字符表示：
	 *
	 * \\d  数字
	   \\D, 非数字
	   \\s, 空白
	   \\S, 非空白
	   \\w, 字母
	   \\W, 非字母
	 [:alnum:] 字母与数字
	 [:alpha:] 字母
	 [:ascii:] ascii 字符
	 [:blank:] 空白 \09 \20
	 [:cntrl:]
	 [:digit:]
	 [:graph:]
	 [:lower:]
	 [:print:]
	 [:punct:]
	 [:space:]
	  [:upper:]
	  [:xdigit:]
	  \p{Arabic}  Arabic  字符
	   \P{Arabic} 非Arabic 字符
	   \p{Armenian} Armenian 字符
	   \P{Armenian} 非 Armenian
	   \p{Avestan}
	   \P{Avestan}
	    \p{Balinese}
	    \P{Balinese}
	\p{Bamum}
	\p{Batak}
	\p{Bengali}
	\p{Bopomofo}
	\p{Brahmi}
	\p{Braille}
	\p{Buginese}
	\p{Buhid}
	\p{C}
	\p{Canadian_Aboriginal}
	\p{Carian}
	\p{Cc}
	\p{Cf}
	\p{Cham}
	\p{Cherokee}
	\p{Co}
	\p{Common}
	\p{Coptic}
	\p{Cs}
	\p{Cuneiform}
	\p{Cypriot}
	\p{Cyrillic}
	\p{Deseret}
	\p{Devanagari}
	\p{Egyptian_Hieroglyphs}
	\p{Ethiopic}
	\p{Georgian}
	\p{Glagolitic}
	\p{Gothic}
	\p{Greek}
	\p{Gujarati}
	\p{Gurmukhi}
	\p{Han}
	\p{Hangul}
	\p{Hanunoo}
	\p{Hebrew}
	\p{Hiragana}
	\p{Imperial_Aramaic}
	\p{Inherited}
	\p{Inscriptional_Pahlavi}
	\p{Inscriptional_Parthian}
	\p{Javanese}
	\p{Kaithi}
	\p{Kannada}
	\p{Katakana}
	\p{Kayah_Li}
	\p{Kharoshthi}
	\p{Khmer}
	\p{L}
	\p{Lao}
	\p{Latin}
	\p{Lepcha}
	\p{Limbu}
	\p{Linear_B}
	\p{Lisu}
	\p{Ll}
	\p{Lm}
	\p{Lo}
	\p{Lt}
	\p{Lu}
	\p{Lycian}
	\p{Lydian}
	\p{M}
	\p{Malayalam}
	\p{Mandaic}
	\p{Mc}
	\p{Me}
	\p{Meetei_Mayek}
	\p{Mn}
	\p{Mongolian}
	\p{Myanmar}
	\p{N}
	\p{Nd}
	\p{New_Tai_Lue}
	\p{Nko}
	\p{Nl}
	\p{No}
	\p{Ogham}
	\p{Ol_Chiki}
	\p{Old_Italic}
	\p{Old_Persian}
	\p{Old_South_Arabian}
	\p{Old_Turkic}
	\p{Oriya}
	\p{Osmanya}
	\p{P}  标点（各国文种)
	\p{Pc} 
	\p{Pd}
	\p{Pe}
	\p{Pf}
	\p{Phags_Pa}
	\p{Phoenician}
	\p{Pi}
	\p{Po}
	\p{Ps}
	\p{Rejang}
	\p{Runic}
	\p{S}
	\p{Samaritan}
	\p{Saurashtra}
	\p{Sc}
	\p{Shavian}
	\p{Sinhala}
	\p{Sk}
	\p{Sm}
	\p{So}
	\p{Sundanese}
	\p{Syloti_Nagri}
	\p{Syriac}
	\p{Tagalog}
	\p{Tagbanwa}
	\p{Tai_Le}
	\p{Tai_Tham}
	\p{Tai_Viet}
	\p{Tamil}
	\p{Telugu}
	\p{Thaana}
	\p{Thai}
	\p{Tibetan}
	\p{Tifinagh}
	\p{Ugaritic}
	\p{Vai}
	\p{Yi}
	\p{Z}
	\p{Zl}
	\p{Zp}
	\p{Zs}

	 * @param flags  1表示不区分大小写， 0 表示默认， 区分大小写
	 * @return
	 * @throws RegExprException
	 */
	static native long parsePattern(String input, int flags) throws RegExprException;
	
	/**
	 * 释放parsePattern返回的对象
	 * @param v
	 */
	static native void releasePattern(long v);
	
	/**
	 * 创建一个正则的虚拟机
	 * @param hintStateSize： 正则虚拟机的推荐状态数，可以传0， 推荐是正则的所有字节数
	 * @param hintPatterns： 正则的推荐个数
	 * @return
	 */
	static native long createVsmStates(int hintStateSize, int hintPatterns);
	/**
	 * 将parser 好的正则加入到虚拟机
	 * @param vmStates
	 * @param pattern
	 * @return
	 */
	static native void addPattern(long vmStates, long pattern) throws RegExprException;
	
	/**
	 * 释放虚拟机
	 * @param vmStates
	 */
	static native void releaseVsmStates(long vmStates);
	
	/**
	 * 从虚拟机创建一个 DFA 自动机
	 * @param vmStates
	 * @return
	 */
	static native long createAutomation(long vmStates);
	
	/**
	 * 获取自动机的大小：字节数
	 * @param automation
	 * @return
	 */
	static native int  getAutomationSize(long automation);
	
	/**获取自动机的字节数据， 调用者可以将其存入文件，下次再用 applyAutomationBuffer 打开
	 * 
	 * @param automation
	 * @return
	 */
	static native byte[] getAutomationBuffer(long automation);
	
	/**
	 * 从 buffer 的数据返回自动机
	 * @param automation
	 * @param buffer
	 * @param off
	 * @param len
	 * @return
	 */
	static native long	  readAutomation(byte[] buffer, int off, int len);
	/**
	 * 释放自动机
	 * @param automation
	 */
	static native void releaseAutomation(long automation);
	
	/**
	 * 依据自动机创建一个搜索对象，
	 * @param automation
	 * @return
	 */
	static native long createMatcher(long automation);
	
	/**
	 * 释放搜索对象
	 * @param matcher
	 */
	static native void releaseMatcher(long matcher);
	
	/**
	 * 搜索文本
	 * @param matcher
	 * @param textPtr  文本的句柄（GrepString 的pointer 成员)
	 * @param callback
	 */
	static native void matchText(long matcher, long textPtr, int off, int end, HitCallback callback);
	
	/**
	 * 匹配 textPtr 中开头的文本
	 * @param matcher
	 * @param textPtr 文本的句柄（GrepString 的pointer 成员)
	 * @param off 文本offset（按字节数计算）
	 * @param len  文本 长度（按字节数计算）
	 * @param callback
	 */
	static native void startWithText(long matcher, long textPtr, int off, int end, HitCallback callback);
	
	/**
	 * 按字节匹配
	 * @param matcher
	 * @param buffer
	 * @param off
	 * @param length
	 * @param callback
	 */
	static native void matchBuffer(long matcher, byte[] buffer, int off, int end, HitCallback callback);
	static native void startWithBuffer(long matcher, byte[] buffer, int off, int end, HitCallback callback);
	
	
	static native long createStringBuffer(final byte[] input);
    static native void releaseStringBuffer(final byte[] input, final long pointer);
    
    
    static {
    	loadEmbeddedLibrary("liblightgrep");
    }
	
    private static boolean loadEmbeddedLibrary(final String name) {
        StringBuilder url = new StringBuilder();
        url.append("/Native/");
        url.append(getCurrentPlatformIdentifier());
        url.append('/');
        url.append(name);
        url.append('.');
        URL nativeLibraryUrl = null;
        //System.out.println(url.toString());
        // loop through extensions, stopping after finding first one
        String ext = getSharedExtName();
        
        nativeLibraryUrl = NativeLibrary.class.getResource(url.toString() + ext);
        //
        if (nativeLibraryUrl != null) {
            // native library found within JAR, extract and load
            try {
                final File libfile = File.createTempFile(name, ".lib");
                libfile.deleteOnExit(); // just in case
                //
                final InputStream in = nativeLibraryUrl.openStream();
                final OutputStream out = new BufferedOutputStream(new FileOutputStream(libfile));
                //
                int len = 0;
                byte[] buffer = new byte[8192];
                while ((len = in.read(buffer)) > -1)
                    out.write(buffer, 0, len);
                out.close();
                in.close();
                System.load(libfile.getAbsolutePath());
                                //libfile.delete();
                return true;
            } catch (IOException x) {
                x.printStackTrace();
            }
        } // nativeLibraryUrl exists
        return false;
    }

	static String getCurrentPlatformIdentifier() {
		
        String osName;
        if(IsWindows)
        	osName = "windows";
        else if(IsLinux) {
        	osName = "linux";
        } else
        	osName = "darwin";
        return osName + "/" + ArchName ;
    }
	
	static String getSharedExtName() {
		if(IsWindows)
			return "dll";
		if(IsLinux)
			return "so";
		return "dylib";
	}
	
	
}
