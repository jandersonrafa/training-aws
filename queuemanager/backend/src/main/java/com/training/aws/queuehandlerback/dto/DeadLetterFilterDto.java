package com.training.aws.queuehandlerback.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeadLetterFilterDto {

    private String typeAction;

}