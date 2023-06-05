package com.example.p_jdbcrestapi_couchbase_mvc.repository;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.query.QueryResult;
import com.example.p_jdbcrestapi_couchbase_mvc.Exceptions.RepositoryException;
import com.example.p_jdbcrestapi_couchbase_mvc.model.Products;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductsRepository {

    private Cluster cluster;
    private Bucket bucket;

    //Set<Products> findProductsByIdProdIsBefore(int N);
    private Scope scope;
    private Collection collection;
    private String COLLECTION_NAME = "products";
    public ProductsRepository(Cluster cluster, Bucket bucket, Scope scope) {
        this.cluster = cluster;
        this.bucket = bucket;
        this.scope = scope;
        this.collection = scope.collection(COLLECTION_NAME);
    }

    public List<CategoryProductProfit> findProductWithGreatestProfitInCategory() {
        String query = "select distinct (SS.category) category, SS.Product_name, SS.Profit  \n" +
                "              from (select S.category, S.prod_name Product_name, (S.sales - C.costs) Profit  \n" +
                "                  from (select p.category category, p.prod_name prod_name, sum(i.quantity * i.unit_price) sales  \n" +
                "                      from item i inner join products p on i.id_prod = p.id_prod  \n" +
                "                          inner join invoice inv on inv.nr = i.invoice_nr and inv.invoice_date = i.invoice_date  \n" +
                "                          where inv.type = 'c'  \n" +
                "                          group by i.id_prod,p.prod_name,p.category) S   \n" +
                "                      inner join   \n" +
                "                      (select p.prod_name prod_name, sum(i.quantity * i.unit_price) costs  \n" +
                "                      from item i inner join products p on i.id_prod = p.id_prod  \n" +
                "                          inner join invoice f on f.nr = i.invoice_nr and f.invoice_date = i.invoice_date  \n" +
                "                          where f.type = 's'  \n" +
                "                          group by i.id_prod,p.prod_name) C   \n" +
                "                      on S.prod_name = C.prod_name   \n" +
                "                  order by S.prod_name) SS\n" +
                "where SS.Profit = (select Max((S.sales - C.costs)) as Profit\n" +
                "            from (select p.prod_name as prod_name, p.category as category, sum(i.quantity * i.unit_price) as sales  \n" +
                "            from item i inner join products p on i.id_prod = p.id_prod  \n" +
                "            inner join invoice f on f.nr = i.invoice_nr and f.invoice_date = i.invoice_date  \n" +
                "            where f.type = 'c' and p.category = SS.category  \n" +
                "            group by i.id_prod,p.prod_name,p.category) S   \n" +
                "            inner join   \n" +
                "            (select p.prod_name prod_name, sum(i.quantity * i.unit_price) costs  \n" +
                "            from item i inner join products p on i.id_prod = p.id_prod  \n" +
                "            inner join invoice f on f.nr = i.invoice_nr and f.invoice_date = i.invoice_date  \n" +
                "            where f.type = 's' and p.category = SS.category  \n" +
                "            group by i.id_prod,p.prod_name) as C\n" +
                " on S.prod_name = C.prod_name group by S.category)";
        try {
            QueryResult result = scope.query(query);
            return result.rowsAs(CategoryProductProfit.class);
        } catch (DocumentNotFoundException ex) {
            throw new RepositoryException("Unable to locate documents with key before: ", ex);
        }
    }

    public List<CategoryProfit> findProfitForEachCategory() {
        String query = "select S.category as category, sum(S.sales - C.costs) as profit  \n" +
                "              from (select p.category as category, p.prod_name as prod_name, sum(i.quantity * i.unit_price) as sales  \n" +
                "                  from item i inner join products p on i.id_prod = p.id_prod  \n" +
                "                      inner join invoice f on f.nr = i.invoice_nr and f.invoice_date = i.invoice_date  \n" +
                "                      where f.type = 'c'  \n" +
                "                      group by i.id_prod, p.prod_name, p.category) S   \n" +
                "                  inner join   \n" +
                "                  (select p.prod_name as prod_name, sum(i.quantity * i.unit_price) as costs  \n" +
                "                  from item i inner join products p on i.id_prod = p.id_prod  \n" +
                "                      inner join invoice inv on inv.nr = i.invoice_nr and inv.invoice_date = i.invoice_date  \n" +
                "                      where inv.type = 's'  \n" +
                "                      group by i.id_prod,p.prod_name) C   \n" +
                "                  on S.prod_name = C.prod_name   \n" +
                "              group by S.category  \n" +
                "              order by S.category;";
        try {
            QueryResult result = scope.query(query);
            return result.rowsAs(CategoryProfit.class);
        } catch (DocumentNotFoundException ex) {
            throw new RepositoryException("Unable to locate documents with key before: ", ex);
        }
    }

    public String findByIdAsString(String id) {
        try {
            GetResult result = collection.get(id);
            return result.contentAsObject().toString();
        } catch (DocumentNotFoundException ex) {
            throw new RepositoryException("Unable to locate document for key: " + id, ex);
        }
    }

    public Iterable<Products> findProductsByIdProdIsBefore(int n) {
        String query = "select " + COLLECTION_NAME + ".* from " + COLLECTION_NAME + " where " + COLLECTION_NAME + ".id_prod < " + n;
        try {
            QueryResult result = scope.query(query);
            return result.rowsAs(Products.class);
        } catch (DocumentNotFoundException ex) {
            throw new RepositoryException("Unable to locate documents with key before: " + n, ex);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryProfit {
        private String category;
        private float profit;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryProductProfit {
        private String category;
        private String product_name;
        private float profit;
    }
}
