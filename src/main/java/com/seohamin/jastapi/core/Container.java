package com.seohamin.jastapi.core;

import com.seohamin.jastapi.web.mapping.Router;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 스캔한 클래스들을 관리하는 싱글톤 컨테이너
 *
 *
 * 빈 등록 전략
 * 1. 스캐너로 컨트롤러 클래스들 찾기, HttpMethod 어노테이션 위치 찾기
 * 2. 재귀적으로 해당 컨트롤러 클래스가 필요로하는 클래스들 찾기
 * 3. 역순으로 빈 등록 & 방문 기록 남겨서 무한 루프 방지
 * 4. 미리 찾은 HttpMethod 위치를 라우터로 넘기기
 *
 */
public class Container {

    // 컨테이너
    private static final Map<Class<?>, Object> beans = new HashMap<>();

    // 순환 참조 방지용
    private static final Set<Class<?>> isCreation = new HashSet<>();

    // 인스턴스화 방지
    public Container() {}

    /**
     * 싱클톤 객체를 저장할 컨테이너를 생성하는 메서드.
     * @param scannedClasses Scanner로 스캔한 클래스들
     */
    public static void init(final Map<String, Class<?>> scannedClasses) {

        // 라우터 생성 및 저장
        final Router router = new Router();
        beans.put(Router.class, router);

        // 빈 등록
        for (final String key : scannedClasses.keySet()) {
            final Class<?> clazz = scannedClasses.get(key);

            // 클래스가 어노테이션이나 인터페이스, enum이면 제외
            if (clazz.isAnnotation() || clazz.isInterface() || clazz.isEnum()) {
                continue;
            }

            // 빈에 등록 되지 않은 클래스의 경우 빈에 등록되는 특성을 이용
            getBean(clazz);
        }

        // 빈 등록이 끝난 후 라우터 초기화
        router.init(scannedClasses);
    }

    /**
     * beans에 저장된 싱글톤 객체를 클래스 명을 통해 가져오는 메서드.
     * 찾는 클래스가 없는 경우 빈을 새로 등록함.
     * @param clazz 가져올 클래스명
     * @return 해당 클래스의 싱글톤 객체
     * @param <T> 해당 클래스의 타입
     */
    public static <T> T getBean(final Class<T> clazz) {
        if (beans.containsKey(clazz)) {
            return clazz.cast(beans.get(clazz));
        }

        if (isCreation.contains(clazz)) {
            throw new RuntimeException("[ERROR] 순환 참조 발생: " + clazz.getName());
        }

        isCreation.add(clazz);

        try {
            addBean(clazz);
        } finally {
            isCreation.remove(clazz);
        }

        return clazz.cast(beans.get(clazz));
    }

    /**
     * 클래스를 빈에 등록하는 메서드
     * @param clazz 빈에 등록할 클래스
     */
    private static void addBean(final Class<?> clazz) {
        try {
            final Constructor<?> constructor = clazz.getDeclaredConstructors()[0];

            final Class<?>[] parameterTypes = constructor.getParameterTypes();
            final Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = getBean(parameterTypes[i]);
            }

            final Object instance = constructor.newInstance(parameters);
            beans.put(clazz, instance);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException ex) {
            System.err.println("[WARN] 기본 생성자가 없어 인스턴스 생성 불가: " + clazz.getName());
            ex.printStackTrace();
        }
    }
}
