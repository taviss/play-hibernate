package models;

import lombok.Data;

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
    private String userName;

    @Column(name = "u_pass", nullable = false)
    private String userPass;

    @Column(name = "u_admin", nullable = false)
    private int adminLevel;
}
