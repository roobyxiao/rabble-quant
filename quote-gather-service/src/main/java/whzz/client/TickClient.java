package whzz.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import whzz.pojo.Tick;

@FeignClient(value = "QUANT-DATA-SERVICE", contextId = "tick")
public interface TickClient {
    @RequestMapping(value = "/tick/save", method = RequestMethod.POST)
    String saveTick(@RequestBody Tick tick);
}
