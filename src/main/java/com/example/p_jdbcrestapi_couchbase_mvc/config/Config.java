package com.example.p_jdbcrestapi_couchbase_mvc.config;

import com.couchbase.client.core.env.CoreEnvironment;
import com.couchbase.client.core.env.ThresholdLoggingTracerConfig;
import com.couchbase.client.core.env.ThresholdRequestTracerConfig;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.env.ClusterEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Config {
    @Bean
    public Cluster cluster(@Value("${couchbase.clusterHost}") String hostname, @Value("${couchbase.username}") String username,
                           @Value("${couchbase.password}") String password) {
        ClusterEnvironment.Builder environmentBuilder = ClusterEnvironment.builder();
        environmentBuilder.timeoutConfig().queryTimeout(Duration.ofMinutes(6)).build();
        ClusterOptions options = ClusterOptions.clusterOptions(username,password).environment(environmentBuilder.build());
        return Cluster.connect(hostname,options);
    }


    @Bean
    public Bucket bucket(Cluster cluster, @Value("${couchbase.bucket}") String bucketName) {
        return cluster.bucket(bucketName);
    }

    @Bean
    public Scope scope(Bucket bucket, @Value("${couchbase.scope}")String scopeString) {
        System.out.println("couchbase scope : " + scopeString);
        return  bucket.scope(scopeString);
    }

}
