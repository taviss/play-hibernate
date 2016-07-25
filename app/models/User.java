package models;

import lombok.Data;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by octavian.salcianu on 7/13/2016.
 * POJO containing the User Model
 */

@Data
@Entity
@Table(name = "accounts")
public class User {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "u_name", nullable = false)
    @Constraints.MinLength(3)
    @Constraints.Required
    private String userName;

    @Column(name = "u_mail", nullable = false)
    @Constraints.Required
    @Constraints.Email
    private String userMail;

    @Column(name = "u_token", nullable = false)
    private String userToken;

    @Column(name = "u_active", nullable = false)
    private Boolean userActive;

    @Column(name = "u_pass", nullable = false)
    @Constraints.MinLength(6)
    @Constraints.Required
    private String userPass;

    @Column(name = "u_admin", nullable = false)
    @Constraints.Max(3)
    @Constraints.Min(0)
    private int adminLevel;
}
