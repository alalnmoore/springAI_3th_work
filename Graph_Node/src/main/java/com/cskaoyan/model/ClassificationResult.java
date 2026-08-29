package com.cskaoyan.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class ClassificationResult {
    @JsonPropertyDescription("问题类别，只能是 TECH 或 LIFE")
    private String category;

    @JsonPropertyDescription("做出该分类的简短原因")
    private String reason;
}
