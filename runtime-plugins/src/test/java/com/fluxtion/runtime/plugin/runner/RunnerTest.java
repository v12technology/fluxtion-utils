/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.runner;

import com.fluxtion.learning.utils.monitoring.cooling.generated.RackCoolingSystem;
import spark.Request;
import spark.Response;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class RunnerTest {
    
    
    public static void main(String[] args) {
        SEPRunner runner = new SEPRunner(new RackCoolingSystem(), "rack", 8080);
        runner.get("hello", RunnerTest::hello);
        runner.getJson("json", RunnerTest::myData);
    }
    
    public static Object hello(Request request, Response response) throws Exception{
        return "hello";
    }
    
    public static Object myData(Request request, Response response) throws Exception{
        return new MyData("greg", 100);
    }
    
    public static class MyData{
        
        public String name;
        public int age;

        public MyData(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public MyData() {
        }
    
    }
}
