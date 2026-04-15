package com.seohamin.jastapi.core;

import com.seohamin.jastapi.annotation.core.Component;
import com.seohamin.jastapi.web.Dispatcher;
import com.seohamin.jastapi.web.mapping.Router;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 스캔한 클래스들을 관리하는 싱글톤 컨테이너
 */
@Component
public class Container {

    // 컨테이너
    private final Map<Class<?>, Object> beans = new HashMap<>();

    // 인터페이스와 구현체 맵 (인터페이스는 빈 등록 안하기 때문)
    private final Map<Class<?>, Class<?>> interfaceToImplMap = new HashMap<>();

    // 순환 참조 방지용
    private final Set<Class<?>> isCreation = new HashSet<>();

    public Container() {}

    /**
     * 싱클톤 객체를 저장할 컨테이너를 생성하는 메서드.
     * @param scannedClasses Scanner로 스캔한 클래스들
     */
    public void init(final Map<String, Class<?>> scannedClasses) {

        // 자기 자신 컨테이너에 등록
        beans.put(Container.class, this);

        // 인터페이스와 구현체 맵 생성
        for (final String key : scannedClasses.keySet()) {
            final Class<?> clazz = scannedClasses.get(key);

            for (Class<?> interfaceClass : clazz.getInterfaces()) {
                if (interfaceToImplMap.containsKey(interfaceClass)) {
                    throw new RuntimeException("[ERROR] " + interfaceClass.getName() +
                            "의 구현체가 중복됩니다: " + interfaceToImplMap.get(interfaceClass).getName() +
                            ", " + clazz.getName());
                }

                // 맵에 해당 인터페이스의 구현체를 매핑
                interfaceToImplMap.put(interfaceClass, clazz);
            }
        }

        // 빈 등록
        for (final String key : scannedClasses.keySet()) {
            final Class<?> clazz = scannedClasses.get(key);

            // @Component 어노테이션이 붙은 경우만 빈에 등록
            if (clazz.isAnnotationPresent(Component.class)) {
                // 빈에 등록 되지 않은 클래스의 경우 빈에 등록되는 특성을 이용
                getBean(clazz);
            }
        }


        // 라우터 생성 및 라우터 초기화
        getBean(Router.class).init(scannedClasses);

        // 디스패쳐 생성
        getBean(Dispatcher.class);
    }

    /**
     * beans에 저장된 싱글톤 객체를 클래스 명을 통해 가져오는 메서드.
     * 찾는 클래스가 없는 경우 빈을 새로 등록함.
     * @param clazz 가져올 클래스명
     * @return 해당 클래스의 싱글톤 객체
     * @param <T> 해당 클래스의 타입
     */
    public <T> T getBean(final Class<T> clazz) {

        if (clazz.isInterface()) {
            final Class<?> implClass = interfaceToImplMap.get(clazz);
            if (implClass != null) {
                final Object implInstance = getBean(implClass);
                return clazz.cast(implInstance);
            }
        }

        if (beans.containsKey(clazz)) {
            return clazz.cast(beans.get(clazz));
        }

        // 컴포넌트 아닌 경우 (빈 등록 대상이 아닌경우) null 반환
        if (!clazz.isAnnotationPresent(Component.class)) {
            return null;
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
    private void addBean(final Class<?> clazz) {
        try {
            final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length > 1) {
                throw new IllegalArgumentException("[ERROR] 컨테이너에 등록할 클래스에 생성자가 2개 이상 존재합니다. 클래스: "+clazz.getName());
            }

            final Constructor<?> constructor = constructors[0];

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
