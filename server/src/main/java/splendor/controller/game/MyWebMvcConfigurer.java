//package splendor.controller.game;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * Class responsible for configuring the web mvc.
// */
//@Configuration
//public class MyWebMvcConfigurer implements WebMvcConfigurer {
//  @Autowired
//  private SplendorController splendorController;
//
//  @Override
//  public void addInterceptors(InterceptorRegistry registry) {
//    registry.addInterceptor(splendorController).addPathPatterns("/api/games/**");
//  }
//}