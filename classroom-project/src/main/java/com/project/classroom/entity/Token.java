package com.project.classroom.entity;

import com.project.classroom.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "tokens")
public class Token extends BaseEntity {
    private String token;

    private Date tokenExpiredDate;

    private String refreshToken;

    private Date refreshTokenExpiredDate;

    @ManyToOne
    @JoinColumn(name="userid", nullable=false)
    private User user;

}
