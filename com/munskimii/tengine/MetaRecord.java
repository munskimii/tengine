package com.munskimii.tengine;

/**
 * The MetaObject interface provides the method to the template engine to capture the data 
 * object(s) that will be utilized in the generation of a document.  This class contains the
 * actual class instance object containing the data to be applied to the template.  
 *
 * The MetaRecord object will contain all object instances as defined within the MetaContext.
 *
 * Author: Michael Monschke
**/

import java.util.*;

public interface MetaRecord {

	public Object getObject(String sName);
}


