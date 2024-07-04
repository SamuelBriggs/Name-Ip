package com.example.hngstageone.dto.response;

import lombok.*;

@ToString
@Builder
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiResponse {

    private String client_ip;
    private String location;
    private String greeting;

}
