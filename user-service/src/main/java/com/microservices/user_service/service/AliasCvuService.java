package com.microservices.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AliasCvuService {

    @Autowired
    private ResourceLoader resourceLoader;

    private List<String> cargarPalabrasDesdeArchivo() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:palabras.txt");
        return Files.lines(Paths.get(resource.getURI())) // Obtener la URI del recurso
                .collect(Collectors.toList());
    }

    public String generarCvu() {
        Random random = new Random();
        StringBuilder numeroAleatorio = new StringBuilder();
        numeroAleatorio.append(random.nextInt(9) + 1);
        for (int i = 0; i < 21; i++) {
            numeroAleatorio.append(random.nextInt(10));
        }
        return numeroAleatorio.toString();
    }

    public String generarAlias() {
        try {
            List<String> palabras = cargarPalabrasDesdeArchivo();
            if (palabras.size() < 3) {
                throw new RuntimeException("No hay suficientes palabras para generar el alias.");
            }

            Random random = new Random();
            StringBuilder alias = new StringBuilder();

            for (int i = 0; i < 3; i++) {
                alias.append(palabras.get(random.nextInt(palabras.size())));
                if (i < 2) alias.append(".");
            }

            return alias.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de palabras: " + e.getMessage(), e);
        }
    }
}
