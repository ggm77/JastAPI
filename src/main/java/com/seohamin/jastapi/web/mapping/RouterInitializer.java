package com.seohamin.jastapi.web.mapping;

import com.seohamin.jastapi.annotation.Delete;
import com.seohamin.jastapi.annotation.Get;
import com.seohamin.jastapi.annotation.Patch;
import com.seohamin.jastapi.annotation.Post;
import com.seohamin.jastapi.core.Container;
import com.seohamin.jastapi.web.http.HttpMethod;
import com.seohamin.jastapi.web.mapping.dto.RouteDto;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

public class RouterInitializer {

    public static void init(
            final Router router,
            final Container container,
            final Set<Class<?>> scannedClasses
    ) {
        for (final Class<?> clazz : scannedClasses) {
            final Object instance = container.getBean(clazz);

            for (final Method method : clazz.getDeclaredMethods()) {
                for (final Annotation annotation : method.getAnnotations()) {

                    final RouteDto routeDto = new RouteDto(instance, method);

                    if (annotation.annotationType().equals(Get.class)) {
                        final Get getAnnotation = (Get) annotation;
                        router.addRoute(HttpMethod.GET, getAnnotation.value(), routeDto);
                    }
                    else if (annotation.annotationType().equals(Post.class)) {
                        final Post postAnnotation = (Post) annotation;
                        router.addRoute(HttpMethod.POST, postAnnotation.value(), routeDto);
                    }
                    else if (annotation.annotationType().equals(Patch.class)) {
                        final Patch patchAnnotation = (Patch) annotation;
                        router.addRoute(HttpMethod.PATCH, patchAnnotation.value(), routeDto);
                    }
                    else if (annotation.annotationType().equals(Delete.class)) {
                        final Delete deleteAnnotation = (Delete) annotation;
                        router.addRoute(HttpMethod.DELETE, deleteAnnotation.value(), routeDto);
                    }
                }
            }
        }
    }
}
