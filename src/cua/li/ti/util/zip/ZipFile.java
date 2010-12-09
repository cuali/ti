package cua.li.ti.util.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author A@cua.li
 */
public class ZipFile {
	public static final int OPEN_DELETE = java.util.zip.ZipFile.OPEN_DELETE;
	public static final int OPEN_READ = java.util.zip.ZipFile.OPEN_READ;
	public static final int OPEN_WRITE = 2;

	private ZipOutputStream zos;
	private java.util.zip.ZipFile zf;
	private final String name;

	public ZipFile(final File file, final int mode) throws IOException {
		if (OPEN_WRITE == mode) {
			this.zos = new ZipOutputStream(new FileOutputStream(file));
		} else {
			this.zf = new java.util.zip.ZipFile(file, mode);
		}
		this.name = file.getPath();
	}

	public void addDirectory(final String path, final File file) throws FileNotFoundException, IOException {
		this.addDirectory(path, file, false);
	}

	public void addDirectory(final String path, final File file, final boolean recursively) throws IOException {
		// get a listing of the directory content
		final String[] list = file.list();
		for (final String filename : list) {
			final File content = new File(file, filename);
			if (content.isDirectory()) {
				// if the File object is a directory, call this
				// function again to add its content recursively
				if (recursively) {
					addEntry(path, content, recursively);
				}
			} else {
				addEntry(path, content, recursively);
			}
		}
	}

	public void addEntry(final String path, final File file) throws FileNotFoundException, IOException {
		this.addEntry(path, file, false);
	}

	public void addEntry(final String path, final File file, final boolean recursively) throws FileNotFoundException,
			IOException {
		final String root = ((null != path) && (0 < path.length()) && !path.endsWith("/")) ? path + '/' : "";
		final byte[] buffer = new byte[2156];
		int bytesRead = 0;
		if (file.isDirectory()) {
			final String directory = root + file.getName();
			addDirectory(directory, file, recursively);
		} else {
			// if we reached here, the File object was not a directory
			// create a FileInputStream on top of file
			final FileInputStream fis = new FileInputStream(file);
			// create a new zip entry
			final ZipEntry anEntry = new ZipEntry(root + file.getName());
			// place the zip entry in the ZipOutputStream object
			this.zos.putNextEntry(anEntry);
			// now write the content of the file to the ZipOutputStream
			while ((bytesRead = fis.read(buffer)) != -1) {
				this.zos.write(buffer, 0, bytesRead);
			}
			this.zos.closeEntry();
			// close the Stream
			fis.close();
		}
	}

	public void close() throws IOException {
		if (null != this.zos) {
			this.zos.flush();
			this.zos.close();
		}
		if (null != this.zf) {
			this.zf.close();
		}
	}

	// the following methods are offering a complete compatibility with the
	// java.util.zip.ZipFile

	/**
	 * @see java.util.zip.ZipFile#getName()
	 */
	public String getName() {
		return (null == this.zf) ? this.name : this.zf.getName();
	}

	/**
	 * @see java.util.zip.ZipFile#getInputStream(ZipEntry)
	 */
	public InputStream getInputStream(final ZipEntry entry) throws IOException {
		return (null == this.zf) ? null : this.zf.getInputStream(entry);
	}

	/**
	 * @see java.util.zip.ZipFile#getEntry(String)
	 */
	public ZipEntry getEntry(final String name) {
		return (null == this.zf) ? null : this.zf.getEntry(name);
	}

	/**
	 * @see java.util.zip.ZipFile#size()
	 */
	public int size() {
		return (null == this.zf) ? -1 : this.zf.size();
	}

	/**
	 * @see java.util.zip.ZipFile#entries()
	 */
	public Enumeration<? extends ZipEntry> entries() {
		return (null == this.zf) ? null : this.zf.entries();
	}

	/**
	 * @see java.util.zip.ZipFile#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}
}
