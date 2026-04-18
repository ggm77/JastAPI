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

    // 빈을 저장할 컨테이너 | container that store beans
    private final Map<Class<?>, Object> beans = new HashMap<>();

    // 인터페이스와 구현체 맵 (인터페이스는 빈 등록 안하기 때문) | map about interface and implement
    private final Map<Class<?>, Class<?>> interfaceToImplMap = new HashMap<>();

    // 순환 참조 방지용 | for check Circular Reference
    private final Set<Class<?>> isCreation = new HashSet<>();

    public Container() {}

    /**
     * 싱클톤 객체를 저장할 컨테이너를 생성하는 메서드.
     * Creates a container to store singleton objects.
     * @param scannedClasses Scanner로 스캔한 클래스들 (The classes identified by the scanner.)
     */
    public void init(final Map<String, Class<?>> scannedClasses) {

        // 자기 자신 컨테이너에 등록 | add itself to bean container
        beans.put(Container.class, this);

        // 인터페이스와 구현체 맵 생성 | create the map about interface and implement
        for (final String key : scannedClasses.keySet()) {
            final Class<?> clazz = scannedClasses.get(key);

            for (Class<?> interfaceClass : clazz.getInterfaces()) {
                if (interfaceToImplMap.containsKey(interfaceClass)) {
                    throw new RuntimeException("[ERROR] Duplicate implementation for " + interfaceClass.getName() +
                            ": " + interfaceToImplMap.get(interfaceClass).getName() +
                            ", " + clazz.getName());
                }

                // 맵에 해당 인터페이스의 구현체를 매핑 | Maps the implementation of the specified interface to the map
                interfaceToImplMap.put(interfaceClass, clazz);
            }
        }

        // 빈 등록 | register beans
        for (final String key : scannedClasses.keySet()) {
            final Class<?> clazz = scannedClasses.get(key);

            // @Component 어노테이션이 붙은 경우만 빈에 등록 | Registers a class as a bean only if it is annotated with @Component.
            if (clazz.isAnnotationPresent(Component.class)) {
                // 빈에 등록 되지 않은 클래스의 경우 빈에 등록되는 특성을 이용
                // Leverages the behavior of registering classes that are not already present in the bean container.
                getBean(clazz);
            }
        }


        // 라우터 생성 및 라우터 초기화 | create router and init the router
        getBean(Router.class).init(scannedClasses);

        // 디스패쳐 생성 | create Dispatcher
        getBean(Dispatcher.class);
    }

    /**
     * beans에 저장된 싱글톤 객체를 클래스 명을 통해 가져오는 메서드.
     * 찾는 클래스가 없는 경우 빈을 새로 등록함.
     * Retrieves the singleton instance of the specified class from the bean container.
     * If the requested class is not found, a new bean is registered.
     * @param clazz 가져올 클래스명 (The type of the class.)
     * @return 해당 클래스의 싱글톤 객체 (The class type to be retrieved.)
     * @param <T> 해당 클래스의 타입 (The singleton instance of the specified class.)
     */
    public <T> T getBean(final Class<T> clazz) {

        // if class is interface, it will find implement
        if (clazz.isInterface()) {
            final Class<?> implClass = interfaceToImplMap.get(clazz);
            if (implClass != null) {
                final Object implInstance = getBean(implClass);
                return clazz.cast(implInstance);
            }
        }

        // check the bean container first.
        if (beans.containsKey(clazz)) {
            return clazz.cast(beans.get(clazz));
        }

        // 컴포넌트 아닌 경우 (빈 등록 대상이 아닌경우) null 반환
        // if it is not @Componenet, it returns null.
        if (!clazz.isAnnotationPresent(Component.class)) {
            return null;
        }

        // Circular Reference occurred
        if (isCreation.contains(clazz)) {
            throw new RuntimeException("[ERROR] Circular reference detected: " + clazz.getName());
        }

        isCreation.add(clazz);

        // try to add bean
        try {
            addBean(clazz);
        } finally {
            isCreation.remove(clazz);
        }

        return clazz.cast(beans.get(clazz));
    }

    /**
     * 클래스를 빈에 등록하는 메서드
     * add class to bean
     * @param clazz 빈에 등록할 클래스 (The class to be registered as a bean.)
     */
    private void addBean(final Class<?> clazz) {
        try {
            // get constructor array
            final Constructor<?>[] constructors = clazz.getDeclaredConstructors();

            // if it has many constructor, it will throw exception.
            if (constructors.length > 1) {
                throw new IllegalArgumentException("[ERROR] More than one constructor found for bean registration: "+clazz.getName());
            }

            // get constructor
            final Constructor<?> constructor = constructors[0];

            // get constructor's parameters
            final Class<?>[] parameterTypes = constructor.getParameterTypes();
            final Object[] parameters = new Object[parameterTypes.length];

            // find the parameters from bean container
            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = getBean(parameterTypes[i]);
            }

            // create object
            final Object instance = constructor.newInstance(parameters);
            beans.put(clazz, instance);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException ex) {
            System.err.println("[ERROR] Cannot instantiate due to missing default constructor: " + clazz.getName());
            ex.printStackTrace();
        }
    }
}
