package models;

import lombok.Data;

import javax.annotation.Generated;
import javax.persistence.*;
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
    private String siteURL;

    @Column(name = "keyword")
    private String siteKeyword;

//    @OneToMany(mappedBy="site", cascade = CascadeType.REMOVE)
//    private Set<Product> products;

    /**
     * TBA: Site parsing rules
     */

}

