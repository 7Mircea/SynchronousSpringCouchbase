package com.example.p_jdbcrestapi_couchbase_mvc.repository;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.query.QueryResult;
import com.example.p_jdbcrestapi_couchbase_mvc.Exceptions.RepositoryException;
import com.example.p_jdbcrestapi_couchbase_mvc.model.Customer_Employee_Supplier;
import org.springframework.stereotype.Service;

@Service
public class CustomerEmployeeSupplierRepository  {
    /*Set<CustomerEmployeeSupplier> findCustomerEmployeeSupplierByTypeCES(char c);

    @Query(value = "select CES.Name\n" +
            "from (select count(*) invoice_number, id_employee\n" +
            "    from invoice\n" +
            "    group by id_employee) S inner join CUSTOMER_EMPLOYEE_SUPPLIER CES on S.id_employee = CES.id_ces\n" +
            "where invoice_number = (select max(S2.invoice_number) \n" +
            "                    from (select count(*) invoice_number, id_employee\n" +
            "                        from invoice\n" +
            "                        group by id_employee) S2);\n", nativeQuery = true)
    Set<String> findEmployeeWithGreatestNrOfInvoices();*/

    private Cluster cluster;
    private Bucket bucket;
    private Scope scope;
    private Collection collection;
    private String COLLECTION_NAME = "customer_employee_supplier";

    public CustomerEmployeeSupplierRepository(Cluster cluster, Bucket bucket, Scope scope) {
        this.cluster = cluster;
        this.bucket = bucket;
        this.scope = scope;
        this.collection = scope.collection(COLLECTION_NAME);
    }

    public Iterable<Customer_Employee_Supplier> findCustomerEmployeeSupplierByTypeCES(char c) {
        String query =  "select "+COLLECTION_NAME+".* from "+COLLECTION_NAME+" where "+COLLECTION_NAME+".type_ces=\'"+c + '\'';
        try {
            QueryResult result = scope.query(query);
            return result.rowsAs(Customer_Employee_Supplier.class);
        } catch (DocumentNotFoundException ex) {
            throw new RepositoryException("Unable to locate documents with key before: " + c, ex);
        }
    }
}
