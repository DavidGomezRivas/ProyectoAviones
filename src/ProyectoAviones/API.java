package ProyectoAviones;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONObject;

public class API {
    // Datos de conexión a la base de datos
    static String url = "jdbc:mysql://monorail.proxy.rlwy.net:15847/";
    static String usuario = "root";
    static String contraseña = "-E3B6F3b-d5gAbchEbGFfBhdd6eCCH2e";
    static String dbName = "railway";

    public API() {
        // Constructor vacío
    }
	
    // Configura la base de datos, comprobando si existe y configurando la tabla necesaria
    public static void setupDatabase() {
        // Intenta establecer una conexión con la base de datos y configurar la tabla 'aviones'
        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             Statement stmt = conn.createStatement()) {

            // Comprobar si la base de datos especificada existe
            boolean dbExists = false;
            ResultSet rsDbs = conn.getMetaData().getCatalogs();
            while (rsDbs.next()) {
                if (dbName.equals(rsDbs.getString(1))) {
                    dbExists = true;
                    break;
                }
            }

            // Conectar a la base de datos y configurar la tabla 'aviones'
            try (Connection connDb = DriverManager.getConnection(url + dbName, usuario, contraseña)) {
                setupTable(connDb);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
    // Configura la tabla 'aviones', creándola si no existe
    private static void setupTable(Connection conn) throws SQLException {
        // Comprobar si la tabla 'aviones' existe y crearla si no es así
        DatabaseMetaData dbMetaData = conn.getMetaData();
        ResultSet rs = dbMetaData.getTables(null, null, "aviones", null);
        if (!rs.next()) {
            try (Statement stmt = conn.createStatement()) {
                // SQL para crear la tabla 'aviones'
	            String sqlCreateTable = "CREATE TABLE aviones (" +
	                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
	                    "plane VARCHAR(255), " +
	                    "brand VARCHAR(100), " +
	                    "passenger_capacity INT, " +
	                    "fuel_capacity_litres INT, " +
	                    "max_takeoff_weight_kg INT, " +
	                    "max_landing_weight_kg INT, " +
	                    "empty_weight_kg INT, " +
	                    "range_km INT, " +
	                    "engine VARCHAR(255), " +
	                    "cruise_speed_kmph INT, " +
	                    "imgThumb VARCHAR(255)" +
	                    ");";
	            stmt.executeUpdate(sqlCreateTable);
            }
        }
    }
	
    // Lee datos de una API externa y los inserta en la base de datos
    public static void leerAPI() {
        // Configura y envía una solicitud HTTP GET a la API
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://airplanesdb.p.rapidapi.com/?ordering=-plane"))
                    .header("X-RapidAPI-Key", "cd1cea4a4amsh42d8f7a103c67f0p1d8bf2jsn7df974765e27")
                    .header("X-RapidAPI-Host", "airplanesdb.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            // Recibe la respuesta de la API y la convierte a un JSONArray
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONArray jsonArray = new JSONArray(response.body());
            insertData(jsonArray); // Inserta los datos recibidos en la base de datos
        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Inserta los datos de un JSONArray en la base de datos
    private static void insertData(JSONArray jsonArray) {
        // Conecta a la base de datos y prepara una sentencia SQL para insertar datos
        try (Connection conn = DriverManager.getConnection(url + dbName, usuario, contraseña)) {
            String sql = "INSERT INTO aviones (id, plane, brand, passenger_capacity, fuel_capacity_litres, max_takeoff_weight_kg, max_landing_weight_kg, empty_weight_kg, range_km, engine, cruise_speed_kmph, imgThumb) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";            
        	PreparedStatement pstmt = conn.prepareStatement(sql);

        	// Itera sobre el JSONArray, extrayendo datos de cada JSONObject y los inserta en la base de datos
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                // Configura los parámetros de la sentencia preparada con los datos del JSON
                
                pstmt.setInt(1, jsonObject.optInt("id", 0)); 
                pstmt.setString(2, jsonObject.optString("plane", null));
                pstmt.setString(3, jsonObject.optString("brand", null));
                setIntOrNull(pstmt, 4, jsonObject, "passenger_capacity");
                setIntOrNull(pstmt, 5, jsonObject, "fuel_capacity_litres");
                setIntOrNull(pstmt, 6, jsonObject, "max_takeoff_weight_kg");
                setIntOrNull(pstmt, 7, jsonObject, "max_landing_weight_kg");
                setIntOrNull(pstmt, 8, jsonObject, "empty_weight_kg");
                setIntOrNull(pstmt, 9, jsonObject, "range_km");
                pstmt.setString(10, jsonObject.optString("engine", null));
                setIntOrNull(pstmt, 11, jsonObject, "cruise_speed_kmph");
                pstmt.setString(12, jsonObject.optString("imgThumb", null));
                
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error de SQL: " + e.getMessage());
        }
    }

 // Configura un valor entero en la sentencia preparada, o NULL si el valor es inexistente en el JSON
    private static void setIntOrNull(PreparedStatement pstmt, int parameterIndex, JSONObject jsonObject, String key) throws SQLException {
        if (!jsonObject.isNull(key)) {
            pstmt.setInt(parameterIndex, jsonObject.getInt(key));
        } else {
            pstmt.setNull(parameterIndex, java.sql.Types.INTEGER);
        }
    }
}