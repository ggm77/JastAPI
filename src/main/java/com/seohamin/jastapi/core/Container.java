package com.seohamin.jastapi.core;

import com.seohamin.jastapi.web.mapping.Router;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 스캔한 클래스들을 관리하는 싱글톤 컨테이너
 */
public class Container {

    // 컨테이너
    private static final Map<Class<?>, Object> beans = new HashMap<>();

    // 인스턴스화 방지
    public Container() {}

    /**
     * 싱클톤 객체를 저장할 컨테이너를 생성하는 메서드.
     * @param scannedClasses Scanner로 스캔한 클래스들
     */
    public static void init(final Set<Class<?>> scannedClasses) {

        // 라우터 생성 및 저장
        final Router router = new Router();
        beans.put(Router.class, router);

        for (final Class<?> clazz : scannedClasses) {
            // 클래스가 어노테이션이나 인터페이스, enum이면 제외
            if (clazz.isAnnotation() || clazz.isInterface() || clazz.isEnum()) {
                continue;
            }

            try {
                final Object instance = clazz.getDeclaredConstructor().newInstance();
                beans.put(clazz, instance);
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException ex) {
                System.err.println("[WARN] 기본 생성자가 없어 인스턴스 생성 불가: " + clazz.getName());
                ex.printStackTrace();
            } catch (NoSuchMethodException ex) {
                System.err.println("[ERROR] 객체 생성중 오류 발생: " + clazz.getName());
                ex.printStackTrace();
            }
        }

        // 빈 등록이 끝난 후 라우터 초기화
        router.init(scannedClasses);
    }

    /**
     * beans에 저장된 싱글톤 객체를 클래스 명을 통해 가져오는 메서드.
     * @param clazz 가져올 클래스명
     * @return 해당 클래스의 싱글톤 객체
     * @param <T> 해당 클래스의 타입
     */
    public static <T> T getBean(final Class<T> clazz) {
        final Object bean = beans.get(clazz);

        if (bean == null) {
            return null;
        }

        return clazz.cast(bean);
    }

    // FOR DEV
    public static Map<Class<?>, Object> getAllBeans() {
        return beans;
    }
}
