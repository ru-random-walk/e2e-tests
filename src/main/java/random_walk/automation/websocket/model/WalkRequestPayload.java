package random_walk.automation.websocket.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.random_walk.swagger.chat_service.model.RequestForWalkPayload;

@Data
@Accessors(chain = true)
public class WalkRequestPayload {

    private String type;
    private RequestForWalkPayload payload;
}
