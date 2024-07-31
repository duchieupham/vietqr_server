//package com.vietqr.org.controller;
//
//import com.vietqr.org.service.MqttService;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@CrossOrigin
//@RequestMapping("/api")
//public class MqttController {
//    @Autowired
//    private MqttService mqttService;
//
//    @GetMapping("/mqtt/subscribe/{topic}")
//    public String subscribe(@PathVariable String topic){
//        try{
//            mqttService.subscribe(topic);
//            return "Subscribe to topic: " + topic;
//        } catch (MqttException e) {
//            return "Failed to subscribe: " + e.getMessage();
//        }
//    }
//
//    @PostMapping("/mqtt/publish/{topic}")
//    public String publish(@PathVariable String topic, @RequestBody String message){
//        try{
//            mqttService.publish(topic, message);
//            return "Message published to topic: " + topic;
//        }catch (MqttException e){
//            return "Failed to publish: "+ e.getMessage();
//        }
//    }
//}
