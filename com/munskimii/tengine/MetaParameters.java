package com.munskimii.tengine;

/**
 * The MetaParameters class provides a method to provide parameter type information downstream to the templates.
 *
 * The MetaParameters can be accessed within template via ctx.getMetaParameters()
 *
 * Author: Michael Monschke
**/

import java.util.*;

public class MetaParameters {

	private Properties _rProp = new Properties();

	public MetaParameters() {}

	public void put(Properties p) {
		_rProp.putAll(p);
	}

	public void put(String sName, String sValue) {
		_rProp.put(sName, sValue);
	}

	/** Returns a parameter value for a given name, it will not return null. **/
	public String getParameter(String sName) {

		String s = _rProp.getProperty(sName);
		if (s == null)
			return "";
		return s;
	}
}

