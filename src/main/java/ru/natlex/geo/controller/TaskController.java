package ru.natlex.geo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.natlex.geo.ResourceNotFoundException;
import ru.natlex.geo.TaskStatus;
import ru.natlex.geo.entity.TaskRequest;
import ru.natlex.geo.service.TaskService;

@RestController
public class TaskController extends BaseController {
    private TaskService service;

    @Autowired
    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/export")
    public TaskRequest export() {
        return service.runExport();
    }

    @GetMapping(value = "/export/{id}")
    @ResponseBody
    public TaskRequest getExportStatus(@PathVariable("id") String id) {
        TaskRequest request = service.getTaskRequest(parseId(id), TaskRequest.EXPORT);
        if (request == null)
            throw new ResourceNotFoundException("Task not found");

        return request;
    }

    @GetMapping(value = "/export/{id}/file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getExportedFile(@PathVariable("id") String id) {
        TaskRequest request = service.getTaskRequest(parseId(id), TaskRequest.EXPORT);
        if (request == null)
            throw new ResourceNotFoundException("Task not found");
        if (request.getStatus() == TaskStatus.IN_PROGRESS)  {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Task is in progress");
        }
        byte[] attachment = request.getAttachment();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"Sections.xls\"").body(attachment);
    }

    @PostMapping(value = "/import")
    @ResponseBody
    public ResponseEntity<Object> importFile(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.getOriginalFilename().toLowerCase().endsWith(".xls")) {
                return ResponseEntity.unprocessableEntity().build();
            }

            TaskRequest r = service.runImport(file);
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping(value = "/import/{id}")
    @ResponseBody
    public TaskRequest getImportStatus(@PathVariable("id") String id) {
        TaskRequest request = service.getTaskRequest(parseId(id), TaskRequest.IMPORT);
        if (request == null)
            throw new ResourceNotFoundException("Task not found");

        return request;
    }

}
