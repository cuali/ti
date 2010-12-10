package cua.li.ti.util.zip;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipException;

public class ZipFileTest {
	private File file;
	private ZipFile zip;

	@Before
	public void setUp() throws Exception {
		this.file = File.createTempFile("ZipFileTest", ".zip");
		this.zip = new ZipFile(this.file, ZipFile.OPEN_WRITE);
	}

	@After
	public void tearDown() throws Exception {
		if (null != this.zip) {
			this.zip.close();
		}
		if (null != this.file) {
			this.file.delete();
		}
	}

	private void checkSize(final int size) {
		try {
			final java.util.zip.ZipFile juzzf = new java.util.zip.ZipFile(this.file);
			assertTrue(size < juzzf.size());
			juzzf.close();
		} catch (final ZipException e) {
			fail("Cannot open just created zip file.");
		} catch (final IOException e) {
			fail("Cannot open just created zip file.");
		}
	}

	@Test
	public void testAddDirectoryStringFile() {
		try {
			this.zip.addDirectory("", new File("C:/temp"));
			this.zip.close();
		} catch (final FileNotFoundException e) {
			fail("C:/temp contains files that could not be added.");
		} catch (final IOException e) {
			fail("C:/temp could not be added: " + e.getMessage());
		}
		checkSize(4);
	}

	@Test
	public void testAddDirectoryStringFileBoolean() {
		try {
			this.zip.addDirectory("", new File("C:/temp"), true);
			this.zip.close();
		} catch (final FileNotFoundException e) {
			fail("C:/temp contains files and directories that could not be recursively added.");
		} catch (final IOException e) {
			fail("C:/temp could not be added: " + e.getMessage());
		}
		checkSize(6);
	}

	@Test
	public void testAddEntryStringFile() {
		try {
			final String name = this.zip.addEntry("", new File("C:/temp"));
			assertTrue("temp".equals(name));
			this.zip.close();
		} catch (final FileNotFoundException e) {
			fail("C:/temp contains files that could not be added.");
		} catch (final IOException e) {
			fail("C:/temp could not be added: " + e.getMessage());
		}
		checkSize(4);
	}

	@Test
	public void testAddEntryStringFileBoolean() {
		try {
			final String name = this.zip.addEntry("", new File("C:/temp"), true);
			assertTrue("temp".equals(name));
			this.zip.close();
		} catch (final FileNotFoundException e) {
			fail("C:/temp contains files and directories that could not be recursively added.");
		} catch (final IOException e) {
			fail("C:/temp could not be added: " + e.getCause());
		}
		checkSize(6);
	}

}
