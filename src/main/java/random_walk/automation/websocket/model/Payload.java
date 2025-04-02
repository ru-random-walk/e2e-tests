package random_walk.automation.websocket.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Payload {
    private String type;
    private String text;
}
