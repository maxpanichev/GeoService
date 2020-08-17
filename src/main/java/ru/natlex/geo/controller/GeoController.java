package ru.natlex.geo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.natlex.geo.ResourceNotFoundException;
import ru.natlex.geo.entity.GeologicalClass;
import ru.natlex.geo.service.GeoService;


@RestController
@RequestMapping(value = "/geo")
public class GeoController extends BaseController {
    private GeoService service;

    @Autowired
    public GeoController(GeoService service) {
        this.service = service;
    }

    @PostMapping("/{id}")
    @ResponseBody
    public GeologicalClass add(@PathVariable("id") String sectionId, @RequestBody GeologicalClass geo) {
        return service.addGeo(parseId(sectionId), geo);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public GeologicalClass get(@PathVariable("id") String id) {
        GeologicalClass geo = service.getGeo(parseId(id));
        if (geo == null)
            throw new ResourceNotFoundException();
        return geo;
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity delete(@PathVariable("id") String id) {
        service.deleteGeo(parseId(id));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @ResponseBody
    public GeologicalClass update(@PathVariable("id") String id, @RequestBody GeologicalClass geo) {
        return service.updateGeo(parseId(id), geo);
    }
}
