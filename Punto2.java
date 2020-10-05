import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import javafx.util.Pair;

public class Punto2 extends JFrame {
  
  private static final long serialVersionUID = 1L;

  public Punto2(String titulo) {
    super(titulo); // titulo de la ventana
    this.setSize(700, 400);// tamaño de la ventana
    iniciarComponentes();
  }

  private void iniciarComponentes() {
    JPanel panel = new JPanel();
    panel.setLayout(null); // desactivando el diseño
    this.getContentPane().add(panel);

    JLabel title = new JLabel("Ingreso de datos de las ventas de un vendedor en una ciudad");
    title.setBounds(150, 10, 400, 30);
    panel.add(title);
    JLabel title1 = new JLabel("Codigo Vendedor");
    title1.setBounds(500, 50, 100, 130);
    panel.add(title1);
    JLabel title2 = new JLabel("Seleccione Una Ciudad");
    title2.setBounds(350, 100, 150, 30);
    panel.add(title2);
    Connection conn;
    Statement sentencia;
    ResultSet resultado;
    JComboBox<String> ciudades = new JComboBox<>();
    try { // Se carga el driver JDBC-ODBC
      Class.forName("oracle.jdbc.driver.OracleDriver");
    } catch (Exception err) {
      JOptionPane.showMessageDialog(null, "No se pudo cargar el driver JDBC");
      return;
    }

    try {
      conexion connection = new conexion();
      conn = DriverManager.getConnection(connection.getConn(), connection.getUser(), connection.getPass());
      sentencia = conn.createStatement();
    } catch (SQLException err) {
      JOptionPane.showMessageDialog(null, "No hay conexión con la base de datos.");
      return;
    }

    try {
      resultado = sentencia.executeQuery("select nombre_ciudad from city");
      while (resultado.next()) {
        ciudades.addItem(resultado.getString("nombre_ciudad"));
      }
    } catch (SQLException err) {
      JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
    }

    ciudades.setBounds(350, 130, 100, 30);
    panel.add(ciudades);
    JButton boton1 = new JButton("Insertar");
    boton1.setBounds(500, 200, 80, 40);
    panel.add(boton1);
    JTextField ciudad = new JTextField();
    ciudad.setBounds(500, 130, 100, 30);
    panel.add(ciudad);
    JTextArea datos = new JTextArea();
    datos.setBounds(50, 50, 250, 200);
    panel.add(datos);
    ActionListener oyenteDeAccion = new ActionListener() {
      @Override
      public void actionPerformed(java.awt.event.ActionEvent e) {
        Map<Pair<String, String>, Integer> mapita = new HashMap<>();
        if (datos.getText() == "") {
          JOptionPane.showMessageDialog(null, "Debes llenar los datos para que se pueda ejecutar el programa");
        } else {

          Connection conn;
          Statement sentencia;
          ResultSet resultado;

          try { // Se carga el driver JDBC-ODBC
            Class.forName("oracle.jdbc.driver.OracleDriver");
          } catch (Exception err) {
            JOptionPane.showMessageDialog(null, "No se pudo cargar el driver JDBC");
            return;
          }

          try { // Se establece la conexión con la base de datos Oracle Express
            conn = DriverManager.getConnection("jdbc:oracle:thin:@DESKTOP-NF5GC4E:1521:xe", "ricardo", "123");
            sentencia = conn.createStatement();
          } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, "No hay conexión con la base de datos.");
            return;
          }

          try {
            // ANTES DE EJECUTAR ESTE PROGRAMA SE RECOMIENDA PRIMERO CREAR TODAS LAS TABLAS
            // A MANO EN SQL
            // en esta seccion del codigo llenamos el map con los datos existentes en la
            // base de datos
            // seleccion de una ciudad JOptionPane.showMessageDialog(null, ciudades.getSelectedItem());
            resultado = sentencia.executeQuery(
                "select codigovendedor,ciudad,t2.* from vvcity t,TABLE(t.ventas) t2 WHERE codigovendedor = "
                    + ciudad.getText() + " AND ciudad = '" + ciudades.getSelectedItem() + "'");

            while (resultado.next()) {
              Pair<String, String> ejemplo = new Pair<String, String>(resultado.getString("x"),
                  resultado.getString("y"));
              mapita.put(ejemplo, Integer.parseInt(resultado.getString("v")));

              // JOptionPane.showMessageDialog(null, resultado.getString("codigovendedor"));
              // JOptionPane.showMessageDialog(null, resultado.getString("ciudad"));
              // JOptionPane.showMessageDialog(null, resultado.getString("x"));
              // JOptionPane.showMessageDialog(null, resultado.getString("y"));
              // JOptionPane.showMessageDialog(null, resultado.getString("v"));
            }
            // se llenan los nuevos datos
            for (String punto : datos.getText().split("\n")) {
              Pair<String, String> ejemplo = new Pair<String, String>(punto.split(",")[0], punto.split(",")[1]);
              if (mapita.get(ejemplo) != null) {
                Integer a = mapita.get(ejemplo);
                mapita.put(ejemplo, (Integer.parseInt(punto.split(",")[2]) + a));
              } else {
                mapita.put(ejemplo, Integer.parseInt(punto.split(",")[2]));
              }
            }
            // se inserta a la base de datos
            resultado = sentencia.executeQuery("DELETE FROM VVCITY WHERE codigovendedor = " + ciudad.getText()
                + " AND ciudad = '" + ciudades.getSelectedItem() + "'");
            // JOptionPane.showMessageDialog(null, resultado.getString("ciudad"));
            String consulta = "INSERT INTO VVCITY VALUES(" + ciudad.getText() + "," + "'" + ciudades.getSelectedItem()
                + "'" + ",nest_ventas(";
            for (Map.Entry<Pair<String, String>, Integer> entry : mapita.entrySet()) {
              consulta = consulta + "ventas_ciudad(" + entry.getKey().toString().split("=")[0] + ","
                  + entry.getKey().toString().split("=")[1] + "," + entry.getValue() + "),";
            }
            consulta = consulta.substring(0, consulta.length() - 1) + "))";
            resultado = sentencia.executeQuery(consulta);
            resultado = sentencia.executeQuery("commit");

            // INSERT INTO VVCITY
            // VALUES(4,'cartagena',nest_ventas(ventas_ciudad(4,10,20),ventas_ciudad(8,5,10)));

            // Se cierra la conexion con la BD
            conn.close();
            // Se pintan dos círculos (que NO están en la BD)
            datos.setText("");
            JOptionPane.showMessageDialog(null, "Los datos se ingersaron con exito");
          } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
          }
        }
      }
    };
    boton1.addActionListener(oyenteDeAccion);
  }

}
