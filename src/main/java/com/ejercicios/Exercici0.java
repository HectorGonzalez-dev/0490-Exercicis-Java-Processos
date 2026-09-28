package com.ejercicios;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Exercici0 {
    
    public static void main(String[] args) {

        // Datos bancarios (id_usuario, dinero)
        ConcurrentMap<String, Double> data = new ConcurrentHashMap<>();

        AtomicInteger counter = new AtomicInteger(1);
        
        // Abre el executor
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Prueba 1: Se insertan nuevos usuarios simultaneamente.
        System.out.println("--- Prueba 1 ---");
        List<Future<?>> tasksTest1 = new ArrayList<>();

        tasksTest1.add(executor.submit(new InsertData(1, data, counter)));
        tasksTest1.add(executor.submit(new InsertData(2, data, counter)));
        tasksTest1.add(executor.submit(new InsertData(3, data, counter)));

        try {
            for (Future<?> task : tasksTest1) {
                task.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }

        // Prueba 2: Se accede a los datos de 3 usuarios diferentes simultaneamente.
        // Extra: También pondremos en cola para recibir 3 usuarios más.
        System.out.println("--- Prueba 2 ---");
        List<Future<Double>> tasksTest2 = new ArrayList<>();

        tasksTest2.add(executor.submit(new CheckBalance(data, "USR1")));
        tasksTest2.add(executor.submit(new CheckBalance(data, "USR2")));
        tasksTest2.add(executor.submit(new CheckBalance(data, "USR3")));
        tasksTest2.add(executor.submit(new CheckBalance(data, "USR4")));
        tasksTest2.add(executor.submit(new CheckBalance(data, "USR5")));
        tasksTest2.add(executor.submit(new CheckBalance(data, "USR99"))); // Mal a proposito

        try {
            for (int i = 0; i < (tasksTest2.size()-1); i++) {
                Double balance = tasksTest2.get(i).get();
                System.out.println("Saldo de USR" + (i + 1) + ": " + balance);
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }

        // Prueba 3: Se modifica el saldo de varios usuarios varias veces simultaneamente.
        // Los datos se mantienen consistentes.
        System.out.println("--- Prueba 3 ---");
        List<Future<?>> tasksTest3 = new ArrayList<>();

        tasksTest3.add(executor.submit(new AddMoney(1, data, "USR1", 1000)));
        tasksTest3.add(executor.submit(new AddMoney(2, data, "USR1", 1000)));
        tasksTest3.add(executor.submit(new AddMoney(3, data, "USR2", 1000)));
        tasksTest3.add(executor.submit(new AddMoney(4, data, "USR2", 1000)));
        tasksTest3.add(executor.submit(new AddMoney(5, data, "USR3", 1000)));
        tasksTest3.add(executor.submit(new AddMoney(6, data, "USR99", 1000))); // Mal a proposito

        try {
            for (Future<?> task : tasksTest3) {
                task.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }

        List<Future<Double>> tasksTest3Get = new ArrayList<>();

        tasksTest3Get.add(executor.submit(new CheckBalance(data, "USR1")));
        tasksTest3Get.add(executor.submit(new CheckBalance(data, "USR2")));
        tasksTest3Get.add(executor.submit(new CheckBalance(data, "USR3")));

        try {
            for (int i = 0; i < tasksTest3Get.size(); i++) {
                Double balance = tasksTest3Get.get(i).get();
                System.out.println("Saldo de USR" + (i + 1) + ": " + balance);
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
        }

        // Cierra el executor
        executor.shutdown();

        System.out.println(data);
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

        System.out.println("[" + taskId + "] Usuarios insertados");

    }
}

class AddMoney implements Runnable {
    private final int taskId;
    private final ConcurrentMap<String, Double> data;
    private final String user_id;
    private final double money;

    public AddMoney(int taskId, ConcurrentMap<String, Double> data, String user_id, double money) {
        this.taskId = taskId;
        this.data = data;
        this.user_id = user_id;
        this.money = money;
    }

    @Override
    public void run() {

        Double result = data.computeIfPresent(user_id, (key, value) -> value + money);

        if (result != null) {
            System.out.println("[" + taskId + "] Dinero agregado");
        } else {
            System.out.println("[" + taskId + "] No se puede realizar: " + user_id + " no existe");
        }

    }
}

class CheckBalance implements Callable<Double> {

    private final ConcurrentMap<String, Double> data;
    private final String userId;

    public CheckBalance(ConcurrentMap<String, Double> data, String userId) {
        this.data = data;
        this.userId = userId;
    }

    @Override
    public Double call() {

        Double balance = data.get(userId);
        
        if (balance != null) {
            System.out.println("[Check " + userId + "] Exito");
        } else {
            System.out.println("[Check " + userId + "] El usuario no existe");
        }
        
        return balance;
    }
}