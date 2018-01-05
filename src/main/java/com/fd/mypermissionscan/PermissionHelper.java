package com.fd.mypermissionscan;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 权限处理
 * 
 * @author 符冬
 *
 */
public final class PermissionHelper {
	/**
	 * 扫描类路径下的所有权限
	 * 
	 * @return
	 */
	public static List<MPinfo> getClassPathPermission() {

		return getmps(typepm(true));
	}

	/**
	 * 扫描所有权限
	 * 
	 * @param jars
	 * @return
	 */
	public static List<MPinfo> getPermissionJson(String... jars) {
		return getMp(false, jars);
	}

	/**
	 * 扫描所有权限，并且显示错误
	 * 
	 * @param jars
	 * @return
	 */
	public static List<MPinfo> getPermissionJsonAndShowError(String... jars) {
		return getMp(true, jars);
	}

	private static List<MPinfo> getMp(boolean shower, String... jars) {
		Map<Module, List<Permissions>> mps;
		if (shower) {
			mps = scanAndShowError(jars);
		} else {
			mps = scan(jars);
		}

		return getmps(mps);
	}

	private static List<MPinfo> getmps(Map<Module, List<Permissions>> mps) {
		List<MPinfo> mpinfos = new ArrayList<MPinfo>();
		Set<Entry<Module, List<Permissions>>> enset = mps.entrySet();
		for (Entry<Module, List<Permissions>> en : enset) {
			Minfo minfo = new Minfo(en.getKey().name(), en.getKey().value());
			MPinfo mpinfo = new MPinfo(minfo);
			for (Permissions p : en.getValue()) {
				mpinfo.getPinfos().add(new Pinfo(p.value(), p.name()));
			}
			mpinfos.add(mpinfo);
		}
		return mpinfos;
	}

	/**
	 * 扫描所有权限
	 * 
	 * @param jars
	 * @return
	 */
	private static Map<Module, List<Permissions>> scan(String... jars) {
		return get(jars);
	}

	/***
	 * 扫描所有权限并且显示错误
	 * 
	 * @param showError
	 * @param jars
	 * @return
	 */
	private static Map<Module, List<Permissions>> scanAndShowError(String... jars) {
		ClassHelper.setShowError(true);
		return get(jars);
	}

	private static String getJarPath(String url) {
		String f = url.split("!/.+?")[0];
		String p = f.replaceAll("jar:file:", "");
		if (!p.startsWith(File.separator)) {
			p = p.substring(1);
		}
		return p;
	}

	private static Map<Module, List<Permissions>> get(String... jars) {
		return typepm(false, jars);
	}

	static String warRoot = "/WEB-INF/classes/";
	static String jarRoot = "/BOOT-INF/classes/";

	private static Map<Module, List<Permissions>> typepm(boolean classpath, String... jars) {
		Map<Module, List<Permissions>> mps = new HashMap<Module, List<Permissions>>();
		ClassLoader cl = ClassHelper.getDefaultClassLoader();
		URL[] urls = ((URLClassLoader) cl).getURLs();
		try {
			// 判断是否直接通过 java -jar 运行 spring boot
			boolean b = urls[0].toURI().toString().endsWith("!/")
					&& urls[0].toURI().toString().startsWith("jar:file:/");
			if (b) {
				// spring boot 项目只扫描类路径下面的CLASS
				String fstr = getJarPath(urls[0].toString());
				ClassHelper.getjarpms(mps, Paths.get(fstr), fstr.endsWith(".war") ? warRoot : jarRoot);
			} else {
				for (URL url : urls) {
					Path start = Paths.get(url.toURI());
					if ((classpath && start.toFile().isDirectory()) || !classpath) {
						Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
							@Override
							public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
								String name = file.getFileName().toString();
								if (name.endsWith(".class") && !name.contains("$")) {
									scanclass(mps, start, file);
								} else if (name.endsWith(".jar") && !classpath) {
									scanjar(mps, file, name, jars);
								}
								return FileVisitResult.CONTINUE;
							}

							@Override
							public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
								return FileVisitResult.CONTINUE;
							}

						});
					}

				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return mps;
	}

	private static void scanclass(Map<Module, List<Permissions>> mps, Path start, Path file) {
		String cpn = file.toUri().toString().split(start.toUri().toString())[1];
		String cln = cpn.substring(0, cpn.lastIndexOf(".")).replace('/', '.');
		ClassHelper.getPms(mps, cln);
	}

	private static void scanjar(Map<Module, List<Permissions>> mps, Path file, String name, String... jars) {
		if (ClassHelper.getShowError()) {
			System.err.println(name);
		}
		if (jars != null && jars.length > 0) {
			for (String jar : jars) {
				if (name.endsWith(jar)) {
					ClassHelper.getjarpms(mps, file, "/");
				}
			}
		} else {
			ClassHelper.getjarpms(mps, file, "/");
		}
	}

}
