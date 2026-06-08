package com.nubiz.nutrust.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class ProjectSupportManagerId implements Serializable {
    private Long projectId;
    private Long supportId;
}
