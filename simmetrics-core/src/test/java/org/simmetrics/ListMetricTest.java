
package org.simmetrics;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.simmetrics.tokenizers.Tokenizer;
import org.simmetrics.tokenizers.Whitespace;

@SuppressWarnings("javadoc")
public abstract class ListMetricTest extends MetricTest<List<String>> {

	protected static final class T {
		protected final List<String> a;
		protected final List<String> b;
		protected final float similarity;

		public T(float similarity, List<String> string1, List<String> string2) {
			this.a = string1;
			this.b = string2;
			this.similarity = similarity;
		}

		public T(float similarity, String a, String b) {
			this(new Whitespace(), similarity, a, b);
		}

		public T(Tokenizer t, float similarity, String a, String b) {
			this(similarity, t.tokenizeToList(a), t.tokenizeToList(b));
		}
	}

	private static void testContainsListWithNullVsListWithouthNullTest(
			T[] listTests) {
		for (T t : listTests) {
			if (t.a.contains(null) ^ t.b.contains(null)) {
				return;
			}
		}

		fail("tests did not contain list with null vs list without null test");
	}

	private static void testIllegalArgumentException(Metric<List<String>> metric,
			List<String> a, List<String> b, List<String> nullList) {
		try {
			metric.compare(nullList, b);
			fail("Metric should have thrown a illegal argument exception for the first argument");
		} catch (IllegalArgumentException ignored) {
			// Ignored
		}

		try {
			metric.compare(a, nullList);
			fail("Metric should have thrown a illegal argument exception for the second argument");
		} catch (IllegalArgumentException ignored) {
			// Ignored
		}

		try {
			metric.compare(nullList, nullList);
			fail("Metric should have thrown a illegal argument exception for either argument");
		} catch (IllegalArgumentException ignored) {
			// Ignored
		}
	}

	private static MetricTest.T<List<String>>[] transformTest(T... tests) {
		@SuppressWarnings("unchecked")
		MetricTest.T<List<String>>[] transformed = new MetricTest.T[tests.length];
		for (int i = 0; i < tests.length; i++) {
			T t = tests[i];
			transformed[i] = new MetricTest.T<>(t.similarity,
					unmodifiableList(t.a), unmodifiableList(t.b));
		}
		return transformed;
	}

	private T[] listTests;

	@Override
	protected final List<String> getEmpty() {
		return emptyList();
	}

	protected abstract T[] getListTests();

	public List<String> getNullList() {
		return singletonList(null);
	}

	@Override
	protected final MetricTest.T<List<String>>[] getTests() {
		listTests = getListTests();
		return transformTest(listTests);
	}

	@Test
	public void nullValues() {
		if (supportsNullValues()) {
			testContainsListWithNullVsListWithouthNullTest(listTests);
		} else {
			for (T t : listTests) {
				testIllegalArgumentException(metric, t.a, t.b, getNullList());
			}
		}
	}

	protected boolean supportsNullValues() {
		return true;
	}

}
