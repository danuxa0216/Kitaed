package com.example.springsecurityapplication.config;

import com.example.springsecurityapplication.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig{
    private final PersonDetailsService personDetailsService;

    @Bean
    public PasswordEncoder getPasswordEncode(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // конфигурируем работу Spring Security
        // http.csrf().disable() - отключаем защиту от межсайтовой подделки запросов
        // .authorizeHttpRequests() - указываем, что всестраницы должны быть защищены аутентификацией
        // .requestMatchers("/authentication", "/error") - указываем, что не аутентифицированные
        // пользователи могут зайти на страницу аутентификации и на объект ошибки
        // .permitAll() - указываем, что не аутентифицированные пользователи могут заходить на перечисленные страницы
        // .anyRequest() - указываем, что для всех остальных страниц необходимо вызывать метод .authenticated(),
        // который открыввет форму аутентификации
        // .and() - указываем, что дальше настраивается аутентификация, и соединяем её с настройкой доступа
        // .formLogin().loginPage("/authentication") - указываем, какой url-запрос будет отправляться при заходе на
        // защищённые страницы
        // .loginProcessingUrl("/process_login") - указываем, на какой url-адрес будут отправляться данные с формы
        // чтд: нам не нужно создавать метод в контроллере и обрабатывать данные с формы. мы задали url, который
        // используется по умолчанию для обработки формы аутентификации по средствам Spring Security. Spring Security
        // будет ждать объект с формы аутентификации и затем сверять логин и пароль с данными в БД
        // .defaultSuccessUrl("/index", true) - указываем, на какой url необходимо направлять пользователя после
        // успешной аутентификации. Второй аргумент - true - чтобы перенаправление шло в любом случае после успешной
        // аутентификации
        // .failureUrl("/authentication"); - указываем, куда перенаправить пользователя при неудачной аутентификации.
        // в запрос будет передан объект error, который будет проверяться на форме, и при наличии данного объекта в
        // запросе будет выводиться сообщение "Неправильный логин или пароль"

        // без ролей:
//        http.csrf().disable().authorizeHttpRequests().requestMatchers("/authentication", "/error", "/registration").permitAll().anyRequest().authenticated().and().formLogin().loginPage("/authentication").loginProcessingUrl("/process_login").defaultSuccessUrl("/index", true).failureUrl("/authentication?error").and().logout().logoutUrl("/logout").logoutSuccessUrl("/authentication");
//        return http.build();

        // с ролями:
        // requestMatchers("/admin").hasRole("ADMIN") - указываем на то, что страница /admin доступна пользователю с ADMIN
        // ролью

        // .csrf().disable()
        http.authorizeHttpRequests().requestMatchers("/admin").hasRole("ADMIN").requestMatchers("/authentication",
                "/registration", "/error", "/resources/**", "/static/**", "/css/**", "/js/**", "/img/**").permitAll().anyRequest().hasAnyRole("USER", "ADMIN").and().formLogin().loginPage("/authentication").loginProcessingUrl("/process_login").defaultSuccessUrl("/index", true).failureUrl("/authentication?error").and().logout().logoutUrl("/logout").logoutSuccessUrl("/authentication");
        return http.build();
    }


    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }
//    private final AuthenticationProvider authenticationProvider;
//    public SecurityConfig(AuthenticationProvider authenticationProvider){
//        this.authenticationProvider = authenticationProvider;
//    }

    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        authenticationManagerBuilder.userDetailsService(personDetailsService).passwordEncoder(getPasswordEncode());
    }
}
