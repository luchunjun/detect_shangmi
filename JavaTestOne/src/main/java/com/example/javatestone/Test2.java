package com.example.javatestone;

import java.util.HashMap;
import java.util.Iterator;

public class Test2 {
    public static void  main(String args[]){
        String test ="687421";
        System.out.print(test.substring(3,6));
        System.out.print(test.substring(4,6));
        HashMap<String,String> map = new HashMap<>();
        map.put("01","4");
        map.put("01","3");

        System.out.println("  ###"+map.size());

        for(String key:map.keySet()){
            System.out.println("  key ###"+map.size());
        }
    }
}
