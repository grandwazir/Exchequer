package name.richardson.james.bukkit.exchequer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.avaje.ebean.EbeanServer;

import name.richardson.james.bukkit.utilities.database.Database;

public class DatabaseHandler extends Database {

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
