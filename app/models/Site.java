package models;

import lombok.Data;
import play.data.validation.Constraints;

import javax.annotation.Generated;
import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * Created by octavian.salcianu on 7/14/2016.
 */
@Data
@Entity
@Table(name = "websites")
public class Site {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "s_url", nullable = false)
    @Constraints.Required
    @Constraints.MaxLength(255)
    private String siteURL;

    @Column(name = "keyword")
    @Constraints.MaxLength(45)
    private String siteKeyword;

    @Column(name = "price_element", nullable = false)
    @Constraints.MaxLength(45)
    @Constraints.Required
    private String priceElement;

    @Column(name = "currency_element", nullable = false)
    @Constraints.MaxLength(45)
    @Constraints.Required
    private String currencyElement;

    @Column(name = "deleted")
    private Boolean deleted;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<Product> products;

    public List<Product> getProducts() {return null;}

}

