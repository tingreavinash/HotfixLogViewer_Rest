/**
 * Spring Boot Main class for starting the application.
 * @author Avinash Tingre
 */

package com.avinash.hotfixviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.avinash.hotfixviewer.Service.ECPFileHandler;
import com.avinash.hotfixviewer.Service.ECPLogService;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@SpringBootApplication
public class HotfixviewerApplication implements CommandLineRunner {
	
	private static final Logger LOG = LoggerFactory.getLogger(HotfixviewerApplication.class);
	public static List<String> distinctCramerVersion = new ArrayList<String>();
	public static List<String> distinctModules = new ArrayList<String>();
	
	@Autowired ECPLogService ecpService;
	@Autowired ECPFileHandler ecpHandler;
	public static void main(String[] args) {
		SpringApplication.run(HotfixviewerApplication.class, args);
	}
	
	@Bean
    public OpenAPI customOpenAPI(@Value("${application-description}") String appDesciption, @Value("${application-version}") String appVersion) {
     return new OpenAPI()
          .info(new Info()
          .title("Hotfix Log Viewer - Backend")
          .version(appVersion)
          .description(appDesciption)
          .termsOfService("http://swagger.io/terms/")
          .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
	
	/**
	 * This method runs immediately after starting spring boot app.
	 * It will delete old records from Database and will add all new records from excel into DB.
	 */
	
	@Override
    public void run (String... args) throws IOException {
		LOG.info("\n\n\n------------>> Hotfix Application Started <<------------");

		long total_records_inserted = ecpHandler.mergeExcelDataToDB();
		if (total_records_inserted != 0) {
			LOG.info("Data loaded in DB. Records inserted: "+total_records_inserted);
		}else {
			LOG.warn("Operation failed.");
		}
		Map<Set<String>, Set<String>> map= ecpService.getDistinctValues();
		
		Set<String> version_set =null;
		Set<String> module_set = null;
		for (Map.Entry<Set<String>, Set<String>> entry: map.entrySet()) {
			version_set = entry.getKey();
			module_set = entry.getValue();
		}
		distinctCramerVersion.addAll(version_set);
		distinctModules.addAll(module_set);
		LOG.info("Retrieved distinct cramer versions and modules.");
		
	}

}