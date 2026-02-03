package gateway.controller;

import gateway.service.BinaryForwardService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 * @Package : gateway.controller
 * @Create on : 2026/2/3 15:21
 **/


@RestController
@RequestMapping("/gateway")
public class BinaryGatewayController {

    private final BinaryForwardService forwardService;

    public BinaryGatewayController(BinaryForwardService forwardService) {
        this.forwardService = forwardService;
    }

    @PostMapping(
            value = "/forward",
            consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public byte[] forward(@RequestBody byte[] body) {
        return forwardService.forward(body);
    }
}

