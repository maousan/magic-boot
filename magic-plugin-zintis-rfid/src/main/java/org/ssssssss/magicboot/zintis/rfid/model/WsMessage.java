package org.ssssssss.magicboot.zintis.rfid.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WsMessage {
    @JsonProperty("msgId")
    private String msgId;
    @JsonProperty("seq")
    private long seq;
    @JsonProperty("type")
    private String type;
    @JsonProperty("payload")
    private Object payload;
}
