package com.wcs.sys.ejbtimer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Project: wcsoa</p>
 * <p>Description: </p>
 * <p>Copyright © 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:liushengbin@wcs-global.com">Liu Shengbin</a>
 */
public class ClassLoaderUtil {

	private static Logger logger = LoggerFactory.getLogger(ClassLoaderUtil.class);
	
	private ClassLoaderUtil(){
		
	}

	public synchronized static void reloadClassesFromPath() {
		isLoad = false;
		loadClassesFromPath();
	}

	public synchronized static void loadClassesFromPath() {
		if (!isLoad) {
			try {
				String rootPath = getRootClassPath();
				String[] paths = rootPath.split(";");
				for (String path : paths) {
					File file = new File(path);
					if (file.isFile() && path.endsWith(".jar")) {
						listClassesInZip(file);
					} else if (file.isDirectory()) {
						listClassesInDirectory(path + File.separatorChar, file);
					}
				}
			} catch (Exception e) {
				logger.error("\n",e);
			}
		}
	}

	/**
	 * 获取项目的classpath根路径
	 * 
	 * @param fileName
	 *            文件名
	 * @return URL 文件在应用中的相对路径
	 * @throws URISyntaxException 
	 * @throws Exception
	 *             获取路径过程中的任何异常
	 */
	public synchronized static String getRootClassPath() {
		String url = "";
		try {
			URI uri = Thread.currentThread().getContextClassLoader().getResource("/META-INF/persistence.xml").toURI();
			url = new File(uri).getPath().replaceAll("\\\\", "/");
			url = url.substring(0, url.lastIndexOf("/META-INF/"));
		} catch (Exception e) {
			logger.error("\n",e);
		}

		return url;
	}

	private synchronized static void listClassesInDirectory(String rootPath, File file) {
		File[] subFiles = file.listFiles();
		for (File subFile : subFiles) {
			if (subFile.canRead()) {
				if (subFile.isFile()) {
					String path = subFile.getPath();
					if (path.endsWith(".class")) {
						try {
							String className = getClassName(path.substring(rootPath.length()));
							CLASSES.add(Class.forName(className));
						} catch (Exception e) {
							logger.error("\n",e);
						}
					} else if (path.endsWith(".jar")) {
						listClassesInZip(subFile);
					}
				} else if (subFile.isDirectory()) {
					listClassesInDirectory(rootPath, subFile);
				}
			}
		}
	}

	private synchronized static void listClassesInZip(File jarFile) {
		ZipInputStream in = null;
		try {
			in = new ZipInputStream(new FileInputStream(jarFile));
			ZipEntry ze = null;
			while ((ze = in.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					continue;
				} else {
					try {
						String name = ze.getName();
						if (!name.endsWith(".class")) {
							continue;
						}
						String className = getClassName(name);
						CLASSES.add(Class.forName(className));
					} catch (Exception e) {
						logger.error("\n",e);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("\n",e);
				}
			}
		}
	}

	private static String getClassName(String path) {
		return path.replace('/', '.').replace('\\', '.').replaceAll(".class", "");
	}

	/**
	 * 
	 * <p>Description: 获取某个类的所有子类</p>
	 * @param clazz
	 * @return 子类列表
	 */
	public static List<Class<?>> getSubClasses(Class<?> clazz) {
		List<Class<?>> subClasses = SUB_CLASSES_MAP.get(clazz);
		if (subClasses == null) {
			subClasses = new ArrayList<Class<?>>(10);
			for (Class<?> tmpClass : CLASSES) {
				if (clazz.isAssignableFrom(tmpClass) && !tmpClass.isAssignableFrom(clazz)) {
					subClasses.add(tmpClass);
				}
			}
			SUB_CLASSES_MAP.put(clazz, subClasses);
		}
		return Collections.unmodifiableList(subClasses);
	}

	/**
	 * 
	 * <p>Description:获取含有某个注解的所有类 </p>
	 * @param annotationClazz
	 * @return
	 */
	public static List<Class<?>> findAnnotationClasses(Class<?> annotationClazz) {
		List<Class<?>> resultClasses = new ArrayList<Class<?>>();
		for (Class<?> tmpClass : CLASSES) {
			if (AnnotationUtils.isAnnotationDeclaredLocally(annotationClazz, tmpClass)) {
				resultClasses.add(tmpClass);
			}
		}
		return resultClasses;

	}

	private static List<Class<?>> CLASSES = new ArrayList<Class<?>>(200);

	private static final Map<Class<?>, List<Class<?>>> SUB_CLASSES_MAP = new HashMap<Class<?>, List<Class<?>>>();

	private static boolean isLoad;

	static {
		loadClassesFromPath();
	}
}
