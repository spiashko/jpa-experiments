package com.spiashko.jpademo.simple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class BaseEntity {

    @Column(name = "created_on", updatable = false)
    @CreatedDate
    protected Date createdOn;
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    protected Long createdBy;
    @LastModifiedDate
    @Column(name = "last_modified_on")
    protected Date lastModifiedOn;
    @LastModifiedBy
    @Column(name = "last_modified_by")
    protected Long lastModifiedBy;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;
}
