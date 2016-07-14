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

    @Column(name = "u_id", nullable = false)
    private Long userId;

    @Column(name = "u_status", nullable = false)
    private String status;
}
