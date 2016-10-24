package at.innovative_solutions.tlv;

public class NullValueDecoder implements ValueDecoder {

	@Override
	public String getName(TLV tlv) {
		return null;
	}

	@Override
	public String toString(TLV tlv) {
		return null;
	}

	@Override
	public byte[] toValue(String str, TLV tlv) {
		return null;
	}

	@Override
	public String getFormat(TLV tlv) {
		return null;
	}

	@Override
	public boolean isValueParsable(final TLV tlv) {
		return false;
	}
}
