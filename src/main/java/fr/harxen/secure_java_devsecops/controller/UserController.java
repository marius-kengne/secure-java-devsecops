package fr.harxen.secure_java_devsecops.controller;

import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@RestController
@RequestMapping("/api")
public class UserController {

    // ❌ CRITIQUE 1 — SQL Injection
    // SonarCloud : "Vulnerability - SQL injection"
    @GetMapping("/user")
    public String getUser(@RequestParam String id) throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb");
        Statement stmt = conn.createStatement();

        // Input utilisateur directement dans la requête SQL → SQL Injection
        String query = "SELECT * FROM users WHERE id = " + id;
        ResultSet rs = stmt.executeQuery(query);

        return rs.next() ? rs.getString("name") : "not found";
    }

    // ❌ CRITIQUE 2 — Hardcoded credentials
    // SonarCloud : "Vulnerability - Hard-coded credentials"
    @GetMapping("/admin")
    public String admin() {
        String password = "admin1234";
        String secret   = "mySecretKey123";
        return "admin access";
    }

    // ❌ CRITIQUE 3 — XXE (XML External Entity)
    // SonarCloud : "Vulnerability - XML parsers should not be vulnerable to XXE"
    @PostMapping("/xml")
    public String parseXml(@RequestBody String xmlInput) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory factory =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        // factory non sécurisée → XXE possible
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document doc = builder.parse(
                new org.xml.sax.InputSource(new java.io.StringReader(xmlInput))
        );
        return doc.getDocumentElement().getNodeName();
    }

    // ❌ HIGH — Null Pointer potentiel
    @GetMapping("/name")
    public String getName(@RequestParam String input) {
        String result = null;
        return "Length: " + result.length();
    }
}
