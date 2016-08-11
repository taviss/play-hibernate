package models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by octavian.salcianu on 8/11/2016.
 */
@Data
@Entity
@Table(name = "search_history")
public class SearchHistory {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "query_string", nullable = false)
    private String queryString;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date inputDate;

    @ManyToOne
    @JoinColumn(name="account_id")
    private User user;
}
