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
public class InvoiceRepository{// extends JpaRepository<Invoice, InvoiceId> {
    /*@Query(value = "select max(value) from invoice;",nativeQuery = true)
    public Float findMaxValue();*/






    /*@Query(value = "select S.month, S.sales \n" +
            "from (select SUM(value) as sales,\n" +
            "            EXTRACT(month from invoice_date) as month,\n" +
            "            EXTRACT(year from invoice_date) as year\n" +
            "        from invoice \n" +
            "        where type='c'\n" +
            "        group by EXTRACT(month from invoice_date),EXTRACT(year from invoice_date)) S\n" +
            "    left join (select SUM(value) as sales,\n" +
            "            EXTRACT(month from invoice_date) as month,\n" +
            "            EXTRACT(year from invoice_date) as year\n" +
            "        from invoice \n" +
            "        where type='c'\n" +
            "        group by EXTRACT(month from invoice_date),EXTRACT(year from invoice_date)) S2\n" +
            "on S.sales < S2.sales\n" +
            "where S2.month is null;",nativeQuery = true)
    Set<MonthSale> getMonthWithGreatestSales();





    interface MonthSale {
        String getMonth();
        float getSales();
    }*/

    private Cluster cluster;
    private Bucket bucket;
    private Scope scope;
    private Collection collection;
    private String COLLECTION_NAME = "invoice";

    public InvoiceRepository(Cluster cluster, Bucket bucket, Scope scope) {
        this.cluster = cluster;
        this.bucket = bucket;
        this.scope = scope;
        this.collection = scope.collection(COLLECTION_NAME);
    }

    public Float findMaxValue(){
        String query =  "select max("+COLLECTION_NAME+".value_) as v from "+COLLECTION_NAME;
        try {
            QueryResult result = scope.query(query);
            List<FloatWrapper> answerList = result.rowsAs(FloatWrapper.class);
            return answerList.size() > 0 ? answerList.get(0).v : -1;
        } catch (DocumentNotFoundException ex) {
            throw new RepositoryException(" unable to find max value in invoice" , ex);
        }
    }

    public List<SalesCostMonthYear> findSalesCostMonthYear() {
        String query =  "select S.sales as sales, Ch.costs as costs, S.month as month_, S.year as year_ from \n" +
                "(select SUM(value_) as sales,\n" +
                "DATE_PART_STR(invoice_date,'month') as month,\n" +
                "DATE_PART_STR(invoice_date,'year') as year \n" +
                "from invoice \n" +
                "where type='c'\n" +
                "group by DATE_PART_STR(invoice_date,'month'), DATE_PART_STR(invoice_date,'year')) as S\n" +
                "join\n" +
                "(select SUM(value_) as costs,\n" +
                "DATE_PART_STR(invoice_date,'month') as month,\n" +
                "DATE_PART_STR(invoice_date,'year') as year \n" +
                "from invoice \n" +
                "where type='s'\n" +
                "group by DATE_PART_STR(invoice_date,'month'),DATE_PART_STR(invoice_date,'year')) as Ch\n" +
                "on S.month = Ch.month and S.year=Ch.year\n" +
                " order by S.year, S.month;";
        try {
            QueryResult result = scope.query(query);
            return result.rowsAs(SalesCostMonthYear.class);
        } catch (DocumentNotFoundException ex) {
            throw new RepositoryException(" unable to find max value in invoice" , ex);
        }
    }

    /**
     * Complex Q3
     * @return
     */
    public List<ProfitOnEachProduct> findProfitOnEachProduct() {
        String query =  "select S.prod_name, (S.sales - C.costs) profit from \n" +
                "(select p.prod_name as prod_name, sum(i.quantity * i.unit_price) as sales\n" +
                "from item as i inner join products as p on i.id_prod = p.id_prod\n" +
                "inner join invoice inv on inv.nr = i.invoice_nr and inv.invoice_date = i.invoice_date\n" +
                "where inv.type = 'c'\n" +
                "group by i.id_prod,p.prod_name) as S\n" +
                "inner join\n" +
                "(select p.prod_name as prod_name, sum(i.quantity * i.unit_price) as costs\n" +
                "from item as i inner join products as p on i.id_prod = p.id_prod\n" +
                "inner join invoice inv on inv.nr = i.invoice_nr and inv.invoice_date = i.invoice_date\n" +
                "where inv.type = 's'\n" +
                "group by i.id_prod,p.prod_name) as C\n" +
                "on S.prod_name = C.prod_name\n" +
                "order by S.prod_name;";
        try {
            QueryResult result = scope.query(query);
            return result.rowsAs(ProfitOnEachProduct.class);
        } catch (DocumentNotFoundException ex) {
            throw new RepositoryException(" unable to find max value in invoice" , ex);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class FloatWrapper {
        private Float v;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesCostMonthYear {
        private float sales;
        private float costs;
        private int year_;
        private int month_;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfitOnEachProduct {

        private float profit;
        private String prod_name;
    }
}
