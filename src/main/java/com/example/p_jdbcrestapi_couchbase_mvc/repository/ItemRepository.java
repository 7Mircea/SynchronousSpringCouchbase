package com.example.p_jdbcrestapi_couchbase_mvc.repository;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.query.QueryResult;
import com.example.p_jdbcrestapi_couchbase_mvc.Exceptions.RepositoryException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class ItemRepository {
    /*@Query(value = "select p.prod_name,idProdQ.quantity from products p inner join (select i.id_prod id_prod,sum(i.quantity) quantity\n" +
            "from item i inner join invoice on i.invoice_nr = invoice.nr and i.invoice_Date = invoice.invoice_Date\n" +
            "where EXTRACT(month from invoice.invoice_Date) = 10 and EXTRACT(year from invoice.invoice_Date) = 2022 and invoice.type='c'\n" +
            "group by i.id_prod) idProdQ on p.id_prod=idProdQ.id_prod;",
            nativeQuery = true)
    Set<ProdQuantity1> findItemsBetweenDates();

    */

    private Cluster cluster;
    private Bucket bucket;
    private Scope scope;
    private Collection collection;
    private String COLLECTION_NAME = "item";

    public ItemRepository(Cluster cluster, Bucket bucket, Scope scope) {
        this.cluster = cluster;
        this.bucket = bucket;
        this.scope = scope;
        this.collection = scope.collection(COLLECTION_NAME);
    }

    /**
     * Simple Query 4
     * @return
     */
    public List<ProdQuantity1> findItemsBetweenDates() {
        String query = "select p.prod_name, idProdQ.quantity from products p inner join (select i.id_prod as id_prod,sum(i.quantity) quantity\n" +
                "from " + COLLECTION_NAME + " i inner join invoice inv on i.invoice_nr = inv.nr and i.invoice_date = inv.invoice_date\n" +
                "where DATE_PART_STR(inv.invoice_date, 'month') = 10 and DATE_PART_STR(inv.invoice_date,'year') = 2022 and inv.type='c'\n" +
                "group by i.id_prod) idProdQ on p.id_prod=idProdQ.id_prod;";
        try {
            QueryResult result = scope.query(query);
            List<ProdQuantity1> answerList = result.rowsAs(ProdQuantity1.class);
            return answerList;
        } catch (DocumentNotFoundException ex) {
            throw new RepositoryException(" unable to find max value in invoice", ex);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProdQuantity1 {
        String prod_name;
        int quantity;
    }
}
