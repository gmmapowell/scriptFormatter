package test.geofs.lfs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.zinutils.utils.FileUtils;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.Region;
import com.gmmapowell.geofs.lfs.LocalFileSystem;
import com.gmmapowell.geofs.listeners.LineListener;

// This is an "integration" test and as such, it depends on external realities.
// I have tried to write it in a way that works on all Linux systems.
// Obviously it would be possible to put more effort in to analyze what we have and what exists and adjust.
public class BasicLFS {
	@Rule public JUnitRuleMockery context = new JUnitRuleMockery();

	@Test
	public void testWeCanFindATmpFileWeCreate() throws IOException {
		LineListener lsnr = context.mock(LineListener.class);
		context.checking(new Expectations() {{
			oneOf(lsnr).line("hello, world!");
		}});
		
		File tf = File.createTempFile("lfs", ".txt");
		FileUtils.writeFile(tf, "hello, world!\n");
		List<String> elts = FileUtils.pathElements(tf);
		LocalFileSystem lfs = new LocalFileSystem();
		Region r = lfs.root();
		for (int i=0;i<elts.size()-1;i++) {
			String s = elts.get(i);
			if (s == null || s.length() == 0)
				continue;
			r = r.subregion(s);
		}
		Place place = r.place(elts.get(elts.size()-1));
		place.lines(lsnr);
	}

}
