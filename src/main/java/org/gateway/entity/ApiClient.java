package org.gateway.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBL_API_CLIENT")
public class ApiClient {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "N_ID")
    private Long id;
	
	@Column(name = "S_SERVICE_ID")
	private String serviceId;
	
	@Column(name = "S_API_KEY")
	private String apiKey;
	  
	@Column(name = "N_STATUS")
	private boolean active;
	  
	@Column(name = "DT_LAST_ROTATED_AT")
	private Date lastRotatedAt;
	
	@Column(name = "DT_CREATED_ON")
    private Date createdOn;

    @Column(name = "S_CREATED_BY")
    private String createdBy;
    
    @Column(name="N_ENABLE_API_KEY")
    private boolean activeAPIKey;
}

