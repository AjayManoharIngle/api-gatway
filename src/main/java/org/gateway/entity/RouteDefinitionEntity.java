package org.gateway.entity;

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
@Table(name = "TBL_ROUTE_DEFINITION")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RouteDefinitionEntity {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="S_ROUTE_ID")
    private String routeId;

    @Column(name="S_URI")
    private String uri;

    @Column(name="N_STATUS")
    private boolean active;

    @Column(name="N_ORDER")
    private int order;
}