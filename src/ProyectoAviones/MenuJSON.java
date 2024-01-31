package ProyectoAviones;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@SuppressWarnings("serial")
public class MenuJSON extends JFrame {
    // Datos de conexión a la base de datos
    static String url = "jdbc:mysql://monorail.proxy.rlwy.net:15847/";
    static String usuario = "root";
    static String contraseña = "-E3B6F3b-d5gAbchEbGFfBhdd6eCCH2e";
    static String dbName = "railway";

    public MenuJSON(JFrame parent) {
        // Configuración básica de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setTitle("Menu JSON");
        setLocationRelativeTo(null);

        // Panel principal con BorderLayout para organizar los subpaneles
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel con GridLayout para los botones "Leer" y "Guardar"
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton btnLeer = new JButton("Leer");
        JButton btnGuardar = new JButton("Guardar");

        // Agrega funcionalidad al botón Guardar para guardar los datos en un archivo JSON
        btnGuardar.addActionListener(e -> guardarEnJSON());

        // Funcionalidad para el botón Leer que abre una nueva ventana para leer archivos JSON
        btnLeer.addActionListener(e -> {
            LeerJSON leerJSON = new LeerJSON(this);
            leerJSON.setVisible(true);
            this.setVisible(false);
        });

        // Añade los botones al panel de botones
        buttonPanel.add(btnLeer);
        buttonPanel.add(btnGuardar);

        // Panel superior con un botón "Volver" para regresar a la ventana anterior
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnVolver = new JButton("Volver");
        btnVolver.addActionListener(e -> {
            parent.setVisible(true);
            this.dispose();
        });
        topPanel.add(btnVolver);

        // Agrega los subpaneles al panel principal
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Agrega el panel principal a la ventana
        add(mainPanel);
    }
    
    // Método para guardar los datos de la base de datos en un archivo JSON
    private void guardarEnJSON() {      
        JSONArray jsonArray = new JSONArray();// Array JSON para almacenar los datos

        try (Connection conn = DriverManager.getConnection(url + dbName, usuario, contraseña);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM aviones")) {// Consulta para obtener todos los datos de la tabla 'aviones'
        	
        	// Itera sobre los resultados de la consulta, creando objetos JSON para cada fila y añadiéndolos al array JSON
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                // Añade cada campo de la fila actual al objeto JSON
                obj.put("id", rs.getInt("id"));
                obj.put("plane", rs.getString("plane"));
                obj.put("brand", rs.getString("brand"));
                obj.put("passenger_capacity", rs.getInt("passenger_capacity"));
                obj.put("fuel_capacity_litres", rs.getInt("fuel_capacity_litres"));
                obj.put("max_takeoff_weight_kg", rs.getInt("max_takeoff_weight_kg"));
                obj.put("max_landing_weight_kg", rs.getInt("max_landing_weight_kg"));
                obj.put("empty_weight_kg", rs.getInt("empty_weight_kg"));
                obj.put("range_km", rs.getInt("range_km"));
                obj.put("engine", rs.getString("engine"));
                obj.put("cruise_speed_kmph", rs.getInt("cruise_speed_kmph"));
                obj.put("imgThumb", rs.getString("imgThumb"));
                jsonArray.put(obj);// Añade el objeto JSON al array JSON
            }

            // Escribe el array JSON a un archivo con una indentación de 4 espacios
            try (FileWriter file = new FileWriter("aviones.json")) {
                file.write(jsonArray.toString(4));
            }

            // Muestra un mensaje de éxito
            JOptionPane.showMessageDialog(this, "Datos guardados en aviones.json");

        } catch (Exception ex) {
            // Muestra un mensaje de error en caso de excepción
            JOptionPane.showMessageDialog(this, "Error al guardar JSON: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}