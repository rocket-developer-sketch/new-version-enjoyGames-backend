package com.easygame.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public ResponseEntity<String> home() {
        String msg = """
                <html>
                <head><title>API Docs</title></head>
                <body>
                  <h2>Welcome to the API Server</h2>
                  <p>See <a href="/easygame-api-ui.html">Swagger API Documentation</a></p>
                </body>
                </html>
                """;
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(msg);
    }
}
