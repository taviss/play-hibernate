package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Set;

/**
 * Created by octavian.salcianu on 7/14/2016.
 */

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "link_address", nullable = false)
    @Constraints.MaxLength(300)
    @Constraints.Required
    @Constraints.MinLength(20)
    private String linkAddress;

    @Column(name = "product_name", nullable = false)
    @Constraints.MaxLength(128)
    @Constraints.MinLength(4)
    @Constraints.Required
    private String prodName;

    @OneToMany(mappedBy="product", cascade = CascadeType.REMOVE)
    private Set<Price> prices;

    @OneToMany(mappedBy="product", cascade = CascadeType.ALL)
    private Set<Keyword> keywords;

    /**
     * Multiple links @ one site
     * TBE
     */
    @ManyToOne
    @JoinColumn(name="site_id", nullable = false)
    private Site site;

    @Column(name = "deleted")
    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Set<Keyword> getKeywords() {
        return null;
    }

    public Set<Product> getPrices() {
        return null;
    }

    public Price getPrice() {
        Price p = null;
        for(Price o : prices){
            if (p == null || o.getInputDate().compareTo(p.getInputDate()) > 0){
                p = o;
            }
        }
        return p;
    }
}
