package models;

import lombok.Data;

import javax.annotation.Generated;
import javax.persistence.*;

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
    private String siteURL;

    @Column(name = "keyword")
    private String siteKeyword;

    @Column(name = "price_element", nullable = false)
    private String priceElement;

    @Column(name = "currency_element", nullable = false)
    private String currencyElement;

    /**
     * TBA: Site parsing rules
     */

}

