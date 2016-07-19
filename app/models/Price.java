package models;

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

    //@Column(name = "product_id", nullable = false)
    @Column(name = "price", nullable = false)
    private Float price;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "input_date", nullable = false)
    private Date inputDate;
}
