package my.diploma.project.init;

import org.hibernate.ejb.HibernatePersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by Евгений on 17.11.2015.
 */
@Configuration
@EnableWebMvc
@EnableTransactionManagement //аннотация, включающая менеджер транзакций
@ComponentScan("my.diploma.project") //пакет, в котором искать бины
@PropertySource("classpath:db.config") //адрес на конфиг
@EnableJpaRepositories("my.diploma.project.repository") //пакет с JPA репозиториями
public class ApplicationConfig extends WebMvcConfigurerAdapter{

    private static final String MESSAGE_SOURCE = "message.source"; //сообщения

    private static final String DATABASE_DRIVER = "db.driver"; //драйвер БД
    private static final String DATABASE_USERNAME = "db.username"; //логин к БД
    private static final String DATABASE_PASSWORD = "db.password"; //пароль к БД
    private static final String DATABASE_URL = "db.url"; //адрес для подключения к БД


    private static final String HIBERNATE_DIALECT = "hibernate.dialect"; //диалект общения с БД
    private static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql"; //показывать ли запросы SQL в консоли
    private static final String ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan"; //пакеты, в которых находятся репозитории
    private static final String HOW_TO_OPERATE_WITH_DB = "hibernate.hbm2ddl.auto"; //модель доступа к БД (обновлять или пересоздавать?)

    @Resource //ресурс, который тянет данные из файла.
    private Environment env;

    @Bean
    public DataSource dataSource() { // возврат источника данных (конфиг тянем с файла db.config)
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty(DATABASE_DRIVER));
        dataSource.setUrl(env.getRequiredProperty(DATABASE_URL));
        dataSource.setUsername(env.getRequiredProperty(DATABASE_USERNAME));
        dataSource.setPassword(env.getRequiredProperty(DATABASE_PASSWORD));
        return dataSource;
    }

    @Bean //бин фабрики менеджера сущностей
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistence.class);
        entityManagerFactoryBean.setPackagesToScan(env.getRequiredProperty(ENTITYMANAGER_PACKAGES_TO_SCAN));
        entityManagerFactoryBean.setJpaProperties(hibProperties());
        return entityManagerFactoryBean;
    }

    private Properties hibProperties() { //метод возвращает параметры Hibernate
        Properties properties = new Properties();
        properties.put(HIBERNATE_DIALECT, env.getRequiredProperty(HIBERNATE_DIALECT));
        properties.put(HIBERNATE_SHOW_SQL, env.getRequiredProperty(HIBERNATE_SHOW_SQL));
        properties.put(HOW_TO_OPERATE_WITH_DB, env.getRequiredProperty(HOW_TO_OPERATE_WITH_DB));
        return properties;
    }

    @Bean //менеджер транзакций
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public UrlBasedViewResolver setupViewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setPrefix("/WEB-INF/pages/");
        resolver.setSuffix(".jsp");
        resolver.setViewClass(JstlView.class);
        return resolver;
    }

//    @Bean //бин для загрузки сообщений по коду (интернационализация)
//    public ResourceBundleMessageSource messageSource() {
//        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
//        source.setBasename(env.getRequiredProperty(MESSAGE_SOURCE));
//        source.setUseCodeAsDefaultMessage(true);
//        return source;
//    }

    @Override //разрешение статических ресурсов
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

//    @Bean
//    public CommonsMultipartResolver multipartResolver() {
//        CommonsMultipartResolver resolver=new CommonsMultipartResolver();
//        resolver.setDefaultEncoding("utf-8"); //казываем кодировку
//        //прописать органичения на обьем файла
//        return resolver;
//    }

}
