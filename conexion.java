public class conexion {
  private final String Nombre_PC = "DESKTOP-NF5GC4E";
  private final String usuario = "ricardo";
  private final String contraseña = "123";

  public String getConn() {
    return "jdbc:oracle:thin:@" + Nombre_PC + ":1521:xe";
  }

  public String getUser() {
    return usuario;
  }

  public String getPass() {
    return contraseña;
  }
}
