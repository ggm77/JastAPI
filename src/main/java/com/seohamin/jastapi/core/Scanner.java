package com.seohamin.jastapi.core;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 프로젝트의 클래스를 통해 패키지 정보를 받아서 하위에 존재하는 모든 class를 찾는 클래스.
 * A utility class that retrieves package information from a given project class
 * and scans for all classes within that package and its subpackages.
 */
public class Scanner {

    // 인스턴스화 방지 | Prevents instantiation
    private Scanner() {}

    /**
     * 프로젝트의 클래스를 통해 패키지를 받아서 하위에 존재하는 모든 class를 찾는 메소드.
     * Jar파일 또한 지원함.
     * Scans for all classes within the package of the provided source class.
     * Supports both file directories and JAR files.
     * @param sourceClass 하위 클래스들을 스캔할 패키지에 들어있는 클래스 (The class used to determine the base package for scanning.)
     * @return 찾은 클래스의 집합 (A map containing the found classes, with their fully qualified names as keys.)
     */
    public static Map<String, Class<?>> scan(final Class<?> sourceClass) {

        final String packageName = sourceClass.getPackageName();

        final Map<String, Class<?>> classes = new HashMap<>();

        final String path = packageName.replace(".", "/");
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                final URL resource = resources.nextElement();

                if (resource.getProtocol().equals("jar")) {
                    scanJar(resource, path, classes);
                }
                else {
                    final File file = new File(resource.getFile());
                    scanDirectory(file, packageName, classes);
                }
            }
        } catch (final IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        return classes;
    }

    /**
     * Jar 파일에서 class 파일을 찾는 메서드.
     * Scans for class files within a JAR file.
     * @param resource Jar 파일의 주소 (The URL of the JAR file.)
     * @param path 패키지를 경로로 변환한 문자열 (The package name converted into a directory path string.)
     * @param classes 찾은 클래스를 저장할 집합 (The map where discovered classes will be stored.)
     * @throws IOException Jar 파일 접근에 문제가 생겼을 때 발생 (If an error occurs while accessing the JAR file.)
     * @throws ClassNotFoundException Class 객체로 변환에 실패할 경우 (If a class cannot be loaded by its name.)
     */
    private static void scanJar(
            final URL resource,
            final String path,
            final Map<String, Class<?>> classes
    ) throws IOException, ClassNotFoundException {
        final JarURLConnection conn = (JarURLConnection) resource.openConnection();

        try (final JarFile jarFile = conn.getJarFile()) {
            final Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String name = entry.getName();

                if (name.startsWith(path) && name.endsWith(".class")) {
                    final Class<?> clazz = Class.forName(name.replace("/", ".").replace(".class", ""));
                    classes.put(
                            clazz.getName(),
                            clazz
                    );
                }
            }
        }
    }

    /**
     * Jar 파일이 아닌 경우 스캔 하는 메서드.
     * Scans for class files in a directory (for non-JAR environments).
     * @param dir 스캔 시작할 폴더 (The directory to start scanning from.)
     * @param packageName 원본 패키지명 (The base package name corresponding to the directory.)
     * @param classes 찾은 클래스를 저장할 집합 (The map where discovered classes will be stored.)
     * @throws ClassNotFoundException Class 객체로 변환 실패한 경우 발생 (If a class cannot be loaded by its name.)
     */
    private static void scanDirectory(
            final File dir,
            final String packageName,
            final Map<String, Class<?>> classes
    ) throws ClassNotFoundException{

        final File[] files = dir.listFiles();

        if (files == null) {
            return;
        }

        for (final File f : files) {
            if (f.isDirectory()) {
                scanDirectory(f, packageName + "." + f.getName(), classes);
            }
            else if (f.getName().endsWith(".class")) {
                final Class<?> clazz = Class.forName(packageName+"."+f.getName().replace(".class", ""));
                classes.put(
                        clazz.getName(),
                        clazz
                );
            }
        }
    }
}
