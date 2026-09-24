package com.ejercicios;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Exercici0 {
    
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // Datos bancarios (id_usuario, dinero)
        ConcurrentMap<String, Double> data = new ConcurrentHashMap<>();
    }

}

class InsertData implements Runnable {
    private final int taskId;
    private final ConcurrentMap<String, Double> data;

    public InsertData(int taskId, ConcurrentMap<String, Double> data) {
        this.taskId = taskId;
        this.data = data;
    }

    @Override
    public void run() {

        int dataSize = data.size();

        // Se generan 10 registros
        for (int i = dataSize; i < (dataSize + 10); i++) {
            String user_id = "USR" + (i + 1);
            double numero = 100 + Math.random() * (10000 - 100);
            numero = Math.round(numero * 100.0) / 100.0;
            data.put(user_id, numero);
        }

        System.out.println("Task [" + taskId + "] finalizada.");
    }
}