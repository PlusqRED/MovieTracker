package com.so.movietrackerservice.domain;

import com.so.movietrackerservice.service.QueryProcessor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Session {
    private QueryProcessor currentProcessor;
    private Object token;
}
