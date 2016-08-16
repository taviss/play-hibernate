package models;


import lombok.Data;
import play.data.validation.Constraints;

import javax.persistence.*;

@Data
@Entity
@Table(name = "categories")
public class Category {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	@Constraints.MaxLength(45)
	@Constraints.MinLength(3)
	@Constraints.Required
	private String catName;

	@Column(name = "deleted")
	private Boolean deleted;
}
