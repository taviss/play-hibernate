package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import play.data.validation.Constraints;

import javax.persistence.*;
import javax.validation.Constraint;
import java.util.HashSet;
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
    @Constraints.Required
    private String linkAddress;

    @Column(name = "product_name", nullable = false)
    @Constraints.Required
    private String prodName;

    @OneToMany(mappedBy="product")
    //@JsonBackReference
    //@JsonIgnoreProperties("product")
    private Set<Price> prices;

    @OneToMany(mappedBy="product")
    private Set<Keyword> keywords;

    /**
     * Multiple links @ one site
     * TBE
     */
    @ManyToOne
    @JoinColumn(name="site_id", nullable = false)
    private Site site;

    public Set<Keyword> getKeywords() {
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

	public String siteFromURL(){
		String URL = getLinkAddress();
		String[] URLsite = URL.split("/");
		return URLsite[0];
	}
}