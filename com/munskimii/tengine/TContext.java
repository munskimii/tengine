package com.munskimii.tengine;

/**
 * The TContext class provides all contextual information to the framework and to the templates.
 *
 * The templates can access the TContext via the pre-defined template variable:  ctx
 *
 * The primary methods that should be utilized by the template are:getMetaParameters(), getNL(), getNLEsc()
 *   By default, the NL is short-hand for \n.  The NLEsc is short-hand for \\n (useful for java code generation)
 *
 * Author: Michael Monschke
**/

import java.util.*;

public class TContext {

	private MetaContext _rMetaCtx;
	private MetaParameters _rMetaParms;
	private String _sNL;
	private String _sNLEsc;

	/** Constructor. **/
	public TContext(MetaContext rMetaCtx, MetaParameters rMetaParms) {
		this(rMetaCtx, rMetaParms, "\n", "\\n");
	}

	/** Constructor. You can pass in your own newline and newline escape options. **/
	public TContext(MetaContext rMetaCtx, MetaParameters rMetaParms, String sNL, String sNLEsc) {

		_rMetaCtx = rMetaCtx;
		_rMetaParms = rMetaParms;
		_sNL = sNL;
		_sNLEsc = sNLEsc;
	}

	/** Returns the meta context.  The definition of all pre-defined scriptlet variables. **/
	public MetaContext getMetaContext() {

		return _rMetaCtx;
	}

	/** Returns the meta parameters.  Values passed from the driver program regardless of the individual MetaRecord. **/
	public MetaParameters getMetaParameters() {

		return _rMetaParms;
	}

	/** A suggest new line character to utilize by the template engine. **/
	public String getNL() {

		return _sNL;
	}

	/** A suggest new line character to utilize by the template engine used within code generation, like "\n". **/
	public String getNLEsc() {

		return _sNLEsc;
	}
}
