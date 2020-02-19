package com.so.movietrackerservice.domain;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Builder
@Data
public class DialogStage {
    private String stageName;
    @Builder.Default
    private Map<String, Object> metaData = new HashMap<>();
}
