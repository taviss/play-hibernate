package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by octavian.salcianu on 7/19/2016.
 */

@Data
@Entity
@Table(name = "prices")
public class Price {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="product_id")
    //@JsonManagedReference
    //@JsonIgnoreProperties("prices")
    private Product product;

    @Column(name = "price", nullable = false)
    private Float value;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "input_date", nullable = false)
    //@JsonManagedReference
    private Date inputDate;

    public Product getProduct() {
        return null;
    }
}
