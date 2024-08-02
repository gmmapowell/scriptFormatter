package test.geofs.lfs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.junit.Test;
import org.zinutils.utils.FileUtils;

import com.gmmapowell.geofs.Place;
import com.gmmapowell.geofs.World;
import com.gmmapowell.geofs.lfs.LFSPendingPlace;
import com.gmmapowell.geofs.lfs.LFSPlace;
import com.gmmapowell.geofs.lfs.LocalFileSystem;

/** Tests to handle the behaviour of places that don't exist (yet)
 * 
 */
public class LFSNoPlace {

	@Test
	public void weCanCreateANewFile() throws IOException {
		// Come up with a new file name, but then delete it
		File tf = File.createTempFile("newf", ",txt");
		tf.delete();
		
		// Ask the LFSWorld fr it
		World lfs = new LocalFileSystem(null);
		Place place = lfs.newPlacePath(tf.getPath());
		assertNotNull(place);
		assertTrue(place instanceof LFSPendingPlace);
		
		// And now create it
		Writer pw = place.writer();
		pw.write("hello world\n");
		pw.close();
		assertEquals("hello world\n", FileUtils.readFile(tf));
		tf.delete();
	}

	@Test
	public void anEnsuredFileMayExist() throws IOException {
		File tf = File.createTempFile("efc", ",txt");
		FileUtils.writeFile(tf, "hello, world");
		World lfs = new LocalFileSystem(null);
		Place place = lfs.ensurePlacePath(tf.getPath());
		assertNotNull(place);
		assertTrue(place instanceof LFSPlace);
		
		assertEquals("hello, world", place.read());
		tf.delete();
	}

	@Test
	public void ifAnEnsuredFileDoesNotExistItIsCreated() throws IOException {
		// Come up with a new file name, but then delete it
		File tf = File.createTempFile("efd", ",txt");
		assertTrue(tf.delete());
		
		// Ask the LFSWorld fr it
		World lfs = new LocalFileSystem(null);
		Place place = lfs.ensurePlacePath(tf.getPath());
		assertNotNull(place);
		assertTrue(place instanceof LFSPendingPlace);
		
		// And now create it
		Writer pw = place.writer();
		pw.write("hello world\n");
		pw.close();
		assertEquals("hello world\n", FileUtils.readFile(tf));
		tf.delete();
	}
}
