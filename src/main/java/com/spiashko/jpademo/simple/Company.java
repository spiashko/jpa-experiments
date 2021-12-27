package com.spiashko.jpademo.simple;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "company")
public class Company extends BaseEntity {

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Employee> employees;

    public void addEmployee(Employee employee) {
        if (employees == null) {
            employees = new HashSet<>();
        }
        this.employees.add(employee);
        employee.setCompany(this);
    }

    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);
        employee.setCompany(null);
    }

}
