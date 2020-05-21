package webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jcraft.jroar.JRoar;

import java.io.IOException;

import javax.annotation.PostConstruct;


@Controller
public class WebController {
	
	@Autowired
	WebService webservice;
	
	Integer port = 9000;
	JRoar jroar;
	
	@PostConstruct
	private void init() throws IOException {
		jroar=new JRoar();
	    String puerto = "9000";
        String nombreArchivo = "/test1.ogg";
        String rutaPlaylist = "C:/Users/user/Desktop/EAS/20E04/App/src/main/resources/static/OGG/foo";
        //String rutaPlaylist = "C:/Users/aagui/Documents/EAS Local/20E04/App/src/main/resources/static/OGG/fooAlex";
        String[] argumentos= {"-port", puerto, "-playlist", nombreArchivo, rutaPlaylist,"-passwd","123" };
        jroar.main(argumentos);
    
        try {
            Runtime.getRuntime().exec("cmd /c start vlc http://localhost:9000/test1.ogg");
          } catch (IOException ioe) {
            System.out.println (ioe);
          }

        System.out.println();
        System.out.println("Ejecutando la radio en su VLC");
        System.out.println();
	}

	


    @GetMapping("/")
    public String main() {
        return "index";     
    }

    @GetMapping("/index")
    public String auxMain() {
        return "index";
    }

// ------------------------ INDEX ------------------------------
    @GetMapping("/login")
    public String login() {
        return "login";
    }
// -------------------------  PROFILE ------------------------------------------
    
    @PostMapping("/profile")
    public String profile() {
        return "profile";
    }
    
    @PostMapping("/profile/mount")
    public String profileMount(Model model, @RequestParam String mountPoint , String source, String selectMount, String limit, String password) {
    	System.out.println(mountPoint);
    	System.out.println(source);
    	System.out.println(selectMount);
    	System.out.println(limit);
    	System.out.println(password);
    	port++;
    	String[] argumentos= {"-port", port.toString(), "-playlist", mountPoint, source,"-passwd",password};
    	jroar.main(argumentos);
    	return "profile";
    }
    
    @PostMapping("/profile/drop")
    public String profileDrop(Model model, @RequestParam String selectDrop, String password) {
    	System.out.println(selectDrop);
    	System.out.println(password);
    /*	port++;
    	String[] argumentos= {"-drop",selectDrop,password};
    	jroar.main(argumentos);
    */
        return "profile";
    }

    @PostMapping("/profile/shout")
    public String profileShout(Model model, @RequestParam String selectShout, String ice, String icePassword, String password) {
    	System.out.println(selectShout);
    	System.out.println(ice);
    	System.out.println(icePassword);
    	System.out.println(password);
    	return "profile";
    }
    
    @PostMapping("/profile/update")
    public String profileChange(Model model, @RequestParam String name, String email) {
    	System.out.println(name);
    	System.out.println(email);
        return "profile";
    }
    

    
 // --------------------------------- ERROR ---------------------------------------
    
    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
