package com.project.classroom.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailObjectDto {
    private String from;

    private String to;

    private String subject;

    private String text;
}
