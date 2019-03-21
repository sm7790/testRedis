package com.gruposantander.subscribersarq.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Custodian implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUSTODIAN_SEQ")
    @SequenceGenerator(sequenceName = "custodian_seq", allocationSize = 1, name = "CUSTODIAN_SEQ")
	private Integer id;

	private String hash;

	private String uri;

	private String proc;

	private String version;

	@CreationTimestamp
	private Date timestamp;

	private String information;

}
