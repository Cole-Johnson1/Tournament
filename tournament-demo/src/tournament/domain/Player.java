package tournament.domain;
import java.util.Objects;
public class Player {
 private final String name;
 public Player(String name){this.name=name.trim();}
 public String getName(){return name;}
 @Override public boolean equals(Object o){return o instanceof Player p && Objects.equals(name.toLowerCase(), p.name.toLowerCase());}
 @Override public int hashCode(){return Objects.hash(name.toLowerCase());}
}
