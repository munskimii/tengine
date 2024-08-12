package com.munskimii.tengine;

/**
 * The Template class provides the core function to create and manage the template implementations.
 *
 * If you are new to tengine, this class along with the TemplateInterface class is where things can be a little weird.
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

public class Template {

	private TContext _rCtx;
	private String _sName;
	private String _sGroup;
	private String _sExt;
	private TemplateInterface _rInterface;

	/** Constructor. **/
	public Template(TContext rCtx, String sName, String sGroup, String sExt, TemplateInterface rInterface) {
		_rCtx = rCtx;
		_sName = sName;
		_sGroup = sGroup;
		_sExt = sExt;
		_rInterface = rInterface;
		_rInterface.init(rCtx, sName);
	}

	/** Generates the document (writes to "out" parm) for the data record "meta", which contains object instances of the actual data. **/
	public void generate(MetaRecord meta, OutputStreamWriter out) throws IOException {

		_rInterface.generate(meta, out);
	}

	/** Returns the name of the template passed in the constructor. **/
	public String getName() {
		return _sName;
	}

	/** Returns the group associated to the template (in case template names over lap, group helps to differentiate. **/
	public String getGroup() {
		return _sGroup;
	}
	
	/** Returns the extension of the template file. **/
	public String getExtension() {
		return _sExt;
	}

	/** 
	 * Builds the java code base that is an implementation of the template file.  This is the core method of the whole program.
	 *
	 * This method creates a java class file that will be compiled and loaded later from the given "template text".
	 *
	 * @param rCtx the template engine framework
	 * @param sName the name of the template
	 * @param sInterfaceClass the name of the class that will be generated from the "template text" - does not contain package name
	 * @param rIn the reader class that contains the "template text"
	 * @return the actual java created class as a string.  This code must be written and compiled outside the template engine framework
	**/
	public static String build(	
													TContext rCtx, 
													String sName,
													String sInterfaceClass, 
													InputStreamReader rIn) throws IOException {


		// assists in parsing and building output
		boolean bText = false;
		boolean bScriptlet = false;
		boolean bScriptletAssign = false;
		boolean bScriptletDeclare = false;
		int next = -1;

		// sb is the import statements
		StringBuilder sb = new StringBuilder(4096);
		
		// sb2 contains everything else, you start appending to it in the generate method for the template text
		StringBuilder sb2 = new StringBuilder(4096);

		// import everything necessary for the class
		sb.append("package com.munskimii.tengine.gen;\n");
		sb.append("\n");
		sb.append("import com.munskimii.tengine.MetaContext;\n");
		sb.append("import com.munskimii.tengine.MetaRecord;\n");
		sb.append("import com.munskimii.tengine.TContext;\n");
		sb.append("import com.munskimii.tengine.MetaParameters;\n");
		sb.append("import com.munskimii.tengine.TemplateInterface;\n");
		sb.append("import java.io.IOException;\n");
		sb.append("import java.io.OutputStreamWriter;\n");

		MetaContext rMeta = rCtx.getMetaContext();
		for (int i = 0; i < rMeta.size(); i++) {
			sb.append("import " + rMeta.getClassName(i) + ";\n");
		}

		// create the class statements, default constructor, init method, and start the generate method
		sb2.append("\n");
		sb2.append("public class " + sInterfaceClass + " implements TemplateInterface {\n");
		sb2.append("\n");
		sb2.append("  private TContext ctx;\n");
		sb2.append("  private String name;\n");
		sb2.append("\n");
		sb2.append("  public " + sInterfaceClass + "() {}\n");
		sb2.append("\n");
		sb2.append("  public void init(TContext rCtx, String sName) {\n");
		sb2.append("    ctx = rCtx;\n");
		sb2.append("    name = sName;\n");
		sb2.append("  }\n");
		sb2.append("\n");
		sb2.append("  public void generate(MetaRecord ____my, OutputStreamWriter out)\n");
	  sb2.append("                                                  	throws IOException {\n");
		sb2.append("\n");

		// within the generate method, setup the pre-defined scriptlet variables
		for (int i = 0; i < rMeta.size(); i++) {
			sb2.append("    " +  rMeta.getClassName(i) + " " + rMeta.getVariableName(i) + " = ");
			sb2.append("(" + rMeta.getClassName(i) + ") ");
			sb2.append("____my.getObject(\"" + rMeta.getVariableName(i) + "\");\n");
		}

		// create the string object that will be the final generated java class text
		sb2.append("\n");
		sb2.append("    String ____s = \"\";\n");


		// start parsing the template text, outputting the hard-coded text as out.write 
		// commands and the scriptlet code as embedded code in this generated method
		while ((next = rIn.read()) != -1) {

			// get next char
			char ch = (char) next;
			
			// not fast, but easier to manage
			String str = "";
			str = "" + ch; 

			// new line check
			boolean bNL = false;
			if (ch == '\r')
				continue; // just skip the carriage return characters

			if (ch == '\n') 
				bNL = true;


			// we are currently in text mode
			if (!bScriptlet && !bScriptletAssign && !bScriptletDeclare) {

				if (ch == '<') {
					int ch2 = rIn.read();

					// if a second <, then that is escape, drop one of the '<' - the next check will just get skipped now
					if ((ch2 != -1) && (((char)ch2) != '<')) str += (char) ch2;
					
					if (((char)ch2) == '%') {
						int ch3 = rIn.read();

							if (((char)ch3) == '=') {
								bScriptlet = false;
								bScriptletAssign = true;
								bScriptletDeclare = false;
							}
							else if (((char)ch3) == '@') {
								bScriptlet = false;
								bScriptletAssign = false;
								bScriptletDeclare = true;
							}
							else { // ch3 char should be white space if not "=" or "@"
								bScriptlet = true;
								bScriptletAssign = false;
								bScriptletDeclare = false;
							}

							if (bText) {
								sb2.append("\";\n");
								sb2.append("    out.write(____s);\n");
								//sb2.append("    ____s = \"\";\n"); // done later now
								bText = false;
							}

							if (bScriptletAssign) {
								sb2.append("    ____s = "); // scriptlet assign code will assign this string
							}
							continue;
					}
				}

				// we got into here because we are not in a scriptlet mode, either text mode or not in text mode, 
				// so if we are here and not in text mode, we need to start being in text mode.
				if (!bText) {

					sb2.append("    ____s = \"");
					bText = true;
				}

				// just add the text, if new line, write out the text with the new line character, else, append the character
				if (bNL) {
					sb2.append(rCtx.getNLEsc());
					sb2.append("\";\n");
					sb2.append("    out.write(____s);\n");
					//sb2.append("    ____s = \"\";\n");
					bText = false;
				}
				else {
					// if user put in double-quote in the template text, we have to escape the double-quote
					if (ch == '"')
						str = "\\\"";
					
					sb2.append(str);
				}
			}


			// we are currently in scriptlet mode
			if (bScriptlet) {

				if (ch == '%') {
					int ch2 = rIn.read();
					if (ch2 != -1) str += (char) ch2;
					
					if (((char)ch2) == '>') {

						sb2.append("\n");
						bScriptlet = false;
						continue;
					}
				}

				// since we are in code section, just write whatever they give us
				if (bNL) {
					sb2.append("\n");
				}
				else {
					sb2.append(str);
				}
			}


			// we are currently in scriptlet assign mode
			if (bScriptletAssign) {

				if (ch == '%') {
					int ch2 = rIn.read();
					if (ch2 != -1) str += ch2;
					
					if (((char)ch2) == '>') {

						sb2.append(";\n");
						sb2.append("    out.write(____s);\n");
						//sb2.append("    ____s = \"\";\n");
						bScriptletAssign = false;
						continue;
					}
				}

				// we are in code section, just write whatever they give us
				if (bNL) {
					sb2.append("\n");
				}
				else {
					sb2.append(str);
				}
			}

			// we are currently in scriptlet mode
			if (bScriptletDeclare) {

				if (ch == '%') {
					int ch2 = rIn.read();
					if (ch2 != -1) str += (char) ch2;
					
					if (((char)ch2) == '>') {

						sb.append("\n");
						bScriptletDeclare = false;
						continue;
					}
				}

				// we are in code section, just write whatever they give us after last import statements
				if (bNL) {
					sb.append("\n");
				}
				else {
					sb.append(str);
				}
			}
		}

		// hit end of file, handle appropriately if in mid-state of parsing
		if (bScriptlet || bScriptletAssign || bScriptletDeclare) {
			throw new IOException("Hit end of file within scriptlet for template: " + sName);
		}

		if (bText) {
			sb2.append("\";\n");
			sb2.append("    out.write(____s);\n");
			//sb2.append("    ____s = \"\";\n");
			bText = false;
		}

		sb2.append("  }\n");
		sb2.append("}\n");

		sb.append(sb2);
		return sb.toString();
	}

	/** 
	 * Loads a previously generated/compiled java object.  
	 *
	 * @param rCtx the template engine framework
	 * @param sName the name of the template
	 * @param sGroup the group name of the template, helps differentiate templates with the same name
	 * @param sInterfaceClass the name of the class that will be generated from the "template text" - does not contain package name
	 * @param sExt the extension of the template file
	**/
	public static Template load(
													TContext rCtx, 
													String sName,
													String sGroup,
													String sInterfaceClass,
													String sExt) 
				throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		String sClass = "com.munskimii.tengine.gen." + sInterfaceClass;

		
		ClassLoader rLoader = Template.class.getClassLoader();
		Class rClass = rLoader.loadClass(sClass);
		TemplateInterface rInterface = (TemplateInterface) rClass.newInstance();

		return new Template(rCtx, sName, sGroup, sExt, rInterface);
	}
}
