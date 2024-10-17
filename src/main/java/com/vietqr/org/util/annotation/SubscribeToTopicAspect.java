//package com.vietqr.org.util.annotation;
//
//import com.vietqr.org.util.MQTTHandler;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.Map;
//
//@Aspect
//@Component
//public class SubscribeToTopicAspect {
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    private final Map<String, Method> topicMethodMap = new HashMap<>();
//    private final Map<String, Object> topicInstanceMap = new HashMap<>();
//
//    @Before("@annotation(subscribeToTopic)")
//    public void subscribeToTopic(JoinPoint joinPoint, SubscribeTopic subscribeToTopic) throws Exception {
//        String topic = subscribeToTopic.value();
//        //System.out.println("Subscribing to topic: " + topic); // Debug logging
//        Object target = joinPoint.getTarget();
//        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
//        topicMethodMap.put(topic, method);
//        topicInstanceMap.put(topic, target);
//    }
//
//    public void handleMessage(String topic, String message) throws Exception {
//        Method method = topicMethodMap.get(topic);
//        Object instance = topicInstanceMap.get(topic);
//        if (method != null && instance != null) {
//            method.invoke(instance, message);
//        }
//    }
//}
