import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javafx.util.Pair;

public class GraficoPunto3 extends JFrame {

  static String Ciudad = "";

  public GraficoPunto3(String ciudad) {
    GraficoPunto3.Ciudad = ciudad;
  }

  public void paint(Graphics g) {
    Dimension d = getSize();
    int x = 500;
    int y = 500;
    Map<Pair<String, String>, Integer> mapita = new HashMap<>();

    g.setColor(Color.yellow);
    g.fillRect(50, 50, x, y);

    g.setColor(Color.green);
    for (int i = 0; i < y; i += 25)
      g.drawLine(50, i, x, i);
    for (int i = 0; i < x; i += 25)
      g.drawLine(i, 50, i, y);

    g.setColor(Color.red);
    g.drawLine(x / 2, 50, x / 2, y);
    g.drawLine(50, y / 2, x, y / 2);
    g.setColor(Color.blue);

    Connection conn;
    Statement sentencia;
    ResultSet resultado;

    try { // Se carga el driver JDBC-ODBC
      Class.forName("oracle.jdbc.driver.OracleDriver");
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, "No se pudo cargar el driver JDBC");
      return;
    }

    try {
      conexion connection = new conexion();
      conn = DriverManager.getConnection(connection.getConn(), connection.getUser(), connection.getPass());
      sentencia = conn.createStatement();
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "No hay conexión con la base de datos.");
      return;
    }
    try {
      resultado = sentencia
          .executeQuery("select codigovendedor,ciudad,t2.* from vvcity t,TABLE(t.ventas) t2 WHERE ciudad = '"
              + GraficoPunto3.Ciudad + "'");

      while (resultado.next()) {
        Pair<String, String> ejemplo = new Pair<String, String>(resultado.getString("x"), resultado.getString("y"));
        mapita.put(ejemplo, Integer.parseInt(resultado.getString("v")));

        // JOptionPane.showMessageDialog(null, resultado.getString("codigovendedor"));
        // JOptionPane.showMessageDialog(null, resultado.getString("ciudad"));
        // JOptionPane.showMessageDialog(null, resultado.getString("x"));
        // JOptionPane.showMessageDialog(null, resultado.getString("y"));
        // JOptionPane.showMessageDialog(null, resultado.getString("v"));
      }
      for (Map.Entry<Pair<String, String>, Integer> entry : mapita.entrySet()) {
        g.setColor(Color.blue);
        g.drawOval(Integer.parseInt(entry.getKey().toString().split("=")[0]) + 50,
            Integer.parseInt(entry.getKey().toString().split("=")[1]) + 50, 5, 5);
        g.drawString("$" + entry.getValue(), Integer.parseInt(entry.getKey().toString().split("=")[0]) + 48,
            Integer.parseInt(entry.getKey().toString().split("=")[1]) + 48);
      }

    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "error " + e);
      return;
    }
    try {
      ciudad Ciudad = new ciudad();

      Ciudad.NombreCiudad = GraficoPunto3.Ciudad;

      List<Local> lstLocales1 = new ArrayList<Local>();

      Ciudad.Locales = lstLocales1;

      resultado = sentencia
          .executeQuery("SELECT nombre_ciudad FROM CITY WHERE nombre_ciudad = '" + GraficoPunto3.Ciudad + "'");
      if (resultado.next()) {
        resultado = sentencia
            .executeQuery("SELECT DBMS_XMLGEN.GETXML('SELECT nombre_ciudad, Locales FROM CITY') docxml FROM DUAL");
        // Intento de traer el xml de solo la ciudad ingresada por el usuario en un
        // String
        if (resultado.next()) {
          String consulta = resultado.getString("docxml");
          String rectangulo[] = consulta.split("<ROW>");
          int number = 0;
          for (int i = 1; i < rectangulo.length; i++) {
            String primeraLinea = rectangulo[i].split("\n")[1];
            if (primeraLinea.contains(GraficoPunto3.Ciudad)) {
              number = i;
              break;
            }

          }
          String[] local = rectangulo[number].split("<rectangulo>");
          ciudad CiudadOld = new ciudad();
          List<Local> lstLocales = new ArrayList<>();
          CiudadOld.NombreCiudad = GraficoPunto3.Ciudad;
          for (String string : local) {
            Local newLocal = new Local();
            Pattern patterna = Pattern.compile("\s*(?:(<a>\\w*<..>))", Pattern.CASE_INSENSITIVE);
            Pattern patternb = Pattern.compile("\s*(?:(<b>\\w*<..>))", Pattern.CASE_INSENSITIVE);
            Pattern patternc = Pattern.compile("\s*(?:(<c>\\w*<..>))", Pattern.CASE_INSENSITIVE);
            Pattern patternd = Pattern.compile("\s*(?:(<d>\\w*<..>))", Pattern.CASE_INSENSITIVE);
            Matcher matchera = patterna.matcher(string);
            Matcher matcherb = patternb.matcher(string);
            Matcher matcherc = patternc.matcher(string);
            Matcher matcherd = patternd.matcher(string);
            if (matchera.find()) {
              newLocal.a = Integer.parseInt(matchera.group().substring(6 + 1, (matchera.group().length() - 4)));
            }
            if (matcherb.find()) {
              newLocal.b = Integer.parseInt(matcherb.group().substring(6 + 1, (matcherb.group().length() - 4)));
            }
            if (matcherc.find()) {
              newLocal.c = Integer.parseInt(matcherc.group().substring(6 + 1, (matcherc.group().length() - 4)));
            }
            if (matcherd.find()) {
              newLocal.d = Integer.parseInt(matcherd.group().substring(6 + 1, (matcherd.group().length() - 4)));
            }
            if (newLocal.a != null && newLocal.b != null && newLocal.c != null && newLocal.d != null) {
              lstLocales.add(newLocal);
            }
          }
          CiudadOld.Locales = lstLocales;
          for (Local local2 : lstLocales) {
            g.setColor(Color.black);
            g.drawRect(local2.a + 50, local2.b + 50, local2.c, local2.d);
          }
        }
      }
      conn.close();
    } catch (

    SQLException err) {
      JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
    }

  }

  public class ciudad {
    String NombreCiudad;
    List<Local> Locales;

    public ciudad(String name, List<Local> locals) {
      NombreCiudad = name;
      Locales = locals;
    }

    public ciudad() {
    }
  }

  public class Local {
    Integer a;
    Integer b;
    Integer c;
    Integer d;

    public Local(int a, int b, int c, int d) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
    }

    public Local() {

    }
  }

}
