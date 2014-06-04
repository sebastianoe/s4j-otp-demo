import java.util.Locale;

import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {
	@Override
	public void onStart(Application app) {
		Locale.setDefault(Locale.US);
	}
}