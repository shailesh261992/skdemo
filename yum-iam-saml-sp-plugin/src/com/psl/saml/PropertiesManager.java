package com.psl.saml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesManager {
	private static Properties properties = null;
	private static PropertiesManager propertiesManager = null;

	// private static final String className =
	// PropertiesManager.class.getName();
	private static final String PROPERTIES_FILE = "SPDefault.properties";
	private static final String CLASSPATH_PROPERTY = "java.class.path";
	private static Logger logger = LoggerFactory
			.getLogger(PropertiesManager.class);

	private void load() throws FileNotFoundException, IOException {
		// String method ="load";
		properties = new Properties();
		String dir = findDataDir();
		// String dir = "D:\\WorkSpace\\SCE_EAI\\Data";
		try{
		properties.load(new FileInputStream(new File(dir, PROPERTIES_FILE)));
		}catch(Exception e){
			
		}
		
		if (properties.size()==0) {
			
			try {
				InputStream inStream = Thread.currentThread()
						.getContextClassLoader()
						.getResourceAsStream(PROPERTIES_FILE);
				properties.load(inStream);
			} catch (Exception e) {
				
			}
		}
		if (properties.size() == 0) {
			try {
				properties.load(PropertiesManager.class.getClassLoader()
						.getResourceAsStream(PROPERTIES_FILE));
			} catch (Exception e) {
				
			}

		}
		if (properties.size() == 0) {
			try {
				properties = readProperties("/apps/iam/itim/data/Yum/"
						+ PROPERTIES_FILE, true);
			} catch (Exception e) {
				
			}
		}
		if (properties.size() == 0) {
			try {
				properties = readProperties("/apps/iam/itim/data/"
						+ PROPERTIES_FILE, true);
			} catch (Exception e) {
				
			}
		}
		if (properties.size() == 0) {
			try {
				properties = readProperties("/opt/IBM/itim/data/Yum/"
						+ PROPERTIES_FILE, true);
			} catch (Exception e) {
				
			}
		}
		if (properties.size() == 0) {
			try {
				properties = readProperties(
						"/opt/IBM/itim/data/" + PROPERTIES_FILE, true);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}

		Enumeration<Object> keys = (Enumeration<Object>) properties.keys();
		StringBuffer sb = new StringBuffer();
		while (keys.hasMoreElements()) {
			String str = (String) keys.nextElement();
			String val = properties.getProperty(str);
			sb.append(str).append(" = ").append(val)
					.append(new String(Character.LINE_SEPARATOR + ""));

		}
		String propertyList = sb.toString();
		logger.info("Properties loaded are" + propertyList);

	}

	private static Properties readProperties(String propertyfilename,
			boolean path) {
		InputStream is = null;
		Properties props = new Properties();
		System.out.println("Loading properties from " + propertyfilename);
		try {
			// figure out the name of the props file
			if (!path) {
				// this approach will read from the top of any CLASSPATH entry
				is = PropertiesManager.class
						.getResourceAsStream(propertyfilename);
				if (is != null) {
					props.load(is);
					is.close();
				}
			} else {
				props.load(new FileInputStream(new File(propertyfilename)
						.getAbsolutePath()));
			}

		} catch (IOException ex) {
			//ex.printStackTrace();
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (props.size() > 0) {
			System.out.println("Loaded properties from " + propertyfilename);
		}
		return props;
	}

	private String findDataDir() {
		// String method ="findDataDir";
		// try the class path
		try {
			String classPath = System.getProperty(CLASSPATH_PROPERTY);
			classPath = "." + File.pathSeparator + classPath;
			System.out.println("classPath>" + classPath);
			StringTokenizer st = new StringTokenizer(classPath,
					File.pathSeparator);
			while (st.hasMoreTokens()) {
				String dirName = st.nextToken();
				File file = new File(dirName, PROPERTIES_FILE);
				if (file.isFile()) {
					System.out.println("dirName>" + dirName);
					return dirName;
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return null;
	}

	public static PropertiesManager getInstance() throws Exception {
		// String method ="getInstance";
		try {
			if (propertiesManager == null) {
				propertiesManager = new PropertiesManager();
				propertiesManager.load();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		return propertiesManager;
	}

	public HashMap<String, String> getAllProperties() {
		HashMap<String, String> allProps = new HashMap<String, String>();
		Enumeration<Object> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = getString(keys.nextElement());
			String val = getString(properties.get(key));
			allProps.put(key.trim(), val.trim());
		}
		return allProps;
	}

	private String getString(Object obj) {
		if (obj != null) {
			return (String) obj;
		}
		return "";
	}

	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}
}
