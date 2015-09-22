package fr.jchaline.shelter.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

import fr.jchaline.shelter.config.Constant;
import fr.jchaline.shelter.utils.SpecialEnum;

@Entity
@Table
public class RoomType extends AbstractEntity {
	
	@Column(unique = true, nullable = false)
	@NotBlank
	private String name;
	
	@Column(nullable = false)
	@Min(1)
	private int size;
	
	@Column(nullable = false)
	private int cost;
	
	@Column
	private SpecialEnum special;
	
	@Column(nullable = false)
	@Min(1)
	@Max(Constant.ROOM_MAX_SIZE)
	private int maxSize;
	
	public RoomType() {
		
	}
	
	public RoomType(String name, int size, SpecialEnum special, int cost, int maxSize) {
		this.setName(name);
		this.setSize(size);
		this.setSpecial(special);
		this.setCost(cost);
		this.setMaxSize(maxSize);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public SpecialEnum getSpecial() {
		return special;
	}

	public void setSpecial(SpecialEnum special) {
		this.special = special;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
}
