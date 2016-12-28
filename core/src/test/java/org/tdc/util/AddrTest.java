package org.tdc.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link Addr}.
 */
public class AddrTest {
	@Test
	public void testCachingReturnsSameConfig() {
		Addr a = new Addr("/test1/test2");
		Addr b = new Addr("\\test1\\test2");
		Addr c = new Addr("test1/test2");
		Addr d = new Addr("test1\\test2");
		Addr e = new Addr("test1/test2/");
		Addr f = new Addr("test1\\test2\\");
		Addr g = new Addr("test1/test3");
		assertThat(a).isEqualTo(b);
		assertThat(a).isEqualTo(c);
		assertThat(a).isEqualTo(d);
		assertThat(a).isEqualTo(e);
		assertThat(a).isEqualTo(f);
		assertThat(a).isNotEqualTo(g);
	}
}
