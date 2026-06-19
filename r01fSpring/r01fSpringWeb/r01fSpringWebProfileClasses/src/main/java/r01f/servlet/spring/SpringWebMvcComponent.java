package r01f.servlet.spring;


import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@EnableWebMvc
public class SpringWebMvcComponent
  implements WebMvcConfigurer {
 //Just to enbale WebMVC
}
