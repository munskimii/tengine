package com.munskimii.tengine;

/**
 * The TemplateInterface interface provides the interface to access the generated template code base.
 *
 * If you are new to tengine, this class along with the Template class is where things can be a little weird.
 * A good time to pause and get a feel for these two classes, once you get them, everything will fall into place.
 *
 * The Template.build() method create a new java class (a new code base), the new java class will implement this interface.
 * The Template.load() method will load the built/compiled java class, it will cast the object into this interface.
 *
 * Please note, the actual management or java files, class files, and compilation must be created by a template engine.
 * You can review base/BaseEngine.java for an implementation reference.
 *
 * Author: Michael Monschke
**/

import java.io.*;
import java.util.*;

public interface TemplateInterface {

	/** 
	 * The init method is called after the interface class is instantiated.  It is passed the template context and name of template.
	 * The templates themselves can access these two pre-defined scriptlet variables as "ctx" and "name".
	**/
	public void init(TContext rCtx, String sName);

	/**
	 * The method that will generate documents is found here (the core function).  The actual implementation is found in generated
	 * java files though, the generated java files are created from Template.build().
	 *
	 * @param java object instances representing the data records the template is to use to build documents
	 * @param out the output stream the template should use to write the document
	**/
	public void generate(MetaRecord meta, OutputStreamWriter out) throws IOException;
}


