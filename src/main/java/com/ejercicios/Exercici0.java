package com.ejercicios;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Exercici0 {
    
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // Datos bancarios (id_usuario, dinero)
        ConcurrentMap<String, Double> data = new ConcurrentHashMap<>();
        
        // Abre el executor
        ExecutorService executor = Executors.newFixedThreadPool(3);

        

        // Cierra el executor
        executor.shutdown();
    }

}

class InsertData implements Runnable {
    private final int taskId;
    private final ConcurrentMap<String, Double> data;
    private final AtomicInteger nextId;

    public InsertData(int taskId, ConcurrentMap<String, Double> data, AtomicInteger nextId) {
        this.taskId = taskId;
        this.data = data;
        this.nextId = nextId;
    }

    @Override
    public void run() {

        // Se generan 10 registros
        for (int i = 0; i < 10; i++) {
            // Obtiene la siguiente id disponible y la incrementa
            int id = nextId.getAndIncrement();
            String user_id = "USR" + id;
            // Pone una cantidad de dinero aleatorio en la cuenta
            double numero = 100 + Math.random() * (10000 - 100);
            numero = Math.round(numero * 100.0) / 100.0;
            // Se insertan los datos en el hashmap
            data.put(user_id, numero);
        }

        System.out.println("Task [" + taskId + "] finalizada");

    }
}