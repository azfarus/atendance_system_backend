package com.example.atendance_system_backend.file;



import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


@Data
@NoArgsConstructor
@Getter
@Setter
public class FileDTO {
    private Long id;

    private String filename;
    private String filetype;


    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Timestamp ts;


    public FileDTO(File fs) {
        this.id = fs.getId();
        this.filename = fs.getFilename();
        this.filetype = fs.getFiletype();
        this.ts = fs.getTs();
    }
}