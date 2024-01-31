package ProyectoAviones;

import javax.swing.*;
import java.awt.FlowLayout;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@SuppressWarnings("serial")
public class LeerXML extends JFrame {
    // Inicialización de componentes de la interfaz de usuario y configuración de la ventana
    JButton btnCargar, btnVolver;
    JFileChooser fileChooser;
	static String url = "jdbc:mysql://monorail.proxy.rlwy.net:15847/";
    static String usuario = "root";
    static String contraseña = "-E3B6F3b-d5gAbchEbGFfBhdd6eCCH2e";
    static String dbName = "railway";

    public LeerXML(JFrame parent) {
        // Configuración básica de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setTitle("Leer XML");
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // Botón para volver a la ventana anterior
        btnVolver = new JButton("Volver");
        btnVolver.addActionListener(e -> {
            parent.setVisible(true);
            this.dispose();
        });

        // Botón para iniciar la carga del archivo XML
        btnCargar = new JButton("Cargar");
        btnCargar.addActionListener(e -> cargarXML());

        // Selector de archivos para elegir el archivo XML
        fileChooser = new JFileChooser();

        // Añade los botones a la ventana
        add(btnVolver);
        add(btnCargar);
    }

    private void cargarXML() {
    	// Muestra el diálogo del selector de archivos y procesa el archivo seleccionado
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
            	// Prepara el procesamiento del archivo XML
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(selectedFile);
                doc.getDocumentElement().normalize();
                
                // Establece conexión con la base de datos
                try (Connection conn = DriverManager.getConnection(url + dbName, usuario, contraseña)) {
                    NodeList nodeList = doc.getElementsByTagName("avion"); // Obtiene nodos 'avion' del documento XML
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        Node node = nodeList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) node;

                            // Preparación de la sentencia SQL para inserción de datos
                            String sql = "INSERT INTO aviones (id, plane, brand, passenger_capacity, fuel_capacity_litres, max_takeoff_weight_kg, max_landing_weight_kg, empty_weight_kg, range_km, engine, cruise_speed_kmph, imgThumb) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                // Asigna los valores a la consulta SQL desde el elemento XML
                                // Para los campos que pueden no existir, verifica si están presentes y asigna null si no lo están
                                pstmt.setInt(1, Integer.parseInt(getElementTextContentOrDefault(element, "id", "0")));
                                pstmt.setString(2, getElementTextContentOrDefault(element, "plane", null));
                                pstmt.setString(3, getElementTextContentOrDefault(element, "brand", null));
                                pstmt.setInt(4, Integer.parseInt(getElementTextContentOrDefault(element, "passenger_capacity", "0")));
                                pstmt.setInt(5, Integer.parseInt(getElementTextContentOrDefault(element, "fuel_capacity_litres", "0")));
                                pstmt.setInt(6, Integer.parseInt(getElementTextContentOrDefault(element, "max_takeoff_weight_kg", "0")));
                                pstmt.setInt(7, Integer.parseInt(getElementTextContentOrDefault(element, "max_landing_weight_kg", "0")));
                                pstmt.setInt(8, Integer.parseInt(getElementTextContentOrDefault(element, "empty_weight_kg", "0")));
                                pstmt.setInt(9, Integer.parseInt(getElementTextContentOrDefault(element, "range_km", "0")));
                                pstmt.setString(10, getElementTextContentOrDefault(element, "engine", null));
                                pstmt.setInt(11, Integer.parseInt(getElementTextContentOrDefault(element, "cruise_speed_kmph", "0")));
                                pstmt.setString(12, getElementTextContentOrDefault(element, "imgThumb", null));

                                pstmt.executeUpdate();
                            }
                        }
                    }
                    // Muestra mensaje de éxito
                    JOptionPane.showMessageDialog(this, "Datos cargados con éxito");
                }
            } catch (Exception ex) {
                // Maneja y muestra los errores
                JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
    // Obtiene el contenido de texto de un elemento XML o devuelve un valor predeterminado si el elemento no existe
    private String getElementTextContentOrDefault(Element element, String tagName, String defaultValue) {
        NodeList nl = element.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0 && nl.item(0).getTextContent() != null) {
            return nl.item(0).getTextContent();
        } else {
            return defaultValue;
        }
    }
}