package com.spiashko.jpademo.dynamicfields;

import lombok.*;

import javax.persistence.*;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "person_field")
public class PersonField {

    @Id
    @Column(name = "id", unique = true)
    private String id;

    private String name;
    private String value;

    @ManyToOne
    @JoinColumn(name = "fk_person")
    private Person person;
}
