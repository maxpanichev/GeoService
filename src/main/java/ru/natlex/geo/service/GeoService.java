package ru.natlex.geo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.natlex.geo.DAO.GeoDAO;
import ru.natlex.geo.DAO.SectionDAO;
import ru.natlex.geo.ResourceNotFoundException;
import ru.natlex.geo.entity.GeologicalClass;
import ru.natlex.geo.entity.Section;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class GeoService {
    SectionDAO sdao;
    GeoDAO geoDAO;

    @Autowired
    public GeoService(SectionDAO sdao, GeoDAO geoDAO) {
        this.sdao = sdao;
        this.geoDAO = geoDAO;
    }

    public Section createSection(Section section) {
        for (GeologicalClass g : section.getGeologicalClasses())
            g.setSection(section);

        return sdao.add(section);
    }

    @Transactional(readOnly = true)
    public Section getSection(long id) {
        return sdao.get(id);
    }

    @Transactional(readOnly = true)
    public List<Section> getAllSections() { return sdao.getAll(); }

    @Transactional
    public Section updateSection(long id, Section s) {
        Section section = sdao.get(id);
        if (section == null)
            throw new ResourceNotFoundException("Section " + s.getId() + " not found");

        if (s.getId() == null)
            s.setId(id);
        else if (s.getId() != id)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Section ID mismatches param ID ");

        for (GeologicalClass geo : section.getGeologicalClasses()) {
            long geoId = geo.getId();
            GeologicalClass g = s.getGeologicalClasses().stream()
                    .filter(f -> Objects.equals(f.getId(), geoId)).findFirst().orElse(null);

            if (g == null) {
                System.out.println("delete geo " + geo.getId());
                geo.setSection(null);
                s.addGeo(geo);
            }
        }

        return sdao.update(s);
    }

    public void deleteSection(long id) throws ResponseStatusException {
        Section s = getSection(id);
        if (s==null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Section "+id+" does not exist");

        sdao.delete(s);
    }

    @Transactional(readOnly = true)
    public List<Section> searchByGeoCode(String code) {
        return sdao.findByGeoCode(code);
    }

    public GeologicalClass getGeo(long id) {
        return geoDAO.get(id);
    }

    public GeologicalClass addGeo(long sectionId, GeologicalClass geo) {
        Section s = getSection(sectionId);
        if (s==null)
            throw new ResourceNotFoundException("Session " + sectionId + " not found");

        geo.setSection(s);
        return geoDAO.add(geo);
    }

    public GeologicalClass updateGeo(long id, GeologicalClass geo) {
        GeologicalClass gc = getGeo(id);
        if (gc == null)
            throw new ResourceNotFoundException("GeologicalClass " + id + " not found");

        gc.setCode(geo.getCode());
        gc.setName(geo.getName());
        return geoDAO.update(gc);
    }

    public void deleteGeo(long id) {
        GeologicalClass geo = geoDAO.get(id);
        if (geo==null)
            throw new ResourceNotFoundException("GeologicalClass " + id + " not found");
        geoDAO.delete(geo);
    }
}
