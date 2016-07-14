package models;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by octavian.salcianu on 7/14/2016.
 */

@Data
@Entity
@Table(name = "links")
public class Link {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Multiple links @ one site
     * TBE
     */
    @ManyToOne
    @JoinColumn(name="site_id")
    private Site site;
}
