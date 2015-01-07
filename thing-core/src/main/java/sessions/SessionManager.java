package sessions;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SessionManager {

	private static final Logger log = Logger.getLogger( SessionManager.class.getName() );
	
	private static int counter = 0;
	
	public static synchronized int generateId() {
		log.log(Level.FINE, "Generated new id " + counter);
		return counter++;
	}
	
}
