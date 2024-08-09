//package nl.novi.LivingInSync.security;

//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//import java.util.Arrays;

//@Configuration
//ublic class WebConfig implements WebMvcConfigurer {

//    @Bean
  //  public CorsConfigurationSource corsConfigurationSource() {
 //       CorsConfiguration configuration = new CorsConfiguration();
 //      configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173")); // Allow this origin
 //       configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
 //       configuration.setAllowedHeaders(Arrays.asList("*"));
  //      configuration.setAllowCredentials(true); // If you need cookies, authorization headers or TLS client certificates to be exposed
  //      configuration.setMaxAge(3600L); // How long the response from a preflight request can be cached by clients

  //      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
   //     source.registerCorsConfiguration("/**", configuration); // Apply CORS to all paths
   //     return source;
   // }
//}
