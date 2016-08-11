package models;

import lombok.Data;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @Constraints.MaxLength(value = 512, message = "Max search length is 512 characters")
    private String queryString;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date inputDate;

    @ManyToOne
    @JoinColumn(name="account_id")
    private User user;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (queryString.length() >= 512) {
            errors.add(new ValidationError("queryString", "Max search length is 512 characters"));
        }
        return errors.isEmpty() ? null : errors;
    }
}
