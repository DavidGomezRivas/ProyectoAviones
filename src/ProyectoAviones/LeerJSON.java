package ProyectoAviones;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.*;
import javax.swing.filechooser.FileNameExtensionFilter;

// La clase LeerJSON extiende JFrame para crear una interfaz gráfica
@SuppressWarnings("serial")
public class LeerJSON extends JFrame {
    // Datos de conexión a la base de datos
    static String url = "jdbc:mysql://monorail.proxy.rlwy.net:15847/";
    static String usuario = "root";
    static String contraseña = "-E3B6F3b-d5gAbchEbGFfBhdd6eCCH2e";
    static String dbName = "railway";

    public LeerJSON(JFrame parent) {
        // Configura la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setTitle("Leer JSON");
        setLocationRelativeTo(null);

        // Panel superior con botón 'Volver'
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnVolver = new JButton("Volver");
        btnVolver.addActionListener(e -> {
            parent.setVisible(true);
            this.dispose();
        });
        topPanel.add(btnVolver);

        // Panel central con botón 'Leer JSON'
        JPanel centerPanel = new JPanel();
        JButton btnLeer = new JButton("Leer JSON");
        btnLeer.addActionListener(e -> leerJSON());
        centerPanel.add(btnLeer);

        // Añade los paneles a la ventana
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void leerJSON() {
        // Selector de archivos para elegir un archivo JSON
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(file)) {
                // Lee y analiza el archivo JSON
                JSONTokener tokener = new JSONTokener(fis);
                JSONArray jsonArray = new JSONArray(tokener);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // Inserta cada objeto JSON en la base de datos
                    insertarEnBaseDeDatos(jsonObject);
                }
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Archivo no encontrado.");
            } catch (JSONException e) {
                JOptionPane.showMessageDialog(this, "Error al analizar el archivo JSON.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo.");
            }
        }
    }

    private void insertarEnBaseDeDatos(JSONObject jsonObject) {
        // Conexión a la base de datos y preparación de la consulta SQL para insertar datos
        try {
            Connection conn = DriverManager.getConnection(url + dbName, usuario, contraseña);
            String sql;
            if (!jsonObject.isNull("id")) {
                // Consulta SQL para insertar datos, incluyendo 'id' si está presente
                sql = "INSERT INTO aviones (id, plane, brand, passenger_capacity, fuel_capacity_litres, max_takeoff_weight_kg, max_landing_weight_kg, empty_weight_kg, range_km, engine, cruise_speed_kmph, imgThumb) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            } else {
            	 // Consulta SQL para insertar datos sin 'id'
                sql = "INSERT INTO aviones (plane, brand, passenger_capacity, fuel_capacity_litres, max_takeoff_weight_kg, max_landing_weight_kg, empty_weight_kg, range_km, engine, cruise_speed_kmph, imgThumb) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            }
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Configurar los valores de los parámetros de la consulta SQL
            int parameterIndex = 1;
            if (!jsonObject.isNull("id")) {
                pstmt.setInt(parameterIndex++, jsonObject.getInt("id"));
            }
            pstmt.setString(parameterIndex++, jsonObject.optString("plane", null));
            pstmt.setString(parameterIndex++, jsonObject.optString("brand", null));
            setIntOrNull(pstmt, parameterIndex++, jsonObject, "passenger_capacity");
            setIntOrNull(pstmt, parameterIndex++, jsonObject, "fuel_capacity_litres");
            setIntOrNull(pstmt, parameterIndex++, jsonObject, "max_takeoff_weight_kg");
            setIntOrNull(pstmt, parameterIndex++, jsonObject, "max_landing_weight_kg");
            setIntOrNull(pstmt, parameterIndex++, jsonObject, "empty_weight_kg");
            setIntOrNull(pstmt, parameterIndex++, jsonObject, "range_km");
            pstmt.setString(parameterIndex++, jsonObject.optString("engine", null));
            setIntOrNull(pstmt, parameterIndex++, jsonObject, "cruise_speed_kmph");
            pstmt.setString(parameterIndex, jsonObject.optString("imgThumb", null));

            pstmt.executeUpdate();
            conn.close(); // Cierra la conexión a la base de datos
            JOptionPane.showMessageDialog(this, "Datos insertados correctamente en la base de datos.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la base de datos: " + e.getMessage());
        }
    }

    private void setIntOrNull(PreparedStatement pstmt, int parameterIndex, JSONObject jsonObject, String key) throws SQLException {
    	// Configura un valor entero en la sentencia preparada o NULL si el valor es inexistente en el JSON
        if (!jsonObject.isNull(key)) {
            pstmt.setInt(parameterIndex, jsonObject.optInt(key));
        } else {
            pstmt.setNull(parameterIndex, java.sql.Types.INTEGER);
        }
    }
}