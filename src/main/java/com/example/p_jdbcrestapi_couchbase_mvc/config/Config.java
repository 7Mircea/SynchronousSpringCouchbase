package com.example.p_jdbcrestapi_couchbase_mvc.config;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Scope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public Cluster cluster(@Value("${couchbase.clusterHost}") String hostname, @Value("${couchbase.username}") String username,
                           @Value("${couchbase.password}") String password) {
        return Cluster.connect(hostname,username,password);
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
