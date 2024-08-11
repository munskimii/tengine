package com.munskimii.tengine.base;

/**
 * The Record is a generic data object that is used within the base engine template reference.
 * The class implements MetaRecords and holds named data values for a data record.
 *
 * The reference implementation mainly has to do with the MetaRecord implementation.  You would 
 * probably want to have your own java objects and pre-defined scriptlet variables specific to your 
 * own template engine.
 *
 * Author: Michael Monschke
**/

import java.io.*;
import java.util.*;
import com.munskimii.tengine.*;

import com.munskimii.tengine.*;

public class Record implements MetaRecord {

	private Properties _rProp = new Properties();

	public Record() {}

	public Record(String[] asHeader, String[] asRow) {

		for (int i = 0; i < asHeader.length; i++) {
			put(asHeader[i], asRow[i]);
		}
	}


	public void put(String sName, String sValue) {
		_rProp.put(sName, sValue);
	}

	public String get(String sName) {

		String s = _rProp.getProperty(sName);
		if (s == null)
			return "";
		return s;
	}

	public Object getObject(String sName) {
		if (sName.equals("data"))
			return this;
		return null;
	}
}
