package com.ejercicios;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Exercici1 {

    public static void main(String[] args) {

        // Dinero de un jugador (id_usuario, oro)
        ConcurrentMap<String, Integer> data = new ConcurrentHashMap<>();
        data.put("USR1", 5);

        // Obtiene el oro del jugador USR1
        CompletableFuture.supplyAsync(() -> {
            return data.get("USR1");
        // Le suma 5 de oro mas
        }).thenApply(gold -> {
            return gold + 5;
        // Muestra el resultado
        }).thenAccept(resultado -> {
            System.out.println("El usuario [USR1] tiene " + resultado + " de oro.");
        // Pausa la ejecución de main hasta que acaben las tareas asincronas.
        }).join();
        
    }
}
