package models;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by octavian.salcianu on 7/19/2016.
 */
@Data
@Entity
@Table(name = "keywords")
public class Keyword {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="product_id")
    //@JsonManagedReference
    //@JsonIgnoreProperties("prices")
    private Product product;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    //Commented out as it's used after getting keywords to determine products found
    //StackOverflow solved by returning null in Product.getKeywords()
    /*public Product getProduct() {
        return null;
    }*/
}
