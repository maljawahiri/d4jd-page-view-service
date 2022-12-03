package guru.springframework;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import static guru.springframework.util.PropertiesLogger.runWithPropertiesLogger;

@SpringBootApplication
public class PageViewServiceApplication {

	public static void main(String[] args) {
		runWithPropertiesLogger(PageViewServiceApplication.class, args);
	}
}
