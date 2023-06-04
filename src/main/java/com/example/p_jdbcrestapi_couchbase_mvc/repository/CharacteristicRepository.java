package com.example.p_jdbcrestapi_couchbase_mvc.repository;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.query.QueryResult;
import com.example.p_jdbcrestapi_couchbase_mvc.Exceptions.RepositoryException;
import com.example.p_jdbcrestapi_couchbase_mvc.model.Characteristic;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CharacteristicRepository {
    private Cluster cluster;
    private Bucket bucket;
    private Scope scope;
    private Collection collection;
    private String COLLECTION_NAME = "characteristic";

    public CharacteristicRepository(Cluster cluster, Bucket bucket, Scope scope) {
        this.cluster = cluster;
        this.bucket = bucket;
        this.scope = scope;
        this.collection = scope.collection(COLLECTION_NAME);
    }

    /*
    simple query 2
     */
    public List<Characteristic> findCharacteristicByProduct(int idProd) {
        String query = "select " + COLLECTION_NAME + ".* from " + COLLECTION_NAME + " where " + COLLECTION_NAME + ".id_prod = " + idProd;
        try {
            QueryResult result = scope.query(query);
            return result.rowsAs(Characteristic.class);
        } catch (DocumentNotFoundException ex) {
            throw new RepositoryException("Unable to locate documents with key before: " + idProd, ex);
        }
    }

}
