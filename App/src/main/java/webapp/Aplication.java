package webapp;

import com.jcraft.jroar.JRoar;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class Aplication {
    public static void main (String [] args){
        SpringApplication.run(Aplication.class, args);
    }
}
