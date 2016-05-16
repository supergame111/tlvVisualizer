package at.innovative_solutions.tlv;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

// TODO character set checks
// TODO trailing character check on cn
public class EMVValueDecoder {
	public static String asString(final byte[] data, final String format) throws UnsupportedEncodingException {
		String formatType = format.split(" ")[0];

		StringBuilder sb;
		switch(formatType) {
		case "a":
		case "an":
		case "ans":
			return new String(data, "ISO_8859_1");
		case "b":
			return Utils.bytesToHexString(data);
		case "cn":
			sb = new StringBuilder();
			for(int n = 0; n < data.length * 2; ++n) {
				int nibble = n % 2 == 0 ? (data[n/2] & 0xff) >>> 4 : data[n/2] & 0xf;
			if(nibble == 0xf)
				break;
			sb.append(nibble);
			}
			return sb.toString();
		case "n":
			sb = new StringBuilder();
			boolean leadingZeros = true;
			for(int n = 0; n < data.length * 2; ++n) {
				int nibble = n % 2 == 0 ? (data[n/2] & 0xff) >>> 4 : data[n/2] & 0xf;
			if(nibble == 0 && leadingZeros)
				continue;
			leadingZeros = false;
			sb.append(nibble);
			}
			return sb.toString();
		default:
			return "";
		}
	}

	public static byte[] toValue(final String str, final String format) throws InvalidEncodedValueException {
		String formatType = format.split(" ")[0];
		CharsetEncoder encoder = StandardCharsets.ISO_8859_1.newEncoder();
		encoder.onMalformedInput(CodingErrorAction.REPORT);
		encoder.onUnmappableCharacter(CodingErrorAction.REPORT);

		switch(formatType)
		{
		case "a":
		{
			if(!str.matches("^[a-zA-Z]*$"))
				throw new InvalidEncodedValueException("Invalid character in input.");
			return str.getBytes(StandardCharsets.US_ASCII);
		}
		case "an":
		{
			if(!str.matches("^[a-zA-Z0-9]*$"))
				throw new InvalidEncodedValueException("Invalid character in input");
			return str.getBytes(StandardCharsets.US_ASCII);
		}
		case "ans":
		{
			byte[] conv = new byte[str.length()];
			ByteBuffer bb = ByteBuffer.wrap(conv);
			CoderResult result = encoder.encode(CharBuffer.wrap(str), bb, true);

			if(result.isError())
				throw new InvalidEncodedValueException("Invalid character in input");

			for(byte c : conv)
				if(c > 0x7e || c < 0x20)
					throw new InvalidEncodedValueException("Invalid character in input");

			return conv;
		}
		default:
		case "b":
		{
			if(str.length() % 2 != 0)
				throw new InvalidEncodedValueException("An odd number of characters can not be used for binary fields.");

			final int byteLen = str.length() / 2;
			byte[] data = new byte[byteLen];

			int byteIdx = 0;
			int charIdx = 0;
			while(byteIdx < byteLen) {
				int c1 = (byte) (Character.digit(str.charAt(charIdx), 16));
				int c2 = (byte) (Character.digit(str.charAt(charIdx + 1), 16));

				if(c1 < 0 || c2 < 0)
					throw new InvalidEncodedValueException("Invalid character in input");
				data[byteIdx] = (byte) ((c1 << 4) + c2);

				byteIdx += 1;
				charIdx += 2;
			}

			return data;
		}
		case "cn":
		{
			int byteLength = (str.length() + 1) / 2;
			byte[] data = new byte[byteLength];

			int charIdx = 0;
			int byteIdx = 0;
			while(byteIdx < byteLength)
			{
				int c1 = (str.length() > charIdx) ? Character.digit(str.charAt(charIdx), 10) : 0xF;
				int c2 = (str.length() > charIdx + 1) ? Character.digit(str.charAt(charIdx + 1), 10) : 0xF;

				if(c1 < 0 || c2 < 0)
					throw new InvalidEncodedValueException("Invalid character in input");
				data[byteIdx] = (byte) (c1 << 4 | c2);

				byteIdx += 1;
				charIdx += 2;
			}
			return data;
		}
		case "n":
		{
			int byteLength = (str.length() + 1) / 2;
			byte[] data = new byte[byteLength];

			int charIdx = 0;
			int byteIdx = 0;

			if(str.length() % 2 != 0)
			{
				data[0] = (byte) Character.digit(str.charAt(charIdx), 10);
				if(data[0] < 0)
					throw new InvalidEncodedValueException("Invalid character in input");

				byteIdx = 1;
				charIdx = 1;
			}

			while(byteIdx < byteLength)
			{
				int c1 = Character.digit(str.charAt(charIdx), 10);
				int c2 = Character.digit(str.charAt(charIdx + 1), 10);

				if(c1 < 0 || c2 < 0)
					throw new InvalidEncodedValueException("Invalid character in input");
				data[byteIdx] = (byte) (c1 << 4 | c2);

				byteIdx += 1;
				charIdx += 2;
			}
			return data;
		}
		}
	}
}
