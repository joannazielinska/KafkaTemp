package pl.edu.agh.source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.io.*;

@SpringBootApplication
@EnableBinding(Source.class)
@EnableScheduling
public class SourceApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceApplication.class);

    @Autowired
    Source source;

    @Autowired
    ResourceLoader resourceLoader;

    BufferedReader br;

    public static void main(String[] args) {
        SpringApplication.run(SourceApplication.class, args);
    }

    @Scheduled(fixedRate = 1000)
    void send() {
        source.output()
                .send(MessageBuilder.withPayload(getLine()).build());
    }

    private String getLine(){
        String line="";
        try {
           line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return line;
    }

    @PostConstruct
    private void fileHandler(){

        Resource resource = resourceLoader.getResource("classpath:dataset.txt");
        LOGGER.info("loading data from file");
        try {
            br = new BufferedReader(new FileReader(resource.getFile()));
        } catch (FileNotFoundException e) {
            LOGGER.error("Resource not found: ", e);
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.error("IOException ", e);
            e.printStackTrace();
        }
    }


}
