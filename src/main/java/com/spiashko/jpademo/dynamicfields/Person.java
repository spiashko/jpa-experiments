package com.spiashko.jpademo.dynamicfields;

import lombok.*;

import javax.persistence.*;
import java.util.List;


@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "person")
public class Person {

    @Id
    @Column(name = "id", unique = true)
    private String id;

    @OneToMany(mappedBy = "person")
    private List<PersonField> personFields;

}
