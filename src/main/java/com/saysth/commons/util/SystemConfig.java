package com.saysth.commons.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * parse system config file, and retrieve property value by given key
 * @author
 *
 */
public class SystemConfig {
	
	private static Properties props = new Properties();
	private static String configPath;
	private static String classPath;
	private static String appRoot;
	private static String appAbsolutePath;
	private static boolean isWindows;
	private static String resPath;
	
	private SystemConfig() {
	}
	
	static {
		initialize();
	}
	
	public static boolean isLinux() {
		return !isWindows;
	}
	
	public static void initialize() {
		String osType = System.getProperty("os.name");
		isWindows = osType == null || osType.toUpperCase().indexOf("WINDOWS") > -1;
		if (isWindows) {
			classPath = SystemConfig.class.getResource("/").getPath();
			classPath = classPath.replace("file:", "");
			int pos = classPath.indexOf("/classes/");
			if (pos > -1)
				classPath = classPath.substring(1, pos + 9);
		} else {
			classPath = SystemConfig.class.getResource("").getPath();
			classPath = classPath.replace("file:", "");
			int pos = classPath.indexOf("/classes/");
			if (pos == -1) {
				pos = classPath.indexOf("/lib/");
				if (pos > -1)
					classPath = classPath.substring(0, pos + 5);
			} else {
				classPath = classPath.substring(0, pos + 9);
			}
		}
		classPath = classPath.replaceAll("/lib/", "/classes/");
		
		configPath = classPath + "config/";
		appAbsolutePath = classPath.replaceAll("/WEB-INF/classes/", "");
		//appRoot = appAbsolutePath.substring(appAbsolutePath.lastIndexOf("/"));
		try {
			classPath = java.net.URLDecoder.decode(classPath, "UTF-8");
		} catch(Exception e) {
			//do nothing
		}
		loadProperties(configPath + "SystemConfig.properties");
		loadProperties(configPath + "application-prd.properties");
		loadProperties(configPath + "application-dev.properties");
		
		appRoot = props.getProperty("app.root", "/");
		resPath = props.getProperty("static.resource.root", "/");
		//System.out.println("classPath==========="+classPath);
		//System.out.println("configPath=========="+configPath);
		//System.out.println("appAbsolutePath====="+appAbsolutePath);
		//System.out.println("appRoot============="+appRoot);
	}
	
	public static void reloadProperties() {
		props.clear();
		loadProperties(configPath + "SystemConfig.properties");
		loadProperties(configPath + "application-prd.properties");
		loadProperties(configPath + "application-dev.properties");
	}
	
	public static Properties getProperties() {
		return props;
	}
	
	public static String getResPath() {
		return resPath;
	}
	
	public static String getProperty(String key, String defaultProperty) {
		return props.getProperty(key, defaultProperty);
	}
	
	public static String getProperty(String key, Object ... args) {
		String pattern = props.getProperty(key);
		if (pattern == null)
			return "No such property with key: " + key;
		return MessageFormat.format(pattern, args);
	}
	
	public static String getConfigPath() {
		return configPath;
	}
	
	public static String getClassPath() {
		return classPath;
	}
	
	public static String getAppRoot() {
		return appRoot;
	}
	
	public static String getAppAbsolutePath() {
		return appAbsolutePath;
	}
	
	private static void loadProperties(String propertyFile) {
		InputStream ins = null;
		try {
			ins = new FileInputStream(propertyFile);
			props.load(ins);
			ins.close();
		} catch (Exception e) {
			System.out.println("Can't found system config file: " + configPath + ", please check." + e);
		} finally {
			try {
				if (ins != null)
					ins.close();
			} catch(IOException e) {
				//do nothing
			}
		}
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println(classPath);
		System.out.println(SystemConfig.getProperty("app.root"));
		System.out.println(java.net.URLDecoder.decode("D:/Tomcat%206.0/webapps/dynform/WEB-INF/classes", "UTF-8"));
		System.out.println(SystemConfig.getProperty("not.exist.property", "default value"));
		System.out.println(SystemConfig.getProperty("test.property", "Lin Yufa", new java.util.Date(), "rainy"));
	}
}
