package com.vietqr.org.util;

import com.vietqr.org.util.annotation.MqttTopicHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class MqttTopicHandlerScanner {

    @Autowired
    private ApplicationContext applicationContext;

    public Map<String, MethodHandlerPair> scanForMqttTopicHandlers(Class<?>... classes) {
        Map<String, MethodHandlerPair> topicHandlers = new HashMap<>();
        for (Class<?> clazz : classes) {
            Object bean = applicationContext.getBean(clazz);  // Get the Spring-managed bean
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(MqttTopicHandler.class)) {
                    MqttTopicHandler annotation = method.getAnnotation(MqttTopicHandler.class);
                    topicHandlers.put(annotation.topic(), new MethodHandlerPair(bean, method));
                }
            }
        }
        return topicHandlers;
    }

    public static class MethodHandlerPair {
        private final Object bean;
        private final Method method;

        public MethodHandlerPair(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }

        public Object getBean() {
            return bean;
        }

        public Method getMethod() {
            return method;
        }
    }
}
