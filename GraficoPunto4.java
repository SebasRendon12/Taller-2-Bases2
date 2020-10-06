import java.awt.Color;
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

public class GraficoPunto4 extends JFrame {
  private static final long serialVersionUID = 1L;
  static String Ciudad = "";

  public GraficoPunto4(String ciudad) {
    GraficoPunto4.Ciudad = ciudad;
  }

  public void paint(Graphics g) {
    int x = 550;
    int y = 550;
    Map<Pair<String, String>, Integer> mapita = new HashMap<>();

    g.setColor(Color.yellow);
    g.fillRect(50, 50, 500, 500);

    g.setColor(Color.green);
    for (int i = 50; i < y; i += 25)
      g.drawLine(50, i, x, i);
    for (int i = 50; i < x; i += 25)
      g.drawLine(i, 50, i, y);

    g.setColor(Color.red);
    g.drawLine(600 / 2, 50, 600 / 2, y);
    g.drawLine(50, 600 / 2, x, 600 / 2);
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
      ciudad CiudadActual = new ciudad();
      List<Local> lstLocales = new ArrayList<>();
      resultado = sentencia
          .executeQuery("SELECT DBMS_XMLGEN.GETXML('SELECT nombre_ciudad, Locales FROM CITY') docxml FROM DUAL");
      if (resultado.next()) {
        String consulta = resultado.getString("docxml");
        String rectangulo[] = consulta.split("<ROW>");
        int number = 0;
        for (int i = 1; i < rectangulo.length; i++) {
          String primeraLinea = rectangulo[i].split("\n")[1];
          if (primeraLinea.contains(Ciudad)) {
            number = i;
            break;
          }

        }
        String[] local = rectangulo[number].split("<rectangulo>");

        CiudadActual.NombreCiudad = Ciudad;
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
        CiudadActual.Locales = lstLocales;
      } else {
        JOptionPane.showMessageDialog(null, "No hay locales en esta ciudad");
      }

      resultado = sentencia
          .executeQuery("select codigovendedor,ciudad,t2.* from vvcity t,TABLE(t.ventas) t2 WHERE ciudad = '"
              + GraficoPunto4.Ciudad + "'");

      while (resultado.next()) {
        Pair<String, String> ejemplo = new Pair<String, String>(resultado.getString("x"), resultado.getString("y"));
        mapita.put(ejemplo, Integer.parseInt(resultado.getString("v")));
      }

      boolean dentro = false;
      int total = 0;
      for (Map.Entry<Pair<String, String>, Integer> entry : mapita.entrySet()) {
        int aux = 0;
        dentro = false;
        for (Local local1 : CiudadActual.Locales) {
          // if para validar si una venta ocurrio dentro de un local
          if ((Integer.parseInt(entry.getKey().toString().split("=")[0]) > local1.a.intValue()
              && Integer.parseInt(entry.getKey().toString().split("=")[1]) > local1.b.intValue())
              && (Integer
                  .parseInt(entry.getKey().toString().split("=")[0]) < (local1.c.intValue() + local1.a.intValue())
                  && Integer.parseInt(
                      entry.getKey().toString().split("=")[1]) < (local1.b.intValue() + local1.d.intValue()))) {
            local1.totalVentas += entry.getValue();
            dentro = true;
          } else {
            aux++;
          }
          if (dentro) {
            break;
          } else {
            if (aux == CiudadActual.Locales.size()) {
              total += entry.getValue();
            }
          }
        }
      }

      for (Local local2 : CiudadActual.Locales) {
        g.setColor(Color.black);
        g.drawRect(local2.a + 50, local2.b + 50, local2.c, local2.d);

        g.setColor(Color.blue);
        g.drawOval(local2.a + 50 + (local2.c / 2), local2.b + 50 + (local2.d / 2), 4, 4);
        g.drawString(local2.totalVentas.toString(), local2.a + 48 + (local2.c / 2), local2.b + 48 + (local2.d / 2));
      }
      g.setColor(Color.white);
      g.fillRect(142, 552, 250, 17);
      g.setColor(Color.black);
      g.drawString("Ventas por fuera de los locales: " + total, 150, 565);

    } catch (

    SQLException e) {
      JOptionPane.showMessageDialog(null, "error " + e);
      return;
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
    Integer totalVentas = 0;

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
