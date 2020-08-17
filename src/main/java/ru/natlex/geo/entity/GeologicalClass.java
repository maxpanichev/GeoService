package ru.natlex.geo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name="geological_classes")
public class GeologicalClass implements Serializable {

    private Long id;
    private String name;
    private String code;

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    private Section section;
    @ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JsonBackReference
    @JoinColumn(name="section_id")
    public Section getSection() {
        return section;
    }
    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof GeologicalClass)) {
            return false;
        }

        final GeologicalClass other = (GeologicalClass) obj;
//        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name))
//            return false;
//        if ((this.code == null) ? (other.code != null) : !this.code.equals(other.code))
//            return false;

        return  (this.getId() != other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code);
    }
}
