package com.munskimii.tengine;

/**
 * The MetaContext interface provides the method to allow any type of Java Class to become accessible as a predefined scriptlet variable.
 *
 * Author: Michael Monschke
**/

public interface MetaContext {

	/** Returns how many scriptlet variables your application meta context will support. **/
	public int size();

	/** Returns the variable name to be accessed within the scriplet at a specific index (should correlate to the class name). **/
	public String getVariableName(int iIndex);

	/** Returns the variable class name to be accessed within the scriplet at a specific index (should correlate to the variable name). **/
	public String getClassName(int iIndex);
}
