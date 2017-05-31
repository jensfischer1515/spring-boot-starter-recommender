package org.openended.recommender;

import static net.sf.log4jdbc.tools.LoggingType.SINGLE_LINE;

import javax.sql.DataSource;

import org.springframework.beans.factory.config.BeanPostProcessor;

import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import net.sf.log4jdbc.SpyLogDelegator;
import net.sf.log4jdbc.tools.Log4JdbcCustomFormatter;

// http://blog.jhades.org/logging-the-actualreal-sql-queries-of-a-springhibernate-application/
public class LoggingDataSourcePostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof DataSource) {
            DataSource dataSource = (DataSource) bean;
            Log4jdbcProxyDataSource loggingDataSource = new Log4jdbcProxyDataSource(dataSource);
            loggingDataSource.setLogFormatter(logFormatter());
            return loggingDataSource;
        }
        return bean;
    }

    private SpyLogDelegator logFormatter() {
        Log4JdbcCustomFormatter formatter = new Log4JdbcCustomFormatter();
        formatter.setLoggingType(SINGLE_LINE);
        //formatter.setMargin(19);
        formatter.setSqlPrefix("");
        return formatter;
    }
}
