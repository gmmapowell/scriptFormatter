package com.gmmapowell.geofs;

/** A <tt>Region</tt> corresponds to the general notion of a directory, folder or prefix of a filename.
 * 
 * In every situation, this represents a container of multiple items (<tt>Region</tt>s or <tt>Place</tt>s).
 * 
 * As with the <tt>Place</tt> object, it is possible to ask this object to perform certain operations for you,
 * such as to copy its contents elsewhere, clean itself, create nested <tt>Region</tt> or <tt>Place</tt> objects,
 * etc.
 * 
 * Also, the fact that you have this object says that the directory or folder exists (or at least existed when
 * you obtained it).
 */
// TODO: should there be a "hypothetical" region which may or may not be real?
// We do have String paths, but that seems a little flaky (it may not parse, for example).  Some version of a "path" or a "route" or a "narrowing" might be appropriate.
public interface Region {
	/** Find a nested <tt>Region</tt> within this <tt>Region</tt>.
	 * 
	 * @param name the name of the <tt>Region</tt>
	 * @return the nested <tt>Region</tt>
	 */
	Region subregion(String name);
	/** Find a <tt>Place</tt> within this <tt>Region</tt>.
	 * 
	 * @param name the name of the <tt>Place</tt>
	 * @return the nested <tt>Place</tt>
	 */
	Place place(String name);
	
	/** For compatibility with the usual mechanism of whole paths, this method allows the user to provide an entire path which resolves to
	 * a <tt>Region</tt>.  An error will be thrown if the path cannot be found or resolves to a <tt>Place</tt> instead of a <tt>Region</tt>.
	 */
	Region regionPath(String path);
	
	/** For compatibility with the usual mechanism of whole paths, this method allows the user to provide an entire path which resolves to
	 * a <tt>Place</tt>.  An error will be thrown if the path cannot be found or resolves to a <tt>Region</tt> instead of a <tt>Place</tt>.
	 */
	Place placePath(String path);
	
	Region parent();
	// TODO: directory operations
	// TODO: "create" will need to take a parameter which indicates in an abstract way any configuration parameters, eg. file mode on Linux,
	// or content type on S3

	/** This is basically a nested directory and mkdir() wrapped up in a single operation.
	 * But it quietly allows the directory to already exist.
	 * @param name the name of the subregion to access or create
	 * @return a region object that describes the definitely existing subregion.
	 */
	Region ensureSubregion(String name);

	
}
