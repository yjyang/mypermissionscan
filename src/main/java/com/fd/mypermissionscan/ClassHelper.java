package com.fd.mypermissionscan;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类型操作
 * 
 * @author 符冬
 *
 */
public final class ClassHelper {
	private static Boolean SHOW_ERROR = false;

	public static void setShowError(Boolean showError) {
		SHOW_ERROR = showError;
	}

	public static Boolean getShowError() {
		return SHOW_ERROR;
	}

	public static ClassLoader getDefaultClassLoader() {

		ClassLoader cl = null;

		try {
			cl = MethodHandles.lookup().lookupClass().getClassLoader();

		} catch (Throwable ex) {
		}

		if (cl == null) {

			cl = Thread.currentThread().getContextClassLoader();

			if (cl == null) {

				try {

					cl = ClassLoader.getSystemClassLoader();

				} catch (Throwable ex) {
				}

			}

		}

		return cl;

	}

	public static void getjarpms(Map<Module, List<Permissions>> mps, Path file, String rootpath) {
		try {
			FileSystem fs = FileSystems.newFileSystem(file, null);
			Files.walkFileTree(fs.getPath(rootpath), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String name = file.toString();
					if (name.startsWith(rootpath)) {
						name = name.replaceFirst(rootpath, "");
					}
					if (name.endsWith(".class") && !name.contains("$")) {
						String cln = name.replace('/', '.').substring(0, name.lastIndexOf("."));
						getPms(mps, cln);

					}
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void getPms(Map<Module, List<Permissions>> mps, String cln) {
		Class<?> contr = null;
		try {
			contr = Class.forName(cln);
			if (!contr.isEnum() && !contr.isAnnotation() && !contr.isInterface()) {
				if (contr.isAnnotationPresent(Controller.class) || contr.isAnnotationPresent(RestController.class)) {
					Module md = null;
					if (contr.isAnnotationPresent(Module.class)) {
						md = contr.getAnnotation(Module.class);
						if (mps.get(md) == null) {
							mps.put(md, new ArrayList<Permissions>());
						}
					}
					Method[] ms = contr.getDeclaredMethods();
					for (Method m : ms) {
						if (m.isAnnotationPresent(Permissions.class)) {
							Permissions pm = m.getAnnotation(Permissions.class);
							if (m.isAnnotationPresent(Module.class)) {
								md = m.getAnnotation(Module.class);
							}
							if (md == null) {
								throw new RuntimeException(contr.getSimpleName() + "." + m.getName() + "没有定义任何模块信息");
							}
							List<Permissions> pms = mps.get(md);
							if (pms != null) {
								boolean nex = true;
								for (Permissions per : pms) {
									if (per.value().equals(pm.value())) {
										nex = false;
										break;
									}
								}
								if (nex) {
									pms.add(pm);
								}
							} else {
								pms = new ArrayList<Permissions>();
								pms.add(pm);
								mps.put(md, pms);
							}
						}
					}
				}
			}
		} catch (Throwable e) {
			if (SHOW_ERROR) {
				e.printStackTrace();
				System.err.println(e.getMessage());
			}
		}
	}

}
