package com.github.hoantran.lib.aws.sns;

import java.io.Serializable;

import com.github.hoantran.lib.utility.dto.BaseDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ResponseDTO extends BaseDTO implements Serializable {

    private boolean success;
    private String provider;
    private String response;

    public ResponseDTO(boolean success, String provider, String response) {
        super();
        this.success = success;
        this.provider = provider;
        this.response = response;
    }

}
