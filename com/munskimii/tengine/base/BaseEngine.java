package com.munskimii.tengine.base;

/**
 * The BaseEngine is the driver of the application, the entry point into the application if NOT used within a larger application.
 * The BaseEngine provides an implementation reference of the TemplateEngine framework. Please copy and modify for your purposes.
 *
 * Author: Michael Monschke
**/

import java.io.*;
import java.util.*;
import javax.tools.*;

import com.munskimii.tengine.*;

public class BaseEngine {

	/** The template context. **/
	private TContext _rCtx;

	/** The template parameters. **/
	private MetaParameters _rMetaParm;

	/** Anonymous implementation of the MetaContext for the BaseEngine. **/
	private MetaContext _rMetaCtx = new MetaContext() {

				public int size() { return 1; }
				public String getVariableName(int iIndex) { return "data"; }
				public String getClassName(int iIndex) { return "com.munskimii.tengine.base.Record"; }
			};

	/**
	 * Default constructor. 
	**/
	public BaseEngine() {

		try {

			_rMetaParm = buildMetaParameters();
			_rCtx = new TContext(_rMetaCtx, _rMetaParm);

		} catch (Throwable x) { x.printStackTrace(); System.exit(1); }
	}

	/**
	 * Returns the template context of the base engine. 
	**/
	public TContext getContext() {

		return _rCtx;
	}

	/**
	 * Returns the meta paramaters of the base engine.
	**/
	public MetaParameters getMetaParm() {

		return _rMetaParm;
	}

	/**
	 * Returns the meta context of the base engine.
	**/
	public MetaContext getMetaContext() {

		return _rMetaCtx;
	}

	/**
	 * Creates the meta parameters and returns them.  
	 * Within the base engine, this is done by pulling all properties found in all *.prop files in "config/global" directory.
	**/
	public static MetaParameters buildMetaParameters() throws IOException {

		MetaParameters rMetaParm = new MetaParameters();
		File[] arProp = new File("config\\global\\").listFiles();
		for (int i = 0; i < arProp.length; i++) {
			if (arProp[i].getName().endsWith(".prop")) {
				Properties p = new Properties();
				p.load(new FileReader(arProp[i]));
				rMetaParm.put(p);
			}
		}
		return rMetaParm;
	}

	/**
	 * Creates the code related to each individual given template.  The templates for the base engine are any files in "config/templates/".
	 *
	 * The iVersion param  is necessary if you want to build/compile/load a single template multiple times in a JVM session.
	**/
	public static void buildTemplates(TContext rCtx, int iVersion) throws IOException {

		iterateBuildTemplates(rCtx, new File("config\\templates\\"), "", true, iVersion);
	}

	/** Recursive method to ensure all templates in the "config/templates/" directory is processed. Directory name is part of final output. **/
	private static void iterateBuildTemplates(
																	TContext rCtx, 
																	File rFile, 
																	String sGroup, 
																	boolean bFirst,
																	int iVersion) 
																																		throws IOException {

		if (rFile.isFile()) {
			buildTemplate(rCtx, rFile, sGroup, iVersion);
		}
		else if (rFile.isDirectory()) {

			File[] arFiles = rFile.listFiles();
			if (!bFirst) {
				if (sGroup.length() > 0)
					sGroup += "_";
				sGroup += rFile.getName().toLowerCase().replace(" ", "_");
			}
			
			for (int i = 0; i < arFiles.length; i++) {

				iterateBuildTemplates(rCtx, arFiles[i], sGroup, false, iVersion);
			}
		}
	}

	/** 
	 * BaseEngine implementation of creating the template related code base java files. 
	 *
	 * The method uses the core capabilities of the template engine light-weight framework to do the core work.
	**/
	private static Template buildTemplate(
																TContext rCtx, 
																File rFile, 
																String sGroup, 
																int iVersion) throws IOException {

		// split file name first part (before dot) from its final extension
		String[] asVal = rFile.getName().split("\\.");
		String sName = asVal[0].toLowerCase().replace(" ", "_");
		String sExt = "";
		if (asVal.length == 1)
			sExt = asVal[1];

		System.out.println("Building template: " + sName);

		// get code for each template using the light-weight framework
		FileReader in = new FileReader(rFile);
		String s = Template.build(rCtx, sName, "miibase_" + sGroup + "_" + sName + iVersion, in);
		in.close();

		// write code for each template into a location we will compile from later in the process
		File rOut = new File("com\\munskimii\\tengine\\gen\\miibase_" + sGroup + "_" + sName + iVersion + ".java");
		FileWriter out = new FileWriter(rOut);
		out.write(s);
		out.flush();
		out.close();

		System.out.println("Compiling template: " + rOut.getName());

		// compile the java class we just created
		JavaCompiler rCmplr = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = rCmplr.getStandardFileManager(
				null,
				null,
				null);

		fileManager.setLocation(
									StandardLocation.CLASS_OUTPUT, 
									Arrays.asList(new File("classes")));
		rCmplr.getTask(null, fileManager, null, null, null,
								fileManager.getJavaFileObjectsFromFiles(Arrays.asList(rOut))).call();
		fileManager.close();

		return null;
	}

