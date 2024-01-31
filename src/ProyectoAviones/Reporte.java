package ProyectoAviones;

import java.sql.Connection;
import java.sql.DriverManager;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;


public class Reporte {
    // Datos de conexión a la base de datos
    static String url = "jdbc:mysql://monorail.proxy.rlwy.net:15847/";
    static String usuario = "root";
    static String contraseña = "-E3B6F3b-d5gAbchEbGFfBhdd6eCCH2e";
    static String dbName = "railway";

    // Método estático para generar un reporte
    public static void generarReporte() {
        try {
            // Ruta al archivo JRXML que contiene la definición del reporte
            String reportPath = "ProyectoAviones.jrxml";

            // Compilación del reporte JRXML a JasperReport
            JasperReport jasperReport = JasperCompileManager.compileReport(reportPath);

            // Establecimiento de la conexión con la base de datos
            Connection conn = DriverManager.getConnection(url + dbName, usuario, contraseña);

            // Llenado del reporte con datos, 'null' representa los parámetros del reporte si no se utilizan
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, conn);

            // Mostrar el reporte (la línea está comentada, pero se puede descomentar para visualizar el reporte)
            //JasperViewer.viewReport(jasperPrint, false);
 
            // Exportar el reporte a un archivo PDF
            JasperExportManager.exportReportToPdfFile(jasperPrint, "aviones_report.pdf");

        } catch (Exception e) {
            // Manejo de excepciones, imprime la pila de llamadas de la excepción en caso de error
            e.printStackTrace();
        }
    }
}
