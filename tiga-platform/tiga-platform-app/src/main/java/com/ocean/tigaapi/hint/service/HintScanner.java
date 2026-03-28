package com.ocean.tigaapi.hint.service;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.ocean.tigaapi.hint.entity.HintClassEntity;
import com.ocean.tigaapi.hint.entity.HintMethodEntity;

/**
 * Tiga 元数据服务类
 * 负责扫描类路径下的所有类名、解析本地源码 Javadoc 注释、并自动化识别和注入 Groovy 风格的扩展方法。
 * 该服务为前端编辑器提供类信息、方法列表、文档说明以及代码补全所需的元数据。
 */
@Component
public class HintScanner {

	/**
	 * 允许扫描并返回给前端的 JDK 包前缀白名单，避免加载过多无关的系统类
	 */
	private static final List<String> ALLOWED_PACKAGE_PREFIXES = Arrays.asList(
			"java.util.", 
			"java.lang.",
			"java.time.");

	/** 存储所有扫描到的全路径类名 */
	private List<String> allClassNames = new ArrayList<>();
	
	/** 缓存自动识别到的扩展定义类（例如包名含 .extensions. 的类） */
	private List<Class<?>> extensionClassCache = new ArrayList<>();
	
	/** 类全名 -> 类文档描述（从源码解析得到） */
	private Map<String, String> classDocs = new ConcurrentHashMap<>();
	/** 类全名 -> 注解列表 */
	private Map<String, List<String>> classAnnos = new ConcurrentHashMap<>();
	/** 方法唯一标识 (类名#方法名+参数个数) -> 方法文档描述 */
	private Map<String, String> methodDocs = new ConcurrentHashMap<>();
	/** 方法唯一标识 (类名#方法名+参数个数) -> 注解列表 */
	private Map<String, List<String>> methodAnnos = new ConcurrentHashMap<>();

	/** 缓存压缩后的类名字符串，格式为 "pkg:cls1,cls2"，用于减少前端传输数据量 */
	private String compressedTextCache = "";

