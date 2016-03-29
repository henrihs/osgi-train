package test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.naming.SizeLimitExceededException;

public class Main {
	public static void main(String[] args) throws UnsupportedEncodingException, SizeLimitExceededException {
		byte[] b1 = "012345678".getBytes("utf-8");
		byte[] b2 = pad(b1);
		
		System.out.println(b1.length);
		System.out.println(b2.length);
		
		for (byte b : b1) {
			System.out.print(b);
		}
		System.out.println();
		for (byte b : b2) {
			System.out.print(b);
		}
		System.out.println();
		
		System.out.println(new String(b1));
		System.out.println(new String(b2));
		
		System.out.println(parse(b1));
		System.out.println(parse(b2));
	}
	
	private static String printSignedHex(byte b) {
		if (b < 0) {
			String s = Integer.toHexString(b);
			return (s.substring(s.length()-2));
		}
		else return (Integer.toHexString(b));
	}

	private static void printBuffer(int i, byte[] dataBuffer) {
		String s = (String.format("Block %d content: [", i));
		for (int j = 0; j < 16; j++) {
			s += printSignedHex( dataBuffer[j] );
		}
		s += ("]");
		System.out.println(s);
	}
	
	private static byte[] pad(byte[] oldBytes) throws SizeLimitExceededException {
		if (oldBytes.length > 16) {
			throw new SizeLimitExceededException("Too large data set");
		}
		
		byte[] newBytes = new byte[16];
		int diff = newBytes.length-oldBytes.length;
		for (int i = 0; i < newBytes.length; i++) {
			newBytes[i] = i >= diff ? oldBytes[i-diff] : 0x00;
		}
		
		return newBytes;
	}
	
	private static String parse(byte[] array) {
		ArrayList<Byte> bytes = new ArrayList<Byte>();
		for (byte b : array) {
			if (b != 0x00) {
				bytes.add(b);
			}
		}
		
		byte[] b = new byte[bytes.size()];
		for (int i = 0; i < b.length; i++) {
			b[i] = bytes.get(i);
		}
		
		return new String(b);
	}
}