	/**
	 * After the templates have been built (code created and compiled), the classes are to be loaded into the JVM.
	**/
	public static ArrayList<Template> loadTemplates(TContext rCtx, int iVersion) 
				throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		ArrayList<Template> rTemps = new ArrayList<Template>();
		iterateLoadTemplates(rCtx, new File("config\\templates\\"), rTemps, "", true, iVersion);
		return rTemps;
	}

	/** Recursive method to ensure all templates in the "config/templates/" directory is loaded. **/
	private static void iterateLoadTemplates(
												TContext rCtx, 
												File rFile, 
												ArrayList<Template> rTemps, 
												String sGroup, 
												boolean bFirst, 
												int iVersion) 
				throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		if (rFile.isFile()) {
			rTemps.add(loadTemplate(rCtx, rFile, sGroup, iVersion));
		}
		else if (rFile.isDirectory()) {

			File[] arFiles = rFile.listFiles();
			if (!bFirst) {
				if (sGroup.length() > 0)
					sGroup += "_";
				sGroup += rFile.getName().toLowerCase().replace(" ", "_");
			}
			
			for (int i = 0; i < arFiles.length; i++) {


				iterateLoadTemplates(rCtx, arFiles[i], rTemps, sGroup, false, iVersion);
			}
		}
	}

	/** 
	 * BaseEngine implementation of loaded the template related code base class files. 
	 *
	 * The method uses the core capabilities of the template engine light-weight framework to do the core work.
	**/
	private static Template loadTemplate(TContext rCtx, File rFile, String sGroup, int iVersion) 
				throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		String[] asVal = rFile.getName().split("\\.");
		String sName = asVal[0].toLowerCase().replace(" ", "_");
		String sExt = "";
		if (asVal.length == 2)
			sExt = asVal[1];

		System.out.println("Loading template: " + sName);

		// load the code base for each template
		return Template.load(rCtx, sName, sGroup, "miibase_" + sGroup + "_" + sName + iVersion, sExt);
	}

	/**
	 * Entry point into the BaseEngine, the start of the driver application to generate docs from given templates and data file (contains data records).
	 *
	 * The data file is expected to be in the "config/data" directory.  The file should be pipe delimited, with named columns in the header row.
	**/
	public static void main(String[] asArgs) {

		try {


			if (asArgs.length == 0) {

				System.out.println("Arg needed, either two modes:  compile  |  run");
				return;
			}

			if (asArgs[0].equals("compile")) {

				BaseEngine rEng = new BaseEngine();
				rEng.buildTemplates(rEng.getContext(), 0);
			}

			if (asArgs[0].equals("run")) {

				if (asArgs.length == 1) {
					System.out.println("You must provide data file in run mode");
					return;
				}

				// load data file records into a base Record object (data values can be gathered from column header names), implements MetaRecord
				BufferedReader rDataIn = new BufferedReader(
											new FileReader(new File("config\\data\\" + asArgs[1])));
				String[] asHeader = rDataIn.readLine().split("\\|");
				ArrayList<Record> rData = new ArrayList<Record>();

				String sLine = null;
				while ((sLine = rDataIn.readLine()) != null) {
					String[] asRow = sLine.split("\\|");
					rData.add(new Record(asHeader, asRow));
				}

				// load templates that were previously generated
				BaseEngine rEng = new BaseEngine();
				ArrayList<Template> rTemps = rEng.loadTemplates(rEng.getContext(), 0);

				// generate documents for each template, start by iterating through each template
				for (int i = 0; i < rTemps.size(); i++) {

					// get the code base for each template
					Template rTemp = rTemps.get(i);

					// generate and write the generated data document per data record
					int iCounter = 0;
					for (Record rRecord : rData) {

						System.out.println("Generating document");

						// write code for each template
						File rOut = new File("config\\documents\\" + (++iCounter) + rTemp.getName() + ".dat");
						FileWriter out = new FileWriter(rOut);
						rTemp.generate(rRecord, out); // generates document for given MetaRecord to output stream
						out.flush();
						out.close();
					}
				}
			}


		} catch (Throwable x) { x.printStackTrace(); }
	}
}

