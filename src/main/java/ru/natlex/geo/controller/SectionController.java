package ru.natlex.geo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.natlex.geo.ResourceNotFoundException;
import ru.natlex.geo.entity.Section;
import ru.natlex.geo.service.GeoService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/sections")
public class SectionController extends BaseController {
    private GeoService service;

    @Autowired
    public SectionController(GeoService service) {
        this.service = service;
    }

    @PostMapping("/")
    public Section add(@Valid @RequestBody Section s) {
        service.createSection(s);
        return s;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Section get(@PathVariable("id") String id) {
        Section section = service.getSection(parseId(id));
        if (section == null)
            throw new ResourceNotFoundException();
        return section;
    }

    @GetMapping("/")
    @ResponseBody
    public List<Section> getAll() {
        return service.getAllSections();
    }

    @PutMapping("/{id}")
    @ResponseBody
    public Section update(@PathVariable("id") String id, @RequestBody Section s) {
        return service.updateSection(parseId(id), s);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") String id) throws ResponseStatusException {
        service.deleteSection(parseId(id));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-code")
    @ResponseBody
    public List<Section> getByGeoCode(@RequestParam(name="code") String code) {
        return service.searchByGeoCode(code);
    }

}
