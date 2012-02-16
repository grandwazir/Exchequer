package name.richardson.james.bukkit.exchequer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.EbeanServer;

public class DatabaseHandler extends name.richardson.james.bukkit.util.Database {

  public static List<Class<?>> getDatabaseClasses() {
    final List<Class<?>> list = new ArrayList<Class<?>>();
    list.add(AccountRecord.class);
    list.add(PlayerRecord.class);
    return list;
  }

  public DatabaseHandler(final EbeanServer database) throws SQLException {
    super(database);
  }

}
