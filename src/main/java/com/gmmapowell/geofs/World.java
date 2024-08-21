package com.gmmapowell.geofs;

/** A <tt>World</tt> represents a whole mechanism for obtaining file-like objects.  Examples include things such
 * as local filesystems, unmounted networked filesystems (NFS), cloud filesystems (S3) or remote/cloud filesystems
 * (SCP, GoogleDrive, iCloud).
 * 
 * The first thing you will need to do is to set up the access mechanism: you may need to identify its location, provide authentication,
 * etc.  All of this is outside the scope of this interface and involves the construction of the implemented resource.
 * 
 * However, that resource should implement this interface which then enables you to find <tt>Region</tt>s of that, and within each
 * <tt>Region</tt>, it is possible to find sub-regions and <tt>Place</tt>s.
 */
public interface World {
	/** In order to support a "create-configure-use" model, before use, the "prepare" method will be called.
	 * It is expected that prepare() is idempotent, that is, it can be called multiple times without causing problems.
	 * @throws Exception 
	 */
	void prepare() throws Exception;

	/** Find a single, identifiable root for this <tt>World</tt>.  This may or may not be applicable depending on
	 * the implementation.  For example, a Linux filesystem will have a single root (<tt>/</tt>), whereas a Windows
	 * filesystem will not (it will require a drive letter).  Each implementation should make it clear what is available and what is not.
	 */
	Region root();
	
	/** Find one of many identifiable roots for this <tt>World</tt> by name.  If there is a single, unique root, expect to receive an
	 * error.
	 * 
	 * Note that it is possible to have both this method and the default <tt>root()</tt> method be implemented by a single implementation.
	 * For example, the Linux filesystem supports the default <tt>root()</tt> as an alias for <tt>root("/")</tt>.
	 */
	Region root(String root);
	
	/** For compatibility with the usual mechanism of whole paths, this method allows the user to provide an entire path which resolves to
	 * a <tt>Region</tt>.  An error will be thrown if the path cannot be found or resolves to a <tt>Place</tt> instead of a <tt>Region</tt>.
	 */
	Region regionPath(String path);

	/** For compatibility with the usual mechanism of whole paths, this method allows the user to provide an entire path which resolves to
	 * a <tt>Region</tt>.  An error will be thrown if the path already exists.
	 */
	Region newRegionPath(String path);
	
	/** For compatibility with the usual mechanism of whole paths, this method allows the user to provide an entire path which resolves to
	 * a <tt>Place</tt>.  An error will be thrown if the path cannot be found or resolves to a <tt>Region</tt> instead of a <tt>Place</tt>.
	 */
	Place placePath(String path);

	/** For compatibility with the usual mechanism of whole paths, this method allows the user to provide an entire path which resolves to
	 * a <tt>Place</tt>.  An error will be thrown if the path to the place exists.
	 */
	Place newPlacePath(String path);

	/** For compatibility with the usual mechanism of whole paths, this method allows the user to provide an entire path which resolves to
	 * a <tt>Place</tt>.  The place may or may not exist.  If it does not exist, errors will be thrown if the user attempts to read it.
	 */
	Place ensurePlacePath(String path);

	/** In general, we can expect a World to be part of a wider universe - there may be local filesystem worlds, cloud worlds, remote server
	 * worlds, etc.
	 * 
	 * If a world is part of a Universe, then it is possible to obtain a handle to the universe from this world.
	 * 
	 * @return the broader Universe of which this World is part
	 */
	Universe getUniverse();
}
