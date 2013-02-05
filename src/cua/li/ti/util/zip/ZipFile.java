package cua.li.ti.util.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
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
	private final File tmpzip;

	public ZipFile(final File file, final int mode) throws IOException {
		if (OPEN_WRITE == mode) {
			tmpzip = File.createTempFile("tmp-",".zip");
			this.zos = new ZipOutputStream(new FileOutputStream(tmpzip));
		} else {
			tmpzip = null;
			this.zf = new java.util.zip.ZipFile(file, mode);
		}
		this.name = file.getPath();
	}

	public void addDirectory(final String path, final File file) throws FileNotFoundException, IOException {
		this.addDirectory(path, file, false);
	}

	public void addDirectory(final String path, final File file, final boolean recursively)
	        throws FileNotFoundException, IOException {
		// get a listing of the directory content
		final String[] list = file.list();
		for (final String filename : list) {
			final File content = new File(file, filename);
			if (content.isDirectory()) {
				// if the File object is a directory, call this function again to add its content recursively
				if (recursively) {
					addEntry(path, content, recursively);
				}
			} else {
				addEntry(path, content, recursively);
			}
		}
	}

	public String addEntry(final String path, final File file) throws FileNotFoundException, IOException {
		return this.addEntry(path, file, false);
	}

	public String addEntry(final String path, final File file, final boolean recursively) throws FileNotFoundException,
	        IOException {
		final String entryName = (((null != path) && (0 < path.length()) && !path.endsWith("/")) ? path + '/' : "")
		        + file.getName();
		return this.addEntry(path, entryName, file, recursively);
	}

	public String addEntry(final String path, final String entryAlias, final File file, final boolean recursively)
	        throws FileNotFoundException, IOException {
		if (file.isDirectory()) {
			addDirectory(entryAlias, file, recursively);
		} else {
			// if we reached here, the File object was not a directory
			// create a FileInputStream on top of file
			final FileInputStream fis = new FileInputStream(file);
                        this.addEntry(entryAlias, fis);
			// close the input Stream
			fis.close();
		}
		return entryAlias;
	}

	public String addEntry(final String entryAlias, final InputStream is)
	        throws FileNotFoundException, IOException {
                // create a new zip entry
                final ZipEntry anEntry = new ZipEntry(entryAlias);
                anEntry.setComment(entryAlias);
                // place the zip entry in the ZipOutputStream object if it is not already there
                try {
                        this.zos.putNextEntry(anEntry);
                        final byte[] buffer = new byte[2156];
                        int bytesRead = 0;
                        // now write the content of the file to the ZipOutputStream
                        while ((bytesRead = is.read(buffer)) != -1) {
                                this.zos.write(buffer, 0, bytesRead);
                        }
                        this.zos.closeEntry();
                } catch (ZipException ze) {
                        // just skip this entry silently
                }
                return entryAlias;
	}

	public void close() throws IOException {
		if (null != this.zos) {
			this.zos.flush();
			this.zos.close();
			Files.move(this.tmpzip.toPath(), new File(name).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		if (null != this.zf) {
			this.zf.close();
		}
	}

	public static void extract(final URL url, final File destination) throws IOException {
		InputStream is = null;
		if ("jar".equals(url.getProtocol())) {
			final JarURLConnection connection = (JarURLConnection) url.openConnection();
			is = connection.getJarFile().getInputStream(connection.getJarEntry());
		} else if ("file".equals(url.getProtocol())) {
			try {
				is = new BufferedInputStream(new FileInputStream(new File(url.toURI())));
			} catch (final URISyntaxException urise) {
				Logger.getLogger(ZipFile.class.getName()).log(Level.SEVERE, null, urise);
			}
		} else
			throw new UnsupportedOperationException("Unsupported protocol: " + url.getProtocol());
		extract(is, destination);
	}

    public static void extract(final InputStream is, final File destination) throws FileNotFoundException, IOException {
        if (!destination.getParentFile().exists()) {
			destination.mkdirs();
		}
		final OutputStream bos = new BufferedOutputStream(new FileOutputStream(destination));
		final byte[] buffer = new byte[2156];
		int bytesRead = 0;
		while ((bytesRead = is.read(buffer)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}
		bos.close();
        is.close();
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