	/**
	 * 服务初始化方法
	 * 在 Solon 容器启动后自动执行，完成类扫描、文档解析和扩展类识别。
	 */
	@Init
	public void init() {
		try {
			Set<String> classSet = new HashSet<>();
			// 1. 扫描 JDK 模块（针对 Java 9+ 的 jrt 文件系统）
			scanJdkModules(classSet);
			// 2. 扫描当前应用的 Classpath（Jar 包及本地目录）
			scanFullClasspath(classSet);
			
			this.allClassNames = new ArrayList<>(classSet);
			Collections.sort(this.allClassNames);

			// 3. 扫描本地源码目录，通过 JavaParser 提取 Javadoc 和 注解信息
			scanProjectSource("./src/main/java");

			// 4. 自动化识别扩展类（必须在 scanProjectSource 之后，确保 allClassNames 已填充）
			identifyExtensionClasses();

			// 5. 构建并缓存用于前端快速补全的压缩文本
			this.compressedTextCache = buildCompressedText();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 自动化识别扩展类：凡是包名包含 ".extensions." 的类都视为扩展定义类。
	 * 这些类中的静态方法将被视为目标类型的“成员方法”注入。
	 */
	private void identifyExtensionClasses() {
		this.extensionClassCache.clear();
		for (String className : allClassNames) {
			if (className.contains(".extensions.")) {
				try {
					this.extensionClassCache.add(Class.forName(className));
				} catch (Throwable ignored) {}
			}
		}
	}

	/**
	 * 判断指定的类是否在允许的 JDK 包白名单内
	 */
	private boolean isPackageAllowed(String className) {
		return ALLOWED_PACKAGE_PREFIXES.stream().anyMatch(className::startsWith);
	}

	/**
	 * 扫描 JDK 运行时模块中的类
	 */
	private void scanJdkModules(Set<String> classSet) throws Exception {
		FileSystem jrtFs = FileSystems.getFileSystem(URI.create("jrt:/"));
		Path modules = jrtFs.getPath("/modules");
		Files.walk(modules).filter(p -> p.toString().endsWith(".class")).forEach(p -> {
			String path = p.toString();
			// 处理模块路径，提取类名
			int idx = path.indexOf("/java.base/");
			if (idx < 0) idx = path.indexOf("/modules/");
			if (idx < 0) return;
			
			String className = path.substring(path.lastIndexOf("/modules/") + 9).replaceAll("^[^/]+/", "")
					.replace("/", ".").replace(".class", "");
					
			// 排除匿名内部类，并过滤白名单包
			if (!className.contains("$") && isPackageAllowed(className)) {
				classSet.add(className);
			}
		});
	}

	/**
	 * 扫描完整类路径（包括 Jar 包和编译后的目录）
	 */
	private void scanFullClasspath(Set<String> classSet) throws Exception {
		String classPath = System.getProperty("java.class.path");
		String[] paths = classPath.split(File.pathSeparator);
		for (String path : paths) {
			File file = new File(path);
			if (!file.exists()) continue;
			if (file.isDirectory()) {
				scanDirectory(file, "", classSet);
			} else if (file.getName().endsWith(".jar")) {
				scanJarFile(file, classSet);
			}
		}
	}

	/**
	 * 扫描指定的 Jar 文件
	 */
	private void scanJarFile(File file, Set<String> classSet) {
		try (JarFile jar = new JarFile(file)) {
			jar.stream().forEach(entry -> {
				String name = entry.getName();
				if (name.endsWith(".class") && !name.contains("$")) {
					String className = name.replace("/", ".").replace(".class", "");
					if (isPackageAllowed(className)) {
						classSet.add(className);
					}
				}
			});
		} catch (Exception ignored) {}
	}

	/**
	 * 递归扫描文件目录
	 */
	private void scanDirectory(File dir, String pkg, Set<String> classSet) {
		File[] files = dir.listFiles();
		if (files == null) return;
		for (File f : files) {
			if (f.isDirectory()) {
				scanDirectory(f, pkg + f.getName() + ".", classSet);
			} else if (f.getName().endsWith(".class") && !f.getName().contains("$")) {
				String className = pkg + f.getName().replace(".class", "");
				// 业务类（com.ocean）及白名单 JDK 类均收集
				if (className.startsWith("com.ocean") || isPackageAllowed(className)) {
					classSet.add(className);
				}
			}
		}
	}

	/**
	 * 获取全量类名的压缩格式文本
	 */
	public String getCompressedText() {
		return compressedTextCache;
	}

	/**
	 * 构建压缩类名缓存，通过 "包名:类1,类2" 格式大幅减少文本行数
	 */
	private String buildCompressedText() {
		StringBuilder sb = new StringBuilder();
		String currentPkg = "";
		for (String fullName : allClassNames) {
			int idx = fullName.lastIndexOf(".");
			String pkg = idx > 0 ? fullName.substring(0, idx) : "";
			String cls = idx > 0 ? fullName.substring(idx + 1) : fullName;
			if (!pkg.equals(currentPkg)) {
				if (sb.length() > 0) sb.append("\n");
				sb.append(pkg).append(":");
				currentPkg = pkg;
			} else {
				sb.append(",");
			}
			sb.append(cls);
		}
		return sb.toString();
	}

	/**
	 * 根据类名获取脚本可用的类详情，包含方法、文档和自动注入的扩展方法
	 * @param className 全路径类名
	 * @return ScriptClass 模型
	 */
	public HintClassEntity getScriptClass(String className) throws ClassNotFoundException {
	    Class<?> clazz = Class.forName(className);
	    HintClassEntity sc = new HintClassEntity();
	    sc.setClassName(className);
	    sc.setName(clazz.getSimpleName());

	    // 设置从源码解析到的文档和注解
	    sc.setDoc(classDocs.getOrDefault(className, ""));
	    sc.setAnnotations(classAnnos.getOrDefault(className, new ArrayList<>()));

	    // 1. 获取该类自身的原始公共方法
	    for (Method method : clazz.getMethods()) {
	        if (!Modifier.isPublic(method.getModifiers())) continue;
	        addMethodToScriptClass(sc, className, method, false);
	    }

	    // 2. 自动化注入针对该类的 Groovy 风格扩展方法
	    injectExtensionMethods(sc, className);

	    return sc;
	}
	
	/**
	 * 将 Java 反射得到的 Method 转换为 ScriptMethod 并添加到模型中
	 * @param sc 目标 ScriptClass
	 * @param targetClassName 正在被查询的类全名
	 * @param method 反射得到的方法对象
	 * @param isExtension 是否为扩展方法（静态扩展）
	 */
	private void addMethodToScriptClass(HintClassEntity sc, String targetClassName, Method method, boolean isExtension) {
	    HintMethodEntity sm = new HintMethodEntity();
	    sm.setName(method.getName());
	    sm.setReturnType(method.getReturnType().getSimpleName());
	    
	    Parameter[] parameters = method.getParameters();
	    // 如果是扩展方法，第一个参数是 self，应从提示列表中剔除
	    int startIndex = isExtension ? 1 : 0;
	    
	    for (int i = startIndex; i < parameters.length; i++) {
	        sm.getParameters().add(parameters[i].getType().getSimpleName() + " " + parameters[i].getName());
	    }
	    
	    // 文档 Key 生成策略：扩展方法的文档在定义它的扩展类中；原始方法则在目标类中
	    String key;
	    if (isExtension) {
	        key = method.getDeclaringClass().getName() + "#" + method.getName() + parameters.length;
	    } else {
	        key = targetClassName + "#" + method.getName() + parameters.length;
	    }
	    
	    sm.setComment(methodDocs.getOrDefault(key, ""));
	    sm.setAnnotations(methodAnnos.getOrDefault(key, new ArrayList<>()));
	    sc.getMethods().add(sm);
	}

	/**
	 * 核心自动化注入逻辑：基于反射参数类型匹配扩展方法。
	 * 模拟 Groovy 机制，将 Ext 类中的 static method(Target self, ...) 映射为 Target 类的实例方法。
	 */
	private void injectExtensionMethods(HintClassEntity sc, String targetClassName) {
	    try {
	        Class<?> targetClazz = Class.forName(targetClassName);
	        
	        // 遍历所有已缓存的扩展类
	        for (Class<?> extClazz : extensionClassCache) {
	            for (Method m : extClazz.getMethods()) {
	                // 扩展方法必须是静态的
	                if (!Modifier.isStatic(m.getModifiers())) continue;
	                
	                Parameter[] params = m.getParameters();
	                if (params.length > 0) {
	                    Class<?> firstParamType = params[0].getType();
	                    
	                    // A. 实例扩展识别 (如 public static void toFixed(Double self))
	                    // 如果第一个参数类型是当前类或其父类（isAssignableFrom 判断）
	                    boolean isInstanceExt = firstParamType.isAssignableFrom(targetClazz);
	                    
	                    // B. 静态扩展识别 (如 public static void help(Class selfType))
	                    // 如果第一个参数是 Class 类型，且作用于当前目标类
	                    boolean isStaticExt = (firstParamType == Class.class && sc.getClassName().equals(targetClassName));

	                    if (isInstanceExt || isStaticExt) {
	                        addMethodToScriptClass(sc, targetClassName, m, true);
	                    }
	                }
	            }
	        }
	    } catch (Exception ignored) {}
	}

	/**
	 * 扫描本地项目源码目录，提取 Javadoc 注释和注解。
	 * 依赖 JavaParser 库进行语法树分析。
	 */
	private void scanProjectSource(String path) throws Exception {
		Path sourcePath = Paths.get(path);
		if (!Files.exists(sourcePath)) return;
		Files.walk(sourcePath).filter(p -> p.toString().endsWith(".java")).forEach(p -> {
			try {
				CompilationUnit cu = StaticJavaParser.parse(p);
				// 获取包名
				String packageName = cu.getPackageDeclaration().map(pd -> pd.getNameAsString() + ".").orElse("");

				// 解析类/接口声明
				cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cls -> {
					String fullClassName = packageName + cls.getNameAsString();
					// 获取类 Javadoc
					String cDoc = cls.getJavadocComment().map(this::formatJavadoc)
							.orElse(cls.getComment().map(c -> c.getContent()).orElse(""));
					classDocs.put(fullClassName, cDoc);

					// 获取类注解列表
					List<String> cAnnos = cls.getAnnotations().stream().map(a -> a.toString().trim())
							.collect(Collectors.toList());
					classAnnos.put(fullClassName, cAnnos);

					// 解析方法
					cls.getMethods().forEach(m -> {
						// 唯一 key: 类全名#方法名+参数个数
						String mKey = fullClassName + "#" + m.getNameAsString() + m.getParameters().size();
						String mDoc = m.getJavadocComment().map(this::formatJavadoc)
								.orElse(m.getComment().map(c -> c.getContent()).orElse(""));
						methodDocs.put(mKey, mDoc);

						List<String> mAnnos = m.getAnnotations().stream().map(a -> a.toString().trim())
								.collect(Collectors.toList());
						methodAnnos.put(mKey, mAnnos);
					});
				});
			} catch (Exception ignored) {}
		});
	}

	/**
	 * 将原始的 Javadoc 注释格式化为适合前端显示的 Markdown 风格文本
	 */
	private String formatJavadoc(com.github.javaparser.ast.comments.JavadocComment javadoc) {
		String content = javadoc.getContent();
		return content.lines().map(line -> line.trim().replaceAll("^\\*\\s?", ""))
				.map(line -> {
					// 将常用标签转换为 Markdown 加粗
					if (line.startsWith("@param")) return "\n\n**" + line.replaceFirst("@param", "@param**");
					if (line.startsWith("@return")) return "\n\n**" + line.replaceFirst("@return", "@return**");
					// 转换 HTML 换行符
					if (line.contains("<br>")) return line.replace("<br>", "\n\n");
					return line;
				}).collect(Collectors.joining("\n")).trim();
	}
}