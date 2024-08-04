package test.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.gmmapowell.script.config.VarMap;
import com.gmmapowell.script.config.VarValue;

public class VarMapTests {

	@Test
	public void initiallyItsEmpty() {
		VarMap map = new VarMap();
		assertTrue(map.isEmpty());
	}

	@Test
	public void oneEntryIsEnoughToStopItBeingEmpty() {
		VarMap map = new VarMap();
		map.put(3, "hello", "world");
		assertFalse(map.isEmpty());
	}

	@Test
	public void oneEntryIsAUniqueString() {
		VarMap map = new VarMap();
		map.put(3, "hello", "world");
		assertEquals("world", map.remove("hello"));
		assertTrue(map.isEmpty());
	}

	@Test
	public void twoEntriesWithTheSameKeyMakeAList() {
		VarMap map = new VarMap();
		map.put(3, "hello", "world");
		map.put(3, "hello", "there");
		List<String> strs = (List<String>) map.all("hello");
		assertEquals(2, strs.size());
		assertEquals("world", strs.get(0));
		assertEquals("there", strs.get(1));
		assertFalse(map.isEmpty());
	}

	@Test
	public void weCanExplicitlyDeleteAList() {
		VarMap map = new VarMap();
		map.put(3, "hello", "world");
		map.put(3, "hello", "there");
		map.delete("hello");
		assertTrue(map.isEmpty());
	}

	@Test(expected=RuntimeException.class)
	public void weCannotPushSomethingWithDepth0() {
		VarMap map = new VarMap();
		map.put(0, "hello", "world");
	}

	@Test
	public void valuesCanNest() {
		VarMap map = new VarMap();
		map.put(3, "hello", "world");
		map.put(6, "fred", "there");
		VarValue val = map.value("hello");
		assertNotNull(val);
		assertFalse(val.map().isEmpty());
		assertEquals("there", val.map().remove("fred"));
		assertEquals("world", val.unique());
		assertFalse(map.isEmpty());
	}

	@Test
	public void valuesCanNestAndThenRevert() {
		VarMap map = new VarMap();
		map.put(3, "hello", "world");
		map.put(6, "fred", "there");
		map.put(3, "bert", "bloggs");
		VarValue val = map.value("hello");
		assertEquals("there", val.map().remove("fred"));
		assertEquals("bloggs", map.remove("bert"));
	}
}
