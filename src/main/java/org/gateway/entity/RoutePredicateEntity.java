package org.gateway.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TBL_ROUTE_PREDICATE")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RoutePredicateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="S_NAME", nullable = false)
    private String name;

    @Column(name="S_JSON_VALUE", columnDefinition = "TEXT")
    private String jsonValue;

    @ManyToOne
    @JoinColumn(name="N_ROUTE_ID")
    private RouteDefinitionEntity route;
}