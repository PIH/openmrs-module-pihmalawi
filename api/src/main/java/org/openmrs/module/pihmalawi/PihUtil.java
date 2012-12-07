package org.openmrs.module.pihmalawi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.openmrs.Concept;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.context.Context;
import org.openmrs.logic.result.Result;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.util.OpenmrsUtil;

public class PihUtil {
	
	public static String formatObject(Object o, String defaultVal) {
		if (o == null) {
			return defaultVal;
		}
		if (o instanceof Concept) {
			return toString((Concept)o);
		}
		if (o instanceof OpenmrsMetadata) {
			return ((OpenmrsMetadata)o).getName();
		}
		if (o instanceof Collection) {
			return OpenmrsUtil.join((Collection<?>)o, ", ");
		}
		if (o instanceof Result) {
			return ((Result)o).toString();
		}
		return o.toString();
	}
    
    public static String toString(Date d, Boolean estimated, String emptyMessage) {
    	if (d != null) {
    		DateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
    		return (estimated == Boolean.TRUE ? "~" : "") + df.format(d);
    	}
    	return (emptyMessage == null ? "" : translate(emptyMessage));
    }
    
    public static String toString(Date d, Boolean estimated) {
    	return toString(d, estimated, null);
    }
    
    public static String toString(Date d, String emptyMessage) {
    	return toString(d, null, emptyMessage);
    }
    
    public static String getDateString(Date d) {
    	return toString(d, null, null);
    }

    public static String translate(String s) {
    	return Context.getMessageSourceService().getMessage(s);
    }
    
    public static String getConceptKey(Concept c) { 	
    	return c.getName(Locale.ENGLISH).getName();  	
    }
    
    public static String toString(Concept c) {
    	if (c == null) {
    		return "";
    	}
    	return c.getName().getName();
    }
    
    public static String toString(Map<Object, Object> m, String sep) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<Object, Object> e : m.entrySet()) {
			if (sb.length() > 0) {
				sb.append(sep);
			}
		    sb.append(e.getKey() + " -> " + e.getValue());
		}
		return sb.toString();
    }
    
    public static String toString(String separator, Object...elements) {
    	StringBuffer sb = new StringBuffer();
    	for (Object o : elements) {
    		if (notNull(o)) {
    			if (sb.length() > 0) {
    				sb.append(separator);
    			}
    			sb.append(o.toString());
    		}
    	}
    	return sb.toString();
    }
    
    public static boolean isNull(Object o) {
    	return o == null || o.equals("") || o.equals("&nbsp;");
    }
    
    public static boolean notNull(Object o) {
    	return !isNull(o);
    }
    
    public static <T extends Object> T nvl(T o, T replacement) {
    	return (isNull(o) ? replacement : o);
    }
    
    public static String nvlStr(Object o, Object replacement) {
    	return (isNull(o) ? nvl(replacement, "").toString() : o.toString());
    }
    
    public static <T extends Object> T decode(Object testIfNull, T valueIfNull, T valueIfNotNull) {
    	return (isNull(testIfNull) ? valueIfNull : valueIfNotNull);
    }
    
    public static String decodeStr(Object testIfNull, Object valueIfNull, Object valueIfNotNull) {
    	return (isNull(testIfNull) ? nvlStr(valueIfNull, "") : nvlStr(valueIfNotNull, ""));
    }
    
    public static boolean areEqual(Object o1, Object o2) {
    	Object obj1 = nvl(o1, "");
    	Object obj2 = nvl(o2, "");
    	return obj1.equals(obj2);
    }
    
    public static boolean areEqualStr(Object o1, Object o2) {
    	String obj1 = nvlStr(o1, "");
    	String obj2 = nvlStr(o2, "");
    	return obj1.equals(obj2);
    }
    
    public static boolean equalToAny(Object o1, Object...o2) {
    	if (o1 != null && o2 != null) {
    		for (Object o : o2) {
    			if (o1.equals(o)) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    public static <T extends Object> T coalesce(T...tests) {
    	if (tests != null) {
	    	for (int i=0; i<tests.length; i++) {
	    		if (notNull(tests[i])) {
	    			return tests[i];
	    		}
	    	}
    	}
    	return null;
    }
    
    public static boolean anyNotNull(Object...tests) {
    	return coalesce(tests) != null;
    }
    
    public static int numNotNull(Object...tests) {
    	int count=0;
    	if (tests != null) {
	    	for (int i=0; i<tests.length; i++) {
	    		if (notNull(tests[i])) {
	    			count++;
	    		}
	    	}
    	}
    	return count;
    }
    
	public static boolean containsAny(Collection<String> c, String...toCheck) {
		if (c != null) {
			for (String s : toCheck) {
				if (c.contains(s)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static boolean containsAll(Collection<String> c, String...toCheck) {
		if (c != null) {
			for (String s : toCheck) {
				if (!c.contains(s)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public static String toCamelCase(String s) {
		StringBuilder sb = new StringBuilder();
		boolean nextUpper = false;
		for (char c : s.toCharArray()) {
			if (Character.isWhitespace(c)) {
				nextUpper = true;
			}
			else {
				if (Character.isLetterOrDigit(c)) {
					sb.append((nextUpper ? Character.toUpperCase(c) : Character.toLowerCase(c)));
					nextUpper = false;
				}
			}
		}
		return sb.toString();
	}
	
	public static int compareTo(Date d1, Date d2, String format) {
		String s1 = DateUtil.formatDate(d1, format);
		String s2 = DateUtil.formatDate(d2, format);
		return s1.compareTo(s2);
	}
}
