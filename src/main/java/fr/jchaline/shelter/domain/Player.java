package fr.jchaline.shelter.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Player extends AbstractEntity {
	
	@Column(nullable = false, unique = true)
	private String name;
	
	@OneToOne(cascade = CascadeType.ALL)
	private Shelter shelter;
	
	@Column(nullable = false)
	@Min(0)
	private long money = 500;
	
	/**
	 * List of all street's id discovered by player
	 */
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<Long> discoveredStreets = new HashSet<Long>();

	public Player(String name){
		this.setName(name);
	}
}
