package at.innovative_solutions.tlv.test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import at.innovative_solutions.tlv.ConstructedTLV;
import at.innovative_solutions.tlv.Formatter;
import at.innovative_solutions.tlv.ID;
import at.innovative_solutions.tlv.PrimitiveTLV;
import at.innovative_solutions.tlv.TLV;
import at.innovative_solutions.tlv.Utils;

public class ConstructedTLVTest {
	@Test
	public void test_ConstructedTLV_simple() {
		final ID id = new ID(ID.CLASS_APPLICATION, false, 2);
		final ID s1ID = new ID(ID.CLASS_CONTEXT, true, 3);
		final ID s2ID = new ID(ID.CLASS_PRIVATE, true, 4);
		
		final PrimitiveTLV s1 = new PrimitiveTLV(s1ID, Utils.hexStringToBytes("1122"));
		final PrimitiveTLV s2 = new PrimitiveTLV(s2ID, Utils.hexStringToBytes("3344"));
		
		final ConstructedTLV ref = new ConstructedTLV(id, Arrays.asList(s1, s2));
		
		assertEquals("id", id, ref.getID());
		assertThat("s1", ref.getTLVs().get(0), sameInstance(s1));
		assertThat("s2", ref.getTLVs().get(1), sameInstance(s2));
		assertThat("s1 parent", ref.getTLVs().get(0).getParent(), sameInstance(ref));
		assertThat("s2 parent", ref.getTLVs().get(1).getParent(), sameInstance(ref));
	}
	
	@Test
	public void test_equals_simple() {
		ConstructedTLV ref = new ConstructedTLV(
				new ID(ID.CLASS_APPLICATION, false, 1),
				Arrays.asList(
						new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 2), new byte[] { 0x11, 0x22 }),
						new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 3), new byte[] { 0x33, 0x44 })));
		
		assertThat(ref, not(new String()));
		
		assertEquals("same",
			ref,
			new ConstructedTLV(
				new ID(ID.CLASS_APPLICATION, false, 1),
				Arrays.asList(
					new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 2), new byte[] { 0x11, 0x22 }),
					new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 3), new byte[] { 0x33, 0x44 }))));
		
		assertNotEquals("other root id",
			ref,
			new ConstructedTLV(
				new ID(ID.CLASS_CONTEXT, false, 1),
				Arrays.asList(
					new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 2), new byte[] { 0x11, 0x22 }),
					new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 3), new byte[] { 0x33, 0x44 }))));

		assertNotEquals("other sub tlvs",
			ref,
			new ConstructedTLV(
				new ID(ID.CLASS_APPLICATION, false, 1),
				Arrays.asList(
					new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 4), new byte[] { 0x11, 0x22 }),
					new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 3), new byte[] { 0x33, 0x44 }))));

		assertNotEquals("only one sub tlv",
			ref,
			new ConstructedTLV(
				new ID(ID.CLASS_APPLICATION, false, 1),
				Arrays.asList(
					new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 3), new byte[] { 0x33, 0x44 }))));
		
		assertNotEquals("more sub tlv",
			ref,
			new ConstructedTLV(
				new ID(ID.CLASS_APPLICATION, false, 1),
				Arrays.asList(
					new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 2), new byte[] { 0x11, 0x22 }),
					new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 3), new byte[] { 0x33, 0x44 }),
					new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 4), new byte[] { 0x33, 0x44 }))));

		assertNotEquals("swapped sub tlvs",
			ref,
			new ConstructedTLV(
				new ID(ID.CLASS_APPLICATION, false, 1),
				Arrays.asList(
					new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 3), new byte[] { 0x33, 0x44 }),
					new PrimitiveTLV(new ID(ID.CLASS_CONTEXT, true, 2), new byte[] { 0x11, 0x22 }))));
	}
	
	@Test
	public void test_accept_simple() {
		String result = "asdf";
		@SuppressWarnings("unchecked")
		Formatter<String> f = mock(Formatter.class);
		ConstructedTLV ref = new ConstructedTLV(null, new LinkedList<TLV>());
		when(f.format(ref)).thenReturn(result);
		
		String retVal = ref.accept(f);
		
		verify(f).format(ref);
		assertTrue("retval correct", retVal == result);
	}
	
	@Test
	public void test_toString_simple() {
		ID id = mock(ID.class);
		when(id.toString()).thenReturn("{id}");
		PrimitiveTLV s1 = mock(PrimitiveTLV.class);
		PrimitiveTLV s2 = mock(PrimitiveTLV.class);
		when(s1.toString()).thenReturn("{s1}");
		when(s2.toString()).thenReturn("{s2}");
		
		ConstructedTLV ref = new ConstructedTLV(id, Arrays.asList(s1, s2), false);
		
		assertEquals("ConstructedTLV({id}, <{s1}, {s2}, >, false)", ref.toString());
	}
}