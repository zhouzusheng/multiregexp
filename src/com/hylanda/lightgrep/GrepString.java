package com.hylanda.lightgrep;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/**
 * 对查询输入字符串的简单封装
 * 主要是实现了按字节或字符定位功能
 * 底层JNI 支持的字符是UTF8的， 返回的位置也是按字节计算的，
 * 用这个类保证输入会转换为 UTF8 字节， 返回的位置也自动换算为字符位置
 * @author zhouzusheng
 *
 */
public class GrepString extends Handle implements CharSequence, AutoCloseable{
	
	//输入
	private final CharSequence input;
	
	//输入的字节
    private final byte[] utf8CString;
    
    //字节到字符的换算
    private final UTF8CharOffset utf8Offset;
    
    public GrepString(CharSequence input) {
        this.input = input;
        try {
            this.utf8CString = createUtf8CString(input);
        } catch (Exception e ){
            throw new IllegalArgumentException("Unable to encode input using UTF-8", e);
        }
        //pointer 才是调用JNI 时真正用到的参数
        this.pointer = NativeLibrary.createStringBuffer(utf8CString);
        this.utf8Offset = new UTF8CharOffset(input);
    }

    public int bytePos(int charPosition) {
        check();
        return utf8Offset.fromStringToByte(charPosition);
    }
    public int charPos(int bytePosition) {
        check();
        return utf8Offset.fromByteToChar(bytePosition);
    }
    

    long pointer() { 
        return pointer;
    }

    public byte[] utf8CString() {
    	return utf8CString;
    }

    private void check() {
        if (pointer == 0) 
            throw new IllegalStateException("Buffer has been already closed!");
    }

    private void free() {
        if (pointer != 0) {
        	NativeLibrary.releaseStringBuffer(utf8CString, pointer);
        	pointer = 0;
        }
    }
    @Override
    public void close() {
        free();
    }

    @Override
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }


    @Override
    public int length() {
        return input.length();
    }

    @Override
    public char charAt(int index) {
        return input.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return input.subSequence(start, end);
    }

    @Override
    public String toString() {
        return input.toString();
    }

    private byte[] createUtf8CString(CharSequence s) throws Exception {
        CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

        ByteBuffer bytes = encoder.encode(CharBuffer.wrap(s));
        //末尾添加一个0
        if (bytes.limit() == bytes.capacity()) {
            ByteBuffer newBuffer = ByteBuffer.allocate(bytes.limit()+1);
            System.arraycopy(bytes.array(), 0, newBuffer.array(), 0, bytes.limit());
            bytes = newBuffer;
        } else
            bytes.limit(bytes.limit()+1);

        bytes.put(bytes.limit()-1, (byte) 0);
        return bytes.array();
    }

}
