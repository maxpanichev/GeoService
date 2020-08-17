package ru.natlex.geo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="sections")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) //ignoreUnknown=true
public class Section implements Serializable {

    private Long id;
    private String name;
    private List<GeologicalClass> geologicalClasses = new ArrayList<GeologicalClass>();

    @NotNull(message = "Empty name is not allowed")
    @NotBlank(message = "Empty name is not allowed")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(mappedBy ="section", fetch= FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    public List<GeologicalClass> getGeologicalClasses() {
        return geologicalClasses;
    }
    public void setGeologicalClasses(List<GeologicalClass> geologicalClasses) {
        this.geologicalClasses = geologicalClasses;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public void addGeo(GeologicalClass geo) {
        geologicalClasses.add(geo);
    }
}
