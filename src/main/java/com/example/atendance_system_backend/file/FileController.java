package com.example.atendance_system_backend.file;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileStorageService storageService;


    @CrossOrigin
    @PostMapping("/upload")
    public Long uploadFile(@RequestParam("file") MultipartFile file) {

        try {
            Long id = storageService.store(file);
            return id;
        } catch (Exception e) {
            Long id = (long)-1;
            return id ;
        }
    }


    @CrossOrigin
    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id) {
        File fileDB = storageService.getFile(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getFilename() + "\"")
                .body(fileDB.getData());
    }

    @GetMapping("/fileinfo/{id}")
    public FileDTO getFileInfo(@PathVariable Long id) {
        File fileDB = storageService.getFile(id);
        FileDTO fdto = new FileDTO(fileDB);
        return fdto;
    }
}